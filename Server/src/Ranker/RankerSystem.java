package Ranker;

import Crawler.Crawler;
import Crawler.URLManager;
import com.mongodb.client.*;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.lang.reflect.Array;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.valueOf;
import static java.lang.Math.log;


public class RankerSystem {

    // Logger
    private static final Logger logger = Logger.getLogger(RankerSystem.class.getName());
    private final String connectionString = "mongodb://localhost:27017";
    private final String DATABASE_NAME = "Crawler";
    private final String SECONDARY_DATABASE_NAME = "Ranker";

    private final double DAMPINGFACTOR = 0.825;

    private final double ERRORMARGIN = .0001;

    public void main(String[] args) {

        //----------------------------------------------------Database--------------------------------------------------

        //initialise the database connection
        // Connect to MongoDB server
        MongoClient mongoClient = MongoClients.create(connectionString);

        // Access a MongoDB database
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        MongoDatabase secondaryDatabase = mongoClient.getDatabase(SECONDARY_DATABASE_NAME);

        // Get a collection from the database

        MongoCollection<Document> visitedLinksCollection = database.getCollection("visited_urls");
        MongoCollection<Document> documentsCollection = database.getCollection("documents");
        MongoCollection<Document> pageRankCollection = secondaryDatabase.getCollection("pageRank");
        MongoCollection<Document> relevanceCollection = secondaryDatabase.getCollection("relevance");
        //--------------------------------------------------------------------------------------------------------------
        InitializationResult initialValues = PageRank.initializeData(visitedLinksCollection,documentsCollection);
        PageRank popularity = new PageRank(DAMPINGFACTOR, ERRORMARGIN,
                                    initialValues.pageCount(),
                                    initialValues.transitionMatrix(),
                                    initialValues.pageRank());
//        Relevance relevance = new Relevance(Relevance.loadDocuments(documentsCollection));
//        relevance.calculateTF();
//        relevance.calculateIDF();
        popularity.computePageRank();
        PageRank.savePageRank(popularity.getPageRank(), pageRankCollection);
//        Relevance.saveScores(relevance.getDocuments(),relevanceCollection);
    }

    public ArrayList<Map.Entry<Document, Double>> queryRanker(ArrayList<String> words) {
        // Connect to MongoDB
        MongoClient mongoClient = MongoClients.create(connectionString);

        MongoDatabase indexerDb = mongoClient.getDatabase("Indexer");
        MongoCollection<Document> dfTfCollection = indexerDb.getCollection("df_tf");

        MongoDatabase crawlerDb = mongoClient.getDatabase("Crawler");
        MongoCollection<Document> documentsCollection = crawlerDb.getCollection("documents");

        MongoDatabase rankerDb = mongoClient.getDatabase("Ranker");
        MongoCollection<Document> pageRankCollection = rankerDb.getCollection("pageRank");

        long N = documentsCollection.countDocuments();

        HashMap<Document, Double> pageScores = new HashMap<Document, Double>();
        for (String word : words) {
            Integer DF = 0;
            Document relatedDocs = fetchRelatedDocs(word, dfTfCollection, DF);
            if (relatedDocs == null) continue;
            fetchIndexedDocs(relatedDocs, DF, documentsCollection, pageScores, N);
        }

        ArrayList<Map.Entry<Document, Double>> sortedEntries = orderQueryResult(pageScores, pageRankCollection);

        return sortedEntries;
    }

    private Document fetchRelatedDocs(String word, MongoCollection<Document> dfTfCollection, Integer df) {
        // Fetch all documents from df_tf collection
        Document termQuery = new Document("term", word);
        Document dfTfDoc = dfTfCollection.find(termQuery).first();

        if (dfTfDoc == null) return null;
        df = dfTfDoc.get("df", Integer.class);

        System.out.println(STR."Term: \{word}, DF: \{df}");
        Document documentsObject = dfTfDoc.get("documents", Document.class);

        return documentsObject;
    }

    private void fetchIndexedDocs(Document relatedDocsObject,
                                  Integer df,
                                  MongoCollection<Document> documentsCollection,
                                  HashMap<Document, Double> pageScores,
                                  long N) {

        for (String docId : relatedDocsObject.keySet()) {
            int tf = relatedDocsObject.getInteger(docId);
            Document document = documentsCollection.find(new Document("_id", new ObjectId(docId))).first();
            if (document != null) {
                String url = document.getString("url");
                System.out.println(STR."ID: \{docId}, URL: \{url}, TF: \{tf}");
                double score = tf * log((double) N / df);
                if (pageScores.containsKey(document)) {
                    pageScores.put(document, pageScores.get(document) + score);
                } else {
                    pageScores.put(document, score);
                }

            } else {
                logger.log(Level.SEVERE, STR."Document with ID \{docId} not found in documents table");
            }
        }

    }

    private void fetchPhraseIndexedDocs(String phrase,
                                  MongoCollection<Document> dfTfCollection,
                                  MongoCollection<Document> documentsCollection,
                                  HashMap<Document, Double> pageScores,
                                  long N) {

        // Fetch all documents from df_tf collection
        Document termQuery = new Document("term", phrase);
        Document dfTfDoc = dfTfCollection.find(termQuery).first();

        if (dfTfDoc == null) return;
        int df = dfTfDoc.get("df", Integer.class);

        System.out.println(STR."Term: \{phrase}, DF: \{df}");
        Document documentsObject = dfTfDoc.get("documents", Document.class);
        for (String docId : documentsObject.keySet()) {
            int tf = documentsObject.getInteger(docId);
            Document document = documentsCollection.find(new Document("_id", new ObjectId(docId))).first();
            if (document != null) {
                String url = document.getString("url");
                System.out.println(STR."Term: \{phrase}, ID: \{docId}, URL: \{url}, TF: \{tf}");
                double score = tf * log((double) N / df);
                if (pageScores.containsKey(document)) {
                    pageScores.put(document, pageScores.get(document) + score);
                } else {
                    pageScores.put(document, score);
                }

            } else {
                logger.log(Level.SEVERE, STR."Document with ID \{docId} not found in documents table");
            }
        }

    }

    private ArrayList<Map.Entry<Document, Double>> orderQueryResult (HashMap<Document, Double> pageScores,
                                                                     MongoCollection<Document> pageRankCollection) {

        for (Map.Entry<Document, Double> entry : pageScores.entrySet()) {
            Document document = entry.getKey();
            Double score = entry.getValue();
            String url = document.getString("url");
            Document pageRankDoc = pageRankCollection.find(new Document("url", url)).first();
            if (pageRankDoc != null) {
                score *= pageRankDoc.getDouble("pageRank"); // Updated to use pageRankDoc here
                // Update the score in the hashmap
                pageScores.put(document, score);
            } else {
                logger.log(Level.SEVERE, STR + url + " isn't ranked");
            }
        }

        // Sort the HashMap by values
        ArrayList<Map.Entry<Document, Double>> sortedEntries = new ArrayList<>(pageScores.entrySet());
        // Sort the entries by value in descending order
        sortedEntries.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        return sortedEntries;
    }
}