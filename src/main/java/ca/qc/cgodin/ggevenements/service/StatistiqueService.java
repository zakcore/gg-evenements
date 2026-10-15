package ca.qc.cgodin.ggevenements.service;

import ca.qc.cgodin.ggevenements.dto.Statistiques;
import ca.qc.cgodin.ggevenements.model.StatutEvenement;
import ca.qc.cgodin.ggevenements.repository.AvisRepository;
import ca.qc.cgodin.ggevenements.repository.EvenementRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Fonctionnalité 16 : chaque chiffre provient d'une requête d'agrégation (COUNT, AVG, GROUP BY). */
@Service
@Transactional(readOnly = true)
public class StatistiqueService {

    private final EvenementRepository evenementRepository;
    private final AvisRepository avisRepository;

    public StatistiqueService(EvenementRepository evenementRepository, AvisRepository avisRepository) {
        this.evenementRepository = evenementRepository;
        this.avisRepository = avisRepository;
    }

    public Statistiques calculer() {
        Map<StatutEvenement, Long> parStatut = new EnumMap<>(StatutEvenement.class);
        for (Object[] ligne : evenementRepository.compterParStatut()) {
            parStatut.put((StatutEvenement) ligne[0], (Long) ligne[1]);
        }
        List<Statistiques.ClubActif> clubs = evenementRepository.clubsLesPlusActifs(PageRequest.of(0, 5)).stream()
                .map(l -> new Statistiques.ClubActif((Integer) l[0], (String) l[1], (Long) l[2]))
                .toList();
        return new Statistiques(
                parStatut.getOrDefault(StatutEvenement.PLANIFIE, 0L),
                parStatut.getOrDefault(StatutEvenement.ANNULE, 0L),
                parStatut.getOrDefault(StatutEvenement.TERMINE, 0L),
                evenementRepository.tauxMoyenRemplissageTermines(),
                clubs,
                avisRepository.moyenneGenerale());
    }
}
