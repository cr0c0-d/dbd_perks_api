package dbd.perks.repository;

import dbd.perks.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findFirstByEnNameOrderByCreatedAtDesc(String enName);

    List<Item> findByIsActivatedTrue();

    void deleteByIsActivatedFalse();
}
