package ca.qc.cgodin.ggevenements.dto;

import ca.qc.cgodin.ggevenements.model.Club;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class ClubForm {

    @NotBlank(message = "Le nom du club est obligatoire.")
    @Size(max = 80, message = "Le nom ne doit pas dépasser 80 caractères.")
    private String nom;

    @Size(max = 300, message = "La description ne doit pas dépasser 300 caractères.")
    private String description;

    @NotBlank(message = "Le courriel de contact est obligatoire.")
    @Email(message = "Le courriel de contact n'est pas valide.")
    @Size(max = 100, message = "Le courriel ne doit pas dépasser 100 caractères.")
    private String courrielContact;

    @NotNull(message = "Choisissez un responsable.")
    private Integer responsableId;

    /** Logo facultatif ; s'il n'est pas fourni en modification, le logo actuel est conservé. */
    private MultipartFile logo;

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

    public Integer getResponsableId() {
        return responsableId;
    }

    public void setResponsableId(Integer responsableId) {
        this.responsableId = responsableId;
    }

    public MultipartFile getLogo() {
        return logo;
    }

    public void setLogo(MultipartFile logo) {
        this.logo = logo;
    }
}
