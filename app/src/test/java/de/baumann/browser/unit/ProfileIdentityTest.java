package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProfileIdentityTest {

    @Test
    public void nullProfileIdFallsBackToDefault() {
        assertEquals(ProfileIdentity.DEFAULT_PROFILE_ID, ProfileIdentity.normalize(null));
    }

    @Test
    public void blankProfileIdFallsBackToDefault() {
        assertEquals(ProfileIdentity.DEFAULT_PROFILE_ID, ProfileIdentity.normalize("   "));
    }

    @Test
    public void surroundingWhitespaceIsRemoved() {
        assertEquals("work", ProfileIdentity.normalize("  work  "));
    }

    @Test
    public void nonBlankProfileIdIsPreserved() {
        assertEquals("private", ProfileIdentity.normalize("private"));
    }
}
