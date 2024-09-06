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
@NoArgsConstructor
@AllArgsConstructor
public class Perk implements Data  {

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

    // null인 경우 공용퍽
    @Column(name = "playable_id")
    private Long playableId;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String img;

    @Column(name = "is_activated", columnDefinition = "Boolean")
    private Boolean isActivated;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Perk(String role, String name, String enName, Long playableId, String description, String img, Boolean isActivated) {
        this.role = role;
        this.name = name;
        this.enName = enName;
        this.playableId = playableId;
        this.description = description;
        this.img = img;
        this.isActivated = isActivated;
    }

    @Override
    public Boolean equals(Data data) {
        Perk perk = null;
        if(data instanceof Perk) {
            perk = (Perk) data;

            return this.name.equals(perk.getName())
                    && this.enName.equals(perk.getEnName())
                    && this.role.equals(perk.getRole())
                    && ((this.playableId == null && perk.getPlayableId() == null) || this.playableId.equals(perk.getPlayableId()))
                    && this.description.equals(perk.getDescription())
                    && this.img.equals(perk.getImg());
        } else {
            return false;
        }
    }

    @Override
    public void deactivate() {
        this.isActivated = false;
    }
}
