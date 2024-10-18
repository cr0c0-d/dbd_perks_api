package dbd.perks.controller;

import dbd.perks.crawler.DataCrawler;
import dbd.perks.dto.WholeDataFindResponse;
import dbd.perks.service.DataService;
import dbd.perks.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;

@RestController
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;
    private final DataCrawler dataCrawler;

    private final EmailService emailService;

    @Value("${img.path}")
    String imgPath;

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

    @GetMapping("/imgs/{fileName}")
    public ResponseEntity<String> getImageBase64(@PathVariable String fileName) {
        try {
            File file = new File(imgPath + File.separator+ fileName);
            byte[] fileContent = Files.readAllBytes(file.toPath());
            String base64String = Base64.getEncoder().encodeToString(fileContent);
            return ResponseEntity.ok("data:image/jpeg;base64," + base64String);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
