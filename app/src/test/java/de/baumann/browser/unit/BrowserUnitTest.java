package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Minimal unit test for {@link BrowserUnit#isURL(String)}.
 *
 * BrowserUnit.isURL is a pure, deterministic static method (string/regex
 * logic only, no Android framework calls), making it directly testable
 * as a local JVM unit test without instrumentation or Robolectric.
 */
public class BrowserUnitTest {

    @Test
    public void isURL_returnsTrue_forStandardHttpUrl() {
        assertTrue(BrowserUnit.isURL("https://example.com"));
    }

    @Test
    public void isURL_returnsTrue_forBareDomain() {
        assertTrue(BrowserUnit.isURL("example.com"));
    }

    @Test
    public void isURL_returnsFalse_forPlainSearchQuery() {
        assertFalse(BrowserUnit.isURL("how to make coffee"));
    }

    @Test
    public void isURL_returnsFalse_forNull() {
        assertFalse(BrowserUnit.isURL(null));
    }
}
