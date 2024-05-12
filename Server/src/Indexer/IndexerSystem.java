package Indexer;
import com.mongodb.client.*;
import org.bson.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IndexerSystem {

    // Logging
    private static final Logger logger = Logger.getLogger(IndexerSystem.class.getName());
    private static final String INDEXER_DATABASE_NAME = "Indexer";
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";

    public static void main(String[] args) {
        DocumentManager documentManager = new DocumentManager();

        int threadCount = Integer.parseInt(args[0]);
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Indexer(documentManager));
        }

        System.out.println(threadCount);

        for (Thread thread : threads) {
            thread.start();
        }

        long start = System.currentTimeMillis();

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                logger.log(Level.SEVERE, "Joining indexer threads failed ", e);
            }
        }

        System.out.println(documentManager.invertedIndex);
        System.out.println(documentManager.DF);
        System.out.println(documentManager.TF);

        // Store the inverted index in the database
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
        MongoDatabase database = mongoClient.getDatabase(INDEXER_DATABASE_NAME);

        // Store inverted index
        MongoCollection<Document> invertedIndexCollection = database.getCollection("inverted_index");
        DBManager.saveInvertedIndex(documentManager.invertedIndex.entrySet(), invertedIndexCollection);

        // Store DF-TF
        MongoCollection<Document> dfTfCollection = database.getCollection("df_tf");
        DBManager.saveDfAndTf(documentManager.DF, documentManager.TF, dfTfCollection);

        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);
        System.out.println("All threads have finished execution.");

        mongoClient.close();
    }
}
