package de.baumann.browser.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/** Debug-only launcher used by Runtime Smoke to verify the production transfer screen can open. */
public class ProfileTransferSmokeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, ProfileTransferActivity.class));
        finish();
    }
}
