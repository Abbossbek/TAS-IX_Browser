package com.ARCompany.Tas_ixBrowser;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;

public class AboutAppActivity extends BaseActivity {
    private SharedPreferences preferences;
    private final String SETTING = "setting";
    private LinearLayout layoutRate, layoutShare, layoutSend;
    ImageView appIcon;
    private Intent intent, chooser;
    private boolean hasRateAlertOpened=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = this.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        switch (preferences.getString("theme", "green")){
            case "black": setTheme(R.style.AppTheme_Black); break;
            case "green": setTheme(R.style.AppTheme); break;
            case "red": setTheme(R.style.AppTheme_Red); break;
            case "blue": setTheme(R.style.AppTheme_Blue); break;
            case "pink": setTheme(R.style.AppTheme_Pink); break;
            case "brown": setTheme(R.style.AppTheme_Brown); break;
        }
        setContentView(R.layout.activity_about_app);

        appIcon=findViewById(R.id.app_icon);
        appIcon.setImageBitmap(getBitmapFromAssets("Images/tasix_icon_screen.png"));

        layoutRate=findViewById(R.id.rate_layout);
        layoutRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.ARCompany.Tas_ixBrowser"));
                chooser=Intent.createChooser(intent, getString(R.string.rate_app));
                startActivity(chooser);
                preferences.edit().putBoolean("hasRateAlertOpened", true).apply();
            }
        });

        layoutShare=findViewById(R.id.share_layout);
        layoutShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "TAS-IX browser");
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.ARCompany.Tas_ixBrowser");
                chooser=Intent.createChooser(intent, getString(R.string.share_app));
                startActivity(chooser);
            }
        });

        layoutSend=findViewById(R.id.send_layout);
        layoutSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://t.me/programmer1718"));
                chooser=Intent.createChooser(intent, getString(R.string.send_message));
                startActivity(chooser);
            }
        });

    }


    private Bitmap getBitmapFromAssets(String fileName) {
        AssetManager assetManager = getAssets();
        try {
            return BitmapFactory.decodeStream(assetManager.open(fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
