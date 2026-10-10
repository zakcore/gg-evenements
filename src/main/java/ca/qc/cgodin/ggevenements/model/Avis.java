package ca.qc.cgodin.ggevenements.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "avis",
        uniqueConstraints = @UniqueConstraint(name = "uq_avis", columnNames = {"evenement_id", "utilisateur_id"}))
public class Avis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(nullable = false)
    private int note;

    @Column(length = 500)
    private String commentaire;

    @Column(name = "date_avis", nullable = false)
    private LocalDateTime dateAvis;

    protected Avis() {
    }

    public Avis(Evenement evenement, Utilisateur utilisateur, int note, String commentaire) {
        this.evenement = evenement;
        this.utilisateur = utilisateur;
        this.note = note;
        this.commentaire = commentaire;
        this.dateAvis = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public int getNote() {
        return note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public LocalDateTime getDateAvis() {
        return dateAvis;
    }
}
