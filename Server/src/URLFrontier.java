import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import org.apache.commons.validator.routines.UrlValidator;
public class URLFrontier {
    private PriorityQueue<URLPriorityPair> urlQueue;
    private HashSet<String> visitedUrls;
    public URLFrontier() {
        urlQueue = new PriorityQueue<>();
        visitedUrls = new HashSet<>();
    }

    public boolean isEmpty() {
        return urlQueue.isEmpty();
    }

    public String getNextURL() {
        if (urlQueue.isEmpty()) return null;
        return urlQueue.poll().getUrl();
    }

    public boolean addURL(String url, int priority) {
        String urlHash = getHash(url);
        if (visitedUrls.contains(urlHash))  return false;
        URLPriorityPair newURL = new URLPriorityPair(url, priority);
        return true;
    }

    private String getHash(String url) {
        // 1. Compress URL

        // 2.Implement Hash Function
        // For now returning the same url
        return url;
    }

    private class URLPriorityPair implements Comparable {
        private String url;
        private int priority;

        public String getUrl() {
            return url;
        }

        public int getPriority() {
            return priority;
        }

        public URLPriorityPair(String url, int priority) {
            this.url = url;
            this.priority = priority;
        }

        @Override
        public int compareTo(Object other) {
            return Integer.compare(this.priority, ((URLPriorityPair)other).priority);
        }
    }
}
