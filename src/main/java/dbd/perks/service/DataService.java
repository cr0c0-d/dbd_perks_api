package dbd.perks.service;

import dbd.perks.domain.*;
import dbd.perks.dto.*;
import dbd.perks.repository.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataService {

    private final CrawledDocumentRepository crawledDocumentRepository;
    @Value("${jasypt.encryptor.password}")
    private String password;

    private static WholeDataFindResponse wholeDataFindResponse = null;

    private final AddonRepository addonRepository;
    private final ItemRepository itemRepository;
    private final OfferingRepository offeringRepository;
    private final PerkRepository perkRepository;
    private final PlayableRepository playableRepository;
    private final WeaponRepository weaponRepository;

    public WholeDataFindResponse getData() {
        if(wholeDataFindResponse == null) {
            setData();
        }
        return wholeDataFindResponse;
    }

    @PostConstruct
    public void setData() {
        wholeDataFindResponse = new WholeDataFindResponse(getSurvivorData(), getKillerData());
    }

    public SurvivorFindResponse getSurvivorData() {
        /***************** 생존자 *****************/

        // 애드온
        List<Addon> addonList = addonRepository.findAllByKillerIdIsNullAndIsActivatedTrue();
        List<AddonSurvivorFindResponse> addonSurvivorFindResponseList = addonList.stream().map(AddonSurvivorFindResponse::new).toList();

        // 아이템
        List<Item> itemList = itemRepository.findByIsActivatedTrue();
        List<ItemFindResponse> itemFindResponseList = itemList.stream().map(ItemFindResponse::new).toList();

        // 오퍼링
        List<Offering> offeringList = offeringRepository.findByIsActivatedTrueAndRoleIn(Arrays.asList("survivor", "common"));
        List<OfferingFindResponse> offeringFindResponseList = offeringList.stream().map(OfferingFindResponse::new).toList();

        // 퍽
        List<Perk> perkList = perkRepository.findByIsActivatedTrueAndRole("survivor");
        List<PerkFindResponse> perkFindResponseList = perkList.stream().map(perk -> new PerkFindResponse(perk, perk.getPlayableId() == null ? null : playableRepository.findById(perk.getPlayableId()).get())).toList();

        // 캐릭터
        List<Playable> playableList = playableRepository.findByIsActivatedTrueAndRole("survivor");
        List<PlayableFindResponse> playableFindResponseList = playableList.stream().map(PlayableFindResponse::new).toList();

        return new SurvivorFindResponse(playableFindResponseList, perkFindResponseList, itemFindResponseList, addonSurvivorFindResponseList, offeringFindResponseList);
    }

    public KillerFindResponse getKillerData() {
        /***************** 살인마 *****************/

        // 애드온
        List<Addon> addonList = addonRepository.findAllByKillerIdIsNotNullAndIsActivatedTrue();
        List<AddonKillerFindResponse> addonKillerFindResponseList = addonList.stream().map(addon -> {
            return new AddonKillerFindResponse(addon, weaponRepository.findByKillerId(addon.getKillerId()));
        }).toList();

        // 무기
        List<Weapon> weaponList = weaponRepository.findByIsActivatedTrue();
        List<WeaponFindResponse> weaponFindResponseList = weaponList.stream().map(weapon -> new WeaponFindResponse(weapon, playableRepository.findById(weapon.getKillerId()).get())).toList();

        // 오퍼링
        List<Offering> offeringList = offeringRepository.findByIsActivatedTrueAndRoleIn(Arrays.asList("killer", "common"));
        List<OfferingFindResponse> offeringFindResponseList = offeringList.stream().map(OfferingFindResponse::new).toList();

        // 퍽
        List<Perk> perkList = perkRepository.findByIsActivatedTrueAndRole("killer");
        List<PerkFindResponse> perkFindResponseList = perkList.stream().map(perk -> new PerkFindResponse(perk, perk.getPlayableId() == null ? null : playableRepository.findById(perk.getPlayableId()).get())).toList();

        // 캐릭터
        List<Playable> playableList = playableRepository.findByIsActivatedTrueAndRole("killer");
        List<PlayableFindResponse> playableFindResponseList = playableList.stream().map(PlayableFindResponse::new).toList();

        return new KillerFindResponse(playableFindResponseList, perkFindResponseList, weaponFindResponseList, addonKillerFindResponseList, offeringFindResponseList);
    }

    public boolean dataMigration(WholeDataTransferRequest data, HttpServletRequest httpServletRequest) {
        Cookie[] cookies = httpServletRequest.getCookies();
        String pwValue = "";
        for(Cookie cookie : cookies) {
            if("pw".equals(cookie.getName())) {
                pwValue = cookie.getValue();
                break;
            }
        }
        if(!pwValue.equals(password)) {
            return false;
        } else {
            int count = 0;

            addonRepository.deleteAll();
            count += addonRepository.saveAll(data.getAddonList()).size();

            crawledDocumentRepository.deleteAll();
            count += crawledDocumentRepository.saveAll(data.getCrawledDocumentList()).size();

            itemRepository.deleteAll();
            count += itemRepository.saveAll(data.getItemList()).size();

            offeringRepository.deleteAll();
            count += offeringRepository.saveAll(data.getOfferingList()).size();

            perkRepository.deleteAll();
            count += perkRepository.saveAll(data.getPerkList()).size();

            playableRepository.deleteAll();
            count += playableRepository.saveAll(data.getPlayableList()).size();

            weaponRepository.deleteAll();
            count += weaponRepository.saveAll(data.getWeaponList()).size();

            setData();
            return count == data.getCount();
        }
    }
}
