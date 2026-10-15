package ca.qc.cgodin.ggevenements.model;

import jakarta.persistence.*;
import org.hibernate.annotations.Formula;

import java.time.LocalDateTime;

@Entity
@Table(name = "evenement")
public class Evenement {

    public static final String IMAGE_DEFAUT = "/img/affiche-defaut.svg";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evenement")
    private Integer id;

    @Column(nullable = false, length = 120)
    private String titre;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(name = "date_debut", nullable = false)
    private LocalDateTime dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDateTime dateFin;

    @Column(name = "capacite_max", nullable = false)
    private int capaciteMax;

    @Column(name = "image_affiche", length = 150)
    private String imageAffiche;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutEvenement statut = StatutEvenement.PLANIFIE;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_categorie")
    private Categorie categorie;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_lieu")
    private Lieu lieu;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_club")
    private Club club;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_createur")
    private Utilisateur createur;

    /*
     * Compteurs calculés par la base de données (sous-requêtes SQL), en lecture seule.
     * placesRestantes sert aussi au tri « places restantes » de la liste paginée.
     */
    @Formula("(select count(*) from inscription i where i.id_evenement = id_evenement and i.statut = 'CONFIRMEE')")
    private int nbConfirmes;

    @Formula("(select count(*) from inscription i where i.id_evenement = id_evenement and i.statut = 'EN_ATTENTE')")
    private int nbEnAttente;

    @Formula("(capacite_max - (select count(*) from inscription i where i.id_evenement = id_evenement and i.statut = 'CONFIRMEE'))")
    private int placesRestantes;

    /** Terminé lorsque le statut l'indique ou que la date de fin est passée (avant le passage de la tâche planifiée). */
    public boolean isTermine() {
        return statut == StatutEvenement.TERMINE
                || (statut == StatutEvenement.PLANIFIE && dateFin != null && dateFin.isBefore(LocalDateTime.now()));
    }

    public boolean isAnnule() {
        return statut == StatutEvenement.ANNULE;
    }

    /** Planifié et pas encore commencé : on peut s'y inscrire ou s'en désinscrire. */
    public boolean isOuvert() {
        return statut == StatutEvenement.PLANIFIE && dateDebut != null && dateDebut.isAfter(LocalDateTime.now());
    }

    /** Passé = terminé, ou annulé dont la date est dépassée. */
    public boolean isPasse() {
        return isTermine() || (dateFin != null && dateFin.isBefore(LocalDateTime.now()));
    }

    public boolean isComplet() {
        return placesRestantes <= 0;
    }

    /** Pourcentage de remplissage (0-100) pour la barre visuelle. */
    public int getTauxRemplissage() {
        if (capaciteMax <= 0) {
            return 0;
        }
        return Math.min(100, Math.round(nbConfirmes * 100f / capaciteMax));
    }

    public String getImageUrl() {
        return imageAffiche == null || imageAffiche.isBlank() ? IMAGE_DEFAUT : imageAffiche;
    }

    public Integer getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public int getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(int capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public String getImageAffiche() {
        return imageAffiche;
    }

    public void setImageAffiche(String imageAffiche) {
        this.imageAffiche = imageAffiche;
    }

    public StatutEvenement getStatut() {
        return statut;
    }

    public void setStatut(StatutEvenement statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Utilisateur getCreateur() {
        return createur;
    }

    public void setCreateur(Utilisateur createur) {
        this.createur = createur;
    }

    public int getNbConfirmes() {
        return nbConfirmes;
    }

    public int getNbEnAttente() {
        return nbEnAttente;
    }

    public int getPlacesRestantes() {
        return placesRestantes;
    }
}
