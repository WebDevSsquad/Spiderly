package src;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DocumentManager {
    ConcurrentHashMap<String, List<Map.Entry<Integer, Integer>>> invertedIndex = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, Integer> DF = new ConcurrentHashMap<>();
    ConcurrentHashMap<String, HashMap<Integer, Integer>> TF = new ConcurrentHashMap<>();
    ConcurrentLinkedQueue<Document> docs;
    private final Parser parser;
    DocumentManager() {
        parser = new Parser();
        try {
            docs = GetParsedDocs();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    ConcurrentLinkedQueue<Document> GetParsedDocs() throws IOException {
        //Only testing
        ConcurrentLinkedQueue<Document> parsed = new ConcurrentLinkedQueue<>();
        parsed.offer(parser.parse("https://en.wikipedia.org/wiki/Main_Page"));
        parsed.offer(parser.parse("https://medium.com/"));
        parsed.offer(parser.parse("https://codeforces.com/"));
        return parsed;
    }
}

