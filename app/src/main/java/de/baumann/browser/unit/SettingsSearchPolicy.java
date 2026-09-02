package de.baumann.browser.unit;

import java.util.Locale;

public final class SettingsSearchPolicy {
    private SettingsSearchPolicy() {
    }

    public static boolean matches(String query, CharSequence title, CharSequence summary) {
        if (query == null || query.trim().isEmpty()) {
            return true;
        }

        String normalizedQuery = query.trim().toLowerCase(Locale.ROOT);
        return contains(title, normalizedQuery) || contains(summary, normalizedQuery);
    }

    private static boolean contains(CharSequence value, String query) {
        return value != null && value.toString().toLowerCase(Locale.ROOT).contains(query);
    }
}
