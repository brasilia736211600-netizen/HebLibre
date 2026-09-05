package de.baumann.browser.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import de.baumann.browser.R;
import de.baumann.browser.database.Record;
import de.baumann.browser.database.RecordAction;
import de.baumann.browser.unit.ProfileCatalogPolicy;
import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileIdentity;
import de.baumann.browser.unit.ProfileMetadata;
import de.baumann.browser.unit.ProfileSessionStore;
import de.baumann.browser.unit.ProfileTransferCodec;

/** Local profile export/import using Android's document picker. */
public class ProfileTransferActivity extends AppCompatActivity {
    private static final int CREATE_DOCUMENT = 41;
    private static final int OPEN_DOCUMENT = 42;
    private boolean encryptExport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Profile transfer");

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(16), dp(16), dp(16));

        TextView note = new TextView(this);
        note.setText("Exports contain profile metadata plus app-owned history, bookmarks, and saved tab URLs. WebView-internal cookies, storage, and login secrets are not exported.");
        root.addView(note);

        Button plain = new Button(this);
        plain.setText("Export plain profile");
        plain.setOnClickListener(v -> beginExport(false));
        root.addView(plain);

        Button encrypted = new Button(this);
        encrypted.setText("Export encrypted profile");
        encrypted.setOnClickListener(v -> promptPassword(true, null));
        root.addView(encrypted);

        Button importButton = new Button(this);
        importButton.setText("Import profile");
        importButton.setOnClickListener(v -> beginImport());
        root.addView(importButton);

        setContentView(root);
    }

    private void beginExport(boolean encrypted) {
        encryptExport = encrypted;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, ProfileCatalogStore.getActiveProfileId(this)
                + (encrypted ? ".heblibre" : ".heblibre.txt"));
        startActivityForResult(intent, CREATE_DOCUMENT);
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
            return;
        }
        try {
            if (requestCode == CREATE_DOCUMENT) {
                writeExport(data.getData().toString(), data);
            } else if (requestCode == OPEN_DOCUMENT) {
                String content;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        getContentResolver().openInputStream(data.getData()), StandardCharsets.UTF_8))) {
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line).append('\n');
                    }
                    content = result.toString();
                }
                handleImport(content);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() == null ? "Transfer failed" : e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void writeExport(String ignored, Intent data) throws Exception {
        String activeId = ProfileCatalogStore.getActiveProfileId(this);
        ProfileMetadata metadata = ProfileCatalogStore.get(this, activeId);
        if (metadata == null) {
            throw new IllegalStateException("Active profile metadata is missing");
        }

        RecordAction action = new RecordAction(this);
        action.open(false);
        List<Record> history;
        List<Record> bookmarks;
        List<Record> tabs;
        try {
            history = action.listHistory();
            bookmarks = action.listBookmark(this, false, 0L);
            tabs = ProfileSessionStore.load(this);
        } finally {
            action.close();
        }

        String content;
        if (encryptExport) {
            promptPassword(false, () -> {
                // Export is restarted from the password callback.
            });
            return;
        }
        content = ProfileTransferCodec.encodePlain(metadata, history, bookmarks, tabs);
        try (OutputStreamWriter writer = new OutputStreamWriter(
                getContentResolver().openOutputStream(data.getData()), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
        Toast.makeText(this, "Profile exported", Toast.LENGTH_SHORT).show();
    }

    private void promptPassword(boolean export, Runnable ignored) {
        final EditText password = new EditText(this);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setHint("At least 8 characters");
        new AlertDialog.Builder(this)
                .setTitle(export ? "Encryption password" : "Export encryption password")
                .setView(password)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Continue", (dialog, which) -> {
                    String value = password.getText().toString();
                    try {
                        if (value.length() < 8) {
                            throw new IllegalArgumentException("Password must contain at least 8 characters");
                        }
                        if (export) {
                            exportEncryptedWithPassword(value);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).show();
    }

    private void exportEncryptedWithPassword(String password) throws Exception {
        encryptExport = true;
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, ProfileCatalogStore.getActiveProfileId(this) + ".heblibre");
        startActivityForResult(intent, CREATE_DOCUMENT);
        getPreferences(MODE_PRIVATE).edit().putString("pending_export_password", password).apply();
    }

    private void handleImport(String content) throws Exception {
        if (content.startsWith("HEBLIBRE_PROFILE_V1_AES_GCM")) {
            promptImportPassword(content);
            return;
        }
        importDecoded(ProfileTransferCodec.decode(content, null));
    }

    private void promptImportPassword(final String content) {
        final EditText password = new EditText(this);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        new AlertDialog.Builder(this)
                .setTitle("Import password")
                .setView(password)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Import", (dialog, which) -> {
                    try {
                        importDecoded(ProfileTransferCodec.decode(content, password.getText().toString()));
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }).show();
    }

    private void importDecoded(ProfileTransferCodec.TransferPackage transfer) {
        ProfileMetadata metadata = transfer.getMetadata();
        if (!ProfileCatalogPolicy.isValidUserProfileId(metadata.getId())) {
            throw new IllegalArgumentException("Imported profile ID is invalid");
        }
        if (ProfileCatalogStore.get(this, metadata.getId()) != null) {
            throw new IllegalArgumentException("A profile with this ID already exists");
        }
        if (!ProfileCatalogStore.save(this, metadata)) {
            throw new IllegalStateException("Unable to save imported profile");
        }

        ProfileCatalogStore.setActiveProfileId(this, metadata.getId());
        RecordAction action = new RecordAction(this);
        action.open(true);
        try {
            for (Record record : transfer.getHistory()) {
                action.addHistory(record);
            }
            for (Record record : transfer.getBookmarks()) {
                action.addBookmark(record, metadata.getId());
            }
            for (Record record : transfer.getTabs()) {
                action.addTab(record, metadata.getId());
            }
        } finally {
            action.close();
        }
        getSharedPreferences("").edit().apply();
        Toast.makeText(this, "Profile imported. Restart the browser to apply it.", Toast.LENGTH_LONG).show();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
