package com.avadna.luneblaze.activities.payments;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoSponsorPlanListData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.rest.PojoCreatInstamojoOrderResponse;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.instamojo.android.Instamojo;
import com.razorpay.Checkout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Response;

public class InstamojoPaymentActivity extends AppCompatActivity implements Instamojo.InstamojoPaymentCallback{

    TextInputLayout til_name, til_email, til_phone;
    EditText et_name, et_email, et_phone;
    ImageButton ib_clear_name, ib_clear_email, ib_clear_phone;
    TextView tv_proceed;
    ActionBar actionBar;
    ApiInterface apiService;


    PojoLoginResponseData userLoginData;

    View.OnClickListener onClickListener;

    private static final HashMap<Instamojo.Environment, String> env_options = new HashMap<>();

    static {
        env_options.put(Instamojo.Environment.TEST, "https://test.instamojo.com/");
        env_options.put(Instamojo.Environment.PRODUCTION, "https://api.instamojo.com/");
    }

    private Instamojo.Environment mCurrentEnv = Instamojo.Environment.PRODUCTION;


    PreferenceUtils preferenceUtils;
    String user_id;
    String organisation_id;
    CommonFunctions commonFunctions;
    String paymentType;

    PojoSponsorPlanListData planData;

    String orderid = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payer_details);
        setUpActionBar();
        Checkout.preload(getApplicationContext());

        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        user_id = preferenceUtils.get_user_id();
        commonFunctions = new CommonFunctions(this);
        organisation_id = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);
        paymentType = getIntent().getStringExtra(AppKeys.PAYMENT_TYPE);

        userLoginData = preferenceUtils.getUserLoginData();

        if (paymentType != null && paymentType.equals(AppKeys.SPONSORSHIP_PLAN)) {
            Type type = new TypeToken<PojoSponsorPlanListData>() {
            }.getType();
            String dataStr = getIntent().getStringExtra("data");
            planData = new Gson().fromJson(dataStr, type);
        }

        initViews();
        initClickListeners();
        setClickListeners();
        setTextChangeListener();

        Instamojo.getInstance().initialize(this, mCurrentEnv);

    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.payment));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(InstamojoPaymentActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(InstamojoPaymentActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void initViews() {
        til_name = (TextInputLayout) findViewById(R.id.til_name);
        til_email = (TextInputLayout) findViewById(R.id.til_email);
        til_phone = (TextInputLayout) findViewById(R.id.til_phone);

        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_phone = (EditText) findViewById(R.id.et_phone);

        if (userLoginData != null) {
            if (userLoginData.userFullname != null) {
                et_name.setText(userLoginData.userFullname);
            }
            if (userLoginData.userEmail != null) {
                et_email.setText(userLoginData.userEmail);
            }
            if (userLoginData.userPhone != null) {
                et_phone.setText(userLoginData.userPhone);
            }
        }

        ib_clear_name = (ImageButton) findViewById(R.id.ib_clear_name);
        ib_clear_email = (ImageButton) findViewById(R.id.ib_clear_email);
        ib_clear_phone = (ImageButton) findViewById(R.id.ib_clear_phone);
        tv_proceed = (TextView) findViewById(R.id.tv_proceed);

        String price="";

        switch (paymentType){
            case AppKeys.SPONSORSHIP_PLAN:
                if(planData!=null){
                    price=planData.price;
                }
                break;

            case AppKeys.CAMPUS_DRIVE_PAYMENT:
                price=getIntent().getStringExtra(AppKeys.PRICE);
                break;

            case AppKeys.POST_BOOST_PAYMENT:
                price=getIntent().getStringExtra(AppKeys.PRICE);
                break;

            case AppKeys.ASSESSMENT_PAYMENT:
                price= getIntent().getStringExtra(AppKeys.PRICE);
                break;

                default:
                    price="";
                    break;
            }
            tv_proceed.setText(getString(R.string.proceed_to_pay)+" ₹"+price);

    }

    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_proceed:
                        if (validateData()) {
                            switch (paymentType) {
                                case AppKeys.SPONSORSHIP_PLAN:
                                    if (planData != null) {
                                        String desc = "Org" + organisation_id
                                                + " payment plan id " + planData.id;
                                        // startPayment(et_name.getText().toString().trim(), planData.price, desc);
                                        createOrderOnServer(user_id, et_name.getText().toString().trim(),
                                                et_email.getText().toString().trim(),
                                                et_phone.getText().toString().trim(),
                                                planData.price, desc);
                                    }
                                    break;

                                case AppKeys.CAMPUS_DRIVE_PAYMENT: {
                                    String desc = "Org" + organisation_id
                                            + " payment Drive";
                                    String price = getIntent().getStringExtra(AppKeys.PRICE);
                                    // startPayment(et_name.getText().toString().trim(),AppKeys.CAMPUS_DRIVE_PRICE, desc);
                                    createOrderOnServer(user_id, et_name.getText().toString().trim(),
                                            et_email.getText().toString().trim(),
                                            et_phone.getText().toString().trim(),
                                            price, desc);
                                }

                                break;

                                case AppKeys.POST_BOOST_PAYMENT: {
                                    String postId = getIntent().getStringExtra(AppKeys.POST_ID);
                                    String price = getIntent().getStringExtra(AppKeys.PRICE);
                                    if (postId != null && price != null) {
                                        String desc = "Org " + organisation_id
                                                + " payment post " + postId;
                                        //startPayment(et_name.getText().toString().trim(), "10", desc);
                                        createOrderOnServer(user_id, et_name.getText().toString().trim(),
                                                et_email.getText().toString().trim(),
                                                et_phone.getText().toString().trim(),
                                                price, desc);
                                    }
                                }
                                case AppKeys.ASSESSMENT_PAYMENT: {
                                    String price = getIntent().getStringExtra(AppKeys.PRICE);
                                    String username= preferenceUtils.getUserLoginData().userFullname;
                                    if (price != null) {
                                        String desc = "user " + username +" id "+user_id
                                                + " payment assessment ";
                                        //startPayment(et_name.getText().toString().trim(), "10", desc);
                                        createOrderOnServer(user_id, et_name.getText().toString().trim(),
                                                et_email.getText().toString().trim(),
                                                et_phone.getText().toString().trim(),
                                                price, desc);
                                    }
                                }

                                break;
                            }
                        }
                        break;

                    case R.id.ib_clear_name:
                        et_name.getText().clear();
                        break;

                    case R.id.ib_clear_email:
                        et_email.getText().clear();
                        break;

                    case R.id.ib_clear_phone:
                        et_phone.getText().clear();
                        break;
                }
            }
        };
    }

    public void startPayment(String name, String price, String desc) {
        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();

        /**
         * Set your logo here
         */
        checkout.setImage(R.drawable.rzp_logo);

        /**
         * Reference to current activity
         */
        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            /**
             * Merchant Name
             * eg: ACME Corp || HasGeek etc.
             */
            options.put("name", name);

            /**
             * Description can be anything
             * eg: Order #123123 - This order number is passed by you for your internal reference. This is not the `razorpay_order_id`.
             *     Invoice Payment
             *     etc.
             */
            options.put("description", desc);
            this.orderid = orderid;
            options.put("order_id", this.orderid);
            options.put("currency", "INR");

            /**
             * Amount is always passed in currency subunits
             * Eg: "500" = INR 5.00
             */
            options.put("amount", price);

            checkout.open(activity, options);
        } catch (Exception e) {
            Log.e("RAZORPE", "Error in starting Razorpay Checkout", e);
        }
    }


    private void setTextChangeListener() {
        et_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_email.setError(null);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_email.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_phone.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean validateData() {
        boolean valid = true;
        String name = et_name.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();

        if (name.isEmpty()) {
            valid = false;
            til_name.setError(getString(R.string.please_enter_name));
        } else {
            til_name.setError(null);
        }

        if (!isValidEmail(email)) {
            valid = false;
            til_email.setError(getString(R.string.please_enter_valid_email));
        } else {
            til_email.setError(null);
        }

        if (Patterns.PHONE.matcher(phone).matches() || phone.length() == 0 || phone.length() == 10) {
            til_email.setError(null);
        } else {
            til_email.setError(getString(R.string.please_enter_valid_phone));
            valid = false;
        }

        return valid;
    }


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    private void setClickListeners() {
        tv_proceed.setOnClickListener(onClickListener);
        ib_clear_name.setOnClickListener(onClickListener);
        ib_clear_email.setOnClickListener(onClickListener);
        ib_clear_phone.setOnClickListener(onClickListener);
    }

    private void createOrderOnServer(String user_id, String name, String email, String phone, String amount,
                                     String purpose) {
        String orderID = UUID.randomUUID().toString();
        String transactionID = String.valueOf(new Date().getTime())+user_id;
       /* if (true) {
            Log.d("payment", "Payment complete");
            Intent paymentData = new Intent();
            paymentData.putExtra(AppKeys.ORDER_ID, orderID);
            paymentData.putExtra(AppKeys.TRANSACTION_ID, transactionID);
            paymentData.putExtra(AppKeys.PAYMENT_ID, "test id");
            paymentData.putExtra(AppKeys.PAYMENT_STATUS, "test");
            setResult(RESULT_OK, paymentData);
            finish();
        } else*/ {
            String currency = "INR";
            Call<PojoCreatInstamojoOrderResponse> getOrderIDCall = apiService.createInstaMojoOrder(user_id, name, email,
                    phone, amount, purpose, transactionID, currency);
            commonFunctions.openProgressDialog();

            getOrderIDCall.enqueue(new retrofit2.Callback<PojoCreatInstamojoOrderResponse>() {
                @Override
                public void onResponse(Call<PojoCreatInstamojoOrderResponse> call, Response<PojoCreatInstamojoOrderResponse> response) {
                    commonFunctions.closeProgressDialog();
                    if (response.isSuccessful()) {
                        String orderId = response.body().data.orderId;
                        // Initiate the default SDK-provided payment activity
                        initiateSDKPayment(orderId);
                    } else {
                        // Handle api errors
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            Log.d("Payment", "Error in response" + jObjError.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<PojoCreatInstamojoOrderResponse> call, Throwable t) {
                    // Handle call failure
                    commonFunctions.closeProgressDialog();
                    Log.d("Payment", "Failure");
                }
            });
        }


    }


    private void initiateSDKPayment(String orderID) {
        Instamojo.getInstance().initiatePayment(this, orderID, this);
    }


    @Override
    public void onInstamojoPaymentComplete(String orderID, String transactionID, String paymentID, String paymentStatus) {
        Log.d("payment", "Payment complete");
        Intent paymentData = new Intent();
        paymentData.putExtra(AppKeys.ORDER_ID, orderID);
        paymentData.putExtra(AppKeys.TRANSACTION_ID, transactionID);
        paymentData.putExtra(AppKeys.PAYMENT_ID, paymentID);
        paymentData.putExtra(AppKeys.PAYMENT_STATUS, paymentStatus);
        setResult(RESULT_OK, paymentData);
        finish();
    }


    @Override
    public void onPaymentCancelled() {
        Log.d("payment", "Payment cancelled");
    }

    @Override
    public void onInitiatePaymentFailure(String errorMessage) {
        Log.d("payment", "Payment failure due to " + errorMessage);
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
