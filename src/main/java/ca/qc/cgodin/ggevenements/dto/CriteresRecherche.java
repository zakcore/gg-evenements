package ca.qc.cgodin.ggevenements.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/** Critères de la liste et de la recherche avancée des événements (liés aux paramètres de l'URL). */
public class CriteresRecherche {

    private String titre;
    private Integer categorie;
    private Integer club;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate du;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate au;

    /** date | titre | places */
    private String tri = "date";

    private int page = 0;

    public boolean isFiltre() {
        return (titre != null && !titre.isBlank()) || categorie != null || club != null || du != null || au != null;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Integer getCategorie() {
        return categorie;
    }

    public void setCategorie(Integer categorie) {
        this.categorie = categorie;
    }

    public Integer getClub() {
        return club;
    }

    public void setClub(Integer club) {
        this.club = club;
    }

    public LocalDate getDu() {
        return du;
    }

    public void setDu(LocalDate du) {
        this.du = du;
    }

    public LocalDate getAu() {
        return au;
    }

    public void setAu(LocalDate au) {
        this.au = au;
    }

    public String getTri() {
        return tri;
    }

    public void setTri(String tri) {
        this.tri = tri;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = Math.max(0, page);
    }
}
