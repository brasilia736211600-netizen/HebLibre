package de.baumann.browser.unit;

/**
 * Local desktop-mode user-agent policy.
 *
 * Desktop mode deliberately uses one stable desktop user-agent. When the
 * mode is disabled, an explicitly configured custom user-agent wins; an
 * otherwise empty custom value falls back to the WebView default.
 */
public final class DesktopModePolicy {

    public static final String DESKTOP_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
                    + "AppleWebKit/537.36 (KHTML, like Gecko) "
                    + "Chrome/131.0.0.0 Safari/537.36";

    private DesktopModePolicy() {
        // Utility class.
    }

    public static String resolve(boolean desktopEnabled, String customUserAgent,
                                 String defaultUserAgent) {
        if (desktopEnabled) {
            return DESKTOP_USER_AGENT;
        }
        if (customUserAgent != null && !customUserAgent.trim().isEmpty()) {
            return customUserAgent;
        }
        return defaultUserAgent;
    }
}
