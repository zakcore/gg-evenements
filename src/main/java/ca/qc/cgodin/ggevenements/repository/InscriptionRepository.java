package ca.qc.cgodin.ggevenements.repository;

import ca.qc.cgodin.ggevenements.model.Inscription;
import ca.qc.cgodin.ggevenements.model.StatutInscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InscriptionRepository extends JpaRepository<Inscription, Long> {

    Optional<Inscription> findByEvenementIdAndUtilisateurId(Long evenementId, Long utilisateurId);

    boolean existsByEvenementIdAndUtilisateurId(Long evenementId, Long utilisateurId);

    long countByEvenementIdAndStatut(Long evenementId, StatutInscription statut);

    /** Première personne de la liste d'attente (plus ancienne date d'inscription). */
    Optional<Inscription> findFirstByEvenementIdAndStatutOrderByDateInscriptionAscIdAsc(Long evenementId,
                                                                                      StatutInscription statut);

    List<Inscription> findByEvenementIdAndStatutOrderByDateInscriptionAscIdAsc(Long evenementId,
                                                                             StatutInscription statut);

    @Query("select i from Inscription i join fetch i.evenement e where i.utilisateur.id = :utilisateurId "
            + "and e.dateFin >= :maintenant and e.statut <> ca.qc.cgodin.ggevenements.model.StatutEvenement.TERMINE "
            + "order by e.dateDebut asc")
    List<Inscription> findAVenirPourUtilisateur(@Param("utilisateurId") Long utilisateurId,
                                                @Param("maintenant") LocalDateTime maintenant);

    @Query("select i from Inscription i join fetch i.evenement e where i.utilisateur.id = :utilisateurId "
            + "and (e.dateFin < :maintenant or e.statut = ca.qc.cgodin.ggevenements.model.StatutEvenement.TERMINE) "
            + "order by e.dateDebut desc")
    List<Inscription> findPassesPourUtilisateur(@Param("utilisateurId") Long utilisateurId,
                                                @Param("maintenant") LocalDateTime maintenant);
}
