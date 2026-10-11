package ca.qc.cgodin.ggevenements.dto;

import java.util.List;

/** Chiffres du tableau de bord administrateur, tous issus de requêtes d'agrégation. */
public record Statistiques(long nbPlanifies,
                           long nbAnnules,
                           long nbTermines,
                           Double tauxMoyenRemplissage,
                           List<ClubActif> clubsActifs,
                           Double noteMoyenneGenerale) {

    public long getTotal() {
        return nbPlanifies + nbAnnules + nbTermines;
    }

    public record ClubActif(Long id, String nom, long nbEvenements) {
    }
}
