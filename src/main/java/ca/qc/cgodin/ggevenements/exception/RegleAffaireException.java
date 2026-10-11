package ca.qc.cgodin.ggevenements.exception;

/**
 * Violation d'une règle d'affaires. Lorsque {@code champ} est renseigné, le contrôleur affiche
 * le message à côté du champ concerné du formulaire.
 */
public class RegleAffaireException extends RuntimeException {

    private final String champ;

    public RegleAffaireException(String message) {
        this(null, message);
    }

    public RegleAffaireException(String champ, String message) {
        super(message);
        this.champ = champ;
    }

    public String getChamp() {
        return champ;
    }
}
