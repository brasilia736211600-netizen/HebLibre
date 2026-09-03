package de.baumann.browser.unit;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;

import de.baumann.browser.browser.AdBlock;
import de.baumann.browser.browser.Cookie;
import de.baumann.browser.browser.Javascript;
import de.baumann.browser.browser.Remote;
import de.baumann.browser.database.RecordAction;

/**
 * Profile-aware whitelist import/export entry point.
 *
 * Keeps the existing file format and table selection while ensuring transfer
 * operations use the active profile instead of always using the default one.
 */
public final class ProfileScopedWhitelistTransfer {

    private ProfileScopedWhitelistTransfer() {
    }

    private static String activeProfileId(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return ProfileIdentity.normalize(preferences.getString(
                ProfileIdentity.PREFERENCE_KEY, ProfileIdentity.DEFAULT_PROFILE_ID));
    }

    public static String exportWhitelist(Context context, int table) {
        String profileId = activeProfileId(context);
        RecordAction action = new RecordAction(context);
        List<String> domains;
        String filename;
        action.open(false);
        switch (table) {
            case 0:
                domains = action.listDomains(RecordUnit.TABLE_WHITELIST, profileId);
                filename = "export_whitelist_AdBlock.txt";
                break;
            case 1:
                domains = action.listDomains(RecordUnit.TABLE_JAVASCRIPT, profileId);
                filename = "export_whitelist_java.txt";
                break;
            case 3:
                domains = action.listDomains(RecordUnit.TABLE_REMOTE, profileId);
                filename = "export_whitelist_remote.txt";
                break;
            default:
                domains = action.listDomains(RecordUnit.TABLE_COOKIE, profileId);
                filename = "export_whitelist_cookie.txt";
                break;
        }
        action.close();

        File file = new File(context.getExternalFilesDir(null), "browser_backup//" + filename);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            for (String domain : domains) {
                writer.write(domain);
                writer.newLine();
            }
            writer.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    public static int importWhitelist(Context context, int table) {
        String profileId = activeProfileId(context);
        int count = 0;
        try {
            String filename;
            AdBlock adBlock = null;
            Javascript javascript = null;
            Cookie cookie = null;
            Remote remote = null;
            switch (table) {
                case 0:
                    adBlock = new AdBlock(context);
                    filename = "export_whitelist_AdBlock.txt";
                    break;
                case 1:
                    javascript = new Javascript(context);
                    filename = "export_whitelist_java.txt";
                    break;
                case 3:
                    remote = new Remote(context);
                    filename = "export_whitelist_remote.txt";
                    break;
                default:
                    cookie = new Cookie(context);
                    filename = "export_whitelist_cookie.txt";
                    break;
            }

            File file = new File(context.getExternalFilesDir(null), "browser_backup//" + filename);
            RecordAction action = new RecordAction(context);
            action.open(true);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                switch (table) {
                    case 0:
                        if (!action.checkDomain(line, RecordUnit.TABLE_WHITELIST, profileId)) {
                            adBlock.addDomain(line);
                            count++;
                        }
                        break;
                    case 1:
                        if (!action.checkDomain(line, RecordUnit.TABLE_JAVASCRIPT, profileId)) {
                            javascript.addDomain(line);
                            count++;
                        }
                        break;
                    case 3:
                        if (!action.checkDomain(line, RecordUnit.TABLE_REMOTE, profileId)) {
                            remote.addDomain(line);
                            count++;
                        }
                        break;
                    default:
                        if (!action.checkDomain(line, RecordUnit.TABLE_COOKIE, profileId)) {
                            cookie.addDomain(line);
                            count++;
                        }
                        break;
                }
            }
            reader.close();
            action.close();
        } catch (Exception ignored) {
        }
        return count;
    }
}
