package Crawler;

import com.panforge.robotstxt.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.IOException;
import java.net.URL;

public class Parser {

    // Logging
    private static final Logger logger = Logger.getLogger(Parser.class.getName());

    /**
     * Checks if a URL is allowed to be crawled based on the website's robots.txt file.
     *
     * @param url          The URL to check.
     * @param hostDomain   The domain of the website.
     * @param urlFrontier  The URLFrontier object to store disallowed URLs.
     * @return True if the URL is allowed, otherwise false.
     */
    public boolean isUrlAllowed(String url, String hostDomain , URLFrontier urlFrontier) {
        // Create a URL object for the website's robots.txt file
        URL robotsTxtUrl;
        String robotsTxtStr = getRobotsTxtURL(hostDomain);
        // Parse the robots.txt file

        try {
            robotsTxtUrl = new URL(robotsTxtStr);
            RobotsTxt robotsTxt = RobotsTxt.read(robotsTxtUrl.openStream());

            List<String> disallowedList = robotsTxt.getDisallowList("*");

            for (String disallowed : disallowedList) {
                urlFrontier.disallowUrl(hostDomain+disallowed,hostDomain);
            }

            // Check if a specific user-agent is allowed to access a URL
            return robotsTxt.query("*", url);
        } catch (IOException e) {
            //logger.log(Level.FINE, STR."Error reading robotsTxt URL: \{robotsTxtStr}", e);
            return false;
        }
    }

    /**
     * Parses a web page and retrieves its content.
     *
     * @param urlPair      The URLPriorityPair object representing the URL to parse.
     * @param urlFrontier  The URLFrontier object for managing URL states.
     * @return The parsed document if successful, otherwise null.
     */
    public Document parse(URLPriorityPair urlPair, URLFrontier urlFrontier) {
        String url = urlPair.url().toString();
        try {
            Connection connect = Jsoup.connect(urlPair.url().toString());
            Document doc = connect.get();
            int code = connect.response().statusCode();
            if (code >= 200 && code < 300) {
                System.out.println(doc.title());
                System.out.println(STR."Link: \{url}");

                String urlHost = urlPair.getHost();

                if (urlFrontier.isHostProcessed(urlHost)) {
                    if (urlFrontier.isAllowedUrl(url)) {
                        return doc;
                    }
                } else if (isUrlAllowed(url, urlHost, urlFrontier)) {
                    return doc;
                }
                return null;
            }
        } catch (IOException e) {
            //logger.log(Level.WARNING, STR."Error parsing url URL: \{url}", e);
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
        return STR."\{hostDomain}/robots.txt";
    }



}
