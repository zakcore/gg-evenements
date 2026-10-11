package ca.qc.cgodin.ggevenements.dto;

import ca.qc.cgodin.ggevenements.model.Club;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ClubForm {

    @NotBlank(message = "Le nom du club est obligatoire.")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères.")
    private String nom;

    @Size(max = 1000, message = "La description ne doit pas dépasser 1000 caractères.")
    private String description;

    @NotBlank(message = "Le courriel de contact est obligatoire.")
    @Email(message = "Le courriel de contact n'est pas valide.")
    @Size(max = 120, message = "Le courriel ne doit pas dépasser 120 caractères.")
    private String courrielContact;

    @NotNull(message = "Choisissez un responsable.")
    private Long responsableId;

    public static ClubForm depuis(Club club) {
        ClubForm f = new ClubForm();
        f.nom = club.getNom();
        f.description = club.getDescription();
        f.courrielContact = club.getCourrielContact();
        f.responsableId = club.getResponsable().getId();
        return f;
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

    public Long getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Long responsableId) {
        this.responsableId = responsableId;
    }
}
