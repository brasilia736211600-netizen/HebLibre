package de.baumann.browser.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Message;

import androidx.preference.PreferenceManager;

import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.*;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import de.baumann.browser.browser.*;
import de.baumann.browser.R;
import de.baumann.browser.unit.BangQueryPolicy;
import de.baumann.browser.unit.BrowserUnit;
import de.baumann.browser.unit.DesktopModePolicy;
import de.baumann.browser.unit.GpcPolicy;
import de.baumann.browser.unit.HelperUnit;
import de.baumann.browser.unit.HttpsOnlyPolicy;
import de.baumann.browser.unit.SaveDataPolicy;
import de.baumann.browser.unit.ThirdPartyCookiePolicy;
import de.baumann.browser.unit.WebViewProfileBinder;

import java.util.HashMap;
import java.util.Objects;

public class NinjaWebView extends WebView implements AlbumController {

    private OnScrollChangeListener onScrollChangeListener;


    public NinjaWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        WebViewProfileBinder.bindActiveProfile(context, this);
    }

    public NinjaWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        WebViewProfileBinder.bindActiveProfile(context, this);
    }

    @Override
    protected void onScrollChanged(int l, int t, int old_l, int old_t) {
        super.onScrollChanged(l, t, old_l, old_t);
        if (onScrollChangeListener != null) {
            onScrollChangeListener.onScrollChange(t, old_t);
        }
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public interface OnScrollChangeListener {
        /**
         * Called when the scroll position of a view changes.
         *
         * @param scrollY    Current vertical scroll origin.
         * @param oldScrollY Previous vertical scroll origin.
         */
        void onScrollChange(int scrollY, int oldScrollY);
    }

    private Context context;

    private AlbumItem album;
    private NinjaWebViewClient webViewClient;
    private NinjaWebChromeClient webChromeClient;
    private NinjaDownloadListener downloadListener;
    private NinjaClickHandler clickHandler;
    private GestureDetector gestureDetector;
    private AdBlock adBlock;
    public AdBlock getAdBlock() {
        return adBlock;
    }
    private Cookie cookieHosts;
    public Cookie getCookieHosts() { return cookieHosts; }
    private Javascript javaHosts;
    private Remote remoteHosts;
    private SharedPreferences sp;
    private WebSettings webSettings;
    private String defaultUserAgent;

    private final SharedPreferences.OnSharedPreferenceChangeListener securityPreferenceListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if ("screenshot_protection".equals(key)) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                applyScreenshotProtection();
                            }
                        });
                    } else if ("block_third_party_cookies".equals(key)) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                applyThirdPartyCookiePolicy();
                            }
                        });
                    }
                }
            };

    private boolean foreground;

    public boolean isForeground() {
        return foreground;
    }

    private BrowserController browserController = null;

    public BrowserController getBrowserController() {
        return browserController;
    }

    public void setBrowserController(BrowserController browserController) {
        this.browserController = browserController;
        this.album.setBrowserController(browserController);
    }

    public NinjaWebView(Context context) {
        super(context); // Cannot create a dialog, the WebView context is not an activity

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

        initWebView();
        initWebSettings();
        initPreferences();
        initAlbum();
    }

    private synchronized void initWebView() {
        setWebViewClient(webViewClient);
        setWebChromeClient(webChromeClient);
        setDownloadListener(downloadListener);
        setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                gestureDetector.onTouchEvent(motionEvent);
                return false;
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.O)
    private synchronized void initWebSettings() {


        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = context.getTheme();
        theme.resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        @SuppressLint("Recycle")
        TypedArray arr = context.obtainStyledAttributes(typedValue.data, new int[]{android.R.attr.colorBackground});
        int primaryColor = arr.getColor(0, -1);
        arr.recycle();

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        webSettings = getSettings();
        defaultUserAgent = webSettings.getUserAgentString();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setSupportZoom(false);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(false);
        webSettings.setUseWideViewPort(false);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webSettings.setSafeBrowsingEnabled(true);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            webSettings.setAlgorithmicDarkeningAllowed(false);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(false);
            webSettings.setAllowUniversalAccessFromFileURLs(false);
        }

        webSettings.setUserAgentString(defaultUserAgent);
        applyThirdPartyCookiePolicy();
        applyScreenshotProtection();
    }

    private synchronized void applyThirdPartyCookiePolicy() {
        CookieManager cookieManager = CookieManager.getInstance();
        boolean blockThirdParty = sp != null && sp.getBoolean("block_third_party_cookies", false);
        ThirdPartyCookiePolicy.apply(cookieManager, this, blockThirdParty);
    }

    private synchronized void applyScreenshotProtection() {
        boolean protectedScreen = sp != null && sp.getBoolean("screenshot_protection", false);
        if (protectedScreen) {
            getWindowToken();
            if (context instanceof Activity) {
                ((Activity) context).getWindow().setFlags(
                        android.view.WindowManager.LayoutParams.FLAG_SECURE,
                        android.view.WindowManager.LayoutParams.FLAG_SECURE);
            }
        }
    }

    private synchronized void initPreferences() {
        sp.registerOnSharedPreferenceChangeListener(securityPreferenceListener);
    }

    private synchronized void initAlbum() {
        album.setBrowserController(browserController);
    }

    public AlbumItem getAlbum() {
        return album;
    }

    public NinjaWebViewClient getWebViewClient() {
        return webViewClient;
    }

    public NinjaWebChromeClient getWebChromeClient() {
        return webChromeClient;
    }

    public NinjaDownloadListener getDownloadListener() {
        return downloadListener;
    }

    public NinjaClickHandler getClickHandler() {
        return clickHandler;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public SharedPreferences getSharedPreferences() {
        return sp;
    }

    public String getDefaultUserAgent() {
        return defaultUserAgent;
    }

    public void setForeground(boolean foreground) {
        this.foreground = foreground;
    }

    public void onShow() {
        foreground = true;
    }

    public void onHide() {
        foreground = false;
    }
}
