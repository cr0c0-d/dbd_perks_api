package dbd.perks.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Playable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String en_name;

    @Column
    private String name;

    @Column
    private String name_tag;

    @Column
    private String nickname;

    @CreatedDate
    @Column
    private LocalDateTime created_at;

    @Builder
    public Playable(String en_name, String name, String name_tag, String nickname) {
        this.en_name = en_name;
        this.name = name;
        this.name_tag = name_tag;
        this.nickname = nickname;
    }
}
