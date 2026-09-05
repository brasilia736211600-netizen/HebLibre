package de.baumann.browser.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ProfileManagerSmokeActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, ProfileManagerActivity.class));
        finish();
    }
}
