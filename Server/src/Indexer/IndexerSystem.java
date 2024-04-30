package Indexer;
import com.mongodb.client.*;
import org.bson.Document;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.types.ObjectId;
import java.util.concurrent.ConcurrentHashMap;

public class IndexerSystem {
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
                e.printStackTrace();
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
        for (ConcurrentHashMap.Entry<WordPair, List<Map.Entry<ObjectId, Integer>>> entry : documentManager.invertedIndex.entrySet()) {
            Document invertedIndexDoc = new Document()
                    .append("originalWord", entry.getKey().getOriginalWord())
                    .append("stemmedWord", entry.getKey().getStemmedWord());

            List<Document> documents = new ArrayList<>();
            for (Map.Entry<ObjectId, Integer> docEntry : entry.getValue()) {
                if (docEntry != null) {
                    Document doc = new Document()
                            .append("docId", docEntry.getKey())
                            .append("index", docEntry.getValue());
                    documents.add(doc);
                }
            }
            invertedIndexDoc.append("documents", documents);
            invertedIndexCollection.insertOne(invertedIndexDoc);
        }

        // Store DF-TF
        MongoCollection<Document> dfTfCollection = database.getCollection("df_tf");
        for (ConcurrentHashMap.Entry<String, HashMap<ObjectId, Integer>> entry : documentManager.TF.entrySet()) {
            Document dfTfDoc = new Document()
                    .append("term", entry.getKey())
                    .append("df", documentManager.DF.get(entry.getKey()));


            // Convert HashMap<Integer, Integer> to Document
            Document dfTfDocument = new Document();
            for (Map.Entry<ObjectId, Integer> dfTfEntry : entry.getValue().entrySet()) {
                dfTfDocument.append(dfTfEntry.getKey().toString(), dfTfEntry.getValue());
            }
            dfTfDoc.append("documents", dfTfDocument);
            dfTfCollection.insertOne(dfTfDoc);
        }

        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);
        System.out.println("All threads have finished execution.");

        mongoClient.close();
    }
}
