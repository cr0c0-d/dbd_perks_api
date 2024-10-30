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
public class Item implements Data {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private String name;

    @Column(name="en_name")
    private String enName;

    @Column
    private String level;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String img;

    @Column
    private String typeName;

    @Column
    private String typeEnName;

    @Column(name = "is_activated", columnDefinition = "Boolean")
    private Boolean isActivated;

    @CreatedDate
    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Builder
    public Item(String name, String enName, String level, String description, String img, String typeName, String typeEnName, Boolean isActivated) {
        this.name = name;
        this.enName = enName;
        this.level = level;
        this.description = description;
        this.img = img;
        this.typeName = typeName;
        this.typeEnName = typeEnName;
        this.isActivated = isActivated;
    }

    @Override
    public Boolean equals(Data data) {
        Item item = null;
        if(data instanceof Item) {
            item = (Item) data;

            try {
                return this.name.equals(item.getName())
                        && this.enName.equals(item.getEnName())
                        && this.typeName.equals(item.getTypeName())
                        && this.typeEnName.equals(item.getTypeName())
                        && this.level.equals(item.getLevel())
                        && this.description.equals(item.getDescription())
                        && this.img.equals(item.getImg());

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
                && this.typeName != null
                && this.typeEnName != null
                && this.description != null
                && this.img != null;
    }
}
