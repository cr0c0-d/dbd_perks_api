package dbd.perks.dto;

import dbd.perks.domain.Playable;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlayableFindResponse {

    private String name;
    private String en_name;

    public PlayableFindResponse(Playable playable) {
        this.name = playable.getName();
        this.en_name = playable.getEnName();
    }
}
