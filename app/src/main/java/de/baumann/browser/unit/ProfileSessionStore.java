package de.baumann.browser.unit;

import android.content.Context;

import java.util.List;

import de.baumann.browser.browser.AlbumController;
import de.baumann.browser.database.Record;
import de.baumann.browser.database.RecordAction;
import de.baumann.browser.view.NinjaWebView;

/** Persists the last browser tab set for a specific profile. */
public final class ProfileSessionStore {

    private ProfileSessionStore() {
    }

    public static void save(Context context, List<AlbumController> controllers) {
        if (context == null) {
            return;
        }
        save(context, controllers, ProfileCatalogStore.getActiveProfileId(context));
    }

    public static void save(Context context, List<AlbumController> controllers, String sessionProfileId) {
        if (context == null || controllers == null) {
            return;
        }

        final String profileId = ProfileSessionPolicy.persistenceProfileId(
                sessionProfileId, ProfileCatalogStore.getActiveProfileId(context));
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearTable(RecordUnit.TABLE_TAB, profileId);
        int order = 0;
        try {
            for (AlbumController controller : controllers) {
                if (!(controller instanceof NinjaWebView)) {
                    continue;
                }
                NinjaWebView webView = (NinjaWebView) controller;
                String url = webView.getUrl();
                if (!ProfileSessionPolicy.isRestorableUrl(url)) {
                    continue;
                }
                String title = ProfileSessionPolicy.normalizeTitle(webView.getTitle(), url);
                action.addTab(new Record(title, url.trim(), order++, -1), profileId);
            }
        } finally {
            action.close();
        }
    }

    public static List<Record> load(Context context) {
        RecordAction action = new RecordAction(context);
        action.open(false);
        try {
            return action.listTab();
        } finally {
            action.close();
        }
    }
}
