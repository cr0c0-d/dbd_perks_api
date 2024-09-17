package dbd.perks.repository;

import dbd.perks.domain.Addon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddonRepository extends JpaRepository<Addon, Long> {

    List<Addon> findAllByKillerIdIsNullAndIsActivatedTrue();
    List<Addon> findAllByKillerIdIsNotNullAndIsActivatedTrue();

    Optional<Addon> findFirstByEnNameOrderByCreatedAtDesc(String enName);

    List<Addon> findByIsActivatedTrue();

    void deleteByIsActivatedFalse();
}
