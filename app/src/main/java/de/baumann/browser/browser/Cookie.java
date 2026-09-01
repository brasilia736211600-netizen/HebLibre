package de.baumann.browser.browser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import de.baumann.browser.database.RecordAction;
import de.baumann.browser.unit.ProfileScopedWhitelist;
import de.baumann.browser.unit.RecordUnit;

public class Cookie {
    private static final String FILE = "cookieHosts.txt";
    private static final Set<String> hostsCookie = new HashSet<>();
    private static final ProfileScopedWhitelist whitelists = new ProfileScopedWhitelist();
    @SuppressLint("ConstantLocale")
    private static final Locale locale = Locale.getDefault();

    private static void loadHosts(final Context context) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                AssetManager manager = context.getAssets();
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(manager.open(FILE)));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        hostsCookie.add(line.toLowerCase(locale));
                    }
                } catch (IOException i) {
                    Log.w("browser", "Error loading hosts");
                }
            }
        });
        thread.start();
    }

    private synchronized static void loadDomains(Context context, List<String> whitelistCookie, String profileId) {
        RecordAction action = new RecordAction(context);
        action.open(false);
        whitelistCookie.clear();
        whitelistCookie.addAll(action.listDomains(RecordUnit.TABLE_COOKIE, profileId));
        action.close();
    }

    private final Context context;
    private final List<String> whitelistCookie;
    private final String profileId;

    public Cookie(Context context) {
        this(context, ProfileScopedWhitelist.DEFAULT_PROFILE);
    }

    public Cookie(Context context, String profileId) {
        this.context = context;
        this.profileId = (profileId != null) ? profileId : ProfileScopedWhitelist.DEFAULT_PROFILE;
        this.whitelistCookie = whitelists.forProfile(this.profileId);

        if (hostsCookie.isEmpty()) {
            loadHosts(context);
        }
        loadDomains(context, whitelistCookie, this.profileId);
    }

    public boolean isWhite(String url) {
        return de.baumann.browser.unit.UrlMatcher.containsAnyDomain(whitelistCookie, url);
    }

    public synchronized void addDomain(String domain) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.addDomain(domain, RecordUnit.TABLE_COOKIE, profileId);
        action.close();
        whitelistCookie.add(domain);
    }

    public synchronized void removeDomain(String domain) {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.deleteDomain(domain, RecordUnit.TABLE_COOKIE, profileId);
        action.close();
        whitelistCookie.remove(domain);
    }

    public synchronized void clearDomains() {
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearTable(RecordUnit.TABLE_COOKIE, profileId);
        action.close();
        whitelistCookie.clear();
    }
}
