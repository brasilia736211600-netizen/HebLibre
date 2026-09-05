package de.baumann.browser.unit;

import android.content.Context;
import android.webkit.WebView;

import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;

/**
 * Small runtime seam for binding a WebView to the active application profile.
 *
 * The default profile intentionally keeps the legacy WebView behavior. A
 * non-default profile is bound only when the installed WebView supports
 * AndroidX multi-profile; otherwise the caller continues with the default
 * WebView profile and must not claim isolation.
 */
public final class WebViewProfileBinder {

    private WebViewProfileBinder() {
    }

    public static boolean bindActiveProfile(Context context, WebView webView) {
        if (context == null || webView == null) {
            return false;
        }

        String profileId = ProfileCatalogStore.getActiveProfileId(context);
        boolean supported = WebViewFeature.isFeatureSupported(WebViewFeature.MULTI_PROFILE);
        if (!WebViewProfileBindingPolicy.shouldBind(profileId, supported)) {
            return false;
        }

        WebViewCompat.setProfile(webView, ProfileIdentity.normalize(profileId));
        return true;
    }
}
