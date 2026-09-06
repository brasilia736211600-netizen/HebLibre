package de.baumann.browser.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.preference.PreferenceManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import de.baumann.browser.database.Record;
import de.baumann.browser.database.RecordAction;
import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileIdentity;
import de.baumann.browser.unit.ProfileMetadata;
import de.baumann.browser.unit.ProfilePreferencesStore;
import de.baumann.browser.unit.ProfileTransferCodec;
import de.baumann.browser.unit.RecordUnit;

public class ProfileManagerSmokeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyLegacyDatabaseMigration();
        verifyProfileScopedRecords();
        verifyProfilePreferencesIsolation();
        verifyProfilePreferenceTransferRoundTrip();
        verifyProfileDeletionPurgesRecords();
        seedDefaultSessionForLauncherRestore();
        startActivity(new Intent(this, ProfileManagerActivity.class));
        finish();
    }

    private void verifyLegacyDatabaseMigration() {
        deleteDatabase("Ninja4.db");
        SQLiteDatabase legacy = SQLiteDatabase.openOrCreateDatabase(
                getDatabasePath("Ninja4.db"), null);
        legacy.execSQL("CREATE TABLE HISTORY ( TITLE text, URL text, TIME integer )");
        legacy.execSQL("CREATE TABLE BOOKAMRK ( TITLE text, URL text, TIME integer )");
        legacy.execSQL("CREATE TABLE TAB ( TITLE text, URL text, TIME integer )");
        legacy.execSQL("CREATE TABLE GRID ( TITLE text, URL text, FILENAME text, ORDINAL integer )");
        legacy.execSQL("CREATE TABLE WHITELIST ( DOMAIN text, PROFILE_ID text DEFAULT 'default' )");
        legacy.execSQL("CREATE TABLE JAVASCRIPT ( DOMAIN text, PROFILE_ID text DEFAULT 'default' )");
        legacy.execSQL("CREATE TABLE COOKIE ( DOMAIN text, PROFILE_ID text DEFAULT 'default' )");
        legacy.execSQL("CREATE TABLE REMOTE ( DOMAIN text, PROFILE_ID text DEFAULT 'default' )");
        legacy.execSQL("INSERT INTO HISTORY(TITLE,URL,TIME) VALUES ('Legacy history','https://legacy.example/history',11)");
        legacy.execSQL("INSERT INTO BOOKAMRK(TITLE,URL,TIME) VALUES ('Legacy bookmark','https://legacy.example/bookmark',12)");
        legacy.execSQL("INSERT INTO TAB(TITLE,URL,TIME) VALUES ('Legacy tab','https://legacy.example/tab',13)");
        legacy.setVersion(5);
        legacy.close();

        RecordAction action = new RecordAction(this);
        action.open(false);
        require(action.listHistory().size() == 1, "legacy history migration lost data");
        require(action.listBookmark(this, false, 0L).size() == 1, "legacy bookmark migration lost data");
        require(action.listTab().size() == 1, "legacy tab migration lost data");
        action.close();
    }

    private void verifyProfileScopedRecords() {
        final String firstProfile = "smoke-a";
        final String secondProfile = "smoke-b";
        ProfileCatalogStore.save(this, new ProfileMetadata(
                firstProfile, "Smoke A", "", "", "", Collections.<String>emptyList(), ""));
        ProfileCatalogStore.save(this, new ProfileMetadata(
                secondProfile, "Smoke B", "", "", "", Collections.<String>emptyList(), ""));

        clearRecords(firstProfile);
        clearRecords(secondProfile);

        require(ProfileCatalogStore.setActiveProfileId(this, firstProfile), "cannot select first profile");
        RecordAction writer = new RecordAction(this);
        writer.open(true);
        writer.addHistory(new Record("A history", "https://profile-a.example/history", 1L, -1));
        writer.addBookmark(new Record("A bookmark", "https://profile-a.example/bookmark", 2L, -1));
        writer.addTab(new Record("A tab", "https://profile-a.example/tab", 3L, -1));
        writer.addDomain("profile-a.example", RecordUnit.TABLE_WHITELIST, firstProfile);
        writer.close();

        require(ProfileCatalogStore.setActiveProfileId(this, secondProfile), "cannot select second profile");
        RecordAction reader = new RecordAction(this);
        reader.open(false);
        require(reader.listHistory().isEmpty(), "history leaked across profiles");
        require(reader.listBookmark(this, false, 0L).isEmpty(), "bookmark leaked across profiles");
        require(reader.listTab().isEmpty(), "tab leaked across profiles");
        require(reader.listDomains(RecordUnit.TABLE_WHITELIST, firstProfile).size() == 1,
                "first profile whitelist disappeared");
        require(reader.listDomains(RecordUnit.TABLE_WHITELIST, secondProfile).isEmpty(),
                "whitelist leaked into second profile");
        reader.close();

        require(ProfileCatalogStore.setActiveProfileId(this, firstProfile), "cannot restore first profile");
        reader = new RecordAction(this);
        reader.open(false);
        require(reader.listHistory().size() == 1, "first profile history missing");
        require(reader.listBookmark(this, false, 0L).size() == 1, "first profile bookmark missing");
        require(reader.listTab().size() == 1, "first profile tab missing");
        require(reader.listDomains(RecordUnit.TABLE_WHITELIST, firstProfile).size() == 1, "first profile whitelist missing");
        reader.close();

        verifyProfileImportRollback(firstProfile);

        clearRecords(firstProfile);
        clearRecords(secondProfile);
        require(ProfileCatalogStore.setActiveProfileId(this, RecordUnit.DEFAULT_PROFILE_ID),
                "cannot restore default profile");
        ProfileCatalogStore.delete(this, firstProfile);
        ProfileCatalogStore.delete(this, secondProfile);
    }

    private void verifyProfilePreferencesIsolation() {
        final String firstProfile = "prefs-a";
        final String secondProfile = "prefs-b";
        ProfileCatalogStore.save(this, new ProfileMetadata(
                firstProfile, "Prefs A", "", "", "", Collections.<String>emptyList(), ""));
        ProfileCatalogStore.save(this, new ProfileMetadata(
                secondProfile, "Prefs B", "", "", "", Collections.<String>emptyList(), ""));

        SharedPreferences global = PreferenceManager.getDefaultSharedPreferences(this);
        boolean originalDesktop = global.getBoolean("desktop_mode", false);
        String originalAgent = global.getString("userAgent", "");

        ProfilePreferencesStore.initializeProfile(this, firstProfile);
        ProfilePreferencesStore.initializeProfile(this, secondProfile);

        global.edit().putBoolean("desktop_mode", true).putString("userAgent", "Prefs-A-UA").commit();
        ProfilePreferencesStore.saveGlobalToProfile(this, firstProfile);
        global.edit().putBoolean("desktop_mode", false).putString("userAgent", "Prefs-B-UA").commit();
        ProfilePreferencesStore.saveGlobalToProfile(this, secondProfile);

        ProfilePreferencesStore.loadProfileToGlobal(this, firstProfile);
        require(global.getBoolean("desktop_mode", false), "first profile desktop preference missing");
        require("Prefs-A-UA".equals(global.getString("userAgent", "")), "first profile user agent leaked or missing");

        ProfilePreferencesStore.loadProfileToGlobal(this, secondProfile);
        require(!global.getBoolean("desktop_mode", true), "second profile inherited first profile desktop preference");
        require("Prefs-B-UA".equals(global.getString("userAgent", "")), "second profile user agent missing or polluted");

        global.edit().putBoolean("desktop_mode", originalDesktop).putString("userAgent", originalAgent).commit();
        ProfilePreferencesStore.deleteProfile(this, firstProfile);
        ProfilePreferencesStore.deleteProfile(this, secondProfile);
        ProfileCatalogStore.delete(this, firstProfile);
        ProfileCatalogStore.delete(this, secondProfile);
        require(ProfileCatalogStore.setActiveProfileId(this, ProfileIdentity.DEFAULT_PROFILE_ID),
                "cannot restore default after preference smoke");
    }

    private void verifyProfilePreferenceTransferRoundTrip() {
        final String profileId = "prefs-transfer";
        ProfileCatalogStore.save(this, new ProfileMetadata(
                profileId, "Prefs Transfer", "", "", "", Collections.<String>emptyList(), ""));
        require(ProfileCatalogStore.setActiveProfileId(this, profileId), "cannot select transfer profile");

        SharedPreferences global = PreferenceManager.getDefaultSharedPreferences(this);
        global.edit().putBoolean("desktop_mode", true).putString("userAgent", "Transfer-UA").commit();
        ProfilePreferencesStore.saveGlobalToProfile(this, profileId);
        Map<String, String> snapshot = ProfilePreferencesStore.snapshot(this, profileId);
        String encoded = ProfileTransferCodec.encodePlain(
                ProfileCatalogStore.get(this, profileId),
                Collections.<Record>emptyList(),
                Collections.<Record>emptyList(),
                Collections.<Record>emptyList(),
                snapshot);
        ProfileTransferCodec.TransferPackage decoded = ProfileTransferCodec.decode(encoded, null);
        require("b:true".equals(decoded.getPreferences().get("desktop_mode")),
                "profile preference snapshot lost desktop mode");
        require("s:Transfer-UA".equals(decoded.getPreferences().get("userAgent")),
                "profile preference snapshot lost user agent");

        String importedId = "prefs-transfer-import";
        ProfileCatalogStore.save(this, new ProfileMetadata(
                importedId, "Prefs Transfer Import", "", "", "", Collections.<String>emptyList(), ""));
        ProfilePreferencesStore.restore(this, importedId, decoded.getPreferences());
        require(ProfileCatalogStore.setActiveProfileId(this, importedId), "cannot select restored preference profile");
        require(global.getBoolean("desktop_mode", false), "restored profile desktop mode missing");
        require("Transfer-UA".equals(global.getString("userAgent", "")), "restored profile user agent missing");
        ProfilePreferencesStore.deleteProfile(this, profileId);
        ProfilePreferencesStore.deleteProfile(this, importedId);
        ProfileCatalogStore.delete(this, profileId);
        ProfileCatalogStore.delete(this, importedId);
        require(ProfileCatalogStore.setActiveProfileId(this, ProfileIdentity.DEFAULT_PROFILE_ID),
                "cannot restore default after transfer preference smoke");
    }

    private void verifyProfileImportRollback(String profileId) {
        Record valid = new Record("transaction-valid", "https://transaction.example/valid", 21L, -1);
        Record invalid = new Record("", "https://transaction.example/invalid", 22L, -1);
        RecordAction action = new RecordAction(this);
        action.open(true);
        try {
            try {
                action.importProfileRecords(
                        Arrays.asList(valid, invalid),
                        Collections.singletonList(valid),
                        Collections.singletonList(valid),
                        profileId);
                throw new IllegalStateException("invalid profile import unexpectedly succeeded");
            } catch (IllegalArgumentException expected) {
                // expected: transaction must roll back the first valid history record too
            }
        } finally {
            action.close();
        }

        action = new RecordAction(this);
        action.open(false);
        require(action.listHistory().size() == 1, "failed profile import left partial history");
        require(action.listBookmark(this, false, 0L).size() == 1, "failed profile import left partial bookmarks");
        require(action.listTab().size() == 1, "failed profile import left partial tabs");
        action.close();
    }

    private void verifyProfileDeletionPurgesRecords() {
        final String profileId = "smoke-delete";
        ProfileCatalogStore.save(this, new ProfileMetadata(
                profileId, "Smoke Delete", "", "", "", Collections.<String>emptyList(), ""));
        clearRecords(profileId);

        RecordAction action = new RecordAction(this);
        action.open(true);
        action.addHistory(new Record("delete history", "https://delete.example/history", 31L, -1));
        action.addBookmark(new Record("delete bookmark", "https://delete.example/bookmark", 32L, -1));
        action.addTab(new Record("delete tab", "https://delete.example/tab", 33L, -1));
        action.addDomain("delete.example", RecordUnit.TABLE_WHITELIST, profileId);
        action.addDomain("delete.example", RecordUnit.TABLE_JAVASCRIPT, profileId);
        action.addDomain("delete.example", RecordUnit.TABLE_COOKIE, profileId);
        action.addDomain("delete.example", RecordUnit.TABLE_REMOTE, profileId);
        action.close();

        RecordAction.deleteProfileRecords(this, profileId);
        require(ProfileCatalogStore.setActiveProfileId(this, profileId), "cannot select deletion profile");
        action = new RecordAction(this);
        action.open(false);
        require(action.listHistory().isEmpty(), "profile deletion left history records");
        require(action.listBookmark(this, false, 0L).isEmpty(), "profile deletion left bookmark records");
        require(action.listTab().isEmpty(), "profile deletion left tab records");
        require(action.listDomains(RecordUnit.TABLE_WHITELIST, profileId).isEmpty(), "profile deletion left whitelist records");
        require(action.listDomains(RecordUnit.TABLE_JAVASCRIPT, profileId).isEmpty(), "profile deletion left javascript records");
        require(action.listDomains(RecordUnit.TABLE_COOKIE, profileId).isEmpty(), "profile deletion left cookie records");
        require(action.listDomains(RecordUnit.TABLE_REMOTE, profileId).isEmpty(), "profile deletion left remote records");
        action.close();

        require(ProfileCatalogStore.setActiveProfileId(this, profileId), "cannot reselect deletion profile");
        SharedPreferences global = PreferenceManager.getDefaultSharedPreferences(this);
        boolean originalDesktop = global.getBoolean("desktop_mode", false);
        global.edit().putBoolean("desktop_mode", true).commit();
        ProfilePreferencesStore.saveGlobalToProfile(this, profileId);
        require(ProfileCatalogStore.delete(this, profileId), "active profile catalog deletion failed");
        require(ProfileIdentity.DEFAULT_PROFILE_ID.equals(ProfileCatalogStore.getActiveProfileId(this)),
                "active profile deletion did not restore default id");
        require(!global.getBoolean("desktop_mode", true),
                "active profile deletion did not load default profile preferences");
        global.edit().putBoolean("desktop_mode", originalDesktop).commit();
        require(ProfileCatalogStore.setActiveProfileId(this, RecordUnit.DEFAULT_PROFILE_ID),
                "cannot restore default after delete smoke");
    }

    private void seedDefaultSessionForLauncherRestore() {
        require(ProfileCatalogStore.setActiveProfileId(this, RecordUnit.DEFAULT_PROFILE_ID),
                "cannot select default profile for session restore");
        RecordAction action = new RecordAction(this);
        action.open(true);
        action.clearTable(RecordUnit.TABLE_TAB);
        action.addTab(new Record(
                "Smoke restored tab", "https://example.com", 0L, -1), RecordUnit.DEFAULT_PROFILE_ID);
        action.close();
    }

    private void clearRecords(String profileId) {
        require(ProfileCatalogStore.setActiveProfileId(this, profileId),
                "cannot select profile for cleanup: " + profileId);
        RecordAction action = new RecordAction(this);
        action.open(true);
        action.clearTable(RecordUnit.TABLE_HISTORY);
        action.clearTable(RecordUnit.TABLE_BOOKMARK);
        action.clearTable(RecordUnit.TABLE_TAB);
        action.clearTable(RecordUnit.TABLE_WHITELIST);
        action.clearTable(RecordUnit.TABLE_JAVASCRIPT);
        action.clearTable(RecordUnit.TABLE_COOKIE);
        action.clearTable(RecordUnit.TABLE_REMOTE);
        action.close();
    }

    private void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}