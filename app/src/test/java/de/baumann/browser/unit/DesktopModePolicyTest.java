package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * TDD contract for desktop user-agent selection.
 */
public class DesktopModePolicyTest {

    @Test
    public void desktopModeUsesStableDesktopUserAgent() {
        assertEquals(
                DesktopModePolicy.DESKTOP_USER_AGENT,
                DesktopModePolicy.resolve(true, "custom-mobile-ua", "default-mobile-ua"));
    }

    @Test
    public void disabledUsesCustomUserAgentWhenProvided() {
        assertEquals(
                "custom-ua",
                DesktopModePolicy.resolve(false, "custom-ua", "default-mobile-ua"));
    }

    @Test
    public void disabledFallsBackToDefaultUserAgent() {
        assertEquals(
                "default-mobile-ua",
                DesktopModePolicy.resolve(false, "", "default-mobile-ua"));
    }

    @Test
    public void disabledFallsBackToDefaultForBlankCustomUserAgent() {
        assertEquals(
                "default-mobile-ua",
                DesktopModePolicy.resolve(false, "   ", "default-mobile-ua"));
    }
}
