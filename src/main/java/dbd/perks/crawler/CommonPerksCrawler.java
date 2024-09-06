package dbd.perks.crawler;

import dbd.perks.domain.Perk;
import dbd.perks.repository.PerkRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommonPerksCrawler {

    private final PerkRepository perkRepository;

    private final CrawlerUtil crawlerUtil;
    private final ScrollCrawler scrollCrawler;


    /**
     * 나무위키 도메인
     */
    private String namuWikiDomain = "https://namu.wiki";

    /**
     * 기술 문서
     */
    private String perkUrl = "https://namu.wiki/w/DEAD%20BY%20DAYLIGHT/%EA%B8%B0%EC%88%A0";

    private Long ver;

    /**
     * 공용 기술 크롤러 실행
     */
    public void runCommonPerksCrawler() {
        getCommonPerksDocument();
    }

    /**
     * 기술 문서를 불러와 각 데이터를 수집하는 함수
     */
    public void getCommonPerksDocument() {
        Document document = scrollCrawler.getDocumentByScrollCrawler(perkUrl);
        document = crawlerUtil.removeAnnotation(document);

        getCommonPerks(document, "killer");
        getCommonPerks(document, "survivor");

    }

    public void getCommonPerks(Document document, String role) {
        Element curDiv = crawlerUtil.getContentsElement(document, role.equals("killer") ? "살인마" : "생존자");

        String title = null;

        while(true) {
            curDiv = crawlerUtil.getNextElement(document, curDiv);
            if (curDiv == null) {
                break;
            }

            Elements tables = curDiv.select("table");

            if (tables == null || tables.isEmpty()) {
                Element titleEl = curDiv.selectFirst("h2, h3, h4");
                if (titleEl != null) {
                    titleEl.select("a").forEach(Node::remove);
                    title = titleEl.text();
                }
                continue;
            } else {
                if(title != null && title.equals("공용 기술")) {
                    // 과거 공용 기술 영역 삭제
                    Element past = curDiv.selectFirst("dt:contains(과거 공용)");
                    if(past != null) {
                        past.parent().remove();
                        tables = curDiv.select("table");
                    }

                    for (Element perkElement : tables) {

                        Perk perk = Perk.builder()
                                .role(role)
                                .build();

                        String imgSrc = perkElement.select("noscript img").attr("src");

                        Elements nameElement = perkElement.select("tr:nth-of-type(2) td div strong span");

                        if(nameElement == null || nameElement.size() == 0) {
                            continue;
                        }
                        String name = nameElement.get(0).ownText();
                        String en_name = nameElement.get(1).ownText();

                        Element descriptionSpan = perkElement.select("tr td[rowspan='2'] div span").get(0);

                        String description = descriptionSpan.html();

                        perk.setImg(imgSrc);
                        perk.setName(name);
                        perk.setEnName(en_name);
                        perk.setDescription(description.replaceAll("\n", " "));

                        perkRepository.save(perk);
                    }

                    break;
                }
            }
        }
    }

}
