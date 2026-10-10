package ca.qc.cgodin.ggevenements.model;

import jakarta.persistence.*;

@Entity
@Table(name = "role")
public class Role {

    public static final String UTILISATEUR = "ROLE_UTILISATEUR";
    public static final String ORGANISATEUR = "ROLE_ORGANISATEUR";
    public static final String ADMIN = "ROLE_ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String nom;

    protected Role() {
    }

    public Role(String nom) {
        this.nom = nom;
    }

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    /** Libellé lisible : ROLE_ORGANISATEUR → Organisateur. */
    public String getLibelle() {
        return switch (nom) {
            case ADMIN -> "Administrateur";
            case ORGANISATEUR -> "Organisateur";
            default -> "Utilisateur";
        };
    }
}
