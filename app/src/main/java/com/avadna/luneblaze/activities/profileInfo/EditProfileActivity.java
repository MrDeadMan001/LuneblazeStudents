package com.avadna.luneblaze.activities.profileInfo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import com.google.android.material.textfield.TextInputLayout;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.registration.VenueSuggestPopupAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileInfoResponse;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileInfoResponseData;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUpdateAppSettingsResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppBaseActivity {

    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    PojoProfileInfoResponseData pojoProfileInfoResponseData;
    PojoLoginResponseData pojoLoginResponseData;


    EditText et_first_name;
    ImageButton ib_edit_first_name, ib_done_first_name;

    EditText et_last_name;
    ImageButton ib_edit_last_name, ib_done_last_name;


    EditText et_designation;
    ImageButton ib_edit_designation, ib_done_designation;

    EditText et_organisation;
    ProgressBar pb_loading_venues;
    ImageButton ib_edit_organisation, ib_done_organisation;

    EditText et_area;
    ImageButton ib_edit_area, ib_done_area;
    EditText et_foi;
    TextInputLayout til_foi;
    TextInputLayout til_area;
    TextInputLayout til_organisation;
    TextInputLayout til_designation;
    TextInputLayout til_last_name;
    TextInputLayout til_first_name;

    ImageButton ib_edit_foi, ib_done_foi;

    ScrollView sv_content_wrapper;
    ProgressBar pb_loading_content;

    TextView tv_done;

    View.OnClickListener onClickListener;

    InputMethodManager imm;

    boolean isEditTokenAvailable = true;

    final int FIRST_NAME_MAX_LIMIT = 25;
    final int LAST_NAME_MAX_LIMIT = 15;

    boolean isSearchApiCalled = false;
    ListPopupWindow venueListPopUp;
    String selectedVenue = "";
    private String selectedVenueId = "0";

    boolean allowVenueSuggestion = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        pojoProfileInfoResponseData = new PojoProfileInfoResponseData();
        setUpActionBar();
        // setImagePicker();
        initViews();
        initClickListener();
        setClickListener();
        pojoProfileInfoResponseData = preferenceUtils.getUserProfileInfo();
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        venueListPopUp = new ListPopupWindow(EditProfileActivity.this);
        setTextWatcher();
        if (pojoProfileInfoResponseData != null) {
            setDataOnViews();
        } else {
            hitGetUserProfileApi(user_id, user_id, "dashboard");
        }
    }


    private void setTextWatcher() {
        et_first_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_first_name.setError(null);
                if (s.length() > FIRST_NAME_MAX_LIMIT) {
                    til_first_name.setError(getString(R.string.first_name_not_longer_than_25));

                 /*   commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.first_name_not_longer_than_25), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);*/
                    et_first_name.setText(s.subSequence(0, FIRST_NAME_MAX_LIMIT));
                    et_first_name.setSelection(et_first_name.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_last_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_last_name.setError(null);
                if (s.length() > LAST_NAME_MAX_LIMIT) {
                    til_last_name.setError(getString(R.string.last_name_not_longer_than_15));
/*
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.last_name_not_longer_than_15), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);*/

                    et_last_name.setText(s.subSequence(0, LAST_NAME_MAX_LIMIT));
                    et_last_name.setSelection(et_last_name.getText().length());
                }
                String lastName = s.toString().trim();

                if (!s.toString().isEmpty() && !isValidLastName(lastName)) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.last_name_only_letters_no_spaces), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        et_organisation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til_organisation.setError(null);
                if (!selectedVenue.equals(s.toString().trim())) {
                    hitSearchVenueApi(user_id, s.toString(), 0, "venues");
                    selectedVenueId = "";
                }
                if (s.length() == 0) {
                    //   til_institution.setError(getString(R.string.field_cant_be_empty));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_designation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                til_designation.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_area.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                til_area.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_foi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                til_foi.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    public boolean isValidLastName(String name) {
        if (name.contains(" ")) {
            return false;
        } else {
            return Pattern.compile("[a-zA-Z \\u0904-\\u097f]*+").matcher(name).matches();
        }
    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.edit_profile));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(EditProfileActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(EditProfileActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        sv_content_wrapper = (ScrollView) findViewById(R.id.sv_content_wrapper);
        sv_content_wrapper.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);

        et_first_name = (EditText) findViewById(R.id.et_first_name);
        ib_edit_first_name = (ImageButton) findViewById(R.id.ib_edit_first_name);
        ib_done_first_name = (ImageButton) findViewById(R.id.ib_done_first_name);
        et_last_name = (EditText) findViewById(R.id.et_last_name);
        ib_edit_last_name = (ImageButton) findViewById(R.id.ib_edit_last_name);
        ib_done_last_name = (ImageButton) findViewById(R.id.ib_done_last_name);
        et_designation = (EditText) findViewById(R.id.et_designation);
        ib_edit_designation = (ImageButton) findViewById(R.id.ib_edit_designation);
        ib_done_designation = (ImageButton) findViewById(R.id.ib_done_designation);
        et_organisation = (EditText) findViewById(R.id.et_organisation);
        pb_loading_venues = (ProgressBar) findViewById(R.id.pb_loading_venues);
        pb_loading_venues.setVisibility(View.GONE);
        ib_edit_organisation = (ImageButton) findViewById(R.id.ib_edit_organisation);
        ib_done_organisation = (ImageButton) findViewById(R.id.ib_done_organisation);
        et_area = (EditText) findViewById(R.id.et_area);
        ib_edit_area = (ImageButton) findViewById(R.id.ib_edit_area);
        ib_done_area = (ImageButton) findViewById(R.id.ib_done_area);
        et_foi = (EditText) findViewById(R.id.et_foi);
        ib_edit_foi = (ImageButton) findViewById(R.id.ib_edit_foi);
        ib_done_foi = (ImageButton) findViewById(R.id.ib_done_foi);

        til_foi = (TextInputLayout) findViewById(R.id.til_foi);
        til_area = (TextInputLayout) findViewById(R.id.til_area);
        til_organisation = (TextInputLayout) findViewById(R.id.til_organisation);
        til_designation = (TextInputLayout) findViewById(R.id.til_designation);
        til_last_name = (TextInputLayout) findViewById(R.id.til_last_name);
        til_first_name = (TextInputLayout) findViewById(R.id.til_first_name);

        tv_done = (TextView) findViewById(R.id.tv_done);
    }


    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {

                    case R.id.ib_edit_first_name:
                        if (isEditTokenAvailable) {
                            ib_edit_first_name.setVisibility(View.GONE);
                            ib_done_first_name.setVisibility(View.VISIBLE);
                            et_first_name.setEnabled(true);
                            et_first_name.requestFocus();
                            if (et_first_name.requestFocus()) {
                                imm.showSoftInput(et_first_name, InputMethodManager.SHOW_FORCED);
                            }
                            isEditTokenAvailable = false;
                        }
                        break;
                    case R.id.ib_done_first_name:
                        if (et_first_name.getText().toString().trim().isEmpty() ||
                                !isValidFirstName(et_first_name.getText().toString().trim())) {
                            til_first_name.setError(getString(R.string.please_enter_a_valid_first_name));
                            /*commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_a_valid_first_name)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                        } else {
                            saveUserFullNameToPref(et_first_name.getText().toString() + " " + et_last_name.getText().toString());
                            hitUpdateFullNameApi(user_id, "update_name", et_first_name.getText().toString()
                                    , et_last_name.getText().toString(), "first");
                            ib_edit_first_name.setVisibility(View.VISIBLE);
                            ib_done_first_name.setVisibility(View.GONE);
                            et_first_name.setEnabled(false);
                            et_first_name.setEnabled(false);
                            isEditTokenAvailable = true;

                        }


                        break;

                    case R.id.ib_edit_last_name:
                        if (isEditTokenAvailable) {
                            ib_edit_last_name.setVisibility(View.GONE);
                            ib_done_last_name.setVisibility(View.VISIBLE);
                            et_last_name.setEnabled(true);
                            et_last_name.requestFocus();
                            if (et_last_name.requestFocus()) {
                                imm.showSoftInput(et_last_name, InputMethodManager.SHOW_FORCED);
                            }
                            isEditTokenAvailable = false;

                        }
                        break;
                    case R.id.ib_done_last_name:

                        if (et_last_name.getText().toString().trim().isEmpty()) {
                            til_last_name.setError(getString(R.string.please_enter_a_valid_last_name));
                            /*commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_a_valid_last_name)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                        } else if (!isValidLastName(et_last_name.getText().toString().trim())) {
                            til_last_name.setError(getString(R.string.last_name_only_letters_no_spaces));

                         /*   commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.last_name_only_letters_no_spaces), Toast.LENGTH_LONG,
                                    AppKeys.TOAST_USER);*/
                        } else {
                            saveUserFullNameToPref(et_first_name.getText().toString() + " " + et_last_name.getText().toString());
                            hitUpdateFullNameApi(user_id, "update_name", et_first_name.getText().toString()
                                    , et_last_name.getText().toString(), "last");
                            ib_edit_last_name.setVisibility(View.VISIBLE);
                            ib_done_last_name.setVisibility(View.GONE);
                            et_last_name.setEnabled(false);
                            isEditTokenAvailable = true;
                        }

                        break;

                    case R.id.ib_edit_designation:
                        if (isEditTokenAvailable) {
                            ib_edit_designation.setVisibility(View.GONE);
                            ib_done_designation.setVisibility(View.VISIBLE);
                            et_designation.setEnabled(true);
                            et_designation.requestFocus();
                            if (et_designation.requestFocus()) {
                                imm.showSoftInput(et_designation, InputMethodManager.SHOW_FORCED);
                            }
                            isEditTokenAvailable = false;
                        }
                        break;
                    case R.id.ib_done_designation:
                        if (et_designation.getText().toString().trim().isEmpty()) {
                            til_designation.setError(getString(R.string.please_enter_designation));
                          /*  commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_designation)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                        } else if (!et_designation.getText().toString().isEmpty()) {

                            hitUpdateWorkInfo(user_id, "work",
                                    et_designation.getText().toString().trim(),
                                    et_organisation.getText().toString().trim(), "designation", selectedVenueId);
                            ib_edit_designation.setVisibility(View.VISIBLE);
                            ib_done_designation.setVisibility(View.GONE);
                            et_designation.setEnabled(false);
                            isEditTokenAvailable = true;
                        }
                        break;

                    case R.id.ib_edit_organisation:
                        if (isEditTokenAvailable) {
                            allowVenueSuggestion = true;
                            ib_edit_organisation.setVisibility(View.GONE);
                            ib_done_organisation.setVisibility(View.VISIBLE);
                            et_organisation.setEnabled(true);
                            et_organisation.requestFocus();
                            if (et_organisation.requestFocus()) {
                                imm.showSoftInput(et_organisation, InputMethodManager.SHOW_FORCED);
                            }
                            isEditTokenAvailable = false;

                        }
                        break;
                    case R.id.ib_done_organisation:
                        if (et_organisation.getText().toString().trim().isEmpty()) {
                            til_organisation.setError(getString(R.string.please_enter_work_place));
                            /*commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_work_place)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                        } else {
                            pb_loading_venues.setVisibility(View.GONE);
                            hitUpdateWorkInfo(user_id, "work",
                                    et_designation.getText().toString().trim(),
                                    et_organisation.getText().toString().trim(), "designation"
                                    , selectedVenueId);
                            ib_edit_organisation.setVisibility(View.VISIBLE);
                            ib_done_organisation.setVisibility(View.GONE);
                            et_organisation.setEnabled(false);
                            isEditTokenAvailable = true;
                            allowVenueSuggestion = false;
                        }
                        break;

                    case R.id.ib_edit_area:
                        if (isEditTokenAvailable) {
                            ib_edit_area.setVisibility(View.GONE);
                            ib_done_area.setVisibility(View.VISIBLE);
                            et_area.setEnabled(true);
                            et_area.requestFocus();
                            if (et_area.requestFocus()) {
                                imm.showSoftInput(et_area, InputMethodManager.SHOW_FORCED);
                            }
                            isEditTokenAvailable = false;

                        }
                        break;
                    case R.id.ib_done_area:

                        if (!et_area.getText().toString().trim().isEmpty()) {
                            hitUpdateCurrentCity(user_id, "location", et_area.getText().toString());
                            ib_edit_area.setVisibility(View.VISIBLE);
                            ib_done_area.setVisibility(View.GONE);
                            et_area.setEnabled(false);

                            isEditTokenAvailable = true;
                        } else {
                            til_area.setError(getString(R.string.please_enter_current_city));
                    /*        commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_current_city)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                        }


                        break;


                    case R.id.ib_edit_foi:
                        if (isEditTokenAvailable) {
                            ib_edit_foi.setVisibility(View.GONE);
                            ib_done_foi.setVisibility(View.VISIBLE);
                            et_foi.setEnabled(true);
                            et_foi.requestFocus();
                            if (et_foi.requestFocus()) {
                                imm.showSoftInput(et_foi, InputMethodManager.SHOW_FORCED);
                            }
                            isEditTokenAvailable = false;
                        }
                        break;
                    case R.id.ib_done_foi:

                        if (!et_foi.getText().toString().trim().isEmpty()) {
                            hitUpdateFoi(user_id, "update_current_FOI", et_foi.getText().toString());
                            ib_edit_foi.setVisibility(View.VISIBLE);
                            ib_done_foi.setVisibility(View.GONE);
                            et_foi.setEnabled(false);
                            isEditTokenAvailable = true;
                        } else {
                            til_foi.setError(getString(R.string.please_enter_foi));
                           /* commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_foi)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                        }
                        break;

                    case R.id.tv_done:
                       /* if (et_username.getText().toString().trim().isEmpty()) {
                            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_name)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else */
                        if (!isValidFirstName(et_first_name.getText().toString().trim())) {
                            til_first_name.setError(getString(R.string.please_enter_a_valid_first_name));
                          /*  commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_a_valid_first_name)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                        } else if (!isValidLastName(et_last_name.getText().toString().trim())) {
                            til_last_name.setError(getString(R.string.last_name_only_letters_no_spaces));
                           /* commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.last_name_only_letters_no_spaces), Toast.LENGTH_LONG,
                                    AppKeys.TOAST_USER);*/
                        } else if (et_designation.getText().toString().trim().isEmpty()) {
                            til_designation.setError(getString(R.string.please_enter_designation));
                            /*commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_designation)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                        } else if (et_organisation.getText().toString().trim().isEmpty()) {
                            til_organisation.setError(getString(R.string.please_enter_work_place));
                            /*commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_work_place)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                        } else {
                            hitUpdateFullNameApi(user_id, "update_name", et_first_name.getText().toString()
                                    , et_last_name.getText().toString(), "first");
                            hitUpdateWorkInfo(user_id, "work",
                                    et_designation.getText().toString().trim(),
                                    et_organisation.getText().toString().trim(), "designation", selectedVenueId);
                        }


                        if (!et_area.getText().toString().trim().isEmpty()) {
                            hitUpdateCurrentCity(user_id, "location", et_area.getText().toString());
                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_enter_current_city)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        }

                        if (!et_foi.getText().toString().trim().isEmpty()) {
                            hitUpdateFoi(user_id, "update_current_FOI", et_foi.getText().toString());
                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_foi)
                                    , Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        }
                        break;
                }
            }
        };
    }

    public boolean isValidFirstName(String name) {
        return Pattern.compile("[a-zA-Z \\u0904-\\u097f]*+").matcher(name).matches();
    }


    private void hitSearchVenueApi(final String user_id, final String query, final int localOffset, String type) {
        Call<PojoSearchResultResponse> call = apiService.globalSearch(user_id, query,
                String.valueOf(localOffset), type);

        if (allowVenueSuggestion && !isSearchApiCalled) {
            venueListPopUp.dismiss();
            isSearchApiCalled = true;
            pb_loading_venues.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<PojoSearchResultResponse>() {
                @Override
                public void onResponse(Call<PojoSearchResultResponse> call, Response<PojoSearchResultResponse> response) {
                    pb_loading_venues.setVisibility(View.GONE);
                    String message = "";
                    if (allowVenueSuggestion && response != null && response.body() != null && response.body().data != null) {
                        VenueSuggestPopupAdapter venueSuggestPopupAdapter =
                                new VenueSuggestPopupAdapter(EditProfileActivity.this,
                                        response.body().data.venues);
                        venueListPopUp.setAnchorView(et_organisation);
                        venueListPopUp.setAdapter(venueSuggestPopupAdapter);

                        venueListPopUp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedVenue = response.body().data.venues.get(i).venueName;
                                et_organisation.setText(selectedVenue);
                                selectedVenueId = response.body().data.venues.get(i).venueId;
                                venueListPopUp.dismiss();
                            }
                        });

                        venueListPopUp.show();


                    }
                    isSearchApiCalled = false;

                    String latestQuery = et_organisation.getText().toString().trim();
                    if (!latestQuery.equals(query)) {
                        hitSearchVenueApi(user_id, latestQuery, 0, "venues");
                    }

                }

                @Override
                public void onFailure(Call<PojoSearchResultResponse> call, Throwable t) {
                    // Log error here since request failed
                    //   commonFunctions.setToastMessage(getActivity(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    // pb_loading_content.setVisibility(View.INVISIBLE);
                    pb_loading_venues.setVisibility(View.GONE);
                    isSearchApiCalled = false;

                }
            });
        }

    }

    private void saveUserFullNameToPref(String name) {
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        pojoLoginResponseData.userFullname = name;
        preferenceUtils.saveUserLoginData(pojoLoginResponseData);
    }

    private void setClickListener() {

        ib_edit_first_name.setOnClickListener(onClickListener);
        ib_done_first_name.setOnClickListener(onClickListener);
        ib_edit_last_name.setOnClickListener(onClickListener);
        ib_done_last_name.setOnClickListener(onClickListener);
        ib_edit_designation.setOnClickListener(onClickListener);
        ib_done_designation.setOnClickListener(onClickListener);
        ib_edit_organisation.setOnClickListener(onClickListener);
        ib_done_organisation.setOnClickListener(onClickListener);
        ib_edit_area.setOnClickListener(onClickListener);
        ib_done_area.setOnClickListener(onClickListener);
        ib_edit_foi.setOnClickListener(onClickListener);
        ib_done_foi.setOnClickListener(onClickListener);
        tv_done.setOnClickListener(onClickListener);
    }


    private void hitGetUserProfileApi(String user_id, String targetUserId, String type) {
        Call<PojoProfileInfoResponse> call = apiService.getUserProfile(user_id, targetUserId, type);
        call.enqueue(new Callback<PojoProfileInfoResponse>() {
            @Override
            public void onResponse(Call<PojoProfileInfoResponse> call, Response<PojoProfileInfoResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        pojoProfileInfoResponseData = response.body().data;
                        setDataOnViews();
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoProfileInfoResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_loading_content.setVisibility(View.GONE);

            }
        });
    }

    private void setDataOnViews() {
        et_first_name.setText(pojoProfileInfoResponseData.profile.userFirstname);
        et_last_name.setText(pojoProfileInfoResponseData.profile.userLastname);
        et_designation.setText(pojoProfileInfoResponseData.profile.userWorkTitle);
        et_organisation.setText(pojoProfileInfoResponseData.profile.userWorkPlace);
        et_area.setText(pojoProfileInfoResponseData.profile.userCurrentCity);
        et_foi.setText(pojoProfileInfoResponseData.profile.currentFOI);
        sv_content_wrapper.setVisibility(View.VISIBLE);
        pb_loading_content.setVisibility(View.GONE);
    }


    private void hitUpdateFullNameApi(final String user_id, String type, String first_name,
                                      String last_name, final String position) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.updateFullName(user_id, type, first_name, last_name);
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                if (position.equals("first")) {
                    et_first_name.setEnabled(true);
                    et_first_name.setText(pojoProfileInfoResponseData.profile.userFirstname);
                    if (et_first_name.requestFocus()) {
                        imm.showSoftInput(et_first_name, InputMethodManager.SHOW_FORCED);
                    }
                    ib_done_first_name.setVisibility(View.VISIBLE);
                    ib_edit_first_name.setVisibility(View.GONE);
                } else {
                    et_last_name.setEnabled(true);
                    et_last_name.setText(pojoProfileInfoResponseData.profile.userLastname);
                    if (et_last_name.requestFocus()) {
                        imm.showSoftInput(et_last_name, InputMethodManager.SHOW_FORCED);
                    }
                    ib_done_last_name.setVisibility(View.VISIBLE);
                    ib_edit_last_name.setVisibility(View.GONE);
                }
            }
        });
    }

    private void hitUpdateWorkInfo(final String user_id, String type, String designation, String institution,
                                   final String field, String selectedVenueId) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.updateWorkInfo(user_id, type, designation,
                institution, selectedVenueId);

        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
                    pojoLoginResponseData.oraganisationId = selectedVenueId;
                    preferenceUtils.saveUserLoginData(pojoLoginResponseData);
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);

                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                if (field.equals("designation")) {
                    et_designation.setEnabled(true);
                    et_designation.setText(pojoProfileInfoResponseData.profile.userWorkTitle);
                    if (et_designation.requestFocus()) {
                        imm.showSoftInput(et_designation, InputMethodManager.SHOW_FORCED);
                    }
                    et_designation.setBackgroundResource(R.drawable.app_theme_border);
                    ib_done_designation.setVisibility(View.VISIBLE);
                    ib_edit_designation.setVisibility(View.GONE);
                } else {
                    et_organisation.setEnabled(true);
                    et_organisation.setText(pojoProfileInfoResponseData.profile.userWorkTitle);
                    if (et_organisation.requestFocus()) {
                        imm.showSoftInput(et_organisation, InputMethodManager.SHOW_FORCED);
                    }
                    et_organisation.setBackgroundResource(R.drawable.app_theme_border);
                    ib_done_organisation.setVisibility(View.VISIBLE);
                    ib_edit_organisation.setVisibility(View.GONE);
                }
            }
        });
    }


    private void hitUpdateCurrentCity(final String user_id, String type, String city) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.updateUserCity(user_id, type, city);
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);

                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                et_area.setEnabled(true);
                et_area.setText(pojoProfileInfoResponseData.profile.userWorkTitle);
                if (et_area.requestFocus()) {
                    imm.showSoftInput(et_area, InputMethodManager.SHOW_FORCED);
                }
                et_area.setBackgroundResource(R.drawable.app_theme_border);
                ib_done_area.setVisibility(View.VISIBLE);
                ib_edit_area.setVisibility(View.GONE);
            }
        });
    }

    private void hitUpdateFoi(final String user_id, String type, String foi) {
        Call<PojoUpdateAppSettingsResponse> call = apiService.updateUserFoi(user_id, type, foi);
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);

                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                et_foi.setEnabled(true);
                et_foi.setText(pojoProfileInfoResponseData.profile.userWorkTitle);
                if (et_foi.requestFocus()) {
                    imm.showSoftInput(et_foi, InputMethodManager.SHOW_FORCED);
                }
                et_foi.setBackgroundResource(R.drawable.app_theme_border);
                ib_done_foi.setVisibility(View.VISIBLE);
                ib_edit_foi.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
    }
}
