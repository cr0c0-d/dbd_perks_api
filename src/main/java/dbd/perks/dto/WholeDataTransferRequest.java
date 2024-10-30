package dbd.perks.dto;

import dbd.perks.domain.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class WholeDataTransferRequest {
    private int count;
    private List<Addon> addonList;
    private List<CrawledDocument> crawledDocumentList;
    private List<Item> itemList;
    private List<Offering> offeringList;
    private List<Perk> perkList;
    private List<Playable> playableList;
    private List<Weapon> weaponList;
}
