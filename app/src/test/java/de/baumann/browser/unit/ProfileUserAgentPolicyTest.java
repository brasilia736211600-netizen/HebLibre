package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ProfileUserAgentPolicyTest {
    @Test public void knownPresetsResolve() {
        assertTrue(ProfileUserAgentPolicy.isValidPreset("android_chrome_131"));
        assertNotNull(ProfileUserAgentPolicy.resolvePreset("windows_edge_131"));
    }

    @Test public void unknownPresetIsRejected() {
        assertFalse(ProfileUserAgentPolicy.isValidPreset("unknown-browser-profile"));
    }

    @Test public void customUaIsTrimmedAndBounded() {
        assertTrue(ProfileUserAgentPolicy.normalizeCustom("  Mozilla/test  ").equals("Mozilla/test"));
        StringBuilder oversized = new StringBuilder();
        for (int i = 0; i < 1025; i++) oversized.append('x');
        assertTrue(ProfileUserAgentPolicy.normalizeCustom(oversized.toString()).isEmpty());
    }
}
