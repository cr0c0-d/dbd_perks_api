package dbd.perks.crawler;

import dbd.perks.domain.Perk;
import dbd.perks.domain.Playable;
import dbd.perks.repository.PerkRepository;
import dbd.perks.repository.PlayableRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SurvivorCrawler {

    private final PlayableRepository playableRepository;
    private final PerkRepository perkRepository;

    private final CrawlerUtil crawlerUtil;
    private final ScrollCrawler scrollCrawler;

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
        // Jsoup 연결 - 생존자 오리지널
        Document documentOri = scrollCrawler.getDocumentByScrollCrawler(survivorDocUrlOri);

        getSurvivorData(documentOri);

        // Jsoup 연결 - 생존자 라이센스
        Document documentLic = scrollCrawler.getDocumentByScrollCrawler(survivorDocUrlLic);

        getSurvivorData(documentLic);
    }

    public void getSurvivorData(Document document) {

        // 생존자 한글명 리스트
        List<String> survNames = new ArrayList<>();

        // 생존자 Playable 객체 리스트
        List<Playable> playableList = new ArrayList<>();

        // 1. 생존자 목차 정보에서 한글명 수집
        // 생존자 목차 정보
        // href가 #s-? 형식인 a 태그 선택
        List<Element> survTitleATags = document.select("div div span a").stream().filter(survTitle -> survTitle.attr("href").startsWith("#s-") && !survTitle.attr("href").contains(".")).toList();

        // 생존자 한글명 수집
        for(Element survTitleATag : survTitleATags) {
            // 생존자 이름 저장
            survNames.add(survTitleATag.parent().ownText().replace(".", "").trim());
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

            Element perkDiv = crawlerUtil.getNextElement(document, survDiv);

            while(!perkDiv.wholeText().contains("전승 기술")) {
                perkDiv = crawlerUtil.getNextElement(document, perkDiv);
            }

            perkDiv = crawlerUtil.getNextElement(document, perkDiv);
            perkList = getSurvivorPerks(perkDiv, survivor);

            perkRepository.saveAll(perkList);
        }
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

    public List<Perk> getSurvivorPerks(Element perkDiv, Playable player) {
        List<Perk> perkList = new ArrayList<>();

        Elements tables = perkDiv.select("table");

        if(tables.size() == 3) {
            for(Element table : tables) {
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

                perkList.add(perk);
            }
        } else if(tables.size() == 1) {
            tables = tables.select("td");

            for (Element perkTable : tables) {

                for (Element perkElement : perkTable.children()) {

                    Perk perk = Perk.builder()
                            .role(player.getRole())
                            .playableName(player.getName())
                            .playableEnName(player.getEnName())
                            .build();

                    Elements spans = perkElement.select("div div div div div span");
                    // 이미지 경로
                    String imgSrc = spans.select("noscript img").attr("src");

                    // 한글명
                    String name = spans.select("span strong").get(0).ownText();

                    // 영문명
                    String en_name = spans.select("span strong").get(1).ownText();

                    // 설명
                    Element descriptionSpan = perkElement.select("dl dd div span:nth-child(1)").get(0);

                    String description = descriptionSpan.html();

                    perk.setImg(namuWikiDomain+imgSrc);
                    perk.setName(name);
                    perk.setEnName(en_name);
                    perk.setDescription(description.replaceAll("\n", " "));

                    perkList.add(perk);
                }

            }
        }
        return perkList;
    }


}
