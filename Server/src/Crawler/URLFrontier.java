package Crawler;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class URLFrontier {
    // URL Queue Handler's data members
    private final PriorityBlockingQueue<URLPriorityPair> urlQueue;

    private final ConcurrentHashMap<String, Boolean> hashedURLs;

    private final ConcurrentLinkedQueue<Entry> documents;

    // Visited Pages Handler's data members
    private final ConcurrentHashMap<String, Boolean> hashedPage;

    private final MongoCollection<Document> visitedPagesCollection;
    private final MongoCollection<Document> visitedLinksCollection;



    public URLFrontier(PriorityBlockingQueue<URLPriorityPair> urlQueue, MongoCollection<Document> visitedPagesCollection, MongoCollection<Document> visitedLinksCollection) {
        this.urlQueue = urlQueue;
        this.visitedPagesCollection = visitedPagesCollection;
        this.visitedLinksCollection = visitedLinksCollection;
        hashedURLs = new ConcurrentHashMap<>();
        hashedPage = new ConcurrentHashMap<>();
        documents = new ConcurrentLinkedQueue<>();
    }

    public boolean isEmpty() {
        return urlQueue.isEmpty();
    }

    public URLPriorityPair getNextURL() {
        return urlQueue.poll();
    }

    public boolean addURL(String url, int priority, int depth) {
        URLPriorityPair newURL = new URLPriorityPair(url, priority, depth);
        urlQueue.offer(newURL);
        return true;
    }

    public void addDocument(String doc,String title ,String url) {
        documents.offer(new Entry(doc,title,url));
    }

    private String getHash(String url) {
        // 1. Compress URL
        // 2.Implement Hash Function
        return DigestUtils.md5Hex(url).toUpperCase();
    }

    public PriorityBlockingQueue<URLPriorityPair> getQueue() {
        return urlQueue;
    }

    public static void saveQueueToFile(PriorityBlockingQueue<URLPriorityPair> originalQueue, MongoCollection<Document> seedCollection) {
        for (URLPriorityPair pair : originalQueue) {
            // Create a document representing the URLPriorityPair object
            Document document = new Document("url", pair.getUrl())
                    .append("priority", pair.getPriority())
                    .append("depth", pair.getDepth());
            // Insert the document into the collection
            seedCollection.insertOne(document);
        }
    }

    public static PriorityBlockingQueue<URLPriorityPair> loadQueueFromFile(MongoCollection<Document> seedCollection) {
        PriorityBlockingQueue<URLPriorityPair> queue = new PriorityBlockingQueue<>();
        // Query documents from the collection
        FindIterable<Document> results = seedCollection.find();

        // Iterate over the query results
        for (Document doc : results) {
            String url = doc.getString("url");
            int depth = doc.getInteger("depth");
            int priority = doc.getInteger("priority");
            URLPriorityPair pair = new URLPriorityPair(url, priority, depth);
            queue.offer(pair);
        }
        // Delete the documents from the collection
        seedCollection.deleteMany(new Document());
        return queue;
    }

    public void markURL(String url) {
        String md5Hex = getHash(url);
        hashedURLs.put(md5Hex, true);
    }

    public boolean isVisitedURL(String url) {
        String md5Hex = getHash(url);
        Document criteria = new Document("hash", md5Hex);
        long count = visitedLinksCollection.countDocuments(criteria);
        return hashedURLs.containsKey(md5Hex) || count != 0;
    }

    public void markPage(String url) {
        System.out.println("add");
        String md5Hex = getHash(url);
        hashedPage.put(md5Hex, true);
    }

    public boolean isVisitedPage(String doc) {
        String md5Hex = getHash(doc);
        Document criteria = new Document("hash", md5Hex);
        long count = visitedPagesCollection.countDocuments(criteria);
        return hashedPage.containsKey(md5Hex) || count != 0;
    }

    public static void saveVisitedPages(ConcurrentHashMap<String, Boolean> hashedMap, MongoCollection<Document> collection) {
        // Iterate over the ConcurrentHashMap and insert URL strings into the database
        for (String url : hashedMap.keySet()) {
            Document document = new Document("hash", url);
            // Insert the document into the collection
            collection.insertOne(document);
        }
    }

    public ConcurrentLinkedQueue<Entry> getDocuments() {
        return documents;
    }

    public static void saveDocuments(ConcurrentLinkedQueue<Entry>documents, MongoCollection<Document> documentsCollection) {
            while(!documents.isEmpty()){

                Entry entry = documents.poll();
                Document add = new Document("title", entry.getTitle())
                        .append("url", entry.getLink())
                        .append("document", entry.getContent());
                documentsCollection.insertOne(add);
            }
    }

    public ConcurrentHashMap<String, Boolean> getHashedURLs() {
        return hashedURLs;
    }

    public ConcurrentHashMap<String, Boolean> getHashedPage() {
        return hashedPage;
    }

    public int getHashedPageSize() {
        return hashedPage.size();
    }


    public int getUrlQueueSize() {
        return urlQueue.size();
    }
}

class URLPriorityPair implements Comparable {
    private final String url;
    private final int priority;
    private final int depth;

    public String getUrl() {
        return url;
    }

    public int getPriority() {
        return priority;
    }

    public int getDepth() {
        return depth;
    }

    public URLPriorityPair(String url, int priority, int depth) {
        this.url = url;
        this.priority = priority;
        this.depth = depth;
    }

    @Override
    public int compareTo(Object other) {
        // Compare by depth first
        if (this.depth != ((URLPriorityPair) other).depth) {
            return Integer.compare(this.depth, ((URLPriorityPair) other).depth);
        }
        return Integer.compare(this.priority, ((URLPriorityPair) other).priority);
    }
}
class Entry {
    private final String title;
    private final String content;
    private final String link;

    public Entry(String title, String content, String link) {
        this.title = title;
        this.content = content;
        this.link = link;
    }
    // Getters and setters
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }
    public String getLink() {
        return link;
    }

}