/*import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.codec.digest.DigestUtils;
public class VisitedPageHandler {
    private static final String HASHED_PAGE_FILE = "src/main/resources/visited_pages.txt";
    private final ConcurrentHashMap<String, Boolean> hashedPage;

    public VisitedPageHandler(ConcurrentHashMap<String, Boolean> hashedPage) {
        this.hashedPage = hashedPage;
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

    public void markPage(String url) {
        String md5Hex = DigestUtils
                .md5Hex(url).toUpperCase();
        hashedPage.put(md5Hex, true);
    }

    public boolean isVisitedPage(String url) {
        String md5Hex = DigestUtils
                .md5Hex(url).toUpperCase();
        return hashedPage.containsKey(md5Hex);
    }

    public ConcurrentHashMap<String, Boolean> getHashedPage(){
        return hashedPage;
    }


}*/