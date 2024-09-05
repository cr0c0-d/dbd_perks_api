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
public class Weapon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String name;

    @Column(name = "en_name")
    private String enName;

    @Column
    private String img;

    @Column(name = "killer_id")
    private Long killerId;

    @Column
    private Long ver;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Weapon(String name, String enName, String img, Long killerId, Long ver) {
        this.name = name;
        this.enName = enName;
        this.img = img;
        this.killerId = killerId;
        this.ver = ver;
    }
}
