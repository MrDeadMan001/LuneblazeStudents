package com.avadna.luneblaze.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.ContextCompat;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;

import java.net.URISyntaxException;

public class WebViewActivity extends AppBaseActivity {

    WebView webView;
    TextView et_url;
    ImageButton ib_back;
    ProgressBar progressBar;
    String origUrl = "https://www.google.com/";
    Toolbar tl_top_bar;
    CommonFunctions commonFunctions;
    PreferenceUtils preferenceUtils;


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view);
        commonFunctions = new CommonFunctions(this);
        preferenceUtils = new PreferenceUtils(this);

        Intent intent = getIntent();
        origUrl = getIntent().getStringExtra(AppKeys.URL);

        if (origUrl == null || origUrl.isEmpty()&&Intent.ACTION_VIEW.equals(intent.getAction())) {
            Uri uri = intent.getData();
            origUrl = uri.toString();
        }

        if (origUrl == null || origUrl.isEmpty()) {
            origUrl = "https://www.google.com/";
        }
        if (!origUrl.contains("http")) {
            origUrl = "http://" + origUrl;
        }
        initViews();
        setUpActionBar();
    }

    private void initViews() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tl_top_bar = (Toolbar) findViewById(R.id.tl_top_bar);

        et_url = (TextView) findViewById(R.id.et_url);
        et_url.setText(origUrl);
        et_url.setEnabled(false);

        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        initWebView();
    }

    private void setUpActionBar() {
        tl_top_bar = findViewById(R.id.tl_top_bar);
        setSupportActionBar(tl_top_bar);
        if (tl_top_bar != null) {
            tl_top_bar.setTitle(getString(R.string.fellows));
            tl_top_bar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(WebViewActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(WebViewActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initWebView() {
        webView = (WebView) findViewById(R.id.webview);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.contains("intent://")) {
                    Intent intent = null;
                    try {
                        intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    if (intent != null) {
                        String fallbackUrl = intent.getStringExtra("browser_fallback_url");
                        if (fallbackUrl != null) {
                            webView.loadUrl(fallbackUrl);
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
                if (!URLUtil.isNetworkUrl(url)) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap facIcon) {
                //SHOW LOADING IF IT ISNT ALREADY VISIBLE
                et_url.setText(url);
                super.onPageStarted(view, url, facIcon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                et_url.setText(url);
                super.onPageFinished(view, url);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.loadUrl(et_url.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.webview_activity_side_options, menu);
        if (preferenceUtils.getUserLoginData() == null||commonFunctions.isGuestUser()) {
            menu.removeItem(R.id.share_to);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_to:
                ShareContentOnMediaApps.shareContent(WebViewActivity.this, AppKeys.SHARE_TEXT,
                        et_url.getText().toString(), "", null);
                break;

            case R.id.copy_url:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getString(R.string.url), et_url.getText().toString());
                clipboard.setPrimaryClip(clip);
                commonFunctions.setToastMessage(WebViewActivity.this, getString(R.string.url_copied_to_clipboard)
                        , Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

}


