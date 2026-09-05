package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProfileSessionPolicyTest {

    @Test
    public void acceptsOnlyHttpAndHttpsUrls() {
        assertTrue(ProfileSessionPolicy.isRestorableUrl("https://example.com"));
        assertTrue(ProfileSessionPolicy.isRestorableUrl(" HTTP://example.com/path "));
        assertFalse(ProfileSessionPolicy.isRestorableUrl(null));
        assertFalse(ProfileSessionPolicy.isRestorableUrl("about:blank"));
        assertFalse(ProfileSessionPolicy.isRestorableUrl("intent://example.com"));
        assertFalse(ProfileSessionPolicy.isRestorableUrl("mailto:test@example.com"));
    }

    @Test
    public void normalizesTitleWithUrlFallback() {
        assertEquals("Example", ProfileSessionPolicy.normalizeTitle("  Example  ", "https://example.com"));
        assertEquals("https://example.com", ProfileSessionPolicy.normalizeTitle(" ", "https://example.com"));
        assertEquals("", ProfileSessionPolicy.normalizeTitle(null, null));
    }

    @Test
    public void keepsSessionBoundToOriginalProfile() {
        assertEquals("work", ProfileSessionPolicy.persistenceProfileId("work", "personal"));
        assertEquals("default", ProfileSessionPolicy.persistenceProfileId("default", "personal"));
        assertEquals("personal", ProfileSessionPolicy.persistenceProfileId("", "personal"));
        assertEquals("default", ProfileSessionPolicy.persistenceProfileId(null, null));
    }
}
