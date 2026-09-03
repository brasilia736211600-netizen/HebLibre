package de.baumann.browser.unit;

/**
 * Third-party cookie privacy policy for the WebView.
 */
public final class ThirdPartyCookiePolicy {

    public static final boolean DEFAULT_ENABLED = true;

    private ThirdPartyCookiePolicy() {
        // Utility class.
    }

    public static boolean acceptThirdPartyCookies(boolean blockThirdPartyCookies) {
        return !blockThirdPartyCookies;
    }
}
