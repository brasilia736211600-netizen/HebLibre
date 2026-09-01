package de.baumann.browser.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Minimal profile-aware in-memory whitelist registry.
 *
 * Introduced for P1 step 3 (profile / identity isolation): AdBlock,
 * Javascript, Cookie, and Remote each previously kept their whitelist as a
 * single process-wide static List, shared by every instance regardless of
 * intended "profile". That sharing is intentional and relied upon *within*
 * a profile (a change made on the whitelist management screen must be
 * instantly visible to every already-open tab of the same profile), but it
 * also means there is currently no way to keep two different profiles'
 * whitelists from leaking into each other.
 *
 * This class provides the smallest mechanism that preserves the first
 * property while fixing the second: one List instance per profile id,
 * shared by every caller that asks for the same id, isolated from callers
 * that ask for a different id.
 *
 * Scope: in-memory only. It does not touch persisted storage (the
 * whitelist domain tables in Ninja4.db remain a single unpartitioned
 * table, and RecordAction is not modified). It does not touch
 * CookieManager, WebView data directories, SharedPreferences, or any
 * other subsystem - those remain out of scope for this step.
 */
public class ProfileScopedWhitelist {

    public static final String DEFAULT_PROFILE = "default";

    private final Map<String, List<String>> whitelistsByProfile = new HashMap<>();

    /**
     * Returns the whitelist List for the given profile id, creating it on
     * first access. Repeated calls with the same id (including across
     * different ProfileScopedWhitelist-owning object instances that share
     * this registry) return the same List instance. A null id is treated
     * as {@link #DEFAULT_PROFILE}, preserving current single-profile
     * behavior for all existing call sites that do not pass a profile id.
     */
    public synchronized List<String> forProfile(String profileId) {
        String key = (profileId != null) ? profileId : DEFAULT_PROFILE;
        List<String> list = whitelistsByProfile.get(key);
        if (list == null) {
            list = new ArrayList<>();
            whitelistsByProfile.put(key, list);
        }
        return list;
    }
}
