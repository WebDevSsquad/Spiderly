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

//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
//
//        executor.scheduleAtFixedRate(() -> {
//            UrlDiscovery.saveQueueToFile(Queue.getQueue());
//            VisitedPageHandler.saveVisitedPages(visitedPageHandler.getHashedPage());
//            System.out.println("State Saved");
//        }, 5, intervalSeconds, TimeUnit.SECONDS);

        while (true) {
            try {
                int queueSize = urlManager.getUrlFrontier().getUrlQueueSize();

                while (queueSize-- != 0) {
                    String seed = urlManager.getUrlFrontier().getNextURL();

                    if (seed == null) break;

                    crawl(seed);
                }

                if (++depth >= THRESHOLD) return;

                //Thread.sleep(1000);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void crawl(String url) throws IOException {
        Document doc = parser.parse(url);

        if (doc != null) {
            // handled in UrlFrontier addUrl()
            urlManager.getUrlFrontier().markPage(url);
            for (Element link : doc.select("a[href]")) {
                String new_link = link.absUrl("href");
                if (! urlManager.getUrlFrontier().isVisitedPage(new_link)) {
                    urlManager.getUrlFrontier().addURL(new_link,0);
                }
            }

        } else urlManager.getUrlFrontier().addURL(url,0);
    }
}
