package dbd.perks.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.stereotype.Service;

@Service
public class ScrollCrawler {
    public Document getDocumentByScrollCrawler(String url) {
        System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        driver.get(url);

        // 스크롤 내리기
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < 2; i++) { //  반복
            js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            try {
                Thread.sleep(2000); // 로딩 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // 페이지의 HTML 소스 가져오기
        String pageSource = driver.getPageSource();

        // JSoup으로 HTML 파싱
        Document doc = Jsoup.parse(pageSource);

        driver.quit();

        return doc;
//        Element contents = doc.selectFirst("hr").parent(); // 선택자에 맞는 첫 번째 요소 선택
//
//        System.out.println(contents);
//
//        driver.quit();
    }
}
