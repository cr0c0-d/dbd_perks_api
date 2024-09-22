package dbd.perks.crawler;

import dbd.perks.domain.Addon;
import dbd.perks.domain.Item;
import dbd.perks.domain.Perk;
import dbd.perks.domain.Playable;
import dbd.perks.repository.AddonRepository;
import dbd.perks.repository.ItemRepository;
import dbd.perks.repository.PerkRepository;
import dbd.perks.repository.PlayableRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SurvivorCrawler {

    private final PlayableRepository playableRepository;
    private final PerkRepository perkRepository;
    private final ItemRepository itemRepository;
    private final AddonRepository addonRepository;

    private final CrawlerUtil crawlerUtil;
    private final ScrollCrawler scrollCrawler;

    /**
     * 나무위키 도메인
     */
    private String namuWikiDomain = "https://namu.wiki";

    /**
     * 생존자 오리지널 문서
     */
    private String survivorDocUrlOri = "https://namu.wiki/w/DEAD%20BY%20DAYLIGHT/%EC%83%9D%EC%A1%B4%EC%9E%90/%EC%98%A4%EB%A6%AC%EC%A7%80%EB%84%90%20%EC%BA%90%EB%A6%AD%ED%84%B0";

    /**
     * 생존자 라이센스 문서
     */
    private String survivorDocUrlLic = "https://namu.wiki/w/DEAD%20BY%20DAYLIGHT/%EC%83%9D%EC%A1%B4%EC%9E%90/%EB%9D%BC%EC%9D%B4%EC%84%A0%EC%8A%A4%20%EC%BA%90%EB%A6%AD%ED%84%B0";

    /**
     * 생존자 아이템 문서
     */
    private String itemDocUrl = "https://namu.wiki/w/DEAD%20BY%20DAYLIGHT/%EC%95%84%EC%9D%B4%ED%85%9C";


    // 현재 데이터의 버전정보
    private Long ver;

    /**
     * 생존자 크롤러 실행
     */
    public void runSurvivorCrawler() {
        getSurvivorDocument();
    }

    /**
     * 생존자 문서를 불러와 각 데이터를 수집하는 함수
     */
    public void getSurvivorDocument() {


        // Selenium 연결 - 생존자(오리지널)
        Document documentOri = scrollCrawler.getDocumentByScrollCrawler(survivorDocUrlOri);

        if(crawlerUtil.getVersion(documentOri, survivorDocUrlOri) != null) {
            getSurvivorData(documentOri);
        }
        documentOri = null;

        // Selenium 연결 - 생존자(라이센스)
        Document documentLic = scrollCrawler.getDocumentByScrollCrawler(survivorDocUrlLic);

        if(crawlerUtil.getVersion(documentLic, survivorDocUrlLic) != null) {
            getSurvivorData(documentLic);
        }
        documentLic = null;

        // Selenium 연결 - 생존자 아이템
        Document documentItem = scrollCrawler.getDocumentByScrollCrawler(itemDocUrl);

        if(crawlerUtil.getVersion(documentItem, itemDocUrl) != null) {
            getSurvivorItems(documentItem);
        }
        documentItem = null;

    }

    public void getSurvivorData(Document document) {

        // 주석 제거
        document = crawlerUtil.removeAnnotation(document);

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
            String wholeText = survTitleATag.parent().wholeText();
            // 생존자 이름 저장
            survNames.add(wholeText.split("\\.", 2)[1].trim());
        }


        for(String survName : survNames) {
            Playable survivor = Playable.builder()
                    .name(survName)
                    .role("survivor")
                    .isActivated(true)
                    .build();

            List<Perk> perkList = null;

            Element survDiv = crawlerUtil.getContentsElement(document, survName);

            if(survDiv == null) {
                continue;
            }

            // 미출시 이미지
            Elements imgUpcoming = survDiv.select("img[alt='DBD DLC Upcoming']");
            if(!imgUpcoming.isEmpty()) {
                // 미출시 캐릭터의 경우 스킵
                continue;
            }

            Element survTable = survDiv.selectFirst("table tbody tr");

            Element survNameSpan = survTable.selectFirst("strong span");
            survivor.setEnName(survNameSpan.child(0).ownText());

            playableList.add((Playable) crawlerUtil.getLatestVersion(survivor));

            Element perkDiv = crawlerUtil.getNextElement(document, survDiv);

            while(!perkDiv.wholeText().contains("전승 기술")) {
                perkDiv = crawlerUtil.getNextElement(document, perkDiv);
            }

            perkDiv = crawlerUtil.getNextElement(document, perkDiv);
            perkList = getSurvivorPerks(perkDiv, survivor);

            perkList.forEach(crawlerUtil::getLatestVersion);
        }
    }

    public List<Perk> getSurvivorPerks(Element perkDiv, Playable player) {
        List<Perk> perkList = new ArrayList<>();

        Elements tables = perkDiv.select("table");

        if(tables.size() > 1) {
            for(Element table : tables) {
                Perk perk = Perk.builder()
                        .role(player.getRole())
                        .playableId(player.getId())
                        .isActivated(true)
                        .build();

                String imgSrc = table.select("noscript img").attr("src");

                Elements nameElement = table.select("tr:nth-of-type(2) td div strong span");

                String name = nameElement.get(0).ownText();
                String en_name = nameElement.get(nameElement.size()-1).ownText();

                Element descriptionSpan = table.select("tr td[rowspan='2'] div span").get(0);

                String description = descriptionSpan.html();

                perk.setImg(imgSrc);
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
                            .playableId(player.getId())
                            .isActivated(true)
                            .build();

                    Elements spans = perkElement.select("div div div div div span");
                    // 이미지 경로
                    String imgSrc = spans.select("noscript img").attr("src");

                    // 한글명
                    Elements nameSpans = spans.select("span strong");
                    if(nameSpans.isEmpty()) {
                        continue;
                    }
                    String name = nameSpans.get(0).ownText();

                    // 영문명
                    String en_name = spans.select("span strong").get(1).ownText();

                    // 설명
                    Element descriptionSpan = perkElement.select("dl dd div span:nth-child(1)").get(0);

                    String description = descriptionSpan.html();

                    perk.setImg(imgSrc);
                    perk.setName(name);
                    perk.setEnName(en_name);
                    perk.setDescription(description.replaceAll("\n", " "));

                    perkList.add(perk);
                }

            }
        }
        return perkList;
    }

    public void getSurvivorItems(Document document) {
        // 주석 제거
        document = crawlerUtil.removeAnnotation(document);

        Element curDiv = crawlerUtil.getContentsElement(document, "종류");
        String curTypeName = null;
        String curTypeEnName = null;

        while(true) {
            /********************* 아이템 타입 *********************/
            curDiv = crawlerUtil.getNextElement(document, curDiv);

            if(!curDiv.select("h2").isEmpty()) {
                break;
            }

            // 문단번호 링크 제거
            curDiv.selectFirst("a").remove();

            String[] fullname = curDiv.selectFirst("span").wholeOwnText().split("\\(");
            curTypeName = fullname[0];
            curTypeEnName = fullname[1].substring(0, fullname[1].length()-1);

            /********************* 아이템 목록 *********************/
            curDiv = crawlerUtil.getNextElement(document, curDiv);
            Elements tables = curDiv.select("table");

            if(tables.size() == 1) {
                Element table = tables.get(0);
                Elements trs = table.select("tr");

                for(Element tr : trs) {
                    Element imgEl = tr.selectFirst("noscript img");

                    // 이미지가 없는 경우 스킵 (테이블의 제목줄)
                    if(imgEl == null) {
                        continue;
                    }

                    Elements tds = tr.select("td");

                    String img = imgEl.attr("src");

                    String name = null;
                    String enName = null;

                    String nameText = tds.get(1).text();

                    Pattern pattern = Pattern.compile("([A-Za-z0-9\\s]+)([가-힣0-9\\s]+)|([가-힣0-9\\s]+)([A-Za-z0-9\\s]+)");
                    Matcher matcher = pattern.matcher(nameText);

                    if(matcher.find()) {
                        for(int i = 1; i < 3; i++) {
                            String str = matcher.group(i).trim();
                            if (str.matches("[A-Za-z0-9\\s]+")) {
                                enName = str;
                            } else if (str.matches("[가-힣0-9\\s]+")) {
                                name = str;
                            }
                        }
                    }

                    String level = tds.get(2).text();

                    String description = tds.get(tds.size()-1).html();

                    Item item = Item.builder()
                                    .name(name)
                                    .enName(enName)
                                    .level(level)
                                    .typeName(curTypeName)
                                    .typeEnName(curTypeEnName)
                                    .img(img)
                                    .description(description)
                                    .isActivated(true)
                                    .build();

                    crawlerUtil.getLatestVersion(item);

                }

            } else {
                for (Element table : tables) {
                    Elements trs = table.select("tr");
                    String img = trs.get(0).select("noscript img").attr("src");

                    Element nameElement = trs.get(0).child(1).selectFirst("div span");
                    String name = nameElement.child(0).text();
                    String enName = nameElement.child(1).text().replace("(", "").replace(")", "");

                    if (enName.equals("")) {
                        enName = nameElement.child(2).text().replace("(", "").replace(")", "");
                    }

                    String level = nameElement.ownText();
                    String description = trs.get(1).selectFirst("td").html();

                    Item item = Item.builder()
                            .name(name)
                            .enName(enName)
                            .level(level)
                            .typeName(curTypeName)
                            .typeEnName(curTypeEnName)
                            .img(img)
                            .description(description)
                            .isActivated(true)
                            .build();

                    crawlerUtil.getLatestVersion(item);
                }
            }

            /********************* 애드온 목록 *********************/
            Element addonDiv = crawlerUtil.getNextElement(document, curDiv);
            if(!addonDiv.wholeText().contains("애드온")) {
                break;
            }

            curDiv = crawlerUtil.getNextElement(document, addonDiv);

            Elements addonTables = curDiv.select("table");

            if(tables.size() == 1) {
                Element table = tables.get(0);
                Elements trs = table.select("tr");

                for(Element tr : trs) {
                    Element imgEl = tr.selectFirst("noscript img");

                    // 이미지가 없는 경우 스킵 (테이블의 제목줄)
                    if(imgEl == null) {
                        continue;
                    }

                    Elements tds = tr.select("td");

                    String img = imgEl.attr("src");

                    String name = null;
                    String enName = null;

                    String nameText = tds.get(1).text();

                    Pattern pattern = Pattern.compile("([A-Za-z0-9\\s]+)([가-힣0-9\\s]+)|([가-힣0-9\\s]+)([A-Za-z0-9\\s]+)");
                    Matcher matcher = pattern.matcher(nameText);

                    if(matcher.find()) {
                        for(int i = 1; i < 3; i++) {
                            String str = matcher.group(i).trim();
                            if (str.matches("[A-Za-z0-9\\s]+")) {
                                enName = str;
                            } else if (str.matches("[가-힣0-9\\s]+")) {
                                name = str;
                            }
                        }
                    }

                    String level = tds.get(2).text();

                    String description = tds.get(tds.size()-1).html();

                    Addon addon = Addon.builder()
                                        .name(name)
                                        .enName(enName)
                                        .level(level)
                                        .typeName(curTypeName)
                                        .typeEnName(curTypeEnName)
                                        .img(img)
                                        .description(description)
                                        .isActivated(true)
                                        .build();

                    crawlerUtil.getLatestVersion(addon);

                }

            } else {
                for (Element addonTable : addonTables) {
                    Elements trs = addonTable.select("tr");
                    String img = trs.get(0).select("noscript img").attr("src");

                    Element nameElement = trs.get(0).child(1).selectFirst("div span");
                    String name = nameElement.child(0).text();
                    String enName = nameElement.child(1).text().replace("(", "").replace(")", "");;
                    String level = nameElement.ownText();
                    String description = trs.get(1).selectFirst("td").html();

                    Addon addon = Addon.builder()
                                    .name(name)
                                    .enName(enName)
                                    .level(level)
                                    .typeName(curTypeName)
                                    .typeEnName(curTypeEnName)
                                    .img(img)
                                    .description(description)
                                    .isActivated(true)
                                    .build();

                    crawlerUtil.getLatestVersion(addon);
                }
            }


        }


    }

}
