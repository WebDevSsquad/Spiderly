package Indexer;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        for (ConcurrentHashMap.Entry<String, List<Map.Entry<Integer, Integer>>> entry : documentManager.invertedIndex.entrySet()) {
            Document invertedIndexDoc = new Document()
                    .append("term", entry.getKey());

            List<Document> documents = new ArrayList<>();
            for (Map.Entry<Integer, Integer> docEntry : entry.getValue()) {
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

        // Store document frequency (DF)
        MongoCollection<Document> dfCollection = database.getCollection("document_frequency");
        for (ConcurrentHashMap.Entry<String, Integer> entry : documentManager.DF.entrySet()) {
            Document dfDoc = new Document()
                    .append("term", entry.getKey())
                    .append("frequency", entry.getValue());
            dfCollection.insertOne(dfDoc);
        }

        // Store term frequency (TF)
        MongoCollection<Document> tfCollection = database.getCollection("term_frequency");
        for (ConcurrentHashMap.Entry<String, HashMap<Integer, Integer>> entry : documentManager.TF.entrySet()) {
            Document tfDoc = new Document()
                    .append("term", entry.getKey());

            // Convert HashMap<Integer, Integer> to Document
            Document tfDocument = new Document();
            for (Map.Entry<Integer, Integer> tfEntry : entry.getValue().entrySet()) {
                tfDocument.append(tfEntry.getKey().toString(), tfEntry.getValue());
            }
            tfDoc.append("documents", tfDocument);
            tfCollection.insertOne(tfDoc);
        }

        long end = System.currentTimeMillis();
        System.out.println((end - start) / 1000);
        System.out.println("All threads have finished execution.");

        mongoClient.close();
    }
}
