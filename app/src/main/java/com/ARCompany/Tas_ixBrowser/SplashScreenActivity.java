package com.ARCompany.Tas_ixBrowser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ARCompany.Tas_ixBrowser.Model.MultiLanguageApp;

import java.io.IOException;

public class SplashScreenActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private final String SETTING = "setting";
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
        setContentView(R.layout.activity_splash_screen);

        ImageView imageView=findViewById(R.id.fullscreen_content);
        imageView.setImageBitmap(getBitmapFromAssets("Images/tasix_icon_screen.png"));

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
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
