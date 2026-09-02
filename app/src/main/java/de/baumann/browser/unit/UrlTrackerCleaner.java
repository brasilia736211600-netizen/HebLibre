package de.baumann.browser.unit;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Conservative URL tracking-parameter cleaner.
 *
 * Removes only an explicit allowlist of common analytics/click identifiers
 * and parameters beginning with "utm_". Meaningful query parameters,
 * path, fragment, and parameter order are otherwise preserved.
 */
public final class UrlTrackerCleaner {

    private static final String[] TRACKING_PARAMETERS = {
            "gclid",
            "dclid",
            "fbclid",
            "msclkid",
            "yclid"
    };

    private UrlTrackerCleaner() {
        // Utility class.
    }

    public static String clean(String url) {
        if (url == null || url.trim().isEmpty()) {
            return url;
        }

        try {
            URI uri = new URI(url);
            String query = uri.getRawQuery();
            if (query == null || query.isEmpty()) {
                return url;
            }

            StringBuilder kept = new StringBuilder();
            String[] parameters = query.split("&", -1);
            for (String parameter : parameters) {
                int equals = parameter.indexOf('=');
                String name = equals >= 0 ? parameter.substring(0, equals) : parameter;
                if (isTrackingParameter(name)) {
                    continue;
                }

                if (kept.length() > 0) {
                    kept.append('&');
                }
                kept.append(parameter);
            }

            String newQuery = kept.length() == 0 ? null : kept.toString();
            return new URI(
                    uri.getScheme(),
                    uri.getRawAuthority(),
                    uri.getRawPath(),
                    newQuery,
                    uri.getRawFragment()).toString();
        } catch (URISyntaxException ignored) {
            return url;
        }
    }

    private static boolean isTrackingParameter(String name) {
        String normalized = name.toLowerCase(java.util.Locale.ROOT);
        if (normalized.startsWith("utm_")) {
            return true;
        }
        for (String parameter : TRACKING_PARAMETERS) {
            if (parameter.equals(normalized)) {
                return true;
            }
        }
        return false;
    }
}
