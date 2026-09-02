package de.baumann.browser.unit;

/**
 * Local Global Privacy Control request policy.
 *
 * The enabled signal is represented by the standard GPC header value. No
 * networking or site-specific behavior is implemented here.
 */
public final class GpcPolicy {

    public static final String HEADER_NAME = "Sec-GPC";
    public static final String HEADER_VALUE = "1";

    private GpcPolicy() {
        // Utility class.
    }

    public static String headerValue(boolean enabled) {
        return enabled ? HEADER_VALUE : null;
    }
}
