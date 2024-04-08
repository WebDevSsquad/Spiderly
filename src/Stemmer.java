package src;

import org.tartarus.snowball.ext.PorterStemmer;
import java.util.ArrayList;

public class Stemmer {
    public Stemmer() {}
    public String Stem(String word) {
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
