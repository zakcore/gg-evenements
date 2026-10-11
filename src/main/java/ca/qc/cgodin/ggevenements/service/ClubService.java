package ca.qc.cgodin.ggevenements.service;

import ca.qc.cgodin.ggevenements.dto.ClubForm;
import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.exception.RessourceIntrouvableException;
import ca.qc.cgodin.ggevenements.model.Club;
import ca.qc.cgodin.ggevenements.model.Utilisateur;
import ca.qc.cgodin.ggevenements.repository.ClubRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ClubService {

    private final ClubRepository clubRepository;
    private final UtilisateurService utilisateurService;

    public ClubService(ClubRepository clubRepository, UtilisateurService utilisateurService) {
        this.clubRepository = clubRepository;
        this.utilisateurService = utilisateurService;
    }

    public Club trouver(Long id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Club introuvable."));
    }

    /** Clubs actifs seulement : ce sont eux qui alimentent les listes déroulantes des usagers. */
    public List<Club> listerActifs() {
        return clubRepository.findByActifTrueOrderByNomAsc();
    }

    public List<Club> listerTous() {
        return clubRepository.findAllByOrderByNomAsc();
    }

    public Optional<Club> clubDuResponsable(Long utilisateurId) {
        return clubRepository.findByResponsableId(utilisateurId);
    }

    @Transactional
    public Club creer(ClubForm form) {
        if (clubRepository.existsByNomIgnoreCase(form.getNom().trim())) {
            throw new RegleAffaireException("nom", "Un club porte déjà ce nom.");
        }
        Club club = new Club();
        appliquer(club, form);
        return clubRepository.save(club);
    }

    @Transactional
    public Club modifier(Long id, ClubForm form) {
        Club club = trouver(id);
        if (clubRepository.existsByNomIgnoreCaseAndIdNot(form.getNom().trim(), id)) {
            throw new RegleAffaireException("nom", "Un club porte déjà ce nom.");
        }
        appliquer(club, form);
        return club;
    }

    @Transactional
    public Club basculerActif(Long id) {
        Club club = trouver(id);
        club.setActif(!club.isActif());
        return club;
    }

    /** Désigner un responsable lui accorde automatiquement le rôle Organisateur. */
    private void appliquer(Club club, ClubForm form) {
        Utilisateur responsable = utilisateurService.trouver(form.getResponsableId());
        if (!responsable.isActif()) {
            throw new RegleAffaireException("responsableId", "Ce compte est désactivé.");
        }
        clubRepository.findByResponsableId(responsable.getId())
                .filter(autre -> !autre.getId().equals(club.getId()))
                .ifPresent(autre -> {
                    throw new RegleAffaireException("responsableId",
                            responsable.getNomComplet() + " est déjà responsable du club « " + autre.getNom() + " ».");
                });
        club.setNom(form.getNom().trim());
        club.setDescription(form.getDescription() == null || form.getDescription().isBlank()
                ? null : form.getDescription().trim());
        club.setCourrielContact(form.getCourrielContact().trim());
        club.setResponsable(responsable);
        utilisateurService.accorderRoleOrganisateur(responsable);
    }
}
