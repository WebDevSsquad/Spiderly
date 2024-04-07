public class CrawlerSystem {
    private final int THRESHOLD = 40;

    public void main(String[] args) {
        int threadCount = Integer.parseInt(args[0]);


        System.out.println(threadCount);
        URLManager urlManager = new URLManager();

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Crawler(urlManager, THRESHOLD));
        }
        for (Thread thread : threads) {
            thread.start();
        }
        long start = System.currentTimeMillis();

        // Wait for each thread to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);
        urlManager.saveState();
        System.out.println("All threads have finished execution.");
    }
}
