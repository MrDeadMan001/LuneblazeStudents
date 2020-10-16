package com.avadna.luneblaze.activities;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.venue.VenuePollAdapter;
import com.avadna.luneblaze.adapters.VenueSearchResultAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponse;
import com.avadna.luneblaze.pojo.pojoVenueList.PojoGetVenueListResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//this screen is deprecated
//todo add another list of suggested venues with max limit of 10
//todo for each venue open a list of dates.
public class VenuePollActivity extends AppBaseActivity implements VenueSearchResultAdapter.VenueSearchResultAdapterCallback {
    ActionBar actionBar;
    ScrollView sv_parent_wrapper;
    ApiInterface apiService;
    CommonFunctions commonFunctions;

    CheckBox cb_select_all;
    LinearLayout ll_venue_data_wrapper;
    RecyclerView rv_venue_poll_list;
    VenuePollAdapter venuePollAdapter;
    RecyclerView.LayoutManager venueLayoutManager;
    Button bt_done;

    RecyclerView rv_venue_search_result;
    VenueSearchResultAdapter venueResultsListAdapter;
    List<PojoGetVenueListResponseData> venueSearchResultList;
    EditText et_venue_name;
    TextView tv_venue_search_button;
    TextView tv_venue_name;
    RelativeLayout rl_venue_search_bar;
    int[] location = new int[2];
    int statusBarHeight = 0, actionBarHeight = 0;

    View.OnClickListener onClickListener;
    View.OnFocusChangeListener onFocusChangeListener;



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

        setContentView(R.layout.activity_venue_poll);
        setUpActionBar();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        commonFunctions=new CommonFunctions(this);
        initViews();
        initVenuePollList();
        initVenueSearchList();
        initClickListener();
        setClickListeners();
        initBarSizes();
        setTextChangedListeners();
        initFocusChangeListener();
        setFocusChangeListener();

    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.select_venue));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(VenuePollActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(VenuePollActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        sv_parent_wrapper = (ScrollView) findViewById(R.id.sv_parent_wrapper);
        ll_venue_data_wrapper = (LinearLayout) findViewById(R.id.ll_venue_data_wrapper);
        et_venue_name = (EditText) findViewById(R.id.et_venue_name);
        rl_venue_search_bar = (RelativeLayout) findViewById(R.id.rl_venue_search_bar);
        tv_venue_search_button = (TextView) findViewById(R.id.tv_venue_search_button);
        bt_done = (Button) findViewById(R.id.bt_done);
        cb_select_all = (CheckBox) findViewById(R.id.cb_select_all);

        cb_select_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                venuePollAdapter.setCheckedAll(isChecked);

            }
        });

    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_venue_search_button:
                        hitVenueSearchWithTextApi(40.0f, 74.0f, et_venue_name.getText().toString().trim(), "0");
                        break;

                    case R.id.bt_done:
                        Intent datePollIntent = new Intent(VenuePollActivity.this, DatePollActivity.class);
                        startActivity(datePollIntent);
                        break;
                }
            }
        };
    }

    private void setTextChangedListeners() {
        et_venue_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_venue_name.getText().toString().trim().isEmpty()) {
                    venueSearchResultList.clear();
                    venueResultsListAdapter.notifyDataSetChanged();
                } else {
                    hitVenueSearchWithTextApi(40.0f, 74.0f, et_venue_name.getText().toString().trim(), "0");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void initFocusChangeListener() {
        onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                switch (v.getId()) {
                    case R.id.et_interest_name:
                        if (hasFocus) {
                            rl_venue_search_bar.getLocationOnScreen(location);
                            int x = location[0];
                            int y = location[1];
                            sv_parent_wrapper.smoothScrollBy(0, y - statusBarHeight - actionBarHeight);
                        }
                        break;
                }
            }
        };

    }

    private void initBarSizes() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }


        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize}
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

      /*  // status bar height
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // action bar height
        int actionBarHeight = 0;
        final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize }
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        // navigation bar height
        int navigationBarHeight = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId);
        }*/
    }

    private void setFocusChangeListener() {
        et_venue_name.setOnFocusChangeListener(onFocusChangeListener);

    }

    private void setClickListeners() {
        tv_venue_search_button.setOnClickListener(onClickListener);
        bt_done.setOnClickListener(onClickListener);
    }

    private void initVenueSearchList() {
        venueSearchResultList = new ArrayList<>();
        rv_venue_search_result = (RecyclerView) findViewById(R.id.rv_venue_search_result);
        rv_venue_search_result.setLayoutManager
                (new LinearLayoutManager(VenuePollActivity.this,
                        LinearLayoutManager.VERTICAL, false));
        rv_venue_search_result.setNestedScrollingEnabled(false);
        venueResultsListAdapter = new VenueSearchResultAdapter(VenuePollActivity.this, venueSearchResultList);
        rv_venue_search_result.setAdapter(venueResultsListAdapter);
    }

    private void initVenuePollList() {
        rv_venue_poll_list = (RecyclerView) findViewById(R.id.rv_venue_poll_list);
        venuePollAdapter = new VenuePollAdapter(VenuePollActivity.this, 6);
        //using horizontal linearlayout as we want horizontal list
        rv_venue_poll_list.setNestedScrollingEnabled(false);
        venueLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_venue_poll_list.setLayoutManager(venueLayoutManager);
        rv_venue_poll_list.setAdapter(venuePollAdapter);
    }


    private void hitVenueSearchWithTextApi(float lat, float lon, String text, String offset) {
        String latitude = Float.toString(lat);
        String longitude = Float.toString(lon);
        boolean allFieldsOk = !offset.isEmpty() && !text.isEmpty() && !latitude.isEmpty() && !longitude.isEmpty();

        if (allFieldsOk) {
            Call<PojoGetVenueListResponse> call = apiService.searchVenueWithTextApi(latitude, longitude, text, offset);
            call.enqueue(new Callback<PojoGetVenueListResponse>() {
                @Override
                public void onResponse(Call<PojoGetVenueListResponse> call, Response<PojoGetVenueListResponse> response) {

                    String message = response.body().message;
                    if (response.body().data != null) {
                        venueSearchResultList.clear();
                        //only show 4 results in list
                        for (int i = 0; i < response.body().data.size() && i < 4; i++) {
                            venueSearchResultList.add(response.body().data.get(i));
                        }
                        // interestSearchResultList.addAll(response.body().data);
                        venueResultsListAdapter.notifyDataSetChanged();
                        rl_venue_search_bar.getLocationOnScreen(location);
                        int x = location[0];
                        int y = location[1];
                        sv_parent_wrapper.smoothScrollBy(0, y - statusBarHeight - actionBarHeight);
                    } else {
                        commonFunctions.setToastMessage(VenuePollActivity.this, "" + message,
                                Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }

                }

                @Override
                public void onFailure(Call<PojoGetVenueListResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(VenuePollActivity.this, t.toString(),
                            Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                }
            });
        }
    }


    @Override
    public void venueResultItemClickCallback(int position, PojoGetVenueListResponseData pojoGetVenueListResponseData) {
        //todo add new venue to list here
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
