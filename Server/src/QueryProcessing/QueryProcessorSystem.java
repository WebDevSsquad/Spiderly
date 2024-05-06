package QueryProcessing;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.*;

public class QueryProcessorSystem {
    public static void main(String [] args){
        Scanner input = new Scanner(System.in);
        String word = input.nextLine();
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase crawlerDatabase = mongoClient.getDatabase("Crawler");
        MongoDatabase indexerDatabase = mongoClient.getDatabase("Indexer");
        MongoDatabase rankerDatabase = mongoClient.getDatabase("Ranker");
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
//        Document doc = documentsCollection.find(new Document("url","https://de.wikipedia.org/")).first();
//        for(String key : doc.keySet()){
//            System.out.printf("key: %s , value: %s\n",key,doc.get(key));
//        }
        //loop through documents to
        mongoClient.close();
    }
}




//Document dfDoc = new Document()
//                    .append("term", entry.getKey())
//                    .append("frequency", entry.getValue());

//        MongoCollection<Document> documentsCollection = crawlerDatabase.getCollection("documents");
//        long totalDocuments = documentsCollection.countDocuments();
//        MongoCollection<Document> dfCollection = indexerDatabase.getCollection("document_frequency");




//        for(Document document : dfCollection.find()){
//            int df = document.getInteger("frequency");
//            double idf = Math.log(1.0*totalDocuments/df);
//            Document idfDoc = new Document()
//                    .append("term",document.get("term"))
//                    .append("inverse_frequency",idf);
//            idfCollection.insertOne(idfDoc);
//        }

//        for(Document idfDocument : idfCollection.find()){
//            Document tfDocument = tfCollection.find(new Document("term", idfDocument.get("term"))).first();
//            Document tf = (Document) tfDocument.get("documents");
//            double idf = idfDocument.getDouble("inverse_frequency");
//
//            // Create a new tf document to store modified TF values
//            Document tfModified = new Document();
//            for (String key : tf.keySet()) {
//                tfModified.put(key, tf.getInteger(key) * idf);
//            }
//
//            Document mergedDocument = new Document()
//                    .append("term", idfDocument.get("term"))
//                    .append("idf", idfDocument.get("inverse_frequency"))
//                    .append("tf", tf)
//                    .append("tf_idf", tfModified);
//
//            indexerInfoCollection.insertOne(mergedDocument);
//        }
//        int cnt = 0;
//        MongoCollection<Document> documentCollection = crawlerDatabase.getCollection("documents");
//        for(Document document : documentCollection.find()){
//            document.append("id", cnt++);
//            // Update the Document in the collection
//            documentCollection.replaceOne(new Document("_id", document.getObjectId("_id")), document);
//        }