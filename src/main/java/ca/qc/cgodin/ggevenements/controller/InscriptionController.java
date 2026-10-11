package ca.qc.cgodin.ggevenements.controller;

import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.model.StatutInscription;
import ca.qc.cgodin.ggevenements.security.UtilisateurDetails;
import ca.qc.cgodin.ggevenements.service.InscriptionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class InscriptionController {

    private final InscriptionService inscriptionService;

    public InscriptionController(InscriptionService inscriptionService) {
        this.inscriptionService = inscriptionService;
    }

    /** Fonctionnalité 5 : un seul clic, aucun formulaire supplémentaire. */
    @PostMapping("/evenements/{id}/inscription")
    public String inscrire(@PathVariable Long id, @AuthenticationPrincipal UtilisateurDetails utilisateur,
                           RedirectAttributes attributs) {
        try {
            StatutInscription statut = inscriptionService.inscrire(id, utilisateur.getId());
            if (statut == StatutInscription.CONFIRMEE) {
                attributs.addFlashAttribute("succes", "Votre inscription est confirmée.");
            } else {
                attributs.addFlashAttribute("info",
                        "L'événement est complet : vous avez été placé(e) en liste d'attente. "
                                + "Votre place sera confirmée automatiquement si une personne se désinscrit.");
            }
        } catch (RegleAffaireException e) {
            attributs.addFlashAttribute("erreur", e.getMessage());
        }
        return "redirect:/evenements/" + id;
    }

    /** Fonctionnalité 6 : la confirmation est demandée dans l'interface avant l'envoi de ce formulaire. */
    @PostMapping("/evenements/{id}/desinscription")
    public String desinscrire(@PathVariable Long id, @RequestParam(defaultValue = "detail") String origine,
                              @AuthenticationPrincipal UtilisateurDetails utilisateur, RedirectAttributes attributs) {
        try {
            inscriptionService.desinscrire(id, utilisateur.getId());
            attributs.addFlashAttribute("succes", "Vous avez été désinscrit(e) de cet événement.");
        } catch (RegleAffaireException e) {
            attributs.addFlashAttribute("erreur", e.getMessage());
        }
        return "mes-evenements".equals(origine) ? "redirect:/mes-evenements" : "redirect:/evenements/" + id;
    }
}
