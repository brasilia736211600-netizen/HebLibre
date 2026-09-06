package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProfileSitePermissionPolicyTest {
    @Test
    public void normalizeOriginCanonicalizesSchemeHostAndPort() {
        assertEquals("https://example.com", ProfileSitePermissionPolicy.normalizeOrigin(" HTTPS://Example.COM/path?q=1 "));
        assertEquals("http://example.com:8080", ProfileSitePermissionPolicy.normalizeOrigin("http://EXAMPLE.com:8080/a"));
    }

    @Test
    public void normalizeOriginRejectsUnsupportedOrMalformedOrigins() {
        assertEquals("", ProfileSitePermissionPolicy.normalizeOrigin("file:///tmp/a"));
        assertEquals("", ProfileSitePermissionPolicy.normalizeOrigin("example.com"));
        assertEquals("", ProfileSitePermissionPolicy.normalizeOrigin("not a uri"));
    }

    @Test
    public void decisionsAndKeysAreBounded() {
        assertTrue(ProfileSitePermissionPolicy.isDecision(ProfileSitePermissionPolicy.DECISION_ALLOW));
        assertTrue(ProfileSitePermissionPolicy.isDecision(ProfileSitePermissionPolicy.DECISION_DENY));
        assertFalse(ProfileSitePermissionPolicy.isDecision("grant-all"));
        assertEquals("media|https://example.com", ProfileSitePermissionPolicy.key(
                "https://Example.com/path", ProfileSitePermissionPolicy.PERMISSION_MEDIA));
        assertEquals("", ProfileSitePermissionPolicy.key("https://example.com", "camera"));
    }
}
