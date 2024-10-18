package dbd.perks.crawler;

import dbd.perks.domain.*;
import dbd.perks.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CrawlerUtil {

    @Value("${img.path}")
    String imgPath;

    private final PlayableRepository playableRepository;
    private final PerkRepository perkRepository;
    private final WeaponRepository weaponRepository;
    private final AddonRepository addonRepository;
    private final ItemRepository itemRepository;
    private final OfferingRepository offeringRepository;

    private final CrawledDocumentRepository crawledDocumentRepository;

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

        } else {
            System.out.println("******************************* 최근 수정 시각 찾을 수 없음 ************************************");
            System.out.println(document);
            System.out.println("******************************* 최근 수정 시각 찾을 수 없음 ************************************");
        }
        return null;
    }

    @Transactional
    public Data getLatestVersion(Data newData) {
        Data oldData = newData instanceof Playable ? playableRepository.findFirstByEnNameOrderByCreatedAtDesc(newData.getEnName()).orElse(null)
                : newData instanceof Addon ? addonRepository.findFirstByEnNameOrderByCreatedAtDesc(newData.getEnName()).orElse(null)
                : newData instanceof Item ? itemRepository.findFirstByEnNameOrderByCreatedAtDesc(newData.getEnName()).orElse(null)
                : newData instanceof Offering ? offeringRepository.findFirstByEnNameOrderByCreatedAtDesc(newData.getEnName()).orElse(null)
                : newData instanceof Perk ? perkRepository.findFirstByEnNameOrderByCreatedAtDesc(newData.getEnName()).orElse(null)
                : newData instanceof Weapon ? weaponRepository.findFirstByEnNameOrderByCreatedAtDesc(newData.getEnName()).orElse(null) : null;

        if (!newData.validate()) {
            return oldData;
        }

        if (oldData != null) {
            if (oldData.equals(newData)) {
                return oldData;
            }
            oldData.deactivate();
        }

        return newData instanceof Playable ? playableRepository.save((Playable) newData)
                : newData instanceof Addon ? addonRepository.save((Addon) newData)
                : newData instanceof Item ? itemRepository.save((Item) newData)
                : newData instanceof Offering ? offeringRepository.save((Offering) newData)
                : newData instanceof Perk ? perkRepository.save((Perk) newData)
                : newData instanceof Weapon ? weaponRepository.save((Weapon) newData)
                : null;
    }

    public Long getVersion(Document document, String url) {
        LocalDateTime docModifiedTime = getLastModifiedTime(document);

        // 문서 수정시각을 불러올 수 없으면 null 리턴
        if(docModifiedTime == null ) {
            return null;
        }

        Optional<CrawledDocument> recorded = crawledDocumentRepository.findFirstByUrlOrderByVerDesc(url);

        // 기록된 문서버전이 없으면 1, 기록이 있고 수정시각이 서로 다르면 이전 버전+1로 insert
//        if(recorded.isEmpty() || !recorded.get().getLastModifiedTime().equals(docModifiedTime)) {
            return crawledDocumentRepository.save(CrawledDocument.builder()
                    .url(url)
                    .ver(recorded.map(crawledDocument -> crawledDocument.getVer() + 1).orElse(1L))
                    .lastModifiedTime(docModifiedTime)
                    .build()).getVer();
//        } else {
//            return null;
//        }
    }

    public String getImgUrl(String imgUrl) {
        try {
            URL url = new URL(imgUrl.startsWith("http") ? imgUrl : "https:" + imgUrl);
            InputStream in = url.openStream();
            String fileName = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
            File file = new File("src/main/resources/static/imgs" + File.separator + fileName);

            // 파일 저장
            try (FileOutputStream out = new FileOutputStream(file)) {
                byte[] buffer = new byte[2048];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return "/imgs/" + file.getName();

        } catch (Exception e) {
            return null;
        }
    }


}
