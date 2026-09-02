package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TDD contract for the conservative tracking-parameter cleaner.
 *
 * Only a small, explicit set of well-known analytics/click identifiers is
 * removable. Meaningful parameters, path, and fragment must be preserved.
 */
public class UrlTrackerCleanerTest {

    @Test
    public void removesUtmParameters_butPreservesMeaningfulQuery() {
        assertEquals(
                "https://example.com/article?id=42#comments",
                UrlTrackerCleaner.clean(
                        "https://example.com/article?utm_source=newsletter&id=42&utm_medium=email#comments"));
    }

    @Test
    public void removesKnownClickIdentifiers() {
        assertEquals(
                "https://example.com/?page=2",
                UrlTrackerCleaner.clean(
                        "https://example.com/?gclid=abc123&page=2&fbclid=xyz789"));
    }

    @Test
    public void preservesUrlWithoutTrackingParameters() {
        String url = "https://example.com/search?q=weblibre&lang=en";
        assertEquals(url, UrlTrackerCleaner.clean(url));
    }

    @Test
    public void removesTrackingParametersWithoutDeletingFragment() {
        assertEquals(
                "https://example.com/page#top",
                UrlTrackerCleaner.clean("https://example.com/page?dclid=abc&utm_campaign=test#top"));
    }

    @Test
    public void nullAndBlankInputAreReturnedUnchanged() {
        assertEquals(null, UrlTrackerCleaner.clean(null));
        assertEquals("   ", UrlTrackerCleaner.clean("   "));
    }
}
