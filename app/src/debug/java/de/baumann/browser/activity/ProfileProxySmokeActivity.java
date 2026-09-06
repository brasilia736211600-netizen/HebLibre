package de.baumann.browser.activity;

import android.app.Activity;
import android.os.Bundle;

import java.util.Collections;

import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfilePreferencesStore;

/** Debug-only harness that configures the active profile to use the emulator host proxy. */
public class ProfileProxySmokeActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String profileId = ProfileCatalogStore.getActiveProfileId(this);
        ProfilePreferencesStore.restore(this, profileId, Collections.singletonMap(
                "proxy_url", "s:http://10.0.2.2:18080"));
        ProfilePreferencesStore.restore(this, profileId, Collections.singletonMap(
                "proxy_bypass", "s:"));
        finish();
    }
}
