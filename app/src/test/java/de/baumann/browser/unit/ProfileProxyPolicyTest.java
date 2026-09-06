package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProfileProxyPolicyTest {
    @Test public void acceptsSupportedProxyUrls() {
        assertTrue(ProfileProxyPolicy.isValid("http://proxy.example.com:8080"));
        assertTrue(ProfileProxyPolicy.isValid("https://proxy.example.com:8443"));
        assertTrue(ProfileProxyPolicy.isValid("socks5://127.0.0.1:1080"));
    }

    @Test public void rejectsAmbiguousOrCredentialBearingProxyUrls() {
        assertFalse(ProfileProxyPolicy.isValid("proxy.example.com:8080"));
        assertFalse(ProfileProxyPolicy.isValid("http://user:pass@proxy.example.com:8080"));
        assertFalse(ProfileProxyPolicy.isValid("http://proxy.example.com"));
        assertFalse(ProfileProxyPolicy.isValid("ftp://proxy.example.com:21"));
    }

    @Test public void normalizesBypassRules() {
        assertEquals("localhost,example.com", ProfileProxyPolicy.normalizeBypassRules(" localhost, , example.com "));
    }
}
