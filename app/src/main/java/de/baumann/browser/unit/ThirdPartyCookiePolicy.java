package de.baumann.browser.unit;

/**
 * Optional third-party cookie privacy policy for the WebView.
 */
public final class ThirdPartyCookiePolicy {

    private ThirdPartyCookiePolicy() {
        // Utility class.
    }

    public static boolean acceptThirdPartyCookies(boolean blockThirdPartyCookies) {
        return !blockThirdPartyCookies;
    }
}
