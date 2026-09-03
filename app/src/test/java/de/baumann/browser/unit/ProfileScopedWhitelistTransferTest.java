package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProfileScopedWhitelistTransferTest {

    @Test
    public void nullProfileIdFallsBackToDefault() {
        assertEquals(ProfileIdentity.DEFAULT_PROFILE_ID,
                ProfileScopedWhitelistTransfer.normalizeProfileId(null));
    }

    @Test
    public void blankProfileIdFallsBackToDefault() {
        assertEquals(ProfileIdentity.DEFAULT_PROFILE_ID,
                ProfileScopedWhitelistTransfer.normalizeProfileId("   "));
    }

    @Test
    public void activeProfileIdIsTrimmed() {
        assertEquals("work", ProfileScopedWhitelistTransfer.normalizeProfileId("  work  "));
    }

    @Test
    public void activeProfileIdIsPreserved() {
        assertEquals("private", ProfileScopedWhitelistTransfer.normalizeProfileId("private"));
    }
}
