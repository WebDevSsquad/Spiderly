package src;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.nodes.Document;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Indexer implements Runnable {
    private final Stemmer stemmer;
    private final DocumentManager documentManager;
    public Indexer(DocumentManager documentManager) {
        //XXX should take documents directly and not deal with parser
        stemmer = new Stemmer();
        this.documentManager = documentManager;
    }
    @Override
    public void run() {
        int queueSize = documentManager.docs.size();
        while(queueSize-- != 0) {
            Document doc = documentManager.docs.poll();
            if(doc == null) break;

            ArrayList<String> textArray = GetDocumentText(doc);

            HashSet<String> stopWords = new HashSet<>();
            try {
                stopWords = ReadStopWords();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Index(textArray, stopWords);
        }
    }

    void Index(ArrayList<String> textArray, HashSet<String> stopWords) {
        //Storing in inverted index
        HashSet<String> visited = new HashSet<>();
        for (int j = 0; j < textArray.size(); j++) {
            //Remove Stop words
            if (stopWords.contains(textArray.get(j)) || textArray.get(j).isEmpty()) continue;
            //Stem the word
            String word = stemmer.Stem(textArray.get(j));
            //Insert in inverted index
            documentManager.invertedIndex.computeIfAbsent(word,
                    _ -> new ArrayList<>()).add(new HashMap.SimpleEntry<>(documentManager.docs.size(), j));
            //Calculate DF
            if (!visited.contains(word)) {
                visited.add(word);
                documentManager.DF.put(word, documentManager.DF.getOrDefault(word, 0) + 1);
            }
            //Calculate TF
            documentManager.TF.computeIfAbsent(word, _ -> new HashMap<>()).merge(documentManager.docs.size(), 1, Integer::sum);
        }
    }

    HashSet<String> ReadStopWords() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader("stopwords.json"));
        return new HashSet<String>((JSONArray)jsonObject.get("stopwords"));
    }
    ArrayList<String> GetDocumentText(Document doc) {
        ArrayList<String> textArray = new ArrayList<>();
        //Tokenize
        String text = doc.text();
        String[] words = text.split("\\W+");
        //Normalize
        for (String word : words)
            textArray.add(word.toLowerCase());
        return textArray;
    }
}
