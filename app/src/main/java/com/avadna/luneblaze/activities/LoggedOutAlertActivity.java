

package com.avadna.luneblaze.activities;

import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoLogoutResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoggedOutAlertActivity extends AppBaseActivity {

    TextView tv_dialog_description, tv_yes, tv_no;

    ApiInterface apiService;

    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_out_alert);
        preferenceUtils = new PreferenceUtils(this);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions = new CommonFunctions(this);

        initViews();
        PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();

        if (pojoLoginResponseData != null) {
            hitLogoutApi(preferenceUtils.get_user_id());
        }

    }

    private void initViews() {
        tv_yes = (TextView) findViewById(R.id.tv_yes);
        tv_no = (TextView) findViewById(R.id.tv_no);
        tv_dialog_description = (TextView) findViewById(R.id.tv_dialog_description);

        tv_dialog_description.setText(getString(R.string.logged_out_from_other_device));

        tv_yes.setText(getString(R.string.login));
        tv_no.setText(getString(R.string.exit));

        tv_no.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                preferenceUtils.logOut();
                finishAffinity();
                finishAndRemoveTask();
                System.exit(0);
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registerActivityIntent = new Intent(LoggedOutAlertActivity.this, LoginActivity.class);
                registerActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(registerActivityIntent);
            }
        });
    }


    private void hitLogoutApi(String user_id) {
        final String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
        if(pojoLoginResponseData!=null&&pojoLoginResponseData.sessionId!=null){
            Call<PojoLogoutResponse> call = apiService.logout(user_id, android_id, pojoLoginResponseData.sessionId.toString());
            call.enqueue(new Callback<PojoLogoutResponse>() {
                @Override
                public void onResponse(Call<PojoLogoutResponse> call, Response<PojoLogoutResponse> response) {
                    if (response != null && response.body() != null) {
                        String message = response.body().message;
                        if (response.body().data != null) {
                            //  commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        /*confirmationDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        preferenceUtils.logOut();*/
                        }
                    }
                }

                @Override
                public void onFailure(Call<PojoLogoutResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            });
        }

    }


}
