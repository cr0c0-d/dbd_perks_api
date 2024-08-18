package dbd.perks.repository;

import dbd.perks.domain.Playable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayableRepository extends JpaRepository<Playable, Long> {

}
