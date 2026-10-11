package ca.qc.cgodin.ggevenements.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    /** Le formulaire est traité par Spring Security (POST /login) ; ce contrôleur ne fait qu'afficher la page. */
    @GetMapping("/login")
    public String connexion(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/evenements";
        }
        return "auth/login";
    }

    @GetMapping("/")
    public String accueil() {
        return "redirect:/evenements";
    }

    /** Page d'accès refusé utilisée par la SecurityFilterChain (accessDeniedPage). */
    @GetMapping("/403")
    public String accesRefuse(HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        return "error/403";
    }
}
