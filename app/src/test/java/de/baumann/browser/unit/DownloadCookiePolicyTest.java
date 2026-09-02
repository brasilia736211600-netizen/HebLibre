package de.baumann.browser.unit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DownloadCookiePolicyTest {
    @Test
    public void defaultPreferencePreservesAuthenticatedDownloads() {
        assertTrue(DownloadCookiePolicy.shouldSendCookies(DownloadCookiePolicy.DEFAULT_ENABLED));
    }

    @Test
    public void disabledPreferenceStopsCookieForwarding() {
        assertFalse(DownloadCookiePolicy.shouldSendCookies(false));
    }
}
