package dbd.perks.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ScrollCrawler {
    public void testCrawler() {
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        driver.get("https://namu.wiki/w/DEAD%20BY%20DAYLIGHT/%EC%83%9D%EC%A1%B4%EC%9E%90/%EC%98%A4%EB%A6%AC%EC%A7%80%EB%84%90%20%EC%BA%90%EB%A6%AD%ED%84%B0");

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

        // 필요한 데이터 추출
        WebElement element = driver.findElement(By.id("app"));

        // 페이지의 HTML 소스 가져오기
        String pageSource = driver.getPageSource();

        // JSoup으로 HTML 파싱
        Document doc = Jsoup.parse(pageSource);
        Element contents = doc.selectFirst("hr").parent(); // 선택자에 맞는 첫 번째 요소 선택

        System.out.println(contents);

        driver.quit();
    }
}
