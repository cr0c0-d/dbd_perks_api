package dbd.perks.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProxyCrawler {

    private final String freeProxyServerUrl = "https://free-proxy-list.net/";

    List<String> getProxyServerUrlList() {
        List<String> proxyUrlList = new ArrayList<>();

        try {
            Document document = Jsoup.connect(freeProxyServerUrl).get();
            Elements proxyServerList = document.select("tbody tr");
            for(Element proxyServer : proxyServerList) {
                Elements tds = proxyServer.select("td");
                if(tds.size() == 8) {
                    if (tds.get(3).ownText().equals("South Korea")) {
                        proxyUrlList.add(
                                (tds.get(6).ownText().equals("yes") ? "https://" : "http://")
                                + tds.get(0).ownText()
                                + ":"
                                + tds.get(1).ownText());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return proxyUrlList;
    }
}
