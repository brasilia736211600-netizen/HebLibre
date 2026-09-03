package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TDD contract for the third-party cookie privacy control.
 */
public class ThirdPartyCookiePolicyTest {

    @Test
    public void defaultEnablesBlocking() {
        assertTrue(ThirdPartyCookiePolicy.DEFAULT_ENABLED);
    }

    @Test
    public void enabledBlocksThirdPartyCookies() {
        assertFalse(ThirdPartyCookiePolicy.acceptThirdPartyCookies(true));
    }

    @Test
    public void disabledPreservesCookieAcceptance() {
        assertTrue(ThirdPartyCookiePolicy.acceptThirdPartyCookies(false));
    }
}
