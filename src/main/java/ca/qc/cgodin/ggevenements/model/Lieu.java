package ca.qc.cgodin.ggevenements.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "lieu")
public class Lieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le campus est obligatoire.")
    @Size(max = 100, message = "Le campus ne doit pas dépasser 100 caractères.")
    @Column(nullable = false, length = 100)
    private String campus;

    @NotBlank(message = "L'adresse est obligatoire.")
    @Size(max = 200, message = "L'adresse ne doit pas dépasser 200 caractères.")
    @Column(nullable = false, length = 200)
    private String adresse;

    @NotNull(message = "La capacité maximale est obligatoire.")
    @Positive(message = "La capacité doit être un entier positif.")
    @Column(name = "capacite_max", nullable = false)
    private Integer capaciteMax;

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

    public String getCampus() {
        return campus;
    }

    public void setCampus(String campus) {
        this.campus = campus;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Integer getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(Integer capaciteMax) {
        this.capaciteMax = capaciteMax;
    }
}
