package dbd.perks.crawler;

import dbd.perks.domain.Playable;
import dbd.perks.domain.Item;
import dbd.perks.domain.Perk;
import dbd.perks.repository.PerkRepository;
import dbd.perks.repository.PlayableRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataCrawler {

    private final PlayableRepository playableRepository;
    private final PerkRepository perkRepository;

    public void runKillerCrawler() {
        KillerCrawler killerCrawler = new KillerCrawler(playableRepository, perkRepository);

        killerCrawler.runKillerCrawler();

    }
}
