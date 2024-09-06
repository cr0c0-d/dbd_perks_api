package dbd.perks.repository;

import dbd.perks.domain.Perk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerkRepository extends JpaRepository<Perk, Long> {
    List<Perk> findByRole(String role);

    Optional<Perk> findFirstByEnNameOrderByCreatedAtDesc(String enName);

    List<Perk> findByIsActivatedTrue();
}
