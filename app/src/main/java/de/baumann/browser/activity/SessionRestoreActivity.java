package de.baumann.browser.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.List;

import de.baumann.browser.database.Record;
import de.baumann.browser.unit.ProfileProxyController;
import de.baumann.browser.unit.ProfileSessionStore;

/** Launcher trampoline that configures the active profile before restoring its tab set. */
public class SessionRestoreActivity extends Activity {

    private static final long RESTORE_STEP_DELAY_MS = 250L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (moveExistingBrowserTaskToFront()) {
            finish();
            return;
        }
        ProfileProxyController.applyActiveProfileProxy(this, new Runnable() {
            @Override public void run() { restoreOrLaunch(); }
        });
    }

    private void restoreOrLaunch() {
        List<Record> tabs = ProfileSessionStore.load(this);
        if (tabs.isEmpty()) {
            launchBrowser(new Intent(Intent.ACTION_MAIN));
            finish();
            return;
        }

        Handler handler = new Handler(Looper.getMainLooper());
        for (int i = 0; i < tabs.size(); i++) {
            final Record tab = tabs.get(i);
            final boolean last = i == tabs.size() - 1;
            handler.postDelayed(new Runnable() {
                @Override public void run() {
                    Intent view = new Intent(Intent.ACTION_VIEW, Uri.parse(tab.getURL()));
                    launchBrowser(view);
                    if (last) finish();
                }
            }, i * RESTORE_STEP_DELAY_MS);
        }
    }

    private boolean moveExistingBrowserTaskToFront() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (manager == null) return false;
        ComponentName browser = new ComponentName(this, BrowserActivity.class);
        List<ActivityManager.AppTask> tasks = manager.getAppTasks();
        for (ActivityManager.AppTask task : tasks) {
            ActivityManager.RecentTaskInfo info = task.getTaskInfo();
            if (info == null) continue;
            if (browser.equals(info.topActivity) || browser.equals(info.baseActivity)) {
                task.moveToFront();
                return true;
            }
        }
        return false;
    }

    private void launchBrowser(Intent intent) {
        intent.setClass(this, BrowserActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
