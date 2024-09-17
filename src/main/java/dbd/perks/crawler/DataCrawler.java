package dbd.perks.crawler;

import dbd.perks.domain.*;
import dbd.perks.repository.*;
import dbd.perks.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DataCrawler {

    private final PlayableRepository playableRepository;
    private final PerkRepository perkRepository;
    private final WeaponRepository weaponRepository;
    private final AddonRepository addonRepository;
    private final ItemRepository itemRepository;
    private final OfferingRepository offeringRepository;

    private final CrawlerUtil crawlerUtil;
    private final ScrollCrawler scrollCrawler;
    private final EmailService emailService;

    @Async
    @Transactional
    public void runCrawlerAll() {
        Map<String, Integer> lastActivated = getActivatedDataCount();

        runKillerCrawler();
        runSurvivorCrawler();
        runOfferingCrawler();
        runCommonPerksCrawler();

        Map<String, Integer> curActivated = getActivatedDataCount();

        verifyData(lastActivated, curActivated);
    }

    @Async
    public void runKillerCrawler() {
        KillerCrawler killerCrawler = new KillerCrawler(playableRepository, perkRepository, weaponRepository, addonRepository, crawlerUtil);
        killerCrawler.runKillerCrawler();

    }

    @Async
    public void runSurvivorCrawler() {
        SurvivorCrawler survivorCrawler = new SurvivorCrawler(playableRepository, perkRepository, itemRepository, addonRepository, crawlerUtil, scrollCrawler);
        survivorCrawler.runSurvivorCrawler();
    }

    @Async
    public void runOfferingCrawler() {
        OfferingCrawler offeringCrawler = new OfferingCrawler(offeringRepository, crawlerUtil, scrollCrawler);
        offeringCrawler.runOfferingCrawler();
    }

    @Async
    public void runCommonPerksCrawler() {
        CommonPerksCrawler commonPerksCrawler = new CommonPerksCrawler(perkRepository, crawlerUtil, scrollCrawler);
        commonPerksCrawler.runCommonPerksCrawler();
    }

    public Map<String, Integer> getActivatedDataCount() {
        List<Addon> addons = addonRepository.findByIsActivatedTrue();
        List<Item> items = itemRepository.findByIsActivatedTrue();
        List<Offering> offerings = offeringRepository.findByIsActivatedTrue();
        List<Perk> perks = perkRepository.findByIsActivatedTrue();
        List<Playable> playables = playableRepository.findByIsActivatedTrue();
        List<Weapon> weapons = weaponRepository.findByIsActivatedTrue();

        Map<String, Integer> map = new HashMap<>();
        map.put("addon", addons.size());
        map.put("item", items.size());
        map.put("offering", offerings.size());
        map.put("perk", perks.size());
        map.put("playable", playables.size());
        map.put("weapon", weapons.size());

        return map;
    }

    @Transactional
    public void verifyData(Map<String, Integer> last, Map<String, Integer> cur ) {

        String emailSubject = "[크롤러 데이터 변동 알림]";

        StringBuffer emailContents = new StringBuffer();

        Iterator<String> it = last.keySet().iterator();

        while(it.hasNext()) {
            String data = it.next();
            if(!Objects.equals(last.get(data), cur.get(data))) {
                emailContents.append("[" + data + "] 이전 버전 : " + last.get(data) + "개 / 현재 버전 : " + cur.get(data) + "개 \n");
            }
        }
//            /** Playable **/
//            List<Playable> curPlayableList = playableRepository.findByVer(curVer);
//            List<Playable> lastPlayableList = playableRepository.findByVer(curVer-1);
//
//            // 이전 버전 데이터 개수와 다를 경우
//            if(curPlayableList.size() != lastPlayableList.size()) {
//                emailContents.append("[Playable] 이전 버전 : " + lastPlayableList.size() + "개 / 현재 버전 : " + curPlayableList.size() + "개 \n");
//            }
//
//            /** Addon **/
//            List<Addon> curAddonList = addonRepository.findByVer(curVer);
//            List<Addon> lastAddonList = addonRepository.findByVer(curVer-1);
//
//            // 이전 버전 데이터 개수와 다를 경우
//            if(curAddonList.size() != lastAddonList.size()) {
//                emailContents.append("[Addon] 이전 버전 : " + lastAddonList.size() + "개 / 현재 버전 : " + curAddonList.size() + "개 \n");
//            }
//
//            /** Item **/
//            List<Item> curItemList = itemRepository.findByVer(curVer);
//            List<Item> lastItemList = itemRepository.findByVer(curVer-1);
//
//            // 이전 버전 데이터 개수와 다를 경우
//            if(curItemList.size() != lastItemList.size()) {
//                emailContents.append("[Item] 이전 버전 : " + lastItemList.size() + "개 / 현재 버전 : " + curItemList.size() + "개 \n");
//            }
//
//            /** Offering **/
//            List<Offering> curOfferingList = offeringRepository.findByVer(curVer);
//            List<Offering> lastOfferingList = offeringRepository.findByVer(curVer-1);
//
//            // 이전 버전 데이터 개수와 다를 경우
//            if(curPlayableList.size() != lastPlayableList.size()) {
//                emailContents.append("[Offering] 이전 버전 : " + lastOfferingList.size() + "개 / 현재 버전 : " + curOfferingList.size() + "개 \n");
//            }
//
//            /** Perk **/
//            List<Perk> curPerkList = perkRepository.findByVer(curVer);
//            List<Perk> lastPerkList = perkRepository.findByVer(curVer-1);
//
//            // 이전 버전 데이터 개수와 다를 경우
//            if(curPerkList.size() != lastPerkList.size()) {
//                emailContents.append("[Perk] 이전 버전 : " + lastPerkList.size() + "개 / 현재 버전 : " + curPerkList.size() + "개 \n");
//            }
//
//            /** Weapon **/
//            List<Weapon> curWeaponList = weaponRepository.findByVer(curVer);
//            List<Weapon> lastWeaponList = weaponRepository.findByVer(curVer-1);
//
//            // 이전 버전 데이터 개수와 다를 경우
//            if(curWeaponList.size() != lastWeaponList.size()) {
//                emailContents.append("[Weapon] 이전 버전 : " + lastWeaponList.size() + "개 / 현재 버전 : " + curWeaponList.size() + "개 \n");
//            }

        if(!emailContents.isEmpty()) {
            emailService.sendEmail(emailSubject, emailContents.toString());
        } else {
            playableRepository.deleteByIsActivatedFalse();
            addonRepository.deleteByIsActivatedFalse();
            itemRepository.deleteByIsActivatedFalse();
            offeringRepository.deleteByIsActivatedFalse();
            perkRepository.deleteByIsActivatedFalse();
            weaponRepository.deleteByIsActivatedFalse();
        }
    }
}
