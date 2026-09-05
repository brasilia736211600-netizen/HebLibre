package de.baumann.browser.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.os.Message;
import android.os.StrictMode;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.baumann.browser.browser.AdBlock;
import de.baumann.browser.browser.AlbumController;
import de.baumann.browser.browser.BrowserContainer;
import de.baumann.browser.browser.BrowserController;
import de.baumann.browser.browser.Cookie;
import de.baumann.browser.browser.Javascript;
import de.baumann.browser.browser.Remote;
import de.baumann.browser.database.BookmarkList;
import de.baumann.browser.database.Record;
import de.baumann.browser.database.RecordAction;
import de.baumann.browser.R;
import de.baumann.browser.service.ClearService;
import de.baumann.browser.unit.BrowserUnit;
import de.baumann.browser.unit.HelperUnit;
import de.baumann.browser.unit.ProfileCatalogStore;
import de.baumann.browser.unit.ProfileIdentity;
import de.baumann.browser.unit.RecordUnit;
import de.baumann.browser.unit.ViewUnit;
import de.baumann.browser.view.CompleteAdapter;
import de.baumann.browser.view.GridAdapter;

import de.baumann.browser.view.GridItem;
import de.baumann.browser.view.NinjaToast;
import de.baumann.browser.view.NinjaWebView;
import de.baumann.browser.view.RecordAdapter;
import de.baumann.browser.view.SwipeTouchListener;

import static android.content.ContentValues.TAG;

@SuppressWarnings({"ApplySharedPref"})
public class BrowserActivity extends AppCompatActivity implements BrowserController {

    // Menus

    private RecordAdapter adapter;

    // Views

    private ImageButton omniboxRefresh;
    private ImageButton open_startPage;
    private ImageButton open_bookmark;
    private ImageButton open_history;
    private ImageButton open_menu;
    private ImageButton omniboxOverview;
    private FloatingActionButton fab_imageButtonNav;
    private AutoCompleteTextView inputBox;
    private ProgressBar progressBar;
    private EditText searchBox;
    private BottomSheetDialog bottomSheetDialog_OverView;
    private NinjaWebView ninjaWebView;
    private ListView listView;
    private TextView omniboxTitle;
    private View customView;
    private VideoView videoView;
    private ScrollView tab_ScrollView;

    // Layouts

    private RelativeLayout appBar;
    private RelativeLayout omnibox;
    private RelativeLayout searchPanel;
    private FrameLayout contentFrame;
    private LinearLayout tab_container;
    private FrameLayout fullscreenHolder;

    private View open_startPageView;
    private View open_bookmarkView;
    private View open_historyView;
    private View open_tabView;

    // Others

    private String overViewTab;
    private BroadcastReceiver downloadReceiver;
    private BottomSheetBehavior mBehavior;

    private Activity activity;
    private Context context;
    private SharedPreferences sp;
    private Javascript javaHosts;
    private Cookie cookieHosts;
    private AdBlock adBlock;
    private Remote remote;
    private final BrowserContainer browserContainer = new BrowserContainer();
    private String browserProfileId;

    private long newIcon;
    private boolean filter;
    private long filterBy;
    private boolean showOverflow = false;
    private TextView overflowTitle;

    private boolean prepareRecord() {
        NinjaWebView webView = (NinjaWebView) currentAlbumController;
        String title = webView.getTitle();
        String url = webView.getUrl();
        return (title == null
                || title.isEmpty()
                || url == null
                || url.isEmpty()
                || url.startsWith(BrowserUnit.URL_SCHEME_ABOUT)
                || url.startsWith(BrowserUnit.URL_SCHEME_MAIL_TO)
                || url.startsWith(BrowserUnit.URL_SCHEME_INTENT));
    }

    private int originalOrientation;
    private boolean searchOnSite;

    private ValueCallback<Uri[]> filePathCallback = null;
    private AlbumController currentAlbumController = null;

    private static final int INPUT_FILE_REQUEST_CODE = 1;

    private ValueCallback<Uri[]> mFilePathCallback;

    // Classes

    private class VideoCompletionListener implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }
        @Override
        public void onCompletion(MediaPlayer mp) {
            onHideCustomView();
        }
    }

    // Overrides

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        WebView.enableSlowWholeDocumentDraw();
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        context = BrowserActivity.this;
        activity = BrowserActivity.this;
        browserProfileId = ProfileCatalogStore.getActiveProfileId(context);

        sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putInt("restart_changed", 0).apply();
        sp.edit().putBoolean("pdf_create", false).commit();

        HelperUnit.applyTheme(context);
        setContentView(R.layout.activity_main);

        if (Objects.requireNonNull(sp.getString("saved_key_ok", "no")).equals("no")) {
            if (Locale.getDefault().getCountry().equals("CN")) {
                sp.edit().putString(getString(R.string.sp_search_engine), "2").apply();
            }
            sp.edit().putString("saved_key_ok", "yes").apply();

            sp.edit().putString("setting_gesture_tb_up", "08").apply();
            sp.edit().putString("setting_gesture_tb_down", "01").apply();
            sp.edit().putString("setting_gesture_tb_left", "07").apply();
            sp.edit().putString("setting_gesture_tb_right", "06").apply();

            sp.edit().putString("setting_gesture_nav_up", "04").apply();
            sp.edit().putString("setting_gesture_nav_down", "05").apply();
            sp.edit().putString("setting_gesture_nav_left", "03").apply();
            sp.edit().putString("setting_gesture_nav_right", "02").apply();

            sp.edit().putBoolean(getString(R.string.sp_location), false).apply();
        }

        contentFrame = findViewById(R.id.main_content);
        appBar = findViewById(R.id.appBar);

        initOmnibox();
        initSearchPanel();
        initOverview();

        new AdBlock(context); // For AdBlock cold boot
        new Javascript(context);
        new Cookie(context);
        new Remote(context);

        downloadReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                View dialogView = View.inflate(context, R.layout.dialog_action, null);
                TextView textView = dialogView.findViewById(R.id.dialog_text);
                textView.setText(R.string.toast_downloadComplete);
                Button action_ok = dialogView.findViewById(R.id.action_ok);
                action_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                        bottomSheetDialog.cancel();
                    }
                });
                bottomSheetDialog.setContentView(dialogView);
                bottomSheetDialog.show();
                HelperUnit.setBottomSheetBehavior(bottomSheetDialog, dialogView, BottomSheetBehavior.STATE_EXPANDED);
            }
        };

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);

        if (!restoreSessionTabs()) {
            dispatchIntent(getIntent());
        }

        if (sp.getBoolean("start_tabStart", false)){
            showOverview();
        }
        HelperUnit.initRendering(ninjaWebView);
    }

    private boolean restoreSessionTabs() {
        Intent intent = getIntent();
        if (hasExplicitLaunchIntent(intent)) {
            return false;
        }

        RecordAction action = new RecordAction(context);
        action.open(false);
        List<Record> records = action.listTab();
        action.close();
        if (records.isEmpty()) {
            return false;
        }

        for (int i = 0; i < records.size(); i++) {
            Record record = records.get(i);
            String url = record.getURL();
            if (!isRestorableSessionUrl(url)) {
                continue;
            }
            String title = record.getTitle();
            if (title == null || title.trim().isEmpty()) {
                title = url;
            }
            addAlbum(title, url, i == records.size() - 1);
        }
        return currentAlbumController != null;
    }

    private boolean hasExplicitLaunchIntent(Intent intent) {
        if (intent == null) {
            return false;
        }
        String action = intent.getAction();
        return Intent.ACTION_VIEW.equals(action)
                || Intent.ACTION_SEND.equals(action)
                || Intent.ACTION_WEB_SEARCH.equals(action)
                || "sc_history".equals(action)
                || "sc_bookmark".equals(action)
                || "sc_startPage".equals(action);
    }

    private boolean isRestorableSessionUrl(String url) {
        if (url == null) {
            return false;
        }
        String normalized = url.trim().toLowerCase(Locale.US);
        return normalized.startsWith("https://")
                || normalized.startsWith("http://")
                || "about:blank".equals(normalized);
    }

    private void saveSessionTabs() {
        if (browserProfileId == null) {
            return;
        }
        RecordAction action = new RecordAction(context);
        action.open(true);
        action.clearTable(RecordUnit.TABLE_TAB, browserProfileId);
        int ordinal = 0;
        for (AlbumController controller : browserContainer.list()) {
            if (!(controller instanceof NinjaWebView)) {
                continue;
            }
            NinjaWebView webView = (NinjaWebView) controller;
            String url = webView.getUrl();
            if (!isRestorableSessionUrl(url)) {
                continue;
            }
            String title = webView.getTitle();
            if (title == null || title.trim().isEmpty()) {
                title = url;
            }
            action.addTab(new Record(title, url, ordinal, -1), browserProfileId);
            ordinal++;
        }
        action.close();
    }

    @Override
    public void onStop() {
        saveSessionTabs();
        super.onStop();
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        Uri[] results = null;
        // Check that the response is a good one
        if(resultCode == Activity.RESULT_OK) {
            if(data != null) {
                // If there is not data, then we may have taken a photo
                String dataString = data.getDataString();
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        mFilePathCallback.onReceiveValue(results);
        mFilePathCallback = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (sp.getInt("restart_changed", 1) == 1) {
            sp.edit().putInt("restart_changed", 0).apply();
            final BottomSheetDialog dialog = new BottomSheetDialog(context);
            View dialogView = View.inflate(context, R.layout.dialog_action, null);
            TextView textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.toast_restart);
            Button action_ok = dialogView.findViewById(R.id.action_ok);
            action_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            dialog.setContentView(dialogView);
            dialog.show();
            HelperUnit.setBottomSheetBehavior(dialog, dialogView, BottomSheetBehavior.STATE_EXPANDED);
        }

        dispatchIntent(getIntent());

        if (sp.getBoolean("pdf_create", false)) {
            sp.edit().putBoolean("pdf_create", false).commit();

            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            View dialogView = View.inflate(context, R.layout.dialog_action, null);
            TextView textView = dialogView.findViewById(R.id.dialog_text);
            textView.setText(R.string.toast_downloadComplete);

            Button action_ok = dialogView.findViewById(R.id.action_ok);
            action_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                    bottomSheetDialog.cancel();
                }
            });
            bottomSheetDialog.setContentView(dialogView);
            bottomSheetDialog.show();
            HelperUnit.setBottomSheetBehavior(bottomSheetDialog, dialogView, BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public void onDestroy() {
        if (sp.getBoolean(getString(R.string.sp_clear_quit), false)) {
            Intent toClearService = new Intent(this, ClearService.class);
            startService(toClearService);
        }
        saveSessionTabs();
        browserContainer.clear();
        unregisterReceiver(downloadReceiver);
        finish();
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                showOverflow();
            case KeyEvent.KEYCODE_BACK:
                hideKeyboard(activity);
                hideOverview();
                if (fullscreenHolder != null || customView != null || videoView != null) {
                    Log.v(TAG, "FOSS Browser in fullscreen mode");
                } else if (omnibox.getVisibility() == View.GONE && sp.getBoolean("sp_toolbarShow", true)) {
                    showOmnibox();
                } else {
                    if (ninjaWebView.canGoBack()) {
                        ninjaWebView.goBack();
                    } else {
                        removeAlbum(currentAlbumController);
                    }
                }
                return true;
        }
        return false;
    }

    @Override
    public synchronized void showAlbum(AlbumController controller) {
        if (currentAlbumController != null) {
            currentAlbumController.deactivate();
            View av = (View) controller;
            contentFrame.removeAllViews();
            contentFrame.addView(av);
        } else {
            contentFrame.removeAllViews();
            contentFrame.addView((View) controller);
        }
        currentAlbumController = controller;
        currentAlbumController.activate();
        updateOmnibox();
    }

    @Override
    public void updateAutoComplete() {
        RecordAction action = new RecordAction(this);
        action.open(false);
        List<Record> list = action.listEntries(activity);
        action.close();
        CompleteAdapter adapter = new CompleteAdapter(this, R.layout.list_item, list);
        inputBox.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        inputBox.setThreshold(1);
        inputBox.setDropDownVerticalOffset(-16);
        inputBox.setDropDownWidth(ViewUnit.getWindowWidth(this));
        inputBox.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = ((TextView) view.findViewById(R.id.record_item_time)).getText().toString();
                updateAlbum(url);
                hideKeyboard(activity);
            }
        });
    }

    private void showOverview() {
        mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetDialog_OverView.show();
    }

    public void hideOverview () {
        if (bottomSheetDialog_OverView != null) {
            bottomSheetDialog_OverView.cancel();
        }
    }

    private void printPDF () {
        String title = HelperUnit.fileName(ninjaWebView.getUrl());
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = ninjaWebView.createPrintDocumentAdapter(title);
        Objects.requireNonNull(printManager).print(title, printAdapter, new PrintAttributes.Builder().build());
        sp.edit().putBoolean("pdf_create", true).commit();
    }

    private void dispatchIntent(Intent intent) {

        String action = intent.getAction();
        String url = intent.getStringExtra(Intent.EXTRA_TEXT);

        if ("".equals(action)) {
            Log.i(TAG, "resumed FOSS browser");
        } else if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_WEB_SEARCH)) {
            addAlbum(null, intent.getStringExtra(SearchManager.QUERY), true);
        } else if (filePathCallback != null) {
            filePathCallback = null;
        } else if ("sc_history".equals(action)) {
            addAlbum(getString(R.string.app_name), sp.getString("favoriteURL", "https://github.com/scoute-dich/browser"), true);
            showOverview();
            open_history.performClick();
        } else if ("sc_bookmark".equals(action)) {
            addAlbum(getString(R.string.app_name), sp.getString("favoriteURL", "https://github.com/scoute-dich/browser"), true);
            showOverview();
            open_bookmark.performClick();
        } else if ("sc_startPage".equals(action)) {
            addAlbum(getString(R.string.app_name), sp.getString("favoriteURL", "https://github.com/scoute-dich/browser"), true);
            showOverview();
            open_startPage.performClick();
        } else if (Intent.ACTION_SEND.equals(action)) {
            addAlbum(getString(R.string.app_name), url, true);
        } else if (Intent.ACTION_VIEW.equals(action)) {
            String data = Objects.requireNonNull(getIntent().getData()).toString();
            addAlbum(getString(R.string.app_name), data, true);
        } else {
            addAlbum(getString(R.string.app_name), sp.getString("favoriteURL", "https://github.com/scoute-dich/browser"), true);
        }
        getIntent().setAction("");
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOmnibox() {

        omnibox = findViewById(R.id.main_omnibox);
        inputBox = findViewById(R.id.main_omnibox_input);
        omniboxOverview = findViewById(R.id.omnibox_overview);
        ImageButton omniboxOverflow = findViewById(R.id.omnibox_overflow);
        omniboxTitle = findViewById(R.id.omnibox_title);
        progressBar = findViewById(R.id.main_progress_bar);

        String nav_position = Objects.requireNonNull(sp.getString("nav_position", "0"));

        switch (nav_position) {
            case "1":
                fab_imageButtonNav = findViewById(R.id.fab_imageButtonNav_left);
                break;
            case "2":
                fab_imageButtonNav = findViewById(R.id.fab_imageButtonNav_center);
                break;
            case "3":
                fab_imageButtonNav = findViewById(R.id.fab_imageButtonNav_null);
                break;
            default:
                fab_imageButtonNav = findViewById(R.id.fab_imageButtonNav_right);
                break;
        }

        fab_imageButtonNav.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                show_dialogFastToggle();
                return false;
            }
        });

        omniboxOverflow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                show_dialogFastToggle();
                return false;
            }
        });

        fab_imageButtonNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverflow();
            }
        });

        omniboxOverflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverflow();
            }
        });

        if (sp.getBoolean("sp_gestures_use", true)) {
            fab_imageButtonNav.setOnTouchListener(new SwipeTouchListener(context) {
                public void onSwipeTop() { performGesture("setting_gesture_nav_up"); }
                public void onSwipeBottom() { performGesture("setting_gesture_nav_down"); }
                public void onSwipeRight() { performGesture("setting_gesture_nav_right"); }
                public void onSwipeLeft() { performGesture("setting_gesture_nav_left"); }
            });

            omniboxOverflow.setOnTouchListener(new SwipeTouchListener(context) {
                public void onSwipeTop() { performGesture("setting_gesture_nav_up"); }
                public void onSwipeBottom() { performGesture("setting_gesture_nav_down"); }
                public void onSwipeRight() { performGesture("setting_gesture_nav_right"); }
                public void onSwipeLeft() { performGesture("setting_gesture_nav_left"); }
            });

            inputBox.setOnTouchListener(new SwipeTouchListener(context) {
                public void onSwipeTop() { performGesture("setting_gesture_tb_up"); }
                public void onSwipeBottom() { performGesture("setting_gesture_tb_down"); }
                public void onSwipeRight() { performGesture("setting_gesture_tb_right"); }
                public void onSwipeLeft() { performGesture("setting_gesture_tb_left"); }
            });
        }

        inputBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String query = inputBox.getText().toString().trim();
                if (query.isEmpty()) {
                    NinjaToast.show(context, getString(R.string.toast_input_empty));
                    return true;
                }
                updateAlbum(query);
                hideKeyboard(activity);
                return false;
            }
        });

        inputBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (inputBox.hasFocus()) {
                    ninjaWebView.stopLoading();
                    inputBox.setText(ninjaWebView.getUrl());
                    omniboxTitle.setVisibility(View.GONE);
                    inputBox.requestFocus();
                    inputBox.setSelection(0,inputBox.getText().toString().length());
                } else {
                    omniboxTitle.setVisibility(View.VISIBLE);
                    omniboxTitle.setText(ninjaWebView.getTitle());
                    hideKeyboard(activity);
                }
            }
        });
        updateAutoComplete();
        omniboxOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverview();
            }
        });
    }

    private void performGesture (String gesture) {
        String gestureAction = Objects.requireNonNull(sp.getString(gesture, "0"));
        AlbumController controller;
        ninjaWebView = (NinjaWebView) currentAlbumController;

        switch (gestureAction) {
            case "01":
                break;
            case "02":
                if (ninjaWebView.canGoForward()) {
                    ninjaWebView.goForward();
                } else {
                    NinjaToast.show(context,R.string.toast_webview_forward);
                }
                break;
            case "03":
                if (ninjaWebView.canGoBack()) {
                    ninjaWebView.goBack();
                } else {
                    removeAlbum(currentAlbumController);
                }
                break;
            case "04":
                ninjaWebView.pageUp(true);
                break;
            case "05":
                ninjaWebView.pageDown(true);
                break;
            case "06":
                controller = nextAlbumController(false);
                showAlbum(controller);
                break;
            case "07":
                controller = nextAlbumController(true);
                showAlbum(controller);
                break;
            case "08":
                showOverview();
                break;
            case "09":
                addAlbum(getString(R.string.app_name), sp.getString("favoriteURL", "https://github.com/scoute-dich/browser"), true);
                break;
            case "10":
                removeAlbum(currentAlbumController);
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initOverview() {

        bottomSheetDialog_OverView = new BottomSheetDialog(context);
        View dialogView = View.inflate(context, R.layout.dialog_overview, null);
        final TextView overview_title = dialogView.findViewById(R.id.overview_title);

        open_startPage = dialogView.findViewById(R.id.open_startSite);
        open_bookmark = dialogView.findViewById(R.id.open_bookmark_2);
        open_history = dialogView.findViewById(R.id.open_history_2);
        open_menu = dialogView.findViewById(R.id.open_menu);
        tab_container = dialogView.findViewById(R.id.tab_container);
        ImageButton open_tab = dialogView.findViewById(R.id.open_tab);
        tab_ScrollView = dialogView.findViewById(R.id.listTabs);
        listView = dialogView.findViewById(R.id.listRecord);

        open_startPageView = dialogView.findViewById(R.id.open_startSiteView);
        open_bookmarkView = dialogView.findViewById(R.id.open_bookmarkView);
        open_historyView = dialogView.findViewById(R.id.open_historyView);
        open_tabView = dialogView.findViewById(R.id.open_tabView);

        // allow scrolling in listView without closing the bottomSheetDialog
        listView.setOnTouchListener(new ListView.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {// Disallow NestedScrollView to intercept touch events.
                    if (listView.canScrollVertically(-1)) {
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }
                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });

        open_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog menuList = new BottomSheetDialog(context);
                View view = View.inflate(context, R.layout.dialog_menu, null);
                GridView gridView = view.findViewById(R.id.menu_grid);
                TextView gridTitle = view.findViewById(R.id.overview_title);
                gridTitle.setText(overViewTab);

                List<GridItem> list = new LinkedList<>();
                GridItem item_01 = new GridItem(R.drawable.icon_delete, getResources().getString(R.string.menu_delete), null, 0);
                GridItem item_02 = new GridItem(R.drawable.icon_sort_title, getResources().getString(R.string.menu_sort), null, 0);
                GridItem item_03 = new GridItem(R.drawable.filter_variant, getResources().getString(R.string.menu_filter), null, 0);

                list.add(list.size(), item_01);

                if (overViewTab.equals(getString(R.string.album_title_home)) || overViewTab.equals(getString(R.string.album_title_bookmarks))) {
                    list.add(list.size(), item_02);
                }

                if (overViewTab.equals(getString(R.string.album_title_bookmarks))) {
                    list.add(list.size(), item_03);
                }

                GridAdapter gridAdapter = new GridAdapter(context, list);
                gridView.setNumColumns(1);
                gridView.setAdapter(gridAdapter);
                gridAdapter.notifyDataSetChanged();
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        if (position == 0) {
                            menuList.cancel ();
                            final BottomSheetDialog dialogDelete = new BottomSheetDialog(context);
                            View dialogDeleteView = View.inflate(context, R.layout.dialog_action, null);
                            TextView textView = dialogDeleteView.findViewById(R.id.dialog_text);
                            textView.setText(R.string.hint_database);
                            Button action_ok = dialogDeleteView.findViewById(R.id.action_ok);
                            action_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (overViewTab.equals(getString(R.string.album_title_home))) {
                                        BrowserUnit.clearHome(context);
                                        open_startPage.performClick();
                                    } else if (overViewTab.equals(getString(R.string.album_title_bookmarks))) {
                                        BrowserUnit.clearBookmark(context);
                                        open_bookmark.performClick();
                                    } else if (overViewTab.equals(getString(R.string.album_title_history))) {
                                        BrowserUnit.clearHistory(context);
                                        open_history.performClick();
                                    }
                                    dialogDelete.cancel();
                                }
                            });
                            dialogDelete.setContentView(dialogDeleteView);
                            dialogDelete.show();
                            HelperUnit.setBottomSheetBehavior(dialogDelete, dialogDeleteView, BottomSheetBehavior.STATE_EXPANDED);

                        } else if (position == 1) {
                            menuList.cancel();

                            final BottomSheetDialog menuSort = new BottomSheetDialog(context);
                            View menuSortView = View.inflate(context, R.layout.dialog_menu, null);
                            GridView gridView = menuSortView.findViewById(R.id.menu_grid);
                            TextView gridTitle = menuSortView.findViewById(R.id.overview_title);
                            gridTitle.setText(overViewTab);

                            List<GridItem> list = new LinkedList<>();
                            GridItem item_01 = new GridItem(R.drawable.icon_sort_title, getResources().getString(R.string.dialog_sortName), null, 0);
                            GridItem item_02 = new GridItem(R.drawable.icon_sort_icon, getResources().getString(R.string.dialog_sortIcon), null, 0);
                            GridItem item_03 = new GridItem(R.drawable.icon_sort_tme, getResources().getString(R.string.dialog_sortDate), null, 0);


                            if (overViewTab.equals(getString(R.string.album_title_home))) {
                                list.add(list.size(), item_01);
                                list.add(list.size(), item_03);
                            }
                            if (overViewTab.equals(getString(R.string.album_title_bookmarks))) {
                                list.add(list.size(), item_01);
                                list.add(list.size(), item_02);
                            }
                            GridAdapter gridAdapter = new GridAdapter(context, list);
                            gridView.setNumColumns(1);
                            gridView.setAdapter(gridAdapter);
                            gridAdapter.notifyDataSetChanged();
                            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    if (position == 0) {
                                        if (overViewTab.equals(getString(R.string.album_title_bookmarks))) {
                                            sp.edit().putString("sort_bookmark", "title").apply();
                                            menuSort.cancel();
                                            open_bookmark.performClick();
                                        } else if (overViewTab.equals(getString(R.string.album_title_home))){
                                            sp.edit().putString("sort_startSite", "title").apply();
                                            menuSort.cancel();
                                            open_startPage.performClick();
                                        }

                                    } else if (position == 1) {
                                        if (overViewTab.equals(getString(R.string.album_title_bookmarks))) {
                                            sp.edit().putString("sort_bookmark", "time").apply();
                                            menuSort.cancel();
                                            open_bookmark.performClick();
                                        } else if (overViewTab.equals(getString(R.string.album_title_home))){
                                            sp.edit().putString("sort_startSite", "ordinal").apply();
                                            menuSort.cancel();
                                            open_startPage.performClick();
                                        }
                                    }
                                }
                            });
                            menuSort.setContentView(menuSortView);
                            menuSort.show();
                            HelperUnit.setBottomSheetBehavior(menuSort, menuSortView, BottomSheetBehavior.STATE_EXPANDED);
                        } else if (position == 2) {
                            menuList.cancel();
                            show_dialogFilter();
                        }
                    }
                });
                menuList.setContentView(view);
                menuList.show();
                HelperUnit.setBottomSheetBehavior(menuList, view, BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        bottomSheetDialog_OverView.setContentView(dialogView);

        mBehavior = BottomSheetBehavior.from((View) dialogView.getParent());
        mBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN || newState == BottomSheetBehavior.STATE_COLLAPSED){
                    hideOverview();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        open_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                omniboxOverview.setImageResource(R.drawable.icon_preview);
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                open_startPageView.setVisibility(View.INVISIBLE);
                open_bookmarkView.setVisibility(View.INVISIBLE);
                open_historyView.setVisibility(View.INVISIBLE);
                open_tabView.setVisibility(View.VISIBLE);
                overViewTab = getString(R.string.album_title_tab);
                overview_title.setText(overViewTab);
                open_menu.setVisibility(View.GONE);
                listView.setVisibility(View.GONE);
                tab_ScrollView.setVisibility(View.VISIBLE);
            }
        });

        open_startPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                omniboxOverview.setImageResource(R.drawable.icon_earth);
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                open_startPageView.setVisibility(View.VISIBLE);
                open_bookmarkView.setVisibility(View.INVISIBLE);
                open_historyView.setVisibility(View.INVISIBLE);
                open_tabView.setVisibility(View.INVISIBLE);
                overViewTab = getString(R.string.album_title_home);
                overview_title.setText(overViewTab);
                open_menu.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                tab_ScrollView.setVisibility(View.GONE);

                RecordAction action = new RecordAction(context);
                action.open(false);
                final List<Record> list = action.listStartSite(activity);
                action.close();

                adapter = new RecordAdapter(context, list);
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        updateAlbum(list.get(position).getURL());
                        hideOverview();
                    }
                });

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        show_contextMenu_list(list.get(position).getTitle(), list.get(position).getURL(), adapter, list, position,0);
                        return true;
                    }
                });
            }
        });

        open_bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                omniboxOverview.setImageResource(R.drawable.icon_bookmark);
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                open_startPageView.setVisibility(View.INVISIBLE);
                open_bookmarkView.setVisibility(View.VISIBLE);
                open_historyView.setVisibility(View.INVISIBLE);
                open_tabView.setVisibility(View.INVISIBLE);
                overViewTab = getString(R.string.album_title_bookmarks);
                overview_title.setText(overViewTab);
                open_menu.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                tab_ScrollView.setVisibility(View.GONE);

                RecordAction action = new RecordAction(context);
                action.open(false);
                final List<Record> list;
                list = action.listBookmark(activity, filter, filterBy);
                action.close();

                adapter = new RecordAdapter(context, list){
                    @SuppressWarnings("NullableProblems")
                    @Override
                    public View getView (int position, View convertView, @NonNull ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        ImageView record_item_icon = v.findViewById(R.id.record_item_icon);
                        record_item_icon.setVisibility(View.VISIBLE);
                        return v;
                    }
                };

                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                filter = false;
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        updateAlbum(list.get(position).getURL());
                        hideOverview();
                    }
                });

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        show_contextMenu_list(list.get(position).getTitle(), list.get(position).getURL(), adapter, list, position, list.get(position).getTime());
                        return true;
                    }
                });
                initBookmarkList();
            }
        });

        open_bookmark.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                show_dialogFilter();
                return false;
            }
        });

        open_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                omniboxOverview.setImageResource(R.drawable.icon_history);
                mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                open_startPageView.setVisibility(View.INVISIBLE);
                open_bookmarkView.setVisibility(View.INVISIBLE);
                open_historyView.setVisibility(View.VISIBLE);
                open_tabView.setVisibility(View.INVISIBLE);
                overViewTab = getString(R.string.album_title_history);
                overview_title.setText(overViewTab);
                open_menu.setVisibility(View.VISIBLE);
                listView.setVisibility(View.VISIBLE);
                tab_ScrollView.setVisibility(View.GONE);

                RecordAction action = new RecordAction(context);
                action.open(false);
                final List<Record> list;
                list = action.listHistory();
                action.close();

                //noinspection NullableProblems
                adapter = new RecordAdapter(context, list){
                    @Override
                    public View getView (int position, View convertView, @NonNull ViewGroup parent) {
                        View v = super.getView(position, convertView, parent);
                        TextView record_item_time = v.findViewById(R.id.record_item_time);
                        record_item_time.setVisibility(View.VISIBLE);
                        return v;
                    }
                };

                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ninjaWebView.loadUrl(list.get(position).getURL());
                        hideOverview();
                    }
                });

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        show_contextMenu_list(list.get(position).getTitle(), list.get(position).getURL(), adapter, list, position,0);
                        return true;
                    }
                });
            }
        });

        switch (Objects.requireNonNull(sp.getString("start_tab", "0"))) {
            case "3":
                open_bookmark.performClick();
                break;
            case "4":
                open_history.performClick();
                break;
            case "5":
                open_tab.performClick();
                break;
            default:
                open_startPage.performClick();
                break;
        }
    }

    private void initSearchPanel() {
        searchPanel = findViewById(R.id.main_search_panel);
        searchBox = findViewById(R.id.main_search_box);
        ImageView searchUp = findViewById(R.id.main_search_up);
        ImageView searchDown = findViewById(R.id.main_search_down);
        ImageView searchCancel = findViewById(R.id.main_search_cancel);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override
            public void afterTextChanged(Editable s) {
                if (currentAlbumController != null) {
                    ((NinjaWebView) currentAlbumController).findAllAsync(s.toString());
                }
            }
        });
        searchUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(activity);
                ((NinjaWebView) currentAlbumController).findNext(false);
            }
        });
        searchDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(activity);
                ((NinjaWebView) currentAlbumController).findNext(true);
            }
        });
        searchCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(activity);
                searchOnSite = false;
                searchBox.setText("");
                showOmnibox();
            }
        });
    }

    private void initBookmarkList() {
        BookmarkList db = new BookmarkList(context);
        db.open();
        Cursor cursor = db.fetchAllData(activity);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            RecordAction action = new RecordAction(context);
            action.open(true);
            action.addBookmark(new Record(
                    cursor.getString(cursor.getColumnIndexOrThrow("pass_title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("pass_content")),
                    1, 0));
            cursor.moveToNext();
            action.close();
            deleteDatabase("pass_DB_v01.db");
        }
    }

    // ... rest of original BrowserActivity methods remain unchanged ...
}
