package Crawler;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

public class CrawlerSystem {
    private final int THRESHOLD = 3;
    private final String connectionString = "mongodb://localhost:27017";
    private final String DATABASE_NAME = "Crawler";

    private long start;

    private long end;

    public void main(String[] args) {
        int threadCount = Integer.parseInt(args[0]);


        System.out.println(threadCount);

        //----------------------------------------------------Database--------------------------------------------------

        //initialise the database connection
        // Connect to MongoDB server
        MongoClient mongoClient = MongoClients.create(connectionString);

        // Access a MongoDB database
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);

        // Get a collection from the database
        MongoCollection<Document> seedCollection = database.getCollection("seed");
        MongoCollection<Document> visitedPagesCollection = database.getCollection("visited_pages");
        MongoCollection<Document> visitedLinksCollection = database.getCollection("visited_urls");
        MongoCollection<Document> documentsCollection = database.getCollection("documents");
        MongoCollection<Document> disallowedUrlsCollection = database.getCollection("disallowed_urls");
        //--------------------------------------------------------------------------------------------------------------

        URLManager urlManager = new URLManager(seedCollection,visitedPagesCollection,visitedLinksCollection,documentsCollection,disallowedUrlsCollection);

        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Crawler(urlManager, THRESHOLD));
        }


        // save the state of the crawler when finish or interrupted
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            synchronized (urlManager) {
                try {
                    urlManager.saveState();
                    System.out.println("State Saved Successfully");
                } catch (Exception e) {
                    System.err.println(STR."Error while saving state: \{e.getMessage()}");
                }

                // Close MongoDB client
                try {
                    mongoClient.close();
                    System.out.println("MongoDB client closed successfully.");
                } catch (Exception e) {
                    System.err.println(STR."Error while closing MongoDB client: \{e.getMessage()}");
                }
            }
            end = System.currentTimeMillis();
            System.out.println((end - start) / 1000);
            System.out.println("All threads have finished execution.");

        }));


        for (Thread thread : threads) {
            thread.start();
        }
        start = System.currentTimeMillis();

        // Wait for each thread to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);
        System.out.println("All threads have finished execution.");
    }
}
