package com.avadna.luneblaze.activities.interest;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.InterestSearchResultAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponse;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateInterestActivity extends AppBaseActivity
        implements InterestSearchResultAdapter.InterestResultAdapterCallback {

    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;

    ScrollView sv_wrapper;


    RecyclerView rv_list;
    InterestSearchResultAdapter interestSearchResultAdapter;
    List<PojoSearchInterestWithTextResponseData> interestSearchResultList;
    EditText et_search_interest;

    EditText et_interest_name, et_description;
    ImageButton ib_clear;
    TextView tv_done, tv_parent_interest;
    int[] location = new int[2];
    int statusBarHeight = 0, actionBarHeight = 0;


    View.OnFocusChangeListener onFocusChangeListener;
    View.OnClickListener onClickListener;
    private boolean searchAllowed=true;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_interest);
        setUpActionBar();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        initBarSizes();
        initViews();
        initClickListener();
        setClickListener();
        setTextChangedListeners();
        initFocusChangeListener();
        setFocusChangeListener();
        initInterestResultList();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_done:
                        if (et_interest_name.getText().toString().trim().isEmpty()) {
                            commonFunctions.setToastMessage(CreateInterestActivity.this,
                                    getString(R.string.enter_interest_name), Toast.LENGTH_LONG,AppKeys.TOAST_USER);
                        } else if (et_description.getText().toString().trim().isEmpty()) {
                            commonFunctions.setToastMessage(CreateInterestActivity.this,
                                    getString(R.string.enter_interest_description), Toast.LENGTH_LONG,AppKeys.TOAST_USER);
                        } else if (et_search_interest.getText().toString().trim().isEmpty()) {
                            commonFunctions.setToastMessage(CreateInterestActivity.this,
                                    getString(R.string.select_parent_interest), Toast.LENGTH_LONG,AppKeys.TOAST_USER);
                        } else {
                            hitCreateInterestApi(user_id, et_interest_name.getText().toString().trim(),
                                    et_description.getText().toString().trim(),
                                    et_search_interest.getText().toString().trim());
                        }

                    case R.id.ib_clear:
                        et_search_interest.setEnabled(true);
                        et_search_interest.getText().clear();
                        interestSearchResultList.clear();
                        interestSearchResultAdapter.notifyDataSetChanged();
                        et_search_interest.requestFocus();
                        searchAllowed=true;
                        break;
                }
            }
        };
    }

    private void hitCreateInterestApi(String user_id, String name, String description, String parent) {
        Call<PojoNoDataResponse> call = apiService.createInterest(user_id, name, description, parent);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if(response!=null&&response.body()!=null){
                    String message = response.body().message;
                    commonFunctions.setToastMessage(CreateInterestActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(CreateInterestActivity.this, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

            }
        });
    }

    private void setClickListener() {
        tv_done.setOnClickListener(onClickListener);
        ib_clear.setOnClickListener(onClickListener);
    }

    private void initFocusChangeListener() {
        onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                switch (v.getId()) {
                    case R.id.et_interest_name:
                        if (hasFocus) {
                            tv_parent_interest.getLocationOnScreen(location);
                            int x = location[0];
                            int y = location[1];
                            sv_wrapper.smoothScrollBy(0, y - statusBarHeight - actionBarHeight);
                        }
                        break;
                }
            }
        };
    }

    private void setFocusChangeListener() {
        et_search_interest.setOnFocusChangeListener(onFocusChangeListener);
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


    private void initViews() {
        sv_wrapper = (ScrollView) findViewById(R.id.sv_wrapper);
        et_search_interest = (EditText) findViewById(R.id.et_search_interest);
        et_interest_name = (EditText) findViewById(R.id.et_interest_name);
        et_description = (EditText) findViewById(R.id.et_description);
        ib_clear= (ImageButton) findViewById(R.id.ib_clear);
        ib_clear.setVisibility(View.GONE);
        tv_done = (TextView) findViewById(R.id.tv_done);
        tv_parent_interest = (TextView) findViewById(R.id.tv_parent_interest);
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.create_interest));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(CreateInterestActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(CreateInterestActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void setTextChangedListeners() {
        et_search_interest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length()==0) {
                    interestSearchResultList.clear();
                    interestSearchResultAdapter.notifyDataSetChanged();
                } else if(searchAllowed){
                    hitInterestSearchWithTextApi(et_search_interest.getText().toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    private void initInterestResultList() {
        interestSearchResultList = new ArrayList<>();
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        rv_list.setLayoutManager
                (new LinearLayoutManager(CreateInterestActivity.this,
                        RecyclerView.VERTICAL, false));
        rv_list.setNestedScrollingEnabled(false);
        interestSearchResultAdapter = new InterestSearchResultAdapter(CreateInterestActivity.this, interestSearchResultList);
        rv_list.setAdapter(interestSearchResultAdapter);
    }

    private void hitInterestSearchWithTextApi(String text) {
        String offset = "0";
        boolean allFieldsOk = !offset.isEmpty() && !text.isEmpty();

        if (allFieldsOk) {
            Call<PojoSearchInterestWithTextResponse> call = apiService.searchInterestWithTextApi(text, offset);
            call.enqueue(new Callback<PojoSearchInterestWithTextResponse>() {
                @Override
                public void onResponse(Call<PojoSearchInterestWithTextResponse> call, Response<PojoSearchInterestWithTextResponse> response) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        interestSearchResultList.clear();
                        for (int i = 0; i < response.body().data.size() && i < 4; i++) {
                            interestSearchResultList.add(response.body().data.get(i));
                        }
                        // interestSearchResultList.addAll(response.body().data);
                        interestSearchResultAdapter.notifyDataSetChanged();
                        tv_parent_interest.getLocationOnScreen(location);
                        int x = location[0];
                        int y = location[1];
                        sv_wrapper.smoothScrollBy(0, y - statusBarHeight - actionBarHeight);
                    } else {
                        commonFunctions.setToastMessage(CreateInterestActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }

                }

                @Override
                public void onFailure(Call<PojoSearchInterestWithTextResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(CreateInterestActivity.this, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                }
            });
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
    public void interestResultItemClickCallback(int position, PojoSearchInterestWithTextResponseData pojoSearchInterestWithTextResponseData) {
        searchAllowed=false;
        et_search_interest.setText(pojoSearchInterestWithTextResponseData.text);
        ib_clear.setVisibility(View.VISIBLE);
        interestSearchResultList.clear();
        interestSearchResultAdapter.notifyDataSetChanged();
        et_search_interest.setEnabled(false);
    }


    @Override
    protected void onPause() {
        super.onPause();

    }
}
