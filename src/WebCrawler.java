import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
    A WebCrawler has a list of page crawlers, which are used in testing specifically. It aslo keeps a list of pages that
    it has visited to make sure that a page will not be crawled twice. Moreover, when you instantiate a web crawer, the
    url that is given to the web crawler will be assumed to be the home page
 */
public class WebCrawler {
    private List<PageCrawler> pageCrawlers = new ArrayList<>();
    public static List<String> visitedPages = new ArrayList<>();
    public static String domain;

    public WebCrawler(String link) {
        domain = link;
    }

    public static void main(String[] args) throws IOException {
        WebCrawler crawler = new WebCrawler("https://gocardless.com/");
        crawler.crawlDom();
    }

    public void crawlDom() throws IOException {
        //crawl through the homepage
        System.out.println("[");
        crawl(new PageCrawler(domain));
        System.out.println("]");
    }

    /*
        crawl(pageCrawler) crawls through the current page it is on and also the links that are found on the page
     */
    public void crawl(PageCrawler pageCrawler) throws IOException {
        pageCrawler.getLinksAssets();
        pageCrawlers.add(pageCrawler);

        //crawl every link that is found on the current page
        for(String l : pageCrawler.getLinks()) {
            if(!visitedPages.contains(l)) {
                visitedPages.add(l);
                crawl(new PageCrawler(l));
            }
        }
    }

    public List<PageCrawler> getPageCrawlers() {
        return pageCrawlers;
    }
}
