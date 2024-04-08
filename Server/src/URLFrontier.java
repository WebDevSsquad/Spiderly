import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

public class URLFrontier {
    // URL Queue Handler's data members
    private static final String SEED_FILE = "src/main/resources/seed.txt";
    private final PriorityBlockingQueue<URLPriorityPair> urlQueue;

    private static final String HASHED_URL_FILE = "src/main/resources/hashed_urls.txt";
    private final ConcurrentHashMap<String, Boolean> hashedURLs;

    // Visited Pages Handler's data members
    private static final String HASHED_PAGE_FILE = "src/main/resources/visited_pages.txt";
    private final ConcurrentHashMap<String, Boolean> hashedPage;
    public URLFrontier(PriorityBlockingQueue<URLPriorityPair> urlQueue,
                       ConcurrentHashMap<String, Boolean> hashedPage,
                       ConcurrentHashMap<String, Boolean> hashedURLs) {
        this.urlQueue = urlQueue;
        this.hashedPage = hashedPage;
        this.hashedURLs = hashedURLs;
    }

    public  boolean isEmpty() {
        return urlQueue.isEmpty();
    }

    public  URLPriorityPair getNextURL() {
        return urlQueue.poll();
    }

    public  boolean addURL(String url, int priority, int depth) {
        URLPriorityPair newURL = new URLPriorityPair(url, priority, depth);
        urlQueue.offer(newURL);
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
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SEED_FILE))) {
            // Iterate over the entries of the queue and write each entry to the file
            for (URLPriorityPair pair : originalQueue) {
                writer.write(STR."\{pair.getUrl()} \{pair.getPriority()} \{pair.getDepth()}");
                writer.newLine(); // Add a newline character to separate lines
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PriorityBlockingQueue<URLPriorityPair> loadQueueFromFile() {
        PriorityBlockingQueue<URLPriorityPair> queue =new PriorityBlockingQueue<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(SEED_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 3) {
                    String url = parts[0];
                    int priority = Integer.parseInt(parts[1]);
                    int depth = Integer.parseInt(parts[2]);
                    URLPriorityPair pair = new URLPriorityPair(url, priority, depth);
                    queue.offer(pair);
                } else {
                    // Handle invalid format or empty lines
                    System.err.println(STR."Invalid line: \{line}");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queue;
    }

    public void markURL(String url) {
        String md5Hex = getHash(url);
        hashedURLs.put(md5Hex, true);
    }

    public boolean isVisitedURL(String url) {
        String md5Hex = getHash(url);
        return hashedURLs.containsKey(md5Hex);
    }

    public void markPage(String url) {
        String md5Hex = getHash(url);
        hashedPage.put(md5Hex, true);
    }

    public boolean isVisitedPage(String doc) {
        String md5Hex = getHash(doc);
        return hashedPage.containsKey(md5Hex);
    }

    public static void saveVisitedPages(ConcurrentHashMap<String, Boolean> hashedMap, int fileSelector) {
        String path = (fileSelector == 1 ? HASHED_PAGE_FILE : HASHED_URL_FILE);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            // Iterate over the entries of the map and write each entry to the file
            for (Map.Entry<String, Boolean> entry : hashedMap.entrySet()) {
                writer.write(entry.getKey());
                writer.newLine(); // Add a newline character to separate lines
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ConcurrentHashMap<String, Boolean> loadVisitedPages(int fileSelector) {
        ConcurrentHashMap<String, Boolean> hashMap = new ConcurrentHashMap<>();
        String path = (fileSelector == 1 ? HASHED_PAGE_FILE : HASHED_URL_FILE);
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                hashMap.put(line, true);
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hashMap;
    }

    public ConcurrentHashMap<String, Boolean> getHashedURLs(){
        return hashedURLs;
    }

    public ConcurrentHashMap<String, Boolean> getHashedPage(){
        return hashedPage;
    }

    public int getHashedPageSize(){
        return hashedPage.size();
    }


    public  int getUrlQueueSize(){return urlQueue.size();}
}

class URLPriorityPair implements Comparable {
    private final String url;
    private final int priority;
    private final int depth;

    public String getUrl() {
        return url;
    }

    public int getPriority() {
        return priority;
    }

    public int getDepth() {
        return depth;
    }

    public URLPriorityPair(String url, int priority, int depth) {
        this.url = url;
        this.priority = priority;
        this.depth = depth;
    }

    @Override
    public int compareTo(Object other) {
        // Compare by depth first
        if (this.depth != ((URLPriorityPair)other).depth) {
            return Integer.compare(this.depth, ((URLPriorityPair)other).depth);
        }
        return Integer.compare(this.priority, ((URLPriorityPair)other).priority);
    }
}