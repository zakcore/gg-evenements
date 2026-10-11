package ca.qc.cgodin.ggevenements.controller;

import ca.qc.cgodin.ggevenements.dto.EvenementForm;
import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.model.Evenement;
import ca.qc.cgodin.ggevenements.security.UtilisateurDetails;
import ca.qc.cgodin.ggevenements.service.CategorieService;
import ca.qc.cgodin.ggevenements.service.EvenementService;
import ca.qc.cgodin.ggevenements.service.InscriptionService;
import ca.qc.cgodin.ggevenements.service.LieuService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashSet;
import java.util.List;

/** Espace organisateur (fonctionnalités 10 à 12) — /organisateur/** est réservé à ROLE_ORGANISATEUR. */
@Controller
@RequestMapping("/organisateur/evenements")
public class OrganisateurController {

    private final EvenementService evenementService;
    private final InscriptionService inscriptionService;
    private final CategorieService categorieService;
    private final LieuService lieuService;

    public OrganisateurController(EvenementService evenementService, InscriptionService inscriptionService,
                                  CategorieService categorieService, LieuService lieuService) {
        this.evenementService = evenementService;
        this.inscriptionService = inscriptionService;
        this.categorieService = categorieService;
        this.lieuService = lieuService;
    }

    @GetMapping
    public String mesEvenementsAOrganiser(@AuthenticationPrincipal UtilisateurDetails utilisateur, Model model) {
        model.addAttribute("club", evenementService.clubDeLOrganisateur(utilisateur.getId()));
        model.addAttribute("evenements", evenementService.evenementsDuClub(utilisateur.getId()));
        return "organisateur/evenements";
    }

    // ----- Fonctionnalité 10 : création -----

    @GetMapping("/nouveau")
    public String nouveau(@AuthenticationPrincipal UtilisateurDetails utilisateur, Model model) {
        model.addAttribute("evenementForm", new EvenementForm());
        return afficherFormulaire(model, utilisateur, null);
    }

    @PostMapping("/nouveau")
    public String creer(@Valid @ModelAttribute("evenementForm") EvenementForm form, BindingResult resultat,
                        @AuthenticationPrincipal UtilisateurDetails utilisateur, Model model,
                        RedirectAttributes attributs) {
        if (!resultat.hasErrors()) {
            try {
                Evenement evenement = evenementService.creer(form, utilisateur.getId());
                attributs.addFlashAttribute("succes", "L'événement « " + evenement.getTitre() + " » a été créé.");
                return "redirect:/organisateur/evenements";
            } catch (RegleAffaireException e) {
                rattacher(e, resultat);
            }
        }
        return afficherFormulaire(model, utilisateur, null);
    }

    // ----- Fonctionnalité 11 : modification et annulation -----

    @GetMapping("/{id}/modifier")
    public String modifier(@PathVariable Long id, @AuthenticationPrincipal UtilisateurDetails utilisateur, Model model) {
        Evenement evenement = evenementService.trouverPourGestion(id, utilisateur.getId());
        model.addAttribute("evenementForm", EvenementForm.depuis(evenement));
        return afficherFormulaire(model, utilisateur, evenement);
    }

    @PostMapping("/{id}/modifier")
    public String enregistrer(@PathVariable Long id, @Valid @ModelAttribute("evenementForm") EvenementForm form,
                              BindingResult resultat, @AuthenticationPrincipal UtilisateurDetails utilisateur,
                              Model model, RedirectAttributes attributs) {
        Evenement evenement = evenementService.trouverPourGestion(id, utilisateur.getId());
        if (!resultat.hasErrors()) {
            try {
                evenementService.modifier(id, form, utilisateur.getId());
                attributs.addFlashAttribute("succes", "Les modifications ont été enregistrées.");
                return "redirect:/organisateur/evenements";
            } catch (RegleAffaireException e) {
                rattacher(e, resultat);
            }
        }
        return afficherFormulaire(model, utilisateur, evenement);
    }

    @PostMapping("/{id}/annuler")
    public String annuler(@PathVariable Long id, @AuthenticationPrincipal UtilisateurDetails utilisateur,
                          RedirectAttributes attributs) {
        try {
            Evenement evenement = evenementService.annuler(id, utilisateur.getId());
            attributs.addFlashAttribute("succes", "L'événement « " + evenement.getTitre() + " » a été annulé.");
        } catch (RegleAffaireException e) {
            attributs.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/organisateur/evenements";
    }

    // ----- Fonctionnalité 12 : gestion des inscriptions -----

    @GetMapping("/{id}/inscriptions")
    public String inscriptions(@PathVariable Long id, @AuthenticationPrincipal UtilisateurDetails utilisateur,
                               Model model) {
        model.addAttribute("evenement", evenementService.trouverPourGestion(id, utilisateur.getId()));
        model.addAttribute("confirmees", inscriptionService.confirmees(id, utilisateur.getId()));
        model.addAttribute("enAttente", inscriptionService.enAttente(id, utilisateur.getId()));
        return "organisateur/inscriptions";
    }

    @PostMapping("/{id}/presences")
    public String presences(@PathVariable Long id, @RequestParam(name = "presents", required = false) List<Long> presents,
                            @AuthenticationPrincipal UtilisateurDetails utilisateur, RedirectAttributes attributs) {
        int nb = inscriptionService.enregistrerPresences(id, utilisateur.getId(),
                presents == null ? new HashSet<>() : new HashSet<>(presents));
        attributs.addFlashAttribute("succes", "Présences enregistrées (" + nb + " présence(s) confirmée(s)).");
        return "redirect:/organisateur/evenements/" + id + "/inscriptions";
    }

    private String afficherFormulaire(Model model, UtilisateurDetails utilisateur, Evenement evenement) {
        model.addAttribute("club", evenementService.clubDeLOrganisateur(utilisateur.getId()));
        model.addAttribute("evenement", evenement);
        model.addAttribute("categories", categorieService.listerToutes());
        model.addAttribute("lieux", lieuService.listerTous());
        return "organisateur/formulaire";
    }

    private void rattacher(RegleAffaireException e, BindingResult resultat) {
        if (e.getChamp() != null) {
            resultat.rejectValue(e.getChamp(), "regle.affaire", e.getMessage());
        } else {
            resultat.reject("regle.affaire", e.getMessage());
        }
    }
}
