package ca.qc.cgodin.ggevenements.controller;

import ca.qc.cgodin.ggevenements.dto.AvisForm;
import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.security.UtilisateurDetails;
import ca.qc.cgodin.ggevenements.service.AvisService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Fonctionnalité 8 : laisser un avis sur un événement terminé. */
@Controller
public class AvisController {

    private final AvisService avisService;

    public AvisController(AvisService avisService) {
        this.avisService = avisService;
    }

    @GetMapping("/evenements/{id}/avis")
    public String formulaire(@PathVariable Integer id, @AuthenticationPrincipal UtilisateurDetails utilisateur,
                             Model model, RedirectAttributes attributs) {
        try {
            model.addAttribute("evenement", avisService.verifierAdmissible(id, utilisateur.getId()));
        } catch (RegleAffaireException e) {
            attributs.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/mes-evenements";
        }
        model.addAttribute("avisForm", new AvisForm());
        return "evenements/avis";
    }

    @PostMapping("/evenements/{id}/avis")
    public String soumettre(@PathVariable Integer id, @Valid @ModelAttribute("avisForm") AvisForm form,
                            BindingResult resultat, @AuthenticationPrincipal UtilisateurDetails utilisateur,
                            Model model, RedirectAttributes attributs) {
        try {
            if (resultat.hasErrors()) {
                model.addAttribute("evenement", avisService.verifierAdmissible(id, utilisateur.getId()));
                return "evenements/avis";
            }
            avisService.soumettre(id, utilisateur.getId(), form);
        } catch (RegleAffaireException e) {
            attributs.addFlashAttribute("erreur", e.getMessage());
            return "redirect:/mes-evenements";
        }
        attributs.addFlashAttribute("succes", "Merci ! Votre avis a été enregistré.");
        return "redirect:/mes-evenements";
    }
}
