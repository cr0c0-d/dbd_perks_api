package dbd.perks.repository;

import dbd.perks.domain.CrawledDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CrawledDocumentRepository extends JpaRepository<CrawledDocument, Long> {
    Optional<CrawledDocument> findFirstByUrlOrderByVerDesc(String url);
}
