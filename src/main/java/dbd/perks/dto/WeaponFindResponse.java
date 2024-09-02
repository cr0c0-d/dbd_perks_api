package dbd.perks.dto;

import dbd.perks.domain.Playable;
import dbd.perks.domain.Weapon;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WeaponFindResponse {

    private String name;
    private String en_name;
    private String character;
    private String description;
    private String img;

    public WeaponFindResponse(Weapon weapon, Playable killer) {
        this.name = weapon.getName();
        this.en_name = weapon.getEnName();
        this.character = killer.getEnName();
        this.description = "";
        this.img = weapon.getImg();
    }
}
