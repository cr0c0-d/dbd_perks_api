package dbd.perks.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class Ver {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long ver;

    @Column
    private String type;

    @Column(name = "doc_update_time")
    private LocalDateTime docUpdateTime;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Ver(String type, LocalDateTime docUpdateTime) {
        this.type = type;
        this.docUpdateTime = docUpdateTime;
    }

}
