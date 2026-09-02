package de.baumann.browser.unit;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

/**
 * Minimal built-in bang routing for the existing HebLibre search engines.
 * Returns null when the input is not a recognized bang query.
 */
public final class BangQueryPolicy {

    private BangQueryPolicy() {
        // Utility class.
    }

    public static String resolve(String query) {
        if (query == null) {
            return null;
        }

        String trimmed = query.trim();
        if (!trimmed.startsWith("!")) {
            return null;
        }

        int separator = trimmed.indexOf(' ');
        if (separator <= 1 || separator == trimmed.length() - 1) {
            return null;
        }

        String bang = trimmed.substring(1, separator).toLowerCase(Locale.ROOT);
        String search = trimmed.substring(separator + 1).trim();
        if (search.isEmpty()) {
            return null;
        }

        String prefix;
        switch (bang) {
            case "g":
            case "google":
                prefix = "https://www.google.com/search?q=";
                break;
            case "ddg":
            case "duck":
            case "duckduckgo":
                prefix = "https://duckduckgo.com/?q=";
                break;
            case "sp":
            case "startpage":
                prefix = "https://startpage.com/do/search?query=";
                break;
            case "b":
            case "bing":
                prefix = "http://www.bing.com/search?q=";
                break;
            case "bd":
            case "baidu":
                prefix = "https://www.baidu.com/s?wd=";
                break;
            case "q":
            case "qwant":
                prefix = "https://www.qwant.com/?q=";
                break;
            case "e":
            case "ecosia":
                prefix = "https://www.ecosia.org/search?q=";
                break;
            case "sx":
            case "searx":
                prefix = "https://searx.me/?q=";
                break;
            default:
                return null;
        }

        try {
            return prefix + URLEncoder.encode(search, BrowserUnit.URL_ENCODING);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
