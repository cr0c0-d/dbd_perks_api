package dbd.perks.crawler;

import dbd.perks.domain.Addon;
import dbd.perks.domain.Offering;
import dbd.perks.repository.OfferingRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class OfferingCrawler {

    private final OfferingRepository offeringRepository;

    private final CrawlerUtil crawlerUtil;
    private final ScrollCrawler scrollCrawler;

    /**
     * 나무위키 도메인
     */
    private String namuWikiDomain = "https://namu.wiki";

    private String offeringUrl = "https://namu.wiki/w/DEAD%20BY%20DAYLIGHT/%EA%B3%B5%EB%AC%BC";
    private String offeringEnUrl = "https://deadbydaylight.fandom.com/wiki/Offerings";

    private Long ver;

    /**
     * 오퍼링 크롤러 실행
     */
    public void runOfferingCrawler() {
        getOfferingDocument();
    }

    /**
     * 오퍼링 문서를 불러와 각 데이터를 수집하는 함수
     */
    public void getOfferingDocument() {
        Document document = scrollCrawler.getDocumentByScrollCrawler(offeringUrl);

        Document documentEn = null;

        // 버전 조회
        Long ver = crawlerUtil.getVersion(document, offeringUrl);

        // 이전 버전과 같은 경우 스킵
        if(ver == null) {
            return;
        }

        // 주석 링크 제거
        document = crawlerUtil.removeAnnotation(document);

        Element curDiv = crawlerUtil.getContentsElement(document, "목록");

        String title = null;

        while(true) {
            curDiv = crawlerUtil.getNextElement(document, curDiv);
            if(curDiv == null) {
                break;
            }

            Element table = curDiv.selectFirst("table");

            if(table == null) {
                Element titleEl = curDiv.selectFirst("h2, h3, h4");
                if(titleEl != null) {
                    titleEl.select("a").forEach(Node::remove);
                    title = titleEl.text();
                }
                continue;
            }

            Elements trs = table.select("tr");

            for (Element tr : trs) {
                Elements tds = tr.select("td");


                // 테이블 제목줄은 스킵 (조건 : 열 수가 4 미만 || 설명란 텍스트 길이가 10 미만)
                if (tds.size() < 4 || tds.get(tds.size()-1).text().length() < 10) {
                    continue;
                }

                String name = null;
                String enName = null;

                List<Node> nodeList = tds.get(1).selectFirst("strong").childNodes();

                for(Node node : nodeList) {
                    if(node instanceof TextNode) {
                        TextNode textNode = (TextNode) node;
                        String str = textNode.text();
                        if(str.matches("[^가-힣]+")) {
                            enName = str;
                        } else {
                            name = str;
                        }
                    }
                }

//                String nameText = tds.get(1).text();
//
//                Pattern pattern = Pattern.compile("([^가-힣]+)([가-힣\\s\\d\\p{Punct}]+)|([가-힣\\s\\d\\p{Punct}]+)([^가-힣]+)");
//                Matcher matcher = pattern.matcher(nameText);
//
//                if (matcher.find()) {
//                    for (int i = 1; i < 3; i++) {
//                        String str = matcher.group(i);
//                        if(str == null) {
//                            break;
//                        }
//                        if (str.matches("[가-힣\\s\\d\\p{Punct}]+")) {
//                            name = str;
//                        } else {
//                            enName = str;
//                        }
//                    }
//                } else {
//                    name = nameText;
//                }

                String level = tds.get(2).text();

                String description = tds.get(tds.size() - 1).html();
                
                // 만료된 공물 스킵
                if(description.contains("이 공물은 만료되어")) {
                    continue;
                }

                Element imgEl = tr.selectFirst("noscript img");
                String img = null;

                if(imgEl != null) {
                    img = imgEl.attr("src");
                } else {
                    // 이미지 없는 경우 영문위키에서 따올 것.
                    if(enName != null) {
                        if(documentEn == null) {
                            documentEn = scrollCrawler.getDocumentByScrollCrawler(offeringEnUrl);
                            documentEn = crawlerUtil.removeAnnotation(documentEn);
                        }
                        Elements aTags = documentEn.select("th a");
                        for(Element aTag : aTags) {
                            String str = aTag.html();
                            if(str.contains(enName)) {
                                img = aTag.parent().parent().selectFirst("img").attr("data-src");
                            }
                        }
                    }
                }

                Offering offering = Offering.builder()
                        .name(name)
                        .enName(enName)
                        .level(level)
                        .img(img)
                        .description(description)
                        .isActivated(true)
                        .build();

                offering.setRole(getRole(title, offering));

                crawlerUtil.getLatestVersion(offering);

            }

        }
    }

    /**
     * [임시 함수] 오퍼링 영역의 제목과 오퍼링 객체를 받아 해당 오퍼링의 역할군을 문자열로 반환하는 함수.
     * [문서 페이지에서 역할군 정보를 불러올 수 없어 작성한 임시 함수]
     * @param title 영역 제목
     * @param offering 오퍼링 객체
     * @return 역할군
     */
    public String getRole(String title, Offering offering) {
        String name = offering.getName();
        switch(title) {
            case "생존자", "행운":
                return "survivor";
            case "살인마", "즉결 처형" :
                return "killer";
            case "상자" :
                if(name.contains("잘려진") || name.contains("긁힌")) {
                    return "killer";
                } else {
                    return "survivor";
                }
            case "갈고리" :
                if(name.contains("굳어버린")) {
                    return "survivor";
                } else {
                    return "killer";
                }
            case "장막" :
                if(name.contains("분단의")) {
                    return "killer";
                } else {
                    return "survivor";
                }
            case "보호자" :
                if(name.contains("검은")) {
                    return "killer";
                } else if(name.contains("하얀")) {
                    return "survivor";
                } else {
                    return "common";
                }

            case "공용", "어두운 안개", "비상탈출구", "지하실", "특정 맵 확률" :
                return "common";

            default :
                return null;
        }
    }




}
