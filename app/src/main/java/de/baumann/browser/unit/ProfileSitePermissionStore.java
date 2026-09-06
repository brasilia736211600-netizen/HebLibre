package de.baumann.browser.unit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/** Local profile-scoped site permission decisions. */
public final class ProfileSitePermissionStore {
    private static final String PREFIX = "site_permission_v1|";

    public static final class Entry {
        private final String origin;
        private final String permission;
        private final String decision;

        public Entry(String origin, String permission, String decision) {
            this.origin = origin;
            this.permission = permission;
            this.decision = decision;
        }

        public String getOrigin() { return origin; }
        public String getPermission() { return permission; }
        public String getDecision() { return decision; }
    }

    private ProfileSitePermissionStore() { }

    private static SharedPreferences preferences(Context context, String profileId) {
        String normalized = ProfileIdentity.normalize(profileId);
        String suffix = Base64.encodeToString(
                normalized.getBytes(StandardCharsets.UTF_8), Base64.NO_WRAP | Base64.URL_SAFE);
        return context.getApplicationContext().getSharedPreferences(
                "profile_site_permissions_" + suffix, Context.MODE_PRIVATE);
    }

    private static String ruleKey(String origin, String permission) {
        String key = ProfileSitePermissionPolicy.key(origin, permission);
        return key.isEmpty() ? "" : PREFIX + key;
    }

    public static String getDecision(Context context, String profileId, String origin, String permission) {
        String key = ruleKey(origin, permission);
        if (key.isEmpty()) return "";
        return preferences(context, profileId).getString(key, "");
    }

    public static boolean setDecision(Context context, String profileId, String origin,
                                      String permission, String decision) {
        String normalizedOrigin = ProfileSitePermissionPolicy.normalizeOrigin(origin);
        String key = ruleKey(normalizedOrigin, permission);
        if (key.isEmpty() || !ProfileSitePermissionPolicy.isDecision(decision)) return false;
        preferences(context, profileId).edit().putString(key, decision).apply();
        return true;
    }

    public static void clearDecision(Context context, String profileId, String origin, String permission) {
        String key = ruleKey(origin, permission);
        if (key.isEmpty()) return;
        preferences(context, profileId).edit().remove(key).apply();
    }

    public static List<Entry> list(Context context, String profileId) {
        List<Entry> result = new ArrayList<>();
        for (Map.Entry<String, ?> item : preferences(context, profileId).getAll().entrySet()) {
            if (!item.getKey().startsWith(PREFIX) || !(item.getValue() instanceof String)) continue;
            String value = (String) item.getValue();
            if (!ProfileSitePermissionPolicy.isDecision(value)) continue;
            String remainder = item.getKey().substring(PREFIX.length());
            int separator = remainder.indexOf('|');
            if (separator <= 0 || separator == remainder.length() - 1) continue;
            String permission = remainder.substring(0, separator);
            if (!ProfileSitePermissionPolicy.isPermission(permission)) continue;
            String origin = remainder.substring(separator + 1);
            origin = ProfileSitePermissionPolicy.normalizeOrigin(origin);
            if (!origin.isEmpty()) result.add(new Entry(origin, permission, value));
        }
        Collections.sort(result, new Comparator<Entry>() {
            @Override public int compare(Entry a, Entry b) {
                int permission = a.getPermission().compareTo(b.getPermission());
                if (permission != 0) return permission;
                return a.getOrigin().compareTo(b.getOrigin());
            }
        });
        return result;
    }

    public static void clearProfile(Context context, String profileId) {
        preferences(context, profileId).edit().clear().apply();
    }
}
