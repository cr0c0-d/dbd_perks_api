package dbd.perks.repository;

import dbd.perks.domain.Addon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddonRepository extends JpaRepository<Addon, Long> {

    List<Addon> findAllByKillerIdIsNull();
    List<Addon> findAllByKillerIdIsNotNull();

    List<Addon> findByVer(Long ver);
}
