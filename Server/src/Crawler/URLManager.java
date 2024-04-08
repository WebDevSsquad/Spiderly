package Crawler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;

import org.apache.commons.validator.UrlValidator;
import org.apache.commons.codec.net.URLCodec;

public class URLManager {
    private final URLFrontier urlFrontier;

    public URLManager() {
        urlFrontier = new URLFrontier(URLFrontier.loadQueueFromFile(),
                URLFrontier.loadVisitedPages(1),
                URLFrontier.loadVisitedPages(2));
    }

    public static void Main(String[] args) {

    }

    public void addToFrontier(String url, int priority, int depth) {
        String shortenedURL = shortenURL(url);
        if (urlFrontier.addURL(shortenedURL, priority, depth))
            urlFrontier.markURL(url);
    }

    public void manageFrontier() {
        while (!urlFrontier.isEmpty()) {
            URLPriorityPair seed = urlFrontier.getNextURL();
            // Perform tasks to manage the URL frontier
            // List<String> urls = handleURL();
            // Dummy links for testing
            // urls.add("www.wikipedia.org");
            // urls.add("www.facebook.com");
            // This could include tasks such as prioritization, filtering, or concurrency control
            // Example tasks:

            // 1. URL Normalization
            // In Crawler.Parser

            // 2. URL Filtering
            // In Crawler.Parser
            // 3. URL Prioritization
            // urls = prioritizeURLs(urls);

            // 4. Concurrency Control (Optional)
            // Implement concurrency control mechanisms if needed
            // for (String url : urls) {
            //     addToFrontier(url, 0);
            // }
        }
    }

    public void handleURL(String url, int depth) {
        int priority = prioritizeURL(url);
        addToFrontier(url, priority, depth);
    }

    public boolean validURL(String url) {
        // Implement logic to shorten URLs if needed
        UrlValidator urlValidator = new UrlValidator();
        return urlValidator.isValid(url);
    }


    /**
     * Extracts the host domain from a given URL string.
     *
     * @param urlString The URL string from which to extract the host domain.
     * @return The host domain extracted from the URL string, or an empty string if extraction fails.
     */
    public String extractHost(String urlString) {
        try {
            URL url = new URL(urlString);
            String host = url.getHost();
            //System.out.println("Host domain: " + host);
            return host;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Extracts the path part of the URL after the host domain.
     *
     * @param urlString The URL from which to extract the path.
     * @return The path part of the URL after the host domain.
     */
    public String extractPath(String urlString) {
        try {
            URI uri = new URI(urlString);
            return uri.getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return null; // Return null if unable to extract URL path
        }
    }

        //checks for robot.txt
//    public  boolean isUrlAllowed(String url) {
//
//
//    }
//    private  byte[] getContentFromUrl(String url) {
//        try {
//            // Connect to the URL and fetch the content
//            Connection connection = Jsoup.connect(url);
//            Document document = connection.get();
//            String content = document.outerHtml();
//
//            // Convert the content to a byte array
//            return content.getBytes();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new byte[0];
//        }
//    }



    public String shortenURL(String url) {
        // Implement logic to shorten URLs if needed
        return url;
    }

    public String normalizeURL(String url) {
        // Implement logic to normalize URLs for consistency and comparability
        // This can include removing trailing slashes, converting to lowercase, etc.
        // 1. Cloudflare normalization
        // 2. RFC 3986 normalization

        try {
            URI uri = new URI(url);
            // 1. Remove the part after '#' (the fragment part)
            if (uri.getFragment() != null) {
                // Remake with same parts, less the fragment:
                uri = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
            }

            URI normalizedURI = uri.normalize();
            String normalizedURL = normalizedURI.toString();
            // Return the normalized url
            normalizedURL = apacheNormalization(normalizedURL);

//            if (!url.equals(normalizedURL)) Crawler.Logger.log(url + " --normalized--> " + normalizedURL);

            return normalizedURL.toLowerCase();
        } catch (URISyntaxException e) {
            Logger.logError(STR."Invalid URL: \{url}", e);
            return null;
        }
    }

    public  String apacheNormalization(String urlString) {
        try {
            // Decode percent-encoded characters
            String decodedURL = URLDecoder.decode(urlString, StandardCharsets.UTF_8);

            // Normalize decoded URL using Apache Commons Codec
            URLCodec codec = new URLCodec();
            return codec.decode(codec.encode(decodedURL));
        } catch (DecoderException | EncoderException e) {
            throw new RuntimeException(e);
        }
    }




    private int prioritizeURL(String url) {
        // Implement URL prioritization logic
        // For example, prioritize URLs based on domain importance, freshness, or relevance
        return 0;
    }

    // Dummy methods for basic structure
    private double getImportanceScore(String url) {
        // Example: Calculate importance score based on domain reputation or user ratings
        return 0.85; // Dummy importance score (range: 0 to 1)
    }

    // Dummy methods for basic structure
    private double getVisitsCount(String url) {
        // Retrieve number of visits for the URL from analytics data
        // Dummy implementation, replace with actual data retrieval logic
        return 1000; // Example: 1000 visits
    }

    public URLFrontier getUrlFrontier() {
        return urlFrontier;
    }

    public void saveState() {
        URLFrontier.saveQueueToFile(urlFrontier.getQueue());
        URLFrontier.saveVisitedPages(urlFrontier.getHashedPage(), 1);
        URLFrontier.saveVisitedPages(urlFrontier.getHashedURLs(), 2);
    }
}