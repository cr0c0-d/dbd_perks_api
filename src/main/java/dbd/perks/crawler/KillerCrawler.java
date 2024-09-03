package dbd.perks.crawler;

import dbd.perks.domain.Addon;
import dbd.perks.domain.Perk;
import dbd.perks.domain.Playable;
import dbd.perks.domain.Weapon;
import dbd.perks.repository.AddonRepository;
import dbd.perks.repository.PerkRepository;
import dbd.perks.repository.PlayableRepository;
import dbd.perks.repository.WeaponRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KillerCrawler {

    private final PlayableRepository playableRepository;
    private final PerkRepository perkRepository;
    private final WeaponRepository weaponRepository;
    private final AddonRepository addonRepository;

    private final CrawlerUtil crawlerUtil;

    /**
     * 나무위키 도메인
     */
    private String namuWikiDomain = "https://namu.wiki";

    /**
     * 살인마 문서 목록
     */
    private String killerDocsListUrl = "https://namu.wiki/w/%EB%B6%84%EB%A5%98:DEAD%20BY%20DAYLIGHT/%EC%82%B4%EC%9D%B8%EB%A7%88";

    /**
     * 살인마 개별 문서 링크 주소 리스트
     */
    private List<String> killerDocsLinkUrlList = new ArrayList<>();


    /**
     * 킬러 크롤러 실행
     */
    public void runKillerCrawler() {
        getKillerDocUrlList();
    }

    /**
     * 킬러 개별 문서의 url 주소를 조회하는 함수
     */
    public void getKillerDocUrlList() {
        try {

            // Jsoup 연결 - 살인마 개별 문서 목록 페이지
            Document document = Jsoup.connect(killerDocsListUrl).get();

            document = crawlerUtil.removeAnnotation(document);


            // 살인마 개별 문서 링크에 해당하는 div 목록 추출
            List<Element> filteredDivList = document.select("h3").parents().stream().filter(div ->
                                div.is("div")   // 1. div 요소일 것
                            && div.child(0).is("h3")    // 2. 자식 요소의 첫 번째 요소가 h3일 것
                            && div.child(0).text().matches("[ㄱ-ㅎ]")).toList();  // 3. h3의 텍스트가 한글 자음 (ㄱ-ㅎ)일 것

            // 추출된 div -> li -> a 요소의 href 속성을 불러와 목록에 추가
            filteredDivList.stream().forEach(element -> element.children().select("li").forEach(li -> killerDocsLinkUrlList.add(li.select("a").attr("href"))));


        } catch (IOException e) {
            e.printStackTrace();
        }

        // 살인마 문서 url 목록이 비어 있지 않을 경우 다음 단계로
        if(!killerDocsLinkUrlList.isEmpty()) {
            getKillersData();
        }
    }

    /**
     * 수집된 킬러 개별 문서 Url에 접근해 각 킬러의 데이터 수집
     */
    public void getKillersData() {
        // 킬러 개별 문서 url이 수집되지 않았으면 return
        if(killerDocsLinkUrlList.isEmpty()) {
            return;
        }

        try {

            for(String url : killerDocsLinkUrlList) {
                Document document = Jsoup.connect(namuWikiDomain + url).get();

                // 미출시 이미지
                Elements imgUpcoming = document.select("img[alt='DBD DLC Upcoming']");
                if(!imgUpcoming.isEmpty()) {
                    // 미출시 캐릭터의 경우 스킵
                    continue;
                }

                // 주석 제거
                document = crawlerUtil.removeAnnotation(document);

                Elements tables = document.select("table");

                Element profileTable = null;

                // 프로필 영역 table 요소 찾기
                for(Element table : tables) {
                    if(table.text().contains("속도") && table.text().contains("공포 범위")) {
                        profileTable = table;
                        break;
                    }
                }

                if(profileTable == null) {
                    continue;
                }
                Playable player = Playable.builder()
                        .role("killer")
                        .build();

                /* 방법 1 : span 찾기
                // 프로필 영역의 span 요소만 찾기
                Elements textElements = profileTable.select("span.jrW0Zn5O, span.jrW0Zn5O div, span.jrW0Zn5O span");


                for(Element text : textElements) {
                    String textStr = text.wholeOwnText();
                    // 영어로만 이루어진 경우 - 영문명
                    if(textStr.matches("^[a-zA-Z\s]+$")) {
                        player.setEn_name(textStr);
                    } else if (textStr.matches("^[가-힣\s]+$")) {
                        // 한글로만 이루어진 경우 - 한글명
                        player.setName(textStr);
                    }

                    // 둘 다 찾았으면 break
                    if(player.getName() != null && player.getEn_name() != null) {
                        break;
                    }
                }

                 */

                /* 방법 2 : cssSelector로 찾기*/
                Elements nameSpans = profileTable.select("tbody tr td div div span strong span");
                for(Element nameSpan : nameSpans) {
                    if(nameSpan.childrenSize() > 1) {
                        player.setEnName(nameSpan.child(0).ownText());
                        player.setName(nameSpan.child(nameSpan.childrenSize() - 1).ownText());
                    }
                }

                // 한글명과 영문명 둘 다 찾은 경우

                if(player.getName() != null && player.getEnName() != null) {

                    // 리스트에 추가
                    // killerList.add(player);
                    
                    // DB에 저장
                    player = playableRepository.save(player);
                }

                List<Perk> perks = getKillerPerks(document, player);
                Weapon weapon = getKillerWeapon(document, player);
                List<Addon> addons = getKillerAddons(document, player);
                perkRepository.saveAll(perks);
                weaponRepository.save(weapon);
                addonRepository.saveAll(addons);

            }

        } catch(IOException e) {
            e.printStackTrace();
        }



    }

    /**
     *  살인마 개별 문서 Document 객체와 살인마 Playable 객체를 받아, 해당 살인마의 고유 기술을 찾아 List 형태로 반환하는 함수
     * @param document 살인마 개별 문서
     * @param killer 살인마 객체
     * @return Perk(고유 기술) 리스트
     */
    public List<Perk> getKillerPerks(Document document, Playable killer) {

        List<Perk> perkList = new ArrayList<>();

        Element perkDiv = crawlerUtil.getContentsElement(document, "고유 기술");

        Elements perkTables = perkDiv.select("table tbody");

        // perk마다 개별 table이 존재하는 유형
        if(perkTables.size() > 1) {

            for(Element perkElement : perkTables) {
                Perk perk = Perk.builder()
                        .role(killer.getRole())
                        .playableId(killer.getId())
                        .build();

                String imgSrc = perkElement.select("noscript img").attr("src");

                Elements nameElement = perkElement.select("tr:nth-of-type(2) td div strong span");

                String name = nameElement.get(0).ownText();
                String en_name = nameElement.get(1).ownText();

                Element descriptionSpan = perkElement.select("tr td[rowspan='2'] div span").get(0);

                String description = descriptionSpan.html();

                perk.setImg(imgSrc);
                perk.setName(name);
                perk.setEnName(en_name);
                perk.setDescription(description.replaceAll("\n", " "));

                perkList.add(perk);

            }


        } else if (perkTables.size() == 1) { // 한 table에 합쳐진 유형
            perkTables = perkTables.select("tr");

            for (Element perkTable : perkTables) {

                // 자식 요소가 3개여야 함 (각 캐릭터당 고유 기술 개수는 3개이므로)
                if (perkDiv.childrenSize() != 3) {
                    continue;
                }

                for (Element perkElement : perkTable.children()) {

                    Perk perk = Perk.builder()
                            .role(killer.getRole())
                            .playableId(killer.getId())
                            .build();

                    Elements spans = perkElement.select("div div div div div span");
                    // 이미지 경로
                    String imgSrc = spans.select("noscript img").attr("src");

                    // 한글명
                    String name = spans.select("span strong").get(0).ownText();

                    // 영문명
                    String en_name = spans.select("span strong").get(1).ownText();

                    // 설명
                    Element descriptionSpan = perkElement.select("td div div div div div div span span dl dd div span:nth-child(1)").get(0);

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


    /**
     *  살인마 개별 문서 Document 객체와 살인마 Playable 객체를 받아, 해당 살인마의 특수능력을 찾아 Weapon 객체로 반환하는 함수
     * @param document 살인마 개별 문서
     * @param killer 살인마 객체
     * @return 특수능력 Weapon 객체
     */
    public Weapon getKillerWeapon(Document document, Playable killer) {

        Weapon weapon = Weapon.builder()
                .killerId(killer.getId())
                .build();

        Element wpDiv = crawlerUtil.getContentsElement(document, "무기 & 능력");

        Elements wpTableTds = wpDiv.select("table tbody").get(1).select("td");

        String img = wpTableTds.get(0).select("noscript img").attr("src");
        Element nameElement = wpTableTds.get(1).selectFirst("div span");
        String name = nameElement.selectFirst("strong").wholeText();
        String enName = nameElement.select("span").get(nameElement.select("span").size()-1).wholeText();

        weapon.setImg(img);
        weapon.setName(name);
        weapon.setEnName(enName.replace("(", "").replace(")", ""));

        return weapon;
    }

    /**
     *  살인마 개별 문서 Document 객체와 살인마 Playable 객체를 받아, 해당 살인마의 애드온을 찾아 List 형태로 반환하는 함수
     * @param document 살인마 개별 문서
     * @param killer 살인마 객체
     * @return Addon(애드온) 리스트
     */
    public List<Addon> getKillerAddons(Document document, Playable killer) {
        List<Addon> addonList = new ArrayList<>();

        Element addonDiv = crawlerUtil.getContentsElement(document, "애드온");

        Elements addonLines = addonDiv.select("table tbody tr:nth-child(n+2)");

        for(Element addonLine : addonLines) {
            Elements tds = addonLine.select("td");

            Addon addon = Addon.builder()
                    .killerId(killer.getId())
                    .build();

            for(int i = 0; i < tds.size(); i++) {
                Element td = tds.get(i);
                switch(i) {
                    case 0 :
                        String img = td.select("noscript img").attr("src");
                        addon.setImg(img);
                        break;
                    case 1 :
                        Elements aTags = td.select("a");
                        if(!aTags.isEmpty()) {
                            for(Element aTag : aTags) {
                                aTag.remove();
                            }
                        }
                        Element nameElement = td.select("strong").get(0);
                        List<Node> childNodes = nameElement.childNodes();
                        for(Node node : childNodes) {
                            if(node.siblingIndex() == 0) {
                                String name = ((TextNode) node).text();
                                addon.setName(name);
                            } else if (node.siblingIndex() == 2) {
                                String enName = ((TextNode) node).text();
                                addon.setEnName(enName);
                            }
                        }

                        break;
                    case 3 :
                        String level = td.select("span").get(0).wholeText();
                        addon.setLevel(level);
                        break;
                    case 4 :
                        addon.setDescription(td.html());
                        break;
                    default :
                        break;
                }
            }
            addonList.add(addon);
        }

        return addonList;
    }
}
