package src;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class Parser {
    public Document parse(String url) throws IOException {
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
}