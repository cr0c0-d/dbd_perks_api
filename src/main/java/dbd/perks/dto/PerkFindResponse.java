package dbd.perks.dto;

import dbd.perks.domain.Perk;
import dbd.perks.domain.Playable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PerkFindResponse {

    private String character;
    private String name;
    private String description;
    private String img;

    public PerkFindResponse(Perk perk, Playable survivor) {
        this.character = survivor == null ? "common" : survivor.getEnName();
        this.name = perk.getName();
        this.description = perk.getDescription();
        this.img = perk.getImg();
    }
}
