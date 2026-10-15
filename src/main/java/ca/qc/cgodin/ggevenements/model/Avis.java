package ca.qc.cgodin.ggevenements.model;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "avis",
        uniqueConstraints = @UniqueConstraint(name = "uq_avis", columnNames = {"id_evenement", "id_utilisateur"}))
public class Avis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_avis")
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_evenement")
    private Evenement evenement;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur utilisateur;

    @JdbcTypeCode(SqlTypes.TINYINT)
    @Column(nullable = false, columnDefinition = "tinyint")
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

    public Integer getId() {
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
