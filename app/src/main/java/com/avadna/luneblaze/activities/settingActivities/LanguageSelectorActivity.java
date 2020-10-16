package com.avadna.luneblaze.activities.settingActivities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.avadna.luneblaze.LanguageApp;
import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;

public class LanguageSelectorActivity extends AppCompatActivity {

    TextView tv_english;
    TextView tv_hindi;
    TextView tv_done;
    PreferenceUtils preferenceUtils;
    ActionBar actionBar;
    View.OnClickListener onClickListener;
    String selectedLang = "en";
    CommonFunctions commonFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selector);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListeners();
    }

    private void initViews() {
        tv_english = (TextView) findViewById(R.id.tv_english);
        tv_hindi = (TextView) findViewById(R.id.tv_hindi);
        tv_done = (TextView) findViewById(R.id.tv_done);
    }

    private void setClickListeners() {
        tv_english.setOnClickListener(onClickListener);
        tv_hindi.setOnClickListener(onClickListener);
        tv_done.setOnClickListener(onClickListener);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_english:
                        selectedLang = "en";
                        deselectAll();
                        tv_english.setBackgroundResource(R.color.app_theme_chat_box);
                        break;

                    case R.id.tv_hindi:
                        selectedLang = "hi";
                        deselectAll();
                        tv_hindi.setBackgroundResource(R.color.app_theme_chat_box);
                        break;

                    case R.id.tv_done:
                        preferenceUtils.setLocaleSettings(selectedLang);
                        Intent groupUpdate = new Intent(AppKeys.LANGUAGE_CHANGED);
                        LocalBroadcastManager.getInstance(LanguageSelectorActivity.this).sendBroadcast(groupUpdate);
                        commonFunctions.openProgressDialog();

                        tv_done.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                commonFunctions.closeProgressDialog();
                                finish();
                            }
                        }, 2000);

                        break;


                }
            }
        };
    }

    private void deselectAll() {
        tv_hindi.setBackgroundResource(R.color.white);
        tv_english.setBackgroundResource(R.color.white);
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.language));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(LanguageSelectorActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(LanguageSelectorActivity.this, R.color.status_bar_color));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
