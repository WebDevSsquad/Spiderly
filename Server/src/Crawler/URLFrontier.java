package Crawler;

import com.mongodb.client.MongoCollection;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;

import java.net.URI;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * URLFrontier manages the queue of URLs to be crawled, keeps track of crawled URLs.
 */
public class URLFrontier {

    // Queue that holds the future seed of the crawler.
    // This queue loads at the beginning of the program from the database to get the previous state
    private final PriorityBlockingQueue<URLPriorityPair> urlQueue;

    // Queue for parsed documents of crawled pages
    private final ConcurrentLinkedQueue<Entry> documents;

    // Map to store visited or crawled URLs and their parent URLs
    private final ConcurrentHashMap<String, ArrayList<String>> hashedURLs;

    // Map to store crawled URLs
    private final ConcurrentHashMap<String, Boolean> crawledURLS;

    // Map to store disallowed URLs and their hosts
    private final ConcurrentHashMap<String, String> DisallowedURLS;

    // Map to store hashed parsed document of page content
    private final ConcurrentHashMap<String, Boolean> hashedPage;

    // MongoDB Collections

    // Collection for crawled pages, contains the parsed doc hashed to overcome
    // the problem of pages which have the same URL
    private final MongoCollection<Document> visitedPagesCollection;

    // Collection for links that are crawled or in queue to prevent putting
    // duplicates in the queue and visiting the same URL twice
    private final MongoCollection<Document> visitedLinksCollection;

    // Collection to save the disallowed URLs from a certain host to prevent
    // downloading the robots.txt file twice and visiting a disallowed URL
    private final MongoCollection<Document> disallowedUrlsCollection;

    /**
     * Constructs a URLFrontier object.
     *
     * @param urlQueue                 Priority Queue for URLs to be crawled
     * @param visitedPagesCollection   Collection for crawled pages
     * @param visitedLinksCollection   Collection for links that are crawled or in queue
     * @param disallowedUrlsCollection Collection for disallowed URLs
     */
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

    //--------------------------------------------------- GETTERS ----------------------------------------------------//

    public synchronized URLPriorityPair getNextURL() {
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

    //----------------------------------------------------------------------------------------------------------------//

    //---------------------------------------------------- ADDERS ----------------------------------------------------//

    /**
     * Adds a URL to the URL queue.
     *
     * @param url      The URL to add
     * @param priority The priority of the URL
     * @param depth    The depth of the URL in the crawl hierarchy
     */
    public void addURL(URI url, int priority, int depth) {
        URLPriorityPair newURL = new URLPriorityPair(url, priority, depth);
        urlQueue.offer(newURL);
        markURL(newURL.url().toString());
    }

    /**
     * Adds a document to the queue of parsed documents.
     *
     * @param doc   The document content
     * @param title The title of the document
     * @param url   The URL of the document
     */
    public void addDocument(String doc, String title, String url) {
        documents.offer(new Entry(title, doc, url));
    }

    /**
     * Marks a URL as visited.
     *
     * @param url The URL to mark
     */
    public void markURL(String url) {
        String md5Hex = getHash(url);
        hashedURLs.put(md5Hex, new ArrayList<>());
    }

    /**
     * Marks a URL as crawled.
     *
     * @param url The URL to mark as crawled
     */
    public void markCrawled(String url) {
        String md5Hex = getHash(url);
        crawledURLS.put(md5Hex, true);
    }


    /**
     * Adds a parent URL for a given URL.
     *
     * @param url    The URL to add a parent to
     * @param parent The parent URL
     */
    public void addParent(String url, String parent) {
        // Get hashes of both urls
        String md5Hex = getHash(url);
        String parentHash = getHash(parent);

        // Check if the URL exists in the database
        Document criteria = new Document("hash", md5Hex);
        Document foundDoc = visitedLinksCollection.find(criteria).first();

        if (foundDoc != null) {    // Exists in the database not the present memory.
            // Get the existing parent list
            ArrayList<String> urls = foundDoc.get("parents", ArrayList.class);
            if (urls == null) {
                urls = new ArrayList<>();
            }

            // Add the new parent
            urls.add(parentHash);

            // Update the document in the database with the new parent list
            criteria.put("parents", urls);
            visitedLinksCollection.updateOne(new Document("_id", foundDoc.getObjectId("_id")), new Document("$set", criteria));
        } else {
            // Get the existing parent list
            ArrayList<String> urls;
            if (hashedURLs.containsKey(md5Hex))
                urls = hashedURLs.get(md5Hex);
            else
                urls = new ArrayList<>();
            urls.add(parentHash);
            hashedURLs.put(md5Hex, urls);
        }
    }


    /**
     * Marks a page as visited, storing its hash in the hashedPage map.
     *
     * @param url The URL of the page to mark as visited.
     */
    public void markPage(String url) {
        String md5Hex = getHash(url);
        hashedPage.put(md5Hex, true);
    }

    /**
     * Disallows a URL from being crawled by adding it to the DisallowedURLS map.
     *
     * @param url  The URL to disallow.
     * @param host The host associated with the disallowed URL.
     */
    public void disallowUrl(String url, String host) {
        String md5Hex = getHash(url);
        DisallowedURLS.put(md5Hex, getHash(host));
    }

    //----------------------------------------------------------------------------------------------------------------//

    //--------------------------------------------------- BOOLEANS ---------------------------------------------------//

    /**
     * Checks if the URL queue is empty.
     *
     * @return True if the URL queue is empty, otherwise false.
     */
    public boolean isEmpty() {
        return urlQueue.isEmpty();
    }

    /**
     * Checks if a URL has been visited.
     *
     * @param url The URL to check.
     * @return True if the URL has been visited, otherwise false.
     */
    public boolean isVisitedURL(String url) {
        String md5Hex = getHash(url);
        Document criteria = new Document("hash", md5Hex);
        long count = visitedLinksCollection.countDocuments(criteria);
        return hashedURLs.containsKey(md5Hex) || count != 0;
    }

    /**
     * Checks if a page has been visited.
     *
     * @param doc The document to check.
     * @return True if the page has been visited, otherwise false.
     */
    public boolean isVisitedPage(String doc) {
        String md5Hex = getHash(doc);
        Document criteria = new Document("hash", md5Hex);
        long count = visitedPagesCollection.countDocuments(criteria);
        return hashedPage.containsKey(md5Hex) || count != 0;
    }

    /**
     * Checks if a URL is allowed to be crawled.
     *
     * @param url The URL to check.
     * @return True if the URL is allowed, otherwise false.
     */
    public boolean isAllowedUrl(String url) {
        String md5Hex = getHash(url);
        Document criteria = new Document("disallowed", md5Hex);
        long count = disallowedUrlsCollection.countDocuments(criteria);
        return !DisallowedURLS.containsKey(md5Hex) && count == 0;
    }

    /**
     * Checks if a host has been processed.
     *
     * @param host The host to check.
     * @return True if the host has been processed, otherwise false.
     */
    public boolean isHostProcessed(String host) {
        String md5Hex = getHash(host);
        Document criteria = new Document("host", md5Hex);
        long count = disallowedUrlsCollection.countDocuments(criteria);
        return DisallowedURLS.containsValue(md5Hex) || count != 0;
    }

    //----------------------------------------------------------------------------------------------------------------//
}

