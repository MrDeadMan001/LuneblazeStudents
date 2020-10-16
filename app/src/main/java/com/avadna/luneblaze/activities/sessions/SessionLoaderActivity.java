package com.avadna.luneblaze.activities.sessions;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.utils.NotificationUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionLoaderActivity extends AppBaseActivity {
    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String session_id = "104";
    String tabType;
    String action;
    //this is the android native notif id
    int notifId;
    PojoSessionDetailsResponseData pojoSessionDetailsResponseData;
    Call<PojoSessionDetailsResponse> call;

    //this is the api related notification id
    String notification_id;
    Response<PojoSessionDetailsResponse> globalResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_loader);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions = new CommonFunctions(this);
        preferenceUtils = new PreferenceUtils(this);
        user_id = preferenceUtils.get_user_id();

        tabType = getIntent().getStringExtra("type");
        session_id = getIntent().getStringExtra("id");
        if (session_id == null) {
            session_id = "104";
        }
        action = getIntent().getStringExtra("action");
        notifId = getIntent().getIntExtra("notifId", 999999);
        dismissNotification();

        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }

        setUpActionBar();
        hitGetSessionDetailsApi(user_id, session_id, "home");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (globalResponse != null) {
            handleApiResponse(globalResponse);
            globalResponse = null;
        }

    }

    private void dismissNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(notifId);


    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SessionLoaderActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SessionLoaderActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void hitGetSessionDetailsApi(String user_id, final String session_id, String view) {
        call = apiService.getSessionDetails(user_id, session_id, view, "",
                "", "", "0");
        call.enqueue(new Callback<PojoSessionDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().data != null) {
                        if (NotificationUtils.isAppIsInBackground(SessionLoaderActivity.this)) {
                            globalResponse = response;
                        } else {
                            handleApiResponse(response);
                        }
                    } else if (response.body().message.contains("deleted")) {
                        commonFunctions.showSessionDeactivated(SessionLoaderActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(SessionLoaderActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.session));

            }
        });
    }

    public void handleApiResponse(Response<PojoSessionDetailsResponse> response) {
        String message = "";

        message = response.body().message;
        pojoSessionDetailsResponseData = response.body().data;
//                    commonFunctions.setToastMessage(SessionLoaderActivity.this, message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
        String dataStr = new Gson().toJson(
                pojoSessionDetailsResponseData,
                new TypeToken<PojoSessionDetailsResponseData>() {
                }.getType());
        Intent intent;

        if (pojoSessionDetailsResponseData.status.equals("1")) {
            intent = new Intent(SessionLoaderActivity.this, VerifiedSessionActivity.class);
        } else if (pojoSessionDetailsResponseData.status.equals("2")) {
            intent = new Intent(SessionLoaderActivity.this, VerifiedSessionActivity.class);
        } else if (pojoSessionDetailsResponseData.status.equals("3")) {
            intent = new Intent(SessionLoaderActivity.this, AssignedSessionActivity.class);
        } else {
            intent = new Intent(SessionLoaderActivity.this, OrganisedSessionActivity.class);

        }

        if (tabType != null && tabType.equals("discussion")) {
            String discussion_id = getIntent().getStringExtra("discussion_id");
            if (discussion_id != null) {
                intent.putExtra("discussion_id", discussion_id);
            }
        }

        intent.putExtra("id", session_id);
        intent.putExtra("data", dataStr);
        intent.putExtra("type", tabType);
        intent.putExtra("action", action);
        intent.putExtra("notifId", notifId);

        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        if (!SessionLoaderActivity.this.isFinishing()) {
            startActivityForResult(intent, 0);
        }
        overridePendingTransition(0, 0);
        finish();
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetSessionDetailsApi(user_id, session_id, "home");
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
            e.printStackTrace();
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


    @Override
    protected void onPause() {
        super.onPause();

    }
}
