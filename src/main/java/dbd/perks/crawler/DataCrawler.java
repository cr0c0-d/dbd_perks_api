package dbd.perks.crawler;

import dbd.perks.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void runCrawlerAll() {
        runKillerCrawler();
        runSurvivorCrawler();
        runOfferingCrawler();
        runCommonPerksCrawler();
    }

    public void runKillerCrawler() {
        KillerCrawler killerCrawler = new KillerCrawler(playableRepository, perkRepository, weaponRepository, addonRepository, crawlerUtil);

        killerCrawler.runKillerCrawler();

    }

    public void runSurvivorCrawler() {
        SurvivorCrawler survivorCrawler = new SurvivorCrawler(playableRepository, perkRepository, itemRepository, addonRepository, crawlerUtil, scrollCrawler);
        survivorCrawler.runSurvivorCrawler();
    }

    public void runOfferingCrawler() {
        OfferingCrawler offeringCrawler = new OfferingCrawler(offeringRepository, crawlerUtil, scrollCrawler);
        offeringCrawler.runOfferingCrawler();
    }

    public void runCommonPerksCrawler() {
        CommonPerksCrawler commonPerksCrawler = new CommonPerksCrawler(perkRepository, crawlerUtil, scrollCrawler);
        commonPerksCrawler.runCommonPerksCrawler();
    }
}
