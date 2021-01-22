package com.ARCompany.Tas_ixBrowser;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.ARCompany.Tas_ixBrowser.Model.Global;
import com.ARCompany.Tas_ixBrowser.View.TouchWebView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private SharedPreferences preferences;
    private final String SETTING = "setting";
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
            switch (preferences.getString("theme", "green")) {
                case "black":
                setTheme(R.style.AppTheme_Black);
                break;
            case "green":
                setTheme(R.style.AppTheme);
                break;
            case "red":
                setTheme(R.style.AppTheme_Red);
                break;
            case "blue":
                setTheme(R.style.AppTheme_Blue);
                break;
            case "pink":
                setTheme(R.style.AppTheme_Pink);
                break;
            case "brown":
                setTheme(R.style.AppTheme_Brown);
                break;
        }
        setContentView(R.layout.activity_main);

        int openCount=preferences.getInt("openCount", 0);
        openCount++;
        preferences.edit().putInt("openCount",openCount).apply();

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseMessaging.getInstance().subscribeToTopic("public");

        registerReceiver(new BroadcastReceiver() {
            @SuppressLint("ShowToast")
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(MainActivity.this, R.string.download_complate, Toast.LENGTH_LONG);
            }
        }, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        BottomNavigationView navView = findViewById(R.id.nav_view);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(navView, navController);

        if (Global.webViewList == null) {
            Global.webViewList = new ArrayList<>();
        }

        AudienceNetworkAds.initialize(this);
        Intent intent = getIntent();
        if (intent.getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);

                if (key.equals("url")) {
                    if (value != null) {
                        Global.newSearch = true;
                        Global.searchText = value;
                        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_browser);
                    }
                }

            }
        }


    }


    @Override
    protected void onResume() {
        String url = null;
        try {
            url = getIntent().getData().toString();
        } catch (Exception ex) {
        }
        if (url != null) {
            Global.newSearch = true;
            Global.searchText = url;
            Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.navigation_browser);
        }
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        try {
            BottomSheetBehavior bsbPages = BottomSheetBehavior.from(findViewById(R.id.list_pages));
            if (bsbPages.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bsbPages.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return;
            }
            TouchWebView webView = findViewById(R.id.webview);
            if (webView.canGoBack()) {
                webView.goBack();
                return;
            }
        } catch (Exception ex) {
        }
        super.onBackPressed();

    }
}
