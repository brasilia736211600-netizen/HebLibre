package de.baumann.browser.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.baumann.browser.R;
import de.baumann.browser.database.Record;
import de.baumann.browser.database.RecordAction;
import de.baumann.browser.unit.ProfileCatalogPolicy;
import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileMetadata;
import de.baumann.browser.unit.ProfilePreferencesStore;
import de.baumann.browser.unit.ProfileSessionStore;
import de.baumann.browser.unit.ProfileTransferCodec;

/** Local profile export/import using Android's document picker. */
public class ProfileTransferActivity extends AppCompatActivity {
    private static final int CREATE_DOCUMENT = 41;
    private static final int OPEN_DOCUMENT = 42;
    private static final String STATE_ENCRYPTED = "pending_encrypted_export";

    private boolean pendingEncryptedExport;
    private String pendingExportPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            pendingEncryptedExport = savedInstanceState.getBoolean(STATE_ENCRYPTED, false);
        }
        setTitle("Profile transfer");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));

        TextView note = new TextView(this);
        note.setText("Exports contain profile metadata, selected profile settings, app-owned history, bookmarks, and saved tab URLs. WebView-internal cookies, storage, and login secrets are not exported.");
        root.addView(note);

        Button plain = new Button(this);
        plain.setText("Export plain profile");
        plain.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { beginPlainExport(); }
        });
        root.addView(plain);

        Button encrypted = new Button(this);
        encrypted.setText("Export encrypted profile");
        encrypted.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { beginEncryptedExport(); }
        });
        root.addView(encrypted);

        Button importButton = new Button(this);
        importButton.setText("Import profile");
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { beginImport(); }
        });
        root.addView(importButton);

        setContentView(root);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_ENCRYPTED, pendingEncryptedExport);
        // Never persist the export password; it exists only in memory for the current Activity instance.
        super.onSaveInstanceState(outState);
    }

    private void beginPlainExport() {
        pendingEncryptedExport = false;
        pendingExportPassword = null;
        startDocumentCreation(false);
    }

    private void beginEncryptedExport() {
        pendingEncryptedExport = true;
        pendingExportPassword = null;
        startDocumentCreation(true);
    }

    private void startDocumentCreation(boolean encrypted) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, ProfileCatalogStore.getActiveProfileId(this)
                + (encrypted ? ".heblibre" : ".heblibre.txt"));
        startActivityForResult(intent, CREATE_DOCUMENT);
    }

    private void promptExportPassword(final Uri destination) {
        final EditText password = passwordField();
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Encryption password")
                .setView(password)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Export", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface ignored) {
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        String value = password.getText().toString();
                        if (value.length() < 8) {
                            Toast.makeText(ProfileTransferActivity.this,
                                    "Password must contain at least 8 characters", Toast.LENGTH_LONG).show();
                            return;
                        }
                        pendingExportPassword = value;
                        dialog.dismiss();
                        try {
                            writeExport(destination);
                        } catch (Exception e) {
                            pendingExportPassword = null;
                            pendingEncryptedExport = false;
                            Toast.makeText(ProfileTransferActivity.this,
                                    e.getMessage() == null ? "Transfer failed" : e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private EditText passwordField() {
        EditText password = new EditText(this);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setHint("At least 8 characters");
        return password;
    }

    private void beginImport() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, OPEN_DOCUMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK || data == null || data.getData() == null) {
            if (requestCode == CREATE_DOCUMENT) {
                pendingEncryptedExport = false;
                pendingExportPassword = null;
            }
            return;
        }
        try {
            if (requestCode == CREATE_DOCUMENT) {
                if (pendingEncryptedExport && pendingExportPassword == null) {
                    promptExportPassword(data.getData());
                } else {
                    writeExport(data.getData());
                }
            } else if (requestCode == OPEN_DOCUMENT) {
                handleImport(readDocument(data.getData()));
            }
        } catch (Exception e) {
            pendingEncryptedExport = false;
            pendingExportPassword = null;
            Toast.makeText(this, e.getMessage() == null ? "Transfer failed" : e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void writeExport(Uri destination) throws Exception {
        String activeId = ProfileCatalogStore.getActiveProfileId(this);
        ProfileMetadata metadata = ProfileCatalogStore.get(this, activeId);
        if (metadata == null) throw new IllegalStateException("Active profile metadata is missing");

        ProfilePreferencesStore.saveGlobalToProfile(this, activeId);
        Map<String, String> profilePreferences = ProfilePreferencesStore.snapshot(this, activeId);

        RecordAction action = new RecordAction(this);
        action.open(false);
        List<Record> history;
        List<Record> bookmarks;
        try {
            history = action.listHistory();
            bookmarks = action.listBookmark(this, false, 0L);
        } finally {
            action.close();
        }
        List<Record> tabs = ProfileSessionStore.load(this);
        if (pendingEncryptedExport && pendingExportPassword == null) {
            throw new IllegalStateException("Encryption password is required");
        }
        String content = pendingEncryptedExport
                ? ProfileTransferCodec.encodeEncrypted(metadata, history, bookmarks, tabs, pendingExportPassword, profilePreferences)
                : ProfileTransferCodec.encodePlain(metadata, history, bookmarks, tabs, profilePreferences);
        try (OutputStreamWriter writer = new OutputStreamWriter(
                getContentResolver().openOutputStream(destination), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
        pendingEncryptedExport = false;
        pendingExportPassword = null;
        Toast.makeText(this, "Profile exported", Toast.LENGTH_SHORT).show();
    }

    private void handleImport(String content) {
        if (content.startsWith("HEBLIBRE_PROFILE_V1_AES_GCM")) {
            promptImportPassword(content);
        } else {
            importDecoded(ProfileTransferCodec.decode(content, null));
        }
    }

    private void promptImportPassword(final String content) {
        final EditText password = passwordField();
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Import password")
                .setView(password)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Import", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface ignored) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        try {
                            importDecoded(ProfileTransferCodec.decode(content, password.getText().toString()));
                            dialog.dismiss();
                        } catch (Exception e) {
                            Toast.makeText(ProfileTransferActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        dialog.show();
    }

    private void importDecoded(final ProfileTransferCodec.TransferPackage transfer) {
        final ProfileMetadata source = transfer.getMetadata();
        if (ProfileCatalogPolicy.isValidUserProfileId(source.getId())
                && ProfileCatalogStore.get(this, source.getId()) == null) {
            importWithMetadata(transfer, source);
            return;
        }

        final EditText id = new EditText(this);
        id.setSingleLine(true);
        id.setHint("New profile ID");
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Choose imported profile ID")
                .setMessage("The exported ID is reserved or already exists. Choose a new ID for the imported profile.")
                .setView(id)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Import", null)
                .create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override public void onShow(DialogInterface ignored) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override public void onClick(View v) {
                        String profileId = id.getText().toString().trim();
                        if (!ProfileCatalogPolicy.isValidUserProfileId(profileId)
                                || ProfileCatalogStore.get(ProfileTransferActivity.this, profileId) != null) {
                            Toast.makeText(ProfileTransferActivity.this,
                                    "Choose a valid unused profile ID", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ProfileMetadata replacement = new ProfileMetadata(
                                profileId,
                                source.getName(),
                                source.getColor(),
                                source.getIcon(),
                                source.getNotes(),
                                new ArrayList<>(source.getTags()),
                                source.getGroup());
                        importWithMetadata(transfer, replacement);
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private void importWithMetadata(ProfileTransferCodec.TransferPackage transfer, ProfileMetadata metadata) {
        if (!ProfileCatalogStore.save(this, metadata)) {
            throw new IllegalStateException("Unable to save imported profile");
        }

        RecordAction action = new RecordAction(this);
        action.open(true);
        try {
            if (!action.importProfileRecords(
                    transfer.getHistory(), transfer.getBookmarks(), transfer.getTabs(), metadata.getId())) {
                throw new IllegalStateException("Unable to import profile records");
            }
            ProfilePreferencesStore.restore(this, metadata.getId(), transfer.getPreferences());
        } catch (RuntimeException e) {
            // The record transaction is already rolled back. Remove the catalog entry and
            // its profile-local settings as well so a failed import cannot leave a visible
            // empty/partial profile behind.
            ProfileCatalogStore.delete(this, metadata.getId());
            throw e;
        } finally {
            action.close();
        }

        if (!ProfileCatalogStore.setActiveProfileId(this, metadata.getId())) {
            ProfileCatalogStore.delete(this, metadata.getId());
            throw new IllegalStateException("Unable to activate imported profile");
        }
        Toast.makeText(this, "Profile imported. Restart the browser to apply it.", Toast.LENGTH_LONG).show();
    }

    private String readDocument(Uri source) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getContentResolver().openInputStream(source), StandardCharsets.UTF_8))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) result.append(line).append('\n');
            return result.toString();
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
