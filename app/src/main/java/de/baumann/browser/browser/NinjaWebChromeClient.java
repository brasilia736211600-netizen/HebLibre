package de.baumann.browser.browser;

import android.app.Activity;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.*;

import androidx.preference.PreferenceManager;
import android.content.SharedPreferences;

import de.baumann.browser.unit.GeolocationPermissionPolicy;
import de.baumann.browser.unit.HelperUnit;
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
        if (view.getTitle().isEmpty()) {
            ninjaWebView.update(view.getUrl());
        } else {
            ninjaWebView.update(view.getTitle());
        }
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ninjaWebView.getContext());
        if (preferences.getBoolean("block_media_permissions", true)) {
            for (String resource : request.getResources()) {
                if (WebRtcPermissionPolicy.shouldBlock(true, resource)) {
                    request.deny();
                    return;
                }
            }
        }
        super.onPermissionRequest(request);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ninjaWebView.getContext());
        boolean enabled = preferences.getBoolean("sp_location", false);
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
