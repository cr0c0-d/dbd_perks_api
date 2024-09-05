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

    public void runCrawlerAll() {
        runKillerCrawler();
        runSurvivorCrawler();
        runOfferingCrawler();
        runCommonPerksCrawler();
        Ver ver = verRepository.save(new Ver());

        runKillerCrawler(ver.getVer());
        runSurvivorCrawler(ver.getVer());
        runOfferingCrawler(ver.getVer());
        runCommonPerksCrawler(ver.getVer());
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
}
