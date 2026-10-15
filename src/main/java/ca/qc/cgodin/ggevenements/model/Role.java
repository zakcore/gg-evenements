package ca.qc.cgodin.ggevenements.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {

    /** Valeurs de la colonne nom_role ; Spring Security reçoit l'autorité « ROLE_ » + nom_role. */
    public static final String PUBLIC = "PUBLIC";
    public static final String ORGANISATEUR = "ORGANISATEUR";
    public static final String ADMIN = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Integer id;

    @Column(name = "nom_role", nullable = false, unique = true, length = 30)
    private String nom;

    @Column(length = 150)
    private String description;

    protected Role() {
    }

    public Role(String nom, String description) {
        this.nom = nom;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    /** Autorité Spring Security : ORGANISATEUR → ROLE_ORGANISATEUR (hasRole('ORGANISATEUR')). */
    public String getAutorite() {
        return "ROLE_" + nom;
    }

    /** Libellé lisible : ORGANISATEUR → Organisateur. */
    public String getLibelle() {
        return switch (nom) {
            case ADMIN -> "Administrateur";
            case ORGANISATEUR -> "Organisateur";
            default -> "Public";
        };
    }
}
