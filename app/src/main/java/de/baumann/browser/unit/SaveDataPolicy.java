package de.baumann.browser.unit;

/**
 * Local policy for the HTTP Save-Data request signal.
 */
public final class SaveDataPolicy {

    public static final boolean DEFAULT_ENABLED = true;

    private SaveDataPolicy() {
        // Utility class.
    }

    public static boolean isEnabled(boolean preferenceValue) {
        return preferenceValue;
    }
}
