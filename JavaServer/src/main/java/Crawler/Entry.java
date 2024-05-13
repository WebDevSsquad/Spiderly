package Crawler;

import java.util.ArrayList;

/**
 * Represents an entry containing title, content, and link.
 *
 * @param title The title of the entry.
 * @param headerArray The content of the entry.
 * @param link The link of the entry.
 */
public record Entry(String title, ArrayList<String> headerArray, ArrayList<String> titleArray, ArrayList<String> textArray, String link) { }
