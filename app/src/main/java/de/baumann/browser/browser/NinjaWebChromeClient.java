package de.baumann.browser.browser;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.webkit.*;

import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;

import de.baumann.browser.R;
import de.baumann.browser.unit.GeolocationPermissionPolicy;
import de.baumann.browser.unit.HelperUnit;
import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfilePreferencesStore;
import de.baumann.browser.unit.ProfileSitePermissionPolicy;
import de.baumann.browser.unit.ProfileSitePermissionStore;
import de.baumann.browser.unit.WebRtcPermissionPolicy;
import de.baumann.browser.view.NinjaWebView;

public class NinjaWebChromeClient extends WebChromeClient {

    private final NinjaWebView ninjaWebView;

    public NinjaWebChromeClient(NinjaWebView ninjaWebView) {
        super();
        this.ninjaWebView = ninjaWebView;
    }

    @Override
    public void onProgressChanged(WebView view, int progress) {
        super.onProgressChanged(view, progress);
        ninjaWebView.update(progress);
        if (view.getTitle().isEmpty()) ninjaWebView.update(view.getUrl());
        else ninjaWebView.update(view.getTitle());
    }

    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        ninjaWebView.getBrowserController().onShowCustomView(view, callback);
        super.onShowCustomView(view, callback);
    }

    @Override
    public void onHideCustomView() {
        ninjaWebView.getBrowserController().onHideCustomView();
        super.onHideCustomView();
    }

    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        ninjaWebView.getBrowserController().showFileChooser(filePathCallback);
        return true;
    }

    @Override
    public void onPermissionRequest(final PermissionRequest request) {
        String profileId = ProfileCatalogStore.getActiveProfileId(ninjaWebView.getContext());
        String decision = ProfileSitePermissionStore.getDecision(
                ninjaWebView.getContext(), profileId, request.getOrigin().toString(),
                ProfileSitePermissionPolicy.PERMISSION_MEDIA);
        if (ProfileSitePermissionPolicy.DECISION_DENY.equals(decision)) {
            request.deny();
            return;
        }

        boolean globalBlock = ProfilePreferencesStore.snapshot(ninjaWebView.getContext(), profileId)
                .containsKey("block_media_permissions")
                && "b:true".equals(ProfilePreferencesStore.snapshot(ninjaWebView.getContext(), profileId)
                .get("block_media_permissions"));
        if (globalBlock && !ProfileSitePermissionPolicy.DECISION_ALLOW.equals(decision)) {
            request.deny();
            return;
        }
        super.onPermissionRequest(request);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        String profileId = ProfileCatalogStore.getActiveProfileId(ninjaWebView.getContext());
        String siteDecision = ProfileSitePermissionStore.getDecision(
                ninjaWebView.getContext(), profileId, origin,
                ProfileSitePermissionPolicy.PERMISSION_GEOLOCATION);
        if (ProfileSitePermissionPolicy.DECISION_DENY.equals(siteDecision)) {
            callback.invoke(origin, false, false);
            return;
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ninjaWebView.getContext());
        MapBackedBooleanReader settings = new MapBackedBooleanReader(ProfilePreferencesStore.snapshot(ninjaWebView.getContext(), profileId));
        boolean enabled = settings.getBoolean("sp_location", preferences.getBoolean(
                ninjaWebView.getContext().getString(R.string.sp_location), false));
        if (!GeolocationPermissionPolicy.shouldGrant(enabled)
                && !ProfileSitePermissionPolicy.DECISION_ALLOW.equals(siteDecision)) {
            callback.invoke(origin, false, false);
            return;
        }
        Activity activity = (Activity) ninjaWebView.getContext();
        HelperUnit.grantPermissionsLoc(activity);
        callback.invoke(origin, true, false);
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    /** Tiny adapter to avoid exposing profile preference encoding outside the existing store. */
    private static final class MapBackedBooleanReader {
        private final java.util.Map<String, String> values;
        MapBackedBooleanReader(java.util.Map<String, String> values) { this.values = values; }
        boolean getBoolean(String key, boolean fallback) {
            String value = values.get(key);
            return value == null ? fallback : "b:true".equals(value);
        }
    }
}
