package com.avadna.luneblaze.fragments.search;

import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.search.SearchActivity;
import com.avadna.luneblaze.adapters.InterestSearchResultAdapter;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.adapters.search.ArticleSearchResultListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponse;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponseData;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchArticle;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponse;
import com.avadna.luneblaze.pojo.pojoSearch.PojoSearchResultResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleSearchResultFragment extends Fragment implements
        InterestSearchResultAdapter.InterestResultAdapterCallback,
        TaggedInterestAdapter.TaggedInterestAdapterCallback {

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id = "";

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;

    RecyclerView rv_list;
    ArticleSearchResultListAdapter articleSearchResultListAdapter;
    List<PojoSearchArticle> articleList;
    LinearLayoutManager linearLayoutManager;

    //ImageView iv_filter;
    Dialog filterDialog;
    LinearLayout ll_interest_bar;

    TextView tv_done, tv_cancel, tv_my_roles, tv_interest_option;


    static final String MY_ROLES_ANY = "any";
    static final String MY_ROLES_MY_ARTICLE = "My Articles";


    String newMyRole = MY_ROLES_ANY;
    String oldMyRole = MY_ROLES_ANY;
    int offset = 0;


    static final String INTEREST_OPTION_ANY = "Any";
    static final String INTEREST_OPTION_MY_INTEREST = "my_interest";
    static final String INTEREST_OPTION_SEARCH = "search_interest";
    String newInterestOption = INTEREST_OPTION_ANY;
    String oldInterestOption = INTEREST_OPTION_ANY;


    String newIsMyInterestSelected = "0";
    String oldIsMyInterestSelected = "0";

    Dialog interestSearchDialog;
    RecyclerView rv_interest_search_result;
    InterestSearchResultAdapter interestSearchResultAdapter;
    List<PojoSearchInterestWithTextResponseData> interestSearchResultList = new ArrayList<>();
    EditText et_interest_name;


    int[] location = new int[2];
    RecyclerView rv_added_interests_in_filter, rv_added_interests_in_search;
    TaggedInterestAdapter taggedInterestAdapter_in_filter, taggedInterestAdapter_in_search;
    TextView tv_length_getter;
    DisplayMetrics displayMetrics;
    List<PojoGetInterestListResponseDataListItem> selectedInterestList = new ArrayList<>();
    List<String> selectedInterestIdList = new ArrayList<>();

    int screenHeight, screenWidth;


    private BroadcastReceiver mRegistrationBroadcastReceiver;

    String query = "";
    int position;
    boolean isSearchApiCalled = false;
    private boolean loadedForEmptyString = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     /*   if (getArguments() != null) {
            Type type = new TypeToken< List<PojoSearchArticle>>() {}.getType();
            String str=getArguments().getString("article");
            if(str!=null&&str.isEmpty()){
                articleList=new ArrayList<>();
            }
            else {
                articleList= new Gson().fromJson(str, type);
            }
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_article_search_result, container, false);
        preferenceUtils = new PreferenceUtils(this.getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

        initViews(baseLayout);
        initDispMetrics();
        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SEARCH_STRING_UPDATE));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.OPEN_ARTICLE_FILTER));
        query = getArguments().getString("query", "");
        position = getArguments().getInt("position");

        if (position == 3) {
            offset = 0;
            hitSearchArticleApi(user_id, query, offset, "articles", oldMyRole,
                    selectedInterestIdList, oldIsMyInterestSelected);
        }

        return baseLayout;

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initViews(View baseLayout) {
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                offset = 0;
                hitSearchArticleApi(user_id, query, offset, "articles", oldMyRole,
                        selectedInterestIdList, oldIsMyInterestSelected);
            }
        });

        initList(baseLayout);
    }


    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.SEARCH_STRING_UPDATE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                  /*  int pos = ((SearchActivity) getActivity()).getSelectedTab();
                    if (pos == 3) {
                        query = intent.getStringExtra("query");
                        score = 0;
                        hitSearchArticleApi(user_id, query, score, "articles", oldMyRole,
                                selectedInterestIdList, oldIsMyInterestSelected);
                    }*/
                }
                if (intent.getAction().equals(Config.OPEN_ARTICLE_FILTER)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    openFilterDilaog();
                }
            }
        };

    }

    public void updateSearchString(String str) {
        if (str.isEmpty() && !loadedForEmptyString) {
            loadedForEmptyString = true;
            query = str;
            offset = 0;
            hitSearchArticleApi(user_id, query, offset, "articles", oldMyRole,
                    selectedInterestIdList, oldIsMyInterestSelected);
        } else if (!query.equals(str)|| articleList.isEmpty()) {
            query = str;
            offset = 0;
            hitSearchArticleApi(user_id, query, offset, "articles", oldMyRole,
                    selectedInterestIdList, oldIsMyInterestSelected);
        }
    }


    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        screenWidth = (int) (0.9f * screenWidth);

    }

    private void updateSelectedInterestListSearch() {
        GridLayoutManager taggedInterestGridLayoutManager;
        taggedInterestGridLayoutManager = (new GridLayoutManager(getActivity(), 200));
        taggedInterestGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Display display;

                display = interestSearchDialog.getWindow().getWindowManager().getDefaultDisplay();

                Point size = new Point();
                display.getSize(size);
                String text;
                if (!selectedInterestList.isEmpty()) {
                    text = selectedInterestList.get(position).text;
                    tv_length_getter.setText(text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();

                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (0.8f * screenWidth);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });
        rv_added_interests_in_search.setLayoutManager(taggedInterestGridLayoutManager);
        taggedInterestAdapter_in_search = new TaggedInterestAdapter(selectedInterestList.size(), this, selectedInterestList, 1);
        rv_added_interests_in_search.setAdapter(taggedInterestAdapter_in_search);
    }


    private void updateSelectedInterestListFilter() {
        GridLayoutManager taggedInterestGridLayoutManager;
        taggedInterestGridLayoutManager = (new GridLayoutManager(getActivity(), 200));
        taggedInterestGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                Display display;
                display = filterDialog.getWindow().getWindowManager().getDefaultDisplay();

                Point size = new Point();
                display.getSize(size);
                String text;
                if (!selectedInterestList.isEmpty()) {
                    text = selectedInterestList.get(position).text;
                    tv_length_getter.setText(text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();

                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (0.8f * screenWidth);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });

        rv_added_interests_in_filter.setLayoutManager(taggedInterestGridLayoutManager);
        taggedInterestAdapter_in_filter = new TaggedInterestAdapter(selectedInterestList.size(), this, selectedInterestList, 1);
        rv_added_interests_in_filter.setAdapter(taggedInterestAdapter_in_filter);
    }

    private void hitInterestSearchWithTextApi(String text) {

        Call<PojoSearchInterestWithTextResponse> call = apiService.searchInterestWithTextApi(text,
                String.valueOf(offset));
        call.enqueue(new Callback<PojoSearchInterestWithTextResponse>() {
            @Override
            public void onResponse(Call<PojoSearchInterestWithTextResponse> call,
                                   Response<PojoSearchInterestWithTextResponse> response) {

                String message = response.body().message;
                if (response.body().data != null) {
                    interestSearchResultList.clear();
                    for (int i = 0; i < response.body().data.size() && i < 4; i++) {
                        interestSearchResultList.add(response.body().data.get(i));
                    }
                    // interestSearchResultList.addAll(response.body().data);
                    interestSearchResultAdapter.notifyDataSetChanged();
                } else {
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoSearchInterestWithTextResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void initList(View baseLayout) {
        articleList = new ArrayList<>();
        rv_list = (RecyclerView) baseLayout.findViewById(R.id.rv_list);
        articleSearchResultListAdapter = new ArticleSearchResultListAdapter(getActivity(), articleList);
        linearLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        rv_list.setAdapter(articleSearchResultListAdapter);
        articleSearchResultListAdapter.setShowProgressBar(false);

        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 3) {
                    if (linearLayoutManager.findLastVisibleItemPosition() > articleList.size() - 2) {
                        hitSearchArticleApi(user_id, query, ++offset, "articles", oldMyRole,
                                selectedInterestIdList, oldIsMyInterestSelected);
                    }
                    ((SearchActivity) getActivity()).closeKeyboard();
                }
            }
        });
    }

    public void openFilterDilaog() {
        filterDialog = new MyCustomThemeDialog(getActivity());
        filterDialog.setContentView(R.layout.interest_question_list_filter_dialog);
        filterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        filterDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        filterDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout ll_relevance_wrapper = (LinearLayout) filterDialog.findViewById(R.id.ll_relevance_wrapper);
        ll_relevance_wrapper.setVisibility(View.GONE);


        tv_done = (TextView) filterDialog.findViewById(R.id.tv_done);
        tv_cancel = (TextView) filterDialog.findViewById(R.id.tv_cancel);
        tv_my_roles = (TextView) filterDialog.findViewById(R.id.tv_my_roles);
        tv_interest_option = (TextView) filterDialog.findViewById(R.id.tv_interest_option);

        tv_length_getter = (TextView) filterDialog.findViewById(R.id.tv_length_getter);

        rv_added_interests_in_filter = (RecyclerView) filterDialog.findViewById(R.id.rv_added_interests);
        updateSelectedInterestListFilter();


        if (oldIsMyInterestSelected.equals("1")) {
            tv_interest_option.setText(R.string.my_interests);
        } else {
            switch (oldInterestOption) {
                case INTEREST_OPTION_ANY:
                    tv_interest_option.setText(R.string.any);
                    break;

                case INTEREST_OPTION_SEARCH:
                    tv_interest_option.setText(R.string.choose_interest);
                    break;

            }
        }

        switch (oldMyRole) {
            case MY_ROLES_ANY:
                tv_my_roles.setText(R.string.any);
                break;

            case MY_ROLES_MY_ARTICLE:
                tv_my_roles.setText(R.string.my_articles);
                break;
        }


        final PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {

                    case R.id.interest_any:
                        newInterestOption = INTEREST_OPTION_ANY;
                        newIsMyInterestSelected = "0";
                        tv_interest_option.setText(getString(R.string.any));
                        rv_added_interests_in_filter.setVisibility(View.GONE);
                        break;

                    case R.id.my_interest:
                        newInterestOption = INTEREST_OPTION_MY_INTEREST;
                        newIsMyInterestSelected = "1";
                        tv_interest_option.setText(getString(R.string.my_interests));
                        rv_added_interests_in_filter.setVisibility(View.GONE);
                        break;

                    case R.id.choose_interest:
                        newInterestOption = INTEREST_OPTION_SEARCH;
                        newIsMyInterestSelected = "0";
                        tv_interest_option.setText(getString(R.string.choose_interest));
                        openInterestSearchDialog();
                        rv_added_interests_in_filter.setVisibility(View.VISIBLE);
                        break;


                    case R.id.role_any:
                        newMyRole = MY_ROLES_ANY;
                        tv_my_roles.setText(R.string.any);
                        break;

                    case R.id.role_my_article:
                        newMyRole = MY_ROLES_MY_ARTICLE;
                        tv_my_roles.setText(R.string.my_articles);
                        break;

                }
                return false;
            }
        };


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.tv_done:
                        oldMyRole = newMyRole;
                        oldIsMyInterestSelected = newIsMyInterestSelected;
                        oldInterestOption = newInterestOption;

                        hitSearchArticleApi(user_id, query, offset, "articles", oldMyRole,
                                selectedInterestIdList, oldIsMyInterestSelected);

                        filterDialog.dismiss();
                        break;

                    case R.id.tv_cancel:
                        newMyRole = oldMyRole;
                        newInterestOption = oldInterestOption;
                        newIsMyInterestSelected = oldIsMyInterestSelected;
                        filterDialog.dismiss();
                        break;

                    case R.id.tv_interest_option:
                        PopupMenu interestOptionMenu = new PopupMenu(getActivity(), tv_interest_option);
                        MenuInflater interestOptionMenuInflater = interestOptionMenu.getMenuInflater();
                        interestOptionMenuInflater.inflate(R.menu.interest_session_filter_interest_option_menu,
                                interestOptionMenu.getMenu());
                        interestOptionMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        interestOptionMenu.show();
                        break;

                    case R.id.tv_my_roles:
                        PopupMenu roleMenu = new PopupMenu(getActivity(), tv_my_roles);
                        MenuInflater rolesInflater = roleMenu.getMenuInflater();
                        rolesInflater.inflate(R.menu.interest_article_filter_role_menu, roleMenu.getMenu());
                        roleMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        roleMenu.show();
                        break;
                }
            }
        };

        tv_done.setOnClickListener(onClickListener);
        tv_cancel.setOnClickListener(onClickListener);
        tv_my_roles.setOnClickListener(onClickListener);
        tv_interest_option.setOnClickListener(onClickListener);
        filterDialog.show();
    }


    public void openInterestSearchDialog() {

        interestSearchDialog = new MyCustomThemeDialog(getActivity());
        interestSearchDialog.setContentView(R.layout.interest_search_dialog);
        interestSearchDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        interestSearchDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        rv_interest_search_result = (RecyclerView) interestSearchDialog.findViewById(R.id.rv_interest_search_result);
        rv_interest_search_result.setLayoutManager
                (new LinearLayoutManager(getActivity(),
                        RecyclerView.VERTICAL, false));
        rv_interest_search_result.setNestedScrollingEnabled(false);
        interestSearchResultAdapter = new InterestSearchResultAdapter(this, interestSearchResultList);
        rv_interest_search_result.setAdapter(interestSearchResultAdapter);

        tv_length_getter = interestSearchDialog.findViewById(R.id.tv_length_getter);
        rv_added_interests_in_search = (RecyclerView) interestSearchDialog.findViewById(R.id.rv_added_interests);
        updateSelectedInterestListSearch();


        et_interest_name = (EditText) interestSearchDialog.findViewById(R.id.et_interest_name);
        et_interest_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (et_interest_name.getText().toString().trim().isEmpty()) {
                    interestSearchResultList.clear();
                    interestSearchResultAdapter.notifyDataSetChanged();
                } else {
                    hitInterestSearchWithTextApi(et_interest_name.getText().toString().trim());
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        TextView tv_done_button = (TextView) interestSearchDialog.findViewById(R.id.tv_done_button);
        tv_done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interestSearchDialog.dismiss();
            }
        });

        interestSearchDialog.show();

    }

    private void hitSearchArticleApi(final String user_id, final String query, final int localOffset, String type,
                                     String my_role, List<String> interests, String myInterest) {
        ll_no_connection_wrapper.setVisibility(View.GONE);

        HashMap<String, String> interestMap = new HashMap<>();
        for (int i = 0; i < interests.size(); i++) {
            interestMap.put("interest_search[" + i + "]", interests.get(i));
        }
        Call<PojoSearchResultResponse> call = apiService.searchArticle(user_id, query, String.valueOf(offset),
                type, my_role, interestMap, myInterest);
        if (localOffset == 0) {
            articleSearchResultListAdapter.setShowNoMoreResults(false);
            articleList.clear();
            articleSearchResultListAdapter.notifyDataSetChanged();
        }
        if (!isSearchApiCalled) {
            isSearchApiCalled = true;
            articleSearchResultListAdapter.setShowProgressBar(true);

            call.enqueue(new Callback<PojoSearchResultResponse>() {
                @Override
                public void onResponse(Call<PojoSearchResultResponse> call, Response<PojoSearchResultResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null) {
                        PojoSearchResultResponseData data=response.body().data;
                        SearchActivity searchActivity = (SearchActivity) getActivity();
                        if (searchActivity != null) {
                            searchActivity.updateTabCount(data.usersCount, data.sessionsCount,
                                    data.postsCount, data.articlesCount, data.pollCount, data.qaCount, data.interestCount,
                                    data.venuesCount, 0);
                        }
                        message = response.body().message;
                        if (localOffset == 0) {
                            articleList.clear();
                        }
                        if (response.body().data.articles != null) {
                            if (response.body().data.articles.isEmpty()) {
                                articleSearchResultListAdapter.setShowNoMoreResults(true);
                            }
                            articleList.addAll(response.body().data.articles);
                        }
                        articleSearchResultListAdapter.notifyDataSetChanged();


                    } else {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), "Url Error" + message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    articleSearchResultListAdapter.setShowProgressBar(false);
                    isSearchApiCalled = false;

                    SearchActivity searchActivity = ((SearchActivity) getActivity());
                    if (searchActivity != null) {
                        String latestQuery = searchActivity.getSearchQuery();
                        if (!latestQuery.equals(query)) {
                            offset = 0;
                            hitSearchArticleApi(user_id, ArticleSearchResultFragment.this.query, offset,
                                    "articles", oldMyRole, selectedInterestIdList, oldIsMyInterestSelected);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PojoSearchResultResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isSearchApiCalled = false;
                    articleSearchResultListAdapter.setShowProgressBar(false);
                    if (articleList.isEmpty()) {
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }


    }

    @Override
    public void interestResultItemClickCallback(int position, PojoSearchInterestWithTextResponseData data) {
        PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem
                = new PojoGetInterestListResponseDataListItem(data.interestId, data.parentId, data.text, 0);
        for (int i = 0; i < selectedInterestList.size(); i++) {
            //if item already present in list then return
            if (selectedInterestList.get(i).interestId
                    .equals(data.interestId)) {
                return;
            }
        }

        //add 10 interests at max
        if (selectedInterestList.size() < 10) {
            selectedInterestList.add(pojoGetInterestListResponseDataListItem);
            selectedInterestIdList.clear();
            for (int i = 0; i < selectedInterestList.size(); i++) {
                selectedInterestIdList.add(selectedInterestList.get(i).interestId);
            }
            updateSelectedInterestListFilter();
            updateSelectedInterestListSearch();
        }
    }

    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem) {
        selectedInterestList.remove(position);
       /* selectedInterestIdList.clear();
        for(int i=0;i<selectedInterestList.size();i++){
            selectedInterestIdList.add(selectedInterestList.get(i).interestId);
        }*/
        updateSelectedInterestListFilter();
        updateSelectedInterestListSearch();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
