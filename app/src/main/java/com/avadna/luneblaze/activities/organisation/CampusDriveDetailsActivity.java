package com.avadna.luneblaze.activities.organisation;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.adapters.organisation.VacancyListAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoCampusDriveListResponseData;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoFilterCriteria;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoJobListItem;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.instamojo.android.activities.BaseActivity;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CampusDriveDetailsActivity extends BaseActivity implements
        VacancyListAdapter.VacancyListAdapterCallback,
        TaggedInterestAdapter.TaggedInterestAdapterCallback {
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String organization_id = "2";
    private PojoCampusDriveListResponseData campusDrive;

    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;

    private ActionBar actionBar;
    private RecyclerView rv_vacancy_list;
    private VacancyListAdapter vacancyListAdapter;
    private List<PojoJobListItem> vacancyList;

    private TextView tv_venue;
    private TextView tv_date;
    private TextView tv_num_sessions;
    private TextView tv_session_role;

    private RecyclerView rv_tagged_interests_list;
    private List<PojoGetInterestListResponseDataListItem> taggedInterestList;
    private TaggedInterestAdapter taggedInterestAdapter;
    private GridLayoutManager gridLayoutManager;
    private TextView tv_length_getter;
    private DisplayMetrics displayMetrics;
    private int screenHeight, screenWidth;

    private View.OnClickListener onClickListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_drive_details);
        initDispMetrics();
        setUpActionBar();

        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();

        String campusDriveDataStr = getIntent().getStringExtra(AppKeys.CAMPUS_DRIVE_DATA);
        campusDrive = new Gson().fromJson(campusDriveDataStr,
                new TypeToken<PojoCampusDriveListResponseData>() {
                }.getType());

        campusDrive.filterCriteria = new PojoFilterCriteria();
        campusDrive.filterCriteria.taggedInterestList = new ArrayList<>();
        campusDrive.filterCriteria.numSessions = Integer.parseInt(campusDrive.noofsessions);
        if (campusDrive.sessionroles != null && !campusDrive.sessionroles.isEmpty()) {
            if (campusDrive.sessionroles.equalsIgnoreCase("conducted")) {
                campusDrive.filterCriteria.role = getString(R.string.conducted);
            }
            if (campusDrive.sessionroles.equalsIgnoreCase("attended")) {
                campusDrive.filterCriteria.role = getString(R.string.attended);
            }
            if (campusDrive.sessionroles.equalsIgnoreCase("both")) {
                campusDrive.filterCriteria.role = getString(R.string.attended_or_conducted);
            }
        }

        organization_id = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);
        initViews();
        setDataOnViews();
        initClickListener();
        setClickListener();
    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.campus_drive));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(CampusDriveDetailsActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(CampusDriveDetailsActivity.this, R.color.status_bar_color));
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
        rv_tagged_interests_list = (RecyclerView) findViewById(R.id.rv_tagged_interests_list);
        tv_length_getter = (TextView) findViewById(R.id.tv_length_getter);
        rv_vacancy_list = (RecyclerView) findViewById(R.id.rv_vacancy_list);
        tv_venue = (TextView) findViewById(R.id.tv_venue);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_num_sessions = (TextView) findViewById(R.id.tv_num_sessions);
        tv_session_role = (TextView) findViewById(R.id.tv_session_role);
        initVacancyList();
        updateTaggedInterestList();
    }


    private void initVacancyList() {
        vacancyList = new ArrayList<>();
        rv_vacancy_list = (RecyclerView) findViewById(R.id.rv_vacancy_list);
        rv_vacancy_list.setLayoutManager
                (new LinearLayoutManager(CampusDriveDetailsActivity.this,
                        RecyclerView.VERTICAL, false));
        rv_vacancy_list.setNestedScrollingEnabled(false);
        vacancyListAdapter = new VacancyListAdapter(CampusDriveDetailsActivity.this,
                vacancyList, false);
        rv_vacancy_list.setAdapter(vacancyListAdapter);
    }


    private void updateTaggedInterestList() {
        if (taggedInterestList == null) {
            taggedInterestList = new ArrayList<>();
        }
        gridLayoutManager = (new GridLayoutManager(CampusDriveDetailsActivity.this, 200));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (!taggedInterestList.isEmpty()) {
                    tv_length_getter.setText(taggedInterestList.get(position).text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();
                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (screenWidth * 0.85f);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });

        rv_tagged_interests_list.setLayoutManager(gridLayoutManager);
        taggedInterestAdapter = new TaggedInterestAdapter(taggedInterestList.size(),
                CampusDriveDetailsActivity.this, taggedInterestList, 1);
        rv_tagged_interests_list.setAdapter(taggedInterestAdapter);
    }


    private void setDataOnViews() {
        vacancyList.clear();
        vacancyList.addAll(campusDrive.jobs);
        vacancyListAdapter.notifyDataSetChanged();

        tv_venue.setText(campusDrive.collegename);
        tv_date.setText(commonFunctions.parseDateToName(campusDrive.date));
        tv_num_sessions.setText(String.valueOf(campusDrive.filterCriteria.numSessions));
        tv_session_role.setText(campusDrive.filterCriteria.role);

        taggedInterestList.clear();
        taggedInterestList.addAll(campusDrive.filterCriteria.taggedInterestList);
        updateTaggedInterestList();
    }


    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_venue:
                        Intent venueIntent = new Intent(CampusDriveDetailsActivity.this,
                                VenueActivity.class);
                        venueIntent.putExtra("id", campusDrive.college);
                        startActivity(venueIntent);
                        break;
                }
            }
        };
    }


    private void setClickListener() {
        tv_venue.setOnClickListener(onClickListener);
    }


 /*   @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.campus_drive_menu, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            case R.id.edit:
                Intent createCampusDriveIntent = new Intent(CampusDriveDetailsActivity.this,
                        CreateCampusDriveActivity.class);
                Type type = new TypeToken<PojoCampusDriveListResponseData>() {
                }.getType();
                String data = new Gson().toJson(campusDrive, type);
                createCampusDriveIntent.putExtra(AppKeys.CAMPUS_DRIVE_DATA, data);
                createCampusDriveIntent.putExtra(AppKeys.ORGANISATION_ID, organization_id);
                startActivity(createCampusDriveIntent);
                break;

            case R.id.delete:
                onBackPressed();
                break;
        }
        return true;

    }

    @Override
    public void vacancyListAdapterClick(String action, int position) {

    }

    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem
            pojoGetInterestListResponseDataListItem) {

    }
}
