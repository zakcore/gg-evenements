package ca.qc.cgodin.ggevenements.controller;

import ca.qc.cgodin.ggevenements.dto.CriteresRecherche;
import ca.qc.cgodin.ggevenements.model.Evenement;
import ca.qc.cgodin.ggevenements.security.UtilisateurDetails;
import ca.qc.cgodin.ggevenements.service.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class EvenementController {

    private final EvenementService evenementService;
    private final InscriptionService inscriptionService;
    private final AvisService avisService;
    private final CategorieService categorieService;
    private final ClubService clubService;

    public EvenementController(EvenementService evenementService, InscriptionService inscriptionService,
                               AvisService avisService, CategorieService categorieService, ClubService clubService) {
        this.evenementService = evenementService;
        this.inscriptionService = inscriptionService;
        this.avisService = avisService;
        this.categorieService = categorieService;
        this.clubService = clubService;
    }

    /** Fonctionnalité 2 : liste paginée des événements à venir. */
    @GetMapping("/evenements")
    public String liste(@ModelAttribute("criteres") CriteresRecherche criteres, Model model) {
        model.addAttribute("page", evenementService.listerAVenir(criteres));
        ajouterListesDeroulantes(model);
        return "evenements/liste";
    }

    /** Fonctionnalité 7 : recherche et filtres avancés. */
    @GetMapping("/evenements/recherche")
    public String recherche(@ModelAttribute("criteres") CriteresRecherche criteres, Model model) {
        model.addAttribute("page", evenementService.rechercher(criteres));
        ajouterListesDeroulantes(model);
        return "evenements/recherche";
    }

    /** Fonctionnalité 4 : détail d'un événement. */
    @GetMapping("/evenements/{id}")
    public String detail(@PathVariable Integer id, @AuthenticationPrincipal UtilisateurDetails utilisateur, Model model) {
        Evenement evenement = evenementService.trouver(id);
        model.addAttribute("evenement", evenement);
        model.addAttribute("inscription", inscriptionService.inscriptionDe(id, utilisateur.getId()).orElse(null));
        if (evenement.isTermine()) {
            model.addAttribute("avis", avisService.avisDe(id));
            model.addAttribute("moyenne", avisService.moyenne(id));
        }
        return "evenements/detail";
    }

    /** Fonctionnalité 3 : « Mes événements ». */
    @GetMapping("/mes-evenements")
    public String mesEvenements(@AuthenticationPrincipal UtilisateurDetails utilisateur, Model model) {
        model.addAttribute("mes", inscriptionService.mesEvenements(utilisateur.getId()));
        return "evenements/mes-evenements";
    }

    private void ajouterListesDeroulantes(Model model) {
        model.addAttribute("categories", categorieService.listerToutes());
        model.addAttribute("clubs", clubService.listerActifs());
    }
}
