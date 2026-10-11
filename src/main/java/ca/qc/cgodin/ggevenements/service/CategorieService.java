package ca.qc.cgodin.ggevenements.service;

import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.exception.RessourceIntrouvableException;
import ca.qc.cgodin.ggevenements.model.Categorie;
import ca.qc.cgodin.ggevenements.repository.CategorieRepository;
import ca.qc.cgodin.ggevenements.repository.EvenementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategorieService {

    private final CategorieRepository categorieRepository;
    private final EvenementRepository evenementRepository;

    public CategorieService(CategorieRepository categorieRepository, EvenementRepository evenementRepository) {
        this.categorieRepository = categorieRepository;
        this.evenementRepository = evenementRepository;
    }

    public List<Categorie> listerToutes() {
        return categorieRepository.findAllByOrderByNomAsc();
    }

    public Categorie trouver(Long id) {
        return categorieRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Catégorie introuvable."));
    }

    @Transactional
    public Categorie enregistrer(Categorie categorie) {
        String nom = categorie.getNom().trim();
        boolean doublon = categorie.getId() == null
                ? categorieRepository.existsByNomIgnoreCase(nom)
                : categorieRepository.existsByNomIgnoreCaseAndIdNot(nom, categorie.getId());
        if (doublon) {
            throw new RegleAffaireException("nom", "Une catégorie porte déjà ce nom.");
        }
        if (categorie.getId() != null) {
            trouver(categorie.getId());
        }
        categorie.setNom(nom);
        categorie.setCouleur(categorie.getCouleur().toUpperCase());
        return categorieRepository.save(categorie);
    }

    /** Refuse proprement la suppression d'une catégorie encore utilisée, avant toute erreur d'intégrité SQL. */
    @Transactional
    public void supprimer(Long id) {
        Categorie categorie = trouver(id);
        if (evenementRepository.existsByCategorieId(id)) {
            throw new RegleAffaireException("La catégorie « " + categorie.getNom()
                    + " » est utilisée par au moins un événement et ne peut pas être supprimée.");
        }
        categorieRepository.delete(categorie);
    }
}
