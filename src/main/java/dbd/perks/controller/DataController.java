package dbd.perks.controller;

import dbd.perks.crawler.DataCrawler;
import dbd.perks.dto.WholeDataFindResponse;
import dbd.perks.dto.WholeDataTransferRequest;
import dbd.perks.service.DataService;
import dbd.perks.service.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

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
    public ResponseEntity<Resource> getImageFile(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(imgPath).resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/data/migration")
    public ResponseEntity<String> dataMigration(@RequestBody WholeDataTransferRequest wholeDataTransferRequest, HttpServletRequest httpServletRequest) {
        boolean result = dataService.dataMigration(wholeDataTransferRequest, httpServletRequest);
        return result ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
