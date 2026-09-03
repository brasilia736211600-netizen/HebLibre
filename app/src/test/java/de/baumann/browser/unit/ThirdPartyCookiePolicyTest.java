package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TDD contract for the optional third-party cookie privacy control.
 */
public class ThirdPartyCookiePolicyTest {

    @Test
    public void enabledBlocksThirdPartyCookies() {
        assertFalse(ThirdPartyCookiePolicy.acceptThirdPartyCookies(true));
    }

    @Test
    public void disabledPreservesDefaultAcceptance() {
        assertTrue(ThirdPartyCookiePolicy.acceptThirdPartyCookies(false));
    }
}
