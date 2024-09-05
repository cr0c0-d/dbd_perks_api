package dbd.perks.repository;

import dbd.perks.domain.Playable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayableRepository extends JpaRepository<Playable, Long> {
    List<Playable> findByRole(String role);
    List<Playable> findByVer(Long ver);
}
