package de.baumann.browser.unit;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Characterizes the profile-aware whitelist storage introduced in P1 step 3.
 * Locks in the two contractual guarantees this class exists for:
 *  - same profile id => same List instance (live visibility of changes
 *    among instances that share a profile, matching pre-existing behavior)
 *  - different profile id => separate List instance (no cross-profile
 *    leakage of in-memory whitelist state)
 *
 * Pure Java, no Android dependency; does not exercise AdBlock/Javascript/
 * Cookie/Remote directly since their constructors touch RecordAction/DB
 * and asset loading, which require Android runtime.
 */
public class ProfileScopedWhitelistTest {

    @Test
    public void sameProfileId_returnsSameListInstance_acrossCalls() {
        ProfileScopedWhitelist store = new ProfileScopedWhitelist();

        List<String> first = store.forProfile("profileA");
        first.add("example.com");
        List<String> second = store.forProfile("profileA");

        assertSame(first, second);
        assertTrue(second.contains("example.com"));
    }

    @Test
    public void differentProfileIds_doNotShareState() {
        ProfileScopedWhitelist store = new ProfileScopedWhitelist();

        store.forProfile("profileA").add("example.com");
        List<String> profileBList = store.forProfile("profileB");

        assertFalse(profileBList.contains("example.com"));
    }

    @Test
    public void nullProfileId_fallsBackToDefaultProfile() {
        ProfileScopedWhitelist store = new ProfileScopedWhitelist();

        List<String> viaNull = store.forProfile(null);
        List<String> viaDefault = store.forProfile(ProfileScopedWhitelist.DEFAULT_PROFILE);

        assertSame(viaNull, viaDefault);
    }
}
