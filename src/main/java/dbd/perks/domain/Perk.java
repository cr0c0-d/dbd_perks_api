package dbd.perks.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Blob;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
public class Perk {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String role;

    @Column
    private String name;

    @Column(name = "en_name")
    private String enName;

    @Column(name = "playable_name")
    private String playableName;

    @Column(name = "playable_en_name")
    private String playableEnName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String dlc;

    @Column
    private String img;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Perk(String role, String name, String enName, String playableName, String playableEnName, String description, String img) {
        this.role = role;
        this.name = name;
        this.enName = enName;
        this.playableName = playableName;
        this.playableEnName = playableEnName;
        this.description = description;
        this.img = img;
    }
}
