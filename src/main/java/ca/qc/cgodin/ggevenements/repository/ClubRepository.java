package ca.qc.cgodin.ggevenements.repository;

import ca.qc.cgodin.ggevenements.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

    List<Club> findAllByOrderByNomAsc();

    List<Club> findByActifTrueOrderByNomAsc();

    Optional<Club> findByResponsableId(Long responsableId);

    boolean existsByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCaseAndIdNot(String nom, Long id);
}
