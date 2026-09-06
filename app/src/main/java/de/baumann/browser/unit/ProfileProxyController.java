package de.baumann.browser.unit;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.webkit.ProxyConfig;
import androidx.webkit.ProxyController;
import androidx.webkit.WebViewFeature;

import java.util.Map;
import java.util.concurrent.Executor;

/** Applies the active profile's real WebView proxy before BrowserActivity creates WebViews. */
public final class ProfileProxyController {
    private ProfileProxyController() { }

    public static void applyActiveProfileProxy(Context context, final Runnable afterApply) {
        if (context == null || !WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
            runOnMain(afterApply);
            return;
        }

        String profileId = ProfileCatalogStore.getActiveProfileId(context);
        ProfilePreferencesStore.initializeProfile(context, profileId);
        Map<String, String> snapshot = ProfilePreferencesStore.snapshot(context, profileId);
        String encodedProxy = snapshot.get("proxy_url");
        String proxyUrl = encodedProxy != null && encodedProxy.startsWith("s:")
                ? ProfileProxyPolicy.normalize(encodedProxy.substring(2)) : "";
        String encodedBypass = snapshot.get("proxy_bypass");
        String bypass = encodedBypass != null && encodedBypass.startsWith("s:")
                ? ProfileProxyPolicy.normalizeBypassRules(encodedBypass.substring(2)) : "";

        final Runnable callback = new Runnable() {
            @Override public void run() { runOnMain(afterApply); }
        };
        Executor executor = new Executor() {
            @Override public void execute(Runnable command) {
                new Handler(Looper.getMainLooper()).post(command);
            }
        };
        try {
            if (proxyUrl.isEmpty()) {
                ProxyController.getInstance().clearProxyOverride(executor, callback);
                return;
            }
            ProxyConfig.Builder builder = new ProxyConfig.Builder().addProxyRule(proxyUrl);
            for (String rule : bypass.split(",")) {
                if (!rule.trim().isEmpty()) builder.addBypassRule(rule.trim());
            }
            ProxyController.getInstance().setProxyOverride(builder.build(), executor, callback);
        } catch (RuntimeException ignored) {
            runOnMain(afterApply);
        }
    }

    private static void runOnMain(Runnable runnable) {
        if (runnable == null) return;
        if (Looper.myLooper() == Looper.getMainLooper()) runnable.run();
        else new Handler(Looper.getMainLooper()).post(runnable);
    }
}
