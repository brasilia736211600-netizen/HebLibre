package de.baumann.browser.unit;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ProfileCatalogViewPolicyTest {

    private ProfileMetadata profile(String id, String name, String group, String notes, String... tags) {
        return new ProfileMetadata(
                id, name, "", "", notes,
                Arrays.asList(tags), group);
    }

    @Test
    public void searchMatchesNameGroupNotesAndTags() {
        List<ProfileMetadata> profiles = Arrays.asList(
                profile("work", "Work", "business", "office", "team"),
                profile("personal", "Personal", "home", "", "family"),
                profile("research", "Research", "lab", "browser notes"));

        assertEquals(Collections.singletonList("work"),
                ids(ProfileCatalogViewPolicy.filterAndSort(profiles, "team", "", null)));
        assertEquals(Collections.singletonList("research"),
                ids(ProfileCatalogViewPolicy.filterAndSort(profiles, "browser", "", null)));
        assertEquals(Collections.singletonList("work"),
                ids(ProfileCatalogViewPolicy.filterAndSort(profiles, "business", "", null)));
    }

    @Test
    public void activeProfileAlwaysAppearsFirst() {
        List<ProfileMetadata> profiles = Arrays.asList(
                profile("zeta", "Zeta", "", ""),
                profile("alpha", "Alpha", "", ""),
                profile("work", "Work", "", ""));

        assertEquals(Arrays.asList("work", "alpha", "zeta"),
                ids(ProfileCatalogViewPolicy.filterAndSort(
                        profiles, "", "work", ProfileCatalogViewPolicy.SortMode.NAME)));
    }

    @Test
    public void groupAndIdSortsAreDeterministic() {
        List<ProfileMetadata> profiles = Arrays.asList(
                profile("z", "Zulu", "b", ""),
                profile("a", "Alpha", "c", ""),
                profile("m", "Mike", "a", ""));

        assertEquals(Arrays.asList("m", "z", "a"),
                ids(ProfileCatalogViewPolicy.filterAndSort(
                        profiles, "", "", ProfileCatalogViewPolicy.SortMode.GROUP)));
        assertEquals(Arrays.asList("a", "m", "z"),
                ids(ProfileCatalogViewPolicy.filterAndSort(
                        profiles, "", "", ProfileCatalogViewPolicy.SortMode.ID));
    }

    private List<String> ids(List<ProfileMetadata> profiles) {
        java.util.ArrayList<String> result = new java.util.ArrayList<>();
        for (ProfileMetadata profile : profiles) {
            result.add(profile.getId());
        }
        return result;
    }
}
