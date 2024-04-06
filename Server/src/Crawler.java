import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Crawler implements Runnable {
    private final URLManager urlManager;

    private final Parser parser;

    private final int THRESHOLD;

    public Crawler(URLManager urlManager, int maxDepth) {
        this.urlManager = urlManager;
        parser = new Parser();
        THRESHOLD = maxDepth;
    }

    @Override
    public void run() {
        int intervalSeconds = 10, depth = 0; // Specify the interval in seconds
        URLFrontier urlFrontier = urlManager.getUrlFrontier();
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//
//        executor.scheduleAtFixedRate(() -> {
//            UrlDiscovery.saveQueueToFile(Queue.getQueue());
//            VisitedPageHandler.saveVisitedPages(visitedPageHandler.getHashedPage());
//            System.out.println("State Saved");
//        }, 5, intervalSeconds, TimeUnit.SECONDS);
        while (true) {
            try {
                int queueSize = urlFrontier.getUrlQueueSize();

                while (queueSize-- != 0) {
                    URLPriorityPair seed = urlFrontier.getNextURL();

                    if (seed == null) break;
                    if (seed.getDepth() >= THRESHOLD) return;
                    crawl(seed);
                }



                //Thread.sleep(1000);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void crawl(URLPriorityPair urlPriorityPair) throws IOException {
        Document doc = parser.parse(urlPriorityPair.getUrl());
        URLFrontier urlFrontier = urlManager.getUrlFrontier();

        if (doc != null) {
            // handled in UrlFrontier addUrl()
            //urlManager.getUrlFrontier().markPage(url);
            for (Element link : doc.select("a[href]")) {
                String new_link = link.absUrl("href");
                if (!urlManager.validURL(new_link)) {
                    Logger.log("Invalid Link: " + new_link);
                    continue;
                };
                String normalized_url = urlManager.normalizeURL(new_link);
                if (! urlFrontier.isVisitedPage(normalized_url)) {
                    urlManager.handleURL(normalized_url, urlPriorityPair.getDepth() + 1);
                }
            }

        } else urlManager.handleURL(urlPriorityPair.getUrl(), urlPriorityPair.getDepth() + 1);
    }
}