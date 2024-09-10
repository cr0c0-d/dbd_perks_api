package dbd.perks.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.time.Duration;

@Service
public class ScrollCrawler {
    public Document getDocumentByScrollCrawler(String url) {
        String os = System.getProperty("os.name").toLowerCase();
        ChromeOptions options = new ChromeOptions();
        if(os.contains("win")) {
            System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
        } else {

            // ChromeDriver 경로 설정
            String driverPath = Paths.get("/home/chromedriver-linux64/chromedriver").toString();
            System.setProperty("webdriver.chrome.driver", driverPath);

            // 사용자 인터페이스 없이 백그라운드에서 실행
            options.addArguments("--headless");

            // 샌드박스 기능 비활성화
            options.addArguments("--no-sandbox");

            // 공유메모리 사용 비활성화
            options.addArguments("--disable-dev-shm-usage");

            // javascript 활성화
            options.addArguments("--enable-javascript");

            // 자동화 감지 회피
            options.addArguments("--disable-blink-features=AutomationControlled");

            // User-Agent 지정으로 봇 인식 회피
            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");


        }

        WebDriver driver = new ChromeDriver(options);

        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(url.contains("namu.wiki") ? "app" : "content")));
        } catch (TimeoutException e) {
            System.out.println("*********** 시간 초과, 이하 document 내용 ************");
            System.out.println(driver.getPageSource());
            System.out.println("*********** 시간 초과, document 내용 끝 ************");

            e.printStackTrace();
        }

        try {
            // 스크롤 내리기
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (int i = 0; i < 2; i++) { //  반복
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(2000); // 로딩 대기
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
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
