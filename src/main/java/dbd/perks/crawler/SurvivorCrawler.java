package dbd.perks.crawler;

import dbd.perks.domain.Perk;
import dbd.perks.domain.Playable;
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

        // 생존자 한글명 리스트
        List<String> survNames = new ArrayList<>();

        // 생존자 Playable 객체 리스트
        List<Playable> playableList = new ArrayList<>();

        // 생존자 목차 정보
        Elements survTitles = document.select("div div span");

        // 생존자 한글명 수집
        for(Element survTitle : survTitles) {
            Element num = survTitle.selectFirst("a");

            if(!num.ownText().contains(".")) {
                // 생존자 한글명인 경우
                num.remove();
                // 생존자 이름 저장
                survNames.add(survTitle.wholeText().replace(".", "").trim());
            }
        }


        for(String survName : survNames) {
            Playable survivor = Playable.builder()
                    .name(survName)
                    .role("survivor")
                    .build();

            List<Perk> perkList = new ArrayList<>();

            Element survDiv = crawlerUtil.getContentsElement(document, survName);

            if(survDiv == null) {
                continue;
            }

            Element survTable = survDiv.selectFirst("table tbody tr");

            Element survNameSpan = survTable.selectFirst("strong span");
            survivor.setEnName(survNameSpan.child(0).ownText());

            playableList.add(playableRepository.save(survivor));

            Element perkDiv = crawlerUtil.getNextElement(document, crawlerUtil.getNextElement(document, survDiv));

            Elements perkTables = perkDiv.select("table");

            for(Element table : perkTables) {
                perkList.add(getSurvivorPerks(table, survivor));
            }

            perkRepository.saveAll(perkList);
        }


//
//        Elements perkDivs = crawlerUtil.getContentsElements(document, "전승 기술");
//
//        for(int i = 0; i < perkDivs.size(); i++) {
//            Element perkDiv = perkDivs.get(i);
//            Playable survivor = playableList.get(i);
//            List<Perk> perkList = new ArrayList<>();
//
//            Elements tables = perkDiv.select("table");
//
//            for(Element table : tables) {
//                perkList.add(getSurvivorPerks(table, survivor));
//            }
//
//            perkRepository.saveAll(perkList);
//        }



//        Elements tables = document.select("table.AVEibs0x");



//        Playable player = null;
//        List<Perk> perkList = new ArrayList<>();
//
//        for(Element table : tables) {
//            Elements survInfoEl = table.select("div.Fm-HYseR div");
//
//            if(!survInfoEl.isEmpty() && table.text().contains("신음 소리")) {
//                // 생존자 기본 정보 table일 경우
//                player = playableRepository.save(getSurvivorName(table));
//
//            } else if(table.text().contains("기술")){
//                // 생존자 기술 정보 table일 경우
//                perkList.add(getSurvivorPerks(table, player));
//
//                // 기술정보 3개 수집 후 현재 player 및 Perk 리스트 리셋
//                if(perkList.size() == 3) {
//                    player = null;
//
//                    perkRepository.saveAll(perkList);
//                    perkList = new ArrayList<>();
//                }
//
//            }
//        }
    }

    public Playable getSurvivorName(Element table) {
        Playable playable = Playable.builder()
                .role("survivor")
                .build();

        Elements nameSpans = table.select("tbody tr td div div span strong span");
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

        String imgSrc = table.select("noscript img").attr("src");

        Elements nameElement = table.select("tr:nth-of-type(2) td div strong span");

        String name = nameElement.get(0).ownText();
        String en_name = nameElement.get(1).ownText();

        Element descriptionSpan = table.select("tr td[rowspan='2'] div span").get(0);

        String description = descriptionSpan.html();

        perk.setImg(namuWikiDomain+imgSrc);
        perk.setName(name);
        perk.setEnName(en_name);
        perk.setDescription(description.replaceAll("\n", " "));

        return perk;
    }


}
