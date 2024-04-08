package src;
import java.io.IOException;

public class IndexerSystem {
    public static void main(String[] args) throws IOException {
        DocumentManager documentManager = new DocumentManager();
        int threadCount = Integer.parseInt(args[0]);
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Indexer(documentManager));
        }
        System.out.println(threadCount);
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
        System.out.println(documentManager.invertedIndex);
        System.out.println(documentManager.DF);
        System.out.println(documentManager.TF);
        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);
        System.out.println("All threads have finished execution.");
    }
}
