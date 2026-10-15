package ca.qc.cgodin.ggevenements.service;

import ca.qc.cgodin.ggevenements.dto.MesEvenements;
import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.exception.RessourceIntrouvableException;
import ca.qc.cgodin.ggevenements.model.Evenement;
import ca.qc.cgodin.ggevenements.model.Inscription;
import ca.qc.cgodin.ggevenements.model.StatutInscription;
import ca.qc.cgodin.ggevenements.repository.AvisRepository;
import ca.qc.cgodin.ggevenements.repository.EvenementRepository;
import ca.qc.cgodin.ggevenements.repository.InscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class InscriptionService {

    private final InscriptionRepository inscriptionRepository;
    private final EvenementRepository evenementRepository;
    private final AvisRepository avisRepository;
    private final EvenementService evenementService;
    private final UtilisateurService utilisateurService;

    public InscriptionService(InscriptionRepository inscriptionRepository, EvenementRepository evenementRepository,
                              AvisRepository avisRepository, EvenementService evenementService,
                              UtilisateurService utilisateurService) {
        this.inscriptionRepository = inscriptionRepository;
        this.evenementRepository = evenementRepository;
        this.avisRepository = avisRepository;
        this.evenementService = evenementService;
        this.utilisateurService = utilisateurService;
    }

    public Optional<Inscription> inscriptionDe(Integer evenementId, Integer utilisateurId) {
        return inscriptionRepository.findByEvenementIdAndUtilisateurId(evenementId, utilisateurId);
    }

    /**
     * Fonctionnalité 5. Vérification de la capacité + création de l'inscription dans une seule transaction.
     * La ligne de l'événement est verrouillée (SELECT ... FOR UPDATE) : deux inscriptions simultanées au même
     * événement sont traitées l'une après l'autre, la capacité ne peut donc jamais être dépassée.
     *
     * @return le statut attribué (CONFIRMEE ou EN_ATTENTE)
     */
    @Transactional
    public StatutInscription inscrire(Integer evenementId, Integer utilisateurId) {
        Evenement evenement = verrouiller(evenementId);
        if (!evenement.isOuvert()) {
            throw new RegleAffaireException("Les inscriptions à cet événement sont fermées.");
        }
        if (inscriptionRepository.existsByEvenementIdAndUtilisateurId(evenementId, utilisateurId)) {
            throw new RegleAffaireException("Vous êtes déjà inscrit(e) à cet événement.");
        }
        long confirmes = inscriptionRepository.countByEvenementIdAndStatut(evenementId, StatutInscription.CONFIRMEE);
        StatutInscription statut = confirmes < evenement.getCapaciteMax()
                ? StatutInscription.CONFIRMEE
                : StatutInscription.EN_ATTENTE;
        inscriptionRepository.save(new Inscription(evenement, utilisateurService.trouver(utilisateurId), statut));
        return statut;
    }

    /**
     * Fonctionnalité 6 — règle principale du projet. Le retrait de l'inscription et la promotion éventuelle
     * de la première personne en attente forment une seule transaction : tout réussit, ou rien n'est écrit.
     */
    @Transactional
    public void desinscrire(Integer evenementId, Integer utilisateurId) {
        Evenement evenement = verrouiller(evenementId);
        Inscription inscription = inscriptionRepository.findByEvenementIdAndUtilisateurId(evenementId, utilisateurId)
                .orElseThrow(() -> new RegleAffaireException("Vous n'êtes pas inscrit(e) à cet événement."));
        if (!evenement.isOuvert()) {
            throw new RegleAffaireException("Il n'est plus possible de se désinscrire de cet événement.");
        }
        boolean liberePlace = inscription.isConfirmee();
        inscriptionRepository.delete(inscription);
        inscriptionRepository.flush();

        if (liberePlace) {
            inscriptionRepository
                    .findFirstByEvenementIdAndStatutOrderByDateInscriptionAscIdAsc(evenementId, StatutInscription.EN_ATTENTE)
                    .ifPresent(premier -> premier.setStatut(StatutInscription.CONFIRMEE));
        }
    }

    private Evenement verrouiller(Integer evenementId) {
        return evenementRepository.findByIdPourMiseAJour(evenementId)
                .orElseThrow(() -> new RessourceIntrouvableException("Cet événement n'existe pas."));
    }

    /** Fonctionnalité 3 : inscriptions à venir et passées de l'utilisateur. */
    public MesEvenements mesEvenements(Integer utilisateurId) {
        LocalDateTime maintenant = LocalDateTime.now();
        return new MesEvenements(
                inscriptionRepository.findAVenirPourUtilisateur(utilisateurId, maintenant),
                inscriptionRepository.findPassesPourUtilisateur(utilisateurId, maintenant),
                avisRepository.findEvenementIdsAvecAvis(utilisateurId));
    }

    // ------------------------------------------------------------------
    //  Fonctionnalité 12 : gestion des inscriptions par l'organisateur
    // ------------------------------------------------------------------

    public List<Inscription> confirmees(Integer evenementId, Integer organisateurId) {
        evenementService.trouverPourGestion(evenementId, organisateurId);
        return inscriptionRepository.findByEvenementIdAndStatutOrderByDateInscriptionAscIdAsc(
                evenementId, StatutInscription.CONFIRMEE);
    }

    public List<Inscription> enAttente(Integer evenementId, Integer organisateurId) {
        evenementService.trouverPourGestion(evenementId, organisateurId);
        return inscriptionRepository.findByEvenementIdAndStatutOrderByDateInscriptionAscIdAsc(
                evenementId, StatutInscription.EN_ATTENTE);
    }

    /** Coche « Présence confirmée » pour les inscriptions reçues, décoche les autres (confirmées seulement). */
    @Transactional
    public int enregistrerPresences(Integer evenementId, Integer organisateurId, Set<Integer> presents) {
        List<Inscription> confirmees = confirmees(evenementId, organisateurId);
        confirmees.forEach(i -> i.setPresenceConfirmee(presents.contains(i.getId())));
        return (int) confirmees.stream().filter(Inscription::isPresenceConfirmee).count();
    }
}
