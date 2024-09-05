package dbd.perks.repository;

import dbd.perks.domain.Weapon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WeaponRepository extends JpaRepository<Weapon, Long> {
    Weapon findByKillerId(Long killerId);

    List<Weapon> findByVer(Long ver);
}
