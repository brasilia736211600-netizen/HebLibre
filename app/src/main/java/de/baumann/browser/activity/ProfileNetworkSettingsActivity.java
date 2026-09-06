package de.baumann.browser.activity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Map;

import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileConsistencyPolicy;
import de.baumann.browser.unit.ProfileLanguagePolicy;
import de.baumann.browser.unit.ProfilePreferencesStore;
import de.baumann.browser.unit.ProfileProxyPolicy;
import de.baumann.browser.unit.ProfileUserAgentPolicy;

/** Profile-local network and browser-compatibility settings. */
public class ProfileNetworkSettingsActivity extends AppCompatActivity {
    private EditText proxy;
    private EditText bypass;
    private EditText uaPreset;
    private EditText customUa;
    private EditText language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Network & identity");

        String profileId = ProfileCatalogStore.getActiveProfileId(this);
        Map<String, String> values = ProfilePreferencesStore.snapshot(this, profileId);

        LinearLayout fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.VERTICAL);
        fields.setPadding(dp(12), dp(8), dp(12), dp(8));

        TextView active = new TextView(this);
        active.setText("Active profile: " + profileId);
        active.setTextSize(16);
        fields.addView(active);

        TextView notice = new TextView(this);
        notice.setText("Proxy is a real WebView network override. It is applied before browser startup and therefore takes effect after restarting the browser process. Credentials embedded in proxy URLs are intentionally rejected by the validation policy.");
        notice.setPadding(0, dp(8), 0, dp(12));
        fields.addView(notice);

        proxy = field("Proxy URL (http://host:port, https://host:port, socks5://host:port)", value(values, "proxy_url"));
        fields.addView(proxy);
        bypass = field("Proxy bypass rules (comma-separated)", value(values, "proxy_bypass"));
        fields.addView(bypass);
        uaPreset = field("UA preset id (for example android_chrome_152)", value(values, "ua_preset"));
        fields.addView(uaPreset);
        customUa = field("Custom User-Agent (optional; overrides preset)", value(values, "userAgent_custom"));
        customUa.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        fields.addView(customUa);
        language = field("Preferred language (for example ar-YE)", value(values, "preferred_language"));
        fields.addView(language);

        TextView timezone = new TextView(this);
        timezone.setText("Timezone: system timezone. Automatic proxy-location timezone is not enabled because Android WebView exposes no reliable profile-local timezone override API.");
        timezone.setPadding(0, dp(8), 0, dp(12));
        fields.addView(timezone);

        Button presets = new Button(this);
        presets.setText("Show available UA presets");
        presets.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                StringBuilder text = new StringBuilder();
                for (String id : ProfileUserAgentPolicy.presets().keySet()) {
                    if (text.length() > 0) text.append("\n");
                    text.append(id);
                }
                Toast.makeText(ProfileNetworkSettingsActivity.this, text.toString(), Toast.LENGTH_LONG).show();
            }
        });
        fields.addView(presets);

        Button audit = new Button(this);
        audit.setText("Run profile consistency audit");
        audit.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                List<String> issues = ProfileConsistencyPolicy.findIssues(ProfilePreferencesStore.snapshot(ProfileNetworkSettingsActivity.this, profileId));
                Toast.makeText(ProfileNetworkSettingsActivity.this,
                        issues.isEmpty() ? "Profile configuration is consistent" : "Consistency findings: " + issues.size(),
                        Toast.LENGTH_LONG).show();
            }
        });
        fields.addView(audit);

        Button save = new Button(this);
        save.setText("Save profile network settings");
        save.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) { save(profileId); }
        });
        fields.addView(save);

        ScrollView scroll = new ScrollView(this);
        scroll.addView(fields);
        setContentView(scroll);
    }

    private void save(String profileId) {
        String proxyValue = proxy.getText().toString().trim();
        String preset = uaPreset.getText().toString().trim().toLowerCase(java.util.Locale.ROOT);
        String custom = ProfileUserAgentPolicy.normalizeCustom(customUa.getText().toString());
        String preferredLanguage = language.getText().toString().trim();
        if (!proxyValue.isEmpty() && !ProfileProxyPolicy.isValid(proxyValue)) {
            Toast.makeText(this, "Invalid proxy URL. Use http(s)://host:port or socks5://host:port", Toast.LENGTH_LONG).show();
            return;
        }
        if (!preset.isEmpty() && !ProfileUserAgentPolicy.isValidPreset(preset)) {
            Toast.makeText(this, "Unknown UA preset", Toast.LENGTH_LONG).show();
            return;
        }
        if (!customUa.getText().toString().trim().isEmpty() && custom.isEmpty()) {
            Toast.makeText(this, "Custom User-Agent is too long", Toast.LENGTH_LONG).show();
            return;
        }
        if (!preferredLanguage.isEmpty() && !ProfileLanguagePolicy.isValid(preferredLanguage)) {
            Toast.makeText(this, "Use a valid language tag such as ar-YE or en-US", Toast.LENGTH_LONG).show();
            return;
        }

        String effectiveUa = custom.isEmpty() ? ProfileUserAgentPolicy.resolvePreset(preset) : custom;
        Map<String, String> values = new java.util.LinkedHashMap<>();
        values.put("proxy_url", "s:" + ProfileProxyPolicy.normalize(proxyValue));
        values.put("proxy_bypass", "s:" + ProfileProxyPolicy.normalizeBypassRules(bypass.getText().toString()));
        values.put("ua_preset", "s:" + preset);
        values.put("userAgent_custom", "s:" + custom);
        values.put("userAgent", "s:" + effectiveUa);
        values.put("preferred_language", "s:" + ProfileLanguagePolicy.normalize(preferredLanguage));
        ProfilePreferencesStore.restore(this, profileId, values);
        Toast.makeText(this, "Profile settings saved. Restart the browser to apply the proxy.", Toast.LENGTH_LONG).show();
    }

    private EditText field(String hint, String value) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setText(value);
        input.setSingleLine(false);
        input.setPadding(dp(8), dp(8), dp(8), dp(8));
        return input;
    }

    private String value(Map<String, String> values, String key) {
        String encoded = values.get(key);
        return encoded != null && encoded.startsWith("s:") ? encoded.substring(2) : "";
    }

    private int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
}
