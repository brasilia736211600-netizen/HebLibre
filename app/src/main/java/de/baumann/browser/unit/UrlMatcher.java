package de.baumann.browser.unit;

import java.util.List;

/**
 * Pure-Java extraction of the domain-whitelist matching logic that was
 * previously duplicated identically in AdBlock.isWhite, Javascript.isWhite,
 * Cookie.isWhite, and Remote.isWhite. No Android dependency; safe for
 * plain JVM unit testing.
 */
public class UrlMatcher {

    public static boolean containsAnyDomain(List<String> domains, String url) {
        for (String domain : domains) {
            if (url != null && url.contains(domain)) {
                return true;
            }
        }
        return false;
    }
}
