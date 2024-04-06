public class CrawlerSystem {
    private final int THRESHOLD = 3;

    public void main(String[] args) {
        int threadCount = Integer.parseInt(args[0]);


        System.out.println(threadCount);

        UrlDiscovery Queue = new UrlDiscovery(UrlDiscovery.loadQueueFromFile());

        VisitedPageHandler visitedPageHandler = new VisitedPageHandler(VisitedPageHandler.loadVisitedPages());

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Crawler(Queue, visitedPageHandler, THRESHOLD));
        }
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for each thread to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        UrlDiscovery.saveQueueToFile(Queue.getQueue());
        VisitedPageHandler.saveVisitedPages(visitedPageHandler.getHashedPage());
        System.out.println("All threads have finished execution.");
    }
}
