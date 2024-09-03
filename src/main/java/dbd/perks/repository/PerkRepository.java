package dbd.perks.repository;

import dbd.perks.domain.Perk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerkRepository extends JpaRepository<Perk, Long> {
    List<Perk> findByRole(String role);
}
