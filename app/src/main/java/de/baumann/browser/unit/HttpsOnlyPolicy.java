package de.baumann.browser.unit;

import java.util.Locale;

/**
 * Local HTTPS-only navigation policy.
 *
 * Only absolute HTTP URLs are upgraded. HTTPS and non-HTTP schemes are
 * returned unchanged; no network access or fallback request is performed.
 */
public final class HttpsOnlyPolicy {

    private static final String HTTP_PREFIX = "http://";
    private static final String HTTPS_PREFIX = "https://";

    private HttpsOnlyPolicy() {
        // Utility class.
    }

    public static String enforce(String url) {
        if (url == null || url.trim().isEmpty()) {
            return url;
        }

        if (url.regionMatches(true, 0, HTTP_PREFIX, 0, HTTP_PREFIX.length())) {
            return HTTPS_PREFIX + url.substring(HTTP_PREFIX.length());
        }

        return url;
    }
}
