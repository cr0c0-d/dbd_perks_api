package dbd.perks.repository;

import dbd.perks.domain.Offering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OfferingRepository extends JpaRepository<Offering, Long> {
    List<Offering> findByIsActivatedTrueAndRoleIn(List<String> roles);

    Optional<Offering> findFirstByEnNameOrderByCreatedAtDesc(String enName);

    List<Offering> findByIsActivatedTrue();
}
