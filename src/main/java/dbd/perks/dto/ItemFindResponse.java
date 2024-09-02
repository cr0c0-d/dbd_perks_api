package dbd.perks.dto;

import dbd.perks.domain.Item;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemFindResponse {
    private String name;
    private String classification;
    private String description;
    private String img;

    public ItemFindResponse(Item item) {
        this.name = item.getName();
        this.classification = item.getTypeEnName();
        this.description = item.getDescription();
        this.img = item.getImg();
    }
}
