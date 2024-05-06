package Indexer;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;
/**
 * The DocumentManager class manages documents for indexing.
 * It provides methods to parse documents, store their content, and manage indexes.
 */
class WordPair {
    private final String originalWord;
    private final String stemmedWord;

    public WordPair(String originalWord, String stemmedWord) {
        this.originalWord = originalWord;
        this.stemmedWord = stemmedWord;
    }

    // Add getters for original and stemmed words
    public String getOriginalWord() {
        return originalWord;
    }

    public String getStemmedWord() {
        return stemmedWord;
    }

    // Override equals and hashCode for proper usage in HashMap
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WordPair wordPair = (WordPair) obj;
        return Objects.equals(originalWord, wordPair.originalWord) &&
                Objects.equals(stemmedWord, wordPair.stemmedWord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originalWord, stemmedWord);
    }
}

public class DocumentManager {

    private static final String CRAWLER_DATABASE_NAME = "Crawler";
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";

    /**
     * The inverted index storing terms and their corresponding document occurrences.
     */
    ConcurrentHashMap<WordPair, List<Map.Entry<ObjectId, Integer>>> invertedIndex = new ConcurrentHashMap<>();

    /**
     * The document frequency (DF) map storing the number of documents each term appears in.
     */
    ConcurrentHashMap<String, Integer> DF = new ConcurrentHashMap<>();

    /**
     * The term frequency (TF) map storing the frequency of each term in each document.
     */
    ConcurrentHashMap<String, HashMap<ObjectId, Integer>> TF = new ConcurrentHashMap<>();

    /**
     * The queue containing documents to be indexed.
     */
    ConcurrentLinkedQueue<Document> docs;


    /**
     * Constructs a DocumentManager object and initializes it with parsed documents.
     */
    DocumentManager() {
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
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
        MongoDatabase database = mongoClient.getDatabase(CRAWLER_DATABASE_NAME);
        MongoCollection<Document> documentCollection = database.getCollection("documents");

        FindIterable<Document> documents = documentCollection.find();

        for (Document document : documents) {
            parsed.add(document);
        }

        mongoClient.close();
        return parsed;
    }
}
