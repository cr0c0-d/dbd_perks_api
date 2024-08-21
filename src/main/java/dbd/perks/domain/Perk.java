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

    @Column
    private String en_name;

    @Column
    private String playable_name;

    @Column
    private String playable_en_name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String dlc;

    @Column
    private String img;

    @CreatedDate
    @Column
    private LocalDateTime created_at;

    @Builder
    public Perk(String role, String name, String en_name, String playable_name, String playable_en_name, String description, String img) {
        this.role = role;
        this.name = name;
        this.en_name = en_name;
        this.playable_name = playable_name;
        this.playable_en_name = playable_en_name;
        this.description = description;
        this.img = img;
    }
}
