package dbd.perks.crawler;

import dbd.perks.domain.*;
import dbd.perks.repository.*;
import dbd.perks.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataCrawler {

    private final PlayableRepository playableRepository;
    private final PerkRepository perkRepository;
    private final WeaponRepository weaponRepository;
    private final AddonRepository addonRepository;
    private final ItemRepository itemRepository;
    private final OfferingRepository offeringRepository;
    private final VerRepository verRepository;

    private final CrawlerUtil crawlerUtil;
    private final ScrollCrawler scrollCrawler;
    private final EmailService emailService;

    public void runCrawlerAll() {
        Ver ver = verRepository.save(new Ver());

        runKillerCrawler(ver.getVer());
        runSurvivorCrawler(ver.getVer());
        runOfferingCrawler(ver.getVer());
        runCommonPerksCrawler(ver.getVer());

//        verifyData();
    }

    public void runKillerCrawler(Long ver) {
        KillerCrawler killerCrawler = new KillerCrawler(playableRepository, perkRepository, weaponRepository, addonRepository, crawlerUtil);
        killerCrawler.runKillerCrawler(ver);

    }

    public void runSurvivorCrawler(Long ver) {
        SurvivorCrawler survivorCrawler = new SurvivorCrawler(playableRepository, perkRepository, itemRepository, addonRepository, crawlerUtil, scrollCrawler);
        survivorCrawler.runSurvivorCrawler(ver);
    }

    public void runOfferingCrawler(Long ver) {
        OfferingCrawler offeringCrawler = new OfferingCrawler(offeringRepository, crawlerUtil, scrollCrawler);
        offeringCrawler.runOfferingCrawler(ver);
    }

    public void runCommonPerksCrawler(Long ver) {
        CommonPerksCrawler commonPerksCrawler = new CommonPerksCrawler(perkRepository, crawlerUtil, scrollCrawler);
        commonPerksCrawler.runCommonPerksCrawler(ver);
    }

//    public void verifyData() {
//        // 현재 버전 객체
//        Ver curVersion = verRepository.findFirstByOrderByVerDesc().get();
//
//        Long curVer = curVersion.getVer();
//
//        String emailSubject = "[크롤러 데이터 알림] " + curVersion.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " / 버전 " + curVer;
//
//        StringBuffer emailContents = new StringBuffer();
//
//        if(curVer != 1) {
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
//        }
//
//        if(!emailContents.isEmpty()) {
//            emailService.sendEmail(emailSubject, emailContents.toString());
//        }
//    }
}
