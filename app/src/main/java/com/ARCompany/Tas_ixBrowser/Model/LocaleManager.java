package com.ARCompany.Tas_ixBrowser.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

public class LocaleManager {

    @Retention(RetentionPolicy.SOURCE)

    @StringDef({ ENGLISH,  RUSSIAN, UZBEK })

    public @interface LocaleDef {

        String[] SUPPORTED_LOCALES = { ENGLISH,  RUSSIAN, UZBEK };

    }
    public static final String ENGLISH = "en";

    public static final String RUSSIAN = "ru";

    public static final String UZBEK = "uz";


    /**

     * SharedPreferences Key

     */

    private static final String LANGUAGE_KEY = "language_key";



    /**

     * set current pref locale

     */

    public static Context setLocale(Context mContext) {

        return updateResources(mContext, getLanguagePref(mContext));

    }



    /**

     * Set new Locale with context

     */

    public static Context setNewLocale(Context mContext, @LocaleDef String language) {

        setLanguagePref(mContext, language);

        return updateResources(mContext, language);

    }



    /**

     * Get saved Locale from SharedPreferences

     *

     * @param mContext current context

     * @return current locale key by default return english locale

     */

    public static String getLanguagePref(Context mContext) {

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        String deviceLocale=getLocale(mContext.getResources()).getLanguage();

        return mPreferences.getString(LANGUAGE_KEY, deviceLocale);

    }



    /**

     * set pref key

     */

    private static void setLanguagePref(Context mContext, String localeKey) {

        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);

        mPreferences.edit().putString(LANGUAGE_KEY, localeKey).apply();

    }



    /**

     * update resource

     */

    private static Context updateResources(Context context, String language) {

        Locale locale = new Locale(language);

        Locale.setDefault(locale);

        Resources res = context.getResources();

        Configuration config = new Configuration(res.getConfiguration());

        Locale.setDefault(locale);

        config.setLocale(locale);

        context = context.createConfigurationContext(config);

        context.getResources().updateConfiguration(config, res.getDisplayMetrics());

        return context;

    }



    /**

     * get current locale

     */

    public static Locale getLocale(Resources res) {

        Configuration config = res.getConfiguration();

        return Build.VERSION.SDK_INT >= 24 ? config.getLocales().get(0) : config.locale;

    }


}
