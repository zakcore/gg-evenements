package ca.qc.cgodin.ggevenements.repository;

import ca.qc.cgodin.ggevenements.model.Avis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface AvisRepository extends JpaRepository<Avis, Integer> {

    List<Avis> findByEvenementIdOrderByDateAvisDesc(Integer evenementId);

    boolean existsByEvenementIdAndUtilisateurId(Integer evenementId, Integer utilisateurId);

    @Query("select avg(a.note) from Avis a where a.evenement.id = :evenementId")
    Double moyennePourEvenement(@Param("evenementId") Integer evenementId);

    @Query("select avg(a.note) from Avis a")
    Double moyenneGenerale();

    @Query("select a.evenement.id from Avis a where a.utilisateur.id = :utilisateurId")
    Set<Integer> findEvenementIdsAvecAvis(@Param("utilisateurId") Integer utilisateurId);
}
