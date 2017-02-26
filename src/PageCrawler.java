import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
    PageCrawler is a helper class to WebCrawler, it crawls the webstie in 3 steps:
    1. establish a connection to the given url and saved the html file from that url to htmlDoc
    2. get links to another website by finding <a href...> elem. because we do not want links that belong to the subdomains
       of the website, we only add links which contain the domain to subLinks
    3. get assets of the website by finding elements with attributes src and <link ...> elements. These links are automatically
       added to assets so long as they have form (WebCrawler.domain + something + "." + something_else)
 */
public class PageCrawler {
    //link to the page that we need to crawl
    private String link;
    private Document htmlDoc;

    private List<String> subLinks = new ArrayList<>();
    private List<String> assets =  new ArrayList<>();
    private static final int OK_STATUS_CODE = 200;

    public PageCrawler(String link) {
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

    public List<String> getAssets() {
        return assets;
    }

    public String getLink() {
        return link;
    }


    public void getLinksAssets() throws IOException {
        if(htmlDoc != null) {
            getStaticContent();
        }
    }

    private void getStaticContent() {
        getLinksFromDomain();
        getAssetsFromLink();
        printLinkAssets();
    }

    /*
        Get all links - elements that have <a href .. > tag
     */
    public void getLinksFromDomain() {
        Elements linksOnPage = htmlDoc.select("a[href]");

        for(Element li : linksOnPage) {
            //remove the redirection to a section on a page entirely, only keeping the link
            String l = li.absUrl("href").split("#")[0];

            //make sure the extracted link belongs to the domain and is not a subdomain
            if(l.contains(WebCrawler.domain) && !subLinks.contains(l)) {
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

    /*
        Get all elements with src attribute
     */
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

    /*
        Get all imports element <link ... >
     */
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
                }
            }
        }
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

    /*
        Overriding equals method to aid testing. Two PageCrawlers are equals if and only if they have the same link, list
        of assets and subLinks.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PageCrawler) {
            PageCrawler p = ((PageCrawler) obj);

            if(link.equals(p.getLink())) {
                for(String l : subLinks) {
                    if(!p.getLinks().contains(l)) {
                        return false;
                    }
                }

                for(String a : assets) {
                    if(!p.getAssets().contains(a)) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
