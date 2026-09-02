package de.baumann.browser.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DownloadCookiePolicyTest {
    @Test
    public void defaultKeepsAuthenticatedDownloadCompatibility() {
        assertTrue(DownloadCookiePolicy.isEnabled(DownloadCookiePolicy.DEFAULT_ENABLED));
    }

    @Test
    public void disabledPreferenceBlocksCookieForwarding() {
        assertFalse(DownloadCookiePolicy.isEnabled(false));
    }
}
