package de.baumann.browser.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import java.util.Arrays;
import java.util.Collections;

import de.baumann.browser.database.Record;
import de.baumann.browser.database.RecordAction;
import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileMetadata;
import de.baumann.browser.unit.RecordUnit;

public class ProfileManagerSmokeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        verifyLegacyDatabaseMigration();
        verifyProfileScopedRecords();
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
        writer.close();

        require(ProfileCatalogStore.setActiveProfileId(this, secondProfile), "cannot select second profile");
        RecordAction reader = new RecordAction(this);
        reader.open(false);
        require(reader.listHistory().isEmpty(), "history leaked across profiles");
        require(reader.listBookmark(this, false, 0L).isEmpty(), "bookmark leaked across profiles");
        require(reader.listTab().isEmpty(), "tab leaked across profiles");
        reader.close();

        require(ProfileCatalogStore.setActiveProfileId(this, firstProfile), "cannot restore first profile");
        reader = new RecordAction(this);
        reader.open(false);
        require(reader.listHistory().size() == 1, "first profile history missing");
        require(reader.listBookmark(this, false, 0L).size() == 1, "first profile bookmark missing");
        require(reader.listTab().size() == 1, "first profile tab missing");
        reader.close();

        verifyProfileImportRollback(firstProfile);

        clearRecords(firstProfile);
        clearRecords(secondProfile);
        require(ProfileCatalogStore.setActiveProfileId(this, RecordUnit.DEFAULT_PROFILE_ID),
                "cannot restore default profile");
        ProfileCatalogStore.delete(this, firstProfile);
        ProfileCatalogStore.delete(this, secondProfile);
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
        action.close();
    }

    private void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}