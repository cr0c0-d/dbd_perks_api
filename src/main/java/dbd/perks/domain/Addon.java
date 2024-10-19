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
public class Addon implements Data {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private Long id;

    @Column
    private String name;

    @Column(name = "en_name")
    private String enName;

    @Column
    private String typeName;

    @Column
    private String typeEnName;

    @Column
    private String level;

    @Column
    private Long killerId;

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
    public Addon(String name, String enName, String typeName, String typeEnName, String level, Long killerId, String description, String img, Boolean isActivated) {
        this.name = name;
        this.enName = enName;
        this.typeName = typeName;
        this.typeEnName = typeEnName;
        this.level = level;
        this.killerId = killerId;
        this.description = description;
        this.img = img;
        this.isActivated = isActivated;
    }

    @Override
    public Boolean equals(Data data) {
        Addon addon = null;
        if(data instanceof Addon) {
            addon = (Addon) data;
        } else {
            return false;
        }
        try {
            return this.name.equals(addon.getName())
                    && this.enName.equals(addon.getEnName())
                    && ((this.typeName == null && addon.getTypeName() == null) || this.typeName.equals(addon.getTypeName()))
                    && ((this.typeEnName == null && addon.getTypeEnName() == null) || this.typeEnName.equals(addon.getTypeName()))
                    && this.level.equals(addon.getLevel())
                    && ((this.killerId == null && addon.getKillerId() == null) || this.killerId.equals(addon.getKillerId()))
                    && this.description.equals(addon.getDescription())
                    && this.img.equals(addon.getImg());
        } catch (Exception e) {
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
                && ((this.typeName != null && this.typeEnName != null) || this.killerId != null)
                && this.description != null
                && this.img != null;
    }
}
