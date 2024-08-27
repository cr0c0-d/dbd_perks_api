package dbd.perks.crawler;

import dbd.perks.domain.Perk;
import dbd.perks.domain.Playable;
import dbd.perks.repository.AddonRepository;
import dbd.perks.repository.PerkRepository;
import dbd.perks.repository.PlayableRepository;
import dbd.perks.repository.WeaponRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SurvivorCrawler {

    private final PlayableRepository playableRepository;
    private final PerkRepository perkRepository;

    private final CrawlerUtil crawlerUtil;

    /**
     * 나무위키 도메인
     */
    private String namuWikiDomain = "https://namu.wiki";

    /**
     * 생존자 문서 목록
     */
    private String survivorDocUrlOri = "https://namu.wiki/w/DEAD%20BY%20DAYLIGHT/%EC%83%9D%EC%A1%B4%EC%9E%90/%EC%98%A4%EB%A6%AC%EC%A7%80%EB%84%90%20%EC%BA%90%EB%A6%AD%ED%84%B0";
    private String survivorDocUrlLic = "https://namu.wiki/w/DEAD%20BY%20DAYLIGHT/%EC%83%9D%EC%A1%B4%EC%9E%90/%EB%9D%BC%EC%9D%B4%EC%84%A0%EC%8A%A4%20%EC%BA%90%EB%A6%AD%ED%84%B0";

    /**
     * 생존자 크롤러 실행
     */
    public void runSurvivorCrawler() {
        getSurvivorDocument();
    }

    /**
     * 생존자 문서에 접근해
     */
    public void getSurvivorDocument() {
        try {

            // Jsoup 연결 - 생존자 오리지널
            Document documentOri = Jsoup.connect(survivorDocUrlOri).get();

            getSurvivorData(documentOri);

            // Jsoup 연결 - 생존자 라이센스
            Document documentLic = Jsoup.connect(survivorDocUrlLic).get();

            getSurvivorData(documentLic);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void getSurvivorData(Document document) {

        Elements tables = document.select("table.AVEibs0x");

        List<Playable> playableList = new ArrayList<>();

        Playable player = null;
        List<Perk> perkList = new ArrayList<>();

        for(Element table : tables) {
            Elements survInfoEl = table.select("div.Fm-HYseR div");

            if(!survInfoEl.isEmpty() && table.text().contains("신음 소리")) {
                // 생존자 기본 정보 table일 경우
                player = playableRepository.save(getSurvivorName(table));

            } else if(table.text().contains("기술")){
                // 생존자 기술 정보 table일 경우
                perkList.add(getSurvivorPerks(table, player));

                // 기술정보 3개 수집 후 현재 player 및 Perk 리스트 리셋
                if(perkList.size() == 3) {
                    player = null;

                    perkRepository.saveAll(perkList);
                    perkList = new ArrayList<>();
                }

            }
        }
    }

    public Playable getSurvivorName(Element table) {
        Playable playable = Playable.builder()
                .role("survivor")
                .build();

        Elements nameSpans = table.select("tbody tr td div.Fm-HYseR div span strong span.jrW0Zn5O");
        for(Element nameSpan : nameSpans) {
            if(nameSpan.childrenSize() > 1) {
                playable.setEnName(nameSpan.child(0).ownText());
                playable.setName(nameSpan.child(nameSpan.childrenSize() - 1).ownText());
            }
        }

        return playable;
    }

    public Perk getSurvivorPerks(Element table, Playable player) {
        Perk perk = Perk.builder()
                .role(player.getRole())
                .playableName(player.getName())
                .playableEnName(player.getEnName())
                .build();

        String imgSrc = table.select("noscript img.pSe7sj7a").attr("src");

        Elements nameElement = table.select("tr:nth-of-type(2) td div.Fm-HYseR strong span");

        String name = nameElement.get(0).ownText();
        String en_name = nameElement.get(1).ownText();

        Element descriptionSpan = table.select("tr td[rowspan='2'] div.Fm-HYseR span").get(0);

        String description = descriptionSpan.html();

        perk.setImg(namuWikiDomain+imgSrc);
        perk.setName(name);
        perk.setEnName(en_name);
        perk.setDescription(description.replaceAll("\n", " "));

        return perk;
    }


}
