package dbd.perks.service;

import dbd.perks.domain.*;
import dbd.perks.dto.*;
import dbd.perks.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataService {

    private final AddonRepository addonRepository;
    private final ItemRepository itemRepository;
    private final OfferingRepository offeringRepository;
    private final PerkRepository perkRepository;
    private final PlayableRepository playableRepository;
    private final WeaponRepository weaponRepository;

    public WholeDataFindResponse getData() {
        return new WholeDataFindResponse(getSurvivorData(), getKillerData());
    }

    public SurvivorFindResponse getSurvivorData() {
        /***************** 생존자 *****************/

        // 애드온
        List<Addon> addonList = addonRepository.findAllByKillerIdIsNull();
        List<AddonSurvivorFindResponse> addonSurvivorFindResponseList = addonList.stream().map(AddonSurvivorFindResponse::new).toList();

        // 아이템
        List<Item> itemList = itemRepository.findAll();
        List<ItemFindResponse> itemFindResponseList = itemList.stream().map(ItemFindResponse::new).toList();

        // 오퍼링
        List<Offering> offeringList = offeringRepository.findByRoleIn(Arrays.asList("survivor", "common"));
        List<OfferingFindResponse> offeringFindResponseList = offeringList.stream().map(OfferingFindResponse::new).toList();

        // 퍽
        List<Perk> perkList = perkRepository.findAll();
        List<PerkFindResponse> perkFindResponseList = perkList.stream().filter(perk -> playableRepository.findById(perk.getPlayableId()).get().getRole().equals("survivor")).map(PerkFindResponse::new).toList();

        // 캐릭터
        List<Playable> playableList = playableRepository.findAllByRole("survivor");
        List<PlayableFindResponse> playableFindResponseList = playableList.stream().map(PlayableFindResponse::new).toList();

        return new SurvivorFindResponse(playableFindResponseList, perkFindResponseList, itemFindResponseList, addonSurvivorFindResponseList, offeringFindResponseList);
    }

    public KillerFindResponse getKillerData() {
        /***************** 살인마 *****************/

        // 애드온
        List<Addon> addonList = addonRepository.findAllByKillerIdIsNotNull();
        List<AddonKillerFindResponse> addonKillerFindResponseList = addonList.stream().map(addon -> {
            return new AddonKillerFindResponse(addon, weaponRepository.findByKillerId(addon.getKillerId()));
        }).toList();

        // 무기
        List<Weapon> weaponList = weaponRepository.findAll();
        List<WeaponFindResponse> weaponFindResponseList = weaponList.stream().map(weapon -> new WeaponFindResponse(weapon, playableRepository.findById(weapon.getKillerId()).get())).toList();

        // 오퍼링
        List<Offering> offeringList = offeringRepository.findByRoleIn(Arrays.asList("killer", "common"));
        List<OfferingFindResponse> offeringFindResponseList = offeringList.stream().map(OfferingFindResponse::new).toList();

        // 퍽
        List<Perk> perkList = perkRepository.findAll();
        List<PerkFindResponse> perkFindResponseList = perkList.stream().filter(perk -> playableRepository.findById(perk.getPlayableId()).get().getRole().equals("killer")).map(PerkFindResponse::new).toList();

        // 캐릭터
        List<Playable> playableList = playableRepository.findAllByRole("killer");
        List<PlayableFindResponse> playableFindResponseList = playableList.stream().map(PlayableFindResponse::new).toList();

        return new KillerFindResponse(playableFindResponseList, perkFindResponseList, weaponFindResponseList, addonKillerFindResponseList, offeringFindResponseList);
    }
}
