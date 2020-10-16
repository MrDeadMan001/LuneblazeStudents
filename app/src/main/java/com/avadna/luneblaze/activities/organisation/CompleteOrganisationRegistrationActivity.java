package com.avadna.luneblaze.activities.organisation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.SimplePopUpListAdapter;
import com.avadna.luneblaze.adapters.organisation.OrganisationTypeListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AES;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoCreateOrganisationResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationInfoResponseData;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationTypeResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationTypeResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class CompleteOrganisationRegistrationActivity extends AppCompatActivity {

    private ActionBar actionBar;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private ApiInterface apiService;

    private ImageView iv_logo;
    private String cover_photo_path;
    private File cover_photo;

    private TextView tv_add_photo;
    private ProgressBar pb_loading;
    private ScrollView sv_parent_wrapper;
    private TextInputLayout /*til_type,*/ til_city, til_size, til_about, til_address;
    private EditText /*et_type, */et_city, et_size, et_about, et_address;

    private ProgressBar pb_loading_type;

    private TextView tv_done_button;
    private final int PICK_IMAGE_REQUEST = 9124;
    private ImageButton ib_location_picker;

    //private String selectedType = "";
    //  private String selectedTypeId = "";

    private List<String> company_sizes;

    private ListPopupWindow organisationListPopupWindow;
    private boolean isSearchApiCalled = false;

    private int PLACE_PICKER_REQUEST = 1234;

    private View.OnClickListener onClickListener;

    private String type = AppKeys.EDIT;
    private String organisation_id = "";

    private PojoGetOrganisationInfoResponseData pojoGetOrganisationInfoResponseData;
    private InputMethodManager imm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_organisation_registration);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        user_id = preferenceUtils.get_user_id();
        organisation_id = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);
        imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        initLists();
        initViews();
        initClickListener();
        setClickListener();
        // setFocusChangeListener();

        organisationListPopupWindow = new ListPopupWindow(CompleteOrganisationRegistrationActivity.this);
        organisationListPopupWindow.setSoftInputMode(PopupWindow.INPUT_METHOD_NEEDED);
        organisationListPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        organisation_id = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);
        setUpActionBar();
        setTextWatchers();
    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            //actionBar.setDisplayHomeAsUpEnabled(true);
            //   actionBar.setTitle(getString(R.string.comments) + " (" + comment_count + ")");

            actionBar.setTitle(getString(R.string.complete_registration));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(CompleteOrganisationRegistrationActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(CompleteOrganisationRegistrationActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initLists() {
        company_sizes = new ArrayList<>();
        company_sizes.add("1-10 employees");
        company_sizes.add("10-50 employees");
        company_sizes.add("50-200 employees");
        company_sizes.add("200-1000 employees");
        company_sizes.add("1000+ employees");
    }

    private void initViews() {
        tv_add_photo = (TextView) findViewById(R.id.tv_add_photo);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        pb_loading.setVisibility(View.GONE);
        sv_parent_wrapper = (ScrollView) findViewById(R.id.sv_parent_wrapper);
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        til_city = (TextInputLayout) findViewById(R.id.til_city);
        //  til_type = (TextInputLayout) findViewById(R.id.til_type);
        til_size = (TextInputLayout) findViewById(R.id.til_size);
        til_about = (TextInputLayout) findViewById(R.id.til_about);
        til_address = (TextInputLayout) findViewById(R.id.til_address);
        //  et_type = (EditText) findViewById(R.id.et_type);
        pb_loading_type = (ProgressBar) findViewById(R.id.pb_loading_type);
        pb_loading_type.setVisibility(View.GONE);
        et_city = (EditText) findViewById(R.id.et_city);
        et_size = (EditText) findViewById(R.id.et_size);
        et_about = (EditText) findViewById(R.id.et_about);

        et_about.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (et_about.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        et_address = (EditText) findViewById(R.id.et_address);
        et_address.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (et_about.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        ib_location_picker = (ImageButton) findViewById(R.id.ib_location_picker);
        tv_done_button = (TextView) findViewById(R.id.tv_done_button);
/*
        et_city.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!selectedCityName.equals(s.toString().trim())) {
                    hitGetCitiesApi(s.toString(), 0);
                    selectedCity = null;
                }

                if (s.length() == 0) {
                    //   til_institution.setError(getString(R.string.field_cant_be_empty));

                } else {
                    til_city.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
    }

    /*private void hitGetOrganisationTypeApi(String user_id, String query) {
        Call<PojoGetOrganisationTypeResponse> call = apiService.getOrganisationType(user_id, query.toLowerCase());
        if (!isSearchApiCalled) {
            isSearchApiCalled = true;
            pb_loading_type.setVisibility(View.VISIBLE);
            organisationListPopupWindow.dismiss();
            call.enqueue(new Callback<PojoGetOrganisationTypeResponse>() {
                @Override
                public void onResponse(Call<PojoGetOrganisationTypeResponse> call,
                                       Response<PojoGetOrganisationTypeResponse> response) {
                    pb_loading_type.setVisibility(View.GONE);
                    String message = "";
                    if (response != null && response.body() != null && response.body().data != null) {
                        organisationListPopupWindow.dismiss();
                        organisationListPopupWindow.setAnchorView(et_type);
                        List<PojoGetOrganisationTypeResponseData> filteredList = new ArrayList<>();
                        for (int i = 0; i < response.body().data.size(); i++) {
                            if (response.body().data.get(i).name.toLowerCase().contains(query.toLowerCase())) {
                                filteredList.add(response.body().data.get(i));
                            }
                        }
                        Collections.sort(filteredList, new Comparator<PojoGetOrganisationTypeResponseData>() {
                            public int compare(PojoGetOrganisationTypeResponseData d1, PojoGetOrganisationTypeResponseData d2) {
                                return d1.getName().compareTo(d2.getName());
                            }
                        });
                        OrganisationTypeListAdapter organisationTypeListAdapter = new OrganisationTypeListAdapter(
                                CompleteOrganisationRegistrationActivity.this, filteredList);
                        organisationListPopupWindow.setAdapter(organisationTypeListAdapter);

                        organisationListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                selectedType = filteredList.get(i).name;
                                et_type.setText(selectedType);
                                selectedTypeId = filteredList.get(i).id;
                                organisationListPopupWindow.dismiss();
                            }
                        });
                        organisationListPopupWindow.show();

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "Url Error" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    isSearchApiCalled = false;

                    String latestQuery = et_type.getText().toString().trim();
                    if (!latestQuery.equals(query)) {
                        hitGetOrganisationTypeApi(user_id, latestQuery);
                    }
                }

                @Override
                public void onFailure(Call<PojoGetOrganisationTypeResponse> call, Throwable t) {
                    // Log error here since request failed
                    //   commonFunctions.setToastMessage(getActivity(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    // pb_loading_content.setVisibility(View.INVISIBLE);
                    isSearchApiCalled = false;
                    pb_loading_type.setVisibility(View.GONE);

                }
            });
        }
    }*/

   /* private void setFocusChangeListener() {
        et_type.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && (selectedTypeId == null || selectedTypeId.isEmpty())) {
                    et_type.getText().clear();
                }
            }
        });
    }*/

    private void setClickListener() {
        iv_logo.setOnClickListener(onClickListener);
        tv_add_photo.setOnClickListener(onClickListener);
        et_size.setOnClickListener(onClickListener);
        ib_location_picker.setOnClickListener(onClickListener);
        tv_done_button.setOnClickListener(onClickListener);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_logo:
                        break;

                    case R.id.tv_add_photo:
                        requestStoragePermission(PICK_IMAGE_REQUEST);
                        break;

                    case R.id.tv_done_button:
                        if (pojoGetOrganisationInfoResponseData == null) {
                            if (validateAllFields()) {
                                //todo call completion api here
                                if (et_address.requestFocus()) {
                                    imm.hideSoftInputFromWindow(et_address.getWindowToken(), 0);
                                }
                                hitEditOrganisationApi(user_id, organisation_id,
                                        et_city.getText().toString(),
                                        et_about.getText().toString(), et_address.getText().toString(),
                                        et_size.getText().toString());
                            }
                        }
                        break;

                    case R.id.ib_location_picker:
                        if (!Places.isInitialized()) {
                            Places.initialize(getApplicationContext(), AES.decrypt(AppKeys.PLACES_API_KEY, AppKeys.enKey));
                        }
                        List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                        Intent intent = new Autocomplete.IntentBuilder(
                                AutocompleteActivityMode.FULLSCREEN, fields)
                                .build(CompleteOrganisationRegistrationActivity.this);
                        startActivityForResult(intent, PLACE_PICKER_REQUEST);
                        break;

                    case R.id.et_size:
                        organisationListPopupWindow.dismiss();
                        organisationListPopupWindow.setAnchorView(et_size);

                        SimplePopUpListAdapter simplePopUpListAdapter = new SimplePopUpListAdapter(
                                CompleteOrganisationRegistrationActivity.this, company_sizes);
                        organisationListPopupWindow.setAdapter(simplePopUpListAdapter);
                        organisationListPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                et_size.setText(company_sizes.get(i));
                                organisationListPopupWindow.dismiss();
                            }
                        });
                        organisationListPopupWindow.show();
                        break;
                }
            }
        };
    }


    private void hitEditOrganisationApi(String user_id, String oraganisation_id,
                                        String city, String description, String address,
                                        String noofemployee) {
        commonFunctions.openProgressDialog(getString(R.string.updating_organisation_details));
        // pb_loading.setVisibility(View.VISIBLE);
        tv_done_button.setVisibility(View.GONE);
        //sv_parent_wrapper.setVisibility(View.GONE);

        RequestBody user_id_body = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody organisation_id_body = RequestBody.create(MediaType.parse("text/plain"), oraganisation_id);
        RequestBody city_body = RequestBody.create(MediaType.parse("text/plain"), city);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);
        RequestBody addressBody = RequestBody.create(MediaType.parse("text/plain"), address);
        RequestBody noofemployeeBody = RequestBody.create(MediaType.parse("text/plain"), noofemployee);

        MultipartBody.Part fileToUpload = null;

        if (cover_photo != null) {
            RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(cover_photo)), cover_photo);
            fileToUpload = MultipartBody.Part.createFormData("logo", cover_photo.getName(), mFile);
        }

        Call<PojoNoDataResponse> call = apiService.completeCompanyRegistration(fileToUpload,
                user_id_body, organisation_id_body, city_body, descriptionBody, addressBody,
                noofemployeeBody);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                commonFunctions.closeProgressDialog();
                if (response != null && response.body() != null && response.body().status == 1) {
                    final Dialog confirmationDialog;
                    confirmationDialog = new MyCustomThemeDialog(CompleteOrganisationRegistrationActivity.this);
                    confirmationDialog.setContentView(R.layout.message_dialog);
                    TextView tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                    tv_dialog_description.setText(getString(R.string.updated_organisation_details));
                    Button bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);
                    bt_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                         /*   commonFunctions.setToastMessage(getApplicationContext(), message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);*/
                            confirmationDialog.dismiss();
                            finish();
                        }
                    });
                    confirmationDialog.setCancelable(false);
                    confirmationDialog.show();
                } else {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getApplicationContext().getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    pb_loading.setVisibility(View.GONE);
                    sv_parent_wrapper.setVisibility(View.VISIBLE);
                    tv_done_button.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.closeProgressDialog();
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_loading.setVisibility(View.GONE);
                sv_parent_wrapper.setVisibility(View.VISIBLE);
                tv_done_button.setVisibility(View.VISIBLE);
            }
        });

    }

    private boolean validateAllFields() {
        boolean allValid = true;
       /* if (selectedTypeId == null || selectedTypeId.isEmpty()) {
            til_type.setError(getString(R.string.please_choose_company_type));
            allValid = false;
            if (et_type.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_type.getWindowToken(), 0);
            }
        } else */
        if (et_size.getText().toString().trim().isEmpty()) {
            til_size.setError(getString(R.string.please_enter_company_size));
            allValid = false;
            if (et_size.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_size.getWindowToken(), 0);
            }
        } else if (et_about.getText().toString().trim().isEmpty()) {
            til_about.setError(getString(R.string.please_enter_company_description));
            allValid = false;
            if (et_about.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_about.getWindowToken(), 0);
            }
        } else if (et_address.getText().toString().trim().isEmpty()) {
            til_address.setError(getString(R.string.please_enter_company_address));
            allValid = false;
            if (et_address.requestFocus()) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_address.getWindowToken(), 0);
            }
        }
        return allValid;
    }

    private void setTextWatchers() {
        et_size.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    til_size.setError(getString(R.string.please_enter_company_size));
                } else {
                    til_size.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        et_about.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    til_about.setError(getString(R.string.please_enter_company_description));
                } else {
                    til_about.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_address.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    til_address.setError(getString(R.string.please_enter_company_address));
                } else {
                    til_about.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    til_address.setError(getString(R.string.please_enter_company_address));
                } else {
                    til_about.setError(null);
                }
            }
        });

        /*et_type.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!selectedType.equals(charSequence.toString().trim())) {
                    String query = charSequence.toString();
                    hitGetOrganisationTypeApi(user_id, query);
                }
                if (!charSequence.toString().isEmpty()) {
                    til_type.setError(null);
                } else {
                    til_type.setError(getString(R.string.please_choose_company_type));
                }
                selectedTypeId = null;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });*/

        et_size.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    private void requestStoragePermission(int requestType) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else*/
            {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE}, requestType);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.


            }

        } else {
            {
                if (requestType == PICK_IMAGE_REQUEST) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(CompleteOrganisationRegistrationActivity.this);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PICK_IMAGE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(CompleteOrganisationRegistrationActivity.this);
                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                et_address.setText(place.getAddress());
                til_address.setError(null);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                cover_photo = commonFunctions.getScaledDownImage(result.getUri().getPath());
                cover_photo_path = cover_photo.getPath();
                iv_logo.setImageURI(Uri.fromFile(cover_photo));
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.create_company_page_menu, menu);

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            /*case R.id.done:
                onBackPressed();
                break;*/
        }
        return true;
    }

}

