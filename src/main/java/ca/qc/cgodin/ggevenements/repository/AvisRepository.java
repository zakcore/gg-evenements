package ca.qc.cgodin.ggevenements.repository;

import ca.qc.cgodin.ggevenements.model.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface AvisRepository extends JpaRepository<Avis, Long> {

    List<Avis> findByEvenementIdOrderByDateAvisDesc(Long evenementId);

    boolean existsByEvenementIdAndUtilisateurId(Long evenementId, Long utilisateurId);

    @Query("select avg(a.note) from Avis a where a.evenement.id = :evenementId")
    Double moyennePourEvenement(@Param("evenementId") Long evenementId);

    @Query("select avg(a.note) from Avis a")
    Double moyenneGenerale();

    @Query("select a.evenement.id from Avis a where a.utilisateur.id = :utilisateurId")
    Set<Long> findEvenementIdsAvecAvis(@Param("utilisateurId") Long utilisateurId);
}
