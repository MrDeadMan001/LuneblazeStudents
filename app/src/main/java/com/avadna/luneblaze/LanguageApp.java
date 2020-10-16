package com.avadna.luneblaze;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import androidx.multidex.MultiDex;

import com.avadna.luneblaze.helperClasses.PreferenceUtils;

import java.util.Locale;

public class LanguageApp extends Application {

    private Locale locale;
    private static Context context;

    public static Context getAppContext() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getBaseContext();
        changeLocale(context);
    }

    public void changeLocale(Context context) {
        Configuration config = context.getResources().getConfiguration();
        PreferenceUtils preferenceUtils = new PreferenceUtils(context);
        String lang = preferenceUtils.getLocaleSettings();
        if (!(config.locale.getLanguage().equals(lang))) {
            locale = new Locale(lang);
            Locale.setDefault(locale);
            config.locale = locale;
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (locale != null) {
            newConfig.locale = locale;
            Locale.setDefault(locale);
            context.getResources().updateConfiguration(newConfig, context.getResources().getDisplayMetrics());
        }
    }

}