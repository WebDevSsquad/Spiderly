package Crawler;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;

public class Parser {
    /**
     * Checks if the given URL is allowed based on the robots.txt rules.
     *
     * @param urlPath The URL to check.
     * @param disallowedPaths The list of disallowed paths from robots.txt.
     * @return true if the URL is allowed, false otherwise.
     */
    public boolean isUrlAllowed(String urlPath, List<String> disallowedPaths) {
        // Iterate over disallowed paths for the section's user-agent
        System.out.println(STR."Checking \{urlPath}");
        for (String disallowPath : disallowedPaths) {
            System.out.println(disallowPath);
            if (urlPath.startsWith(disallowPath)) {
                return false; // URL matches a disallowed path
            }
        }
        return true; // URL does not match any disallowed path
    }

    public Document parse(String url) {
        try {
            Connection connect = Jsoup.connect(url);
            Document doc = connect.get();
            int code = connect.response().statusCode();
            if (code >= 200 && code < 300) {
                System.out.println(doc.title());
                System.out.println(STR."Link: \{url}");
                return doc;
            }
        } catch (IOException e) {
            return null;
        }
        return null;
    }

    /**
     * Extracts the host domain from a given URL string and reads the robots.txt file.
     *
     * @param hostDomain The URL string from which to read the robots.txt file.
     * @return The content of the robots.txt file, or an empty string if reading fails or the file does not exist.
     */
    public List<String> readRobotsTxt(String hostDomain) {
        try {
            // Construct the URL for the robots.txt file
            String robotsUrl = STR."http://\{hostDomain}/robots.txt";

            // Fetch the content of the robots.txt file using Jsoup
            Document doc = Jsoup.connect(robotsUrl).get();
            System.out.println(doc.text());
            // Get the text content of the document
            return extractDisallowedPaths(doc.text());
        } catch (HttpStatusException e) {
            // Handle the case where the robots.txt file does not exist
            System.out.println("Robots.txt file not found for the provided URL.");
        } catch (IOException e) {
            // If an exception occurs during URL processing or file reading, print the stack trace
            e.printStackTrace();
        }

        // Return an empty string if reading fails or the file does not exist
        return null;
    }

    /**
     * Extracts the disallowed paths for the '*' user-agent from the robots.txt content.
     *
     * @param robotsTxtContent The content of the robots.txt file.
     * @return A list of disallowed paths for the '*' user-agent.
     */
    public List<String> extractDisallowedPaths(String robotsTxtContent) {
        try {
            List<String> disallowedPaths = new ArrayList<>();
            String filteredSection = robotsTxtContent.split("User-agent:\\s*\\*")[1];
            filteredSection = filteredSection.split("(?i)^User-agent:")[0];
            System.out.println(filteredSection);

            // Define the pattern to match Disallow directives and paths
            Pattern pattern = Pattern.compile("Disallow:\\s*(/\\S*)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(filteredSection);

            // Iterate over matches and extract paths
            while (matcher.find()) {
                String path = matcher.group(1).trim();
                disallowedPaths.add(path);
            }
            return disallowedPaths;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println(STR."There is no * specified: \{e}");
            return null;
        }
    }

    /*public static void main(String[] args) {
        Parser reader = new Parser();
        String host = "www.wikipedia.org";
        List<String> disallowedPaths = reader.readRobotsTxt(host);
        System.out.println("Disallowed Paths for '*' User-agent:");
        for (String path : disallowedPaths) {
            System.out.println(path);
        }
        System.out.println("Is allowed? " + reader.isUrlAllowed("/wiki/Wikipedia:SDU/", disallowedPaths));
    }*/
}
