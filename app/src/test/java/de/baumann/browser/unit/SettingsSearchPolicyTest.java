package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SettingsSearchPolicyTest {
    @Test
    public void blankQueryMatchesEverything() {
        assertTrue(SettingsSearchPolicy.matches("", "HTTPS-only mode", "Upgrade plain HTTP navigation"));
    }

    @Test
    public void queryMatchesTitleCaseInsensitively() {
        assertTrue(SettingsSearchPolicy.matches("desktop", "Desktop Mode", "Request desktop sites"));
    }

    @Test
    public void queryMatchesSummary() {
        assertTrue(SettingsSearchPolicy.matches("cookies", "Privacy", "Block third party cookies"));
    }

    @Test
    public void unrelatedQueryDoesNotMatch() {
        assertFalse(SettingsSearchPolicy.matches("geolocation", "Desktop Mode", "Request desktop sites"));
    }
}
