package dbd.perks.repository;

import dbd.perks.domain.Weapon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeaponRepository extends JpaRepository<Weapon, Long> {
    Weapon findByKillerId(Long killerId);

    Optional<Weapon> findFirstByEnNameOrderByCreatedAtDesc(String enName);

    List<Weapon> findByIsActivatedTrue();

    void deleteByIsActivatedFalse();
}
