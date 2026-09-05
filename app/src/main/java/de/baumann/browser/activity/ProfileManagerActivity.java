package de.baumann.browser.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;
import androidx.webkit.WebViewFeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.baumann.browser.R;
import de.baumann.browser.database.RecordAction;
import de.baumann.browser.unit.ProfileCatalogPolicy;
import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileIdentity;
import de.baumann.browser.unit.ProfileMetadata;
import de.baumann.browser.unit.ProfilePreferencesStore;

/** Lightweight profile catalog UI. */
public class ProfileManagerActivity extends AppCompatActivity {

    private LinearLayout profileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.profile_manager_title);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(12);
        root.setPadding(padding, padding, padding, padding);

        TextView description = new TextView(this);
        description.setText(R.string.profile_manager_summary);
        description.setPadding(0, 0, 0, dp(8));
        root.addView(description, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView capability = new TextView(this);
        capability.setText(WebViewFeature.isFeatureSupported(WebViewFeature.MULTI_PROFILE)
                ? "WebView multi-profile isolation: supported"
                : "WebView multi-profile isolation: unavailable on this device; app-owned profile records remain isolated");
        capability.setPadding(0, 0, 0, dp(8));
        root.addView(capability, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        ScrollView scroll = new ScrollView(this);
        profileList = new LinearLayout(this);
        profileList.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(profileList, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT, ScrollView.LayoutParams.WRAP_CONTENT));
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        Button add = new Button(this);
        add.setText(R.string.profile_new);
        add.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { showEditor(null); }
        });
        root.addView(add, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        setContentView(root);
        renderProfiles();
    }

    private void renderProfiles() {
        profileList.removeAllViews();
        String activeId = ProfileCatalogStore.getActiveProfileId(this);
        List<ProfileMetadata> profiles = ProfileCatalogStore.load(this);
        for (final ProfileMetadata profile : profiles) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            row.setPadding(dp(4), dp(4), dp(4), dp(4));
            row.setBackgroundResource(android.R.drawable.list_selector_background);

            TextView label = new TextView(this);
            String marker = profile.getId().equals(activeId) ? getString(R.string.profile_active) + "  " : "";
            label.setText(marker + profile.getName() + "  (" + profile.getId() + ")");
            label.setGravity(Gravity.CENTER_VERTICAL);
            label.setMaxLines(2);
            row.addView(label, new LinearLayout.LayoutParams(0, dp(56), 1f));

            Button edit = new Button(this);
            edit.setText(R.string.profile_edit);
            edit.setEnabled(!ProfileIdentity.DEFAULT_PROFILE_ID.equals(profile.getId()));
            edit.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { showEditor(profile); }
            });
            row.addView(edit, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(56)));

            Button delete = new Button(this);
            delete.setText(R.string.profile_delete);
            delete.setEnabled(!ProfileIdentity.DEFAULT_PROFILE_ID.equals(profile.getId()));
            delete.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { confirmDelete(profile); }
            });
            row.addView(delete, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(56)));

            row.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) { selectProfile(profile.getId()); }
            });
            profileList.addView(row, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        }
    }

    private void selectProfile(String profileId) {
        String current = ProfileCatalogStore.getActiveProfileId(this);
        String normalizedTarget = ProfileIdentity.normalize(profileId);
        if (current.equals(normalizedTarget)) return;

        // Persist every profile-local browser/privacy preference before changing the active namespace.
        ProfilePreferencesStore.saveGlobalToProfile(this, current);
        ProfilePreferencesStore.initializeProfile(this, normalizedTarget);
        if (!ProfileCatalogStore.setActiveProfileId(this, normalizedTarget)) return;
        ProfilePreferencesStore.loadProfileToGlobal(this, normalizedTarget);

        PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .edit().putInt("restart_changed", 1).apply();
        Toast.makeText(this, R.string.profile_switched, Toast.LENGTH_LONG).show();
        renderProfiles();
    }

    private void showEditor(final ProfileMetadata existing) {
        final boolean isNew = existing == null;
        LinearLayout fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        fields.setPadding(dp(4), 0, dp(4), 0);

        final EditText id = editText(R.string.profile_id, existing == null ? "" : existing.getId());
        id.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        if (!isNew) id.setEnabled(false);
        fields.addView(id);
        final EditText name = editText(R.string.profile_name, existing == null ? "" : existing.getName()); fields.addView(name);
        final EditText color = editText(R.string.profile_color, existing == null ? "" : existing.getColor()); fields.addView(color);
        final EditText icon = editText(R.string.profile_icon, existing == null ? "" : existing.getIcon()); fields.addView(icon);
        final EditText notes = editText(R.string.profile_notes, existing == null ? "" : existing.getNotes()); fields.addView(notes);
        final EditText tags = editText(R.string.profile_tags, existing == null ? "" : join(existing.getTags())); fields.addView(tags);
        final EditText group = editText(R.string.profile_group, existing == null ? "" : existing.getGroup()); fields.addView(group);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(fields);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(isNew ? R.string.profile_new : R.string.profile_edit)
                .setView(scroll)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.profile_saved, null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface ignored) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        String profileId = id.getText().toString().trim();
                        String profileName = name.getText().toString().trim();
                        if (profileId.isEmpty() || profileName.isEmpty()) {
                            Toast.makeText(ProfileManagerActivity.this, R.string.profile_required_fields, Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (isNew && !ProfileCatalogPolicy.isValidUserProfileId(profileId)) {
                            Toast.makeText(ProfileManagerActivity.this, R.string.profile_invalid_id, Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (isNew && ProfileCatalogStore.get(ProfileManagerActivity.this, profileId) != null) {
                            Toast.makeText(ProfileManagerActivity.this, R.string.profile_duplicate_id, Toast.LENGTH_LONG).show();
                            return;
                        }
                        List<String> tagValues = new ArrayList<>();
                        String rawTags = tags.getText().toString();
                        if (!rawTags.trim().isEmpty()) tagValues.addAll(Arrays.asList(rawTags.split(",")));
                        ProfileMetadata metadata = new ProfileMetadata(profileId, profileName,
                                color.getText().toString(), icon.getText().toString(), notes.getText().toString(),
                                tagValues, group.getText().toString());
                        if (!ProfileCatalogStore.save(ProfileManagerActivity.this, metadata)) {
                            Toast.makeText(ProfileManagerActivity.this, R.string.profile_invalid_id, Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (isNew) {
                            ProfilePreferencesStore.initializeProfile(ProfileManagerActivity.this, profileId);
                        }
                        dialog.dismiss();
                        renderProfiles();
                    }
                });
            }
        });
        dialog.show();
    }

    private void confirmDelete(final ProfileMetadata profile) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.profile_delete)
                .setMessage(profile.getName() + " (" + profile.getId() + ")")
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.profile_delete, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        try {
                            RecordAction.deleteProfileRecords(ProfileManagerActivity.this, profile.getId());
                        } catch (RuntimeException e) {
                            Toast.makeText(ProfileManagerActivity.this, "Unable to delete profile data", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (ProfileCatalogStore.delete(ProfileManagerActivity.this, profile.getId())) {
                            ProfilePreferencesStore.deleteProfile(ProfileManagerActivity.this, profile.getId());
                            Toast.makeText(ProfileManagerActivity.this, R.string.profile_deleted, Toast.LENGTH_SHORT).show();
                            renderProfiles();
                        }
                    }
                }).show();
    }

    private EditText editText(int hintRes, String value) {
        EditText input = new EditText(this);
        input.setHint(hintRes);
        input.setText(value);
        input.setPadding(dp(8), dp(6), dp(8), dp(6));
        return input;
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }

    private String join(List<String> values) {
        StringBuilder result = new StringBuilder();
        for (String value : values) {
            if (result.length() > 0) result.append(", ");
            result.append(value);
        }
        return result.toString();
    }
}