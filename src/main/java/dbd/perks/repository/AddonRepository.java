package dbd.perks.repository;

import dbd.perks.domain.Addon;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddonRepository extends JpaRepository<Addon, Long> {

}
