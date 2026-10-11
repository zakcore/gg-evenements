package ca.qc.cgodin.ggevenements.controller;

import ca.qc.cgodin.ggevenements.dto.ClubForm;
import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.model.Categorie;
import ca.qc.cgodin.ggevenements.model.Club;
import ca.qc.cgodin.ggevenements.model.Lieu;
import ca.qc.cgodin.ggevenements.model.Utilisateur;
import ca.qc.cgodin.ggevenements.security.UtilisateurDetails;
import ca.qc.cgodin.ggevenements.service.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Espace administrateur (fonctionnalités 13 à 16) — /admin/** est réservé à ROLE_ADMIN. */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StatistiqueService statistiqueService;
    private final UtilisateurService utilisateurService;
    private final ClubService clubService;
    private final CategorieService categorieService;
    private final LieuService lieuService;

    public AdminController(StatistiqueService statistiqueService, UtilisateurService utilisateurService,
                           ClubService clubService, CategorieService categorieService, LieuService lieuService) {
        this.statistiqueService = statistiqueService;
        this.utilisateurService = utilisateurService;
        this.clubService = clubService;
        this.categorieService = categorieService;
        this.lieuService = lieuService;
    }

    // ----- Fonctionnalité 16 : tableau de bord -----

    @GetMapping
    public String tableauDeBord(Model model) {
        model.addAttribute("stats", statistiqueService.calculer());
        return "admin/tableau-de-bord";
    }

    // ----- Fonctionnalité 13 : utilisateurs -----

    @GetMapping("/utilisateurs")
    public String utilisateurs(Model model) {
        model.addAttribute("utilisateurs", utilisateurService.listerTous());
        return "admin/utilisateurs";
    }

    @PostMapping("/utilisateurs/{id}/actif")
    public String basculerActif(@PathVariable Long id, @AuthenticationPrincipal UtilisateurDetails admin,
                                RedirectAttributes attributs) {
        try {
            Utilisateur u = utilisateurService.basculerActif(id, admin.getId());
            attributs.addFlashAttribute("succes", "Le compte de " + u.getNomComplet() + " a été "
                    + (u.isActif() ? "réactivé." : "désactivé."));
        } catch (RegleAffaireException e) {
            attributs.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/utilisateurs";
    }

    @PostMapping("/utilisateurs/{id}/organisateur")
    public String basculerOrganisateur(@PathVariable Long id, RedirectAttributes attributs) {
        try {
            Utilisateur u = utilisateurService.basculerRoleOrganisateur(id);
            attributs.addFlashAttribute("succes", u.isOrganisateur()
                    ? u.getNomComplet() + " a maintenant le rôle Organisateur."
                    : "Le rôle Organisateur a été retiré à " + u.getNomComplet() + ".");
        } catch (RegleAffaireException e) {
            attributs.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/utilisateurs";
    }

    // ----- Fonctionnalité 14 : clubs -----

    @GetMapping("/clubs")
    public String clubs(Model model) {
        model.addAttribute("clubs", clubService.listerTous());
        return "admin/clubs";
    }

    @GetMapping("/clubs/nouveau")
    public String nouveauClub(Model model) {
        model.addAttribute("clubForm", new ClubForm());
        return formulaireClub(model, null);
    }

    @PostMapping("/clubs/nouveau")
    public String creerClub(@Valid @ModelAttribute("clubForm") ClubForm form, BindingResult resultat, Model model,
                            RedirectAttributes attributs) {
        if (!resultat.hasErrors()) {
            try {
                Club club = clubService.creer(form);
                attributs.addFlashAttribute("succes", "Le club « " + club.getNom() + " » a été créé. "
                        + club.getResponsable().getNomComplet() + " en est responsable (rôle Organisateur accordé).");
                return "redirect:/admin/clubs";
            } catch (RegleAffaireException e) {
                rattacher(e, resultat);
            }
        }
        return formulaireClub(model, null);
    }

    @GetMapping("/clubs/{id}/modifier")
    public String modifierClub(@PathVariable Long id, Model model) {
        Club club = clubService.trouver(id);
        model.addAttribute("clubForm", ClubForm.depuis(club));
        return formulaireClub(model, club);
    }

    @PostMapping("/clubs/{id}/modifier")
    public String enregistrerClub(@PathVariable Long id, @Valid @ModelAttribute("clubForm") ClubForm form,
                                  BindingResult resultat, Model model, RedirectAttributes attributs) {
        Club club = clubService.trouver(id);
        if (!resultat.hasErrors()) {
            try {
                clubService.modifier(id, form);
                attributs.addFlashAttribute("succes", "Le club a été mis à jour.");
                return "redirect:/admin/clubs";
            } catch (RegleAffaireException e) {
                rattacher(e, resultat);
            }
        }
        return formulaireClub(model, club);
    }

    @PostMapping("/clubs/{id}/actif")
    public String basculerClub(@PathVariable Long id, RedirectAttributes attributs) {
        Club club = clubService.basculerActif(id);
        attributs.addFlashAttribute("succes", "Le club « " + club.getNom() + " » a été "
                + (club.isActif() ? "activé." : "désactivé."));
        return "redirect:/admin/clubs";
    }

    private String formulaireClub(Model model, Club club) {
        model.addAttribute("club", club);
        model.addAttribute("utilisateurs", utilisateurService.listerActifs());
        return "admin/club-formulaire";
    }

    // ----- Fonctionnalité 15 : catégories et lieux -----

    @GetMapping("/referentiels")
    public String referentiels(Model model) {
        model.addAttribute("categories", categorieService.listerToutes());
        model.addAttribute("lieux", lieuService.listerTous());
        return "admin/referentiels";
    }

    @GetMapping("/categories/nouvelle")
    public String nouvelleCategorie(Model model) {
        model.addAttribute("categorie", new Categorie());
        return "admin/categorie-formulaire";
    }

    @GetMapping("/categories/{id}/modifier")
    public String modifierCategorie(@PathVariable Long id, Model model) {
        model.addAttribute("categorie", categorieService.trouver(id));
        return "admin/categorie-formulaire";
    }

    @PostMapping({"/categories/nouvelle", "/categories/{id}/modifier"})
    public String enregistrerCategorie(@PathVariable(required = false) Long id,
                                       @Valid @ModelAttribute("categorie") Categorie categorie, BindingResult resultat,
                                       RedirectAttributes attributs) {
        categorie.setId(id);
        if (!resultat.hasErrors()) {
            try {
                categorieService.enregistrer(categorie);
                attributs.addFlashAttribute("succes", "La catégorie « " + categorie.getNom() + " » a été enregistrée.");
                return "redirect:/admin/referentiels";
            } catch (RegleAffaireException e) {
                rattacher(e, resultat);
            }
        }
        return "admin/categorie-formulaire";
    }

    @PostMapping("/categories/{id}/supprimer")
    public String supprimerCategorie(@PathVariable Long id, RedirectAttributes attributs) {
        try {
            categorieService.supprimer(id);
            attributs.addFlashAttribute("succes", "La catégorie a été supprimée.");
        } catch (RegleAffaireException e) {
            attributs.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/referentiels";
    }

    @GetMapping("/lieux/nouveau")
    public String nouveauLieu(Model model) {
        model.addAttribute("lieu", new Lieu());
        return "admin/lieu-formulaire";
    }

    @GetMapping("/lieux/{id}/modifier")
    public String modifierLieu(@PathVariable Long id, Model model) {
        model.addAttribute("lieu", lieuService.trouver(id));
        return "admin/lieu-formulaire";
    }

    @PostMapping({"/lieux/nouveau", "/lieux/{id}/modifier"})
    public String enregistrerLieu(@PathVariable(required = false) Long id, @Valid @ModelAttribute("lieu") Lieu lieu,
                                  BindingResult resultat, RedirectAttributes attributs) {
        lieu.setId(id);
        if (!resultat.hasErrors()) {
            lieuService.enregistrer(lieu);
            attributs.addFlashAttribute("succes", "Le lieu « " + lieu.getNom() + " » a été enregistré.");
            return "redirect:/admin/referentiels";
        }
        return "admin/lieu-formulaire";
    }

    @PostMapping("/lieux/{id}/supprimer")
    public String supprimerLieu(@PathVariable Long id, RedirectAttributes attributs) {
        try {
            lieuService.supprimer(id);
            attributs.addFlashAttribute("succes", "Le lieu a été supprimé.");
        } catch (RegleAffaireException e) {
            attributs.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/admin/referentiels";
    }

    private void rattacher(RegleAffaireException e, BindingResult resultat) {
        if (e.getChamp() != null) {
            resultat.rejectValue(e.getChamp(), "regle.affaire", e.getMessage());
        } else {
            resultat.reject("regle.affaire", e.getMessage());
        }
    }
}
