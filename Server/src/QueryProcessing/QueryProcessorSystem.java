package QueryProcessing;

import com.mongodb.client.*;
import org.bson.Document;

import javax.print.Doc;
import java.util.*;

public class QueryProcessorSystem {
    public static void main(String [] args){
        Scanner input = new Scanner(System.in);
        String word = input.nextLine();
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase crawlerDatabase = mongoClient.getDatabase("Crawler");
        MongoDatabase indexerDatabase = mongoClient.getDatabase("Indexer");
        MongoDatabase rankerDatabase = mongoClient.getDatabase("Ranker");
        MongoCollection<Document> invertedIndexCollection = indexerDatabase.getCollection("inverted_index");
        MongoCollection<Document> crawledDocumentsCollection = crawlerDatabase.getCollection("documents");


        Document wantedDocument = invertedIndexCollection.find(new Document("originalWord",word)).first();
        if(wantedDocument != null){
            String stemmedWord = wantedDocument.getString("stemmedWord");
            FindIterable<Document> wordsOfSameStemming = invertedIndexCollection.find(new Document("stemmedWord",stemmedWord));
            for(Document wordDocument : wordsOfSameStemming){
                ArrayList<Document> innerDocuments = (ArrayList<Document>) wordDocument.get("documents");
                for(Document innerObject   : innerDocuments){
                    Document originDocument = crawledDocumentsCollection.find(new Document("_id",innerObject.get("docId"))).first();
                    System.out.println(originDocument.toJson());
                }
            }

        }
//        Document doc = documentsCollection.find(new Document("url","https://de.wikipedia.org/")).first();
//        for(String key : doc.keySet()){
//            System.out.printf("key: %s , value: %s\n",key,doc.get(key));
//        }
        //loop through documents to
        mongoClient.close();

        /*
            MongoCollection<Document> documentsCollection = crawlerDatabase.getCollection("documents");
            MongoCollection<Document> indexerInfoCollection = indexerDatabase.getCollection("indexer_info");
            MongoCollection<Document> pageRankCollection = rankerDatabase.getCollection("pageRank");
            Document query = new Document("term", word);
            Document wordIndexerInfoDocument = indexerInfoCollection.find(query).first();
            Document innerTFIDFDocument = (Document)wordIndexerInfoDocument.get("tf_idf");
            ArrayList<Pair> documents = new ArrayList<>();
            for(String key : innerTFIDFDocument.keySet()){
                Document indexedDocument = documentsCollection.find(new Document("id",key)).first();
                Document pageRankedDocument = pageRankCollection.find(new Document("url",indexedDocument.get("url"))).first();
                double tf_idf = innerTFIDFDocument.getDouble(key);
                double pageRank = pageRankedDocument.getDouble("pageRank");
                documents.add(new Pair(indexedDocument,Math.sqrt(tf_idf*pageRank)));
                System.out.printf("key: %s , value: %s\n",key,innerTFIDFDocument.get(key));
            }
            Collections.sort(documents, Comparator.reverseOrder());

            for(Pair p : documents){
                System.out.println(p.getGeometricMean());
                System.out.println(p.getDocument().toJson());
            }
        *
        * */
    }
}




//Document dfDoc = new Document()
//                    .append("term", entry.getKey())
//                    .append("frequency", entry.getValue());

//        MongoCollection<Document> documentsCollection = crawlerDatabase.getCollection("documents");
//        long totalDocuments = documentsCollection.countDocuments();
//        MongoCollection<Document> dfCollection = indexerDatabase.getCollection("document_frequency");



