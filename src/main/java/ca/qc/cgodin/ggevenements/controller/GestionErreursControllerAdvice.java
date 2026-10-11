package ca.qc.cgodin.ggevenements.controller;

import ca.qc.cgodin.ggevenements.exception.AccesRefuseException;
import ca.qc.cgodin.ggevenements.exception.RessourceIntrouvableException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

@ControllerAdvice
public class GestionErreursControllerAdvice {

    /** Chemin courant, utilisé par la barre de navigation pour indiquer la section active. */
    @ModelAttribute("uriCourante")
    public String uriCourante(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    /** URL courante (filtres et tri compris) sans le paramètre « page » : base des liens de pagination. */
    @ModelAttribute("urlSansPage")
    public String urlSansPage(HttpServletRequest request) {
        return UriComponentsBuilder.fromPath(uriCourante(request))
                .query(request.getQueryString())
                .replaceQueryParam("page")
                .build()
                .toUriString();
    }

    @ExceptionHandler({RessourceIntrouvableException.class, MethodArgumentTypeMismatchException.class})
    public ModelAndView introuvable(Exception ex, HttpServletResponse response) {
        response.setStatus(HttpStatus.NOT_FOUND.value());
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex instanceof RessourceIntrouvableException ? ex.getMessage() : null);
        return mav;
    }

    @ExceptionHandler(AccesRefuseException.class)
    public ModelAndView accesRefuse(AccesRefuseException ex, HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        ModelAndView mav = new ModelAndView("error/403");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String fichierTropVolumineux(HttpServletRequest request, RedirectAttributes attributs) {
        attributs.addFlashAttribute("erreur", "L'image dépasse la taille maximale permise (5 Mo).");
        String origine = request.getRequestURI().substring(request.getContextPath().length());
        return "redirect:" + origine;
    }
}
