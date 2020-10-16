package com.avadna.luneblaze.activities;

import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AlertDialog;

import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLatestVesionResponse;
import com.avadna.luneblaze.pojo.PojoLoggedInDevicesResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.firebase.FirebaseApp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.System.exit;

public class AppInitialiserActivity extends AppBaseActivity {
    public static final int MY_PERMISSIONS_REQUEST_WRITE_FIELS = 102;
    AlertDialog dialog;
    ApiInterface apiService;

    CommonFunctions commonFunctions;
    PreferenceUtils preferenceUtils;

    RelativeLayout rl_images_wrapper;
    ImageView iv_inner, iv_middle, iv_outer, iv_logo_text;

    TabLayout tl_pager_dots;
    Handler radioButtonHandler;
    Runnable radioButtonRunnable;
    int onlineStatusDelay = 400;
    int counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_app_initialiser);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(AppInitialiserActivity.this);
        commonFunctions = new CommonFunctions(this);
        radioButtonHandler = new Handler();


      /*  FontRequest fontRequest = new FontRequest(
                "com.avadna.luneblaze.fontprovider",
                "com.avadna.luneblaze",
                "emoji compat Font Query",
                R.array.com_google_android_gms_fonts_certs);
        EmojiCompat.Config config = new FontRequestEmojiCompatConfig(this, fontRequest);
        EmojiCompat.init(config);*/
        initViews();
        // initAnimations();
        // checkAppPermissions();
        checkVersionCompatibility();
    }

    private void initViews() {
        rl_images_wrapper = (RelativeLayout) findViewById(R.id.rl_images_wrapper);

        iv_outer = (ImageView) findViewById(R.id.iv_outer);
        iv_logo_text = (ImageView) findViewById(R.id.iv_logo_text);

        tl_pager_dots = (TabLayout) findViewById(R.id.tl_pager_dots);

        tl_pager_dots.addTab(tl_pager_dots.newTab());
        tl_pager_dots.addTab(tl_pager_dots.newTab());
        tl_pager_dots.addTab(tl_pager_dots.newTab());
        tl_pager_dots.addTab(tl_pager_dots.newTab());
        tl_pager_dots.addTab(tl_pager_dots.newTab());

        radioButtonRunnable = new Runnable() {
            public void run() {
                //buttonList.get(counter % 5).setChecked(true);
                TabLayout.Tab tab = tl_pager_dots.getTabAt(counter % 5);
                tab.select();
                counter++;
                radioButtonHandler.postDelayed(this, onlineStatusDelay);
            }
        };
        radioButtonHandler.postDelayed(radioButtonRunnable, 500);
    }

    private void initAnimations() {
        initRotateImageWrapperAnim();
    }

    private void initRotateImageWrapperAnim() {
        AnimationSet animSet = new AnimationSet(true);
        animSet.setInterpolator(new LinearInterpolator());
        final RotateAnimation animRotate = new RotateAnimation(0.0f, 360.0f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animRotate.setDuration(8000);
        animRotate.setRepeatCount(Animation.INFINITE);
        animRotate.setRepeatMode(Animation.INFINITE);


        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.4f);
        alphaAnimation.setDuration(5000);
        alphaAnimation.setRepeatCount(Animation.INFINITE);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        animSet.addAnimation(animRotate);
        animSet.addAnimation(alphaAnimation);
        rl_images_wrapper.startAnimation(animSet);
        iv_logo_text.startAnimation(alphaAnimation);

    }


    private void checkVersionCompatibility() {
        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        final int currentAppVersionCode = pInfo.versionCode;

        // use below code to use debug version checker
/*
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(50, TimeUnit.SECONDS)
                .connectTimeout(50, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://strotam.com/api/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();

        ApiInterface debugApiService = retrofit.create(ApiInterface.class);*/

        Call<PojoLatestVesionResponse> call = apiService.getCurrentVersion(String.valueOf(currentAppVersionCode));
        call.enqueue(new Callback<PojoLatestVesionResponse>() {
            @Override
            public void onResponse(Call<PojoLatestVesionResponse> call, Response<PojoLatestVesionResponse> response) {
                String message = "";
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    if (currentAppVersionCode < Integer.parseInt(response.body().data.version) && !response.body().data.compatible) {
                        openNotCompatibleDialog();
                    } else {
                        goNext();
                    }
                } else {
                    openNoConnectionDialog("");
                }
            }

            @Override
            public void onFailure(Call<PojoLatestVesionResponse> call, Throwable t) {
                // Log error here since request failed
                openNoConnectionDialog("");
            }
        });
    }


    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    checkVersionCompatibility();
                    connectionErrorDialog.dismiss();
                }

                @Override
                public void onBack() {
                    connectionErrorDialog.dismiss();
                    finish();
                }
            });
            connectionErrorDialog.show();
            connectionErrorDialog.setTitle(title);

        } catch (Exception e) {

        }
    }

    public void goNext() {
        if (!AppInitialiserActivity.this.isFinishing()) {
            if (preferenceUtils.showAppTour()) {
                Intent introVideoIntent = new Intent(AppInitialiserActivity.this, IntroVideoActivity.class);
                startActivity(introVideoIntent);
                finish();
               /* Intent tourIntent = new Intent(AppInitialiserActivity.this, AppTourActivity.class);
                startActivity(tourIntent);
                finish();*/
            } else if (preferenceUtils.is_user_login()&&!preferenceUtils.get_user_id().equals(AppKeys.GUEST_USER_ID)) {
                Intent mainIntent = new Intent(AppInitialiserActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            } else {
                Intent loginIntent = new Intent(AppInitialiserActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        }

    }

    public void openNotCompatibleDialog() {
        TextView tv_dialog_description, tv_yes, tv_no;
        final Dialog confirmationDialog = new MyCustomThemeDialog(AppInitialiserActivity.this, R.style.NoTitleDialogTheme);
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
        tv_yes.setText(getString(R.string.update));
        tv_no.setText(getString(R.string.exit));

        tv_dialog_description.setText(getString(R.string.app_no_longer_supported));
        confirmationDialog.setCancelable(false);

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
                exit(0);
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                confirmationDialog.dismiss();
                finish();


            }
        });
        confirmationDialog.show();
    }

    private void hitGetLoginDevicesApi(String email) {
        final String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Call<PojoLoggedInDevicesResponse> call = apiService.getLoggedInDevices(email);
        call.enqueue(new Callback<PojoLoggedInDevicesResponse>() {
            @Override
            public void onResponse(Call<PojoLoggedInDevicesResponse> call, Response<PojoLoggedInDevicesResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    commonFunctions.closeProgressDialog();
                    if (!response.body().devices.isEmpty() && response.body().devices.get(0).equals(android_id)) {
                        Intent mainIntent = new Intent(AppInitialiserActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                    } else {
                        Intent loginIntent = new Intent(AppInitialiserActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoLoggedInDevicesResponse> call, Throwable t) {
                // Log error here since request failed
            }
        });
    }

    public void openPermissionScreen() {
        // startActivityForResult(new Intent(android.provider.Settings.ACTION_SETTINGS), 0);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", AppInitialiserActivity.this.getPackageName(), null));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    @Override
    protected void onPause() {
        super.onPause();
        radioButtonHandler.removeCallbacks(radioButtonRunnable);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);

    }

    @Override
    protected void onDestroy() {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }

}
