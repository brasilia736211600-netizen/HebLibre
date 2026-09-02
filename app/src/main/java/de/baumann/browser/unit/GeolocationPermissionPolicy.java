package de.baumann.browser.unit;

/**
 * Small policy seam for browser geolocation permissions.
 */
public final class GeolocationPermissionPolicy {

    private GeolocationPermissionPolicy() {
        // Utility class.
    }

    public static boolean shouldGrant(boolean enabled) {
        return enabled;
    }
}
