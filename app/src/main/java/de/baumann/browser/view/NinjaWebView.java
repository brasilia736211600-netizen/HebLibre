package de.baumann.browser.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.view.*;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.preference.PreferenceManager;
import androidx.webkit.WebViewCompat;
import androidx.webkit.WebViewFeature;
import androidx.webkit.WebSettingsCompat;

import de.baumann.browser.R;
import de.baumann.browser.browser.*;
import de.baumann.browser.unit.*;

import java.util.HashMap;
import java.util.Objects;

public class NinjaWebView extends WebView implements AlbumController {

    private OnScrollChangeListener onScrollChangeListener;

    public NinjaWebView(Context context, android.util.AttributeSet attrs) { super(context, attrs); WebViewProfileBinder.bindActiveProfile(context, this); }
    public NinjaWebView(Context context, android.util.AttributeSet attrs, int defStyleAttr) { super(context, attrs, defStyleAttr); WebViewProfileBinder.bindActiveProfile(context, this); }

    @Override protected void onScrollChanged(int l, int t, int old_l, int old_t) { super.onScrollChanged(l,t,old_l,old_t); if (onScrollChangeListener != null) onScrollChangeListener.onScrollChange(t,old_t); }
    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) { this.onScrollChangeListener = onScrollChangeListener; }
    public interface OnScrollChangeListener { void onScrollChange(int scrollY, int oldScrollY); }

    private Context context;
    private AlbumItem album;
    private NinjaWebViewClient webViewClient;
    private NinjaWebChromeClient webChromeClient;
    private NinjaDownloadListener downloadListener;
    private NinjaClickHandler clickHandler;
    private GestureDetector gestureDetector;
    private AdBlock adBlock;
    private Cookie cookieHosts;
    private Javascript javaHosts;
    private Remote remoteHosts;
    private SharedPreferences sp;
    private WebSettings webSettings;
    private String defaultUserAgent;

    public AdBlock getAdBlock() { return adBlock; }
    public Cookie getCookieHosts() { return cookieHosts; }

    private final SharedPreferences.OnSharedPreferenceChangeListener securityPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            post(new Runnable() { @Override public void run() {
                if ("screenshot_protection".equals(key)) applyScreenshotProtection();
                else if ("block_third_party_cookies".equals(key)) applyThirdPartyCookiePolicy();
                else if ("block_popups".equals(key)) applyPopupPolicy();
            }});
        }
    };

    private boolean foreground;
    public boolean isForeground() { return foreground; }
    private BrowserController browserController = null;
    public BrowserController getBrowserController() { return browserController; }
    public void setBrowserController(BrowserController browserController) { this.browserController = browserController; if (this.album != null) this.album.setBrowserController(browserController); }

    public NinjaWebView(Context context) {
        super(context);
        WebViewProfileBinder.bindActiveProfile(context, this);
        this.context = context;
        this.foreground = false;
        this.adBlock = new AdBlock(this.context);
        this.javaHosts = new Javascript(this.context);
        this.cookieHosts = new Cookie(this.context);
        this.remoteHosts = new Remote(this.context);
        this.album = new AlbumItem(this.context, this, this.browserController);
        this.webViewClient = new NinjaWebViewClient(this);
        this.webChromeClient = new NinjaWebChromeClient(this);
        this.downloadListener = new NinjaDownloadListener(this.context);
        this.clickHandler = new NinjaClickHandler(this);
        this.gestureDetector = new GestureDetector(context, new NinjaGestureListener(this));
        initWebView(); initWebSettings(); initPreferences(); initAlbum();
    }

    private synchronized void initWebView() {
        setWebViewClient(webViewClient); setWebChromeClient(webChromeClient); setDownloadListener(downloadListener);
        setOnTouchListener(new OnTouchListener() { @SuppressLint("ClickableViewAccessibility") @Override public boolean onTouch(View view, MotionEvent motionEvent) { gestureDetector.onTouchEvent(motionEvent); return false; }});
    }

    @TargetApi(Build.VERSION_CODES.O)
    private synchronized void initWebSettings() {
        TypedValue typedValue = new TypedValue(); Resources.Theme theme = context.getTheme(); theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        @SuppressLint("Recycle") TypedArray arr = context.obtainStyledAttributes(typedValue.data, new int[]{android.R.attr.colorBackground});
        int primaryColor = arr.getColor(0, -1); arr.recycle(); setBackgroundColor(primaryColor);
        webSettings = getSettings(); defaultUserAgent = webSettings.getUserAgentString();
        webSettings.setBuiltInZoomControls(true); webSettings.setDisplayZoomControls(false); webSettings.setSupportZoom(true); webSettings.setSupportMultipleWindows(true); webSettings.setLoadWithOverviewMode(true); webSettings.setUseWideViewPort(true);
        if (android.os.Build.VERSION.SDK_INT >= 26) webSettings.setSafeBrowsingEnabled(true);
    }

    public synchronized void initPreferences() {
        sp = PreferenceManager.getDefaultSharedPreferences(context); sp.registerOnSharedPreferenceChangeListener(securityPreferenceListener); webSettings = getSettings();
        String userAgent = sp.getString("userAgent", "");
        webSettings.setUserAgentString(DesktopModePolicy.resolve(sp.getBoolean("desktop_mode", false), userAgent, defaultUserAgent));
        applyScreenshotProtection(); applyThirdPartyCookiePolicy(); applyPopupPolicy();
        webViewClient.enableAdBlock(sp.getBoolean(context.getString(R.string.sp_ad_block), true));
        webSettings.setTextZoom(Integer.parseInt(Objects.requireNonNull(sp.getString("sp_fontSize", "100"))));
        webSettings.setAllowFileAccessFromFileURLs(sp.getBoolean("sp_remote", true)); webSettings.setAllowUniversalAccessFromFileURLs(sp.getBoolean("sp_remote", true)); webSettings.setDomStorageEnabled(sp.getBoolean("sp_remote", true)); webSettings.setBlockNetworkImage(!sp.getBoolean(context.getString(R.string.sp_images), true)); webSettings.setJavaScriptEnabled(sp.getBoolean(context.getString(R.string.sp_javascript), true)); webSettings.setJavaScriptCanOpenWindowsAutomatically(!sp.getBoolean("block_popups", false)); webSettings.setGeolocationEnabled(sp.getBoolean(context.getString(R.string.sp_location), false));
    }

    private synchronized void applyUserAgentPreference() { String customUserAgent = sp.getString("userAgent", ""); webSettings.setUserAgentString(DesktopModePolicy.resolve(sp.getBoolean("desktop_mode", false), customUserAgent, defaultUserAgent)); }
    private void applyPopupPolicy() { if (webSettings != null && sp != null) webSettings.setJavaScriptCanOpenWindowsAutomatically(!sp.getBoolean("block_popups", false)); }

    private void applyScreenshotProtection() { if (!(context instanceof Activity) || sp == null) return; Activity activity = (Activity) context; if (sp.getBoolean("screenshot_protection", false)) activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE); else activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE); }
    private void applyThirdPartyCookiePolicy() { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && sp != null) { boolean block = sp.getBoolean("block_third_party_cookies", false); if (WebViewFeature.isFeatureSupported(WebViewFeature.MULTI_PROFILE)) { CookieManager profileCookieManager = WebViewCompat.getProfile(this).getCookieManager(); profileCookieManager.setAcceptThirdPartyCookies(this, ThirdPartyCookiePolicy.acceptThirdPartyCookies(block)); return; } CookieManager.getInstance().setAcceptThirdPartyCookies(this, ThirdPartyCookiePolicy.acceptThirdPartyCookies(block)); } }

    private synchronized void initAlbum() { album.setAlbumTitle(context.getString(R.string.app_name)); album.setBrowserController(browserController); }

    public synchronized HashMap<String, String> getRequestHeaders() {
        HashMap<String, String> requestHeaders = new HashMap<>(); requestHeaders.put("DNT", "1");
        if (SaveDataPolicy.isEnabled(sp.getBoolean(context.getString(R.string.sp_savedata), SaveDataPolicy.DEFAULT_ENABLED))) requestHeaders.put("Save-Data", "on");
        if (sp.getBoolean("gpc_enabled", false)) requestHeaders.put("Sec-GPC", GpcPolicy.HEADER_VALUE);
        String preferredLanguage = ProfileLanguagePolicy.normalize(sp.getString("preferred_language", "")); if (!preferredLanguage.isEmpty()) requestHeaders.put("Accept-Language", preferredLanguage);
        return requestHeaders;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override public synchronized void loadUrl(String url) {
        if (url == null || url.trim().isEmpty()) { NinjaToast.show(context, R.string.toast_load_error); return; }
        HelperUnit.initRendering(this); applyUserAgentPreference(); applyThirdPartyCookiePolicy(); applyPopupPolicy();
        if (javaHosts.isWhite(url) || sp.getBoolean(context.getString(R.string.sp_javascript), true)) webSettings.setJavaScriptEnabled(true); else webSettings.setJavaScriptEnabled(false);
        if (remoteHosts.isWhite(url) || sp.getBoolean("sp_remote", true)) { webSettings.setAllowFileAccessFromFileURLs(true); webSettings.setAllowUniversalAccessFromFileURLs(true); webSettings.setDomStorageEnabled(true); } else { webSettings.setAllowFileAccessFromFileURLs(false); webSettings.setAllowUniversalAccessFromFileURLs(false); webSettings.setDomStorageEnabled(false); }
        String trimmedUrl = url.trim(); String bangUrl = BangQueryPolicy.resolve(trimmedUrl); String navigationUrl = bangUrl != null ? bangUrl : BrowserUnit.queryWrapper(context, trimmedUrl); if (sp.getBoolean("https_only", false)) navigationUrl = HttpsOnlyPolicy.enforce(navigationUrl); super.loadUrl(navigationUrl, getRequestHeaders());
    }

    @Override public View getAlbumView() { return album.getAlbumView(); }
    public void setAlbumTitle(String title) { album.setAlbumTitle(title); }
    @Override public synchronized void activate() { foreground = true; }
    @Override public synchronized void deactivate() { foreground = false; }
}
