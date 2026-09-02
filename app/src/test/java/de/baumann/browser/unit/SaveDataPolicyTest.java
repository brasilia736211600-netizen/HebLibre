package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TDD contract for the Save-Data request policy.
 */
public class SaveDataPolicyTest {

    @Test
    public void defaultPreferenceEnablesSaveData() {
        assertTrue(SaveDataPolicy.isEnabled(true));
    }

    @Test
    public void explicitDisabledPreferenceDisablesSaveData() {
        assertFalse(SaveDataPolicy.isEnabled(false));
    }
}
