package ca.qc.cgodin.ggevenements.repository;

import ca.qc.cgodin.ggevenements.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByNom(String nom);
}
