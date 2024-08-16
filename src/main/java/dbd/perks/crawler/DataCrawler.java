package dbd.perks.crawler;

import dbd.perks.domain.Character;
import dbd.perks.domain.Item;
import dbd.perks.domain.Perk;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataCrawler {

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

    private List<Character> killerList = new ArrayList<>();

    private List<Character> survivorList = new ArrayList<>();

    private List<Perk> perkList = new ArrayList<>();

    private List<Item> itemList = new ArrayList<>();

    public void getKillerDocUrlList() {
        try {

            // Jsoup 연결 - 살인마 개별 문서 목록 페이지
            Document document = Jsoup.connect(killerDocsListUrl).get();

            // 문서 목록 요소 선택 (다수일 경우 첫 번째 요소)
            Element listDiv = document.select("_1mly7SSR").get(0);

            // 살인마 개별 문서 링크에 해당하는 div 목록 추출
            List<Element> filteredDivList = listDiv.children().stream().filter(div ->
                                div.is("div")   // 1. div 요소일 것
                            && div.child(0).is("h3")    // 2. 자식 요소의 첫 번째 요소가 h3일 것
                            && div.child(0).text().matches("[ㄱ-ㅎ]")).toList();  // 3. h3의 텍스트가 한글 자음 (ㄱ-ㅎ)일 것

            // 추출된 div -> li -> a 요소의 href 속성을 불러와 목록에 추가
            filteredDivList.stream().forEach(element -> element.children().select("li").forEach(li -> killerDocsLinkUrlList.add(li.select("a").attr("href"))));

            System.out.println(killerDocsLinkUrlList);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Perk> getKillerDocs() {

        try {
            // URL에서 HTML 문서 가져오기
            //Document doc = Jsoup.connect("https://namu.wiki/w/DEAD%20BY%20DAYLIGHT/%EA%B8%B0%EC%88%A0").get();
            Document killerDocsListDocument = Jsoup.connect(killerDocsListUrl).get();

            Elements killerDocsList = killerDocsListDocument.select("li");



            for (Element li : killerDocsList) {
                if(li.childrenSize() != 0 && li.child(0).is("a")) {
                    String linkUrl = li.child(0).attr("href");
                    if(linkUrl.startsWith("/w")) {
                        killerDocsLinkUrlList.add(namuWikiDomain + linkUrl);
                    }
                }

            }
            System.out.println(killerDocsLinkUrlList);

            // 특정 요소 선택 (예: 모든 <a> 태그)
//            Elements links = doc.select("a");
//            for (Element link : links) {
//                System.out.println("링크: " + link.attr("href")); // 링크 URL
//                System.out.println("링크 텍스트: " + link.text()); // 링크 텍스트
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
