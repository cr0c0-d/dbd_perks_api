package dbd.perks.crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
        Element span = document.selectFirst("span:contains(분류)").parent();
        Element contents = span.nextElementSibling();
        while(contents.childrenSize() < 3) {
            contents = contents.nextElementSibling();
        }

        // 제목 요소를 이용해 본문 영역을 찾는 과정
        // 제목 span 요소
        Element titleDiv = document.selectFirst("div h2 span:contains(" + titleString + ")");

        if(titleDiv == null) {
            titleDiv = document.selectFirst("div h4 span:contains(" + titleString + ")");
        }

        if(titleDiv == null) {
            return null;
        }

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

    /**
     * 문서 객체와 제목영역 문자열을 인수로 받아, 해당 제목에 해당하는 본문 영역 Elements를 반환하는 함수
     * @param document 문서 객체
     * @param titleString 제목 영역 문자열
     * @return 본문 영역 Element
     */
    public Elements getContentsElements(Document document, String titleString) {

        // 문서의 본문 부분 div
        Element span = document.selectFirst("span:contains(분류)").parent();
        Element contents = span.nextElementSibling();
        while(contents.childrenSize() < 3) {
            contents = contents.nextElementSibling();
        }

        Elements titleDivs = document.select("div h2 span:contains(" + titleString + ")");

        Elements target = null;

        for(Element titleDiv : titleDivs) {

            Element title = titleDiv;

            // 1. 제목 영역의 최상단 div를 찾기
            // 문서 본문 영역(contents)이 부모 요소가 될 때까지 거슬러 올라감
            while (true) {
                if (title != null && title.parent() != null && title.parent().equals(contents)) {
                    break;
                } else {
                    title = titleDiv.parent();
                }
            }

            // 2. 문서 본문 영역(contents)에서 목표 제목 영역 다음 인덱스 요소가 목표 본문 영역
            target.add(contents.child(title.siblingIndex()));

        }
        return target;
    }

    public Element getNextElement(Document document, Element element) {
        // 문서의 본문 부분 div
        Element span = document.selectFirst("span:contains(분류)").parent();
        Element contents = span.nextElementSibling();
        while(contents.childrenSize() < 3) {
            contents = contents.nextElementSibling();
        }

        if(contents.children().contains(element)) {
            if(contents.childrenSize() > element.siblingIndex()) {
                return contents.child(element.siblingIndex());
            } else {
                return null;
            }

        } else {
            Element curEl = element;
            while(true) {
                if(curEl.parent().equals(contents)) {
                    return contents.child(curEl.siblingIndex());
                } else {
                    curEl = curEl.parent();
                }
            }
        }
    }
}
