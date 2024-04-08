package src;
import org.tartarus.snowball.ext.PorterStemmer;

public class Stemmer {
    public Stemmer() {}
    public String Stem(String word) {
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
