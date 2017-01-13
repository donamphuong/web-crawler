import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WebCrawler {
    //private List<URL> urls = new ArrayList<>();
    public static List<String> visitedPages = new ArrayList<>();
    public static String domain;
    private String startPage;

    public WebCrawler(String link) {
        startPage = link;

        try {
            URL url = new URL(link);
            domain = url.getHost();

            //adding https:// or http:// or wwww. at the beginning of domain name
            domain = link.split(domain)[0] + domain;
        } catch (MalformedURLException e) {
            System.out.println("Invalid URL");
        }

    }

    public static void main(String[] args) throws IOException {
        WebCrawler crawler = new WebCrawler("http://moodfurniture.com.au/" +
                "");
        crawler.crawl();
    }


    public void crawl() throws IOException {
        //crawl through the homepage
        PageCrawler pageCrawler = new PageCrawler(startPage);
        pageCrawler.getLinksAssets();
    }
}
