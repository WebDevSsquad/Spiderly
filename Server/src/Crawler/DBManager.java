package Crawler;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DBManager class provides methods for saving and loading data to/from MongoDB collections.
 */
public class DBManager {

    // Logging
    private static final java.util.logging.Logger logger = Logger.getLogger(DBManager.class.getName());

    /**
     * Saves a priority queue of URLs to the MongoDB collection.
     *
     * @param originalQueue  The priority queue of URLs to save.
     * @param seedCollection The MongoDB collection to save the URLs to.
     */
    public static void saveQueue(PriorityBlockingQueue<URLPriorityPair> originalQueue, MongoCollection<Document> seedCollection) {
        for (URLPriorityPair pair : originalQueue) {
            // Create a document representing the URLPriorityPair object
            Document document = new Document("url", pair.url().toString())
                    .append("priority", pair.priority())
                    .append("depth", pair.depth());
            // Insert the document into the collection
            seedCollection.insertOne(document);
        }
    }

    /**
     * Saves a map of crawled URLs to the MongoDB collection.
     *
     * @param hashedMap  The map of crawled URLs.
     * @param collection The MongoDB collection to save the crawled URLs to.
     */
    public static void saveCrawledPages(ConcurrentHashMap<String, Boolean> hashedMap, MongoCollection<Document> collection) {
        // Iterate over the ConcurrentHashMap and insert URL strings into the database
        for (String url : hashedMap.keySet()) {
            Document document = new Document("hash", url);
            // Insert the document into the collection
            collection.insertOne(document);
        }
    }

    /**
     * Saves visited pages to the MongoDB collection.
     *
     * @param hashedMap    The map of visited pages.
     * @param crawledUrls  The map indicating whether the pages have been crawled.
     * @param collection   The MongoDB collection to save the visited pages to.
     */
    public static void saveVisitedPages(ConcurrentHashMap<String, ArrayList<String>>hashedMap, ConcurrentHashMap<String,Boolean>crawledUrls, MongoCollection<Document> collection) {
        // Iterate over the ConcurrentHashMap and insert URL strings into the database
        for (String hash : hashedMap.keySet()) {
            ArrayList<String> parents = hashedMap.get(hash);
            boolean isCrawled = crawledUrls.get(hash) != null;
            // Create a document to represent the entry
            Document document = new Document("hash", hash).append("parents", parents).append("isCrawled",isCrawled);
            // Insert the document into the collection
            collection.insertOne(document);
        }
    }

    /**
     * Saves documents to the MongoDB collection.
     *
     * @param documents  The queue of documents to save.
     * @param collection The MongoDB collection to save the documents to.
     */
    public static void saveDocuments(ConcurrentLinkedQueue<Entry> documents, MongoCollection<Document> collection) {
        while (!documents.isEmpty()) {

            Entry entry = documents.poll();
            Document add = new Document("title", entry.title())
                    .append("url", entry.link())
                    .append("headerArray", entry.headerArray())
                    .append("titleArray", entry.titleArray())
                    .append("textArray", entry.textArray());

            collection.insertOne(add);
        }
    }

    /**
     * Saves URL disallowed paths to the MongoDB collection.
     *
     * @param hashedMap  The map of URL disallowed paths.
     * @param collection The MongoDB collection to save the disallowed paths to.
     */
    public static void saveUrlDisallowedPaths(ConcurrentHashMap<String, String> hashedMap, MongoCollection<Document> collection) {
        for (Map.Entry<String, String> entry : hashedMap.entrySet()) {
            String disallowed = entry.getKey();
            String host = entry.getValue();
            Document document = new Document("disallowed", disallowed).append("host", host);
            collection.insertOne(document);
        }
    }

    /**
     * Loads a priority queue of URLs from the MongoDB collection.
     *
     * @param seedCollection The MongoDB collection to load URLs from.
     * @return A priority queue of URLs loaded from the collection.
     */
    public static PriorityBlockingQueue<URLPriorityPair> loadQueueFromFile(MongoCollection<Document> seedCollection) {
        PriorityBlockingQueue<URLPriorityPair> queue = new PriorityBlockingQueue<>();

        // Query documents from the collection
        FindIterable<Document> results = seedCollection.find();

        // Iterate over the query results
        for (Document doc : results) {
            try {
                URI url = new URI(doc.getString("url"));
                int depth = doc.getInteger("depth");
                int priority = doc.getInteger("priority");
                URLPriorityPair pair = new URLPriorityPair(url, priority, depth);
                queue.offer(pair);
            } catch (URISyntaxException e) {
                //logger.log(Level.SEVERE, STR."Error creating URI: \{doc.getString("url")}", e);
            }
        }
        // Delete the documents from the collection
        seedCollection.deleteMany(new Document());
        return queue;
    }
}
