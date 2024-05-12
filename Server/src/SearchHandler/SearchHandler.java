package SearchHandler;

import QueryProcessing.QueryProcessing;
import Ranker.RankerSystem;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Map;

public class SearchHandler {
    public static void main(String[] args) {
        String query = "Run in \"New York";

        long start = System.currentTimeMillis();
        ArrayList<Map.Entry<Document, Double>> resultPages = searchQuery(query);
        long end = System.currentTimeMillis();
        int i = 1;
        for (Map.Entry<Document, Double> page : resultPages) {
            Document document = page.getKey();
            double score = page.getValue();
            System.out.println(STR."\{i++}- score: \{score}, doc: \{document.getString("title")}");
        }
        System.out.println((end - start) / 1000);
    }

    public static ArrayList<Map.Entry<Document, Double>> searchQuery (String query) {
        ArrayList<String> tokens = QueryProcessing.processQuery(query);

        for (String word : tokens) {
            System.out.print(word + " ");
        }
        System.out.println();

        RankerSystem rankerSystem = new RankerSystem();
        return rankerSystem.queryRanker(tokens);
    }
}
