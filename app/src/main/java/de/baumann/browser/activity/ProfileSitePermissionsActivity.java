package de.baumann.browser.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileSitePermissionPolicy;
import de.baumann.browser.unit.ProfileSitePermissionStore;

/** Simple profile-scoped editor for site permission decisions. */
public class ProfileSitePermissionsActivity extends AppCompatActivity {
    private LinearLayout list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Site permissions");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        int padding = dp(12);
        root.setPadding(padding, padding, padding, padding);

        TextView note = new TextView(this);
        note.setText("Decisions are local to the active profile. Remove a rule to inherit the normal browser policy. Android OS permissions remain the final gate.");
        note.setPadding(0, 0, 0, dp(8));
        root.addView(note, new LinearLayout.LayoutParams(-1, -2));

        ScrollView scroll = new ScrollView(this);
        list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(list, new ScrollView.LayoutParams(-1, -2));
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1f));

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button add = new Button(this);
        add.setText("Add rule");
        add.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { showEditor(null, null, null); }
        });
        actions.addView(add, new LinearLayout.LayoutParams(0, -2, 1f));

        Button refresh = new Button(this);
        refresh.setText("Refresh");
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { render(); }
        });
        actions.addView(refresh, new LinearLayout.LayoutParams(0, -2, 1f));
        root.addView(actions, new LinearLayout.LayoutParams(-1, -2));

        setContentView(root);
        render();
    }

    private void render() {
        list.removeAllViews();
        String profileId = ProfileCatalogStore.getActiveProfileId(this);
        List<ProfileSitePermissionStore.Entry> entries = ProfileSitePermissionStore.list(this, profileId);
        if (entries.isEmpty()) {
            TextView empty = new TextView(this);
            empty.setText("No site-specific rules. Sites inherit the normal browser policy.");
            empty.setPadding(0, dp(12), 0, dp(12));
            list.addView(empty);
            return;
        }

        for (final ProfileSitePermissionStore.Entry entry : entries) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.VERTICAL);
            row.setPadding(dp(4), dp(6), dp(4), dp(6));

            TextView label = new TextView(this);
            label.setText(entry.getOrigin() + "\n" + entry.getPermission() + ": " + entry.getDecision());
            label.setTextSize(15);
            row.addView(label, new LinearLayout.LayoutParams(-1, -2));

            LinearLayout buttons = new LinearLayout(this);
            buttons.setOrientation(LinearLayout.HORIZONTAL);

            Button allow = new Button(this);
            allow.setText("Allow");
            allow.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    ProfileSitePermissionStore.setDecision(ProfileSitePermissionsActivity.this,
                            ProfileCatalogStore.getActiveProfileId(ProfileSitePermissionsActivity.this),
                            entry.getOrigin(), entry.getPermission(), ProfileSitePermissionPolicy.DECISION_ALLOW);
                    render();
                }
            });
            buttons.addView(allow, new LinearLayout.LayoutParams(0, -2, 1f));

            Button deny = new Button(this);
            deny.setText("Deny");
            deny.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    ProfileSitePermissionStore.setDecision(ProfileSitePermissionsActivity.this,
                            ProfileCatalogStore.getActiveProfileId(ProfileSitePermissionsActivity.this),
                            entry.getOrigin(), entry.getPermission(), ProfileSitePermissionPolicy.DECISION_DENY);
                    render();
                }
            });
            buttons.addView(deny, new LinearLayout.LayoutParams(0, -2, 1f));

            Button remove = new Button(this);
            remove.setText("Remove");
            remove.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    ProfileSitePermissionStore.clearDecision(ProfileSitePermissionsActivity.this,
                            ProfileCatalogStore.getActiveProfileId(ProfileSitePermissionsActivity.this),
                            entry.getOrigin(), entry.getPermission());
                    render();
                }
            });
            buttons.addView(remove, new LinearLayout.LayoutParams(0, -2, 1f));

            row.addView(buttons, new LinearLayout.LayoutParams(-1, -2));
            list.addView(row, new LinearLayout.LayoutParams(-1, -2));
        }
    }

    private void showEditor(final String currentOrigin, final String currentPermission,
                            final String currentDecision) {
        LinearLayout fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        fields.setPadding(dp(6), 0, dp(6), 0);

        final EditText origin = new EditText(this);
        origin.setHint("https://example.com");
        origin.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI);
        if (currentOrigin != null) origin.setText(currentOrigin);
        fields.addView(origin);

        final EditText permission = new EditText(this);
        permission.setHint("media or geolocation");
        permission.setInputType(InputType.TYPE_CLASS_TEXT);
        if (currentPermission != null) permission.setText(currentPermission);
        fields.addView(permission);

        final EditText decision = new EditText(this);
        decision.setHint("allow or deny");
        decision.setInputType(InputType.TYPE_CLASS_TEXT);
        if (currentDecision != null) decision.setText(currentDecision);
        fields.addView(decision);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Site permission rule")
                .setView(fields)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Save", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface ignored) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        String originValue = origin.getText().toString().trim();
                        String permissionValue = permission.getText().toString().trim().toLowerCase();
                        String decisionValue = decision.getText().toString().trim().toLowerCase();
                        if (!ProfileSitePermissionStore.setDecision(ProfileSitePermissionsActivity.this,
                                ProfileCatalogStore.getActiveProfileId(ProfileSitePermissionsActivity.this),
                                originValue, permissionValue, decisionValue)) {
                            Toast.makeText(ProfileSitePermissionsActivity.this,
                                    "Invalid origin, permission, or decision", Toast.LENGTH_LONG).show();
                            return;
                        }
                        dialog.dismiss();
                        render();
                    }
                });
            }
        });
        dialog.show();
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
