import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class URLFrontier {
    // URL Queue Handler's data members
    private static final String FUTURE_SEED_FILE = "src/main/resources/future_seed.txt";
    private PriorityBlockingQueue<URLPriorityPair> urlQueue;

    // Visited Pages Handler's data members
    private static final String HASHED_PAGE_FILE = "src/main/resources/visited_pages.txt";
    private final ConcurrentHashMap<String, Boolean> hashedPage;
    public URLFrontier(PriorityBlockingQueue<URLPriorityPair> urlQueue, ConcurrentHashMap<String, Boolean> hashedPage) {
        this.urlQueue = urlQueue;
        this.hashedPage = hashedPage;
    }

    public boolean isEmpty() {
        return urlQueue.isEmpty();
    }

    public String getNextURL() {
        if (urlQueue.isEmpty()) return null;
        return urlQueue.poll().getUrl();
    }

    public boolean addURL(String url, int priority) {
        markPage(url);
        URLPriorityPair newURL = new URLPriorityPair(url, priority);
        return true;
    }

    private String getHash(String url) {
        // 1. Compress URL
        // 2.Implement Hash Function
        return DigestUtils.md5Hex(url).toUpperCase();
    }

    public PriorityBlockingQueue<URLPriorityPair> getQueue() {
        return urlQueue;
    }

    public static void saveQueueToFile(PriorityBlockingQueue<URLPriorityPair> originalQueue) {
        PriorityBlockingQueue<URLPriorityPair> queue = new PriorityBlockingQueue<>(originalQueue);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FUTURE_SEED_FILE))) {
            // Iterate over the entries of the map and write each entry to the file
            while (!queue.isEmpty()) {
                String element = queue.poll();
                writer.write(element);
                writer.newLine(); // Add a newline character to separate lines
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PriorityBlockingQueue<URLPriorityPair> loadQueueFromFile() {
        PriorityBlockingQueue<URLPriorityPair> queue = new PriorityBlockingQueue<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FUTURE_SEED_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                queue.offer(line);
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queue;
    }

    public void markPage(String url) {
        String md5Hex = getHash(url);
        hashedPage.put(md5Hex, true);
    }

    public boolean isVisitedPage(String url) {
        String md5Hex = getHash(url);
        return hashedPage.containsKey(md5Hex);
    }

    public static void saveVisitedPages(ConcurrentHashMap<String, Boolean> hashedPage) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HASHED_PAGE_FILE))) {
            // Iterate over the entries of the map and write each entry to the file
            for (Map.Entry<String, Boolean> entry : hashedPage.entrySet()) {
                writer.write(entry.getKey());
                writer.newLine(); // Add a newline character to separate lines
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConcurrentHashMap<String, Boolean> loadVisitedPages() {
        ConcurrentHashMap<String, Boolean> hashedPage = new ConcurrentHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HASHED_PAGE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                hashedPage.put(line, true);
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashedPage;
    }

    public ConcurrentHashMap<String, Boolean> getHashedPage(){
        return hashedPage;
    }

    private class URLPriorityPair implements Comparable {
        private String url;
        private int priority;
        private int depth;

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
