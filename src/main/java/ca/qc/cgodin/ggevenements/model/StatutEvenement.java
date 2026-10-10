package ca.qc.cgodin.ggevenements.model;

public enum StatutEvenement {
    PLANIFIE("Planifié"),
    ANNULE("Annulé"),
    TERMINE("Terminé");

    private final String libelle;

    StatutEvenement(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
