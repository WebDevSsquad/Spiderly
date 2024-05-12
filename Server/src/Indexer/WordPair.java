package Indexer;

import java.util.Objects;

/**
 * Constructs a new WordPair with the specified original word and stemmed word.
 *
 * @param originalWord the original word
 * @param stemmedWord the stemmed version of the word
 */
public record WordPair(String originalWord, String stemmedWord) {

    /**
     * Compares this WordPair to the specified object for equality.
     *
     * @param obj the object to compare
     * @return true if the given object is also a WordPair and the two WordPairs represent the same pair of words
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        WordPair wordPair = (WordPair) obj;
        return Objects.equals(originalWord, wordPair.originalWord) &&
                Objects.equals(stemmedWord, wordPair.stemmedWord);
    }

    /**
     * Returns a hash code value for this WordPair.
     *
     * @return a hash code value for this WordPair
     */
    @Override
    public int hashCode() {
        return Objects.hash(originalWord, stemmedWord);
    }
}
