package QueryProcessing;

import Indexer.Stemmer;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import Indexer.Indexer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class QueryProcessing {

    public static ArrayList<String> tokenization(String query) {
        ArrayList<String> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|\\S+");
        Matcher matcher = pattern.matcher(query);

        while (matcher.find()) {
            String token = matcher.group(1); // Quoted phrase
            if (token != null) {
                tokens.add(token);
            } else {
                tokens.add(matcher.group()); // Non-quoted token
            }
        }

        return tokens;
    }

    private static ArrayList<String> removeStopWords(ArrayList<String> tokens) throws IOException, ParseException {
        // Define stop words
        HashSet<String> stopWordsList = ReadStopWords();
        // Remove stop words and perform stemming
        ArrayList<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!stopWordsList.contains(token)) {
                filteredTokens.add(token);
            }
        }

        return filteredTokens;
    }

    private static HashSet<String> ReadStopWords() throws IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader("src/stopwords.json"));
        return new HashSet<String>((JSONArray) jsonObject.get("stopwords"));
    }

    private static ArrayList<String> stemmer(ArrayList<String> tokens) {
        ArrayList<String> stemmedTokens = new ArrayList<>();
        Stemmer stemmer = new Stemmer();
        for (String token : tokens) {
            stemmedTokens.add(stemmer.Stem(token));
        }
        return stemmedTokens;
    }

    public static ArrayList<String> processQuery(String query) throws IOException, ParseException {
        ArrayList<String> tokens = tokenization(query);
        tokens = removeStopWords(tokens);
        tokens = stemmer(tokens);
        return tokens;
    }

    public static boolean containsSpace(String str) {
        return str.contains(" ");
    }

}
