package de.baumann.browser.unit;

import java.util.Locale;

/** Deterministic rules for deciding which tab URLs may be persisted/restored. */
public final class ProfileSessionPolicy {

    private ProfileSessionPolicy() {
    }

    public static boolean isRestorableUrl(String url) {
        if (url == null) {
            return false;
        }
        String normalized = url.trim().toLowerCase(Locale.ROOT);
        return normalized.startsWith("http://") || normalized.startsWith("https://");
    }

    public static String normalizeTitle(String title, String fallbackUrl) {
        if (title == null || title.trim().isEmpty()) {
            return fallbackUrl == null ? "" : fallbackUrl.trim();
        }
        return title.trim();
    }
}
