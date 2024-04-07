/*import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UrlDiscovery {
    private static final String FUTURE_SEED_FILE = "src/main/resources/hashed_urls.txt";
    private ConcurrentLinkedQueue<String> queue;

    public UrlDiscovery(ConcurrentLinkedQueue<String> queue) {
        setQueue(queue);
    }

    public static void saveQueueToFile(ConcurrentLinkedQueue<String> originalQueue) {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>(originalQueue);
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

    public static ConcurrentLinkedQueue<String> loadQueueFromFile() {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
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

    public boolean addUrl(String Url) {
        return queue.offer(Url);
    }

    public String getUrl() {
        if(queue.isEmpty())return null;
        return queue.poll();
    }

    public ConcurrentLinkedQueue<String> getQueue() {
        return queue;
    }

    public void setQueue(ConcurrentLinkedQueue<String> queue) {
        this.queue = queue;
    }

    public int getSize(){return queue.size();}
}*/
