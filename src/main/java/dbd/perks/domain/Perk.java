package dbd.perks.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class Perk {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String perk_tag;

    @Column
    private String role;

    @Column
    private String name;

    @Column
    private String en_name;

    @Column
    private String name_tag;

    @Column
    private String perk_name;

    @Column
    private String description;

    @Column
    private String lang;

    @Column
    private String level;

    @Column
    private String DLC;

    @Column
    private String icon;

    @Column
    private String tags;

    @Column
    private String own;

    @CreatedDate
    @Column
    private LocalDateTime created_at;
}
