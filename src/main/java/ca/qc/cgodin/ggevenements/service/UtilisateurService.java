package ca.qc.cgodin.ggevenements.service;

import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.exception.RessourceIntrouvableException;
import ca.qc.cgodin.ggevenements.model.Role;
import ca.qc.cgodin.ggevenements.model.Utilisateur;
import ca.qc.cgodin.ggevenements.repository.ClubRepository;
import ca.qc.cgodin.ggevenements.repository.RoleRepository;
import ca.qc.cgodin.ggevenements.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleRepository roleRepository;
    private final ClubRepository clubRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, RoleRepository roleRepository,
                              ClubRepository clubRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleRepository = roleRepository;
        this.clubRepository = clubRepository;
    }

    public Utilisateur trouver(Integer id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Utilisateur introuvable."));
    }

    public List<Utilisateur> listerTous() {
        return utilisateurRepository.findAllByOrderByNomAscPrenomAsc();
    }

    public List<Utilisateur> listerActifs() {
        return utilisateurRepository.findByActifTrueOrderByNomAscPrenomAsc();
    }

    /** Désactive ou réactive un compte. Aucune suppression : l'historique (inscriptions, avis) est conservé. */
    @Transactional
    public Utilisateur basculerActif(Integer id, Integer adminConnecteId) {
        Utilisateur utilisateur = trouver(id);
        if (utilisateur.getId().equals(adminConnecteId)) {
            throw new RegleAffaireException("Vous ne pouvez pas désactiver votre propre compte.");
        }
        utilisateur.setActif(!utilisateur.isActif());
        return utilisateur;
    }

    /** Attribue ou retire le rôle Organisateur. */
    @Transactional
    public Utilisateur basculerRoleOrganisateur(Integer id) {
        Utilisateur utilisateur = trouver(id);
        if (utilisateur.isOrganisateur()) {
            clubRepository.findFirstByResponsableIdOrderByActifDescNomAsc(id).ifPresent(club -> {
                throw new RegleAffaireException(utilisateur.getNomComplet() + " est responsable du club « "
                        + club.getNom() + " ». Désignez d'abord un autre responsable pour ce club.");
            });
            utilisateur.getRoles().removeIf(r -> r.getNom().equals(Role.ORGANISATEUR));
        } else {
            accorderRoleOrganisateur(utilisateur);
        }
        return utilisateur;
    }

    @Transactional
    public void accorderRoleOrganisateur(Utilisateur utilisateur) {
        if (!utilisateur.isOrganisateur()) {
            Role role = roleRepository.findByNom(Role.ORGANISATEUR)
                    .orElseThrow(() -> new IllegalStateException("Rôle ORGANISATEUR absent de la base."));
            utilisateur.getRoles().add(role);
        }
    }
}
