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
public class Addon {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String name;

    @Column(name = "en_name")
    private String enName;

    @Column
    private String typeName;

    @Column
    private String typeEnName;

    @Column
    private String level;

    @Column
    private Long killerId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String img;

    @Column
    private Long ver;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Addon(String name, String enName, String typeName, String typeEnName, String level, Long killerId, String description, String img, Long ver) {
        this.name = name;
        this.enName = enName;
        this.typeName = typeName;
        this.typeEnName = typeEnName;
        this.level = level;
        this.killerId = killerId;
        this.description = description;
        this.img = img;
        this.ver = ver;
    }
}
