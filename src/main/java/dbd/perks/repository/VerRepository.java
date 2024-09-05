package dbd.perks.repository;

import dbd.perks.domain.Ver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerRepository extends JpaRepository<Ver, Long> {
    Optional<Ver> findFirstByTypeOrderByVerDesc(String type);
}
