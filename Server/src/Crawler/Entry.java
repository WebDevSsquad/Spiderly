package Crawler;

/**
 * Represents an entry containing title, content, and link.
 *
 * @param title The title of the entry.
 * @param content The content of the entry.
 * @param link The link of the entry.
 */
public record Entry(String title, String content, String link) { }
