package de.baumann.browser.unit;

/**
 * Pure policy for deciding whether an application profile should be bound to a WebView.
 */
public final class WebViewProfileBindingPolicy {

    private WebViewProfileBindingPolicy() {
    }

    public static boolean shouldBind(String profileId, boolean multiProfileSupported) {
        String normalized = ProfileIdentity.normalize(profileId);
        return multiProfileSupported
                && !ProfileIdentity.DEFAULT_PROFILE_ID.equals(normalized);
    }
}
