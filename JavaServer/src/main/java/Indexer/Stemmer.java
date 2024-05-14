package Indexer;

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

    /**
     * Stems the given word using the Porter stemming algorithm.
     *
     * @param word the word to be stemmed
     * @return the stemmed word
     */
    public String Stem(String word) {
        stemmer.setCurrent(word.toLowerCase());
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
