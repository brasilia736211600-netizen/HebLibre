package de.baumann.browser.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewFeature;

import java.util.Map;

import de.baumann.browser.database.RecordAction;
import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileIdentity;
import de.baumann.browser.unit.ProfilePreferencesStore;
import de.baumann.browser.unit.RecordUnit;

/** Transparent, local-only diagnostics for the active profile's privacy/storage state. */
public class ProfilePrivacyStatusActivity extends AppCompatActivity {

    private LinearLayout content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Privacy & storage");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(12);
        root.setPadding(padding, padding, padding, padding);

        ScrollView scroll = new ScrollView(this);
        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(content, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button refresh = new Button(this);
        refresh.setText("Refresh");
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { render(); }
        });
        actions.addView(refresh, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        Button clear = new Button(this);
        clear.setText("Clear profile data");
        clear.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { confirmClear(); }
        });
        actions.addView(clear, new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        root.addView(actions, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        setContentView(root);
        render();
    }

    private void render() {
        content.removeAllViews();
        String profileId = ProfileCatalogStore.getActiveProfileId(this);
        Map<String, String> settings = ProfilePreferencesStore.snapshot(this, profileId);

        add("Active profile: " + profileId, true);
        add(WebViewFeature.isFeatureSupported(WebViewFeature.MULTI_PROFILE)
                ? "WebView storage isolation: supported by AndroidX WebKit"
                : "WebView storage isolation: unavailable; app-owned records remain profile-scoped", true);

        RecordAction action = new RecordAction(this);
        action.open(false);
        try {
            addCount("History", action.listHistory().size());
            addCount("Bookmarks", action.listBookmark(this, false, 0L).size());
            addCount("Saved tabs", action.listTab().size());
            addCount("Ad-block whitelist", action.listDomains(RecordUnit.TABLE_WHITELIST, profileId).size());
            addCount("JavaScript rules", action.listDomains(RecordUnit.TABLE_JAVASCRIPT, profileId).size());
            addCount("Cookie rules", action.listDomains(RecordUnit.TABLE_COOKIE, profileId).size());
            addCount("Remote rules", action.listDomains(RecordUnit.TABLE_REMOTE, profileId).size());
        } finally {
            action.close();
        }

        add("Profile-owned protections", true);
        addProtection(settings, "https_only", "HTTPS-only");
        addProtection(settings, "gpc_enabled", "Global Privacy Control");
        addProtection(settings, "block_third_party_cookies", "Block third-party cookies");
        addProtection(settings, "sp_ad_block", "Ad blocking");
        addProtection(settings, "sp_javascript", "JavaScript");
        addProtection(settings, "sp_location", "Location access");
        addProtection(settings, "block_media_permissions", "Media permissions");
        addProtection(settings, "screenshot_protection", "Screenshot protection");

        add("Honest boundary: this page reports profile isolation and controls that the platform actually exposes. It does not claim fingerprint or anti-fraud evasion.", false);
    }

    private void confirmClear() {
        final String profileId = ProfileCatalogStore.getActiveProfileId(this);
        if (ProfileIdentity.DEFAULT_PROFILE_ID.equals(profileId)) {
            new AlertDialog.Builder(this)
                    .setTitle("Clear default profile data")
                    .setMessage("This removes app-owned history, bookmarks, saved tabs, and profile privacy-rule rows for the default profile. WebView-internal data is cleared only where supported by the platform APIs.")
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialog, int which) {
                            RecordAction.deleteProfileRecords(ProfilePrivacyStatusActivity.this, profileId);
                            render();
                        }
                    }).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Clear profile data")
                .setMessage("Remove this profile's app-owned history, bookmarks, saved tabs, and privacy-rule rows? The profile itself will remain.")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        RecordAction.deleteProfileRecords(ProfilePrivacyStatusActivity.this, profileId);
                        render();
                    }
                }).show();
    }

    private void addCount(String label, int count) {
        add(label + ": " + count, false);
    }

    private void addProtection(Map<String, String> settings, String key, String label) {
        String value = settings.get(key);
        boolean enabled = "b:true".equals(value);
        add(label + ": " + (enabled ? "ON" : "OFF"), false);
    }

    private void add(String text, boolean heading) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(heading ? 16 : 14);
        view.setPadding(0, dp(6), 0, dp(6));
        content.addView(view, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
