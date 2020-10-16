package com.avadna.luneblaze.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CheckConnectivity;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.services.GlobalToastService;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Locale;

public abstract class AppBaseActivity extends AppCompatActivity {

    CheckConnectivity connectivityReceiver;
    public ConnectionErrorDialog connectionErrorDialog;
    public PreferenceUtils preferenceUtils;
    private BroadcastReceiver languageReceiver;
    public CommonFunctions commonFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        Locale locale = new Locale(preferenceUtils.getLocaleSettings());
        Locale.setDefault(locale);
        // Create a new configuration object
        Configuration config = new Configuration();
        // Set the locale of the new configuration
        config.locale = locale;
        // Update the configuration of the Application context
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(languageReceiver,
                new IntentFilter(AppKeys.LANGUAGE_CHANGED));
    }

    private void initBroadCastReceiver() {
        languageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(AppKeys.LANGUAGE_CHANGED)) {
                    recreate();
                }
            }
        };

        connectivityReceiver = new CheckConnectivity();
        connectionErrorDialog = new ConnectionErrorDialog(this, R.style.AppTheme, "");
        connectionErrorDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectivityReceiver, filter);

        super.onResume();

    }

    @Override
    protected void onPause() {
        unregisterReceiver(connectivityReceiver);
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        if (connectionErrorDialog != null && connectionErrorDialog.isShowing()) {
            super.onBackPressed();
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(languageReceiver);

    }
}
