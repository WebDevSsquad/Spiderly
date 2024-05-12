package Indexer;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * The DBManager class provides methods for saving and loading data to/from MongoDB collections.
 */
public class DBManager {

    // Logging
    private static final java.util.logging.Logger logger = Logger.getLogger(DBManager.class.getName());

    /**
     * Saves the inverted index to a MongoDB collection.
     * This method takes the inverted index, document frequency (DF), term frequency (TF), and a MongoDB collection as input.
     * It iterates over the inverted index, constructs documents representing each word, and saves them to the provided MongoDB collection.
     * Each document contains information about the original word, its document frequency (DF), and a list of documents (with their respective term frequencies and indices).
     *
     * @param invertedIndex         The inverted index mapping words to a map of document IDs and corresponding lists of indices.
     * @param DF                    The document frequency (DF) map containing the frequency of each word across all documents.
     * @param TF                    The term frequency (TF) map containing the frequency of each word in each document.
     * @param invertedIndexCollection The MongoDB collection where the inverted index documents will be saved.
     */

    public static void saveInvertedIndex(
            ConcurrentHashMap<String, ConcurrentHashMap<ObjectId, List<Integer>>> invertedIndex,
            ConcurrentHashMap<String, Integer> DF, ConcurrentHashMap<String, HashMap<ObjectId, Integer>> TF,
            MongoCollection<Document> invertedIndexCollection) {

        for (Map.Entry<String, ConcurrentHashMap<ObjectId, List<Integer>>> entry : invertedIndex.entrySet()) {
            String word = entry.getKey();
            int df = DF.getOrDefault(word, 0);
            Document invertedIndexDoc = new Document()
                    .append("originalWord", word)
                    .append("DF", df);

            List<Document> documents = new ArrayList<>();
            for (Map.Entry<ObjectId, List<Integer>> docEntry : entry.getValue().entrySet()) {
                ObjectId docId = docEntry.getKey();
                List<Integer> indexes = docEntry.getValue();

                int tf = TF.containsKey(word) && TF.get(word).containsKey(docId) ? TF.get(word).get(docId) : 0;

                Document doc = new Document()
                        .append("docId", docId)
                        .append("TF", tf)
                        .append("indices", indexes);

                documents.add(doc);
            }
            invertedIndexDoc.append("documents", documents);
            invertedIndexCollection.insertOne(invertedIndexDoc); // Insert the document into collection
        }
    }



}
