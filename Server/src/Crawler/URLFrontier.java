package Crawler;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class URLFrontier {

    //queue that holds the future seed of the crawler.this queue loads at the beginning of the program from the database to get the previous state
    private final PriorityBlockingQueue<URLPriorityPair> urlQueue;
    //saves the parsed documents of the crawled page in addition to the title and url of that page
    private final ConcurrentLinkedQueue<Entry> documents;
    //saves the visited or crawled urls and its parents
    private final ConcurrentHashMap<String, ArrayList<String>> hashedURLs;
    private final ConcurrentHashMap<String, Boolean> crawledURLS;
    //saves the hostname and the disallowed url
    private final ConcurrentHashMap<String, String> DisallowedURLS;
    //save hashed parsed document of the page content
    private final ConcurrentHashMap<String, Boolean> hashedPage;

    // MongoDB Collections

    //Collection for crawled pages , contains the parsed doc hashed to overcome the problem of pages which have the same url
    private final MongoCollection<Document> visitedPagesCollection;
    //Collection for links that is crawled or in queue to prevent putting duplicates in queue and visiting the same url twice
    private final MongoCollection<Document> visitedLinksCollection;
    //Collection to save the disallowed urls from a certain host this prevent downloading the robots.txt file twice and visiting a disallowed url
    private final MongoCollection<Document> disallowedUrlsCollection;


    public URLFrontier(PriorityBlockingQueue<URLPriorityPair> urlQueue,
                       MongoCollection<Document> visitedPagesCollection,
                       MongoCollection<Document> visitedLinksCollection,
                       MongoCollection<Document> disallowedUrlsCollection) {
        this.urlQueue = urlQueue;
        this.visitedPagesCollection = visitedPagesCollection;
        this.visitedLinksCollection = visitedLinksCollection;
        this.disallowedUrlsCollection = disallowedUrlsCollection;
        crawledURLS = new ConcurrentHashMap<>();
        hashedURLs = new ConcurrentHashMap<>();
        hashedPage = new ConcurrentHashMap<>();
        documents = new ConcurrentLinkedQueue<>();
        DisallowedURLS = new ConcurrentHashMap<>();
    }

    //----------------------------------------Getters-------------------------------------------------------------------

    public URLPriorityPair getNextURL() {
        return urlQueue.poll();
    }

    private String getHash(String url) {
        return DigestUtils.md5Hex(url).toUpperCase();
    }

    public PriorityBlockingQueue<URLPriorityPair> getQueue() {
        return urlQueue;
    }

    public ConcurrentLinkedQueue<Entry> getDocuments() {
        return documents;
    }

    public ConcurrentHashMap<String, ArrayList<String>> getHashedURLs() {
        return hashedURLs;
    }

    public ConcurrentHashMap<String, Boolean> getCrawledURLs() {
        return crawledURLS;
    }

    public ConcurrentHashMap<String, Boolean> getHashedPage() {
        return hashedPage;
    }

    public ConcurrentHashMap<String, String> getDisallowedURLS() {
        return DisallowedURLS;
    }

    public int getHashedPageSize() {
        return hashedPage.size();
    }

    public int getUrlQueueSize() {
        return urlQueue.size();
    }


    //------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------Adders-----------------------------------------------------------------
    public boolean addURL(String url, int priority, int depth) {
        URLPriorityPair newURL = new URLPriorityPair(url, priority, depth);
        urlQueue.offer(newURL);
        return true;
    }

    public void addDocument(String doc, String title, String url) {
        documents.offer(new Entry(doc, title, url));
    }


    public void markURL(String url) {
        String md5Hex = getHash(url);
        hashedURLs.put(md5Hex, new ArrayList<>());
    }

    public void markCrawled(String url){
        String md5Hex = getHash(url);
        crawledURLS.put(md5Hex, true);
    }


    public void addParent(String url , String parent){
        //TODO check if the url is not present in the memory of the program and exist on the database
        String md5Hex = getHash(url);
        String parentHash = getHash(parent);
        ArrayList<String> urls = hashedURLs.get(md5Hex);
        urls.add(parentHash);
        hashedURLs.put(md5Hex, urls);
    }


    public void markPage(String url) {
        String md5Hex = getHash(url);
        hashedPage.put(md5Hex, true);
    }

    public void disallowUrl(String url, String host) {
        String md5Hex = getHash(url);
        DisallowedURLS.put(md5Hex, getHash(host));
    }

    //------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------Booleans---------------------------------------------------------------
    public boolean isEmpty() {
        return urlQueue.isEmpty();
    }

    public boolean isVisitedURL(String url) {
        String md5Hex = getHash(url);
        Document criteria = new Document("hash", md5Hex);
        long count = visitedLinksCollection.countDocuments(criteria);
        return hashedURLs.containsKey(md5Hex) || count != 0;
    }

    public boolean isVisitedPage(String doc) {
        String md5Hex = getHash(doc);
        Document criteria = new Document("hash", md5Hex);
        long count = visitedPagesCollection.countDocuments(criteria);
        return hashedPage.containsKey(md5Hex) || count != 0;
    }

    public boolean isAllowedUrl(String url) {
        String md5Hex = getHash(url);
        Document criteria = new Document("disallowed", md5Hex);
        long count = disallowedUrlsCollection.countDocuments(criteria);
        return DisallowedURLS.containsKey(md5Hex) || count != 0;
    }

    public boolean isHostProcessed(String host) {
        String md5Hex = getHash(host);
        Document criteria = new Document("host", md5Hex);
        long count = disallowedUrlsCollection.countDocuments(criteria);
        return DisallowedURLS.containsValue(md5Hex) || count != 0;
    }


    //------------------------------------------------------------------------------------------------------------------

    //-------------------------------------------State Handlers---------------------------------------------------------

    public static void saveQueue(PriorityBlockingQueue<URLPriorityPair> originalQueue, MongoCollection<Document> seedCollection) {
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

    public static void saveCrawledPages(ConcurrentHashMap<String, Boolean> hashedMap, MongoCollection<Document> collection) {
        // Iterate over the ConcurrentHashMap and insert URL strings into the database
        for (String url : hashedMap.keySet()) {
            Document document = new Document("hash", url);
            // Insert the document into the collection
            collection.insertOne(document);
        }
    }

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

    public static void saveDocuments(ConcurrentLinkedQueue<Entry> documents, MongoCollection<Document> collection) {
        while (!documents.isEmpty()) {

            Entry entry = documents.poll();
            Document add = new Document("title", entry.getTitle())
                    .append("url", entry.getLink())
                    .append("document", entry.getContent());
            collection.insertOne(add);
        }
    }

    public static void saveUrlDisallowedPaths(ConcurrentHashMap<String, String> hashedMap, MongoCollection<Document> collection) {
        for (Map.Entry<String, String> entry : hashedMap.entrySet()) {
            String disallowed = entry.getKey();
            String host = entry.getValue();
            Document document = new Document("disallowed", disallowed).append("host", host);
            collection.insertOne(document);
        }
    }

    //------------------------------------------------------------------------------------------------------------------

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