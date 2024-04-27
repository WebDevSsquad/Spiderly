package src;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The DocumentManager class manages documents for indexing.
 * It provides methods to parse documents, store their content, and manage indexes.
 */
public class DocumentManager {

    /**
     * The inverted index storing terms and their corresponding document occurrences.
     */
    ConcurrentHashMap<String, List<Map.Entry<Integer, Integer>>> invertedIndex = new ConcurrentHashMap<>();

    /**
     * The document frequency (DF) map storing the number of documents each term appears in.
     */
    ConcurrentHashMap<String, Integer> DF = new ConcurrentHashMap<>();

    /**
     * The term frequency (TF) map storing the frequency of each term in each document.
     */
    ConcurrentHashMap<String, HashMap<Integer, Integer>> TF = new ConcurrentHashMap<>();

    /**
     * The queue containing documents to be indexed.
     */
    ConcurrentLinkedQueue<Document> docs;

    /**
     * The Parser instance used to parse documents.
     */
    private final Parser parser;

    /**
     * Constructs a DocumentManager object and initializes it with parsed documents.
     */
    DocumentManager() {
        parser = new Parser();
        try {
            docs = GetParsedDocs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Parses and retrieves documents from specified URLs.
     *
     * @return the queue of parsed documents
     * @throws IOException if an I/O error occurs during parsing
     */
    ConcurrentLinkedQueue<Document> GetParsedDocs() throws IOException {
        // Only for testing
        ConcurrentLinkedQueue<Document> parsed = new ConcurrentLinkedQueue<>();
        parsed.offer(parser.parse("https://en.wikipedia.org/wiki/Main_Page"));
        parsed.offer(parser.parse("https://medium.com/"));
        parsed.offer(parser.parse("https://codeforces.com/"));
        return parsed;
    }
}
