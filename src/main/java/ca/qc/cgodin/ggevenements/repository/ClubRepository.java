package ca.qc.cgodin.ggevenements.repository;

import ca.qc.cgodin.ggevenements.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Integer> {

    List<Club> findAllByOrderByNomAsc();

    List<Club> findByActifTrueOrderByNomAsc();

    /** Le service garantit un club par responsable ; « First » tolère des données qui en attribueraient plusieurs. */
    Optional<Club> findFirstByResponsableIdOrderByActifDescNomAsc(Integer responsableId);

    boolean existsByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCaseAndIdNot(String nom, Integer id);
}
