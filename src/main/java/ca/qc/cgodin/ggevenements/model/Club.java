package ca.qc.cgodin.ggevenements.model;

import jakarta.persistence.*;

@Entity
@Table(name = "club")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(length = 1000)
    private String description;

    @Column(name = "courriel_contact", nullable = false, length = 120)
    private String courrielContact;

    @ManyToOne(optional = false)
    @JoinColumn(name = "responsable_id")
    private Utilisateur responsable;

    @Column(nullable = false)
    private boolean actif = true;

    public Long getId() {
        return id;
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

    public String getCourrielContact() {
        return courrielContact;
    }

    public void setCourrielContact(String courrielContact) {
        this.courrielContact = courrielContact;
    }

    public Utilisateur getResponsable() {
        return responsable;
    }

    public void setResponsable(Utilisateur responsable) {
        this.responsable = responsable;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}
