package Ranker;

import Crawler.Crawler;
import Crawler.URLManager;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Arrays;


public class RankerSystem {
    private final String connectionString = "mongodb://localhost:27017";
    private final String DATABASE_NAME = "Crawler";
    private final String SECONDARY_DATABASE_NAME = "Ranker";

    private final double DUMPINGFACTOR = 0.825;

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
        //--------------------------------------------------------------------------------------------------------------
        InitializationResult initialValues = PageRank.initializeData(visitedLinksCollection,documentsCollection);
        PageRank popularity = new PageRank(DUMPINGFACTOR, ERRORMARGIN,
                                    initialValues.getPageCount(),
                                    initialValues.getTransitionMatrix(),
                                    initialValues.getPageRank());
        popularity.computePageRank();
        PageRank.savePageRank(popularity.getPageRank(), pageRankCollection);

    }
}
