package ca.qc.cgodin.ggevenements.exception;

/** Ressource demandée inexistante : traduite en page 404. */
public class RessourceIntrouvableException extends RuntimeException {

    public RessourceIntrouvableException(String message) {
        super(message);
    }
}
