package SearchHandler;

import QueryProcessing.QueryProcessing;
import Ranker.PageScorer;
import Ranker.RankerSystem;

import com.mongodb.client.*;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        ArrayList<String> tokens = QueryProcessing.processQuery(query.toLowerCase());

        for (String word : tokens) {
            System.out.println(word);
        }
        System.out.println();

        RankerSystem rankerSystem = new RankerSystem();
        ArrayList<Map.Entry<Document, PageScorer>> arr =  rankerSystem.queryRanker(tokens);

        if (!arr.isEmpty()) addSuggestion(query.toLowerCase());

        HashMap<String,Object> map = new HashMap<>();
        ArrayList<Document> result = new ArrayList<>();

        for (Map.Entry<Document, PageScorer> page : arr) {
            Document doc = page.getKey();
            PageScorer scorer = page.getValue();
            Document pageInfo = new Document();
            pageInfo.put("title", doc.getString("title"));
            pageInfo.put("url", doc.getString("url"));
            pageInfo.put("words", scorer.words);

            result.add(pageInfo);
        }

        map.put("documents",result);
        return map;
    }

    public void addSuggestion(String query) {
        final String connectionString = "mongodb://localhost:27017";
        final String DATABASE_NAME = "SearchEngine";
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase searchDB = mongoClient.getDatabase(DATABASE_NAME);
        MongoCollection<Document> suggestionsCollection = searchDB.getCollection("suggestions");
        Document term = new Document("term", query);
        long found = suggestionsCollection.countDocuments(term);
        System.out.println(found);
        if (found == 0) {
            suggestionsCollection.insertOne(term);
        }
    }

    public HashMap<String, Object> getSuggestions() {
        final String connectionString = "mongodb://localhost:27017";
        final String DATABASE_NAME = "SearchEngine";
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase searchDB = mongoClient.getDatabase(DATABASE_NAME);
        MongoCollection<Document> suggestionsCollection = searchDB.getCollection("suggestions");
        FindIterable<Document> docs = suggestionsCollection.find();

        ArrayList<String> suggestions = new ArrayList<>();

        for (Document doc : docs) {
            suggestions.add(doc.getString("term"));
        }

        HashMap<String,Object> map = new HashMap<>();
        map.put("suggestions", suggestions);
        return map;
    }
}
