package de.baumann.browser.unit;

/**
 * Canonical application-level profile identity contract.
 *
 * This class deliberately contains no Android dependencies so the profile-id
 * normalization contract can be tested on the plain JVM before UI wiring.
 */
public final class ProfileIdentity {

    public static final String PREFERENCE_KEY = "current_profile_id";
    public static final String DEFAULT_PROFILE_ID = RecordUnit.DEFAULT_PROFILE_ID;

    private ProfileIdentity() {
        // Utility class.
    }

    public static String normalize(String profileId) {
        if (profileId == null) {
            return DEFAULT_PROFILE_ID;
        }

        String normalized = profileId.trim();
        return normalized.isEmpty() ? DEFAULT_PROFILE_ID : normalized;
    }
}
