/**
 * The Logger class provides functionality for logging messages,
 * errors, warnings, and other events during the crawling process.
 * It facilitates monitoring, debugging, and error tracking.
 */
// use java.util.logging.Logger; later
public class Logger {
    public static void log(String message) {
        System.out.println(message);
    }

    public static void logError(String errorMessage, Exception e) {
        System.out.println(STR."Error: \{e.toString()}, \{errorMessage}");
    }
}
