package de.baumann.browser.unit;

import android.net.Uri;

import java.util.Locale;

/** Pure decision/normalization rules for profile-local site permissions. */
public final class ProfileSitePermissionPolicy {
    public static final String PERMISSION_MEDIA = "media";
    public static final String PERMISSION_GEOLOCATION = "geolocation";
    public static final String DECISION_ALLOW = "allow";
    public static final String DECISION_DENY = "deny";

    private ProfileSitePermissionPolicy() { }

    public static String normalizeOrigin(String origin) {
        if (origin == null) return "";
        String value = origin.trim();
        if (value.isEmpty()) return "";
        try {
            Uri uri = Uri.parse(value);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (scheme == null || host == null) return "";
            scheme = scheme.toLowerCase(Locale.ROOT);
            host = host.toLowerCase(Locale.ROOT);
            if (!"http".equals(scheme) && !"https".equals(scheme)) return "";
            int port = uri.getPort();
            if (port > 0) return scheme + "://" + host + ":" + port;
            return scheme + "://" + host;
        } catch (RuntimeException ignored) {
            return "";
        }
    }

    public static boolean isPermission(String permission) {
        return PERMISSION_MEDIA.equals(permission) || PERMISSION_GEOLOCATION.equals(permission);
    }

    public static boolean isDecision(String decision) {
        return DECISION_ALLOW.equals(decision) || DECISION_DENY.equals(decision);
    }

    public static String key(String origin, String permission) {
        String normalized = normalizeOrigin(origin);
        if (normalized.isEmpty() || !isPermission(permission)) return "";
        return permission + "|" + normalized;
    }
}
