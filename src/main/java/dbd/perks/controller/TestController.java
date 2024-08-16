package dbd.perks.controller;

import dbd.perks.crawler.DataCrawler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final DataCrawler dataCrawler;

    @GetMapping("/api/crawlerTest")
    public void test() {
        dataCrawler.getKillerDocUrlList();
    }

}
