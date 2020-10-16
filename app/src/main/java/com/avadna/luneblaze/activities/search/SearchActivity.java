package com.avadna.luneblaze.activities.search;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.avadna.luneblaze.fragments.search.OrganisationSearchResultFragment;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.normalPost.TagUserSuggestionPopUpAdapter;
import com.avadna.luneblaze.adapters.search.SearchResultPagerAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.fragments.search.ArticleSearchResultFragment;
import com.avadna.luneblaze.fragments.search.InterestSearchListFragment;
import com.avadna.luneblaze.fragments.search.PollSearchResultFragment;
import com.avadna.luneblaze.fragments.search.PostSearchResultFragment;
import com.avadna.luneblaze.fragments.search.QuestionSearchResultFragment;
import com.avadna.luneblaze.fragments.search.SessionSearchResultFragment;
import com.avadna.luneblaze.fragments.search.UserSearchResultListFragment;
import com.avadna.luneblaze.fragments.search.VenueSearchResultListFragment;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppBaseActivity {
    ActionBar actionBar;
    TabLayout tl_result_types_tabs;
    ViewPager vp_results_pager;
    ImageButton ib_search_filter, ib_back_gray;
    View.OnClickListener onClickListener;
    Toolbar tl_top_bar;
    EditText et_search_bar;
    boolean keyboardFlag = true;

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";

    boolean tabShown = false;
    ListPopupWindow popupWindow;
    String currentSearchQuery;
    String tabType;

    SearchResultPagerAdapter searchResultPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        preferenceUtils = new PreferenceUtils(SearchActivity.this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

        tabType = getIntent().getStringExtra("tab_type");
        if (tabType == null || tabType.isEmpty()) {
            tabType = AppKeys.USER;
        }

        currentSearchQuery = getIntent().getStringExtra("query");
        if (currentSearchQuery == null || currentSearchQuery.isEmpty()) {
            currentSearchQuery = "";
        }

        setUpActionBar();
        initViews();
        initClickListener();
        setClickListeners();
        setTextWatchers();
        setUpTabLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        tabType = intent.getStringExtra("tab_type");
        if (tabType == null || tabType.isEmpty()) {
            tabType = AppKeys.USER;
        }

        currentSearchQuery = intent.getStringExtra("query");
        if (currentSearchQuery == null || currentSearchQuery.isEmpty()) {
            currentSearchQuery = "";
        }
        et_search_bar.setText(currentSearchQuery);

        if (tabType.equals(AppKeys.POSTS)) {
            PostSearchResultFragment postSearchResultFragment =
                    ((PostSearchResultFragment) (searchResultPagerAdapter
                            .getRegisteredFragment(2)));
            if (postSearchResultFragment != null) {
                postSearchResultFragment.updateSearchString(currentSearchQuery);
            }
        }

    }

    private void setUpActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat
                    .getColor(SearchActivity.this, R.color.status_bar_color));
        }
      /*  tl_top_bar = findViewById(R.id.tl_top_bar);
        setSupportActionBar(tl_top_bar);
        actionBar = getSupportActionBar();
        if (tl_top_bar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            tl_top_bar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SearchActivity.this, R.color.action_bar_color)));

        }*/
    }

    private void initViews() {
        ib_search_filter = (ImageButton) findViewById(R.id.ib_search_filter);
        ib_back_gray = (ImageButton) findViewById(R.id.ib_back_gray);

        et_search_bar = (EditText) findViewById(R.id.et_search_bar);
        et_search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!et_search_bar.getText().toString().trim().isEmpty()) {
                        Intent searchIntent = new Intent(Config.SEARCH_STRING_UPDATE);
                        searchIntent.putExtra("query", et_search_bar.getText().toString().trim());
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(searchIntent);
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_search_bar.getWindowToken(), 0);
                    }
                    return true;
                }
                return false;
            }
        });

        et_search_bar.setText(currentSearchQuery);
        if (et_search_bar.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_search_bar, InputMethodManager.SHOW_FORCED);
        }
        popupWindow = new ListPopupWindow(SearchActivity.this);
    }

    public String getSearchQuery() {
        return currentSearchQuery;
    }

    public int getSelectedTab() {
        return tl_result_types_tabs.getSelectedTabPosition();
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.ib_back_gray:
                        onBackPressed();
                        break;

                    case R.id.ib_search_filter:
                       /* final Dialog dialog = new MyCustomThemeDialog(SearchActivity.this);
                        dialog.setContentView(R.layout.search_filter_dialog_box);
                        ImageView iv_close = (ImageView) dialog.findViewById(R.id.iv_close);
                        iv_close.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();*/

                        switch (vp_results_pager.getCurrentItem()) {
                            case 0:

                                break;

                            case 1:
                                ((SessionSearchResultFragment) (searchResultPagerAdapter.getRegisteredFragment(1))).openFilterDilaog();
                            /*    Intent filterSessionIntent = new Intent(Config.OPEN_SESSION_FILTER);
                                filterSessionIntent.putExtra(AppKeys.ACTIVITY_NAME, SearchActivity.this.getClass().getSimpleName());
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(filterSessionIntent);*/
                                break;

                            case 2:
                                ((PostSearchResultFragment) (searchResultPagerAdapter.getRegisteredFragment(2))).openFilterDilaog();

                                /*Intent filterPostIntent = new Intent(Config.OPEN_POST_FILTER);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(filterPostIntent);*/
                                break;

                            case 3:
                                ((ArticleSearchResultFragment) (searchResultPagerAdapter.getRegisteredFragment(3))).openFilterDilaog();

                        /*        Intent filterArticleIntent = new Intent(Config.OPEN_ARTICLE_FILTER);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(filterArticleIntent);*/
                                break;

                            case 5:
                                ((QuestionSearchResultFragment) (searchResultPagerAdapter.getRegisteredFragment(5))).openFilterDilaog();

              /*                  Intent filterQuestionIntent = new Intent(Config.OPEN_QUESTION_FILTER);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(filterQuestionIntent);*/
                                break;

                            case 6:
                                break;

                            case 7:
                                ((VenueSearchResultListFragment) (searchResultPagerAdapter.getRegisteredFragment(7))).openFilterDilaog();
                               /* Intent filterVenueIntent = new Intent(Config.OPEN_VENUE_FILTER);
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(filterVenueIntent);*/
                                break;


                            case 8:
                                break;
                        }
                        break;
                }
            }
        };
    }

    public void setTextWatchers() {
        et_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // hitGlobalSearchApi(user_id,s.toString(),"0","all");

                currentSearchQuery = s.toString().trim();
                updateFragmentsWithQuery();

                /*if (!currentSearchQuery.isEmpty()) {
                    Intent searchIntent = new Intent(Config.SEARCH_STRING_UPDATE);
                    searchIntent.putExtra("query", currentSearchQuery);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(searchIntent);
                }*/
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void updateFragmentsWithQuery() {
        switch (vp_results_pager.getCurrentItem()) {
            case 0:
                UserSearchResultListFragment userSearchResultListFragment = (UserSearchResultListFragment)
                        searchResultPagerAdapter.getRegisteredFragment(0);
                if (userSearchResultListFragment != null) {
                    userSearchResultListFragment.updateSearchString(currentSearchQuery);
                }
                break;

            case 1:
                SessionSearchResultFragment sessionSearchResultFragment =
                        (SessionSearchResultFragment) (searchResultPagerAdapter.getRegisteredFragment(1));
                if (sessionSearchResultFragment != null) {
                    sessionSearchResultFragment.updateSearchString(currentSearchQuery);
                }
                break;

            case 2:
                PostSearchResultFragment postSearchResultFragment =
                        ((PostSearchResultFragment) (searchResultPagerAdapter
                                .getRegisteredFragment(2)));
                if (postSearchResultFragment != null) {
                    postSearchResultFragment.updateSearchString(currentSearchQuery);
                }
                break;

            case 3:
                ArticleSearchResultFragment articleSearchResultFragment =
                        ((ArticleSearchResultFragment) (searchResultPagerAdapter
                                .getRegisteredFragment(3)));
                if (articleSearchResultFragment != null) {
                    articleSearchResultFragment.updateSearchString(currentSearchQuery);
                }
                break;

            case 4:
                PollSearchResultFragment pollSearchResultFragment =
                        ((PollSearchResultFragment) (searchResultPagerAdapter
                                .getRegisteredFragment(4)));
                if (pollSearchResultFragment != null) {
                    pollSearchResultFragment.updateSearchString(currentSearchQuery);
                }
                break;

            case 5:
                QuestionSearchResultFragment questionSearchResultFragment =
                        ((QuestionSearchResultFragment) (searchResultPagerAdapter
                                .getRegisteredFragment(5)));
                if (questionSearchResultFragment != null) {
                    questionSearchResultFragment.updateSearchString(currentSearchQuery);
                }
                break;

            case 6:
                InterestSearchListFragment interestSearchListFragment =
                        ((InterestSearchListFragment) (searchResultPagerAdapter
                                .getRegisteredFragment(6)));
                if (interestSearchListFragment != null) {
                    interestSearchListFragment.updateSearchString(currentSearchQuery);
                }
                break;

            case 7:
                VenueSearchResultListFragment venueSearchResultListFragment =
                        ((VenueSearchResultListFragment) (searchResultPagerAdapter
                                .getRegisteredFragment(7)));
                if (venueSearchResultListFragment != null) {
                    venueSearchResultListFragment.updateSearchString(currentSearchQuery);
                }
                break;

            case 8:
                OrganisationSearchResultFragment organisationSearchResultFragment =
                        ((OrganisationSearchResultFragment) (searchResultPagerAdapter
                                .getRegisteredFragment(8)));
                if (organisationSearchResultFragment != null) {
                    organisationSearchResultFragment.updateSearchString(currentSearchQuery);
                }
                break;


        }
    }

    private void hitSearchUserApi(final String user_id, final String query, final int localOffset, String type) {
        Call<PojoSearchResultResponse> call = apiService.globalSearch(user_id, query,
                String.valueOf(localOffset), type);
        call.enqueue(new Callback<PojoSearchResultResponse>() {
            @Override
            public void onResponse(Call<PojoSearchResultResponse> call, Response<PojoSearchResultResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    TagUserSuggestionPopUpAdapter tagUserSuggestionPopUpAdapter =
                            new TagUserSuggestionPopUpAdapter(SearchActivity.this, response.body().data.users, "");
                    popupWindow.setAnchorView(et_search_bar);
                    int popUpWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            200, getResources().getDisplayMetrics());
                    popupWindow.setWidth(popUpWidth);
                    popupWindow.setAdapter(tagUserSuggestionPopUpAdapter);
                    popupWindow.show();
                }
            }

            @Override
            public void onFailure(Call<PojoSearchResultResponse> call, Throwable t) {
                // Log error here since request failed
                //   pb_loading_content.setVisibility(View.GONE);
                commonFunctions.setToastMessage(SearchActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });


    }

    private void setClickListeners() {
        ib_search_filter.setOnClickListener(onClickListener);
        ib_back_gray.setOnClickListener(onClickListener);
    }


    public void updateTabCount(int people, int sessions, int posts, int articles, int polls,
                               int questions, int interests, int venues, int organisations) {
        //todo enable code below when the count is received for all tabs in api
      /*  if (tl_result_types_tabs != null) {
            if (people != 0) {
                tl_result_types_tabs.getTabAt(0).setText(getString(R.string.people) + "(" + people+")");
            }
            if (sessions != 0) {
                tl_result_types_tabs.getTabAt(1).setText(getString(R.string.sessions) + "(" + sessions+")");
            }
            if (posts != 0) {
                tl_result_types_tabs.getTabAt(2).setText(getString(R.string.posts) + "(" + posts+")");
            }
            if (articles != 0) {
                tl_result_types_tabs.getTabAt(3).setText(getString(R.string.articles) + "(" + articles+")");
            }
            if (polls != 0) {
                tl_result_types_tabs.getTabAt(4).setText(getString(R.string.polls) + "(" + polls+")");
            }
            if (questions != 0) {
                tl_result_types_tabs.getTabAt(5).setText(getString(R.string.questions) + "(" + questions+")");
            }
            if (interests != 0) {
                tl_result_types_tabs.getTabAt(6).setText(getString(R.string.interests) + "(" + interests+")");
            }
            if (venues != 0) {
                tl_result_types_tabs.getTabAt(7).setText(getString(R.string.venues) + "(" + venues+")");
            }
            if (organisations != 0) {
                tl_result_types_tabs.getTabAt(8).setText(getString(R.string.organisations) + "(" + organisations+")");
            }
        }*/
    }

    private void setUpTabLayout() {
        tl_result_types_tabs = (TabLayout) findViewById(R.id.tl_result_types_tabs);
        tl_result_types_tabs.addTab(tl_result_types_tabs.newTab().setText(getString(R.string.people)));
        tl_result_types_tabs.addTab(tl_result_types_tabs.newTab().setText(getString(R.string.sessions)));
        tl_result_types_tabs.addTab(tl_result_types_tabs.newTab().setText(getString(R.string.posts)));
        tl_result_types_tabs.addTab(tl_result_types_tabs.newTab().setText(getString(R.string.articles)));
        tl_result_types_tabs.addTab(tl_result_types_tabs.newTab().setText(getString(R.string.polls)));
        tl_result_types_tabs.addTab(tl_result_types_tabs.newTab().setText(getString(R.string.questions)));
        tl_result_types_tabs.addTab(tl_result_types_tabs.newTab().setText(getString(R.string.interests)));
        tl_result_types_tabs.addTab(tl_result_types_tabs.newTab().setText(getString(R.string.venues)));
        tl_result_types_tabs.addTab(tl_result_types_tabs.newTab().setText(getString(R.string.organisations)));

        tl_result_types_tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        tl_result_types_tabs.setTabMode(TabLayout.MODE_SCROLLABLE);

        tl_result_types_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                vp_results_pager.setCurrentItem(position);
                currentSearchQuery = et_search_bar.getText().toString().trim();
                updateFragmentsWithQuery();
                hideFilterBasedOnPostition(position);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        int selectedTabPos = 0;
        switch (tabType) {

            case AppKeys.USER:
                selectedTabPos = 0;
                break;

            case AppKeys.SESSION:
                selectedTabPos = 1;
                break;

            case AppKeys.POSTS:
                selectedTabPos = 2;
                break;

            case AppKeys.ARTICLE:
                selectedTabPos = 3;
                break;

            case AppKeys.POLL:
                selectedTabPos = 4;
                break;

            case AppKeys.QUESTION:
                selectedTabPos = 5;
                break;

            case AppKeys.INTEREST:
                selectedTabPos = 6;
                break;

            case AppKeys.VENUE:
                selectedTabPos = 7;
                break;

            case AppKeys.ORGANISATION:
                selectedTabPos = 8;
                break;

        }

        vp_results_pager = (ViewPager) findViewById(R.id.vp_results_pager);

        searchResultPagerAdapter = new SearchResultPagerAdapter(SearchActivity.this,
                getSupportFragmentManager(), currentSearchQuery, selectedTabPos);

        vp_results_pager.setAdapter(searchResultPagerAdapter);
        vp_results_pager.setOffscreenPageLimit(8);
        vp_results_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tl_result_types_tabs.setScrollPosition(position, positionOffset, false);
            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tl_result_types_tabs.getTabAt(position);
                tab.select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        vp_results_pager.setCurrentItem(selectedTabPos);
        hideFilterBasedOnPostition(selectedTabPos);


        if (getIntent().getBooleanExtra(AppKeys.SHOW_KEYBOARD, false)) {
            et_search_bar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_search_bar, InputMethodManager.SHOW_FORCED);
                }
            }, 300);
        }
    }

    public void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (et_search_bar.requestFocus()) {
            imm.hideSoftInputFromWindow(et_search_bar.getWindowToken(), 0);
        }
    }


    public void hideFilterBasedOnPostition(int position) {
        if (position == 0 || position == 2 || position == 4 || position == 6 || position == 7
                || position == 8) {
            ib_search_filter.setVisibility(View.GONE);
        } else {
            ib_search_filter.setVisibility(View.VISIBLE);
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
