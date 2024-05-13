package SearchHandler;

import QueryProcessing.QueryProcessing;
import Ranker.PageScorer;
import Ranker.RankerSystem;

import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchHandler {
//    public  void main(String[] args) {
//        String query = "Run sport in \"Trump campaign\"";
//
//        long start = System.currentTimeMillis();
//        ArrayList<Map.Entry<Document, PageScorer>> resultPages = searchQuery(query);
//        long end = System.currentTimeMillis();
//        int i = 1;
//        for (Map.Entry<Document, PageScorer> page : resultPages) {
//            Document document = page.getKey();
//            PageScorer scorer = page.getValue();
//            double score = scorer.finalScore();
//            System.out.println(STR."\{i++}- score: \{score} url: \{document.getString("url")} words: \{scorer.words.toString()}");
//        }
//        System.out.println((end - start) / 1000);
//    }



    public  HashMap<String,Object> searchQuery(String query) {
        long start = System.currentTimeMillis();
        ArrayList<String> tokens = QueryProcessing.processQuery(query);

        for (String word : tokens) {
            System.out.println(word);
        }
        System.out.println();

        RankerSystem rankerSystem = new RankerSystem();
        ArrayList<Map.Entry<Document, PageScorer>> arr =  rankerSystem.queryRanker(tokens);

        long end = System.currentTimeMillis();
        HashMap<String,Object> map = new HashMap<>();
        map.put("documents",arr);
        map.put("time",(end - start) / 1000);
        return map;
    }
}
