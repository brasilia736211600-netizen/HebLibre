package de.baumann.browser.unit;

import java.util.Locale;

/** Validates a conservative BCP-47 language-tag form for profile-local requests. */
public final class ProfileLanguagePolicy {
    private static final String TAG_PATTERN = "[A-Za-z]{2,8}(?:-[A-Za-z0-9]{1,8})*";

    private ProfileLanguagePolicy() {
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim();
        if (normalized.isEmpty() || normalized.length() > 64 || !normalized.matches(TAG_PATTERN)) {
            return "";
        }
        return normalized.toLowerCase(Locale.ROOT);
    }

    public static boolean isValid(String value) {
        return !normalize(value).isEmpty();
    }
}
