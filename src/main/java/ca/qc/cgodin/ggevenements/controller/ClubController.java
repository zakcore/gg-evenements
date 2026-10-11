package ca.qc.cgodin.ggevenements.controller;

import ca.qc.cgodin.ggevenements.service.ClubService;
import ca.qc.cgodin.ggevenements.service.EvenementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/** Fiche publique d'un club (liée depuis le détail d'un événement). */
@Controller
public class ClubController {

    private final ClubService clubService;
    private final EvenementService evenementService;

    public ClubController(ClubService clubService, EvenementService evenementService) {
        this.clubService = clubService;
        this.evenementService = evenementService;
    }

    @GetMapping("/clubs/{id}")
    public String fiche(@PathVariable Long id, Model model) {
        model.addAttribute("club", clubService.trouver(id));
        model.addAttribute("aVenir", evenementService.aVenirDuClub(id));
        model.addAttribute("passes", evenementService.passesDuClub(id));
        return "clubs/fiche";
    }
}
