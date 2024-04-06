import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class Crawler implements Runnable {
    private final UrlDiscovery Queue;
    private final VisitedPageHandler visitedPageHandler;

    private final Parser parser;

    private final int THRESHOLD;

    public Crawler(UrlDiscovery Queue, VisitedPageHandler visitedPageHandler, int maxDepth) {
        this.Queue = Queue;
        this.visitedPageHandler = visitedPageHandler;
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
                int queueSize = Queue.getSize();

                while (queueSize-- != 0) {
                    String seed = Queue.getUrl();

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
            visitedPageHandler.markPage(url);
            for (Element link : doc.select("a[href]")) {
                String new_link = link.absUrl("href");
                if (!visitedPageHandler.isVisitedPage(new_link)) {
                    Queue.addUrl(new_link);
                }
            }

        } else Queue.addUrl(url);
    }
}
