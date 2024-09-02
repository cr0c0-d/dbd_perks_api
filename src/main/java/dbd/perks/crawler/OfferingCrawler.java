package dbd.perks.crawler;

import dbd.perks.domain.Addon;
import dbd.perks.domain.Offering;
import dbd.perks.repository.OfferingRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

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

            if(title.equals("달빛") || title.equals("쐐기")) {
                continue;
            }

            Elements trs = table.select("tr");

            for (Element tr : trs) {
                Elements tds = tr.select("td");

                Element imgEl = tr.selectFirst("noscript img");

                // 테이블 제목줄은 스킵 (조건 : 열 수가 4 미만 || 설명란 텍스트 길이가 10 미만)
                if (tds.size() < 4 || tds.get(tds.size()-1).text().length() < 10) {
                    continue;
                }

                String img = imgEl.attr("src");

                String name = null;
                String enName = null;

                String nameText = tds.get(1).text();

                Pattern pattern = Pattern.compile("([^가-힣]+)([가-힣\\s\\d\\p{Punct}]+)|([가-힣\\s\\d\\p{Punct}]+)([^가-힣]+)");
                Matcher matcher = pattern.matcher(nameText);

                if (matcher.find()) {
                    for (int i = 1; i < 3; i++) {
                        String str = matcher.group(i);
                        if(str == null) {
                            break;
                        }
                        if (str.matches("[가-힣\\s\\d\\p{Punct}]+")) {
                            name = str;
                        } else {
                            enName = str;
                        }
                    }
                }

                String level = tds.get(2).text();

                String description = tds.get(tds.size() - 1).html();

                Offering offering = Offering.builder()
                        .name(name)
                        .enName(enName)
                        .level(level)
                        .img(img)
                        .description(description)
                        .build();

                offering.setRole(getRole(title, offering));

                offeringRepository.save(offering);

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
