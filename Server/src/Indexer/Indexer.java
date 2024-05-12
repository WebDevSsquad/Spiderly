package Indexer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.parser.JSONParser;
import java.util.HashMap;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Indexer class is responsible for indexing documents by tokenizing, stemming, and storing them in an inverted index.
 * It implements the Runnable interface to allow concurrent indexing of multiple documents.
 */
public class Indexer implements Runnable {

    /**
     * The Stemmer instance used for stemming words.
     */
    private final Stemmer stemmer;

    /**
     * The DocumentManager instance that manages the documents and their indexes.
     */
    private final DocumentManager documentManager;

    /**
     * Constructs an Indexer object with the specified DocumentManager.
     *
     * @param documentManager the DocumentManager instance to use
     */
    public Indexer(DocumentManager documentManager) {
        this.stemmer = new Stemmer();
        this.documentManager = documentManager;
    }

    /**
     * Executes the indexing process for documents in the DocumentManager.
     * It retrieves documents, processes their text, and indexes them.
     */
    @Override
    public void run() {
        int queueSize = documentManager.docs.size();
        while (queueSize-- != 0) {
            Document doc = documentManager.docs.poll();
            if (doc == null) break;

            ArrayList<String> textArray = GetDocumentText(doc);

            HashSet<String> stopWords = new HashSet<>();
            try {
                stopWords = ReadStopWords();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ObjectId id = doc.getObjectId("_id");
            Index(textArray, stopWords, id);
        }
    }

    /**
     * Indexes the given text array, excluding stop words and storing the stemmed words in the inverted index.
     *
     * @param textArray the array of text to be indexed
     * @param stopWords the set of stop words to be excluded from indexing
     */
    void Index(ArrayList<String> textArray, HashSet<String> stopWords, ObjectId index) {
        HashSet<String> visited = new HashSet<>();

        for (int j = 0; j < textArray.size(); j++) {
            if (stopWords.contains(textArray.get(j)) || textArray.get(j).isEmpty()) continue;
            String word = stemmer.Stem(textArray.get(j));

            documentManager.invertedIndex.computeIfAbsent(word, _ -> new ConcurrentHashMap<>())
                    .computeIfAbsent(index, _ -> new ArrayList<>())
                    .add(j);

            if (!visited.contains(word)) {
                visited.add(word);
                documentManager.DF.put(word, documentManager.DF.getOrDefault(word, 0) + 1);
            }
            synchronized (documentManager.TF) {
                documentManager.TF.computeIfAbsent(word, _ -> new HashMap<>()).merge(index, 1, Integer::sum);
            }
        }
    }



    /**
     * Reads stop words from a JSON file and returns them as a HashSet.
     *
     * @return the set of stop words
     * @throws IOException    if an I/O error occurs
     * @throws ParseException if the JSON parsing fails
     */
    HashSet<String> ReadStopWords() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("src/stopwords.json"));
        return new HashSet<String>((JSONArray) jsonObject.get("stopwords"));
    }

    /**
     * Extracts text from a given document and returns it as an ArrayList of strings.
     *
     * @param doc the document from which to extract text
     * @return the ArrayList containing the text tokens
     */
    ArrayList<String> GetDocumentText(Document doc) {
        ArrayList<String> textArray = new ArrayList<>();
        String text = doc.getString("document");
        String[] words = text.split("\\W+");
        for (String word : words)
            textArray.add(word.toLowerCase());
        return textArray;
    }
}
