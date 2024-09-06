package dbd.perks.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class CrawledDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String url;

    @Column
    private Long ver;

    @Column(name = "last_modified_time")
    private LocalDateTime lastModifiedTime;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public CrawledDocument(String url, Long ver, LocalDateTime lastModifiedTime) {
        this.url = url;
        this.ver = ver;
        this.lastModifiedTime = lastModifiedTime;
    }
}
