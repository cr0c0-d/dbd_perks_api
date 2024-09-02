package dbd.perks.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class KillerFindResponse {
    List<PlayableFindResponse> characters;
    List<PerkFindResponse> perks;
    List<WeaponFindResponse> weapons;
    List<AddonKillerFindResponse> addons;
    List<OfferingFindResponse> offerings;

    public KillerFindResponse(List<PlayableFindResponse> characters, List<PerkFindResponse> perks, List<WeaponFindResponse> weapons, List<AddonKillerFindResponse> addons, List<OfferingFindResponse> offerings) {
        this.characters = characters;
        this.perks = perks;
        this.weapons = weapons;
        this.addons = addons;
        this.offerings = offerings;
    }
}
