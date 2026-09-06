package de.baumann.browser.unit;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Keeps browser/privacy preferences local to a profile while preserving the existing
 * global SharedPreferences contract used throughout the app.
 */
public final class ProfilePreferencesStore {
    private static final String STORE_PREFIX = "profile_preferences_";
    private static final String INITIALIZED_KEY = "__profile_preferences_initialized";

    private static final Set<String> BOOLEAN_KEYS = new HashSet<>(Arrays.asList(
            "desktop_mode", "screenshot_protection", "block_media_permissions",
            "block_third_party_cookies", "send_download_cookies", "sp_images",
            "sp_savedata", "https_only", "gpc_enabled", "saveHistory", "sp_location",
            "sp_ad_block", "sp_javascript", "sp_cookies", "sp_remote"
    ));

    private static final Set<String> STRING_KEYS = new HashSet<>(Arrays.asList(
            "favoriteURL", "sp_search_engine", "sp_search_engine_custom", "userAgent",
            "preferred_language", "ua_preset", "proxy_url", "proxy_bypass"
    ));

    private ProfilePreferencesStore() { }

    public static void initializeProfile(Context context, String profileId) {
        if (context == null || !isSupportedProfileId(profileId)) return;
        SharedPreferences profile = profilePreferences(context, profileId);
        if (profile.getBoolean(INITIALIZED_KEY, false)) return;
        copyGlobalToProfile(profile, PreferenceManager.getDefaultSharedPreferences(context));
        profile.edit().putBoolean(INITIALIZED_KEY, true).apply();
    }

    public static void saveGlobalToProfile(Context context, String profileId) {
        if (context == null || !isSupportedProfileId(profileId)) return;
        SharedPreferences global = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences profile = profilePreferences(context, profileId);
        copyGlobalToProfile(profile, global);
        profile.edit().putBoolean(INITIALIZED_KEY, true).apply();
    }

    public static void loadProfileToGlobal(Context context, String profileId) {
        if (context == null || !isSupportedProfileId(profileId)) return;
        initializeProfile(context, profileId);
        SharedPreferences profile = profilePreferences(context, profileId);
        SharedPreferences global = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = global.edit();
        for (String key : BOOLEAN_KEYS) {
            if (profile.contains(key)) editor.putBoolean(key, profile.getBoolean(key, defaultBoolean(key)));
        }
        for (String key : STRING_KEYS) {
            if (profile.contains(key)) editor.putString(key, profile.getString(key, defaultString(key)));
        }
        editor.apply();
    }

    public static Map<String, String> snapshot(Context context, String profileId) {
        Map<String, String> result = new LinkedHashMap<>();
        if (context == null || !isSupportedProfileId(profileId)) return result;
        initializeProfile(context, profileId);
        SharedPreferences profile = profilePreferences(context, profileId);
        for (String key : BOOLEAN_KEYS) result.put(key, "b:" + profile.getBoolean(key, defaultBoolean(key)));
        for (String key : STRING_KEYS) result.put(key, "s:" + profile.getString(key, defaultString(key)));
        return result;
    }

    public static void restore(Context context, String profileId, Map<String, String> values) {
        if (context == null || !isSupportedProfileId(profileId) || values == null) return;
        initializeProfile(context, profileId);
        SharedPreferences.Editor editor = profilePreferences(context, profileId).edit();
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (value == null) continue;
            if (BOOLEAN_KEYS.contains(key) && value.startsWith("b:")) {
                String raw = value.substring(2);
                if ("true".equals(raw) || "false".equals(raw)) editor.putBoolean(key, Boolean.parseBoolean(raw));
            } else if (STRING_KEYS.contains(key) && value.startsWith("s:")) {
                String raw = value.substring(2);
                if ("preferred_language".equals(key)) raw = ProfileLanguagePolicy.normalize(raw);
                if ("proxy_url".equals(key)) raw = ProfileProxyPolicy.normalize(raw);
                if ("proxy_bypass".equals(key)) raw = ProfileProxyPolicy.normalizeBypassRules(raw);
                if ("ua_preset".equals(key) && !raw.isEmpty() && !ProfileUserAgentPolicy.isValidPreset(raw)) continue;
                editor.putString(key, raw);
            }
        }
        editor.putBoolean(INITIALIZED_KEY, true).apply();
    }

    public static void deleteProfile(Context context, String profileId) {
        if (context == null || !ProfileCatalogPolicy.isValidUserProfileId(profileId)) return;
        profilePreferences(context, profileId).edit().clear().apply();
    }

    private static boolean isSupportedProfileId(String profileId) {
        String normalized = ProfileIdentity.normalize(profileId);
        return ProfileIdentity.DEFAULT_PROFILE_ID.equals(normalized)
                || ProfileCatalogPolicy.isValidUserProfileId(normalized);
    }

    private static SharedPreferences profilePreferences(Context context, String profileId) {
        return context.getSharedPreferences(STORE_PREFIX + ProfileIdentity.normalize(profileId), Context.MODE_PRIVATE);
    }

    private static void copyGlobalToProfile(SharedPreferences profile, SharedPreferences global) {
        SharedPreferences.Editor editor = profile.edit();
        for (String key : BOOLEAN_KEYS) editor.putBoolean(key, global.getBoolean(key, defaultBoolean(key)));
        for (String key : STRING_KEYS) {
            String value = global.getString(key, defaultString(key));
            if ("preferred_language".equals(key)) value = ProfileLanguagePolicy.normalize(value);
            if ("proxy_url".equals(key)) value = ProfileProxyPolicy.normalize(value);
            if ("proxy_bypass".equals(key)) value = ProfileProxyPolicy.normalizeBypassRules(value);
            if ("ua_preset".equals(key) && !value.isEmpty() && !ProfileUserAgentPolicy.isValidPreset(value)) value = "";
            editor.putString(key, value);
        }
        editor.apply();
    }

    private static boolean defaultBoolean(String key) {
        switch (key) {
            case "desktop_mode": case "screenshot_protection": case "block_third_party_cookies":
            case "https_only": case "gpc_enabled": case "sp_location":
                return false;
            default: return true;
        }
    }

    private static String defaultString(String key) {
        switch (key) {
            case "favoriteURL": return "https://github.com/scoute-dich/browser";
            case "sp_search_engine": return "0";
            case "sp_search_engine_custom": return "https://www.ecosia.org/search?q=";
            default: return "";
        }
    }
}
