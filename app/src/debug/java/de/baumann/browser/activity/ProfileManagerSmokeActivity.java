package de.baumann.browser.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

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
        verifyProfileScopedRecords();
        startActivity(new Intent(this, ProfileManagerActivity.class));
        finish();
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

        clearRecords(firstProfile);
        clearRecords(secondProfile);
        require(ProfileCatalogStore.setActiveProfileId(this, RecordUnit.DEFAULT_PROFILE_ID),
                "cannot restore default profile");
        ProfileCatalogStore.delete(this, firstProfile);
        ProfileCatalogStore.delete(this, secondProfile);
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
