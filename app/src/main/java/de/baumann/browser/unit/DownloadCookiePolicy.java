package de.baumann.browser.unit;

/** Controls whether WebView cookies are forwarded to downloads. */
public final class DownloadCookiePolicy {
    public static final boolean DEFAULT_ENABLED = true;

    private DownloadCookiePolicy() {
    }

    public static boolean shouldSendCookies(boolean enabled) {
        return enabled;
    }
}
