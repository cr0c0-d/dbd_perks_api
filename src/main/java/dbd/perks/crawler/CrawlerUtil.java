package dbd.perks.crawler;

import dbd.perks.domain.Ver;
import dbd.perks.repository.VerRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrawlerUtil {

    private final VerRepository verRepository;

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

    /**
     * 문서 객체 Document를 받아 주석([1] 형식의 a 태그)을 제거 후 반환
     * @param document 문서
     * @return 주석이 제거된 Document
     */
    public Document removeAnnotation(Document document) {
        Elements links = document.select("a");

        for (Element link : links) {
            String text = link.text();
            if (text.matches("[^>]*\\[[A-Z0-9]+\\]")) { // 대괄호 안에 1 이상의 숫자 혹은 알파벳 대문자가 있는지 확인
                link.remove(); // 해당 a 태그 삭제
            }
        }

        return document;
    }

    public LocalDateTime getLastModifiedTime(Document document) {
        Element lastModifiedTime = document.selectFirst("div:contains(최근 수정 시각:)");
        if(lastModifiedTime != null) {
            Element time = lastModifiedTime.selectFirst("time");
            if(time != null) {
                String datetime = time.attr("datetime");

                OffsetDateTime offsetDateTime = OffsetDateTime.parse(datetime);
                return offsetDateTime.toLocalDateTime();
            }

        }
        return null;
    }

    public Long getVersion(Document document, String type) {
    public Long getVersion(Document document) {
        LocalDateTime docModifiedTime = getLastModifiedTime(document);

        Optional<CrawledDocument> recorded = crawledDocumentRepository.findFirstByUrlOrderByVerDesc(document.baseUri());

        // 기록된 문서버전이 없으면 1, 기록이 있고 수정시각이 서로 다르면 이전 버전+1로 insert
        if(recorded.isEmpty() || !recorded.get().getLastModifiedTime().equals(docModifiedTime)) {
            return crawledDocumentRepository.save(CrawledDocument.builder()
                    .url(document.baseUri())
                    .ver(recorded.map(crawledDocument -> crawledDocument.getVer() + 1).orElse(1L))
                    .lastModifiedTime(docModifiedTime)
                    .build()).getVer();
        } else {
            return null;
        }
    }


}
