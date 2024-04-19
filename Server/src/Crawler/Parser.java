package Crawler;

import com.panforge.robotstxt.*;
import org.jsoup.Connection;
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
    public boolean isUrlAllowed(String url, String hostDomain , URLFrontier urlFrontier) {
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

            List<String> disallowedList = robotsTxt.getDisallowList("*");

            for (String disallowed : disallowedList) {
                urlFrontier.disallowUrl(hostDomain+disallowed,hostDomain);
            }

            // Check if a specific user-agent is allowed to access a URL
            return robotsTxt.query("*", url);
        } catch (IOException e) {
            System.err.println(STR."Error reading robots.txt: \{e.getMessage()}");
            return true;
        }
    }

    public Document parse(String url,URLManager urlManager,URLFrontier urlFrontier) {
        try {
            Connection connect = Jsoup.connect(url);
            Document doc = connect.get();
            int code = connect.response().statusCode();
            if (code >= 200 && code < 300) {
                System.out.println(doc.title());
                System.out.println(STR."Link: \{url}");

                String urlHost = urlManager.extractHost(url);
                if (urlFrontier.isHostProcessed(urlHost)) {
                    if (urlFrontier.isAllowedUrl(url))
                        return doc;
                } else if (isUrlAllowed(url, urlHost, urlFrontier)) {
                    return doc;
                }

                return null;
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

//    public static void main(String[] args) {
//        // Create a URL object for the website's robots.txt file
//        URL robotsTxtUrl;
//        try {
//            robotsTxtUrl = new URL("https://wikipedia.com/robots.txt");
//        } catch (IOException e) {
//            System.err.println("Error creating URL: " + e.getMessage());
//            return;
//        }
//
//        // Parse the robots.txt file
//        try {
//            RobotsTxt robotsTxt = RobotsTxt.read(robotsTxtUrl.openStream());
//            List<String> disallowedList = robotsTxt.getDisallowList("*");
//
//            // Print the list of disallowed URLs
//            System.out.println("Disallowed URLs:");
//            for (String url : disallowedList) {
//                System.out.println(url);
//            }
//            // Check if a specific user-agent is allowed to access a URL
//            boolean isAllowed = robotsTxt.query("*", "https://wikipedia.com/wiki/Wikipedia:Redirects_for_discussion");
//            if (isAllowed) {
//                System.out.println("URL is allowed for crawling");
//            } else {
//                System.out.println("URL is not allowed for crawling");
//            }
//        } catch (IOException e) {
//            System.err.println("Error reading robots.txt: " + e.getMessage());
//        }
//    }
}
