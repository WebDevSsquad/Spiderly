package SearchHandler;

import QueryProcessing.QueryProcessing;
import Ranker.PageScorer;
import Ranker.RankerSystem;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Map;

public class SearchHandler {
    public static void main(String[] args) {
        String query = "check \"i run\" \"city\" \"liberty city running\"";

        long start = System.currentTimeMillis();
        ArrayList<Map.Entry<Document, PageScorer>> resultPages = searchQuery(query);
        long end = System.currentTimeMillis();
        int i = 1;
        for (Map.Entry<Document, PageScorer> page : resultPages) {
            Document document = page.getKey();
            double score = page.getValue().score;
            System.out.println(STR."\{i++}- score: \{score}");
        }
        System.out.println((end - start) / 1000);
    }

    public static ArrayList<Map.Entry<Document, PageScorer>> searchQuery(String query) {
        ArrayList<String> tokens = QueryProcessing.processQuery(query);

        for (String word : tokens) {
            System.out.println(word);
        }
        System.out.println();

        RankerSystem rankerSystem = new RankerSystem();
        return rankerSystem.queryRanker(tokens);
    }
}
