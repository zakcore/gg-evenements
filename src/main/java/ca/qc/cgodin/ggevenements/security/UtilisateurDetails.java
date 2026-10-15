package ca.qc.cgodin.ggevenements.security;

import ca.qc.cgodin.ggevenements.model.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/** Principal Spring Security : expose l'identifiant et le nom de l'utilisateur connecté. */
public class UtilisateurDetails implements UserDetails {

    private final Integer id;
    private final String courriel;
    private final String motDePasse;
    private final String prenom;
    private final String nomComplet;
    private final boolean actif;
    private final List<GrantedAuthority> autorites;

    public UtilisateurDetails(Utilisateur utilisateur) {
        this.id = utilisateur.getId();
        this.courriel = utilisateur.getCourriel();
        this.motDePasse = utilisateur.getMotPasse();
        this.prenom = utilisateur.getPrenom();
        this.nomComplet = utilisateur.getNomComplet();
        this.actif = utilisateur.isActif();
        this.autorites = utilisateur.getRoles().stream()
                .map(r -> (GrantedAuthority) new SimpleGrantedAuthority(r.getAutorite()))
                .toList();
    }

    public Integer getId() {
        return id;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public String getInitiales() {
        String[] parties = nomComplet.trim().split("\\s+");
        String initiales = parties[0].substring(0, 1);
        if (parties.length > 1) {
            initiales += parties[parties.length - 1].substring(0, 1);
        }
        return initiales.toUpperCase();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return autorites;
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public String getUsername() {
        return courriel;
    }

    @Override
    public boolean isEnabled() {
        return actif;
    }
}
