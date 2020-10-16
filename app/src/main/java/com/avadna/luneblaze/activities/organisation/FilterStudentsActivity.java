package com.avadna.luneblaze.activities.organisation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.assessment.AssessmentCategoryListAdapter;
import com.avadna.luneblaze.adapters.assessment.AssessmentDomainCategoryAdapter;
import com.avadna.luneblaze.adapters.assessment.TaggedAssessmentCategoriesAdapter;
import com.avadna.luneblaze.adapters.organisation.EligibleCollegeListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoEligibilityFilters;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;
import com.avadna.luneblaze.pojo.assessment.PojoGetAssessmentCategoriesResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetCollegeListForDriveResponse;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetCollegeListForDriveResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FilterStudentsActivity extends AppBaseActivity
        implements AssessmentDomainCategoryAdapter.AssessmentDomainCategoryListCallback,
        TaggedAssessmentCategoriesAdapter.TaggedAssessmentCategoriesAdapterCallback,
        AssessmentCategoryListAdapter.AssessmentCategoryListAdapterCallback {

    private ActionBar actionBar;

    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;

    private ProgressBar pb_loading_content;
    private RelativeLayout rl_content_wrapper;
    private AppCompatSeekBar sb_session_count_bar;

    private ImageButton ib_expand_domain;
    private RecyclerView rv_domain_categories;
    private ArrayList<PojoAssessmentCategory> domainCategoryList;
    private AssessmentDomainCategoryAdapter domainCategoryAdapter;

    private RecyclerView rv_non_domain_categories;
    private ArrayList<PojoAssessmentCategory> nonDomainCategoryList;
    private AssessmentDomainCategoryAdapter nonDomainCategoryAdapter;

    private ArrayList<PojoAssessmentCategory> selectedCategoryList;

    private TextView tv_length_getter;
    private TextView tv_see_results;
    private TextView tv_done;
    //  private  EditText et_number_of_sessions;
    private TextView tv_num_sessions;
    private RadioButton rb_both, rb_attend, rb_conduct;
    private DisplayMetrics displayMetrics;
    private int screenHeight, screenWidth;


    private View.OnClickListener onClickListener;

    private boolean isGetCollegeListApiCalled = false;

    private RecyclerView rv_upvoters;
    private EligibleCollegeListAdapter eligibleCollegeListAdapter;
    private List<PojoGetCollegeListForDriveResponseData> collegeList;


    private RecyclerView rv_sub_categories;
    private AssessmentCategoryListAdapter assessmentSubCategoryListAdapter;
    private final String OVERALL_CAT = "-1";

    ProgressBar pb_loading_college_list;
    private TextView tv_no_students;
    private PojoEligibilityFilters pojoEligibilityFilters;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_students);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initDispMetrics();
        initViews();
        initClickListener();
        setClickListener();
        initFilterValues();
        hitGetAssessmentCategoriesApi(user_id);
    }

    private void initFilterValues() {
        Type type = new TypeToken<PojoEligibilityFilters>() {
        }.getType();

        String dataStr = getIntent().getStringExtra(AppKeys.ELIGIBILITY_FILTERS);
        pojoEligibilityFilters = new Gson().fromJson(dataStr, type);
        if (pojoEligibilityFilters != null) {
            tv_num_sessions.setText(pojoEligibilityFilters.no_of_sessions);
            setSelectedSessionRole(pojoEligibilityFilters.session_role);
            sb_session_count_bar.setProgress(Integer.parseInt(pojoEligibilityFilters.no_of_sessions));
        }
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            //   actionBar.setTitle(getString(R.string.comments) + " (" + comment_count + ")");
            actionBar.setTitle(getString(R.string.eligibility_criteria));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(FilterStudentsActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(FilterStudentsActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void initViews() {

        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        rl_content_wrapper.setVisibility(View.GONE);
        pb_loading_content.setVisibility(View.VISIBLE);

        selectedCategoryList = new ArrayList<>();
        ib_expand_domain = (ImageButton) findViewById(R.id.ib_expand_domain);
        tv_length_getter = (TextView) findViewById(R.id.tv_length_getter);
        tv_num_sessions = (TextView) findViewById(R.id.tv_num_sessions);
        //et_number_of_sessions = (EditText) findViewById(R.id.et_number_of_sessions);
        tv_see_results = (TextView) findViewById(R.id.tv_see_results);
        tv_done = (TextView) findViewById(R.id.tv_done);
        rb_both = (RadioButton) findViewById(R.id.rb_both);
        rb_attend = (RadioButton) findViewById(R.id.rb_attend);
        rb_conduct = (RadioButton) findViewById(R.id.rb_conduct);
        sb_session_count_bar = (AppCompatSeekBar) findViewById(R.id.sb_session_count_bar);

        sb_session_count_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // et_number_of_sessions.setText(String.valueOf(i));
                tv_num_sessions.setText(String.valueOf(i));

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        domainCategoryList = new ArrayList<>();
        rv_domain_categories = (RecyclerView) findViewById(R.id.rv_domain_categories);
        rv_domain_categories.setLayoutManager
                (new LinearLayoutManager(FilterStudentsActivity.this,
                        RecyclerView.VERTICAL, false));
        rv_domain_categories.setNestedScrollingEnabled(false);
        domainCategoryAdapter = new AssessmentDomainCategoryAdapter(FilterStudentsActivity.this,
                domainCategoryList, AppKeys.DOMAIN);
        rv_domain_categories.setAdapter(domainCategoryAdapter);

        nonDomainCategoryList = new ArrayList<>();
        rv_non_domain_categories = (RecyclerView) findViewById(R.id.rv_non_domain_categories);
        rv_non_domain_categories.setLayoutManager
                (new LinearLayoutManager(FilterStudentsActivity.this,
                        RecyclerView.VERTICAL, false));
        rv_non_domain_categories.setNestedScrollingEnabled(false);
        nonDomainCategoryAdapter = new AssessmentDomainCategoryAdapter(FilterStudentsActivity.this,
                nonDomainCategoryList, AppKeys.NON_DOMAIN);
        rv_non_domain_categories.setAdapter(nonDomainCategoryAdapter);


    }

    public String getSelectedSessionRole() {
        String role = "Both";
        if (rb_both.isChecked()) {
            role = "Both";
        } else if (rb_conduct.isChecked()) {
            role = "Conducted";
        } else if (rb_attend.isChecked()) {
            role = "Attended";
        }
        return role;
    }

    public void setSelectedSessionRole(String role) {
        if (role.equals("Both") && rb_both != null) {
            rb_both.setChecked(true);
        } else if (role.equals("Conducted") && rb_conduct != null) {
            rb_conduct.setChecked(true);
        } else if (role.equals("Attended") && rb_attend != null) {
            rb_attend.setChecked(true);
        }
    }


    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_see_results: {
                        openResultsDialog();
                    }
                    break;

                    case R.id.ib_expand_domain:
                        if (rv_domain_categories.getVisibility() == View.GONE) {
                            rv_domain_categories.setVisibility(View.VISIBLE);
                            ib_expand_domain.setRotation(180);
                        } else {
                            rv_domain_categories.setVisibility(View.GONE);
                            ib_expand_domain.setRotation(0);
                        }
                        break;
                    case R.id.tv_done: {
                        addDomainCategoriesRecur();
                        addNonDomainCategoriesRecur();
                        PojoEligibilityFilters pojoEligibilityFilters = new PojoEligibilityFilters();
                        pojoEligibilityFilters.no_of_sessions = tv_num_sessions.getText().toString();

                        //pojoEligibilityFilters.no_of_sessions = et_number_of_sessions.getText().toString();
                        pojoEligibilityFilters.session_role = getSelectedSessionRole();
                        pojoEligibilityFilters.selectedCategoryList = selectedCategoryList;
                        pojoEligibilityFilters.domainCategoryList = domainCategoryList;
                        pojoEligibilityFilters.nonDomainCategoryList = nonDomainCategoryList;
                        String dataStr = new Gson().toJson(
                                pojoEligibilityFilters,
                                new TypeToken<PojoEligibilityFilters>() {
                                }.getType());
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra(AppKeys.ELIGIBILITY_FILTERS, dataStr);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    }
                    break;
                }
            }
        };
    }

    private void hitGetAssessmentCategoriesApi(String user_id) {
        rl_content_wrapper.setVisibility(View.GONE);
        pb_loading_content.setVisibility(View.VISIBLE);
        if (pojoEligibilityFilters != null
                && pojoEligibilityFilters.domainCategoryList != null
                && !pojoEligibilityFilters.domainCategoryList.isEmpty()
                && pojoEligibilityFilters.nonDomainCategoryList != null
                && !pojoEligibilityFilters.nonDomainCategoryList.isEmpty()) {
            domainCategoryList.clear();
            domainCategoryList.addAll(pojoEligibilityFilters.domainCategoryList);
            domainCategoryAdapter.notifyDataSetChanged();

            nonDomainCategoryList.clear();
            nonDomainCategoryList.addAll(pojoEligibilityFilters.nonDomainCategoryList);
            nonDomainCategoryAdapter.notifyDataSetChanged();
            rl_content_wrapper.setVisibility(View.VISIBLE);
            pb_loading_content.setVisibility(View.GONE);
        } else {
            Call<PojoGetAssessmentCategoriesResponse> call = apiService.getAssessmentCategories(user_id);
            call.enqueue(new Callback<PojoGetAssessmentCategoriesResponse>() {
                @Override
                public void onResponse(Call<PojoGetAssessmentCategoriesResponse> call, Response<PojoGetAssessmentCategoriesResponse> response) {
                    if (response.body() != null) {
                        pb_loading_content.setVisibility(View.GONE);
                        rl_content_wrapper.setVisibility(View.VISIBLE);
                        preferenceUtils.saveAssessmentCategoriesList(response.body().data);
                        nonDomainCategoryList.clear();
                        domainCategoryList.clear();
                        for (int i = 0; i < response.body().data.size(); i++) {
                            if (response.body().data.get(i).categoryId.equals("3")) {
                                domainCategoryList.addAll(response.body().data.get(i).child);

                            } else {
                                nonDomainCategoryList.add(response.body().data.get(i));
                            }
                        }
                        //add overall category to each parent category
                        for (int j = 0; j < domainCategoryList.size(); j++) {
                            PojoAssessmentCategory overAllCat = new PojoAssessmentCategory(OVERALL_CAT,
                                    getApplicationContext().getString(R.string.overall_in)
                                            + domainCategoryList.get(j).text,
                                    domainCategoryList.get(j).categoryId, "1",
                                    new ArrayList<>());
                            domainCategoryList.get(j).child.add(0, overAllCat);
                        }

                        //add overall category to each parent category
                        for (int j = 0; j < nonDomainCategoryList.size(); j++) {
                            PojoAssessmentCategory overAllCat = new PojoAssessmentCategory(OVERALL_CAT,
                                    getApplicationContext().getString(R.string.overall_in)
                                            + nonDomainCategoryList.get(j).text,
                                    nonDomainCategoryList.get(j).categoryId, "1",
                                    new ArrayList<>());
                            nonDomainCategoryList.get(j).child.add(0, overAllCat);
                        }
                        domainCategoryAdapter.notifyDataSetChanged();
                        nonDomainCategoryAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<PojoGetAssessmentCategoriesResponse> call, Throwable t) {
                    // Log error here since request failed
                    int a = 5;
                    pb_loading_content.setVisibility(View.GONE);
                    openNoConnectionDialog(getString(R.string.assessment_categories));
                }
            });
        }
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetAssessmentCategoriesApi(user_id);
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


    private void openResultsDialog() {
        TextView tv_title;
        ImageView iv_back;

        final Dialog userListDialog = new MyCustomThemeDialog(this);
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        pb_loading_college_list = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);
        tv_no_students = (TextView) userListDialog.findViewById(R.id.tv_no_students);

        tv_title = userListDialog.findViewById(R.id.tv_title);
        tv_title.setText(this.getString(R.string.eligibility_list));
        iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);

        String finalRole = getSelectedSessionRole();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* PojoEligibilityFilters pojoEligibilityFilters = new PojoEligibilityFilters();
                pojoEligibilityFilters.no_of_sessions = tv_num_sessions.getText().toString();

                //pojoEligibilityFilters.no_of_sessions = et_number_of_sessions.getText().toString();
                pojoEligibilityFilters.session_role = finalRole;
                pojoEligibilityFilters.selectedCategoryList = selectedCategoryList;
                String dataStr = new Gson().toJson(
                        pojoEligibilityFilters,
                        new TypeToken<PojoEligibilityFilters>() {
                        }.getType());
                Intent resultIntent = new Intent();
                resultIntent.putExtra(dataStr, AppKeys.ELIGIBILITY_FILTERS);
                setResult(RESULT_OK, resultIntent);*/
                userListDialog.dismiss();
                // finish();
            }
        });
        userListDialog.setTitle(this.getString(R.string.eligibility_list));

        rv_upvoters = (RecyclerView) userListDialog.findViewById(R.id.rv_list);
        //rv_list.setPadding(16,32,16,32);
        collegeList = new ArrayList<>();
        rv_upvoters.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false));
        eligibleCollegeListAdapter = new EligibleCollegeListAdapter(this, collegeList);
        rv_upvoters.setAdapter(eligibleCollegeListAdapter);


        selectedCategoryList.clear();
        addDomainCategoriesRecur();
        addNonDomainCategoriesRecur();
        hitGetCollegeListApi(user_id, tv_num_sessions.getText().toString(), getSelectedSessionRole(),
                selectedCategoryList);
        userListDialog.show();
    }


    private void hitGetCollegeListApi(String user_id, String noofsession, String session_roles,
                                      ArrayList<PojoAssessmentCategory> selectedCategoryList) {

        HashMap<String, String> interestMap = new HashMap<>();
        HashMap<String, String> categoryMap = new HashMap<>();
        HashMap<String, String> dependencyMap = new HashMap<>();
        HashMap<String, String> andOrMap = new HashMap<>();

        for (int i = 0; i < selectedCategoryList.size(); i++) {
            categoryMap.put("categories[" + i + "]", selectedCategoryList.get(i).categoryId);

            if (selectedCategoryList.get(i).marksDependency) {
                dependencyMap.put("dependency[" + i + "]", "1");
            } else {
                dependencyMap.put("dependency[" + i + "]", "0");
            }
            andOrMap.put("and_or[" + i + "]", selectedCategoryList.get(i).and_or + "");
        }

        if (!isGetCollegeListApiCalled) {
            //tv_no_questions.setVisibility(View.GONE);

            isGetCollegeListApiCalled = true;
            Call<PojoGetCollegeListForDriveResponse> call =
                    apiService.getStudentsPerCollege(user_id, noofsession, session_roles,
                            interestMap, categoryMap, dependencyMap, andOrMap);
            call.enqueue(new Callback<PojoGetCollegeListForDriveResponse>() {
                @Override
                public void onResponse(Call<PojoGetCollegeListForDriveResponse> call,
                                       Response<PojoGetCollegeListForDriveResponse> response) {

                    String message = "";
                    if (response != null && response.body() != null && response.body().data != null) {
                        message = response.body().message;
                        collegeList.clear();
                        collegeList.addAll(response.body().data);
                        eligibleCollegeListAdapter.notifyDataSetChanged();
                        isGetCollegeListApiCalled = false;
                        if (pb_loading_college_list != null) {
                            pb_loading_college_list.setVisibility(View.GONE);
                        }

                        if (tv_no_students != null) {
                            if (collegeList.isEmpty()) {
                                tv_no_students.setVisibility(View.VISIBLE);
                            } else {
                                tv_no_students.setVisibility(View.VISIBLE);
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<PojoGetCollegeListForDriveResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (FilterStudentsActivity.this != null)
                        commonFunctions.setToastMessage(getApplicationContext(),
                                t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    //  ll_parent.setVisibility(View.VISIBLE);
                    isGetCollegeListApiCalled = false;
                    if (pb_loading_college_list != null) {
                        pb_loading_college_list.setVisibility(View.GONE);
                    }
                }
            });
        }

    }


    private void setClickListener() {
        //tv_add_categories.setOnClickListener(onClickListener);
        // tv_remove_categories.setOnClickListener(onClickListener);
        tv_see_results.setOnClickListener(onClickListener);
        tv_done.setOnClickListener(onClickListener);
        ib_expand_domain.setOnClickListener(onClickListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void addDomainCategoriesRecur() {
        for (int i = 0; i < domainCategoryList.size(); i++) {
            if (domainCategoryList.get(i).isCbChecked) {
                //if above is selected then use marks dependency
                if (domainCategoryList.get(i).cb_type.equals(AppKeys.ABOVE)) {
                    for (int j = 0; j < domainCategoryList.get(i).child.size(); j++) {
                        if (domainCategoryList.get(i).child.get(j).isCbChecked) {
                            if (domainCategoryList.get(i).child.get(j).categoryId.equals(OVERALL_CAT)) {
                                removeFromList(domainCategoryList.get(i));
                                addToListWithDependency(domainCategoryList.get(i));
                                break;
                            } else {
                                removeFromList(domainCategoryList.get(i).child.get(j));
                                addToListWithDependency(domainCategoryList.get(i).child.get(j));
                            }
                        }
                    }
                }
                //if any or all is selected then no need to use marks dependency
                else {
                    removeFromList(domainCategoryList.get(i));
                    addToListWithoutDependency(domainCategoryList.get(i));
                }
            }
        }
    }

    private void addNonDomainCategoriesRecur() {
        for (int i = 0; i < nonDomainCategoryList.size(); i++) {
            if (nonDomainCategoryList.get(i).isCbChecked) {
                if (nonDomainCategoryList.get(i).cb_type.equals(AppKeys.ABOVE)) {
                    for (int j = 0; j < nonDomainCategoryList.get(i).child.size(); j++) {
                        if (nonDomainCategoryList.get(i).child.get(j).isCbChecked) {
                            if (nonDomainCategoryList.get(i).child.get(j).categoryId.equals(OVERALL_CAT)) {
                                removeFromList(nonDomainCategoryList.get(i));
                                addToListWithDependency(nonDomainCategoryList.get(i));
                                break;
                            } else {
                                removeFromList(nonDomainCategoryList.get(i).child.get(j));
                                addToListWithDependency(nonDomainCategoryList.get(i).child.get(j));
                            }
                        }
                    }
                } else {
                    removeFromList(nonDomainCategoryList.get(i));
                    addToListWithoutDependency(nonDomainCategoryList.get(i));
                }
            }
        }
    }


    @Override
    public void onAssessmentDomainCategoryItemClick(int position, PojoAssessmentCategory item,
                                                    String clickType, String listType) {
        switch (clickType) {
            case AppKeys.ALL:
                // removeFromList(item);
                // addToListWithoutDependency(item);
                setCategoryChecked(item, clickType, listType);
                break;

            case AppKeys.ANY:
                //removeFromList(item);
                // addToListWithoutDependency(item);
                setCategoryChecked(item, clickType, listType);
                break;

            case AppKeys.ABOVE:
                openSubcategoryDialog(position, listType);
                setCategoryChecked(item, clickType, listType);
                break;

            default:
                break;

        }
    }

    private void setCategoryChecked(PojoAssessmentCategory item, String clickType, String listType) {
        switch (listType) {
            case AppKeys.DOMAIN:
                for (int i = 0; i < domainCategoryList.size(); i++) {
                    if (domainCategoryList.get(i).categoryId.equals(item.categoryId)) {
                        //if select category checkbox is clicked
                        if (clickType.equals(AppKeys.ALL)) {
                            domainCategoryList.get(i).isCbChecked = !domainCategoryList.get(i).isCbChecked;
                            //if a main category is selected
                            if (domainCategoryList.get(i).isCbChecked) {
                                domainCategoryList.get(i).cb_type = AppKeys.ANY;
                            }
                            //if a main category is un selected
                            else {
                                domainCategoryList.get(i).cb_type = null;
                                //deselect all its children
                                for (int j = 0; j < domainCategoryList.get(i).child.size(); j++) {
                                    domainCategoryList.get(i).child.get(j).isCbChecked = false;
                                }
                            }
                        }

                        //if above or any radio button is clicked
                        if (clickType.equals(AppKeys.ABOVE) || clickType.equals(AppKeys.ANY)) {
                            domainCategoryList.get(i).isCbChecked = true;
                            domainCategoryList.get(i).cb_type = clickType;

                            if (clickType.equals(AppKeys.ANY)) {
                                //deselect all its children
                                for (int j = 0; j < domainCategoryList.get(i).child.size(); j++) {
                                    domainCategoryList.get(i).child.get(j).isCbChecked = false;
                                }
                            }
                        }
                        domainCategoryAdapter.notifyDataSetChanged();
                    }
                }
                break;

            case AppKeys.NON_DOMAIN:
                for (int i = 0; i < nonDomainCategoryList.size(); i++) {
                    if (nonDomainCategoryList.get(i).categoryId.equals(item.categoryId)) {
                        //if select category checkbox is clicked
                        if (clickType.equals(AppKeys.ALL)) {
                            nonDomainCategoryList.get(i).isCbChecked = !nonDomainCategoryList.get(i).isCbChecked;
                            //if a main category is selected
                            if (nonDomainCategoryList.get(i).isCbChecked) {
                                nonDomainCategoryList.get(i).cb_type = AppKeys.ANY;
                            }
                            //if a main category is un selected
                            else {
                                nonDomainCategoryList.get(i).cb_type = null;
                                //deselect all its children
                                for (int j = 0; j < nonDomainCategoryList.get(i).child.size(); j++) {
                                    nonDomainCategoryList.get(i).child.get(j).isCbChecked = false;
                                }
                            }
                        }

                        //if above or any radio button is clicked
                        if (clickType.equals(AppKeys.ABOVE) || clickType.equals(AppKeys.ANY)) {
                            nonDomainCategoryList.get(i).isCbChecked = true;
                            nonDomainCategoryList.get(i).cb_type = clickType;
                            if (clickType.equals(AppKeys.ANY)) {
                                //deselect all its children
                                for (int j = 0; j < nonDomainCategoryList.get(i).child.size(); j++) {
                                    nonDomainCategoryList.get(i).child.get(j).isCbChecked = false;
                                }
                            }
                        }
                        nonDomainCategoryAdapter.notifyDataSetChanged();
                    }
                }
                break;
        }
    }


    public void addToListWithDependency(PojoAssessmentCategory item) {
        item.marksDependency = true;
        selectedCategoryList.add(item);
    }

    public void addToListWithoutDependency(PojoAssessmentCategory item) {
        item.marksDependency = false;
        selectedCategoryList.add(item);
    }

    public void removeFromList(PojoAssessmentCategory item) {
        for (int i = 0; i < selectedCategoryList.size(); i++) {
            if (item.categoryId.equals(selectedCategoryList.get(i).categoryId)) {
                selectedCategoryList.remove(i);
            }
        }
    }

    private void openSubcategoryDialog(int position, String listType) {

        final Dialog dialog;
        dialog = new MyCustomThemeDialog(FilterStudentsActivity.this);
        dialog.setContentView(R.layout.sub_category_dialog);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        LinearLayout ll_all_category = (LinearLayout) dialog.findViewById(R.id.ll_all_category);
        LinearLayout ll_any_category = (LinearLayout) dialog.findViewById(R.id.ll_any_category);
        RadioButton rb_any = (RadioButton) dialog.findViewById(R.id.rb_any);
        RadioButton rb_all = (RadioButton) dialog.findViewById(R.id.rb_all);

        rv_sub_categories = (RecyclerView) dialog.findViewById(R.id.rv_sub_categories);

        ll_all_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_any.setChecked(false);
                rb_all.setChecked(true);
            }
        });

        ll_any_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rb_any.setChecked(true);
                rb_all.setChecked(false);
            }
        });

        rb_any.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_any_category.callOnClick();
            }
        });

        rb_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ll_all_category.callOnClick();
            }
        });

        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listType.equals(AppKeys.DOMAIN)) {
                    //if none were selected then change parent to any
                    boolean noChecked = true;
                    for (int i = 0; i < domainCategoryList.get(position).child.size(); i++) {
                        if (domainCategoryList.get(position).child.get(i).isCbChecked) {
                            noChecked = false;
                            break;
                        }
                    }
                    if (noChecked) {
                        domainCategoryList.get(position).cb_type = AppKeys.ANY;
                    }
                    domainCategoryAdapter.notifyDataSetChanged();

                } else {
                    //if none were selected then change parent to any
                    boolean noChecked = true;
                    for (int i = 0; i < nonDomainCategoryList.get(position).child.size(); i++) {
                        if (nonDomainCategoryList.get(position).child.get(i).isCbChecked) {
                            noChecked = false;
                            break;
                        }
                    }
                    if (noChecked) {
                        nonDomainCategoryList.get(position).cb_type = AppKeys.ANY;
                    }
                    nonDomainCategoryAdapter.notifyDataSetChanged();
                }
                dialog.dismiss();

                //set and_or variable for selected categories
                int and_or = 0;
                if (rb_all.isChecked()) {
                    and_or = 1;
                }
                if (listType.equals(AppKeys.DOMAIN)) {
                    for (int i = 0; i < domainCategoryList.get(position).child.size(); i++) {
                        if (domainCategoryList.get(position).child.get(i).isCbChecked) {
                            domainCategoryList.get(position).child.get(i).and_or = and_or;
                        }
                       /* for (int j = 0; j < selectedCategoryList.size(); j++) {
                            if (domainCategoryList.get(position).child.get(i).categoryId
                                    .equals(selectedCategoryList.get(j).categoryId)) {
                                selectedCategoryList.get(j).and_or = and_or;
                            }
                        }*/
                    }
                } else {
                    for (int i = 0; i < nonDomainCategoryList.get(position).child.size(); i++) {
                        if (nonDomainCategoryList.get(position).child.get(i).isCbChecked) {
                            nonDomainCategoryList.get(position).child.get(i).and_or = and_or;
                        }
                       /* for (int j = 0; j < selectedCategoryList.size(); j++) {
                            if (nonDomainCategoryList.get(position).child.get(i).categoryId
                                    .equals(selectedCategoryList.get(j).categoryId)) {
                                selectedCategoryList.get(j).and_or = and_or;
                            }
                        }*/
                    }
                }
            }
        });

        if (listType.equals(AppKeys.DOMAIN)) {
            assessmentSubCategoryListAdapter =
                    new AssessmentCategoryListAdapter(FilterStudentsActivity.this,
                            domainCategoryList.get(position).child, listType);
        } else {
            assessmentSubCategoryListAdapter =
                    new AssessmentCategoryListAdapter(FilterStudentsActivity.this,
                            nonDomainCategoryList.get(position).child, listType);
        }
        rv_sub_categories.setLayoutManager(new LinearLayoutManager(FilterStudentsActivity.this,
                RecyclerView.VERTICAL, false));
        rv_sub_categories.setAdapter(assessmentSubCategoryListAdapter);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    public void onTaggedAssessmentCategoryItemClick(int position, PojoAssessmentCategory pojoAssessmentCategory) {

    }

    @Override
    public void onAssessmentCategoryListItemClick(int position, String type, Boolean cbChecked,
                                                  PojoAssessmentCategory assessmentCategory, String listType) {
        switch (listType) {
            case AppKeys.DOMAIN:
                for (int i = 0; i < domainCategoryList.size(); i++) {
                    if (assessmentCategory.parentId.equals(domainCategoryList.get(i).categoryId)) {
                        //flip the checked condition
                        domainCategoryList.get(i).child.get(position).isCbChecked =
                                !domainCategoryList.get(i).child.get(position).isCbChecked;

                        //if clicked sub cat was overall cat then deselect all siblings
                        if (assessmentCategory.categoryId.equals(OVERALL_CAT)) {
                            for (int j = 1; j < domainCategoryList.get(i).child.size(); j++) {
                                domainCategoryList.get(i).child.get(j).isCbChecked = false;
                            }
                        }


                        /*//if clicked category was overall category then add/remove parent
                        if (domainCategoryList.get(i).child.get(position).categoryId.equals(OVERALL_CAT)) {
                            removeFromList(domainCategoryList.get(i));
                            if (domainCategoryList.get(i).child.get(position).isCbChecked) {
                                addToListWithDependency(domainCategoryList.get(i));
                            }
                        }
                        //else add/remove the clicked category
                        else {
                            removeFromList(domainCategoryList.get(i).child.get(position));
                            if (domainCategoryList.get(i).child.get(position).isCbChecked) {
                                addToListWithDependency(domainCategoryList.get(i).child.get(position));
                            }
                        }*/
                        assessmentSubCategoryListAdapter.notifyDataSetChanged();

                        break;
                    }
                }
                break;

            case AppKeys.NON_DOMAIN:
                for (int i = 0; i < nonDomainCategoryList.size(); i++) {
                    if (assessmentCategory.parentId.equals(nonDomainCategoryList.get(i).categoryId)) {
                        //flip the checked condition
                        nonDomainCategoryList.get(i).child.get(position).isCbChecked =
                                !nonDomainCategoryList.get(i).child.get(position).isCbChecked;

                        //if clicked sub cat was overall cat then deselect all siblings
                        if (assessmentCategory.categoryId.equals(OVERALL_CAT)) {
                            for (int j = 1; j < nonDomainCategoryList.get(i).child.size(); j++) {
                                nonDomainCategoryList.get(i).child.get(j).isCbChecked = false;
                            }
                        }

                        /*//if clicked category was overall category then add/remove parent
                        if (nonDomainCategoryList.get(i).child.get(position).categoryId.equals(OVERALL_CAT)) {
                            removeFromList(nonDomainCategoryList.get(i));
                            if (nonDomainCategoryList.get(i).child.get(position).isCbChecked) {
                                addToListWithDependency(nonDomainCategoryList.get(i));
                            }
                        }
                        //else add/remove the clicked category
                        else {
                            removeFromList(nonDomainCategoryList.get(i).child.get(position));
                            if (nonDomainCategoryList.get(i).child.get(position).isCbChecked) {
                                addToListWithDependency(nonDomainCategoryList.get(i).child.get(position));
                            }
                        }*/
                        assessmentSubCategoryListAdapter.notifyDataSetChanged();

                        break;
                    }
                }
                break;
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
