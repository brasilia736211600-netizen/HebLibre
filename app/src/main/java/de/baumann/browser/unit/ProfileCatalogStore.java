package de.baumann.browser.unit;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Lightweight local-first store for the user-visible profile catalog.
 *
 * The catalog metadata is application-owned. WebView browsing-state isolation
 * remains provided by the AndroidX WebKit profile when that capability exists.
 */
public final class ProfileCatalogStore {

    private static final String CATALOG_IDS_KEY = "profile_catalog_ids";
    private static final String KEY_PREFIX = "profile_";
    private static final String KEY_NAME = "_name";
    private static final String KEY_COLOR = "_color";
    private static final String KEY_ICON = "_icon";
    private static final String KEY_NOTES = "_notes";
    private static final String KEY_TAGS = "_tags";
    private static final String KEY_GROUP = "_group";

    private ProfileCatalogStore() {
    }

    public static List<ProfileMetadata> load(Context context) {
        SharedPreferences preferences = preferences(context);
        List<String> ids = ProfileCatalogPolicy.deserialize(
                preferences.getString(CATALOG_IDS_KEY, null));
        ensureDefaultMetadata(preferences);

        List<ProfileMetadata> profiles = new ArrayList<>();
        for (String id : ids) {
            profiles.add(read(preferences, id));
        }
        return Collections.unmodifiableList(profiles);
    }

    public static ProfileMetadata get(Context context, String profileId) {
        String normalizedId = ProfileIdentity.normalize(profileId);
        SharedPreferences preferences = preferences(context);
        ensureCatalog(preferences);
        if (!ProfileCatalogPolicy.contains(
                ProfileCatalogPolicy.deserialize(preferences.getString(CATALOG_IDS_KEY, null)),
                normalizedId)) {
            return null;
        }
        return read(preferences, normalizedId);
    }

    public static String getActiveProfileId(Context context) {
        SharedPreferences preferences = preferences(context);
        ensureCatalog(preferences);
        List<String> ids = ProfileCatalogPolicy.deserialize(
                preferences.getString(CATALOG_IDS_KEY, null));
        String active = ProfileCatalogPolicy.selectActiveId(
                ids, preferences.getString(
                        ProfileIdentity.PREFERENCE_KEY, ProfileIdentity.DEFAULT_PROFILE_ID));
        if (!active.equals(preferences.getString(ProfileIdentity.PREFERENCE_KEY, null))) {
            preferences.edit().putString(ProfileIdentity.PREFERENCE_KEY, active).apply();
        }
        return active;
    }

    public static boolean setActiveProfileId(Context context, String profileId) {
        if (context == null) {
            return false;
        }
        SharedPreferences preferences = preferences(context);
        ensureCatalog(preferences);
        String normalizedId = ProfileIdentity.normalize(profileId);
        List<String> ids = ProfileCatalogPolicy.deserialize(
                preferences.getString(CATALOG_IDS_KEY, null));
        if (!ProfileCatalogPolicy.contains(ids, normalizedId)) {
            return false;
        }

        String currentId = ProfileIdentity.normalize(
                preferences.getString(ProfileIdentity.PREFERENCE_KEY, ProfileIdentity.DEFAULT_PROFILE_ID));
        if (currentId.equals(normalizedId)) {
            ProfilePreferencesStore.initializeProfile(context, normalizedId);
            return true;
        }

        ProfilePreferencesStore.saveGlobalToProfile(context, currentId);
        ProfilePreferencesStore.initializeProfile(context, normalizedId);
        preferences.edit().putString(ProfileIdentity.PREFERENCE_KEY, normalizedId).apply();
        ProfilePreferencesStore.loadProfileToGlobal(context, normalizedId);
        return true;
    }

    public static boolean save(Context context, ProfileMetadata metadata) {
        if (context == null || metadata == null
                || !ProfileCatalogPolicy.isValidUserProfileId(metadata.getId())) {
            return false;
        }

        SharedPreferences preferences = preferences(context);
        List<String> ids = ProfileCatalogPolicy.add(
                ProfileCatalogPolicy.deserialize(preferences.getString(CATALOG_IDS_KEY, null)),
                metadata.getId());

        SharedPreferences.Editor editor = preferences.edit()
                .putString(CATALOG_IDS_KEY, ProfileCatalogPolicy.serialize(ids))
                .putString(key(metadata.getId(), KEY_NAME), metadata.getName())
                .putString(key(metadata.getId(), KEY_COLOR), metadata.getColor())
                .putString(key(metadata.getId(), KEY_ICON), metadata.getIcon())
                .putString(key(metadata.getId(), KEY_NOTES), metadata.getNotes())
                .putString(key(metadata.getId(), KEY_GROUP), metadata.getGroup());

        editor.putStringSet(key(metadata.getId(), KEY_TAGS),
                new LinkedHashSet<>(metadata.getTags()));
        editor.apply();
        ProfilePreferencesStore.initializeProfile(context, metadata.getId());
        ensureDefaultMetadata(preferences);
        return true;
    }

    public static boolean delete(Context context, String profileId) {
        if (context == null) {
            return false;
        }

        String normalizedId = ProfileIdentity.normalize(profileId);
        if (!ProfileCatalogPolicy.isValidUserProfileId(normalizedId)) {
            return false;
        }

        SharedPreferences preferences = preferences(context);
        List<String> ids = ProfileCatalogPolicy.deserialize(
                preferences.getString(CATALOG_IDS_KEY, null));
        if (!ids.contains(normalizedId)) {
            return false;
        }

        boolean deletingActive = normalizedId.equals(getActiveProfileId(context));
        List<String> remaining = ProfileCatalogPolicy.remove(ids, normalizedId);
        SharedPreferences.Editor editor = preferences.edit()
                .putString(CATALOG_IDS_KEY, ProfileCatalogPolicy.serialize(remaining))
                .remove(key(normalizedId, KEY_NAME))
                .remove(key(normalizedId, KEY_COLOR))
                .remove(key(normalizedId, KEY_ICON))
                .remove(key(normalizedId, KEY_NOTES))
                .remove(key(normalizedId, KEY_TAGS))
                .remove(key(normalizedId, KEY_GROUP));

        if (deletingActive) {
            editor.putString(ProfileIdentity.PREFERENCE_KEY, ProfileIdentity.DEFAULT_PROFILE_ID)
                    .putInt("restart_changed", 1);
        }
        editor.apply();
        ProfilePreferencesStore.deleteProfile(context, normalizedId);
        if (deletingActive) {
            ProfilePreferencesStore.loadProfileToGlobal(context, ProfileIdentity.DEFAULT_PROFILE_ID);
        }
        return true;
    }

    private static ProfileMetadata read(SharedPreferences preferences, String id) {
        if (ProfileIdentity.DEFAULT_PROFILE_ID.equals(id)) {
            ensureDefaultMetadata(preferences);
        }
        Set<String> storedTags = preferences.getStringSet(
                key(id, KEY_TAGS), Collections.<String>emptySet());
        return new ProfileMetadata(
                id,
                preferences.getString(key(id, KEY_NAME), id),
                preferences.getString(key(id, KEY_COLOR), ""),
                preferences.getString(key(id, KEY_ICON), ""),
                preferences.getString(key(id, KEY_NOTES), ""),
                new ArrayList<>(storedTags),
                preferences.getString(key(id, KEY_GROUP), ""));
    }

    private static void ensureCatalog(SharedPreferences preferences) {
        String serialized = preferences.getString(CATALOG_IDS_KEY, null);
        List<String> ids = ProfileCatalogPolicy.deserialize(serialized);
        String normalized = ProfileCatalogPolicy.serialize(ids);
        if (!normalized.equals(serialized)) {
            preferences.edit().putString(CATALOG_IDS_KEY, normalized).apply();
        }
        ensureDefaultMetadata(preferences);
    }

    private static void ensureDefaultMetadata(SharedPreferences preferences) {
        String nameKey = key(ProfileIdentity.DEFAULT_PROFILE_ID, KEY_NAME);
        if (!preferences.contains(nameKey)) {
            preferences.edit()
                    .putString(nameKey, "Default")
                    .putString(key(ProfileIdentity.DEFAULT_PROFILE_ID, KEY_COLOR), "")
                    .putString(key(ProfileIdentity.DEFAULT_PROFILE_ID, KEY_ICON), "")
                    .putString(key(ProfileIdentity.DEFAULT_PROFILE_ID, KEY_NOTES), "")
                    .putStringSet(key(ProfileIdentity.DEFAULT_PROFILE_ID, KEY_TAGS),
                            Collections.<String>emptySet())
                    .putString(key(ProfileIdentity.DEFAULT_PROFILE_ID, KEY_GROUP), "")
                    .apply();
        }
    }

    private static SharedPreferences preferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    private static String key(String profileId, String suffix) {
        return KEY_PREFIX + profileId + suffix;
    }
}
