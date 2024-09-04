package dbd.perks.controller;

import dbd.perks.crawler.DataCrawler;
import dbd.perks.dto.WholeDataFindResponse;
import dbd.perks.service.DataService;
import dbd.perks.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;
    private final DataCrawler dataCrawler;

    private final EmailService emailService;


    // 매일 자정에 실행
    @Scheduled(cron = "0 0 0 * * *")
    @GetMapping("/api/runCrawlerAll")
    public void runCrawlerAll() {
        dataCrawler.runCrawlerAll();
    }

    @GetMapping("/api/runKillerCrawler")
    public void runKillerCrawler() {
        dataCrawler.runKillerCrawler();
    }

    @GetMapping("/api/runSurvivorCrawler")
    public void runSurvivorCrawler() {
        dataCrawler.runSurvivorCrawler();
    }

    @GetMapping("/api/runOfferingCrawler")
    public void runOfferingCrawler() {
        dataCrawler.runOfferingCrawler();
    }

    @GetMapping("/api/runCommonPerksCrawler")
    public void runCommonPerksCrawler() {
        dataCrawler.runCommonPerksCrawler();
    }

    @GetMapping("/api/getData")
    public ResponseEntity<WholeDataFindResponse> getData() {
        return ResponseEntity.ok()
                .body(dataService.getData());
    }

    @GetMapping("/api/mailTest")
    public void sendMail() {
        emailService.sendEmail("테스트");
    }

}
