package de.baumann.browser.unit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Dependency-free policy for the persisted set of user-created profile ids.
 */
public final class ProfileCatalogPolicy {

    public static final int MAX_PROFILE_ID_LENGTH = 64;
    private static final String PROFILE_ID_PATTERN = "[A-Za-z0-9][A-Za-z0-9._-]{0,63}";

    private ProfileCatalogPolicy() {
    }

    public static boolean isValidUserProfileId(String profileId) {
        String normalized = ProfileIdentity.normalize(profileId);
        return !ProfileIdentity.DEFAULT_PROFILE_ID.equals(normalized)
                && normalized.length() <= MAX_PROFILE_ID_LENGTH
                && normalized.matches(PROFILE_ID_PATTERN);
    }

    public static List<String> normalizeIds(List<String> profileIds) {
        Set<String> normalized = new LinkedHashSet<>();
        normalized.add(ProfileIdentity.DEFAULT_PROFILE_ID);
        if (profileIds != null) {
            for (String profileId : profileIds) {
                if (isValidUserProfileId(profileId)) {
                    normalized.add(ProfileIdentity.normalize(profileId));
                }
            }
        }
        return Collections.unmodifiableList(new ArrayList<>(normalized));
    }

    public static String serialize(List<String> profileIds) {
        return join(normalizeIds(profileIds));
    }

    public static List<String> deserialize(String serializedIds) {
        if (serializedIds == null || serializedIds.trim().isEmpty()) {
            return normalizeIds(Collections.<String>emptyList());
        }
        String[] parts = serializedIds.split(",");
        List<String> ids = new ArrayList<>();
        Collections.addAll(ids, parts);
        return normalizeIds(ids);
    }

    public static boolean contains(List<String> profileIds, String profileId) {
        String normalized = ProfileIdentity.normalize(profileId);
        return normalizeIds(profileIds).contains(normalized);
    }

    public static String selectActiveId(List<String> profileIds, String requestedId) {
        String normalized = ProfileIdentity.normalize(requestedId);
        return contains(profileIds, normalized)
                ? normalized
                : ProfileIdentity.DEFAULT_PROFILE_ID;
    }

    public static List<String> add(List<String> profileIds, String profileId) {
        if (!isValidUserProfileId(profileId)) {
            throw new IllegalArgumentException("Invalid user profile id");
        }
        List<String> ids = new ArrayList<>(normalizeIds(profileIds));
        String normalized = ProfileIdentity.normalize(profileId);
        if (!ids.contains(normalized)) {
            ids.add(normalized);
        }
        return Collections.unmodifiableList(ids);
    }

    public static List<String> remove(List<String> profileIds, String profileId) {
        String normalized = ProfileIdentity.normalize(profileId);
        if (ProfileIdentity.DEFAULT_PROFILE_ID.equals(normalized)) {
            throw new IllegalArgumentException("The default profile cannot be removed");
        }
        List<String> ids = new ArrayList<>(normalizeIds(profileIds));
        ids.remove(normalized);
        return Collections.unmodifiableList(ids);
    }

    private static String join(List<String> values) {
        StringBuilder builder = new StringBuilder();
        for (String value : values) {
            if (builder.length() > 0) {
                builder.append(',');
            }
            builder.append(value);
        }
        return builder.toString();
    }
}
