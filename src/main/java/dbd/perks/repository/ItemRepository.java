package dbd.perks.repository;

import dbd.perks.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByVer(Long ver);
}
