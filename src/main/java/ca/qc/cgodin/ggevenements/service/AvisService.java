package ca.qc.cgodin.ggevenements.service;

import ca.qc.cgodin.ggevenements.dto.AvisForm;
import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.model.Avis;
import ca.qc.cgodin.ggevenements.model.Evenement;
import ca.qc.cgodin.ggevenements.model.Inscription;
import ca.qc.cgodin.ggevenements.repository.AvisRepository;
import ca.qc.cgodin.ggevenements.repository.InscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AvisService {

    private final AvisRepository avisRepository;
    private final InscriptionRepository inscriptionRepository;
    private final EvenementService evenementService;
    private final UtilisateurService utilisateurService;

    public AvisService(AvisRepository avisRepository, InscriptionRepository inscriptionRepository,
                       EvenementService evenementService, UtilisateurService utilisateurService) {
        this.avisRepository = avisRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.evenementService = evenementService;
        this.utilisateurService = utilisateurService;
    }

    public List<Avis> avisDe(Integer evenementId) {
        return avisRepository.findByEvenementIdOrderByDateAvisDesc(evenementId);
    }

    public Double moyenne(Integer evenementId) {
        return avisRepository.moyennePourEvenement(evenementId);
    }

    /**
     * Fonctionnalité 8 : seul un participant CONFIRMÉ d'un événement terminé, n'ayant encore laissé
     * aucun avis, peut en soumettre un.
     */
    public Evenement verifierAdmissible(Integer evenementId, Integer utilisateurId) {
        Evenement evenement = evenementService.trouver(evenementId);
        Inscription inscription = inscriptionRepository.findByEvenementIdAndUtilisateurId(evenementId, utilisateurId)
                .orElseThrow(() -> new RegleAffaireException("Vous n'étiez pas inscrit(e) à cet événement."));
        if (!evenement.isTermine()) {
            throw new RegleAffaireException("Vous pourrez laisser un avis une fois l'événement terminé.");
        }
        if (!inscription.isConfirmee()) {
            throw new RegleAffaireException("Seules les personnes dont l'inscription était confirmée peuvent laisser un avis.");
        }
        if (avisRepository.existsByEvenementIdAndUtilisateurId(evenementId, utilisateurId)) {
            throw new RegleAffaireException("Vous avez déjà laissé un avis pour cet événement.");
        }
        return evenement;
    }

    @Transactional
    public Avis soumettre(Integer evenementId, Integer utilisateurId, AvisForm form) {
        Evenement evenement = verifierAdmissible(evenementId, utilisateurId);
        String commentaire = form.getCommentaire() == null || form.getCommentaire().isBlank()
                ? null : form.getCommentaire().trim();
        return avisRepository.save(new Avis(evenement, utilisateurService.trouver(utilisateurId),
                form.getNote(), commentaire));
    }
}
