package ca.qc.cgodin.ggevenements.model;

public enum StatutInscription {
    CONFIRMEE("Confirmé"),
    EN_ATTENTE("En liste d'attente");

    private final String libelle;

    StatutInscription(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
