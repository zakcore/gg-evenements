package ca.qc.cgodin.ggevenements.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "categorie")
public class Categorie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 60, message = "Le nom ne doit pas dépasser 60 caractères.")
    @Column(nullable = false, unique = true, length = 60)
    private String nom;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères.")
    @Column(length = 500)
    private String description;

    @NotBlank(message = "La couleur est obligatoire.")
    @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "La couleur doit être au format #RRGGBB.")
    @Column(nullable = false, length = 7)
    private String couleur = "#787774";

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCouleur() {
        return couleur;
    }

    public void setCouleur(String couleur) {
        this.couleur = couleur;
    }
}
