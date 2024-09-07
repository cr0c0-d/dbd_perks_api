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
public class Playable implements Data {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String role;

    @Column(name = "en_name")
    private String enName;

    @Column
    private String name;

    @Column(name = "is_activated", columnDefinition = "Boolean")
    private Boolean isActivated;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Playable(String role, String enName, String name, Boolean isActivated) {
        this.role = role;
        this.enName = enName;
        this.name = name;
        this.isActivated = isActivated;
    }

    @Override
    public Boolean equals(Data data) {
        Playable playable = null;
        if(data instanceof Playable) {
            playable = (Playable) data;

            try {

                return this.name.equals(playable.getName())
                        && this.enName.equals(playable.getEnName())
                        && this.role.equals(playable.getRole());
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
                && this.role != null;
    }
}
