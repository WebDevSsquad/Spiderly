package QueryProcessing;

import Indexer.Stemmer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class QueryProcessing {

    private static ArrayList<String> tokenization(String query) {
        ArrayList<String> tokens = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(query);
        while(tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken().toLowerCase());
        }
        return tokens;
    }

    private static ArrayList<String> removeStopWords(ArrayList<String> tokens) {
        // Define stop words
        String[] stopWords = {"a", "an", "and", "the", "is", "are", "in", "on", "at", "to", "from", "by", "with", "for", "of"};

        ArrayList<String> stopWordsList = new ArrayList<>(Arrays.asList(stopWords));

        // Remove stop words and perform stemming
        ArrayList<String> filteredTokens = new ArrayList<>();
        for (String token : tokens) {
            if (!stopWordsList.contains(token)) {
                filteredTokens.add(token);
            }
        }

        return filteredTokens;
    }

    private static ArrayList<String> stemmer(ArrayList<String> tokens) {
        ArrayList<String> stemmedTokens = new ArrayList<>();
        Stemmer stemmer = new Stemmer();
        for (String token : tokens) {
            stemmedTokens.add(stemmer.Stem(token));
        }
        return stemmedTokens;
    }

    public static ArrayList<String> processQuery(String query) {
        ArrayList<String> tokens = tokenization(query);
        tokens = removeStopWords(tokens);
        tokens = stemmer(tokens);
        return tokens;
    }
}
