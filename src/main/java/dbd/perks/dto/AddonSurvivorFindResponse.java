package dbd.perks.dto;

import dbd.perks.domain.Addon;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddonSurvivorFindResponse {

    private String name;
    private String classification;
    private String description;
    private String img;

    public AddonSurvivorFindResponse(Addon addon) {
      this.name = addon.getName();
      this.classification = addon.getTypeEnName();
      this.description = addon.getDescription();
      this.img = addon.getImg();
    }
}
