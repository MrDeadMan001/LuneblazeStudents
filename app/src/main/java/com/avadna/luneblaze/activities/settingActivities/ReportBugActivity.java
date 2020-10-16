package com.avadna.luneblaze.activities.settingActivities;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUpdateAppSettingsResponse;
import com.avadna.luneblaze.rest.ApiClientLongDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportBugActivity extends AppBaseActivity {
    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    Spinner sp_bug_list;
    ArrayAdapter<String> bugSpinnerAdapter;
    List<String> bugListOptions;

    EditText et_description;
    TextView tv_submit;
    TextView tv_attach_image;
    ImageView iv_image;
    String imagePath="";

    RelativeLayout rl_parent_wrapper;
    ProgressBar pb_loading;

    View.OnClickListener onClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_report_bug);
        apiService = ApiClientLongDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions=new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initViews();
        initBugListSpinner();
    }


    private void initViews() {
        et_description = (EditText) findViewById(R.id.et_description);
        tv_submit = (TextView) findViewById(R.id.tv_submit);
        tv_attach_image = (TextView) findViewById(R.id.tv_attach_image);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        rl_parent_wrapper= (RelativeLayout) findViewById(R.id.rl_parent_wrapper);
        pb_loading= (ProgressBar) findViewById(R.id.pb_loading);
        pb_loading.setVisibility(View.GONE);

        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitReportBugApi(user_id, "report_bugs",
                        et_description.getText().toString().trim(),
                        bugListOptions.get(sp_bug_list.getSelectedItemPosition()),imagePath);
            }
        });

        tv_attach_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ReportBugActivity.this);
            }
        });
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.report_bug));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(ReportBugActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(ReportBugActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initBugListSpinner() {
        sp_bug_list=(Spinner)findViewById(R.id.sp_bug_list);
        bugListOptions = new ArrayList<String>();
        bugListOptions.add(getString(R.string.interest));
        bugListOptions.add(getString(R.string.question));
        bugListOptions.add(getString(R.string.session));
        bugListOptions.add(getString(R.string.post));
        bugListOptions.add(getString(R.string.venue));
        bugListOptions.add(getString(R.string.article));
        bugListOptions.add(getString(R.string.poll));


        bugSpinnerAdapter = new ArrayAdapter<String>(ReportBugActivity.this, R.layout.custom_spinner_dropdown_item, bugListOptions);
        bugSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        sp_bug_list.setAdapter(bugSpinnerAdapter);
    }

    private void hitReportBugApi(String user_id, String type, String reportBugs, String section, String imagePath) {

        pb_loading.setVisibility(View.VISIBLE);
        rl_parent_wrapper.setVisibility(View.GONE);
        tv_submit.setVisibility(View.GONE);
        section=section.toLowerCase();

        RequestBody user_id_body = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody typeBody = RequestBody.create(MediaType.parse("text/plain"), type);
        RequestBody reportBugsBody = RequestBody.create(MediaType.parse("text/plain"), reportBugs);
        RequestBody sectionBody = RequestBody.create(MediaType.parse("text/plain"), section);

        MultipartBody.Part fileToUpload=null;

        if(!imagePath.isEmpty()){
            File file = commonFunctions.getScaledDownImage(imagePath);
            // file = getScaledDownImage(file);
            RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(file)), file);
            fileToUpload= MultipartBody.Part.createFormData("screenshot", file.getName(), mFile);
        }

        Call<PojoUpdateAppSettingsResponse> call = apiService.reportBugWithImage(fileToUpload,
                user_id_body, typeBody, reportBugsBody,sectionBody);
        call.enqueue(new Callback<PojoUpdateAppSettingsResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateAppSettingsResponse> call, Response<PojoUpdateAppSettingsResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.thank_for_bug_report),
                            Toast.LENGTH_LONG,AppKeys.TOAST_USER);
                    finish();
                }
                else {
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG,AppKeys.TOAST_USER);
                    pb_loading.setVisibility(View.GONE);
                    rl_parent_wrapper.setVisibility(View.VISIBLE);
                    tv_submit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PojoUpdateAppSettingsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                pb_loading.setVisibility(View.GONE);
                rl_parent_wrapper.setVisibility(View.VISIBLE);
                tv_submit.setVisibility(View.VISIBLE);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                iv_image.setVisibility(View.VISIBLE);
                File imgFile=commonFunctions.getScaledDownImage(result.getUri().getPath());
                iv_image.setImageURI(Uri.fromFile(imgFile));
                imagePath=Uri.fromFile(imgFile).getPath();

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
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
