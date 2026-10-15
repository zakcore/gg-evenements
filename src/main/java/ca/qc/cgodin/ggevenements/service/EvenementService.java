package ca.qc.cgodin.ggevenements.service;

import ca.qc.cgodin.ggevenements.dto.CriteresRecherche;
import ca.qc.cgodin.ggevenements.dto.EvenementForm;
import ca.qc.cgodin.ggevenements.exception.AccesRefuseException;
import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.exception.RessourceIntrouvableException;
import ca.qc.cgodin.ggevenements.model.*;
import ca.qc.cgodin.ggevenements.repository.EvenementRepository;
import ca.qc.cgodin.ggevenements.repository.InscriptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static ca.qc.cgodin.ggevenements.repository.EvenementSpecifications.*;

@Service
@Transactional(readOnly = true)
public class EvenementService {

    public static final int TAILLE_PAGE = 9;

    private static final Logger log = LoggerFactory.getLogger(EvenementService.class);

    private final EvenementRepository evenementRepository;
    private final InscriptionRepository inscriptionRepository;
    private final ClubService clubService;
    private final CategorieService categorieService;
    private final LieuService lieuService;
    private final UtilisateurService utilisateurService;
    private final FichierService fichierService;

    public EvenementService(EvenementRepository evenementRepository, InscriptionRepository inscriptionRepository,
                            ClubService clubService, CategorieService categorieService, LieuService lieuService,
                            UtilisateurService utilisateurService, FichierService fichierService) {
        this.evenementRepository = evenementRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.clubService = clubService;
        this.categorieService = categorieService;
        this.lieuService = lieuService;
        this.utilisateurService = utilisateurService;
        this.fichierService = fichierService;
    }

    // ------------------------------------------------------------------
    //  Consultation (espace public)
    // ------------------------------------------------------------------

    /** Fonctionnalité 2 : événements à venir, filtrés par catégorie et club, triés et paginés par la BD. */
    public Page<Evenement> listerAVenir(CriteresRecherche criteres) {
        Specification<Evenement> spec = Specification.where(aVenir())
                .and(categorie(criteres.getCategorie()))
                .and(club(criteres.getClub()));
        return evenementRepository.findAll(spec, pagination(criteres));
    }

    /** Fonctionnalité 7 : recherche avancée construite dynamiquement (Specifications). */
    public Page<Evenement> rechercher(CriteresRecherche criteres) {
        Specification<Evenement> spec = Specification.where(aVenir())
                .and(titreContient(criteres.getTitre()))
                .and(categorie(criteres.getCategorie()))
                .and(club(criteres.getClub()))
                .and(debutApresLe(criteres.getDu()))
                .and(debutAvantLe(criteres.getAu()));
        return evenementRepository.findAll(spec, pagination(criteres));
    }

    private PageRequest pagination(CriteresRecherche criteres) {
        Sort tri = switch (criteres.getTri() == null ? "date" : criteres.getTri()) {
            case "titre" -> Sort.by("titre").ascending();
            case "places" -> Sort.by("placesRestantes").ascending().and(Sort.by("dateDebut"));
            default -> Sort.by("dateDebut").ascending();
        };
        return PageRequest.of(criteres.getPage(), TAILLE_PAGE, tri.and(Sort.by("id")));
    }

    public Evenement trouver(Integer id) {
        return evenementRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Cet événement n'existe pas."));
    }

    public List<Evenement> aVenirDuClub(Integer clubId) {
        return evenementRepository.findByClubIdAndStatutAndDateDebutAfterOrderByDateDebutAsc(
                clubId, StatutEvenement.PLANIFIE, LocalDateTime.now());
    }

    public List<Evenement> passesDuClub(Integer clubId) {
        return evenementRepository.findPassesDuClub(clubId, LocalDateTime.now());
    }

    // ------------------------------------------------------------------
    //  Espace organisateur
    // ------------------------------------------------------------------

    /** Club dont l'utilisateur est responsable ; sans club, l'espace organisateur lui est refusé. */
    public Club clubDeLOrganisateur(Integer organisateurId) {
        return clubService.clubDuResponsable(organisateurId)
                .orElseThrow(() -> new AccesRefuseException("Aucun club ne vous est attribué. Contactez la Vie étudiante."));
    }

    public List<Evenement> evenementsDuClub(Integer organisateurId) {
        return evenementRepository.findByClubIdOrderByDateDebutDesc(clubDeLOrganisateur(organisateurId).getId());
    }

    /**
     * Charge un événement pour une action de gestion, en vérifiant qu'il appartient au club de
     * l'organisateur connecté. Sinon : AccesRefuseException → HTTP 403, quel que soit l'affichage.
     */
    public Evenement trouverPourGestion(Integer evenementId, Integer organisateurId) {
        Evenement evenement = trouver(evenementId);
        if (!evenement.getClub().getResponsable().getId().equals(organisateurId)) {
            throw new AccesRefuseException("Cet événement n'appartient pas à votre club.");
        }
        return evenement;
    }

    /** Fonctionnalité 10. Le club vient toujours de l'organisateur connecté, jamais du formulaire. */
    @Transactional
    public Evenement creer(EvenementForm form, Integer organisateurId) {
        Club club = clubDeLOrganisateur(organisateurId);
        if (!club.isActif()) {
            throw new RegleAffaireException("Votre club est désactivé : la création d'événements est suspendue.");
        }
        Evenement evenement = new Evenement();
        appliquer(evenement, form);
        evenement.setClub(club);
        evenement.setCreateur(utilisateurService.trouver(organisateurId));
        evenement.setStatut(StatutEvenement.PLANIFIE);
        evenement.setDateCreation(LocalDateTime.now());
        String image = fichierService.enregistrerAffiche(form.getImage());
        if (image != null) {
            evenement.setImageAffiche(image);
        }
        return evenementRepository.save(evenement);
    }

    /** Fonctionnalité 11 : modification, avec la règle « capacité ≥ inscriptions confirmées ». */
    @Transactional
    public Evenement modifier(Integer evenementId, EvenementForm form, Integer organisateurId) {
        trouverPourGestion(evenementId, organisateurId);
        // Verrou : aucune inscription ne peut se glisser entre la vérification et la mise à jour.
        Evenement evenement = evenementRepository.findByIdPourMiseAJour(evenementId)
                .orElseThrow(() -> new RessourceIntrouvableException("Cet événement n'existe pas."));
        verifierModifiable(evenement);

        long confirmes = inscriptionRepository.countByEvenementIdAndStatut(evenementId, StatutInscription.CONFIRMEE);
        if (form.getCapaciteMax() < confirmes) {
            throw new RegleAffaireException("capaciteMax", "La capacité ne peut pas être inférieure aux "
                    + confirmes + " inscriptions déjà confirmées.");
        }
        appliquer(evenement, form);
        String image = fichierService.enregistrerAffiche(form.getImage());
        if (image != null) {
            evenement.setImageAffiche(image);
        }
        // Capacité augmentée : les premières personnes en attente occupent les nouvelles places.
        promouvoirListeAttente(evenementId, evenement.getCapaciteMax() - confirmes);
        return evenement;
    }

    /** Annulation logique : le statut passe à ANNULE, rien n'est supprimé (inscriptions et avis conservés). */
    @Transactional
    public Evenement annuler(Integer evenementId, Integer organisateurId) {
        Evenement evenement = trouverPourGestion(evenementId, organisateurId);
        verifierModifiable(evenement);
        evenement.setStatut(StatutEvenement.ANNULE);
        return evenement;
    }

    private void verifierModifiable(Evenement evenement) {
        if (evenement.isAnnule()) {
            throw new RegleAffaireException("Cet événement est annulé et ne peut plus être modifié.");
        }
        if (evenement.isTermine()) {
            throw new RegleAffaireException("Cet événement est terminé et ne peut plus être modifié.");
        }
    }

    private void appliquer(Evenement evenement, EvenementForm form) {
        Lieu lieu = lieuService.trouver(form.getLieuId());
        if (form.getCapaciteMax() > lieu.getCapaciteMax()) {
            throw new RegleAffaireException("capaciteMax", "Le lieu « " + lieu.getNom() + " » accueille au plus "
                    + lieu.getCapaciteMax() + " personnes.");
        }
        evenement.setTitre(form.getTitre().trim());
        evenement.setDescription(form.getDescription().trim());
        evenement.setCategorie(categorieService.trouver(form.getCategorieId()));
        evenement.setLieu(lieu);
        evenement.setDateDebut(form.getDateDebut());
        evenement.setDateFin(form.getDateFin());
        evenement.setCapaciteMax(form.getCapaciteMax());
    }

    private void promouvoirListeAttente(Integer evenementId, long placesLibres) {
        if (placesLibres <= 0) {
            return;
        }
        inscriptionRepository.findByEvenementIdAndStatutOrderByDateInscriptionAscIdAsc(evenementId, StatutInscription.EN_ATTENTE)
                .stream()
                .limit(placesLibres)
                .forEach(i -> i.setStatut(StatutInscription.CONFIRMEE));
    }

    // ------------------------------------------------------------------
    //  Tâche planifiée : PLANIFIE → TERMINE lorsque la date de fin est passée
    // ------------------------------------------------------------------

    @EventListener(ApplicationReadyEvent.class)
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void marquerEvenementsTermines() {
        int nb = evenementRepository.marquerTermines(LocalDateTime.now());
        if (nb > 0) {
            log.info("{} événement(s) passé(s) au statut TERMINE", nb);
        }
    }
}
