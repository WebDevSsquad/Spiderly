package Indexer;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

/**
 * The DocumentManager class manages documents for indexing.
 * It provides methods to parse documents, store their content, and manage indexes.
 */
public class DocumentManager {

    // Logging
    private static final Logger logger = Logger.getLogger(DocumentManager.class.getName());
    private static final String CRAWLER_DATABASE_NAME = "Crawler";
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";

    /**
     * The inverted index storing terms and their corresponding document occurrences.
     */
    public ConcurrentHashMap<String, ConcurrentHashMap<ObjectId, ArrayList<Pair<String, Integer>>>> invertedIndex;

    /**
     * The document frequency (DF) map storing the number of documents each term appears in.
     */
    public ConcurrentHashMap<String, Integer> DF;

    /**
     * The term frequency (TF) map storing the frequency of each term in each document.
     */
    public final ConcurrentHashMap<String, HashMap<ObjectId, HashMap<String, Integer>>> TF;


    public final ConcurrentHashMap<String, HashMap<ObjectId, String>> wordDescription;

    /**
     * The queue containing documents to be indexed.
     */
    public ConcurrentLinkedQueue<Document> docs;


    /**
     * Constructs a DocumentManager object and initializes it with parsed documents.
     */
    DocumentManager() {
        try {
            docs = GetParsedDocs();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Parsing Docs failed ", e);
        }

        invertedIndex = new ConcurrentHashMap<>();
        DF = new ConcurrentHashMap<>();
        TF = new ConcurrentHashMap<>();
        wordDescription = new ConcurrentHashMap<>();
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
