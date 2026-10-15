package ca.qc.cgodin.ggevenements.repository;

import ca.qc.cgodin.ggevenements.model.Evenement;
import ca.qc.cgodin.ggevenements.model.StatutEvenement;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Critères dynamiques de recherche d'événements (Specifications Spring Data JPA).
 * Chaque critère nul est ignoré : la requête SQL n'est construite qu'avec les filtres choisis.
 */
public final class EvenementSpecifications {

    private EvenementSpecifications() {
    }

    /** Événements à venir : statut PLANIFIE et date de début future. */
    public static Specification<Evenement> aVenir() {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("statut"), StatutEvenement.PLANIFIE),
                cb.greaterThan(root.get("dateDebut"), LocalDateTime.now()));
    }

    public static Specification<Evenement> titreContient(String texte) {
        if (texte == null || texte.isBlank()) {
            return null;
        }
        String motif = "%" + texte.trim().toLowerCase()
                .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("titre")), motif, '\\');
    }

    public static Specification<Evenement> categorie(Integer categorieId) {
        return categorieId == null ? null
                : (root, query, cb) -> cb.equal(root.get("categorie").get("id"), categorieId);
    }

    public static Specification<Evenement> club(Integer clubId) {
        return clubId == null ? null
                : (root, query, cb) -> cb.equal(root.get("club").get("id"), clubId);
    }

    public static Specification<Evenement> debutApresLe(LocalDate du) {
        return du == null ? null
                : (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("dateDebut"), du.atStartOfDay());
    }

    public static Specification<Evenement> debutAvantLe(LocalDate au) {
        return au == null ? null
                : (root, query, cb) -> cb.lessThan(root.get("dateDebut"), au.plusDays(1).atStartOfDay());
    }
}
