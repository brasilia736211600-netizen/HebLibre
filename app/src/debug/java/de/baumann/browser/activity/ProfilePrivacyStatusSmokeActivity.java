package de.baumann.browser.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/** Debug-only launcher used by Runtime Smoke to verify the production privacy status screen can open. */
public class ProfilePrivacyStatusSmokeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, ProfilePrivacyStatusActivity.class));
        finish();
    }
}
