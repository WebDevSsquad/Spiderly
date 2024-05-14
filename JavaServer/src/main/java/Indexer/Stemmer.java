package Indexer;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import org.tartarus.snowball.ext.PorterStemmer;

/**
 * The Stemmer class provides methods for stemming words using the Porter stemming algorithm.
 */
public class Stemmer {
    PorterStemmer stemmer;
    /**
     * Constructs a Stemmer object.
     */
    public Stemmer() {
        stemmer = new PorterStemmer();
    }

    private static String removePunctuation(String str) {
        Pattern pattern = Pattern.compile("[.?:,'!#%^&*()_+-@]+$");
        Matcher matcher = pattern.matcher(str);

        if (matcher.find()) {
            str = matcher.replaceAll("");
        }

        return str;
    }
    public String Stem(String word) {
        stemmer.setCurrent(word.toLowerCase());
        stemmer.stem();
        String stemmedWord = stemmer.getCurrent();

        return removePunctuation(stemmedWord);
    }
}
