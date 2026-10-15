package ca.qc.cgodin.ggevenements.model;

import jakarta.persistence.*;

@Entity
@Table(name = "club")
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_club")
    private Integer id;

    @Column(nullable = false, unique = true, length = 80)
    private String nom;

    @Column(length = 300)
    private String description;

    @Column(name = "courriel_contact", nullable = false, length = 100)
    private String courrielContact;

    @Column(length = 150)
    private String logo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_responsable")
    private Utilisateur responsable;

    @Column(nullable = false)
    private boolean actif = true;

    public Integer getId() {
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

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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
