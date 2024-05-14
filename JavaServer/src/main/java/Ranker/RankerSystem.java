package Ranker;

import Crawler.Crawler;
import Crawler.URLManager;
import QueryProcessing.QueryProcessing;
import com.mongodb.client.*;
import org.apache.commons.lang3.tuple.Pair;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;

import javax.print.Doc;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Integer.valueOf;
import static java.lang.Math.log;
import static java.lang.Thread.sleep;


public class RankerSystem {

    // Logger
    private static final Logger logger = Logger.getLogger(RankerSystem.class.getName());
    private static final String connectionString = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "Crawler";
    private static final String SECONDARY_DATABASE_NAME = "Ranker";

    private static final double DAMPINGFACTOR = 0.825;

    private static final double ERRORMARGIN = .0001;
    public static void main(String[] args) {
        runRankerSystem();
    }
    public static void runRankerSystem() {

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
        popularity.computePageRank();
        PageRank.savePageRank(popularity.getPageRank(), pageRankCollection);
    }

    public ArrayList<Map.Entry<Document, PageScorer>> queryRanker(ArrayList<String> words) throws IOException, ParseException {
        // Connect to MongoDB
        MongoClient mongoClient = MongoClients.create(connectionString);

        MongoDatabase indexerDb = mongoClient.getDatabase("Indexer");
        MongoCollection<Document> dfTfCollection = indexerDb.getCollection("indexer_collection");

        MongoDatabase crawlerDb = mongoClient.getDatabase("Crawler");
        MongoCollection<Document> documentsCollection = crawlerDb.getCollection("documents");

        MongoDatabase rankerDb = mongoClient.getDatabase("Ranker");
        MongoCollection<Document> pageRankCollection = rankerDb.getCollection("pageRank");

        PageScorer.N = documentsCollection.countDocuments();

        HashMap<Document, PageScorer> pageScores = new HashMap<Document, PageScorer>();
        for (String word : words) {
            if (!QueryProcessing.containsSpace(word)) {
                AtomicInteger DF = new AtomicInteger();
                List<Document> relatedDocs = fetchRelatedDocs(word, dfTfCollection, DF);
                if (relatedDocs == null) continue;
                fetchIndexedDocs(relatedDocs, word, DF, documentsCollection, pageScores);
            } else {
                fetchPhraseIndexedDocs(word, dfTfCollection, documentsCollection, pageScores);
            }
        }

        ArrayList<Map.Entry<Document, PageScorer>> sortedEntries = orderQueryResult(pageScores, pageRankCollection);

        return sortedEntries;
    }

    private List<Document> fetchRelatedDocs(String word, MongoCollection<Document> dfTfCollection, AtomicInteger DF) {
        // Fetch all documents from df_tf collection
        Document termQuery = new Document("term", word);
        Document dfTfDoc = dfTfCollection.find(termQuery).first();

        if (dfTfDoc == null) return null;
        int currDF = dfTfDoc.get("DF", Integer.class);
        DF.set(currDF);

        System.out.println(STR."Term: \{word}, DF: \{DF}");
        List<Document> documentsObject = dfTfDoc.getList("documents", Document.class);

        return documentsObject;
    }

    private void fetchIndexedDocs(List<Document> relatedDocsObject,
                                  String word,
                                  AtomicInteger DF,
                                  MongoCollection<Document> documentsCollection,
                                  HashMap<Document, PageScorer> pageScores) {

        for (Document doc : relatedDocsObject) {
            Integer[] TF = { 0, 0, 0 };
            TF[0] = doc.getInteger("tf_header");
            TF[1] = doc.getInteger("tf_title");
            TF[2] = doc.getInteger("tf_text");
            String docId = doc.getObjectId("docId").toString();
            Document document = documentsCollection.find(new Document("_id", new ObjectId(docId))).first();
            if (document != null) {
                String url = document.getString("url");
                System.out.println(STR."ID: \{docId}, URL: \{url}, TF Header: \{TF[0]}, TF Title: \{TF[1]}, TF Text \{TF[2]}");
                PageScorer scorer;
                if (pageScores.containsKey(document)) {
                    scorer = pageScores.get(document);
                    scorer.addWord(word);
                    scorer.addTF(TF);
                    scorer.addDF(DF.get());
                } else {
                    scorer = new PageScorer();
                    scorer.addWord(word);
                    scorer.addTF(TF);
                    scorer.addDF(DF.get());
                }
                pageScores.put(document, scorer);

            } else {
                logger.log(Level.SEVERE, STR."Document with ID \{docId} not found in documents table");
            }
        }

    }

    private void fetchPhraseIndexedDocs(String phrase,
                                        MongoCollection<Document> dfTfCollection,
                                        MongoCollection<Document> documentsCollection,
                                        HashMap<Document, PageScorer> pageScores) throws IOException, ParseException {
        System.out.println(phrase);
        ArrayList<String> tokens = QueryProcessing.processQuery(phrase);
        int size = tokens.size();

        // Get pages for all words
        HashMap<String, ArrayList<Document>> pagesInfo = new HashMap<>();
        for (String word : tokens) {
            AtomicInteger DF = new AtomicInteger();
            List<Document> relatedDocs = fetchRelatedDocs(word, dfTfCollection, DF);
            // If a word doesn't exist then the phrase doesn't exist fully
            if (relatedDocs == null) return;

            HashSet<String> relatedPages = new HashSet<>();
            for (Document doc : relatedDocs) {
                String docId = doc.getObjectId("docId").toString();
                if (pagesInfo.containsKey(docId)) {
                    pagesInfo.get(docId).add(doc);
                } else {
                    ArrayList<Document> docList = new ArrayList<>();
                    docList.add(doc);
                    pagesInfo.put(docId, docList);
                }
            }
        }

        Integer DF = 0;
        // Get common pages for all words
        HashMap<String, PageScorer> phraseScores = new HashMap<>();
        HashMap<String, ArrayList<Document>> commonPages = new HashMap<>();
        for (Map.Entry<String, ArrayList<Document>> doc : pagesInfo.entrySet()) {
            if (doc.getValue().size() == size) {
                System.out.println(STR."Contains phrase of size: \{size}");

                // get indices
                ArrayList<ArrayList<IndexPair>> indices = new ArrayList<>();
                for (Document docInfo : doc.getValue()) {
                    indices.add(getWordIndices(docInfo));
                }
                Integer[] TF = { 0, 0, 0 };
                if (checkPhraseOccurrence(indices, size, TF)) {
                    DF++;
                    System.out.println(STR."doc: \{doc.getKey()}, TF Header: \{TF[0]}, TF Title: \{TF[1]}, TF Text \{TF[2]}");
                    PageScorer scorer = new PageScorer();
                    scorer.addTF(TF);
                    scorer.addWord(phrase);
                    phraseScores.put(doc.getKey(), scorer);
                }
            }
        }

        for (Map.Entry<String, PageScorer> doc : phraseScores.entrySet()) {
            PageScorer currScorer = doc.getValue();
            currScorer.addDF(DF);
            String docId = doc.getKey();
            Document document = documentsCollection.find(new Document("_id", new ObjectId(docId))).first();
            if (document != null) {
                String url = document.getString("url");
                PageScorer scorer;
                if (pageScores.containsKey(document)) {
                    scorer = pageScores.get(document);
                    scorer.append(currScorer);
                } else {
                    scorer = currScorer;
                }
                pageScores.put(document, scorer);
            } else {
                logger.log(Level.SEVERE, STR."Document with ID \{docId} not found in documents table");
            }
        }
    }

    private ArrayList<IndexPair> getWordIndices(Document doc) {
        ArrayList<IndexPair> translatedIndices = new ArrayList<IndexPair>();

        List<Document> docIndices = doc.getList("indices", Document.class);

        for (Document docIndex : docIndices) {
            int index = docIndex.getInteger("index");
            String type = docIndex.getString("type");
            translatedIndices.add(new IndexPair(index, type));
        }
        System.out.println();

        return translatedIndices;
    }

    private boolean checkPhraseOccurrence(ArrayList<ArrayList<IndexPair>> indices, final int N, Integer[] TF) {
        int[] idx = new int[N];
        int j = 1;
        boolean found = false;
        while (true) {
            String type = null;
            for (; j < N; j++) {
                if (idx[j - 1] >= indices.get(j - 1).size()) return found;
                if (idx[j] >= indices.get(j).size()) return found;
                int first = indices.get(j - 1).get(idx[j - 1]).index();
                int sec = indices.get(j).get(idx[j]).index();
                if (first + 1 == sec) continue;
                if (sec < first) {
                    idx[j]++;
                    j--;
                } else {
                    idx[0]++;
                    j = 1;
                    break;
                }
            }
            if (j == N) {
                found = true;
                type = indices.get(0).get(idx[0]).type();
                idx[0]++;
                j = 1;
                if (type.equals("header")) {
                    TF[0]++;
                } else if (type.equals("title")) {
                    TF[1]++;
                } else {
                    TF[2]++;
                }
            }
            if (idx[0] >= indices.get(0).size()) break;
        }
        return found;
    }


    private ArrayList<Map.Entry<Document, PageScorer>> orderQueryResult (HashMap<Document, PageScorer> pageScores,
                                                                         MongoCollection<Document> pageRankCollection) {

        for (Map.Entry<Document, PageScorer> entry : pageScores.entrySet()) {
            Document document = entry.getKey();
            PageScorer scorer = entry.getValue();
            String url = document.getString("url");
            Document pageRankDoc = pageRankCollection.find(new Document("url", url)).first();
            if (pageRankDoc != null) {
                scorer.updateScore();
                scorer.pageRank = pageRankDoc.getDouble("pageRank");
            } else {
                logger.log(Level.SEVERE, STR + url + " isn't ranked");
            }
        }

        // Sort the HashMap by values
        ArrayList<Map.Entry<Document, PageScorer>> sortedEntries = new ArrayList<>(pageScores.entrySet());
        // Sort the entries by value in descending order
        sortedEntries.sort((a, b) -> Double.compare(b.getValue().finalScore(), a.getValue().finalScore()));

        return sortedEntries;
    }

    private record IndexPair (int index, String type) {}

}