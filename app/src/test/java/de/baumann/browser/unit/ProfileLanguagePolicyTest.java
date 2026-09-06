package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProfileLanguagePolicyTest {
    @Test
    public void validTagsNormalizeConservatively() {
        assertTrue(ProfileLanguagePolicy.isValid("ar-YE"));
        assertTrue(ProfileLanguagePolicy.isValid("en-US"));
        assertEquals("ar-ye", ProfileLanguagePolicy.normalize("  ar-YE "));
    }

    @Test
    public void malformedTagsBecomeEmpty() {
        assertFalse(ProfileLanguagePolicy.isValid(""));
        assertFalse(ProfileLanguagePolicy.isValid("ar_YE"));
        assertFalse(ProfileLanguagePolicy.isValid("123"));
        assertEquals("", ProfileLanguagePolicy.normalize("not a language"));
    }
}
