package de.baumann.browser.unit;

public final class DownloadCookiePolicy {
    public static final boolean DEFAULT_ENABLED = true;

    private DownloadCookiePolicy() { }

    public static boolean isEnabled(boolean preferenceValue) {
        return preferenceValue;
    }
}
