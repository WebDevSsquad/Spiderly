public class CrawlerSystem {
    private final int THRESHOLD = 2;

    public void main(String[] args) {
        int threadCount = Integer.parseInt(args[0]);


        System.out.println(threadCount);
        URLManager urlManager = new URLManager();

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Crawler(urlManager, THRESHOLD));
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            urlManager.saveState();
            System.out.println("State Saved Successfully");
            Runtime.getRuntime().halt(0);
        }));


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
        System.out.println("All threads have finished execution.");
    }
}
