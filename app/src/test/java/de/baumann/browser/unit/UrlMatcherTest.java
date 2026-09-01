package de.baumann.browser.unit;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Characterization test for UrlMatcher.containsAnyDomain, extracted from
 * the identical isWhite(String) logic duplicated in AdBlock, Javascript,
 * Cookie, and Remote. Locks in the exact current matching behavior
 * (substring containment, not exact/host match) before any further
 * per-profile whitelist refactor.
 */
public class UrlMatcherTest {

    @Test
    public void containsAnyDomain_returnsTrue_whenUrlContainsListedDomain() {
        List<String> domains = Arrays.asList("example.com", "foo.org");
        assertTrue(UrlMatcher.containsAnyDomain(domains, "https://example.com/page"));
    }

    @Test
    public void containsAnyDomain_returnsFalse_whenNoDomainMatches() {
        List<String> domains = Arrays.asList("example.com", "foo.org");
        assertFalse(UrlMatcher.containsAnyDomain(domains, "https://other.net/page"));
    }

    @Test
    public void containsAnyDomain_returnsFalse_forEmptyList() {
        assertFalse(UrlMatcher.containsAnyDomain(Collections.emptyList(), "https://example.com"));
    }

    @Test
    public void containsAnyDomain_returnsFalse_forNullUrl() {
        List<String> domains = Arrays.asList("example.com");
        assertFalse(UrlMatcher.containsAnyDomain(domains, null));
    }

    @Test
    public void containsAnyDomain_matchesSubstringNotOnlyHost() {
        // Documents existing (pre-extraction) behavior: plain substring
        // containment, e.g. a domain fragment appearing anywhere in the
        // URL string matches, not just as the host component.
        List<String> domains = Arrays.asList("ads.example.com");
        assertTrue(UrlMatcher.containsAnyDomain(domains, "https://cdn.ads.example.com.evil.test/x"));
    }
}
