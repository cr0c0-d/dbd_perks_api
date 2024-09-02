package dbd.perks.repository;

import dbd.perks.domain.Offering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfferingRepository extends JpaRepository<Offering, Long> {
    List<Offering> findByRoleIn(List<String> roles);
}
