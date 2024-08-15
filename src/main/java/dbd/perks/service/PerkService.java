package dbd.perks.service;

import dbd.perks.domain.Perk;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PerkService {

    private String namuWikiDomain = "https://namu.wiki";

    /**
     * 살인마별 문서 링크 주소 리스트
     */
    private List<String> killerDocsLinkUrlList = new ArrayList<>();

    public List<Perk> getPerks() {

        try {
            // URL에서 HTML 문서 가져오기
            //Document doc = Jsoup.connect("https://namu.wiki/w/DEAD%20BY%20DAYLIGHT/%EA%B8%B0%EC%88%A0").get();
            Document killerDocsListDocument = Jsoup.connect("https://namu.wiki/w/%EB%B6%84%EB%A5%98:DEAD%20BY%20DAYLIGHT/%EC%82%B4%EC%9D%B8%EB%A7%88").get();

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
