package Crawler;

import com.panforge.robotstxt.*;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.net.URL;

public class Parser {
    /**
     * Checks if the given URL is allowed based on the robots.txt rules.
     *
     * @param url The URL to check.
     * @return true if the URL is allowed, false otherwise.
     */
    public boolean isUrlAllowed(String url, String hostDomain) {
        // Create a URL object for the website's robots.txt file
        URL robotsTxtUrl;
        try {
            String robotsTxtStr = getRobotsTxtURL(hostDomain);
            robotsTxtUrl = new URL(robotsTxtStr);
        } catch (IOException e) {
            System.err.println(STR."Error creating URL: \{e.getMessage()}");
            return true;
        }

        // Parse the robots.txt file
        try {
            RobotsTxt robotsTxt = RobotsTxt.read(robotsTxtUrl.openStream());
            // Check if a specific user-agent is allowed to access a URL
            return robotsTxt.query("*", url);
        } catch (IOException e) {
            System.err.println(STR."Error reading robots.txt: \{e.getMessage()}");
            return true;
        }
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
     * @return The url that leads to the robots.txt file, or an empty string if reading fails or the file does not exist.
     */
    private String getRobotsTxtURL(String hostDomain) {
        return STR."http://\{hostDomain}/robots.txt";
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
        // Create a URL object for the website's robots.txt file
        URL robotsTxtUrl;
        try {
            robotsTxtUrl = new URL("https://wikipedia.com/robots.txt");
        } catch (IOException e) {
            System.err.println("Error creating URL: " + e.getMessage());
            return;
        }

        // Parse the robots.txt file
        try {
            RobotsTxt robotsTxt = RobotsTxt.read(robotsTxtUrl.openStream());
            List<String> disallowedList = robotsTxt.getDisallowList("*");

            // Print the list of disallowed URLs
            System.out.println("Disallowed URLs:");
            for (String url : disallowedList) {
                System.out.println(url);
            }
            // Check if a specific user-agent is allowed to access a URL
            boolean isAllowed = robotsTxt.query("*", "https://wikipedia.com/wiki/Wikipedia:Redirects_for_discussion");
            if (isAllowed) {
                System.out.println("URL is allowed for crawling");
            } else {
                System.out.println("URL is not allowed for crawling");
            }
        } catch (IOException e) {
            System.err.println("Error reading robots.txt: " + e.getMessage());
        }
    }*/
}
