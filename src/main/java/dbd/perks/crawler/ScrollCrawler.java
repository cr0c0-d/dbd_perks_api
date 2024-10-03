package dbd.perks.crawler;

import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScrollCrawler {

    @Value("${proxy.server.url}")
    private String proxyServerUrl;

    private final ProxyCrawler proxyCrawler;

    public Document getDocumentByScrollCrawler(String url) {
        return getDocumentByFlaskCrawler(url);
//        // 프록시 서버 Url 리스트
//        List<String> proxyServerUrlList = proxyCrawler.getProxyServerUrlList();
//
//        for(String proxyServerUrl : proxyServerUrlList) {
//
//            String os = System.getProperty("os.name").toLowerCase();
//
//            ChromeOptions options = new ChromeOptions();
//
//            WebDriver driver = null;
//
//            if (os.contains("win")) {
//                System.setProperty("webdriver.chrome.driver", "driver/chromedriver.exe");
//            } else {
//
//                // ChromeDriver 경로 설정
//                String driverPath = Paths.get("/home/chromedriver-linux64/chromedriver").toString();
//                System.setProperty("webdriver.chrome.driver", driverPath);
//
//            }
//
//
//            // 사용자 인터페이스 없이 백그라운드에서 실행
//            options.addArguments("--headless");
//
//            // 샌드박스 기능 비활성화
//            options.addArguments("--no-sandbox");
//
//            // 공유메모리 사용 비활성화
//            options.addArguments("--disable-dev-shm-usage");
//
//            // javascript 활성화
//            options.addArguments("--enable-javascript");
//
//            // 자동화 감지 회피
//            options.addArguments("--disable-blink-features=AutomationControlled");
//
//            // User-Agent 지정으로 봇 인식 회피
//            options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
//
//            // 인증서 무시
//            options.addArguments("--ignore-certificate-errors");
//
//            // 프록시 서버 추가
//            Proxy proxy = new Proxy();
//            if(proxyServerUrl.startsWith("https:")) {
//                proxy.setSslProxy(proxyServerUrl);
//            } else {
//                proxy.setHttpProxy(proxyServerUrl);
//            }
//
//            options.setProxy(proxy);
//
//            driver = new ChromeDriver(options);
//
//            try {
//                driver.get(url);
//
////            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds((int) (Math.random() * 1000)));
////
////            try {
////                wait.until(ExpectedConditions.visibilityOfElementLocated(By.id(url.contains("namu.wiki") ? "app" : "content")));
//
//
//                // 스크롤 내리기
//                JavascriptExecutor js = (JavascriptExecutor) driver;
//                for (int i = 0; i < 2; i++) { //  반복
//                    js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
//                    Thread.sleep(2000); // 로딩 대기
//                }
//
//
//                // 페이지의 HTML 소스 가져오기
//                String pageSource = driver.getPageSource();
//
//                // JSoup으로 HTML 파싱
//                Document doc = Jsoup.parse(pageSource);
//
//                driver.quit();
//
//                if((url.contains("namu.wiki") && doc.getElementById("app") != null) || doc.getElementById("content") != null) {
//                    return doc;
//                } else {
//                    System.out.println("*********** 내용 찾을 수 없음, 이하 document 내용 ************");
//                    System.out.println(doc.text());
//                    System.out.println("*********** 내용 찾을 수 없음, document 내용 끝 ************");
//                }
//
//
//            } catch (Exception e) {
//                System.out.println("*********** 시간 초과, 이하 document 내용 ************");
//                System.out.println(driver.getPageSource());
//                System.out.println("*********** 시간 초과, document 내용 끝 ************");
//
//                e.printStackTrace();
//                driver.quit();
//            }
//        }
//        return null;
    }

    private Document getDocumentByFlaskCrawler(String url) {
        RestTemplate restTemplate = new RestTemplate();

        String pythonCrawlUrl = "http://localhost:5001/getDocument/" + url;

        String html = restTemplate.getForObject(pythonCrawlUrl, String.class);

        // Jsoup을 사용하여 HTML을 Document 객체로 변환
        Document document = Jsoup.parse(html);
        return document;
    }

}


