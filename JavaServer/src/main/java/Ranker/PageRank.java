package Ranker;

import com.mongodb.client.MongoCollection;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.Document;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class PageRank {
    // P' = d . T . P + (1-d) . (1/n) . 1
    // T : transition matrix
    // P : initial value of the page rank
    // n : number of the crawled pages
    // 1 : n*1 matrix with all ones
    // d : dumping factor to avoid problems like "Dangling nodes" and "Disconnected components"

    private final double dampingFactor;
    private final double[][] transitionMatrix;
    private final URLRankPair[] pageRank;
    private final int N;
    private final double errorMargin;

    public PageRank(double dumpingFactor, double errorMargin, int N, double[][] transitionMatrix, URLRankPair[] pageRank) {
        this.dampingFactor = dumpingFactor;
        this.transitionMatrix = transitionMatrix;
        this.pageRank = pageRank;
        this.N = N;
        this.errorMargin = errorMargin;
    }

    public static String getHash(String url) {
        return DigestUtils.md5Hex(url).toUpperCase();
    }

    public URLRankPair[] getPageRank() {
        return pageRank;
    }


    /**
     * Computing the page the algorithm until certain error margin
     */
    public void computePageRank() {

        URLRankPair[] prevPageRank = new URLRankPair[N];
        boolean firstTime = true;
        for (int i = 0; i < N; i++) prevPageRank[i] = new URLRankPair(pageRank[i].getPageRank(), pageRank[i].getUrl());

        while (true) {
            double maxError = -1;
            if (!firstTime) {
                for (int i = 0; i < N; i++) {
                    maxError = Math.max(Math.abs(prevPageRank[i].getPageRank() - pageRank[i].getPageRank()), maxError);
                }
                if (maxError <= errorMargin) break;
            }
            for (int i = 0; i < N; i++) {
                prevPageRank[i].setPageRank(pageRank[i].getPageRank());
            }

            for (int i = 0; i < N; i++) {
                double res = 0;
                for (int j = 0; j < N; j++) {
                    res += prevPageRank[j].getPageRank() * transitionMatrix[i][j];
                }
                pageRank[i].setPageRank(res * dampingFactor + (1 - dampingFactor) / N);
            }

            //normalization
            double norm  = 0;
            for(int i = 0;i<N;i++) norm  += pageRank[i].getPageRank() * pageRank[i].getPageRank();
            norm = Math.sqrt(norm);
            for(int i = 0;i<N;i++) pageRank[i].setPageRank(pageRank[i].getPageRank()/norm);

            firstTime = false;
        }
    }
    /**
     * initializing the matrices to start the page rank algorithm
     */
    public static InitializationResult initializeData(MongoCollection<Document> collection, MongoCollection<Document> documents) {
        // Construct the query filter based on the condition
        Document queryFilter = new Document("isCrawled", true);
        HashMap<String, ArrayList<String>> hashedUrls = new HashMap<>();
        HashMap<String, Integer> crawledUrls = new HashMap<>();

        //Get the urls(crawled) and its parents and assign each crawled url an index
        int N = 0;
        for (Document doc : collection.find(queryFilter)) {
            String hash = doc.getString("hash");
            ArrayList<String> parents = (ArrayList<String>) doc.get("parents");
            hashedUrls.put(hash, parents);
            crawledUrls.put(hash, N);
            N++;
        }
        //--------------------------------------------------------------------------------------------------------------

        //-----------------------------------------------Initializations------------------------------------------------
        URLRankPair[] pageRank = new URLRankPair[N];
        double[][] transitionMatrix = new double[N][N];
        int[] freq = new int[N];

        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) transitionMatrix[i][j] = 0;
        for (int i = 0; i < N; i++) pageRank[i] = new URLRankPair((double) 1 / N, String.valueOf(i));
        for (int i = 0; i < N; i++) freq[i] = 0;
        //--------------------------------------------------------------------------------------------------------------

        //Getting the crawled urls and set all of them to rank 1/N as start and assign with them the original url
        //according to the index we set before
        for (Document doc : documents.find()) {
            String url = doc.getString("url");
            pageRank[crawledUrls.get(getHash(url))].setPageRank((double) 1 / N);
            pageRank[crawledUrls.get(getHash(url))].setUrl(url);
        }
        //--------------------------------------------------------------------------------------------------------------

        //for each destination url (rows) we get the corresponding index of it and then for each parent (columns)
        // "we are concerned with the crawled ones only" we put at the corresponding location in the matrix 1
        //also counting the number of ones in each column
        for (String dest : hashedUrls.keySet()) {
            int i = crawledUrls.get(dest);
            ArrayList<String> parents = hashedUrls.get(dest);
            for (String parent : parents) {
                if (crawledUrls.containsKey(parent)) {
                    int j = crawledUrls.get(parent);
                    freq[j]++;
                    transitionMatrix[i][j] = 1;
                }
            }
        }
        //--------------------------------------------------------------------------------------------------------------

        //divide each cell with its column fre
        // this leads to the sum of each column is equal to 1  "left stochastic matrix"
        for (int i = 0; i < N; i++) for (int j = 0; j < N; j++) {
            if(freq[j] == 0) continue;
            transitionMatrix[i][j] /= freq[j];
        }

        return new InitializationResult(transitionMatrix, pageRank, N);
    }

    public static void savePageRank(URLRankPair[] pageRank, MongoCollection<Document> collection) {
        //sort desc to map the pageRank to values between 1 - N
        Arrays.sort(pageRank);
        for (int i = 0; i < pageRank.length; i++) {
            Document document = new Document("pageRank", pageRank[i].getPageRank()).append("url", pageRank[i].getUrl());
            collection.insertOne(document);
        }
    }

}

record InitializationResult(double[][] transitionMatrix, URLRankPair[] pageRank, int pageCount) { }

class URLRankPair implements Comparable<URLRankPair> {
    private double pageRank;
    private String url;

    public URLRankPair(double pageRank, String url) {
        this.pageRank = pageRank;
        this.url = url;
    }

    public double getPageRank() {
        return pageRank;
    }

    public String getUrl() {
        return url;
    }

    public void setPageRank(double rank) {
        this.pageRank = rank;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public int compareTo(URLRankPair other) {
        return Double.compare(other.pageRank, this.pageRank);
    }
}