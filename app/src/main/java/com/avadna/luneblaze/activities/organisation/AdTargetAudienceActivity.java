package com.avadna.luneblaze.activities.organisation;

import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.payments.RazorPayPaymentActivity;
import com.avadna.luneblaze.activities.registration.InterestHierarchyActivity;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.helperClasses.AES;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.assessment.PojoInitiatePaymentResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetAdTargetsResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.apptik.widget.MultiSlider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdTargetAudienceActivity extends AppBaseActivity implements
        TaggedInterestAdapter.TaggedInterestAdapterCallback {
    private ActionBar actionBar;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String organisation_id;
    private String post_id;
    private ApiInterface apiService;

    private TextView tv_age_group;
    private MultiSlider ms_age_group;
    private EditText et_location;
    private ImageButton ib_location_picker;
    private ImageButton ib_clear_location;
    private TextView tv_distance;
    private SeekBar sb_distance;
    private TextView tv_projected_targets;
    private RadioButton rb_any, rb_male, rb_female, rb_other;
    private ProgressBar pb_loading_results;

    private RecyclerView rv_added_interests;
    private TaggedInterestAdapter taggedInterestAdapter;
    private TextView tv_interests;
    private TextView tv_remove_interests;
    private GridLayoutManager gridLayoutManager;
    private TextView tv_length_getter;
    private DisplayMetrics displayMetrics;
    private TextView tv_cost_message;
    private TextView total_cost;
    private TextView tv_proceed_payment;
    private List<PojoGetInterestListResponseDataListItem> selectedInterestList;
    private int screenHeight, screenWidth;

    private LinearLayout ll_distance_wrapper;

    private static final int PLACE_PICKER_REQUEST = 1234;
    private static final int SELECT_INTEREST_CODE = 2;

    private final static int PAYMENT_REQUEST_CODE = 2345;


    private View.OnClickListener onClickListener;
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    private int minAge = 14; //years
    private int maxAge = 100;
    private int radius = 50; //km
    private double latitude;
    private double longitude;
    private int totalCost;

    private String gender = "";
    private boolean isGetUserCalled = false;
    private int perDayCost = AppKeys.BOOST_COST_HIGH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_target_audiance);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        user_id = preferenceUtils.get_user_id();
        organisation_id = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);
        post_id = getIntent().getStringExtra(AppKeys.POST_ID);
        initDispMetrics();
        setUpActionBar();
        initViews();
        initClickListener();
        setClickListener();
        getAdsTargetCount(selectedInterestList, minAge, maxAge, latitude, longitude, radius, gender);
    }


    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            //   actionBar.setTitle(getString(R.string.comments) + " (" + comment_count + ")");
            actionBar.setTitle(getString(R.string.target_criteria));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(AdTargetAudienceActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(AdTargetAudienceActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        tv_age_group = (TextView) findViewById(R.id.tv_age_group);
        ms_age_group = (MultiSlider) findViewById(R.id.ms_age_group);
        et_location = (EditText) findViewById(R.id.et_location);
        ib_location_picker = (ImageButton) findViewById(R.id.ib_location_picker);
        ib_clear_location = (ImageButton) findViewById(R.id.ib_clear_location);
        tv_distance = (TextView) findViewById(R.id.tv_distance);
        sb_distance = (SeekBar) findViewById(R.id.sb_distance);
        rb_any = (RadioButton) findViewById(R.id.rb_any);
        rb_male = (RadioButton) findViewById(R.id.rb_male);
        rb_female = (RadioButton) findViewById(R.id.rb_female);
        rb_other = (RadioButton) findViewById(R.id.rb_other);
        tv_interests = (TextView) findViewById(R.id.tv_interests);
        tv_remove_interests = (TextView) findViewById(R.id.tv_remove_interests);
        tv_length_getter = (TextView) findViewById(R.id.tv_length_getter);
        tv_projected_targets = (TextView) findViewById(R.id.tv_projected_targets);
        pb_loading_results = (ProgressBar) findViewById(R.id.pb_loading_results);
        ll_distance_wrapper = (LinearLayout) findViewById(R.id.ll_distance_wrapper);
        ll_distance_wrapper.setVisibility(View.GONE);

        tv_projected_targets.setVisibility(View.GONE);
        tv_proceed_payment = (TextView) findViewById(R.id.tv_proceed_payment);
        total_cost = (TextView) findViewById(R.id.total_cost);
        tv_cost_message = (TextView) findViewById(R.id.tv_cost_message);
        pb_loading_results.setVisibility(View.GONE);
        rv_added_interests = (RecyclerView) findViewById(R.id.rv_added_interests);
        initAddedInterestList();

        ms_age_group.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider, MultiSlider.Thumb thumb, int thumbIndex, int value) {
                if (thumbIndex == 0) {
                    minAge = value;
                }
                if (thumbIndex == 1) {
                    maxAge = value;
                }

                tv_age_group.setText("( " + String.valueOf(minAge) + "-" + String.valueOf(maxAge) + " )");
                getAdsTargetCount(selectedInterestList, minAge, maxAge, latitude, longitude, radius, gender);
            }
        });

        sb_distance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                radius = progress * 10;
                tv_distance.setText(radius + "Km");
                getAdsTargetCount(selectedInterestList, minAge, maxAge, latitude, longitude, radius, gender);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initAddedInterestList() {
        selectedInterestList = new ArrayList<>();
        tv_length_getter = (TextView) findViewById(R.id.tv_length_getter);
        rv_added_interests = (RecyclerView) findViewById(R.id.rv_added_interests);
        updateSelectedInterestList();
    }

    private void updateSelectedInterestList() {
        gridLayoutManager = (new GridLayoutManager(AdTargetAudienceActivity.this, 200));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (!selectedInterestList.isEmpty()) {
                    tv_length_getter.setText(selectedInterestList.get(position).text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();
                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        14, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (screenWidth * 0.9f);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });

        rv_added_interests.setLayoutManager(gridLayoutManager);
        taggedInterestAdapter = new TaggedInterestAdapter(selectedInterestList.size(),
                AdTargetAudienceActivity.this, selectedInterestList, 1);
        rv_added_interests.setAdapter(taggedInterestAdapter);
    }

    private void setClickListener() {
        ib_location_picker.setOnClickListener(onClickListener);
        ib_clear_location.setOnClickListener(onClickListener);
        et_location.setOnClickListener(onClickListener);
        tv_interests.setOnClickListener(onClickListener);
        tv_remove_interests.setOnClickListener(onClickListener);
        rb_male.setOnCheckedChangeListener(onCheckedChangeListener);
        rb_any.setOnCheckedChangeListener(onCheckedChangeListener);
        rb_female.setOnCheckedChangeListener(onCheckedChangeListener);
        rb_other.setOnCheckedChangeListener(onCheckedChangeListener);
        tv_proceed_payment.setOnClickListener(onClickListener);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {

                    case R.id.tv_interests: {
                        Intent pickInterestIntent = new Intent(AdTargetAudienceActivity.this,
                                InterestHierarchyActivity.class);
                        Type type = new TypeToken<ArrayList<PojoGetInterestListResponseDataListItem>>() {
                        }.getType();
                        Gson gson = new Gson();
                        String dataStr = gson.toJson(selectedInterestList, type);
                        pickInterestIntent.putExtra("data", dataStr);
                        pickInterestIntent.putExtra("type", AppKeys.SELECT_INTEREST);
                        startActivityForResult(pickInterestIntent, SELECT_INTEREST_CODE);
                    }
                    break;

                    case R.id.tv_remove_interests: {
                        selectedInterestList.clear();
                        taggedInterestAdapter.notifyDataSetChanged();
                        getAdsTargetCount(selectedInterestList, minAge, maxAge, latitude, longitude,
                                radius, gender);
                    }
                    break;

                    case R.id.tv_proceed_payment: {
                        totalCost = AppKeys.AD_DAYS * perDayCost;
                        //todo revert cost after testing
                        totalCost=1;
                        hitInitiateAdPaymentApi(organisation_id, user_id, String.valueOf(totalCost), post_id);
                    }
                    break;

                    case R.id.et_location:
                    case R.id.ib_location_picker:
                        if (!Places.isInitialized()) {
                            Places.initialize(getApplicationContext(),
                                    AES.decrypt(AppKeys.PLACES_API_KEY, AppKeys.enKey));
                        }
                        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS,
                                Place.Field.LAT_LNG, Place.Field.NAME);

                        Intent intent = new Autocomplete.IntentBuilder(
                                AutocompleteActivityMode.FULLSCREEN, fields)
                                .build(AdTargetAudienceActivity.this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                        break;

                    case R.id.ib_clear_location:
                        latitude = 0;
                        longitude = 0;
                        et_location.getText().clear();
                        ll_distance_wrapper.setVisibility(View.GONE);
                        break;


                }
            }
        };

        onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateGenderValue();
                getAdsTargetCount(selectedInterestList, minAge, maxAge, latitude, longitude, radius, gender);
            }
        };
    }

    private void updateGenderValue() {
        if (rb_other.isChecked()) {
            gender = "other";
        } else if (rb_male.isChecked()) {
            gender = "male";
        } else if (rb_female.isChecked()) {
            gender = "female";
        } else if (rb_any.isChecked()) {
            gender = "";
        }
    }


    private void getAdsTargetCount(List<PojoGetInterestListResponseDataListItem> interestList,
                                   int localMinAge, int localMaxAge, double localLatitude,
                                   double localLongitude, int localRadius, String localGender) {
        if (!isGetUserCalled) {
            isGetUserCalled = true;
            HashMap<String, String> interestMap = new HashMap<>();
            for (int i = 0; i < interestList.size(); i++) {
                interestMap.put("interests[" + i + "]", interestList.get(i).interestId);
            }
            //tv_no_questions.setVisibility(View.GONE);
            pb_loading_results.setVisibility(View.VISIBLE);
            tv_projected_targets.setVisibility(View.GONE);
            String latStr = null;

            if (localLatitude != 0) {
                latStr = String.valueOf(localLatitude);
            }

            String longStr = null;
            if (localLongitude != 0) {
                longStr = String.valueOf(localLongitude);
            }

            if (localGender == null || localGender.isEmpty()) {
                localGender = null;
            }

            String radiusStr = String.valueOf(localRadius);
            if (localLatitude == 0 || localLongitude == 0) {
                radiusStr = null;
            }

            Call<PojoGetAdTargetsResponse> call =
                    apiService.getAdsTargetCount(interestMap, String.valueOf(localMinAge), String.valueOf(localMaxAge),
                            latStr, longStr, radiusStr, localGender);
            String finalGender = localGender;
            call.enqueue(new Callback<PojoGetAdTargetsResponse>() {
                @Override
                public void onResponse(Call<PojoGetAdTargetsResponse> call,
                                       Response<PojoGetAdTargetsResponse> response) {
                    String message = "";
                    isGetUserCalled = false;

                    if (response != null && response.body() != null && response.body().data != null) {
                        if (!areParamsSame(interestList, localMinAge, localMaxAge, localLatitude, localLongitude,
                                localRadius, finalGender)) {
                            pb_loading_results.setVisibility(View.VISIBLE);
                            tv_projected_targets.setVisibility(View.GONE);
                            getAdsTargetCount(selectedInterestList, minAge, maxAge, latitude, longitude,
                                    radius, gender);
                        } else {
                            message = response.body().message;
                            pb_loading_results.setVisibility(View.GONE);
                            tv_projected_targets.setVisibility(View.VISIBLE);
                            tv_projected_targets.setText("" + response.body().data);

                            if (response.body().data < 100) {
                                perDayCost = AppKeys.BOOST_COST_LOW;
                            } else {
                                perDayCost = AppKeys.BOOST_COST_HIGH;
                            }
                            int totalCost = AppKeys.AD_DAYS * perDayCost;

                            total_cost.setText(getString(R.string.total_cost) + " ₹" + totalCost);

                            String noteMessage = getString(R.string.your_add_will_run_five_days)
                                    + " and cost ₹" + perDayCost + " per day.";
                            tv_cost_message.setText(noteMessage);

                        }
                    }
                    pb_loading_results.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<PojoGetAdTargetsResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (AdTargetAudienceActivity.this != null)
                        commonFunctions.setToastMessage(getApplicationContext(),
                                t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    //  ll_parent.setVisibility(View.VISIBLE);
                    isGetUserCalled = false;
                }
            });

        }
    }

    public void hitInitiateAdPaymentApi(String organisation_id, String user_id, String amount,
                                        String ad_id) {
        Call<PojoInitiatePaymentResponse> call =
                apiService.initiateAdPayment(organisation_id, user_id, amount, ad_id);
        commonFunctions.openProgressDialog("Please wait... updating payment status. Do not close this page.");
        call.enqueue(new Callback<PojoInitiatePaymentResponse>() {
            @Override
            public void onResponse(Call<PojoInitiatePaymentResponse> call,
                                   Response<PojoInitiatePaymentResponse> response) {
                if (response.isSuccessful()) {
                    startPaymentActivity(response, amount);
                } else {
                    if (AdTargetAudienceActivity.this != null)
                        commonFunctions.setToastMessage(getApplicationContext(),
                                getString(R.string.some_error_occured_contact_support),
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
            }

            @Override
            public void onFailure(Call<PojoInitiatePaymentResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.closeProgressDialog();
                if (AdTargetAudienceActivity.this != null)
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.some_error_occured_contact_support),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                //  ll_parent.setVisibility(View.VISIBLE);
            }
        });
    }

    private void startPaymentActivity(Response<PojoInitiatePaymentResponse> response, String amount) {
        Intent paymentIntent = new Intent(AdTargetAudienceActivity.this,
                RazorPayPaymentActivity.class);
        if (response.body() != null) {
            paymentIntent.putExtra(AppKeys.PAYMENT_REMARKS, getString(R.string.post_boost_payment));
            paymentIntent.putExtra(AppKeys.ID, response.body().data);
            paymentIntent.putExtra(AppKeys.PRICE, amount);
            startActivityForResult(paymentIntent, PAYMENT_REQUEST_CODE);
        }
    }


    private boolean areParamsSame(List<PojoGetInterestListResponseDataListItem> interestList,
                                  int minAge, int maxAge, double latitude, double longitude, int radius,
                                  String gender) {
        boolean areSame = true;
        if (selectedInterestList.size() != interestList.size()) {
            return false;
        }
        for (int i = 0; i < interestList.size(); i++) {
            if (!interestList.get(i).interestId.equals(selectedInterestList.get(i).interestId)) {
                return false;
            }
        }

        if (this.minAge != minAge) {
            return false;
        }

        if (this.maxAge != maxAge) {
            return false;
        }

        if (this.latitude != latitude) {
            return false;
        }
        if (this.longitude != longitude) {
            return false;
        }
        if (this.radius != radius) {
            return false;
        }
        if (gender != null && this.gender != null && !this.gender.equals(gender)) {
            return false;
        }

        return areSame;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                et_location.setText(place.getAddress());
                LatLng latLng = place.getLatLng();
                latitude = latLng.latitude;
                longitude = latLng.longitude;
                ll_distance_wrapper.setVisibility(View.VISIBLE);
                getAdsTargetCount(selectedInterestList, minAge, maxAge, latitude, longitude, radius, gender);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        if (requestCode == SELECT_INTEREST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Type type = new TypeToken<ArrayList<PojoGetInterestListResponseDataListItem>>() {
                }.getType();
                String dataStr = data.getStringExtra("data");
                ArrayList<PojoGetInterestListResponseDataListItem> tempList;
                Gson gson = new Gson();
                tempList = gson.fromJson(dataStr, type);
                selectedInterestList.clear();
                selectedInterestList.addAll(tempList);
                taggedInterestAdapter.notifyDataSetChanged();
                getAdsTargetCount(selectedInterestList, minAge, maxAge, latitude, longitude, radius, gender);
            }
        }
        if (requestCode == PAYMENT_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                updateAdPaymentStatus(organisation_id, user_id, data);
            }

        }
    }

    public void updateAdPaymentStatus(String organisation_id, String user_id, Intent data) {
        Call<PojoNoDataResponse> call =
                apiService.updateAdPaymentStatus(organisation_id, user_id,
                        data.getStringExtra(AppKeys.PAYMENT_STATUS),
                        data.getStringExtra(AppKeys.TRANSACTION_ID),
                        String.valueOf(totalCost), post_id, "Razorpay",
                        data.getStringExtra(AppKeys.ID),  data.getStringExtra(AppKeys.PAYMENT_ID));
        commonFunctions.openProgressDialog("Please wait... updating payment status. Do not close this page.");
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call,
                                   Response<PojoNoDataResponse> response) {
                if (response.isSuccessful()&&response.body()!=null&&response.body().status==1) {
                    if (data.getStringExtra(AppKeys.PAYMENT_STATUS).equals(AppKeys.PAYMENT_COMPLETED)) {
                        String message = "";
                        Intent data = new Intent();
                        data.putExtra(AppKeys.POST_ID, post_id);
                        setResult(RESULT_OK, data);
                        finish();
                    }
                } else {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getApplicationContext().getString(R.string.some_error_occured_contact_support),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                // commonFunctions.closeProgressDialog();
               openNoConnectionDialog(data,getString(R.string.target_criteria));
                //  ll_parent.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openNoConnectionDialog(Intent data, String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    updateAdPaymentStatus(organisation_id, user_id, data);
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

    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem
            pojoGetInterestListResponseDataListItem) {

    }
}
