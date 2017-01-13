import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageCrawler {
    //link to the page that we need to crawl
    private String link;
    private Document htmlDoc;

    private List<String> subLinks = new ArrayList<>();
    private List<String> assets =  new ArrayList<>();
    private static final int OK_STATUS_CODE = 200;

    public PageCrawler(String link) {
        WebClient webClient = new WebClient(BrowserVersion.getDefault());
        //enable JavaScript in web client
        webClient.getOptions().setJavaScriptEnabled(true);

        this.link = link;

        try {
            //disable validation for TLS Certificate to allow connection
            Connection connection = Jsoup.connect(link).validateTLSCertificates(false);

            //make sure response from the connection is an html document that has 200 status code
            this.htmlDoc = connection.get();
            assert (connection.response().statusCode() == OK_STATUS_CODE);
            assert (connection.response().contentType().contains("text/html"));
        } catch (IOException e) {
            System.out.println("Cannot establish connection to " + link);
        }
    }

    public List<String> getLinks() {
        return subLinks;
    }

    public void getLinksAssets() throws IOException {
        WebCrawler.visitedPages.add(link);

        getStaticContent();

        //traverse the sublist
        for(String l : subLinks) {
            if(!WebCrawler.visitedPages.contains(l)) {
                WebCrawler.visitedPages.add(l);
                (new PageCrawler(l)).getLinksAssets();
            }
        }
    }

    private void getStaticContent() {
        getLinksFromDomain();
        getAssetsFromLink();
        printLinkAssets();
    }

    public void printLinkAssets() {
        System.out.println("\t{");
        //print link from page
        System.out.println("\t\t\"url\": \"" + link + "\",");
        System.out.println("\t\t\"assets\": [");

        //print assets from page
        for(String a : assets) {
            System.out.print("\t\t\t\"" + a + "\"");

            //add a comma if the element a is not the last one in assets
            if(assets.indexOf(a) != assets.size() - 1) {
                System.out.println(",");
            }
        }

        System.out.println("\t\t]");
        System.out.println("\t}");
    }

    public void getLinksFromDomain() {
        Elements linksOnPage = htmlDoc.select("a[href]");

        for(Element li : linksOnPage) {
            String l = li.absUrl("href");

            //make sure the extracted link belongs to the domain and is not a subdomain
            if(l.contains(WebCrawler.domain) && !l.contains("#") && !subLinks.contains(l)) {
                String[] removeDom = l.split(WebCrawler.domain);

                if(removeDom.length > 0) {
                    String linkWithRemovedDom = removeDom[1];

                    if(linkWithRemovedDom.contains(".") && !linkWithRemovedDom.contains("html")) {
                        //when l is a link to a file
                        assets.add(l);
                    } else {
                        //remove the case when the link redirects to a section in the same page
                        subLinks.add(l);
                    }
                }

            }
        }
    }

    public void getAssetsFromLink() {
        getMedia();
        getImports();
    }

    public void getMedia() {
        //get all media in the page
        Elements src = htmlDoc.select("[src]");

        for(Element m : src) {
            String ms = m.absUrl("src");

            //make sure the extracted link belongs to the domain and is not a subdomain
            if(ms.contains(WebCrawler.domain)) {
                assets.add(ms);
                //System.out.println(ms);
            }
        }
    }

    public void getImports() {
        //get all imports
        Elements href = htmlDoc.select("link[href]");

        for(Element i : href) {
            String is = i.absUrl("href");

            //make sure the extracted link belongs to the domain and is not a subdomain
            if(is.contains(WebCrawler.domain)) {
                String[] removedDom = is.split(WebCrawler.domain);

                //make sure that the import is a file
                if(removedDom.length > 0 && removedDom[1].contains(".")) {
                    assets.add(is);
                    //System.out.println(is);
                }
            }
        }
    }
}
