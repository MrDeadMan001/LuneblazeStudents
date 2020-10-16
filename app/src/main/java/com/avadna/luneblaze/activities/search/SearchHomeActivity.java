package com.avadna.luneblaze.activities.search;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.google.android.material.appbar.AppBarLayout;

import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.search.TagUserListAdapter;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchHomeActivity extends AppBaseActivity {
    ActionBar actionBar;


    EditText et_search_bar;
    String latestQuery = "";
    ImageButton ib_back_gray;
    ImageButton ib_search_gray;
    ImageButton ib_clear_search;
    RelativeLayout rl_search_wrapper;
    ProgressBar pb_loading;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";
    ListPopupWindow popupWindow;

    View.OnClickListener onClickListener;

    LinearLayout ll_not_found_wrapper;
    TextView tv_see_results;

    ImageView iv_not_found;

    ScrollView sv_icons_wrapper;

    RelativeLayout rl_session_wrapper, rl_user_wrapper, rl_interests_wrapper;
    RelativeLayout rl_article_wrapper, rl_venue_wrapper, rl_question_wrapper;
    RelativeLayout rl_poll_wrapper;
    AppBarLayout app_bar_layout;
    DisplayMetrics displayMetrics;

    RecyclerView rv_list;
    List<PojoUserData> userList;
    TagUserListAdapter tagUserListAdapter;
    LinearLayoutManager linearLayoutManager;

    boolean isApiCalled = false;


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

        setContentView(R.layout.activity_search_home);
        preferenceUtils = new PreferenceUtils(SearchHomeActivity.this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        initViews();
        setTextWatchers();
        initClickListener();
        setClickListeners();

        //et_search_bar.setText(preferenceUtils.getSmsHash());
    }

    private void initViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat
                    .getColor(SearchHomeActivity.this, R.color.status_bar_color));
        }

        ll_not_found_wrapper = (LinearLayout) findViewById(R.id.ll_not_found_wrapper);
        tv_see_results = (TextView) findViewById(R.id.tv_see_results);
        iv_not_found = (ImageView) findViewById(R.id.iv_not_found);
        ll_not_found_wrapper.setVisibility(View.GONE);
        sv_icons_wrapper = (ScrollView) findViewById(R.id.sv_icons_wrapper);
        app_bar_layout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        rl_search_wrapper = (RelativeLayout) findViewById(R.id.rl_search_wrapper);
        et_search_bar = (EditText) findViewById(R.id.et_search_bar);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        pb_loading.setVisibility(View.INVISIBLE);

        et_search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!et_search_bar.getText().toString().trim().isEmpty()) {
                        Intent searchIntent = new Intent(SearchHomeActivity.this, SearchActivity.class);
                        searchIntent.putExtra("query", et_search_bar.getText().toString().trim());
                        searchIntent.putExtra("tab_type", AppKeys.USER);
                        startActivity(searchIntent);
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_search_bar.getWindowToken(), 0);
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });

        ib_back_gray = (ImageButton) findViewById(R.id.ib_back_gray);
        ib_search_gray = (ImageButton) findViewById(R.id.ib_search_gray);
        ib_clear_search = (ImageButton) findViewById(R.id.ib_clear_search);
        rl_session_wrapper = (RelativeLayout) findViewById(R.id.rl_session_wrapper);
        rl_user_wrapper = (RelativeLayout) findViewById(R.id.rl_user_wrapper);
        rl_interests_wrapper = (RelativeLayout) findViewById(R.id.rl_interests_wrapper);
        rl_article_wrapper = (RelativeLayout) findViewById(R.id.rl_article_wrapper);
        rl_venue_wrapper = (RelativeLayout) findViewById(R.id.rl_venue_wrapper);
        rl_question_wrapper = (RelativeLayout) findViewById(R.id.rl_question_wrapper);
        rl_poll_wrapper = (RelativeLayout) findViewById(R.id.rl_poll_wrapper);

        popupWindow = new ListPopupWindow(SearchHomeActivity.this);

        initUserList();


    }

    private void initUserList() {
        userList = new ArrayList<>();
        rv_list = (RecyclerView) findViewById(R.id.rv_list);
        tagUserListAdapter = new TagUserListAdapter(this, userList, "search");
        linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        rv_list.setAdapter(tagUserListAdapter);
        rv_list.setVisibility(View.VISIBLE);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(SearchHomeActivity.this, SearchActivity.class);
                searchIntent.putExtra("query", et_search_bar.getText().toString().trim());

                switch (view.getId()) {
                    case R.id.ib_back_gray:
                        finish();
                        break;

                    case R.id.ib_search_gray:
                        searchIntent.putExtra("tab_type", AppKeys.USER);
                        startActivity(searchIntent);
                        finish();

                        break;

                    case R.id.ib_clear_search:
                        et_search_bar.getText().clear();
                        userList.clear();
                        sv_icons_wrapper.setVisibility(View.VISIBLE);
                        ll_not_found_wrapper.setVisibility(View.GONE);
                        tagUserListAdapter.notifyDataSetChanged();
                        break;


                    case R.id.rl_session_wrapper:
                        searchIntent.putExtra("tab_type", AppKeys.SESSION);
                        startActivity(searchIntent);
                        finish();

                        break;

                    case R.id.tv_see_results:
                        searchIntent.putExtra("query", getCurrentSearchQuery());
                        searchIntent.putExtra("tab_type", AppKeys.USER);
                        startActivity(searchIntent);
                        finish();
                        break;

                    case R.id.rl_user_wrapper:
                        searchIntent.putExtra("tab_type", AppKeys.USER);
                        startActivity(searchIntent);
                        finish();

                        break;

                    case R.id.rl_interests_wrapper:
                        searchIntent.putExtra("tab_type", AppKeys.INTEREST);
                        startActivity(searchIntent);
                        finish();

                        break;

                    case R.id.rl_article_wrapper:
                        searchIntent.putExtra("tab_type", AppKeys.ARTICLE);
                        startActivity(searchIntent);
                        finish();

                        break;

                    case R.id.rl_venue_wrapper:
                        searchIntent.putExtra("tab_type", AppKeys.VENUE);
                        startActivity(searchIntent);
                        finish();

                        break;

                    case R.id.rl_question_wrapper:
                        searchIntent.putExtra("tab_type", AppKeys.QUESTION);
                        startActivity(searchIntent);
                        finish();

                        break;

                    case R.id.rl_poll_wrapper:
                        searchIntent.putExtra("tab_type", AppKeys.POLL);
                        startActivity(searchIntent);
                        finish();

                        break;

                }

            }
        };

    }

    private void setClickListeners() {
        ib_back_gray.setOnClickListener(onClickListener);
        ib_search_gray.setOnClickListener(onClickListener);
        ib_clear_search.setOnClickListener(onClickListener);
        rl_session_wrapper.setOnClickListener(onClickListener);
        rl_user_wrapper.setOnClickListener(onClickListener);
        rl_interests_wrapper.setOnClickListener(onClickListener);
        rl_article_wrapper.setOnClickListener(onClickListener);
        rl_venue_wrapper.setOnClickListener(onClickListener);
        rl_question_wrapper.setOnClickListener(onClickListener);
        rl_poll_wrapper.setOnClickListener(onClickListener);

        tv_see_results.setOnClickListener(onClickListener);
    }

    public void setTextWatchers() {
        et_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // hitGlobalSearchApi(user_id,s.toString(),"0","all");
                ll_not_found_wrapper.setVisibility(View.GONE);

                latestQuery = s.toString();
                if (latestQuery.isEmpty()) {
                    userList.clear();
                    tagUserListAdapter.notifyDataSetChanged();
                    sv_icons_wrapper.setVisibility(View.VISIBLE);

                } else {
                    hitSearchUserApi(user_id, latestQuery, 0, "users");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public String getCurrentSearchQuery() {
        return et_search_bar.getText().toString().trim();
    }

    private void hitSearchUserApi(final String user_id, final String query, final int localOffset, String type) {
        Call<PojoSearchResultResponse> call = apiService.globalSearch(user_id, query,
                String.valueOf(localOffset), type);
        tagUserListAdapter.setShowNoResultsFound(false);

        if (!isApiCalled) {
            isApiCalled = true;
            pb_loading.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<PojoSearchResultResponse>() {
                @Override
                public void onResponse(Call<PojoSearchResultResponse> call, Response<PojoSearchResultResponse> response) {
                    String message = "";
                    pb_loading.setVisibility(View.INVISIBLE);
                    if (response != null && response.body() != null) {
                        try {
                            userList.clear();
                            if (!query.equals(latestQuery)) {
                                isApiCalled = false;
                                hitSearchUserApi(user_id, latestQuery, 0, "users");
                            } else {
                                userList.addAll(response.body().data.users);
                                if (userList.isEmpty()) {
                                    //  tagUserListAdapter.setShowNoResultsFound(true);
                                    sv_icons_wrapper.setVisibility(View.GONE);
                                    ll_not_found_wrapper.setVisibility(View.VISIBLE);
                                }
                                tagUserListAdapter.notifyDataSetChanged();
                                if (!userList.isEmpty()) {
                                    sv_icons_wrapper.setVisibility(View.GONE);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    isApiCalled = false;

                }

                @Override
                public void onFailure(Call<PojoSearchResultResponse> call, Throwable t) {
                    // Log error here since request failed
                    //   pb_loading_content.setVisibility(View.GONE);
                    commonFunctions.setToastMessage(SearchHomeActivity.this, t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isApiCalled = false;
                    pb_loading.setVisibility(View.INVISIBLE);


                }
            });
        }

    }
}
