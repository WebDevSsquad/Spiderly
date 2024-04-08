import org.json.simple.JSONArray;
import org.jsoup.nodes.Document;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Indexer{
    private final Parser parser;
    private final Stemmer stemmer;
    ArrayList<Document> docs;
    public Indexer() throws IOException {
        //XXX should take documents directly and not deal with parser
        parser = new Parser();
        stemmer = new Stemmer();
        docs = null;
    }
    public void run() {
        HashMap<String, List<Map.Entry<Integer, Integer>>> invertedIndex = new HashMap<>();
        //XXX will remove next block later
        try {
            docs = GetParsedDocs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ArrayList<String>[] textArrays = new ArrayList[docs.size()];
        for (int i = 0; i < docs.size(); i++) {
            //Tokenize
            String text = docs.get(i).text();
            String[] words = text.split("\\W+");
            textArrays[i] = new ArrayList<>();
            //Normalize
            for(String word : words)
                textArrays[i].add(stemmer.Stem(word).toLowerCase());
        }
        HashSet<String>stopWords = new HashSet<>();
        try {
            stopWords = ReadStopWords();
        }catch(Exception e) {
            e.printStackTrace();
        }

        //Storing in inverted index
        for(int i = 0; i < docs.size(); i++) {
            for(int j = 0; j < textArrays[i].size(); j++) {
                //Remove Stop words
                if(stopWords.contains(textArrays[i].get(j)) || textArrays[i].get(j).isEmpty()) continue;
                invertedIndex.computeIfAbsent(textArrays[i].get(j),
                        _ -> new ArrayList<>()).add(new HashMap.SimpleEntry<>(i, j));
            }
        }
        System.out.println(invertedIndex);
    }
    ArrayList<Document> GetParsedDocs() throws IOException{
        ArrayList<Document> parsed = new ArrayList<>();
        parsed.add(parser.parse("https://en.wikipedia.org/wiki/Main_Page"));
        parsed.add(parser.parse("https://codeforces.com"));
        return parsed;
    }
    HashSet<String> ReadStopWords() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader("stopwords.json"));
        return new HashSet<String>((JSONArray)jsonObject.get("stopwords"));
    }
}
