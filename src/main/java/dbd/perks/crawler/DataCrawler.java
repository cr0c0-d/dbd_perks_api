package dbd.perks.crawler;

import dbd.perks.domain.Playable;
import dbd.perks.domain.Item;
import dbd.perks.domain.Perk;
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
            }

            playableRepository.saveAll(killerList);
        } catch(IOException e) {
            e.printStackTrace();
        }



    }
}
