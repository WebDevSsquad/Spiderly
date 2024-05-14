package Indexer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.parser.JSONParser;
import java.util.HashMap;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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


    private final Integer DESCRIPTION_THRESHOLD_MIN;
    private final Integer DESCRIPTION_THRESHOLD_MAX;


    /**
     * Constructs an Indexer object with the specified DocumentManager.
     *
     * @param documentManager the DocumentManager instance to use
     */
    public Indexer(DocumentManager documentManager) {
        this.stemmer = new Stemmer();
        this.documentManager = documentManager;
        this.DESCRIPTION_THRESHOLD_MIN = 10;
        this.DESCRIPTION_THRESHOLD_MAX = 1000;
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

            ObjectId id = doc.getObjectId("_id");

            ArrayList<Pair<String, Integer>> textArray = GetDocumentText(doc, id);

            HashSet<String> stopWords = new HashSet<>();
            try {
                stopWords = ReadStopWords();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Index(textArray, stopWords, id);
        }
    }

    /**
     * Indexes the given text array, excluding stop words and storing the stemmed words in the inverted index.
     *
     * @param textArray the array of text to be indexed
     * @param stopWords the set of stop words to be excluded from indexing
     */
    void Index(ArrayList<Pair<String, Integer>> textArray, HashSet<String> stopWords, ObjectId index) {

        HashSet<String> visited = new HashSet<>();

        for (int j = 0; j < textArray.size(); j++) {
            if (stopWords.contains(textArray.get(j).getFirst()) || textArray.get(j).getFirst().isEmpty()) continue;
            String word = stemmer.Stem(textArray.get(j).getFirst());
            Integer tagIndex = textArray.get(j).getSecond();

            String[] tag = {"header", "title", "text"};

            documentManager.invertedIndex.computeIfAbsent(word, _ -> new ConcurrentHashMap<>())
                    .computeIfAbsent(index, _ -> new ArrayList<>())
                    .add(new Pair<>(tag[tagIndex], j));

            if (!visited.contains(word)) {
                visited.add(word);
                documentManager.DF.put(word, documentManager.DF.getOrDefault(word, 0) + 1);
            }
            synchronized (documentManager.TF) {
                documentManager.TF.computeIfAbsent(word, _-> new HashMap<>())
                        .computeIfAbsent(index, _ -> new HashMap<>())
                        .merge(tag[tagIndex], 1, Integer::sum);
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
    ArrayList<Pair<String, Integer>> GetDocumentText(Document doc, ObjectId index) {
        ArrayList<String> headerArray = (ArrayList<String>) doc.get("headerArray");
        ArrayList<String> titleArray = (ArrayList<String>) doc.get("titleArray");
        ArrayList<String> textArray = (ArrayList<String>) doc.get("textArray");


        ArrayList<Pair<String, Integer>> wordsPair = new ArrayList<>();

        addWordsToList(headerArray, wordsPair, 0, index);
        addWordsToList(titleArray, wordsPair, 1, index);
        addWordsToList(textArray, wordsPair, 2, index);

        return wordsPair;
    }

    void addWordsToList(ArrayList<String> tag, ArrayList<Pair<String, Integer>> wordsPair, Integer rank, ObjectId index) {
        for (String text : tag) {
            String[] words = text.split("\\s+");
            for (String word : words) {
                String stemmedWord = stemmer.Stem(word);
                if(Objects.equals(stemmedWord, "")) continue;
                if(text.length() >= DESCRIPTION_THRESHOLD_MIN && text.length() <= DESCRIPTION_THRESHOLD_MAX) {
                    if (!documentManager.wordDescription.containsKey(stemmedWord)) {
                        documentManager.wordDescription.put(stemmedWord, new HashMap<>());
                    }
                    HashMap<ObjectId, String> innerMap = documentManager.wordDescription.get(stemmedWord);
                    if (!innerMap.containsKey(index)) {
                        innerMap.put(index, text);
                    }
                }
                wordsPair.add(new Pair<>(word, rank));
            }
        }
    }
}
