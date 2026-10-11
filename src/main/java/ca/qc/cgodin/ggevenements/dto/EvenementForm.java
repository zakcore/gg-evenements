package ca.qc.cgodin.ggevenements.dto;

import ca.qc.cgodin.ggevenements.model.Evenement;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * Objet de formulaire de création / modification d'un événement.
 * Le club n'en fait volontairement pas partie : il est déduit de l'organisateur connecté.
 */
@DatesCoherentes
public class EvenementForm {

    @NotBlank(message = "Le titre est obligatoire.")
    @Size(max = 150, message = "Le titre ne doit pas dépasser 150 caractères.")
    private String titre;

    @NotBlank(message = "La description est obligatoire.")
    @Size(max = 5000, message = "La description ne doit pas dépasser 5000 caractères.")
    private String description;

    @NotNull(message = "Choisissez une catégorie.")
    private Long categorieId;

    @NotNull(message = "Choisissez un lieu.")
    private Long lieuId;

    @NotNull(message = "La date et l'heure de début sont obligatoires.")
    @Future(message = "La date de début doit être dans le futur.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateDebut;

    @NotNull(message = "La date et l'heure de fin sont obligatoires.")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime dateFin;

    @NotNull(message = "La capacité maximale est obligatoire.")
    @Positive(message = "La capacité doit être un entier positif.")
    @Max(value = 10000, message = "La capacité ne peut dépasser 10 000 places.")
    private Integer capaciteMax;

    /** Téléversement facultatif de l'affiche. */
    private MultipartFile image;

    public static EvenementForm depuis(Evenement e) {
        EvenementForm f = new EvenementForm();
        f.titre = e.getTitre();
        f.description = e.getDescription();
        f.categorieId = e.getCategorie().getId();
        f.lieuId = e.getLieu().getId();
        f.dateDebut = e.getDateDebut();
        f.dateFin = e.getDateFin();
        f.capaciteMax = e.getCapaciteMax();
        return f;
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

    public Long getCategorieId() {
        return categorieId;
    }

    public void setCategorieId(Long categorieId) {
        this.categorieId = categorieId;
    }

    public Long getLieuId() {
        return lieuId;
    }

    public void setLieuId(Long lieuId) {
        this.lieuId = lieuId;
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

    public Integer getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(Integer capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public MultipartFile getImage() {
        return image;
    }

    public void setImage(MultipartFile image) {
        this.image = image;
    }
}
