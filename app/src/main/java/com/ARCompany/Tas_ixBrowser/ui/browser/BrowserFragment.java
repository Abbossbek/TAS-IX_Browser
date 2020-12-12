package com.ARCompany.Tas_ixBrowser.ui.browser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Transformation;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.ARCompany.Tas_ixBrowser.Model.LocaleManager;
import com.ARCompany.Tas_ixBrowser.Model.Global;
import com.ARCompany.Tas_ixBrowser.Model.WebAppInterface;
import com.ARCompany.Tas_ixBrowser.View.TouchWebView;
import com.ARCompany.Tas_ixBrowser.Model.history.HistoryDBHelper;
import com.ARCompany.Tas_ixBrowser.Model.ModelIP;
import com.ARCompany.Tas_ixBrowser.Model.note.NoteDBHelper;
import com.ARCompany.Tas_ixBrowser.Model.webpage.WebPage;
import com.ARCompany.Tas_ixBrowser.Model.webpage.WebPageDBHelper;
import com.ARCompany.Tas_ixBrowser.R;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class BrowserFragment extends Fragment {

    private AdView adView;
    private ImageView imgCloseAd;
    private View root;
    private Toolbar toolbar;
    private BrowserViewModel browserViewModel;
    private ProgressBar progressBar;
    private LinearLayout browserLayout, llPages, layoutAd;
    private ModelIP modelIP;
    private BottomSheetBehavior bsbPages, bsbMenu;
    private SharedPreferences preferences;
    private SearchView searchView;
    private BottomNavigationView bottomMenu;
    private WebPageDBHelper pageDBHelper;
    private HistoryDBHelper historyDBHelper;
    private NoteDBHelper noteDBHelper;
    private final String SETTING = "setting";
    private boolean loadingFinished = true, redirect = false, isManualDownload = false, webviewsLoading = false;
    private String mCM;
    private ValueCallback<Uri> mUM;
    private ValueCallback<Uri[]> mUMA;
    private final static int FCR = 1;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private boolean queryMaybeUrl = false;
    private boolean scrollStarted = false;
    private int coordinateY = 0;
    private Handler adHandler;

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M &&
                PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        adHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (adView != null)
                    adView.loadAd();
            }
        };

    }

    @Override
    public void onDestroyView() {
        adView=null;
        super.onDestroyView();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        browserViewModel =
                ViewModelProviders.of(this).get(BrowserViewModel.class);
        try {
            root = inflater.inflate(R.layout.fragment_browser, container, false);
        } catch (Exception ex) {
            String exs = ex.getMessage();
        }
        setValues(root);
        if (Global.webViewList.size() == 0) {
            webviewsLoading = true;
            for (WebPage webPage : pageDBHelper.loadWebPages()) {
                addWebView(webPage.getUrl(), webPage.getId(), null);
            }
            webviewsLoading = false;
        }


        if (Global.newSearch) {
            if (Patterns.WEB_URL.matcher(Global.searchText).matches()) {
                queryMaybeUrl = true;
                addWebView(Global.searchText, -1, null);
            } else {
                boolean onlyTasix = preferences.getBoolean("onlyTasix", true);
                String url = "https://yandex.uz/search/touch/?text=" + Global.searchText + (onlyTasix ? "&tasix=1" : "");
                addWebView(url, -1, null);
            }
            Global.newSearch = false;
        } else {
            if (Global.webViewList.size() != 0) {
                browserLayout = (LinearLayout) root.findViewById(R.id.browserLayout);
                browserLayout.removeAllViews();
                int webviewIndex = preferences.getInt("webviewIndex", 0);
                webviewIndex = webviewIndex < Global.webViewList.size() ? webviewIndex : Global.webViewList.size() - 1;
                TouchWebView webView1 = (TouchWebView) Global.webViewList.get(webviewIndex);
                if (webView1.getParent() != null) {
                    ((LinearLayout) webView1.getParent()).removeAllViewsInLayout();
                }
                try {
                    addWebView(webView1.getTag(R.id.url_key).toString(), Integer.parseInt(webView1.getTag(R.id.id_key).toString()), webView1);
                    if (!webviewsLoading) {
                        preferences.edit().putInt("webviewIndex", Global.webViewList.indexOf(webView1)).apply();
                    }
                } catch (Exception ignored) {
                }
            } else {
                boolean onlyTasix = preferences.getBoolean("onlyTasix", true);
                addWebView("https://yandex.uz/search/touch/?text=" + (onlyTasix ? "&tasix=1" : ""), -1, null);
            }
        }


        return root;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setValues(View root) {
        toolbar = ((Toolbar) root.findViewById(R.id.tool_bar_browser));
        toolbar.inflateMenu(R.menu.browser_top_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                TouchWebView webView = (TouchWebView) browserLayout.findViewById(R.id.webview);

                switch (item.getItemId()) {
                    case R.id.menu_item_pages:
                        if (bsbPages.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                            openWebPagesList();
                            bsbMenu.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        } else {
                            bsbPages.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        break;
                    case R.id.menu_item_update:
                        if (loadingFinished) {
                            webView.stopLoading();
                            checkUrlAndLoad(webView.getTag(R.id.url_key).toString());
                        } else {
                            loadingFinished = true;
                            progressBar.setVisibility(GONE);
                            toolbar.getMenu().findItem(R.id.menu_item_update)
                                    .setIcon(R.drawable.ic_update_white_24dp);
                            stopLoading();
                            webView.stopLoading();
                        }
                        break;
                    case R.id.menu_item_search:
                        searchView.setQueryHint(((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).getText());
                        String s = webView.getTag(R.id.url_key).toString();
                        searchView.post(new Runnable() {
                            @Override
                            public void run() {
                                TouchWebView webView1 = (TouchWebView) browserLayout.findViewById(R.id.webview);
                                searchView.setQuery(webView1.getTag(R.id.url_key).toString(), true);
                            }
                        });
                        searchView.onActionViewExpanded();
                        break;
                    default:
                }
                return false;
            }
        });
        searchView = (SearchView) getActivity().getLayoutInflater().inflate(R.layout.searchview, null);
        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        AutoCompleteTextView textView = (AutoCompleteTextView) searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(Color.LTGRAY);
        try {
            Field cursorRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            cursorRes.setAccessible(true);
            cursorRes.set(textView, R.drawable.cursor);
        } catch (Exception ignored) {
        }
        //EditText editText=(EditText)searchView.findViewById(android.)
        final LinearLayout layout = ((LinearLayout) searchView.getChildAt(0));
        LinearLayout layout1 = (LinearLayout) layout.getChildAt(2);
        LinearLayout layout2 = (LinearLayout) layout1.getChildAt(1);
        ImageView searchClose = (ImageView) layout2.getChildAt(1);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchClose.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_close_white_24dp));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                TouchWebView webView = (TouchWebView) browserLayout.findViewById(R.id.webview);
                if (!query.equals(webView.getTag(R.id.url_key).toString())) {
                    query = query.trim();
                    if (Patterns.WEB_URL.matcher(query).matches()) {
                        queryMaybeUrl = true;
                        checkUrlAndLoad(query);
                    } else {
                        boolean onlyTasix = preferences.getBoolean("onlyTasix", true);
                        String url = "https://yandex.uz/search/touch/?text=" + URLEncoder.encode(query) + (onlyTasix ? "&tasix=1" : "");
                        checkUrlAndLoad(url);
                    }
                    MenuItemCompat.collapseActionView(toolbar.getMenu().findItem(R.id.menu_item_search));
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        toolbar.getMenu().findItem(R.id.menu_item_search).setActionView(searchView);
        bottomMenu = root.findViewById(R.id.browser_bottom_menu);
        bottomMenu.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                TouchWebView webView = (TouchWebView) browserLayout.findViewById(R.id.webview);
                switch (menuItem.getItemId()) {
                    case R.id.menu_item_back:
                        if (webView.canGoBack()) {
                            webView.goBack();
                        }
                        break;
                    case R.id.menu_item_add_to_note:
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.note_add);
                        builder.setMessage(R.string.alert_add_note);
                        builder.setPositiveButton(R.string.btn_add, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                TouchWebView webView = (TouchWebView) browserLayout.findViewById(R.id.webview);
                                String title = webView.getTitle(), url = webView.getTag(R.id.url_key).toString();
                                noteDBHelper.addNote(title, url);
                            }
                        });
                        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        builder.create().show();
                        break;
                    case R.id.menu_item_downloads:
                        Uri downloadsPath = Uri.parse(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(downloadsPath, "*/*");
                        startActivity(Intent.createChooser(intent, getString(R.string.choose_file_manager)));
                        break;
                    case R.id.menu_item_next:
                        if (webView.canGoForward()) {
                            webView.goForward();
                        }
                        break;
                }
                return false;
            }
        });

        progressBar = ((ProgressBar) root.findViewById(R.id.progress_bar_webview));

        modelIP = new

                ModelIP(getContext());

        llPages = (LinearLayout) root.findViewById(R.id.pagesList);

        bsbPages = BottomSheetBehavior.from(root.findViewById(R.id.list_pages));
        bsbPages.setState(BottomSheetBehavior.STATE_HIDDEN);
        bsbMenu = BottomSheetBehavior.from(root.findViewById(R.id.browser_bottom_menu));
        bsbMenu.setState(BottomSheetBehavior.STATE_EXPANDED);
        preferences = getContext().getSharedPreferences(SETTING, Context.MODE_PRIVATE);

        pageDBHelper = new

                WebPageDBHelper(getContext());
        historyDBHelper = new

                HistoryDBHelper(getContext());
        noteDBHelper = new

                NoteDBHelper(getContext());

        imgCloseAd = (ImageView) root.findViewById(R.id.browser_close_ad);
        imgCloseAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (layoutAd.getVisibility() == GONE) {
                    adView.loadAd();
                    layoutAd.setVisibility(VISIBLE);
                    imgCloseAd.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                } else {
                    layoutAd.setVisibility(GONE);
                    imgCloseAd.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                }
            }
        });

        layoutAd = (LinearLayout) root.findViewById(R.id.browser_ad_layout);
        adView = new

                AdView(getContext(), "266300714543198_269186850921251", AdSize.BANNER_HEIGHT_50);
        AdSettings.setTestMode(false);
        adView.setAdListener(new

                                     AdListener() {
                                         @Override
                                         public void onError(Ad ad, AdError adError) {
                                                 adHandler.sendEmptyMessageDelayed(1, 15000);
                                         }

                                         @Override
                                         public void onAdLoaded(Ad ad) {
                                             adHandler.sendEmptyMessageDelayed(1, 15000);
                                             imgCloseAd.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                                         }

                                         @Override
                                         public void onAdClicked(Ad ad) {
                                         }

                                         @Override
                                         public void onLoggingImpression(Ad ad) {

                                         }
                                     });
        layoutAd.addView(adView);
        adView.loadAd();
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void addWebView(String url, int id, TouchWebView webViewClone) {
        stopLoading();
        if (Global.webViewList.size() < 20 || id != -1) {
            try {
                TouchWebView.setWebContentsDebuggingEnabled(true);
                TouchWebView webView;
                if (webViewClone == null) {
                    webView = new TouchWebView(getActivity());
                    webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    webView.setId(R.id.webview);
                    if (!url.startsWith("file:///")) {
                        webView.setTag(R.id.url_key, url);
                    }
                    WebSettings settings = webView.getSettings();
                    settings.setJavaScriptEnabled(true);
                    settings.setJavaScriptCanOpenWindowsAutomatically(true);
                    settings.setCacheMode(WebSettings.LOAD_DEFAULT);
                    settings.setLoadWithOverviewMode(true);
                    settings.setSupportZoom(true);
                    settings.setDisplayZoomControls(false);
                    settings.setBuiltInZoomControls(true);
                    settings.setDomStorageEnabled(true);
                    settings.setDatabaseEnabled(true);
                    settings.setAppCachePath(getContext().getCacheDir().getAbsolutePath() + "/TASIX_cache");
                    settings.setAppCacheEnabled(true);
                    settings.setUseWideViewPort(true);
                    settings.setAllowFileAccess(true);
                    settings.setAllowFileAccessFromFileURLs(true);
                    settings.setAllowUniversalAccessFromFileURLs(true);
                    String useragent = WebSettings.getDefaultUserAgent(getContext());
                    settings.setUserAgentString(useragent);
                    webView.setScrollBarStyle(TouchWebView.SCROLLBARS_OUTSIDE_OVERLAY);
                    webView.setScrollbarFadingEnabled(false);
                    webView.setSoundEffectsEnabled(true);
                    webView.addJavascriptInterface(new WebAppInterface(getContext()), "Android");
                } else {
                    webView = webViewClone;
                }
                webView.setScrollListener(new TouchWebView.ScrollListener() {
                    @Override
                    public void onScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
                        final float scale = getResources().getDisplayMetrics().density;
                        if (!scrollStarted) {
                            scrollStarted = true;
                            coordinateY = scrollY;
                        } else {
                            RelativeLayout relativeLayout = root.findViewById(R.id.relative_layout_browser);
                            CardView cardView = root.findViewById(R.id.browser_action_bar_cardview);
                            scrollStarted = false;
                            if (scrollY > coordinateY) {
                                bsbMenu.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.action_bar_close);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    CardView cardView = root.findViewById(R.id.browser_action_bar_cardview);

                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                        RelativeLayout relativeLayout = root.findViewById(R.id.relative_layout_browser);
                                        if (relativeLayout.getPaddingTop() != 0) {
                                            Animation a = new Animation() {
                                                @Override
                                                protected void applyTransformation(float interpolatedTime, Transformation t) {
                                                    RelativeLayout relativeLayout = root.findViewById(R.id.relative_layout_browser);
                                                    relativeLayout.setPadding(0, (int) ((60 * scale + 0.5f) * (1 - interpolatedTime)), 0, 0);
                                                }
                                            };
                                            a.setDuration(300);
                                            relativeLayout.startAnimation(a);
                                        }
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        cardView.setVisibility(GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                if (cardView.getVisibility() == VISIBLE)
                                    cardView.startAnimation(animation);
                            } else if (scrollY < coordinateY) {
                                bsbMenu.setState(BottomSheetBehavior.STATE_EXPANDED);
                                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.action_bar_open);
                                if (scrollY == 0 && relativeLayout.getPaddingTop() == 0) {
                                    animation.setRepeatCount(0);
                                    Animation a = new Animation() {
                                        @Override
                                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                                            RelativeLayout relativeLayout = root.findViewById(R.id.relative_layout_browser);
                                            relativeLayout.setPadding(0, (int) ((60 * scale + 0.5f) * interpolatedTime), 0, 0);
                                        }
                                    };
                                    a.setDuration(300);
                                    relativeLayout.startAnimation(a);
                                }
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    CardView cardView = root.findViewById(R.id.browser_action_bar_cardview);

                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                        cardView.setVisibility(VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                if (cardView.getVisibility() == GONE)
                                    cardView.startAnimation(animation);
                            } else {
                                if(scrollY!=0) {
                                    bsbMenu.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                }else{
                                    bsbMenu.setState(BottomSheetBehavior.STATE_EXPANDED);
                                }
                                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.action_bar_open);
                                if (scrollY == 0 && relativeLayout.getPaddingTop() == 0) {
                                    animation.setRepeatCount(0);
                                    Animation a = new Animation() {
                                        @Override
                                        protected void applyTransformation(float interpolatedTime, Transformation t) {
                                            RelativeLayout relativeLayout = root.findViewById(R.id.relative_layout_browser);
                                            relativeLayout.setPadding(0, (int) ((60 * scale + 0.5f) * interpolatedTime), 0, 0);
                                        }
                                    };
                                    a.setDuration(300);
                                    relativeLayout.startAnimation(a);
                                }
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    CardView cardView = root.findViewById(R.id.browser_action_bar_cardview);

                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        cardView.setVisibility(VISIBLE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                if (cardView.getVisibility() == GONE)
                                    cardView.startAnimation(animation);
                            }
                        }
                    }
                });
                webView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        return false;
                    }
                });

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageCommitVisible(WebView view, String url) {
                        stopLoading();
                    }

                    @Override
                    public void onLoadResource(WebView view, String url) {
                        super.onLoadResource(view, url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        loadingFinished = false;
                        if (isDownloadUrl(url)) {
                            downloadDialog(url, null, null, null, getUrlFileLength((url)));
                        } else {
                            progressBar.setVisibility(VISIBLE);
                            toolbar.getMenu().findItem(R.id.menu_item_update)
                                    .setIcon(R.drawable.ic_close_white_24dp);
                        }

                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        try {
                            if (!loadingFinished) {
                                redirect = true;
                            }
                            if (URLUtil.isValidUrl(url)) {
                                isManualDownload = false;
                                if (isDownloadUrl(url)) {
                                    downloadDialog(url, view.getSettings().getUserAgentString(), null, null, getUrlFileLength(url));
                                    isManualDownload = true;
                                } else {
                                    checkUrlAndLoad(url);
                                    progressBar.setVisibility(VISIBLE);
                                    toolbar.getMenu().findItem(R.id.menu_item_update)
                                            .setIcon(R.drawable.ic_close_white_24dp);
                                    loadingFinished = false;
                                }
                            } else {
                                checkUrlAction(view, url);
                                stopLoading();
                            }
                        } catch (Exception ignored) {
                        }
                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {

                        stopLoading();
                        if (!redirect) {
                            loadingFinished = true;
                            if (!view.getUrl().startsWith("file:///")) {
                                historyDBHelper.addHistory(view.getTitle(), view.getUrl(), Calendar.getInstance().getTime());
                                view.setTag(R.id.url_key, url);
                            }
                            if (view.canGoBack()) {
                                bottomMenu.getMenu().findItem(R.id.menu_item_back).setEnabled(true).setVisible(true);
                            } else {
                                bottomMenu.getMenu().findItem(R.id.menu_item_back).setEnabled(false).setVisible(false);
                            }
                            if (view.canGoForward()) {
                                bottomMenu.getMenu().findItem(R.id.menu_item_next).setEnabled(true).setVisible(true);
                            } else {
                                bottomMenu.getMenu().findItem(R.id.menu_item_next).setEnabled(false).setVisible(false);
                            }
                        } else {
                            redirect = false;
                        }
                    }

                    @Override
                    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                        super.onReceivedHttpError(view, request, errorResponse);
                        stopLoading();
                    }

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        super.onReceivedSslError(view, handler, error);
                        stopLoading();
                    }

                    @Override
                    public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
                        super.onReceivedLoginRequest(view, realm, account, args);
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        stopLoading();
                        if (!view.getUrl().startsWith("file:///")) {
                            view.setTag(R.id.url_key, view.getUrl());
                        }
                        switch (errorCode) {
                            case -6:
                            case -1:
                                break;
                            default:
                                loadError("load");
                        }
                        pageDBHelper.updateWebPage(Integer.parseInt(view.getTag(R.id.id_key).toString()), view.getTag(R.id.url_key).toString());
                    }

                    @Nullable
                    @Override
                    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {

                        return super.shouldInterceptRequest(view, request);
                    }
                });
                webView.setWebChromeClient(new WebChromeClient() {
                    private View mCustomView;
                    private WebChromeClient.CustomViewCallback mCustomViewCallback;
                    protected FrameLayout mFullscreenContainer;
                    private int mOriginalOrientation;
                    private int mOriginalSystemUiVisibility;

                    public Bitmap getDefaultVideoPoster() {
                        if (mCustomView == null) {
                            return null;
                        }
                        return BitmapFactory.decodeResource(getActivity().getApplicationContext().getResources(), 2130837573);
                    }

                    public void onHideCustomView() {
                        ((FrameLayout) getActivity().getWindow().getDecorView()).removeView(this.mCustomView);
                        this.mCustomView = null;
                        getActivity().getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                        getActivity().setRequestedOrientation(this.mOriginalOrientation);
                        this.mCustomViewCallback.onCustomViewHidden();
                        this.mCustomViewCallback = null;
                    }

                    public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
                        if (this.mCustomView != null) {
                            onHideCustomView();
                            return;
                        }
                        this.mCustomView = paramView;
                        this.mOriginalSystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
                        this.mOriginalOrientation = getActivity().getRequestedOrientation();
                        this.mCustomViewCallback = paramCustomViewCallback;
                        ((FrameLayout) getActivity().getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
                        getActivity().getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }

                    @SuppressLint("ShowToast")
                    @Override
                    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                        try {
                            if (mUMA != null) {
                                mUMA.onReceiveValue(null);
                            }
                            mUMA = filePathCallback;
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                    takePictureIntent.putExtra("PhotoPath", mCM);
                                } catch (IOException ex) {
                                    Log.e("Webview", "Image file creation failed", ex);
                                }
                                if (photoFile != null) {
                                    mCM = "file:" + photoFile.getAbsolutePath();
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                                } else {
                                    takePictureIntent = null;
                                }
                            }

                            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                            contentSelectionIntent.setType("*/*");
                            Intent[] intentArray;
                            if (takePictureIntent != null) {
                                intentArray = new Intent[]{takePictureIntent};
                            } else {
                                intentArray = new Intent[0];
                            }

                            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
                            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                            chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.choose_file));
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                            startActivityForResult(chooserIntent, FCR);
                            return true;

                        } catch (Exception ex) {
                            Snackbar.make(root, Objects.requireNonNull(ex.getMessage()), Snackbar.LENGTH_SHORT).show();
                            return false;
                        }
                    }

                    @Override
                    public void onProgressChanged(WebView view, int newProgress) {
                        if (newProgress < 100) {
                            progressBar.setVisibility(VISIBLE);
                            toolbar.getMenu().findItem(R.id.menu_item_update)
                                    .setIcon(R.drawable.ic_close_white_24dp);
                        } else {
                            stopLoading();
                        }
                    }

                    @Override
                    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                        return super.onJsAlert(view, url, message, result);
                    }

                    @Override
                    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
                        return super.onJsBeforeUnload(view, url, message, result);
                    }

                    @Override
                    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                        return super.onJsConfirm(view, url, message, result);
                    }

                    @Override
                    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
                        return super.onJsPrompt(view, url, message, defaultValue, result);
                    }

                    @Override
                    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
                        super.onReceivedTouchIconUrl(view, url, precomposed);
                    }

                    @Override
                    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
                        super.onShowCustomView(view, requestedOrientation, callback);
                    }

                    @Override
                    public void onRequestFocus(WebView view) {
                        super.onRequestFocus(view);
                    }
                });
                webView.setDownloadListener(new DownloadListener() {
                    @Override
                    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                        downloadDialog(url, userAgent, contentDisposition, mimetype, contentLength);
                    }
                });

                if (id == -1) {
                    pageDBHelper.addWebPage(url);
                    webView.setTag(R.id.id_key, pageDBHelper.getLastWebPage().getId());
                } else {
                    webView.setTag(R.id.id_key, id);
                }

                if (webViewClone == null) {
                    Global.webViewList.add(webView);
                }

                browserLayout = (LinearLayout) root.findViewById(R.id.browserLayout);
                browserLayout.removeAllViews();
                browserLayout.addView(webView);
                if (!webviewsLoading) {
                    preferences.edit().putInt("webviewIndex", Global.webViewList.indexOf(webView)).apply();
                    checkUrlAndLoad(url);
                }
            } catch (Exception ex) {
//                Snackbar.make(root, "Xatolik yuz berdi!\n" + ex.getMessage(), Snackbar.LENGTH_LONG)
//                        .setAction("Xatolik", null).show();
            }
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.alert_warning);
            builder.setMessage(R.string.webpage_limit);
            builder.setPositiveButton(R.string.btn_open, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    openWebPagesList();
                }
            });
            builder.setNegativeButton(R.string.btn_cancel, null);
            builder.create().show();

            browserLayout = (LinearLayout) root.findViewById(R.id.browserLayout);
            browserLayout.removeAllViews();
            int webviewIndex = preferences.getInt("webviewIndex", 0);
            TouchWebView webView1 = (TouchWebView) Global.webViewList.get(webviewIndex);
            if (webView1.getParent() != null) {
                ((LinearLayout) webView1.getParent()).removeAllViewsInLayout();
            }
            try {
                addWebView(webView1.getTag(R.id.url_key).toString(), Integer.parseInt(webView1.getTag(R.id.id_key).toString()), webView1);
                if (!webviewsLoading) {
                    preferences.edit().putInt("webviewIndex", Global.webViewList.indexOf(webView1)).apply();
                }
            } catch (Exception ignored) {
            }
        }
    }

    private void checkUrlAction(WebView view, String url) {
        if (url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(url));
            startActivity(intent);

        } else if (url.startsWith("sms:")) {
            Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
            startActivity(intent);

        } else if (url.startsWith("intent:")) {
            try {
                Context context = getContext();
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);

                if (intent != null) {
                    view.stopLoading();

                    PackageManager packageManager = context.getPackageManager();
                    ResolveInfo info = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    if (info != null) {
                        context.startActivity(intent);
                    } else {
                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                        view.loadUrl(fallbackUrl);

                    }
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }


        } else if (url.startsWith("print:")) {
            print_page(view, view.getTitle());

            // use this to open your apps page on google play store app :: href="rate:android"
        } else if (url.startsWith("share:")) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, view.getTitle());
            intent.putExtra(Intent.EXTRA_TEXT, view.getTitle() + "\nVisit: " + (Uri.parse(url).toString()).replace("share:", ""));
            startActivity(Intent.createChooser(intent, getString(R.string.share_w_friends)));
        }
    }

    //Printing pages
    private void print_page(WebView view, String print_name) {
        PrintManager printManager = (PrintManager) getActivity().getSystemService(Context.PRINT_SERVICE);
        PrintDocumentAdapter printAdapter = view.createPrintDocumentAdapter(print_name);
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A5);
        PrintJob printJob = printManager.print(print_name, printAdapter, builder.build());

        if (printJob.isCompleted()) {
            Snackbar.make(root, R.string.print_complete, Snackbar.LENGTH_SHORT).show();
        } else if (printJob.isFailed()) {
            Snackbar.make(root, R.string.print_failed, Snackbar.LENGTH_SHORT).show();
        }
    }

    private void stopLoading() {
        try {
            TouchWebView webView = (TouchWebView) browserLayout.findViewById(R.id.webview);
            toolbar.setTitle(webView.getTitle());
            progressBar.setVisibility(GONE);
            toolbar.getMenu().findItem(R.id.menu_item_update)
                    .setIcon(R.drawable.ic_update_white_24dp);
        } catch (Exception ignored) {
        }
    }

    private boolean isDownloadUrl(String url) {
        return url.endsWith(".mp3") || url.endsWith(".mp4") || url.endsWith(".avi") || url.endsWith(".mkv") || url.endsWith(".3gp") || url.endsWith(".jpg") || url.endsWith(".png");
    }

    private static long getUrlFileLength(String url) {
        try {
            final HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setRequestMethod("HEAD");
            final String lengthHeaderField = urlConnection.getHeaderField("content-Length");
            Long result = lengthHeaderField == null ? null : Long.parseLong(lengthHeaderField);
            return result == null || result < 0L ? -1L : result;
        } catch (Exception ignored) {
            //Log.println(Log.ERROR, "", ignored.getMessage());
        }
        return -1L;
    }

    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        this.openFileChooser(uploadMsg, "*/*");
    }

    private void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        this.openFileChooser(uploadMsg, acceptType, null);
    }

    private void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String
            capture) {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("*/*");
        startActivityForResult(Intent.createChooser(i, getString(R.string.choose_file)), FILECHOOSER_RESULTCODE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= 21) {
            Uri[] results = null;
            //Check if response is positive
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == FCR) {
                    if (null == mUMA) {
                        return;
                    }
                    if (intent == null) {
                        //Capture Photo if no image available
                        if (mCM != null) {
                            results = new Uri[]{Uri.parse(mCM)};
                        }
                    } else {
                        String dataString = intent.getDataString();
                        if (dataString != null) {
                            results = new Uri[]{Uri.parse(dataString)};
                        }
                    }
                }
            }
            mUMA.onReceiveValue(results);
            mUMA = null;
        } else {
            if (requestCode == FCR) {
                if (null == mUM) return;
                Uri result = intent == null || resultCode != Activity.RESULT_OK ? null : intent.getData();
                mUM.onReceiveValue(result);
                mUM = null;
            }
        }
    }

    // Create an image file
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "img_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);


    }

    private void downloadDialog(final String url, final String userAgent,
                                final String contentDisposition, final String mimetype, long contentLength) {
        TouchWebView webView = (TouchWebView) browserLayout.findViewById(R.id.webview);

        if (PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED
                || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

            final String fileName = URLDecoder.decode(URLUtil.guessFileName(url, contentDisposition, mimetype));
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.download);
            builder.setMessage(getString(R.string.alert_download) + "\n" + getString(R.string.file_name) + " " + fileName +
                    "\n" + getString(R.string.file_size) + " " + getSizeofFile(contentLength));
            builder.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
                @SuppressLint("ShowToast")
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                    request.setMimeType(mimetype);
                    String cookie = CookieManager.getInstance().getCookie(url);
                    request.addRequestHeader("Cookie", cookie);
                    request.addRequestHeader("User-Agent", userAgent);
                    request.setTitle(fileName);
                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
                    try {
                        if (manager != null) {
                            long id = manager.enqueue(request);
                            Snackbar.make(root, R.string.download_started, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(root, R.string.download_error, Snackbar.LENGTH_SHORT).show();
                        }
                    } catch (Exception ex) {
                        String s = ex.getMessage();
                    }
                    TouchWebView webView = (TouchWebView) browserLayout.findViewById(R.id.webview);

                    if (!isManualDownload && loadingFinished) {
                        if (webView.canGoBack()) {
                            webView.goBack();
                        } else {
                            int id = Integer.parseInt(webView.getTag(R.id.id_key).toString());
                            pageDBHelper.deleteWebPage(id);
                            Global.webViewList.remove(webView);
                            int webviewIndex = preferences.getInt("webviewIndex", 0);
                            browserLayout = (LinearLayout) root.findViewById(R.id.browserLayout);
                            browserLayout.removeAllViews();
                            if (webviewIndex != 0) {
                                TouchWebView webView1 = (TouchWebView) Global.webViewList.get(webviewIndex - 1);
                                if (webView1.getParent() != null) {
                                    ((LinearLayout) webView1.getParent()).removeAllViewsInLayout();
                                }
                                try {
                                    addWebView(webView1.getTag(R.id.url_key).toString(), Integer.parseInt(webView1.getTag(R.id.id_key).toString()), webView1);
                                    if (!webviewsLoading) {
                                        preferences.edit().putInt("webviewIndex", Global.webViewList.indexOf(webView1)).apply();
                                    }
                                } catch (Exception ignored) {
                                }
                            } else {
                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_home);
                            }

                        }
                    } else {
                        stopLoading();
                    }
                }

            });
            builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();


                    TouchWebView webView = (TouchWebView) browserLayout.findViewById(R.id.webview);
                    if (!isManualDownload && loadingFinished) {
                        if (webView.canGoBack()) {
                            webView.goBack();
                        } else {
                            int id = Integer.parseInt(webView.getTag(R.id.id_key).toString());
                            pageDBHelper.deleteWebPage(id);
                            Global.webViewList.remove(webView);
                            int webviewIndex = preferences.getInt("webviewIndex", 0);
                            browserLayout = (LinearLayout) root.findViewById(R.id.browserLayout);
                            browserLayout.removeAllViews();
                            if (webviewIndex != 0) {
                                TouchWebView webView1 = (TouchWebView) Global.webViewList.get(webviewIndex - 1);
                                if (webView1.getParent() != null) {
                                    ((LinearLayout) webView1.getParent()).removeAllViewsInLayout();
                                }
                                try {
                                    addWebView(webView1.getTag(R.id.url_key).toString(), Integer.parseInt(webView1.getTag(R.id.id_key).toString()), webView1);
                                    if (!webviewsLoading) {
                                        preferences.edit().putInt("webviewIndex", Global.webViewList.indexOf(webView1)).apply();
                                    }
                                } catch (Exception ex) {
                                }
                            } else {
                                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_home);
                            }
                        }

                    } else {
                        stopLoading();
                    }
                }
            });
            builder.create().show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @SuppressLint("DefaultLocale")
    private String getSizeofFile(long contentLength) {
        String sizeText = "";
        if (contentLength > 0) {
            if (contentLength < Math.pow(2, 20)) {
                sizeText = String.format("%.3f", contentLength / Math.pow(2, 10)) + " KB";
            } else {
                if (contentLength < Math.pow(2, 30)) {
                    sizeText = String.format("%.3f", contentLength / Math.pow(2, 20)) + " MB";
                } else {
                    if (contentLength < Math.pow(2, 40)) {
                        sizeText = String.format("%.3f", contentLength / Math.pow(2, 30)) + " GB";
                    }
                }
            }
        } else {
            sizeText = "Unknown";
        }
        return sizeText;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void openWebPagesList() {
        llPages.removeAllViewsInLayout();
        for (final TouchWebView webview : Global.webViewList) {
            LinearLayout cardLayout = (LinearLayout) getActivity().getLayoutInflater().inflate(R.layout.card_view_page, null);

            CardView cardView = (CardView) cardLayout.findViewById(R.id.cardview_page);

            ImageView icon = cardView.findViewById(R.id.cardview_page_icon);
            if (webview.getFavicon() != null) {
                icon.setBackgroundColor(Color.TRANSPARENT);
                icon.setImageBitmap(webview.getFavicon());
            }

            LinearLayout linearLayoutVertical = cardView.findViewById(R.id.cardview_page_layout);
            linearLayoutVertical.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    browserLayout = (LinearLayout) root.findViewById(R.id.browserLayout);
                    TouchWebView webView0 = (TouchWebView) browserLayout.findViewById(R.id.webview);
                    stopLoading();
                    webView0.stopLoading();

                    browserLayout.removeAllViews();
                    int webviewIndex = Integer.parseInt(String.valueOf(Global.webViewList.indexOf(webview)));
                    TouchWebView webView1 = (TouchWebView) Global.webViewList.get(webviewIndex);
                    if (webView1.getParent() != null) {
                        ((LinearLayout) webView1.getParent()).removeAllViewsInLayout();
                    }
                    try {
                        addWebView(webView1.getTag(R.id.url_key).toString(), Integer.parseInt(webView1.getTag(R.id.id_key).toString()), webView1);
                        toolbar.setTitle(webView1.getTitle());
                        if (!webviewsLoading) {
                            preferences.edit().putInt("webviewIndex", webviewIndex).apply();
                        }
                        bsbPages.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } catch (Exception ignored) {
                    }

                    adView.loadAd();
                }
            });

            TextView title = linearLayoutVertical.findViewById(R.id.cardview_page_title);
            title.setText(webview.getTitle());

            TextView url = linearLayoutVertical.findViewById(R.id.cardview_page_url);
            url.setText(webview.getTag(R.id.url_key).toString());

            Button button = cardView.findViewById(R.id.cardview_page_delete);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    llPages.removeView((View) view.getParent().getParent().getParent());
                    int id = Integer.parseInt(webview.getTag(R.id.id_key).toString());
                    pageDBHelper.deleteWebPage(id);
                    Global.webViewList.remove(webview);
                    int webviewIndex = preferences.getInt("webviewIndex", 0);
                    if (Global.webViewList.indexOf(webview) == webviewIndex) {
                        browserLayout = (LinearLayout) root.findViewById(R.id.browserLayout);
                        browserLayout.removeAllViews();
                        if (webviewIndex != 0) {
                            TouchWebView webView1 = (TouchWebView) Global.webViewList.get(webviewIndex - 1);
                            if (webView1.getParent() != null) {
                                ((LinearLayout) webView1.getParent()).removeAllViewsInLayout();
                            }
                            try {
                                addWebView(webView1.getTag(R.id.url_key).toString(), Integer.parseInt(webView1.getTag(R.id.id_key).toString()), webView1);
                                if (!webviewsLoading) {
                                    preferences.edit().putInt("webviewIndex", Global.webViewList.indexOf(webView1)).apply();
                                }
                            } catch (Exception ignored) {
                            }
                        } else {
                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.navigation_home);
                        }
                    }
                }
            });

            llPages.addView(cardLayout);

        }
        bsbPages.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void checkUrlAndLoad(String url) {
        TouchWebView webView = (TouchWebView) browserLayout.findViewById(R.id.webview);
        if (!url.startsWith("file:///")) {
            webView.setTag(R.id.url_key, url);
        }
        loadingFinished = false;
        progressBar.setVisibility(VISIBLE);
        toolbar.getMenu().findItem(R.id.menu_item_update)
                .setIcon(R.drawable.ic_close_white_24dp);
        AsyncWork asyncWork = new AsyncWork();
        asyncWork.execute(url);
    }

    private boolean checkInternetConnection() {
        boolean connection = false;
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                connection = true;
            }
        } catch (Exception ignored) {

        }
        return connection;
    }

    @SuppressLint("StaticFieldLeak")
    class AsyncWork extends AsyncTask<String, Void, String[]> {
        @Override
        protected String[] doInBackground(String... strings) {
            if (!loadingFinished) {
                InetAddress address = null;
                try {
                    if (checkInternetConnection()) {
                        URI uri = new URI(strings[0]);
                        if (uri.getHost() == null && queryMaybeUrl) {
                            queryMaybeUrl = false;
                            strings[0] = "http://" + strings[0];
                            uri = new URI(strings[0]);
                            if (uri.getHost() == null) {
                            }
                        }
                        address = InetAddress.getByName(uri.getHost());
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    boolean onlyTasix = preferences.getBoolean("onlyTasix", true);
                    strings[0] = "https://yandex.uz/search/touch/?text=" + Global.searchText + (onlyTasix ? "&tasix=1" : "");
                    URI uri = null;
                    try {
                        uri = new URI(strings[0]);
                        address = InetAddress.getByName(uri.getHost());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                if (address != null) {
                    return new String[]{address.getHostAddress(), strings[0]};
                } else {
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            try {
                    if (!loadingFinished) {
                        TouchWebView webView = (TouchWebView) browserLayout.findViewById(R.id.webview);
                        
//                        if(ProxySettings.setProxy(webView, "5.182.26.130", 4145)) {
                            if (checkInternetConnection()) {
                                if (strings != null && strings[0] != null && strings[0].split("\\.").length == 4) {
                                    boolean onlyTasix = preferences.getBoolean("onlyTasix", true);
                                    if ((!onlyTasix || modelIP.IsInTasix(strings[0]))
                                            || (strings[1].toLowerCase().startsWith("https://yandex.uz") && strings[1].toLowerCase().contains("tasix=1"))
                                            || strings[1].toLowerCase().contains("olx.uz")) {
                                        webView.loadUrl(strings[1]);
                                        if (!strings[1].startsWith("file:///")) {
                                            webView.setTag(R.id.url_key, strings[1]);
                                        }
                                        pageDBHelper.updateWebPage(Integer.parseInt(webView.getTag(R.id.id_key).toString()), strings[1]);
                                    } else {
                                        loadError("tasix");
                                    }
                                } else {
                                    if (!loadingFinished) {
                                        loadError("check");
                                    }
                                }

                            } else {
                                loadError("connection");
                            }
                        }
//                    }else{
//                        Toast.makeText(getContext(), "Proxy ulanmadi!", Toast.LENGTH_LONG);
//                    }

            } catch (Exception ignored) {

            }
        }
    }

    private void loadError(String error) {
        try {
            String lang = LocaleManager.getLanguagePref(getContext());
            TouchWebView webView = (TouchWebView) browserLayout.findViewById(R.id.webview);
            switch (error) {
                case "check":
                    webView.loadUrl("file:///android_asset/HtmlPages/check_error_" + lang + ".html");
                    break;
                case "load":
                    webView.loadUrl("file:///android_asset/HtmlPages/load_error_" + lang + ".html");
                    break;
                case "connection":
                    webView.loadUrl("file:///android_asset/HtmlPages/no_connection_" + lang + ".html");
                    break;
                case "tasix":
                    webView.loadUrl("file:///android_asset/HtmlPages/no_tasix_" + lang + ".html");
                    break;
            }
        } catch (Exception ex) {

        }
    }
}