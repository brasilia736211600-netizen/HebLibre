package de.baumann.browser.activity;

import android.app.Activity;
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
import java.util.List;

import de.baumann.browser.database.Record;
import de.baumann.browser.database.RecordAction;
import de.baumann.browser.unit.ProfileCatalogPolicy;
import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileMetadata;
import de.baumann.browser.unit.ProfileSessionStore;
import de.baumann.browser.unit.ProfileTransferCodec;

/** Local profile export/import using Android's document picker. */
public class ProfileTransferActivity extends AppCompatActivity {
    private static final int CREATE_DOCUMENT = 41;
    private static final int OPEN_DOCUMENT = 42;
    private String pendingExportPassword;

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
        plain.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { beginExport(false); }
        });
        root.addView(plain);

        Button encrypted = new Button(this);
        encrypted.setText("Export encrypted profile");
        encrypted.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { promptExportPassword(); }
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

    private void beginExport(boolean encrypted) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, ProfileCatalogStore.getActiveProfileId(this)
                + (encrypted ? ".heblibre" : ".heblibre.txt"));
        startActivityForResult(intent, CREATE_DOCUMENT);
    }

    private void promptExportPassword() {
        final EditText password = passwordField();
        new AlertDialog.Builder(this)
                .setTitle("Encryption password")
                .setView(password)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Continue", null)
                .setOnDismissListener(null)
                .show();
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Encryption password")
                .setView(passwordField())
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Continue", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            EditText field = (EditText) dialog.findViewById(0);
            // The second dialog is replaced below; retained only to keep Android 5.x compatibility.
            dialog.dismiss();
        }));
        dialog.show();
    }

    private EditText passwordField() {
        EditText password = new EditText(this);
        password.setId(android.R.id.text1);
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
            return;
        }
        try {
            if (requestCode == CREATE_DOCUMENT) {
                writeExport(data.getData());
            } else if (requestCode == OPEN_DOCUMENT) {
                String content = readDocument(data.getData());
                handleImport(content);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() == null ? "Transfer failed" : e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void writeExport(Uri destination) throws Exception {
        String activeId = ProfileCatalogStore.getActiveProfileId(this);
        ProfileMetadata metadata = ProfileCatalogStore.get(this, activeId);
        if (metadata == null) {
            throw new IllegalStateException("Active profile metadata is missing");
        }
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
        String content = pendingExportPassword == null
                ? ProfileTransferCodec.encodePlain(metadata, history, bookmarks, tabs)
                : ProfileTransferCodec.encodeEncrypted(metadata, history, bookmarks, tabs, pendingExportPassword);
        try (OutputStreamWriter writer = new OutputStreamWriter(
                getContentResolver().openOutputStream(destination), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
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
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Import password")
                .setView(password)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton("Import", null)
                .create();
        dialog.setOnShowListener(d -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                importDecoded(ProfileTransferCodec.decode(content, password.getText().toString()));
                dialog.dismiss();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }));
        dialog.show();
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
        getPreferences(MODE_PRIVATE).edit().clear().apply();
        Toast.makeText(this, "Profile imported. Restart the browser to apply it.", Toast.LENGTH_LONG).show();
    }

    private String readDocument(Uri source) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getContentResolver().openInputStream(source), StandardCharsets.UTF_8))) {
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append('\n');
            }
            return result.toString();
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
