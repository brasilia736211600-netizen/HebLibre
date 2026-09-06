package de.baumann.browser.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import de.baumann.browser.unit.ProfileCatalogStore;

/** Read-only local fingerprint exposure diagnostics; no signal mutation or network access. */
public class FingerprintAuditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Fingerprint exposure audit");

        WebView webView = new WebView(this);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(false);
        webView.getSettings().setAllowUniversalAccessFromFileURLs(false);
        webView.loadUrl("file:///android_asset/fingerprint_audit.html");
        setContentView(webView);

        setTitle("Fingerprint audit — " + ProfileCatalogStore.getActiveProfileId(this));
    }
}
