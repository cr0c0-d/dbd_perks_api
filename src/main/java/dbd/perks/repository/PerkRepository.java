package dbd.perks.repository;

import dbd.perks.domain.Perk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerkRepository extends JpaRepository<Perk, Long> {
}
