package de.baumann.browser.unit;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class ProfileMetadataTest {

    @Test
    public void storesAndNormalizesProfileMetadata() {
        ProfileMetadata profile = new ProfileMetadata(
                "  work  ",
                "  Work profile  ",
                " blue ",
                " briefcase ",
                "  office browsing  ",
                Arrays.asList(" office ", "work", "office", " ", null),
                "  personal  ");

        assertEquals("work", profile.getId());
        assertEquals("Work profile", profile.getName());
        assertEquals("blue", profile.getColor());
        assertEquals("briefcase", profile.getIcon());
        assertEquals("office browsing", profile.getNotes());
        assertEquals(Arrays.asList("office", "work"), profile.getTags());
        assertEquals("personal", profile.getGroup());
    }

    @Test
    public void optionalMetadataDefaultsToEmptyValues() {
        ProfileMetadata profile = new ProfileMetadata(
                "default", "Default", null, "", "   ", null, null);

        assertEquals("", profile.getColor());
        assertEquals("", profile.getIcon());
        assertEquals("", profile.getNotes());
        assertEquals(Collections.emptyList(), profile.getTags());
        assertEquals("", profile.getGroup());
    }

    @Test(expected = IllegalArgumentException.class)
    public void blankIdIsRejected() {
        new ProfileMetadata("   ", "Work", null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void blankNameIsRejected() {
        new ProfileMetadata("work", "   ", null, null, null, null, null);
    }

    @Test
    public void tagsAreImmutable() {
        ProfileMetadata profile = new ProfileMetadata(
                "work", "Work", null, null, null, Arrays.asList("one"), null);

        assertNotSame(Collections.singletonList("one"), profile.getTags());
        try {
            profile.getTags().add("two");
        } catch (UnsupportedOperationException expected) {
            assertTrue(true);
            return;
        }
        throw new AssertionError("Profile tags must be immutable");
    }
}
