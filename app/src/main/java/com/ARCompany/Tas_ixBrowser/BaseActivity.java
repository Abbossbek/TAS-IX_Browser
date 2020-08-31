package com.ARCompany.Tas_ixBrowser;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ARCompany.Tas_ixBrowser.Model.LocaleManager;

import java.util.Locale;

import static android.content.pm.PackageManager.GET_META_DATA;


public class BaseActivity extends AppCompatActivity {


    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setLocale();
        super.onCreate(savedInstanceState);

        resetTitles();

    }



    @Override

    protected void attachBaseContext(Context base) {

        super.attachBaseContext(LocaleManager.setLocale(base));

    }

    void setLocale(){
        Locale locale=new Locale(LocaleManager.getLanguagePref(getBaseContext()));
        Configuration configuration=new Configuration(getResources().getConfiguration());
        Locale.setDefault(locale);
        configuration.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(configuration,
                getBaseContext().getResources().getDisplayMetrics());
    }

    protected void resetTitles() {

        try {

            ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), GET_META_DATA);

            if (info.labelRes != 0) {

                setTitle(info.labelRes);

            }

        } catch (PackageManager.NameNotFoundException e) {

            e.printStackTrace();

        }

    }


}
