package Indexer;

/**
 * The IndexerSystem class serves as the entry point for the document indexing system.
 * It creates and manages multiple Indexer threads to concurrently index documents.
 */
public class IndexerSystem {

    /**
     * The main method of the IndexerSystem class.
     * It creates a DocumentManager instance, initializes Indexer threads based on the specified thread count,
     * starts the threads, waits for them to finish, and then prints the indexed data and execution time.
     *
     * @param args the command-line arguments; the first argument specifies the number of threads to use
     */
    public static void main(String[] args) {
        // Create a DocumentManager instance
        DocumentManager documentManager = new DocumentManager();

        // Parse the number of threads from the command-line arguments
        int threadCount = Integer.parseInt(args[0]);

        // Create an array to hold the Indexer threads
        Thread[] threads = new Thread[threadCount];

        // Initialize and start Indexer threads
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Indexer(documentManager));
        }

        // Print the number of threads
        System.out.println(threadCount);

        // Start the threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Record the start time
        long start = System.currentTimeMillis();

        // Wait for each thread to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Print the indexed data
        System.out.println(documentManager.invertedIndex);
        System.out.println(documentManager.DF);
        System.out.println(documentManager.TF);

        // Record the end time
        long end = System.currentTimeMillis();

        // Calculate and print the execution time
        System.out.println((end - start) / 1000);

        // Print a message indicating that all threads have finished execution
        System.out.println("All threads have finished execution.");
    }
}
