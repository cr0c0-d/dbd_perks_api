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
public class Weapon implements Data {

    @Id
    @Column
    private Long id;

    @Column
    private String name;

    @Column(name = "en_name")
    private String enName;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String img;

    @Column(name = "killer_id")
    private Long killerId;

    @Column(name = "is_activated", columnDefinition = "Boolean")
    private Boolean isActivated;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Weapon(String name, String enName, String img, Long killerId, Boolean isActivated) {
        this.name = name;
        this.enName = enName;
        this.img = img;
        this.killerId = killerId;
        this.isActivated = isActivated;
    }

    @Override
    public Boolean equals(Data data) {
        Weapon weapon = null;
        if(data instanceof Weapon) {
            weapon = (Weapon) data;

            try {
                return this.name.equals(weapon.getName())
                        && this.enName.equals(weapon.getEnName())
                        && this.killerId.equals(weapon.getKillerId())
                        && this.img.equals(weapon.getImg());

            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void deactivate() {
        this.isActivated = false;
    }

    @Override
    public Boolean validate() {
        return this.name != null
                && this.enName != null
                && this.killerId != null
                && this.img != null;
    }
}
