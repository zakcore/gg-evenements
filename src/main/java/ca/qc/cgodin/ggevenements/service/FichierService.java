package ca.qc.cgodin.ggevenements.service;

import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/** Enregistre les images téléversées (affiches, logos) sur disque ; elles sont servies sous /uploads/**. */
@Service
public class FichierService {

    private static final Map<String, String> EXTENSIONS = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif");

    private static final long TAILLE_MAX = 5L * 1024 * 1024;

    private final Path dossier;

    public FichierService(@Value("${app.uploads.dir:uploads}") String dossier) {
        this.dossier = Path.of(dossier).toAbsolutePath().normalize();
    }

    public Path getDossier() {
        return dossier;
    }

    /** @return l'URL publique de l'affiche, ou {@code null} si aucun fichier n'a été choisi. */
    public String enregistrerAffiche(MultipartFile fichier) {
        return enregistrerImage(fichier, "image");
    }

    /** @return l'URL publique du logo, ou {@code null} si aucun fichier n'a été choisi. */
    public String enregistrerLogo(MultipartFile fichier) {
        return enregistrerImage(fichier, "logo");
    }

    /** @param champ champ du formulaire auquel rattacher un éventuel message d'erreur */
    private String enregistrerImage(MultipartFile fichier, String champ) {
        if (fichier == null || fichier.isEmpty()) {
            return null;
        }
        if (fichier.getSize() > TAILLE_MAX) {
            throw new RegleAffaireException(champ, "L'image dépasse la taille maximale permise (5 Mo).");
        }
        String type = fichier.getContentType() == null ? "" : fichier.getContentType().toLowerCase(Locale.ROOT);
        String extension = EXTENSIONS.get(type);
        if (extension == null) {
            throw new RegleAffaireException(champ, "Format non pris en charge : utilisez une image JPG, PNG, WEBP ou GIF.");
        }
        try {
            Files.createDirectories(dossier);
            String nom = UUID.randomUUID() + extension;
            try (InputStream in = fichier.getInputStream()) {
                Files.copy(in, dossier.resolve(nom), StandardCopyOption.REPLACE_EXISTING);
            }
            return "/uploads/" + nom;
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible d'enregistrer l'image téléversée.", e);
        }
    }
}
