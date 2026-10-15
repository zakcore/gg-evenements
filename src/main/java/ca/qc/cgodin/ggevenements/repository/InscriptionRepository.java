package ca.qc.cgodin.ggevenements.repository;

import ca.qc.cgodin.ggevenements.model.Inscription;
import ca.qc.cgodin.ggevenements.model.StatutInscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InscriptionRepository extends JpaRepository<Inscription, Integer> {

    Optional<Inscription> findByEvenementIdAndUtilisateurId(Integer evenementId, Integer utilisateurId);

    boolean existsByEvenementIdAndUtilisateurId(Integer evenementId, Integer utilisateurId);

    long countByEvenementIdAndStatut(Integer evenementId, StatutInscription statut);

    /** Première personne de la liste d'attente (plus ancienne date d'inscription). */
    Optional<Inscription> findFirstByEvenementIdAndStatutOrderByDateInscriptionAscIdAsc(Integer evenementId,
                                                                                      StatutInscription statut);

    List<Inscription> findByEvenementIdAndStatutOrderByDateInscriptionAscIdAsc(Integer evenementId,
                                                                             StatutInscription statut);

    @Query("select i from Inscription i join fetch i.evenement e where i.utilisateur.id = :utilisateurId "
            + "and e.dateFin >= :maintenant and e.statut <> ca.qc.cgodin.ggevenements.model.StatutEvenement.TERMINE "
            + "order by e.dateDebut asc")
    List<Inscription> findAVenirPourUtilisateur(@Param("utilisateurId") Integer utilisateurId,
                                                @Param("maintenant") LocalDateTime maintenant);

    @Query("select i from Inscription i join fetch i.evenement e where i.utilisateur.id = :utilisateurId "
            + "and (e.dateFin < :maintenant or e.statut = ca.qc.cgodin.ggevenements.model.StatutEvenement.TERMINE) "
            + "order by e.dateDebut desc")
    List<Inscription> findPassesPourUtilisateur(@Param("utilisateurId") Integer utilisateurId,
                                                @Param("maintenant") LocalDateTime maintenant);
}
