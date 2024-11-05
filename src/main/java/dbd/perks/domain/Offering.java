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
public class Offering implements Data {

    @Id
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

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String img;

    @Column(name = "is_activated", columnDefinition = "Boolean")
    private Boolean isActivated;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Builder
    public Offering(String name, String enName, String level, String role, String description, String img, Boolean isActivated) {
        this.name = name;
        this.enName = enName;
        this.level = level;
        this.role = role;
        this.description = description;
        this.img = img;
        this.isActivated = isActivated;
    }

    @Override
    public Boolean equals(Data data) {
        Offering offering = null;
        if(data instanceof Offering) {
            offering = (Offering) data;

            try {

                return this.name.equals(offering.getName())
                        && this.enName.equals(offering.getEnName())
                        && this.role.equals(offering.getRole())
                        && this.level.equals(offering.getLevel())
                        && this.description.equals(offering.getDescription())
                        && this.img.equals(offering.getImg());

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
                && this.level != null
                && this.role != null
                && this.description != null
                && this.img != null;
    }
}
