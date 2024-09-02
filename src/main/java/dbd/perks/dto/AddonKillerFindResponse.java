package dbd.perks.dto;

import dbd.perks.domain.Addon;
import dbd.perks.domain.Weapon;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AddonKillerFindResponse {

    private String name;
    private String weapon;
    private String description;
    private String img;

    public AddonKillerFindResponse(Addon addon, Weapon weapon) {
      this.name = addon.getName();
      this.weapon = weapon.getEnName();
      this.description = addon.getDescription();
      this.img = addon.getImg();
    }
}
