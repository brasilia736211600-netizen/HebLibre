package de.baumann.browser.unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Pure view policy for searching and ordering the profile catalog. */
public final class ProfileCatalogViewPolicy {
    public enum SortMode { NAME, GROUP, ID }

    private ProfileCatalogViewPolicy() {
    }

    public static List<ProfileMetadata> filterAndSort(
            List<ProfileMetadata> profiles,
            String query,
            final String activeProfileId,
            SortMode mode) {
        if (profiles == null || profiles.isEmpty()) {
            return Collections.emptyList();
        }
        final String normalizedQuery = query == null
                ? ""
                : query.trim().toLowerCase(Locale.ROOT);
        final String normalizedActive = ProfileIdentity.normalize(activeProfileId);
        List<ProfileMetadata> result = new ArrayList<>();
        for (ProfileMetadata profile : profiles) {
            if (profile == null || !matches(profile, normalizedQuery)) {
                continue;
            }
            result.add(profile);
        }

        final SortMode effectiveMode = mode == null ? SortMode.NAME : mode;
        Collections.sort(result, new Comparator<ProfileMetadata>() {
            @Override
            public int compare(ProfileMetadata left, ProfileMetadata right) {
                boolean leftActive = normalizedActive.equals(ProfileIdentity.normalize(left.getId()));
                boolean rightActive = normalizedActive.equals(ProfileIdentity.normalize(right.getId()));
                if (leftActive != rightActive) {
                    return leftActive ? -1 : 1;
                }
                int byPrimary;
                if (effectiveMode == SortMode.GROUP) {
                    byPrimary = compareText(left.getGroup(), right.getGroup());
                    if (byPrimary == 0) {
                        byPrimary = compareText(left.getName(), right.getName());
                    }
                } else if (effectiveMode == SortMode.ID) {
                    byPrimary = compareText(left.getId(), right.getId());
                } else {
                    byPrimary = compareText(left.getName(), right.getName());
                    if (byPrimary == 0) {
                        byPrimary = compareText(left.getGroup(), right.getGroup());
                    }
                }
                if (byPrimary != 0) {
                    return byPrimary;
                }
                return compareText(left.getId(), right.getId());
            }
        });
        return Collections.unmodifiableList(result);
    }

    private static boolean matches(ProfileMetadata profile, String query) {
        if (query.isEmpty()) {
            return true;
        }
        return contains(profile.getId(), query)
                || contains(profile.getName(), query)
                || contains(profile.getGroup(), query)
                || contains(profile.getNotes(), query)
                || containsTags(profile.getTags(), query);
    }

    private static boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(query);
    }

    private static boolean containsTags(List<String> tags, String query) {
        if (tags == null) {
            return false;
        }
        for (String tag : tags) {
            if (contains(tag, query)) {
                return true;
            }
        }
        return false;
    }

    private static int compareText(String left, String right) {
        String l = left == null ? "" : left;
        String r = right == null ? "" : right;
        return l.compareToIgnoreCase(r);
    }
}
