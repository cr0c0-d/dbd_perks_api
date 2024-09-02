package dbd.perks.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class SurvivorFindResponse {
    List<PlayableFindResponse> characters;
    List<PerkFindResponse> perks;
    List<ItemFindResponse> items;
    List<AddonSurvivorFindResponse> addons;
    List<OfferingFindResponse> offerings;

    public SurvivorFindResponse(List<PlayableFindResponse> characters, List<PerkFindResponse> perks, List<ItemFindResponse> items, List<AddonSurvivorFindResponse> addons, List<OfferingFindResponse> offerings) {
        this.characters = characters;
        this.perks = perks;
        this.items = items;
        this.addons = addons;
        this.offerings = offerings;
    }
}
