package com.avadna.luneblaze.activities.organisation;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.payments.RazorPayPaymentActivity;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.assessment.PojoInitiatePaymentResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoSponsorPlanListData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SponsorPlanPaymentActivity extends AppBaseActivity {

    private PreferenceUtils preferenceUtils;
    private String user_id;
    private ApiInterface apiService;
    private CommonFunctions commonFunctions;

    private PojoSponsorPlanListData planData;
    private TextView tv_plan_name;
    private TextView tv_plan_price;
    private TextView tv_target_count;
    private TextView tv_per_student_cost;
    private TextView tv_proceed_payment;
    private String organisation_id;

    private final static int PAYMENT_REQUEST_CODE = 1234;

    private ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sponsor_plan_payment);
        setUpActionBar();
        preferenceUtils = new PreferenceUtils(this);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions = new CommonFunctions(this);
        organisation_id = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);

        Type type = new TypeToken<PojoSponsorPlanListData>() {
        }.getType();

        String dataStr = getIntent().getStringExtra("data");
        planData = new Gson().fromJson(dataStr, type);
        initViews();
        if (planData != null) {
            setDataOnViews();
        }
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.sponsorship_plans));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SponsorPlanPaymentActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SponsorPlanPaymentActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        tv_plan_name = (TextView) findViewById(R.id.tv_plan_name);
        tv_plan_price = (TextView) findViewById(R.id.tv_plan_price);
        tv_target_count = (TextView) findViewById(R.id.tv_target_count);
        tv_per_student_cost = (TextView) findViewById(R.id.tv_per_student_cost);
        tv_proceed_payment = (TextView) findViewById(R.id.tv_proceed_payment);
        tv_proceed_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateSponsorPayment(organisation_id, planData.id, planData.price);
                /*Intent paymentIntent = new Intent(SponsorPlanPaymentActivity.this,
                        InstamojoPaymentActivity.class);
                paymentIntent.putExtra(AppKeys.ORGANISATION_ID, organisation_id);
                paymentIntent.putExtra(AppKeys.PAYMENT_TYPE, AppKeys.SPONSORSHIP_PLAN);
                paymentIntent.putExtra(AppKeys.PRICE, "1");
                Type type = new TypeToken<PojoSponsorPlanListData>() {
                }.getType();
                String planDataStr = new Gson().toJson(planData, type);
                paymentIntent.putExtra("data", planDataStr);
                startActivityForResult(paymentIntent, PAYMENT_REQUEST_CODE);*/
            }
        });
    }


    private void setDataOnViews() {
        tv_plan_name.setText(planData.name);
        tv_plan_price.setText("₹" + planData.price);
        tv_target_count.setText(planData.nooftargetuser);
        float price = Float.parseFloat(planData.price);
        float target_count = Float.parseFloat(planData.nooftargetuser);
        int per = (int) (price / target_count);
        tv_per_student_cost.setText("₹" + String.valueOf(per));
    }


    private void initiateSponsorPayment(String sponsor_id, String planid, String amount) {
        Call<PojoInitiatePaymentResponse> call = apiService.initiateSponsorPayment(sponsor_id, planid, amount);
        commonFunctions.openProgressDialog(getString(R.string.please_wait));
        call.enqueue(new Callback<PojoInitiatePaymentResponse>() {
            @Override
            public void onResponse(Call<PojoInitiatePaymentResponse> call, Response<PojoInitiatePaymentResponse> response) {
                if (response.isSuccessful()) {
                    String message = "";
                    if (response.body() != null) {
                        startPaymentActivity(response, amount);
                    }
                } else {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.some_error_occured_contact_support), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                }
                commonFunctions.closeProgressDialog();
            }

            @Override
            public void onFailure(Call<PojoInitiatePaymentResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                commonFunctions.closeProgressDialog();

            }
        });
    }

    private void startPaymentActivity(Response<PojoInitiatePaymentResponse> response, String amount) {
        Intent paymentIntent = new Intent(SponsorPlanPaymentActivity.this,
                RazorPayPaymentActivity.class);
        if (response.body() != null) {
            paymentIntent.putExtra(AppKeys.PAYMENT_REMARKS, getString(R.string.post_boost_payment));
            paymentIntent.putExtra(AppKeys.ID, response.body().data);
            paymentIntent.putExtra(AppKeys.PRICE, amount);
            startActivityForResult(paymentIntent, PAYMENT_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PAYMENT_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                hitSetSponsorPlanApi(organisation_id, planData.id, planData.price, data);
            }
        }
    }


    private void hitSetSponsorPlanApi(String sponsor_id, String planid, String amount, Intent data) {
        Call<PojoNoDataResponse> call = apiService.setSponsorPlan(sponsor_id,
                data.getStringExtra(AppKeys.TRANSACTION_ID),
                data.getStringExtra(AppKeys.PAYMENT_STATUS), data.getStringExtra(AppKeys.ID),
                data.getStringExtra(AppKeys.PAYMENT_ID), planid, amount);
        commonFunctions.openProgressDialog(getString(R.string.updating_plan_do_not_close));
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().status == 1) {
                    if (data.getStringExtra(AppKeys.PAYMENT_STATUS).equals(AppKeys.PAYMENT_COMPLETED)) {
                        String message = response.body().message;
                        commonFunctions.setToastMessage(getApplicationContext(), message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        Button bt_ok;
                        final Dialog confirmationDialog;
                        confirmationDialog = new MyCustomThemeDialog(SponsorPlanPaymentActivity.this);
                        confirmationDialog.setContentView(R.layout.message_dialog);
                        TextView tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                        tv_dialog_description.setText(getString(R.string.sponser_plan_updated));
                        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);
                        bt_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmationDialog.dismiss();
                                setResult(RESULT_OK);
                                finish();
                            }
                        });
                        confirmationDialog.setCancelable(false);
                        confirmationDialog.show();
                    }
                } else {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.some_error_occured_contact_support), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                }
                commonFunctions.closeProgressDialog();
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                openNoConnectionDialog(data, getApplicationContext().getString(R.string.sponsorship_plans));

                commonFunctions.closeProgressDialog();

            }
        });
    }

    private void openNoConnectionDialog(Intent data, String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitSetSponsorPlanApi(organisation_id, planData.id, planData.price, data);
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
