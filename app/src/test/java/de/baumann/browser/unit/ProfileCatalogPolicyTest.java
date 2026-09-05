package de.baumann.browser.unit;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ProfileCatalogPolicyTest {

    @Test
    public void defaultProfileIsAlwaysPresentAndFirst() {
        assertEquals(Arrays.asList("default", "work"),
                ProfileCatalogPolicy.normalizeIds(Arrays.asList("work", "default", "work")));
    }

    @Test
    public void invalidIdsAreRejectedWithoutCorruptingCatalog() {
        assertFalse(ProfileCatalogPolicy.isValidUserProfileId("default"));
        assertFalse(ProfileCatalogPolicy.isValidUserProfileId("work profile"));
        assertFalse(ProfileCatalogPolicy.isValidUserProfileId("work,private"));
        assertFalse(ProfileCatalogPolicy.isValidUserProfileId(""));
        assertTrue(ProfileCatalogPolicy.isValidUserProfileId("work-01"));
    }

    @Test
    public void serializationRoundTripsDeterministically() {
        String encoded = ProfileCatalogPolicy.serialize(
                Arrays.asList("private", "work", "private", "default"));
        assertEquals("default,private,work", encoded);
        assertEquals(Arrays.asList("default", "private", "work"),
                ProfileCatalogPolicy.deserialize(encoded));
    }

    @Test
    public void blankOrMissingPersistenceFallsBackToDefault() {
        assertEquals(Collections.singletonList("default"),
                ProfileCatalogPolicy.deserialize(null));
        assertEquals(Collections.singletonList("default"),
                ProfileCatalogPolicy.deserialize("   "));
    }

    @Test
    public void activeProfileMustBelongToCatalog() {
        List<String> ids = Arrays.asList("default", "work");
        assertEquals("work", ProfileCatalogPolicy.selectActiveId(ids, " work "));
        assertEquals("default", ProfileCatalogPolicy.selectActiveId(ids, "private"));
    }

    @Test
    public void addAndRemoveKeepDefaultAndOrder() {
        List<String> initial = Arrays.asList("default", "work");
        assertEquals(Arrays.asList("default", "work", "private"),
                ProfileCatalogPolicy.add(initial, " private "));
        assertEquals(Collections.singletonList("default"),
                ProfileCatalogPolicy.remove(initial, "work"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void defaultProfileCannotBeRemoved() {
        ProfileCatalogPolicy.remove(Collections.singletonList("default"), "default");
    }
}
