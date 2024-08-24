package dbd.perks.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class CrawlerUtil {

    /**
     * 문서 객체와 제목영역 문자열을 인수로 받아, 해당 제목에 해당하는 본문 영역 Element를 반환하는 함수
     * @param document 문서 객체
     * @param titleString 제목 영역 문자열
     * @return 본문 영역 Element
     */
    public Element getContentsElement(Document document, String titleString) {

        // 문서의 본문 부분 div
        Element contents = document.selectFirst("div.EoF2tNb4.QLFwR6Ut");

        // 제목 요소를 이용해 본문 영역을 찾는 과정
        // 제목 span 요소
        Element titleDiv = document.selectFirst("div.Imb4r44D h2.mWdzG-BT span:contains(" + titleString + ")");

        // 1. 제목 영역의 최상단 div를 찾기
        // 문서 본문 영역(contents)이 부모 요소가 될 때까지 거슬러 올라감
        while(true) {
            if(titleDiv != null && titleDiv.parent() != null && titleDiv.parent().equals(contents)) {
                break;
            } else {
                titleDiv = titleDiv.parent();
            }
        }

        // 2. 문서 본문 영역(contents)에서 목표 제목 영역 다음 인덱스 요소가 목표 본문 영역
        Element target = contents.child(titleDiv.siblingIndex());

        return target;
    }
}
