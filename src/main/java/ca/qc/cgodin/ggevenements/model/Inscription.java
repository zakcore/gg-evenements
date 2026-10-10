package ca.qc.cgodin.ggevenements.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inscription",
        uniqueConstraints = @UniqueConstraint(name = "uq_inscription", columnNames = {"evenement_id", "utilisateur_id"}))
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "evenement_id")
    private Evenement evenement;

    @ManyToOne(optional = false)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutInscription statut;

    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription;

    @Column(name = "presence_confirmee", nullable = false)
    private boolean presenceConfirmee;

    protected Inscription() {
    }

    public Inscription(Evenement evenement, Utilisateur utilisateur, StatutInscription statut) {
        this.evenement = evenement;
        this.utilisateur = utilisateur;
        this.statut = statut;
        this.dateInscription = LocalDateTime.now();
    }

    public boolean isConfirmee() {
        return statut == StatutInscription.CONFIRMEE;
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

    public StatutInscription getStatut() {
        return statut;
    }

    public void setStatut(StatutInscription statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public boolean isPresenceConfirmee() {
        return presenceConfirmee;
    }

    public void setPresenceConfirmee(boolean presenceConfirmee) {
        this.presenceConfirmee = presenceConfirmee;
    }
}
