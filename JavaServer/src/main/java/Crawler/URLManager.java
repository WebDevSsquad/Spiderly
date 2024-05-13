package Crawler;

//import Indexer.Pair;
import com.mongodb.client.MongoCollection;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.validator.UrlValidator;
import org.bson.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.StringTemplate.STR;

/**
 * URLManager class manages the URLs to be crawled, checks their validity, and handles their states.
 */
public class URLManager {

    // Logging
    private static final Logger logger = Logger.getLogger(URLManager.class.getName());
    private final URLFrontier urlFrontier;

    private final MongoCollection<Document> seedCollection;
    private final MongoCollection<Document> visitedPagesCollection;
    private final MongoCollection<Document> visitedLinksCollection;

    private final MongoCollection<Document> disallowedUrlsCollection;
    private final MongoCollection<Document> documentsCollection;

    /**
     * Constructs a URLManager with the necessary MongoDB collections.
     *
     * @param seedCollection           The collection for storing seed URLs.
     * @param visitedPagesCollection   The collection for storing visited pages.
     * @param visitedLinksCollection   The collection for storing visited links.
     * @param documentsCollection      The collection for storing parsed documents.
     * @param disallowedUrlsCollection The collection for storing disallowed URLs.
     */
    public URLManager(MongoCollection<Document> seedCollection, MongoCollection<Document> visitedPagesCollection,
                      MongoCollection<Document> visitedLinksCollection,
                      MongoCollection<Document> documentsCollection,
                      MongoCollection<Document> disallowedUrlsCollection) {

        PriorityBlockingQueue<URLPriorityPair> urlQueue = DBManager.loadQueueFromFile(seedCollection);
        urlFrontier = new URLFrontier(urlQueue, visitedPagesCollection, visitedLinksCollection, disallowedUrlsCollection);

        this.documentsCollection = documentsCollection;
        this.seedCollection = seedCollection;
        this.visitedPagesCollection = visitedPagesCollection;
        this.visitedLinksCollection = visitedLinksCollection;
        this.disallowedUrlsCollection = disallowedUrlsCollection;
    }

    /**
     * Checks if a URL is valid and adds it to the URLFrontier if it's new.
     *
     * @param url      The URL to check and add.
     * @param parent   The parent URL of the child URL.
     * @param priority The priority of the URL.
     * @param depth    The depth of the URL.
     * @return True if the URL was added successfully, otherwise false.
     */
    public boolean handleChildUrl(String url, String parent, int priority, int depth) {
        if (!validURL(url)) {
            //logger.log(Level.FINE, STR."Invalid Link: \{url}");
            return false;
        }

        // Get the normalized URL without queries or fragments
        String normalized_url = normalizeURL(url);

        if (normalized_url != null && !normalized_url.isEmpty()) {
            // Check if the child url is new
            if (!urlFrontier.isVisitedURL(normalized_url)) {
                addNewURL(normalized_url, priority, depth);
            }

            // Add parent url to the child url
            urlFrontier.addParent(normalized_url, parent);
            return true;
        }
        return false;
    }

    /**
     * Checks if a URL is a seed, mark it. And its document for duplicates.
     *
     * @param url     The URL to check.
     * @param docText The text content of the document.
     * @return True if the URL is new and the document is not a duplicate, otherwise false.
     */
    public boolean checkURL(String url, String docText) {
        // Check if the url is a seed
        if (!urlFrontier.isVisitedURL(url)) urlFrontier.markURL(url);

        // Check if the page is duplicated
        return !urlFrontier.isVisitedPage(docText);
    }

    /**
     * Adds unique words from the given elements to the specified list, avoiding duplicates
     * by tracking visited tags in the provided HashSet.
     *
     * @param elements The elements to extract words from.
     * @param visitedTag The HashSet to track visited tags.
     * @param tag The ArrayList to which unique words are added.
     */
    private void addWordsToList(Elements elements, HashSet<String> visitedTag, ArrayList<String> tag) {
        for (Element element : elements) {
            // Add non-empty text from elements that haven't been visited yet
            if (!element.text().isEmpty() && !visitedTag.contains(element.text())) {
                tag.add(element.text());
                visitedTag.add(element.text());
            }
        }
    }

    /**
     * Extracts HTML tags from the provided HTML document and populates
     * corresponding ArrayLists with unique words found in different sections.
     *
     * @param docHtml The HTML content to parse.
     * @param headerArray The ArrayList to store words found in header tags.
     * @param titleArray The ArrayList to store words found in title tags.
     * @param textArray The ArrayList to store words found in body text.
     */
    public void getHTMLTags(String docHtml, ArrayList<String> headerArray, ArrayList<String> titleArray, ArrayList<String> textArray) {
        org.jsoup.nodes.Document parsedDoc = Jsoup.parse(docHtml);

        HashSet<String> visitedTag = new HashSet<>();
        String[] headers = {"h1", "h2", "h3", "h4", "h5", "h6"};

        // Extract words from header tags
        for (String header : headers) {
            Elements hTags = parsedDoc.select(header);
            addWordsToList(hTags, visitedTag, headerArray);
        }
        visitedTag.clear();

        // Extract words from title tags
        Elements titleTags = parsedDoc.select("title");
        addWordsToList(titleTags, visitedTag, titleArray);

        visitedTag.clear();

        // Extract words from body text
        Elements bodyTags = parsedDoc.body().getAllElements();
        addWordsToList(bodyTags, visitedTag, textArray);
    }

    /**
     * Visits a URL, marks the page in the URL frontier, adds the document to the URL frontier, and marks the URL as crawled.
     *
     * @param url The URL to visit.
     * @param docText The text content of the document.
     * @param headerArray The ArrayList containing words found in header tags of the document.
     * @param titleArray The ArrayList containing words found in title tags of the document.
     * @param textArray The ArrayList containing words found in body text of the document.
     * @param title The title of the document.
     */
    public void visitURL(String url, String docText, ArrayList<String> headerArray, ArrayList<String> titleArray, ArrayList<String> textArray, String title) {
        // Mark the page in the URL frontier
        urlFrontier.markPage(docText);

        // Add the document to the URL frontier
        urlFrontier.addDocument(title, headerArray, titleArray, textArray, url);

        // Mark the URL as crawled
        urlFrontier.markCrawled(url);
    }


    /**
     * Adds a new URL to the URLFrontier.
     *
     * @param url      The URL to add.
     * @param priority The priority of the URL.
     * @param depth    The depth of the URL.
     */
    public void addNewURL(String url, int priority, int depth) {
        try {
            URI uri = new URI(url);
            urlFrontier.addURL(uri, priority, depth);
        } catch (URISyntaxException e) {
            //logger.log(Level.SEVERE, STR."Error creating URI when adding to url frontier: \{url}", e);
        }
    }

    /**
     * Checks if a URL is valid.
     *
     * @param url The URL to check.
     * @return True if the URL is valid, otherwise false.
     */
    public boolean validURL(String url) {
        // Implement logic to shorten URLs if needed
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url);
    }

    /**
     * Normalizes a URL for consistency and comparability.
     *
     * @param url The URL to normalize.
     * @return The normalized URL.
     */
    public String normalizeURL(String url) {
        // Implement logic to normalize URLs for consistency and comparability
        // This can include removing trailing slashes, converting to lowercase, etc.
        // 1. Cloudflare normalization
        // 2. RFC 3986 normalization

        try {
            URI uri = new URI(url);

            // 1. Remove the part after '#' (the fragment part)
            if (uri.getFragment() != null) {
                // Remake with same parts, less the fragment:
                uri = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
            }

            // 2. RFC 3986 Normalization
            URI normalizedURI = uri.normalize();

            // 3. Remove queries
            String normalizedURL = STR."\{normalizedURI.getScheme()}://\{normalizedURI.getAuthority()}\{normalizedURI.getPath()}";;

            // 4. Apache normalization
            // normalizedURL = apacheNormalization(normalizedURL);

            return normalizedURL.toLowerCase();
        } catch (URISyntaxException e) {
            //logger.log(Level.SEVERE, STR."Invalid URL while normalization: \{url}", e);
            return null;
        }
    }

    /**
     * Applies Apache normalization to a URL.
     *
     * @param urlString The URL to normalize.
     * @return The normalized URL.
     */
    private String apacheNormalization(String urlString) {
        try {
            // Decode percent-encoded characters
            String decodedURL = URLDecoder.decode(urlString, StandardCharsets.UTF_8);

            // Normalize decoded URL using Apache Commons Codec
            URLCodec codec = new URLCodec();
            return codec.decode(codec.encode(decodedURL));
        } catch (DecoderException | EncoderException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the URLFrontier object associated with this URLManager.
     *
     * @return The URLFrontier object.
     */
    public URLFrontier getUrlFrontier() {return urlFrontier;}

    /**
     * Saves the state of the URLManager to the database.
     */
    public void saveState() {
        DBManager.saveQueue(urlFrontier.getQueue(), seedCollection);
        DBManager.saveCrawledPages(urlFrontier.getHashedPage(), visitedPagesCollection);
        DBManager.saveVisitedPages(urlFrontier.getHashedURLs(), urlFrontier.getCrawledURLs(), visitedLinksCollection);
        DBManager.saveDocuments(urlFrontier.getDocuments(), documentsCollection);
        DBManager.saveUrlDisallowedPaths(urlFrontier.getDisallowedURLS(), disallowedUrlsCollection);
    }
}
