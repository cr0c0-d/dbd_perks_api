package dbd.perks.repository;

import dbd.perks.domain.Playable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayableRepository extends JpaRepository<Playable, Long> {
    List<Playable> findByIsActivatedTrueAndRole(String role);

    Optional<Playable> findFirstByEnNameOrderByCreatedAtDesc(String enName);

    List<Playable> findByIsActivatedTrue();
}
