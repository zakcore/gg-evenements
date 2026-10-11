package ca.qc.cgodin.ggevenements.exception;

/** Action interdite détectée par la couche service (ex. gérer l'événement d'un autre club) : page 403. */
public class AccesRefuseException extends RuntimeException {

    public AccesRefuseException(String message) {
        super(message);
    }
}
