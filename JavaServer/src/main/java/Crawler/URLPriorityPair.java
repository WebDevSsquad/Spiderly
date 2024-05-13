package Crawler;

import java.net.URI;

import static java.lang.StringTemplate.STR;

/**
 * Represents a URL with priority and depth for URL frontier.
 *
 * @param url   The url as a URI object
 * @param priority  The url priority
 * @param depth The url depth
 */
public record URLPriorityPair(URI url, int priority, int depth) implements Comparable<URLPriorityPair> {

    /**
     * Compares this URLPriorityPair with another URLPriorityPair for order.
     *
     * @param other the other URLPriorityPair to be compared.
     * @return a negative integer, zero, or a positive integer as this URLPriorityPair
     * is less than, equal to, or greater than the specified URLPriorityPair.
     */
    @Override
    public int compareTo(URLPriorityPair other) {
        // Compare by depth first.
        if (this.depth != other.depth()) {
            return Integer.compare(this.depth, other.depth());
        }
        // Compare by priority second.
        return Integer.compare(this.priority, other.priority());
    }

    /**
     * Returns the url host.
     * @return the url host as a string.
     */
    public String getHost() {
        URI url = this.url;
        return STR."\{url.getScheme()}://\{url.getAuthority()}";
    }
}
