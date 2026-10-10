package ca.qc.cgodin.ggevenements.repository;

import ca.qc.cgodin.ggevenements.model.Evenement;
import ca.qc.cgodin.ggevenements.model.StatutEvenement;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EvenementRepository extends JpaRepository<Evenement, Long>, JpaSpecificationExecutor<Evenement> {

    /**
     * Verrou pessimiste (SELECT ... FOR UPDATE) sur la ligne de l'événement : sérialise les inscriptions
     * et désinscriptions concurrentes d'un même événement pour garantir que la capacité n'est jamais dépassée.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Evenement e where e.id = :id")
    Optional<Evenement> findByIdPourMiseAJour(@Param("id") Long id);

    List<Evenement> findByClubIdOrderByDateDebutDesc(Long clubId);

    List<Evenement> findByClubIdAndStatutAndDateDebutAfterOrderByDateDebutAsc(Long clubId, StatutEvenement statut,
                                                                              LocalDateTime maintenant);

    @Query("select e from Evenement e where e.club.id = :clubId and (e.statut = ca.qc.cgodin.ggevenements.model.StatutEvenement.TERMINE "
            + "or e.dateFin < :maintenant) order by e.dateDebut desc")
    List<Evenement> findPassesDuClub(@Param("clubId") Long clubId, @Param("maintenant") LocalDateTime maintenant);

    boolean existsByCategorieId(Long categorieId);

    boolean existsByLieuId(Long lieuId);

    long countByClubId(Long clubId);

    /** Passe au statut TERMINE les événements planifiés dont la date de fin est dépassée. */
    @Modifying
    @Query("update Evenement e set e.statut = ca.qc.cgodin.ggevenements.model.StatutEvenement.TERMINE "
            + "where e.statut = ca.qc.cgodin.ggevenements.model.StatutEvenement.PLANIFIE and e.dateFin < :maintenant")
    int marquerTermines(@Param("maintenant") LocalDateTime maintenant);

    // ----- Agrégations pour le tableau de bord (exécutées par la base de données) -----

    @Query("select e.statut, count(e) from Evenement e group by e.statut")
    List<Object[]> compterParStatut();

    @Query("select c.id, c.nom, count(e) from Evenement e join e.club c group by c.id, c.nom order by count(e) desc, c.nom asc")
    List<Object[]> clubsLesPlusActifs(Pageable pageable);

    /** Moyenne, sur les événements terminés, du ratio inscriptions confirmées / capacité. */
    @Query(value = """
            SELECT AVG(t.taux) FROM (
                SELECT COUNT(i.id) / e.capacite_max AS taux
                FROM evenement e
                LEFT JOIN inscription i ON i.evenement_id = e.id AND i.statut = 'CONFIRMEE'
                WHERE e.statut = 'TERMINE'
                GROUP BY e.id, e.capacite_max
            ) t
            """, nativeQuery = true)
    Double tauxMoyenRemplissageTermines();
}
