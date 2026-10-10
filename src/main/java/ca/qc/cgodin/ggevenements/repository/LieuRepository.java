package ca.qc.cgodin.ggevenements.repository;

import ca.qc.cgodin.ggevenements.model.Lieu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LieuRepository extends JpaRepository<Lieu, Long> {

    List<Lieu> findAllByOrderByNomAsc();
}
