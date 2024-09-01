package dbd.perks.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Offering {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String name;

    @Column(name="en_name")
    private String enName;

    @Column
    private String level;

    @Column
    private String role;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String img;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Offering(String name, String enName, String level, String role, String description, String img) {
        this.name = name;
        this.enName = enName;
        this.level = level;
        this.role = role;
        this.description = description;
        this.img = img;
    }
}
