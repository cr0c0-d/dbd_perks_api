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
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataCrawler {

    private final PlayableRepository playableRepository;
    private final PerkRepository perkRepository;

    /**
     * 나무위키 도메인
     */
    private String namuWikiDomain = "https://namu.wiki";

    /**
     * 살인마 문서 목록
     */
    private String killerDocsListUrl = "https://namu.wiki/w/%EB%B6%84%EB%A5%98:DEAD%20BY%20DAYLIGHT/%EC%82%B4%EC%9D%B8%EB%A7%88";

    /**
     * 생존자 목록 문서
     */
    private String survivorInfoUrl = "https://namu.wiki/w/DEAD%20BY%20DAYLIGHT/%EC%83%9D%EC%A1%B4%EC%9E%90/%EC%98%A4%EB%A6%AC%EC%A7%80%EB%84%90%20%EC%BA%90%EB%A6%AD%ED%84%B0";

    /**
     * 살인마 개별 문서 링크 주소 리스트
     */
    private List<String> killerDocsLinkUrlList = new ArrayList<>();

    private List<Playable> killerList = new ArrayList<>();

    private List<Playable> survivorList = new ArrayList<>();

    private List<Perk> perkList = new ArrayList<>();

    private List<Item> itemList = new ArrayList<>();

    /**
     * 킬러 개별 문서의 url 주소를 조회하는 함수
     */
    public void getKillerDocUrlList() {
        try {

            // Jsoup 연결 - 살인마 개별 문서 목록 페이지
            Document document = Jsoup.connect(killerDocsListUrl).get();

            // 문서 목록 요소 선택 (다수일 경우 첫 번째 요소)
            Element listDiv = document.select("._1mly7SSR").get(0);

            // 살인마 개별 문서 링크에 해당하는 div 목록 추출
            List<Element> filteredDivList = listDiv.children().stream().filter(div ->
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

                Elements tables = document.select(".AVEibs0x");

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
                Elements nameSpans = profileTable.select("tbody tr td div.Fm-HYseR div span strong span.jrW0Zn5O");
                for(Element nameSpan : nameSpans) {
                    if(nameSpan.childrenSize() > 1) {
                        player.setEn_name(nameSpan.child(0).ownText());
                        player.setName(nameSpan.child(nameSpan.childrenSize() - 1).ownText());
                    }
                }

                // 한글명과 영문명 둘 다 찾은 경우 리스트에 추가
                if(player.getName() != null && player.getEn_name() != null) {
                    killerList.add(player);
                }

                List<Perk> perks = getKillerPerks(document, player);

                perkRepository.saveAll(perks);

            }

            playableRepository.saveAll(killerList);
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

        // cssSelector로 고유 기술 영역 찾기
        Elements perkDivs = document.select("div.Xg8bWR6v div.Xg8bWR6v div.Xg8bWR6v div.pukNZvf0 div.rQ4Wdu43 table tbody tr");
        if(perkDivs.size() != 0 ) {

            for (Element perkDiv : perkDivs) {

                // 자식 요소가 3개여야 함 (각 캐릭터당 고유 기술 개수는 3개이므로)
                if (perkDiv.childrenSize() != 3) {
                    continue;
                }

                for (Element perkElement : perkDiv.children()) {

                    Perk perk = Perk.builder()
                            .role(killer.getRole())
                            .playable_name(killer.getName())
                            .playable_en_name(killer.getEn_name())
                            .build();

                    Elements spans = perkElement.select("div.Fm-HYseR div div.Fm-HYseR div div span");
                    // 이미지 경로
                    String imgSrc = spans.get(0).child(0).child(1).attr("src");

                    // 한글명
                    String name = spans.get(1).child(0).ownText();

                    // 영문명
                    String en_name = spans.get(2).child(0).ownText();

                    // 설명
                    String description = perkElement.select("td div.Fm-HYseR div div.Fm-HYseR div div div span span.sek7pjNI dl.xuwY-BDU dd div span:nth-child(1)").get(0).wholeOwnText();

                    perk.setName(name);
                    perk.setEn_name(en_name);

                    perkList.add(perk);
                }
            }
        } else {    // 위 형식이 아닌 경우
            perkDivs = document.select("div.Xg8bWR6v div div.pukNZvf0 div.rQ4Wdu43 table tbody");

            for(Element perkElement : perkDivs) {
                Perk perk = Perk.builder()
                        .role(killer.getRole())
                        .playable_name(killer.getName())
                        .playable_en_name(killer.getEn_name())
                        .build();

                String imgSrc = perkElement.select("tr td div.Fm-HYseR a span span img.pSe7sj7a").attr("src");

                Elements nameElement = perkElement.select("tr td div.Fm-HYseR strong a.yfQL42-A span");
                String name = nameElement.get(0).ownText();
                String en_name = nameElement.get(1).ownText();

                String description = perkElement.select("tr td[rowspan='2'] div.Fm-HYseR span").get(0).ownText();

                perk.setImg(imgSrc);
                perk.setName(name);
                perk.setEn_name(en_name);
                perk.setDescription(description);

                perkList.add(perk);

            }
        }

        return perkList;
    }
}
