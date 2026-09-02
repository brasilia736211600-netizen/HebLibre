package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * TDD contract for a small built-in bang router over existing search engines.
 */
public class BangQueryPolicyTest {

    @Test
    public void googleBangReturnsGoogleSearchUrl() {
        assertEquals(
                "https://www.google.com/search?q=hello+world",
                BangQueryPolicy.resolve("!g hello world"));
    }

    @Test
    public void duckDuckGoBangReturnsDuckDuckGoSearchUrl() {
        assertEquals(
                "https://duckduckgo.com/?q=privacy+browser",
                BangQueryPolicy.resolve("!ddg privacy browser"));
    }

    @Test
    public void shortBingBangReturnsBingSearchUrl() {
        assertEquals(
                "http://www.bing.com/search?q=android+webview",
                BangQueryPolicy.resolve("!b android webview"));
    }

    @Test
    public void unknownBangFallsThrough() {
        assertNull(BangQueryPolicy.resolve("!unknown example"));
    }

    @Test
    public void ordinaryQueryFallsThrough() {
        assertNull(BangQueryPolicy.resolve("ordinary search"));
    }

    @Test
    public void blankBangFallsThrough() {
        assertNull(BangQueryPolicy.resolve("!g"));
    }
}
