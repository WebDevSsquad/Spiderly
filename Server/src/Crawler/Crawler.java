package Crawler;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Crawler class represents a web crawler that crawls web pages and extracts information.
 */
public class Crawler implements Runnable {

    // Logging
    private static final Logger logger = Logger.getLogger(Crawler.class.getName());
    private final URLManager urlManager;

    private final Parser parser;

    private final int THRESHOLD, DOCUMENTS_THRESHOLD;

    /**
     * Initializes a new Crawler object with the specified URLManager and maximum depth.
     *
     * @param urlManager The URLManager to handle URLs and frontier.
     * @param maxDepth   The maximum depth for crawling.
     */
    public Crawler(URLManager urlManager, int maxDepth, int maxDocCount) {
        this.urlManager = urlManager;
        parser = new Parser();
        THRESHOLD = maxDepth;
        DOCUMENTS_THRESHOLD = maxDocCount;
    }

    @Override
    public void run() {
        URLFrontier urlFrontier = urlManager.getUrlFrontier();
        while (true) {
            try {
                int queueSize = urlFrontier.getUrlQueueSize();
                while (queueSize-- != 0) {
                    URLPriorityPair seed = urlFrontier.getNextURL();

                    if (seed == null) break;
                    if (seed.depth() >= THRESHOLD) return;
                    crawl(seed);

                    if (urlFrontier.getHashedPageSize() >= DOCUMENTS_THRESHOLD) return;

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Crawls a URL and extracts information from the web page.
     *
     * @param urlPriorityPair The URLPriorityPair object representing the URL to crawl.
     * @throws IOException If an I/O error occurs.
     */
    public void crawl(URLPriorityPair urlPriorityPair) throws IOException {
        String url = urlPriorityPair.url().toString();

        URLFrontier urlFrontier = urlManager.getUrlFrontier();

        Document doc = parser.parse(urlPriorityPair, urlFrontier);


        if (doc != null) {
            String docText = doc.text();
            String docHtml = doc.html();
            String title = doc.title();

            ArrayList<String> headerArray = new ArrayList<>();
            ArrayList<String> titleArray = new ArrayList<>();
            ArrayList<String> textArray = new ArrayList<>();

            urlManager.getHTMLTags(docHtml, headerArray, titleArray, textArray);

            // Check if the url was crawled or the document is a duplicate
            if (!urlManager.checkURL(url, docText)) return;

            // Set the url and document as crawled
            urlManager.visitURL(url, docText, headerArray, titleArray, textArray, title);

            // Loop on children links in the document
            for (Element link : doc.select("a[href]")) {
                String new_link = link.absUrl("href");
                if (!urlManager.handleChildUrl(new_link, url, 0, urlPriorityPair.depth() + 1)) {
                  //logger.log(Level.FINE, STR."Failed adding URL to frontier:\{new_link}");
                }
            }

        }
    }
}
