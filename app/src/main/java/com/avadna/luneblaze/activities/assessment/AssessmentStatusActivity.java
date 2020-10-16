package com.avadna.luneblaze.activities.assessment;

import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.payments.RazorPayPaymentActivity;
import com.avadna.luneblaze.adapters.AssessmentCategoryAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentTypeResponse;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentTypeResponseData;
import com.avadna.luneblaze.pojo.assessment.PojoInitiatePaymentResponse;
import com.avadna.luneblaze.pojo.pojoSettings.PojoGetAssessmentPaymentStatus;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssessmentStatusActivity extends AppBaseActivity {

    private static final int STORAGE_PERMISSION_REQUEST = 2351;
    private ProgressBar pb_loading_content;
    private ActionBar actionBar;
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id = "";

    private TextView tv_amount;
    private TextView tv_payment_status;
    private TextView tv_transaction_id;
    private TextView tv_transaction_id_header;
    private TextView tv_make_payment;
    private TextView tv_report_status;

    private CheckBox cb_i_agree;

    private LinearLayout ll_payment_pending;
    private LinearLayout ll_payment_done_wrapper;
    private boolean reportReady = false;

    private final static int PAYMENT_REQUEST_CODE = 2345;
    private String amount;
    private String fileUrl = "";
    private EditText et_category;
    private String selectedCategory = "", selectedCategoryId = "";
    private ListPopupWindow categoryListPopup;

    private String payment_id = "";

    private List<PojoAssessmentTypeResponseData> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assessment_payment);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(AssessmentStatusActivity.this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        categoryListPopup = new ListPopupWindow(AssessmentStatusActivity.this);
        setUpActionBar();
        initViews();
        payment_id = getIntent().getStringExtra(AppKeys.PAYMENT_ID);
        if (payment_id == null) {
            payment_id = "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hitGetAssessmentPaymentStatusApi(user_id, payment_id);
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.assessment_report));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(AssessmentStatusActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(AssessmentStatusActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        ll_payment_pending = (LinearLayout) findViewById(R.id.ll_payment_pending);
        ll_payment_done_wrapper = (LinearLayout) findViewById(R.id.ll_payment_done_wrapper);
        et_category = (EditText) findViewById(R.id.et_category);
        tv_amount = (TextView) findViewById(R.id.tv_amount);
        tv_payment_status = (TextView) findViewById(R.id.tv_payment_status);
        tv_transaction_id = (TextView) findViewById(R.id.tv_transaction_id);
        tv_transaction_id_header = (TextView) findViewById(R.id.tv_transaction_id_header);
        tv_make_payment = (TextView) findViewById(R.id.tv_make_payment);
        tv_report_status = (TextView) findViewById(R.id.tv_report_status);
        tv_make_payment.setEnabled(false);
        tv_make_payment.setAlpha(0.5f);
        cb_i_agree = (CheckBox) findViewById(R.id.cb_i_agree);
        cb_i_agree.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tv_make_payment.setAlpha(1f);
                } else {
                    tv_make_payment.setAlpha(0.5f);
                }
                tv_make_payment.setEnabled(isChecked);
            }
        });

        categories = new ArrayList<>();
        et_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssessmentCategoryAdapter assessmentCategoryAdapter =
                        new AssessmentCategoryAdapter(AssessmentStatusActivity.this,
                                categories);
                categoryListPopup.setAnchorView(et_category);
                categoryListPopup.setAdapter(assessmentCategoryAdapter);
                categoryListPopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedCategory = categories.get(i).name;
                        et_category.setText(selectedCategory);
                        selectedCategoryId = categories.get(i).id;
                        categoryListPopup.dismiss();
                    }
                });
                categoryListPopup.show();
            }
        });
        et_category.setEnabled(false);
    }

    private void hitGetAssessmentPaymentStatusApi(String user_id, String payment_id) {
        ll_payment_pending.setVisibility(View.GONE);
        ll_payment_done_wrapper.setVisibility(View.GONE);
        tv_make_payment.setVisibility(View.GONE);
        pb_loading_content.setVisibility(View.VISIBLE);

        if (payment_id.isEmpty()) {
            payment_id = "-1";
        }
        Call<PojoGetAssessmentPaymentStatus> call = apiService.getAssessmentPaymentStatus(user_id, payment_id);
        call.enqueue(new Callback<PojoGetAssessmentPaymentStatus>() {
            @Override
            public void onResponse(Call<PojoGetAssessmentPaymentStatus> call, Response<PojoGetAssessmentPaymentStatus> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    if (response.body().status == 1) {
                        tv_amount.setText("₹" + response.body().amount);
                        amount = response.body().amount;
                        hitGetAssessmentTypesApi(user_id);

                    } else if (response.body().status == 2) {
                        pb_loading_content.setVisibility(View.GONE);
                        ll_payment_done_wrapper.setVisibility(View.VISIBLE);
                        ll_payment_pending.setVisibility(View.GONE);
                        tv_transaction_id.setText(response.body().transactionId);
                        tv_make_payment.setVisibility(View.GONE);
                        reportReady = false;
                    } else if (response.body().status == 3) {
                        pb_loading_content.setVisibility(View.GONE);
                        ll_payment_done_wrapper.setVisibility(View.VISIBLE);
                        ll_payment_pending.setVisibility(View.GONE);
                        tv_transaction_id.setText(response.body().transactionId);
                        tv_report_status.setText(getString(R.string.report_complete));
                        tv_make_payment.setText(getString(R.string.download_report));
                        tv_make_payment.setVisibility(View.VISIBLE);
                        tv_make_payment.setAlpha(1);
                        tv_make_payment.setEnabled(true);
                        reportReady = true;
                        fileUrl = response.body().report;
                        tv_make_payment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestStoragePermission(STORAGE_PERMISSION_REQUEST);
                            }
                        });

                    } else {
                        pb_loading_content.setVisibility(View.GONE);
                        commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoGetAssessmentPaymentStatus> call, Throwable t) {
                // Log error here since request failed
                pb_loading_content.setVisibility(View.GONE);
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.assessment_report));

            }
        });
    }


    private void hitGetAssessmentTypesApi(String user_id) {
        Call<PojoAssessmentTypeResponse> call = apiService.getAssessmentTypes(user_id);

        call.enqueue(new Callback<PojoAssessmentTypeResponse>() {
            @Override
            public void onResponse(Call<PojoAssessmentTypeResponse> call, Response<PojoAssessmentTypeResponse> response) {
                String message = "";
                pb_loading_content.setVisibility(View.GONE);
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    if (response.body().status == 1) {
                        categories.clear();
                        categories.addAll(response.body().data);
                        et_category.setEnabled(true);
                        ll_payment_pending.setVisibility(View.VISIBLE);
                        ll_payment_done_wrapper.setVisibility(View.GONE);
                        tv_make_payment.setVisibility(View.VISIBLE);
                        reportReady = false;
                        tv_make_payment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (selectedCategoryId.isEmpty()) {
                                    commonFunctions.showMessageDialog(getString(R.string.please_choose_assessment_category));
                                } else {
                                    /*for(int i=2;i<30;i++){
                                        try {
                                            Thread.sleep(500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }*/

                                    hitInitiateAssessmentPaymentApi(user_id, amount, selectedCategoryId);

                                }
                            }
                        });

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoAssessmentTypeResponse> call, Throwable t) {
                // Log error here since request failed
                pb_loading_content.setVisibility(View.GONE);
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.assessment_report));

            }
        });
    }

    private void requestStoragePermission(int requestType) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    requestType);


        } else {

            if (requestType == STORAGE_PERMISSION_REQUEST) {
                downloadFile(fileUrl);
            }
        }
    }

    public void downloadFile(String link) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Luneblaze/Luneblaze_assessment/");
        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory()
                    + "/Luneblaze/Luneblaze_assessment/");
            wallpaperDirectory.mkdirs();
        }
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(link);
        String filename = "Luneblaze assessment_" + user_id + "." + fileExtension;
        File file = new File(direct, filename);
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = FileProvider.getUriForFile(AssessmentStatusActivity.this,
                    getApplicationContext().getPackageName() + ".fileprovider", file);
            intent.setDataAndType(fileUri, "application/" + fileExtension);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link))
                    .setTitle("Assessment report")// Title of the Download Notification
                    .setDescription("Downloading")// Description of the Download Notification
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)// Visibility of the download Notification
                    .setDestinationUri(Uri.fromFile(file))// Uri of the destination file
                    .setAllowedOverMetered(true)// Set if download is allowed on Mobile network
                    .setAllowedOverRoaming(true);// Set if download is allowed on roaming network

            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            long downloadID = downloadManager.enqueue(request);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadFile(fileUrl);
                } /*else {
                    cover_photo_path = textAsBitmapPath(pojoCreateSession);
                }*/
                return;
            }
        }
    }


    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetAssessmentPaymentStatusApi(user_id, payment_id);
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


    private void hitInitiateAssessmentPaymentApi(String user_id, String amount, String assessment_type_id) {
        Call<PojoInitiatePaymentResponse> call = apiService.initiateAssessmentPayment(user_id,
                amount, assessment_type_id);
        commonFunctions.openProgressDialog(getString(R.string.please_wait));
        call.enqueue(new Callback<PojoInitiatePaymentResponse>() {
            @Override
            public void onResponse(Call<PojoInitiatePaymentResponse> call,
                                   Response<PojoInitiatePaymentResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    startPaymentActivity(response);
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
                        Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                commonFunctions.closeProgressDialog();
            }
        });
    }

    private void startPaymentActivity(Response<PojoInitiatePaymentResponse> response) {
        Intent paymentIntent = new Intent(AssessmentStatusActivity.this,
                RazorPayPaymentActivity.class);
        if (response.body() != null) {
            paymentIntent.putExtra(AppKeys.PAYMENT_REMARKS, getString(R.string.assessment_payment));
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
                hitUpdateAssessmentPaymentStatusApi(user_id, amount, data);
            }
        }
    }

    private void hitUpdateAssessmentPaymentStatusApi(String user_id, String amount, Intent data) {
        /*if (!et_category.getText().toString().isEmpty()) {
            transaction_id = transaction_id + et_category.getText().toString().trim();
        }*/
        Call<PojoNoDataResponse> call = apiService.makeAssessmentPayment(user_id, amount,
                data.getStringExtra(AppKeys.PAYMENT_STATUS), selectedCategoryId,
                data.getStringExtra(AppKeys.TRANSACTION_ID), data.getStringExtra(AppKeys.ID),
                data.getStringExtra(AppKeys.PAYMENT_ID));
        commonFunctions.openProgressDialog(getString(R.string.please_wait));
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response.isSuccessful()) {
                    String message = "";
                    if (response.body() != null && response.body().status == 1) {
                        if (data.getStringExtra(AppKeys.PAYMENT_STATUS).equals(AppKeys.PAYMENT_COMPLETED)) {
                            message = response.body().message;
                            commonFunctions.setToastMessage(getApplicationContext(), message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                            setResult(RESULT_OK);
                            initViews();
                            Button bt_ok;
                            final Dialog confirmationDialog;
                            confirmationDialog = new MyCustomThemeDialog(AssessmentStatusActivity.this);
                            confirmationDialog.setContentView(R.layout.message_dialog);

                            TextView tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                            tv_dialog_description.setText(getString(R.string.successfully_applied_assessment));

                            bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

                            bt_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    confirmationDialog.dismiss();
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
                openNoConnectionDialog(data, getString(R.string.assessment_report));
                commonFunctions.closeProgressDialog();
            }
        });
    }


    private void openNoConnectionDialog(Intent data, String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitUpdateAssessmentPaymentStatusApi(user_id, amount, data);
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
                break;
        }
        return true;
    }
}
