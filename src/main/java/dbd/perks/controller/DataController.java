package dbd.perks.controller;

import dbd.perks.crawler.DataCrawler;
import dbd.perks.dto.WholeDataFindResponse;
import dbd.perks.service.DataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DataController {

    private final DataService dataService;
    private final DataCrawler dataCrawler;

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
    };

}