package dbd.perks.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Offering {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String en_name;

    @Column
    private String name;

    @Column
    private String level;

    @Column
    private String description;

    @Column
    private String icon;

    @CreatedDate
    @Column
    private LocalDateTime created_at;
}
