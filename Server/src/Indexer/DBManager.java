package Indexer;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The DBManager class provides methods for saving and loading data to/from MongoDB collections.
 */
public class DBManager {

    // Logging
    private static final java.util.logging.Logger logger = Logger.getLogger(DBManager.class.getName());

    /**
     * Saves the inverted indexes to the MongoDB collection.
     *
     * @param entrySet  The entry set of the inverted indexes.
     * @param invertedIndexCollection The MongoDB collection to save the inverted indexes to.
     */
    public static void saveInvertedIndex(
            java.util.Set<java.util.Map.Entry<WordPair, List<java.util.Map.Entry<ObjectId, Integer>>>> entrySet,
            MongoCollection<Document> invertedIndexCollection) {

        for (ConcurrentHashMap.Entry<WordPair, List<Map.Entry<ObjectId, Integer>>> entry : entrySet) {
            Document invertedIndexDoc = new Document()
                    .append("originalWord", entry.getKey().originalWord())
                    .append("stemmedWord", entry.getKey().stemmedWord());

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
    }

    /**
     * Saves the inverted indexes to the MongoDB collection.
     *
     * @param DF  The entry set of the inverted indexes.
     * @param TF  The entry set of the inverted indexes.
     * @param dfTfCollection The MongoDB collection to save the inverted indexes to.
     */
    public static void saveDfAndTf(
            ConcurrentHashMap<String, Integer> DF,
            ConcurrentHashMap<String, HashMap<ObjectId, Integer>> TF,
            MongoCollection<Document> dfTfCollection) {

        for (ConcurrentHashMap.Entry<String, HashMap<ObjectId, Integer>> entry : TF.entrySet()) {
            Document dfTfDoc = new Document()
                    .append("term", entry.getKey())
                    .append("df", DF.get(entry.getKey()));

            Document dfTfDocument = new Document();
            for (Map.Entry<ObjectId, Integer> dfTfEntry : entry.getValue().entrySet()) {
                dfTfDocument.append(dfTfEntry.getKey().toString(), dfTfEntry.getValue());
            }
            dfTfDoc.append("documents", dfTfDocument);
            dfTfCollection.insertOne(dfTfDoc);
            }
    }
}
