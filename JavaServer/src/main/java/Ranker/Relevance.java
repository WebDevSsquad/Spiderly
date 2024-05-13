package Ranker;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Relevance {
    private final int numberOfDocuments;
    private final DocumentPair[] documents;

    public Relevance(DocumentPair[] documents) {
        this.numberOfDocuments = documents.length;
        this.documents = documents;
    }

    public DocumentPair[] getDocuments(){return documents;}


    public void calculateTF() {
        int sum = 0;
        for (DocumentPair pair : documents) {
            HashMap<String, Score> mp = pair.getWords();
            for (Score s : mp.values()) {
                sum += s.getCountInDocument();
            }
        }

        for (int i = 0; i < numberOfDocuments; i++) {
            DocumentPair pair = documents[i];
            HashMap<String, Score> mp = pair.getWords();
            for (String word : mp.keySet()) {
                Score s = mp.get(word);
                double tf = (double) s.getCountInDocument() / sum;
                s.setTf(tf);
                mp.put(word, s);
            }
            documents[i].setWords(mp);
        }
    }

    public void calculateIDF() {
        HashMap<String, Integer> freq = new HashMap<>();
        for (DocumentPair pair : documents) {
            HashMap<String, Score> mp = pair.getWords();
            for (String word : mp.keySet()) {
                if (freq.containsKey(word)) freq.put(word, freq.get(word) + 1);
                else freq.put(word, 1);
            }
        }
        for (int i = 0; i < numberOfDocuments; i++) {
            DocumentPair pair = documents[i];
            HashMap<String, Score> mp = pair.getWords();
            for (String word : mp.keySet()) {
                Score s = mp.get(word);
                double df = freq.get(word);
                s.setIdf(Math.log(numberOfDocuments / df));
            }
            documents[i].setWords(mp);
        }
    }

    public static String cleanInput(String input) {
        String newStr = input.replaceAll("[, .:;\"]", "");
        newStr = newStr.replaceAll("\\p{P}", "");
        newStr = newStr.replaceAll("\t", "");
        return newStr;
    }

    //Returns if input contains numbers or not
    public static boolean isDigit(String input) {
        String regex = "(.)*(\\d)(.)*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static DocumentPair[] loadDocuments(MongoCollection<Document> collection) {
        int numberOfDocuments = (int) collection.countDocuments();
        DocumentPair[] docs = new DocumentPair[numberOfDocuments];
        numberOfDocuments = 0;
        for (Document doc : collection.find()) {
            String[] parsedDoc = doc.getString("document").toLowerCase().split(" ");
            ObjectId id = doc.getObjectId("_id");
            HashMap<String, Score> words = new HashMap<>();
            for (String word : parsedDoc) {
                word = cleanInput(word);
                if (isDigit(word) || word.isEmpty()) continue;
                if (words.containsKey(word)) {
                    Score s = words.get(word);
                    s.setCountInDocument(s.getCountInDocument() + 1);
                    words.put(word, s);
                } else words.put(word, new Score(0, 0, 1));
            }
            docs[numberOfDocuments++] = new DocumentPair(id, words);
        }
        return docs;
    }
    public static void saveScores(DocumentPair[] documents,MongoCollection<Document> collection){
        for (DocumentPair pair : documents) {
            HashMap<String, Score> mp = pair.getWords();
            for (String word : mp.keySet()) {
                Score s  = mp.get(word);
                Document doc = new Document("word",word).append("tf",s.getTf()).append("idf",s.getIdf()).append("documentId",pair.getId());
                collection.insertOne(doc);
            }
        }
    }

}

class DocumentPair {
    private ObjectId id;
    private HashMap<String, Score> words;


    public DocumentPair(ObjectId id, HashMap<String, Score> words) {
        this.id = id;
        this.words = words;
    }

    public ObjectId getId() {
        return id;
    }

    public HashMap<String, Score> getWords() {
        return words;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setWords(HashMap<String, Score> words) {
        this.words = words;
    }

}

class Score {

    private double tf;
    private double idf;
    private int countInDocument;

    public Score(double tf, double idf, int countInDocument) {
        this.tf = tf;
        this.idf = idf;
        this.countInDocument = countInDocument;
    }

    public int getCountInDocument() {
        return countInDocument;
    }

    public double getTf() {
        return tf;
    }

    public double getIdf() {
        return idf;
    }

    public void setCountInDocument(int countInDocument) {
        this.countInDocument = countInDocument;
    }

    public void setTf(double tf) {
        this.tf = tf;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }
}
