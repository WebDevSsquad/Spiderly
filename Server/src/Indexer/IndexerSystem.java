package Indexer;
import com.mongodb.client.*;
import org.bson.Document;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IndexerSystem {

    // Logging
    private static final Logger logger = Logger.getLogger(IndexerSystem.class.getName());
    private static final String INDEXER_DATABASE_NAME = "Indexer";
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";

    public static void main(String[] args) {
        runIndexerSystem(Integer.parseInt(args[0]));
    }

    public static void runIndexerSystem(int threadCount) {
        DocumentManager documentManager = new DocumentManager();

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

        //System.out.println(documentManager.invertedIndex);
        //System.out.println(documentManager.DF);
        //System.out.println(documentManager.TF);

        // Store the inverted index in the database
        MongoClient mongoClient = MongoClients.create(CONNECTION_STRING);
        MongoDatabase database = mongoClient.getDatabase(INDEXER_DATABASE_NAME);

        // Store inverted index
        MongoCollection<Document> invertedIndexCollection = database.getCollection("indexer_collection");
        DBManager.saveInvertedIndex(documentManager.invertedIndex, documentManager.DF, documentManager.TF, invertedIndexCollection);


        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);
        System.out.println("All threads have finished execution.");

        mongoClient.close();
    }
}
