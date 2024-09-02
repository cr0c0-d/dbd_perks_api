package dbd.perks.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class WholeDataFindResponse {

    private SurvivorFindResponse survivor;
    private KillerFindResponse killer;

    public WholeDataFindResponse(SurvivorFindResponse survivor, KillerFindResponse killer) {
        this.survivor = survivor;
        this.killer = killer;
    }
}
