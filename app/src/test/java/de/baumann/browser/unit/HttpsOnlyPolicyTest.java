package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TDD contract for the local HTTPS-only navigation policy.
 */
public class HttpsOnlyPolicyTest {

    @Test
    public void upgradesHttpUrlToHttps() {
        assertEquals(
                "https://example.com/path?q=1#top",
                HttpsOnlyPolicy.enforce("http://example.com/path?q=1#top"));
    }

    @Test
    public void preservesHttpsUrl() {
        String url = "https://example.com/path?q=1#top";
        assertEquals(url, HttpsOnlyPolicy.enforce(url));
    }

    @Test
    public void preservesNonHttpSchemes() {
        assertEquals("file:///tmp/test.html", HttpsOnlyPolicy.enforce("file:///tmp/test.html"));
        assertEquals("mailto:test@example.com", HttpsOnlyPolicy.enforce("mailto:test@example.com"));
    }

    @Test
    public void preservesNullAndBlankInput() {
        assertEquals(null, HttpsOnlyPolicy.enforce(null));
        assertEquals("   ", HttpsOnlyPolicy.enforce("   "));
    }
}
