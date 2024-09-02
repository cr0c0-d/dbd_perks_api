package dbd.perks.dto;

import dbd.perks.domain.Offering;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OfferingFindResponse {

    private String name;
    private String description;
    private String img;

    public OfferingFindResponse(Offering offering) {
      this.name = offering.getName();
      this.description = offering.getDescription();
      this.img = offering.getImg();
    }
}
