import org.junit.Test;
import sun.jvm.hotspot.debugger.Page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class WebCrawlerTest {
    PageCrawler pageCrawler = new PageCrawler("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/introduction.html");
    WebCrawler webCrawler = new WebCrawler("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/");

    @Test
    public void testGetLinksFromDomain() throws Exception {
        List<String> links = new ArrayList<>();
        links.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/index.html");
        links.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/surgery.html");
        links.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/introduction.html");
        links.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/non-surgery.html");
        links.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/technical.html");
        links.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/references.html");
        links.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/howitworks.html");

        pageCrawler.getLinksFromDomain();

        for(String str : links) {
            assert(pageCrawler.getLinks().contains(str));
        }
    }

    @Test
    public void testGetMedia() throws Exception {
        List<String> media = new ArrayList<>();
        media.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/js/vendor/jquery.min.js");
        media.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/js/foundation.min.js");
        media.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/js/vendor/what-input.min.js");
        media.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/11%20snapchat.mp4");
        media.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/Trimmed%20Video%20Layar.mp4");
        media.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/img/5480085507_03f4fc9cdb_b.jpg");

        pageCrawler.getMedia();

        for(String str : media) {
            assert(pageCrawler.getAssets().contains(str));
        }
    }

    @Test
    public void testGetImports() throws Exception {
        List<String> imports = new ArrayList<>();
        imports.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/css/foundation.css");
        imports.add("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/css/app.css");

        pageCrawler.getImports();

        for(String str : imports) {
           assert(pageCrawler.getAssets().contains(str));
        }
    }

    @Test
    public void testWebCrawler() throws IOException {
        List<PageCrawler> result = new ArrayList<>();
        result.add(new PageCrawler("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/index.html"));
        result.add(new PageCrawler("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/surgery.html"));
        result.add(new PageCrawler("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/introduction.html"));
        result.add(new PageCrawler("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/non-surgery.html"));
        result.add(new PageCrawler("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/technical.html"));
        result.add(new PageCrawler("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/references.html"));
        result.add(new PageCrawler("https://www.doc.ic.ac.uk/project/2015/163/g1516332/web/howitworks.html"));

        webCrawler.crawl(new PageCrawler(WebCrawler.domain));

        for(PageCrawler crawler : webCrawler.getPageCrawlers()) {
            result.contains(crawler);
        }
    }
}