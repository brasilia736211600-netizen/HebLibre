package de.baumann.browser.activity;

import android.app.Activity;
import android.os.Bundle;

import java.util.Collections;

import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfilePreferencesStore;

/** Debug-only harness for setting/clearing the emulator's local CONNECT proxy. */
public class ProfileProxySmokeActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String profileId = ProfileCatalogStore.getActiveProfileId(this);
        boolean clear = "clear".equals(getIntent().getStringExtra("mode"));
        ProfilePreferencesStore.restore(this, profileId, Collections.singletonMap(
                "proxy_url", clear ? "s:" : "s:http://10.0.2.2:18080"));
        ProfilePreferencesStore.restore(this, profileId, Collections.singletonMap(
                "proxy_bypass", "s:"));
        finish();
    }
}
