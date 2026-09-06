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
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     WebChromeClient.FileChooserParams fileChooserParams) {
        ninjaWebView.getBrowserController().showFileChooser(filePathCallback);
        return true;
    }

    @Override
    public void onPermissionRequest(final PermissionRequest request) {
        String profileId = ProfileCatalogStore.getActiveProfileId(ninjaWebView.getContext());
        String siteDecision = ProfileSitePermissionStore.getDecision(
                ninjaWebView.getContext(), profileId, request.getOrigin().toString(),
                ProfileSitePermissionPolicy.PERMISSION_MEDIA);
        if (ProfileSitePermissionPolicy.DECISION_DENY.equals(siteDecision)) {
            request.deny();
            return;
        }

        SharedPreferences legacyPreferences = PreferenceManager.getDefaultSharedPreferences(
                ninjaWebView.getContext());
        java.util.Map<String, String> profileSettings =
                ProfilePreferencesStore.snapshot(ninjaWebView.getContext(), profileId);
        String encodedBlock = profileSettings.get("block_media_permissions");
        boolean blockMedia = encodedBlock == null
                ? legacyPreferences.getBoolean("block_media_permissions", true)
                : "b:true".equals(encodedBlock);

        if (blockMedia) {
            for (String resource : request.getResources()) {
                if (WebRtcPermissionPolicy.shouldBlock(true, resource)) {
                    request.deny();
                    return;
                }
            }
        }
        // A profile/site 'allow' never bypasses the browser's global media policy
        // or Android runtime permissions; it only avoids an explicit site-level deny.
        super.onPermissionRequest(request);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin,
                                                     GeolocationPermissions.Callback callback) {
        String profileId = ProfileCatalogStore.getActiveProfileId(ninjaWebView.getContext());
        String siteDecision = ProfileSitePermissionStore.getDecision(
                ninjaWebView.getContext(), profileId, origin,
                ProfileSitePermissionPolicy.PERMISSION_GEOLOCATION);
        if (ProfileSitePermissionPolicy.DECISION_DENY.equals(siteDecision)) {
            callback.invoke(origin, false, false);
            return;
        }

        SharedPreferences legacyPreferences = PreferenceManager.getDefaultSharedPreferences(
                ninjaWebView.getContext());
        java.util.Map<String, String> profileSettings =
                ProfilePreferencesStore.snapshot(ninjaWebView.getContext(), profileId);
        String encodedLocation = profileSettings.get("sp_location");
        boolean enabled = encodedLocation == null
                ? legacyPreferences.getBoolean(
                        ninjaWebView.getContext().getString(R.string.sp_location), false)
                : "b:true".equals(encodedLocation);
        if (!GeolocationPermissionPolicy.shouldGrant(enabled)) {
            callback.invoke(origin, false, false);
            return;
        }
        Activity activity = (Activity) ninjaWebView.getContext();
        HelperUnit.grantPermissionsLoc(activity);
        callback.invoke(origin, true, false);
        super.onGeolocationPermissionsShowPrompt(origin, callback);
    }
}
