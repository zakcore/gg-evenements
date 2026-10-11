package ca.qc.cgodin.ggevenements.service;

import ca.qc.cgodin.ggevenements.exception.RegleAffaireException;
import ca.qc.cgodin.ggevenements.exception.RessourceIntrouvableException;
import ca.qc.cgodin.ggevenements.model.Lieu;
import ca.qc.cgodin.ggevenements.repository.EvenementRepository;
import ca.qc.cgodin.ggevenements.repository.LieuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LieuService {

    private final LieuRepository lieuRepository;
    private final EvenementRepository evenementRepository;

    public LieuService(LieuRepository lieuRepository, EvenementRepository evenementRepository) {
        this.lieuRepository = lieuRepository;
        this.evenementRepository = evenementRepository;
    }

    public List<Lieu> listerTous() {
        return lieuRepository.findAllByOrderByNomAsc();
    }

    public Lieu trouver(Long id) {
        return lieuRepository.findById(id)
                .orElseThrow(() -> new RessourceIntrouvableException("Lieu introuvable."));
    }

    @Transactional
    public Lieu enregistrer(Lieu lieu) {
        if (lieu.getId() != null) {
            trouver(lieu.getId());
        }
        lieu.setNom(lieu.getNom().trim());
        lieu.setCampus(lieu.getCampus().trim());
        lieu.setAdresse(lieu.getAdresse().trim());
        return lieuRepository.save(lieu);
    }

    /** Refuse proprement la suppression d'un lieu encore utilisé, avant toute erreur d'intégrité SQL. */
    @Transactional
    public void supprimer(Long id) {
        Lieu lieu = trouver(id);
        if (evenementRepository.existsByLieuId(id)) {
            throw new RegleAffaireException("Le lieu « " + lieu.getNom()
                    + " » est utilisé par au moins un événement et ne peut pas être supprimé.");
        }
        lieuRepository.delete(lieu);
    }
}
