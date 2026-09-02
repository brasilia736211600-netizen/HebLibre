package de.baumann.browser.unit;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * TDD contract for browser geolocation permission handling.
 */
public class GeolocationPermissionPolicyTest {

    @Test
    public void disabledDoesNotGrantGeolocation() {
        assertFalse(GeolocationPermissionPolicy.shouldGrant(false));
    }

    @Test
    public void enabledAllowsGeolocation() {
        assertTrue(GeolocationPermissionPolicy.shouldGrant(true));
    }
}
