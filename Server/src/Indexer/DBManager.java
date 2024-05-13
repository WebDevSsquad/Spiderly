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
    private static final Logger logger = Logger.getLogger(DBManager.class.getName());

    private static final int Batch = 10000;



    public static void saveInvertedIndex(
            ConcurrentHashMap<String, ConcurrentHashMap<ObjectId, ArrayList<Pair<String, Integer>>>> invertedIndex,
            ConcurrentHashMap<String, Integer> DF, ConcurrentHashMap<String, HashMap<ObjectId, HashMap<String, Integer>>> TF,
            MongoCollection<Document> invertedIndexCollection) {

        String[] tag = {"header", "title", "text"};
        ArrayList<Document>DBDocs = new ArrayList<>();
        int counter = 0;
        for (Map.Entry<String, ConcurrentHashMap<ObjectId, ArrayList<Pair<String, Integer>>>> entry : invertedIndex.entrySet()) {
            String word = entry.getKey();
            int df = DF.getOrDefault(word, 0);
            Document invertedIndexDoc = new Document()
                    .append("term", word)
                    .append("DF", df);

            List<Document> documents = new ArrayList<>();
            for (Map.Entry<ObjectId, ArrayList<Pair<String, Integer>>> docEntry : entry.getValue().entrySet()) {
                ObjectId docId = docEntry.getKey();
                ArrayList<Pair<String, Integer>> indices = docEntry.getValue();

                Document doc = new Document()
                        .append("docId", docId);

                HashMap<ObjectId, HashMap<String, Integer>> wordTF = TF.getOrDefault(word, new HashMap<>());
                HashMap<String, Integer> docTF = wordTF.getOrDefault(docId, new HashMap<>());

                for(int i = 0; i < 3; i++) {
                    doc.append(STR."tf_\{tag[i]}", docTF.getOrDefault(tag[i], 0));
                }

                List<Document> indicesList = new ArrayList<>();
                for (Pair<String, Integer> indexPair : indices) {
                    Document indexDoc = new Document()
                            .append("index", indexPair.getSecond())
                            .append("type", indexPair.getFirst());
                    indicesList.add(indexDoc);
                }
                doc.append("indices", indicesList);

                documents.add(doc);
            }
            invertedIndexDoc.append("documents", documents);

            DBDocs.add(invertedIndexDoc);

            counter++;
            if(counter == Batch) {
                counter = 0;
                invertedIndexCollection.insertMany(DBDocs); // Insert the documents into collection
                DBDocs.clear();
            }

        }
        if(counter != 0) {
            invertedIndexCollection.insertMany(DBDocs);
            DBDocs.clear();
        }
    }
}
