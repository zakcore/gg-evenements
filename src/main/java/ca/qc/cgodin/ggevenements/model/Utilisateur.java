package ca.qc.cgodin.ggevenements.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Integer id;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, unique = true, length = 100)
    private String courriel;

    @Column(name = "mot_passe", nullable = false, length = 60)
    private String motPasse;

    @Column(nullable = false)
    private boolean actif = true;

    @Column(name = "date_inscription", nullable = false)
    private LocalDateTime dateInscription = LocalDateTime.now();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "utilisateur_role",
            joinColumns = @JoinColumn(name = "id_utilisateur"),
            inverseJoinColumns = @JoinColumn(name = "id_role"))
    private Set<Role> roles = new HashSet<>();

    public boolean aRole(String nomRole) {
        return roles.stream().anyMatch(r -> r.getNom().equals(nomRole));
    }

    public boolean isOrganisateur() {
        return aRole(Role.ORGANISATEUR);
    }

    public boolean isAdmin() {
        return aRole(Role.ADMIN);
    }

    /** Rôles triés pour un affichage stable (ADMIN, ORGANISATEUR, PUBLIC). */
    public List<Role> getRolesTries() {
        return roles.stream().sorted(Comparator.comparing(Role::getNom)).toList();
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public String getInitiales() {
        return (prenom.isEmpty() ? "" : prenom.substring(0, 1)) + (nom.isEmpty() ? "" : nom.substring(0, 1));
    }

    public Integer getId() {
        return id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCourriel() {
        return courriel;
    }

    public void setCourriel(String courriel) {
        this.courriel = courriel;
    }

    public String getMotPasse() {
        return motPasse;
    }

    public void setMotPasse(String motPasse) {
        this.motPasse = motPasse;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}
