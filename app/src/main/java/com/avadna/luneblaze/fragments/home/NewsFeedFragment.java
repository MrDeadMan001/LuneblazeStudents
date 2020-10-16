package com.avadna.luneblaze.fragments.home;


import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.activities.PostLoaderActivity;
import com.avadna.luneblaze.activities.SessionAcceptFormActivity;
import com.avadna.luneblaze.activities.article.ArticleActivity;
import com.avadna.luneblaze.activities.article.ArticleCommentListActivity;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.activities.organisation.AdTargetAudienceActivity;
import com.avadna.luneblaze.activities.organisation.OrganisationProfileInfoActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.activities.question.AnswerReplyListActivity;
import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.activities.sessions.SessionLoaderActivity;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.polls.CreatePollActivity;
import com.avadna.luneblaze.activities.InviteActivity;
import com.avadna.luneblaze.activities.MainActivity;
import com.avadna.luneblaze.activities.PostCreationActivity;
import com.avadna.luneblaze.adapters.newsfeed.NewsFeedAdapter;
import com.avadna.luneblaze.adapters.PollPostAdapter;
import com.avadna.luneblaze.adapters.ShareNewsFeedAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoReportContentResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoNewsFeedPollOption;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionDiscussionResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class NewsFeedFragment extends Fragment implements
        NewsFeedAdapter.NewsFeedAdapterCallback,
        ShareNewsFeedAdapter.ShareNewsFeedAdapterCallback,
        PollPostAdapter.PollPostAdapterCallback {
    private RelativeLayout rl_parent_wrapper;

    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;

    private SwipeRefreshLayout srl_refresh;
    private RecyclerView rv_news_feed_list;
    private NewsFeedAdapter newsFeedAdapter;
    private List<PojoGetNewsFeedResponseData> newsFeedList;
    private LinearLayoutManager newFeedLayoutManager;
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;

    private TextView tv_filter_placeholder;

    private TextView tv_new_posts_indicator;
    private int newPostCounter = 0;

    private TextView tv_create_post;
    //  FloatingActionButton fb_create;

    private View.OnClickListener onClickListener;
    private PopupMenu.OnMenuItemClickListener onMenuItemClickListener;
    // Animation animFadeIn, animFadeOut, animScaleUp, animScaleDown;
    private boolean isAnimRunning = false;

    private boolean isNewsFeedApiCalled = false;
    private String currentFilter = "all";

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private boolean isSuggestedListAdded = false;


    private Calendar calender;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.NEW_FEED_ITEM));
      /*  LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.OPEN_NEWS_FEED_FILTER));*/
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.ADD_NEWS_FEED_ITEM));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.EDIT_NEWS_FEED_ITEM));

        calender = Calendar.getInstance();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_news_feed, container, false);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        initView(baseLayout);
        //  initAnimations();
        initClickListeners();
        setClickListeners();
        initNewsFeedList(baseLayout);

        return baseLayout;
    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.NEW_FEED_ITEM)) {
                    newPostCounter++;
                    if (currentFilter.equals("all") && newPostCounter >= 4) {
                        tv_new_posts_indicator.setVisibility(View.VISIBLE);
                    }
                }

                if (intent.getAction().equals(Config.ADD_NEWS_FEED_ITEM)) {
                    String dataStr = intent.getStringExtra("data");
                    PojoGetNewsFeedResponseData newItem = new Gson().fromJson(
                            dataStr, new TypeToken<PojoGetNewsFeedResponseData>() {
                            }.getType());
                    newsFeedList.add(0, newItem);
                    newsFeedAdapter.notifyDataSetChanged();
                }

                if (intent.getAction().equals(Config.EDIT_NEWS_FEED_ITEM)) {
                    String dataStr = intent.getStringExtra("data");
                    PojoGetNewsFeedResponseData newItem = new Gson().fromJson(
                            dataStr, new TypeToken<PojoGetNewsFeedResponseData>() {
                            }.getType());

                    for (int i = 0; i < newsFeedList.size(); i++) {

                        if (newsFeedList.get(i).postId != null
                                && newsFeedList.get(i).postId.equals(newItem.postId)) {
                            newsFeedList.remove(i);
                            newsFeedList.add(i, newItem);
                            break;
                        }
                    }
                    newsFeedAdapter.notifyDataSetChanged();
                }

                /*if (intent.getAction().equals(Config.OPEN_NEWS_FEED_FILTER)) {
                   openFilterDialog();
                }*/
            }
        };
    }

    public void openFilterDialog() {
        PopupMenu filterMenu = new PopupMenu(getActivity(), tv_filter_placeholder);
        MenuInflater interestOptionMenuInflater = filterMenu.getMenuInflater();
        interestOptionMenuInflater.inflate(R.menu.newsfeed_filter_menu, filterMenu.getMenu());
        filterMenu.setOnMenuItemClickListener(onMenuItemClickListener);
        try {
            Field[] fields = filterMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(filterMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        filterMenu.show();
    }

    private void initView(View baseLayout) {
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitGetNewsFeedApi(user_id, "newsfeed", currentFilter, "0");
                newPostCounter = 0;
            }
        });

        rl_parent_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_venue_wrapper);
        tv_filter_placeholder = (TextView) baseLayout.findViewById(R.id.tv_filter_placeholder);
        tv_new_posts_indicator = (TextView) baseLayout.findViewById(R.id.tv_new_posts_indicator);
        tv_new_posts_indicator.setVisibility(View.GONE);
        srl_refresh = (SwipeRefreshLayout) baseLayout.findViewById(R.id.srl_refresh);
        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                preferenceUtils.saveNewsFeedLastDate(null);
                hitGetNewsFeedApi(user_id, "newsfeed", currentFilter, getLastPostScore());
                newPostCounter = 0;
            }
        });

        //  fb_create = (FloatingActionButton) baseLayout.findViewById(R.id.fb_create);
        tv_create_post = (TextView) baseLayout.findViewById(R.id.tv_create_post);
    }

    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                 /*   case R.id.iv_message_icon:

                        break;
                    case R.id.iv_notification_icon:

                        break;
                    case R.id.iv_settings_icon:

                        break;*/
                    case R.id.bt_create_article:

                        break;

                    case R.id.tv_new_posts_indicator:
                        srl_refresh.setRefreshing(true);
                        rv_news_feed_list.smoothScrollToPosition(0);
                        preferenceUtils.saveNewsFeedLastDate(null);
                        hitGetNewsFeedApi(user_id, "newsfeed", currentFilter, getLastPostScore());
                        tv_new_posts_indicator.setVisibility(View.GONE);
                        newPostCounter = 0;
                        break;

                    case R.id.tv_filter:

                        break;

                   /* //post creation dialog buttons
                    case R.id.ll_post_wrapper:
                        Intent postIntent = new Intent(getActivity(), PostCreationActivity.class);
                        startActivity(postIntent);
                        if (postCreationDialog != null) {
                            postCreationDialog.dismiss();
                        }
                        break;

                    case R.id.ll_session_wrapper:
                        Intent sessionIntent = new Intent(getActivity(), SessionCreationPart1.class);
                        startActivity(sessionIntent);
                        if (postCreationDialog != null) {
                            postCreationDialog.dismiss();
                        }
                        break;

                    case R.id.ll_article_wrapper:
                        Intent articleIntent = new Intent(getActivity(), CreateArticleActivity.class);
                        startActivity(articleIntent);
                        if (postCreationDialog != null) {
                            postCreationDialog.dismiss();
                        }
                        break;

                    case R.id.ll_poll_wrapper:
                        Intent pollIntent = new Intent(getActivity(), CreatePollActivity.class);
                        startActivity(pollIntent);
                        if (postCreationDialog != null) {
                            postCreationDialog.dismiss();
                        }
                        break;*/

                    /*case R.id.bt_status:
                        Intent msgListIntent=new Intent(NewsFeedActivity.this,AllMessagesListActivity.class);
                        startActivity(msgListIntent);
                        break;*/

                }
            }
        };


        onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                preferenceUtils.saveNewsFeedLastDate(null);
                switch (menuItem.getItemId()) {
                    case R.id.all:
                        currentFilter = "all";
                        break;

                    case R.id.post:
                        currentFilter = "";
                        break;

                    case R.id.sessions:
                        currentFilter = "session";
                        break;

                    case R.id.article:
                        currentFilter = "article";
                        break;

                    case R.id.question:
                        currentFilter = "question";
                        break;

                    case R.id.poll:
                        currentFilter = "poll";
                        break;
                }
                newsFeedList.clear();
                newsFeedAdapter.notifyDataSetChanged();
                isNewsFeedApiCalled = false;
                if (!currentFilter.equals("all")) {
                    tv_new_posts_indicator.setVisibility(View.GONE);
                } else if (newPostCounter >= 4) {
                    tv_new_posts_indicator.setVisibility(View.VISIBLE);
                }

                hitGetNewsFeedApi(user_id, "newsfeed", currentFilter, getLastPostScore());
                return false;
            }
        };
    }


    private void setClickListeners() {
     /*   iv_settings_icon.setOnClickListener(onClickListener);
        iv_message_icon.setOnClickListener(onClickListener);
        iv_notification_icon.setOnClickListener(onClickListener);*/
        //    bt_create_article.setOnClickListener(onClickListener);
        //    bt_create_session.setOnClickListener(onClickListener);
        //  iv_profile_info.setOnClickListener(onClickListener);
        //    tv_create_post.setOnClickListener(onClickListener);
        tv_new_posts_indicator.setOnClickListener(onClickListener);
        //   fb_create.setOnClickListener(onClickListener);
    }

    private void initNewsFeedList(View baseLayout) {
        newsFeedList = new ArrayList<>();
        newsFeedList.addAll(preferenceUtils.getNewsFeedList());
        rv_news_feed_list = (RecyclerView) baseLayout.findViewById(R.id.rv_news_feed_list);
        newsFeedAdapter = new NewsFeedAdapter(this, newsFeedList, null, false, false);
        newFeedLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_news_feed_list.setLayoutManager(newFeedLayoutManager);
        rv_news_feed_list.setAdapter(newsFeedAdapter);

        rv_news_feed_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //  Log.d("Scroll info","dx = "+dy);

                if (dy > 5) {

                    tv_new_posts_indicator.setVisibility(View.GONE);

                    if (!isNewsFeedApiCalled
                            && newFeedLayoutManager.findLastVisibleItemPosition() > newsFeedList.size() - 2) {
                        hitGetNewsFeedApi(user_id, "newsfeed", currentFilter, getLastPostScore());
                    }

                    /*if (!isAnimRunning && fb_create.getVisibility() == View.VISIBLE) {
                        fb_create.startAnimation(animScaleDown);
                    }*/
                    ((MainActivity) getActivity()).hideBars();
                } else if (dy < -5) {
                   /* if (!isAnimRunning && fb_create.getVisibility() == View.GONE) {
                        rl_parent_wrapper.addView(fb_create);
                        fb_create.setSystemUiVisibility(View.VISIBLE);
                        fb_create.startAnimation(animScaleUp);

                    }*/
                    ((MainActivity) getActivity()).showBars();
                    if (newPostCounter >= 4) {
                        tv_new_posts_indicator.setVisibility(View.VISIBLE);
                    }

                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        preferenceUtils.saveNewsFeedLastDate(null);
        hitGetNewsFeedApi(user_id, "newsfeed", currentFilter, getLastPostScore());

    }

    private String getLastPostScore() {
        if (!newsFeedList.isEmpty()) {
            for (int i = newsFeedList.size() - 1; i >= 0; i--) {
                if (newsFeedList.get(i).score != null) {
                    int score = Integer.parseInt(newsFeedList.get(i).score);
                    int adjustScore = 0;
                    calender = Calendar.getInstance();
                    Date curr = calender.getTime();
                    Date saved = preferenceUtils.getNewsFeedLastDate();

                    if (saved == null) {
                        return "0";
                    }
                    int multiplier = commonFunctions.getScoreMultiplier(newsFeedList.get(i).postType);
                    float diff = curr.getTime() - saved.getTime();
                    adjustScore = (int) (diff / 60000);
                    score = score + (multiplier * adjustScore);
                    return String.valueOf(score);
                }
            }
        }
        return "0";
    }



    /*private void initAnimations() {

        animScaleUp = AnimationUtils.loadAnimation(getContext(),
                R.anim.scale_up);
        animScaleUp.setFillEnabled(true);
        animScaleUp.setFillAfter(true);
        animScaleUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimRunning = true;

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimRunning = false;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        animScaleDown = AnimationUtils.loadAnimation(getContext(),
                R.anim.scale_down);
        animScaleDown.setFillEnabled(true);
        animScaleDown.setFillAfter(true);
        animScaleDown.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAnimRunning = true;

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isAnimRunning = false;
                fb_create.setSystemUiVisibility(View.GONE);
                rl_parent_wrapper.removeView(fb_create);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
*/


    private void hitGetNewsFeedApi(String user_id, String get, final String filter, final String score) {
        Call<PojoGetNewsFeedResponse> call = apiService.getNewsFeed(user_id, get, filter, score);
        newsFeedAdapter.setIsListEmpty(false);
        newsFeedAdapter.setShowProgressBar(true);
        ll_no_connection_wrapper.setVisibility(View.GONE);

        if (!isNewsFeedApiCalled) {
            isNewsFeedApiCalled = true;
            newsFeedAdapter.setShowNoMoreResults(false, getString(R.string.all_are_caught_up));
            call.enqueue(new Callback<PojoGetNewsFeedResponse>() {
                @Override
                public void onResponse(Call<PojoGetNewsFeedResponse> call, Response<PojoGetNewsFeedResponse> response) {
                    if (response.body() != null) {
                        calender = Calendar.getInstance();
                        preferenceUtils.saveNewsFeedLastDate(calender.getTime());
                        String message = response.body().message;
                        if (response.body().data != null) {
                            newsFeedAdapter.setShowProgressBar(false);
                            if (score.equals("0")) {
                                newsFeedList.clear();
                                isSuggestedListAdded = false;
                            }
                            List<PojoGetNewsFeedResponseData> filteredList = new ArrayList<>();


                            for (int i = 0; i < response.body().data.size(); i++) {
                                PojoGetNewsFeedResponseData currentItem = response.body().data.get(i);

                                if (currentItem.postId != null && !currentItem.postId.isEmpty()) {
                                    switch (currentItem.postType) {
                                        case "session_shared":
                                        case "session_attend":
                                        case "session":
                                            if (currentItem.origin.sessionsId == null
                                                    || currentItem.origin.sessionsId.isEmpty()) {
                                                continue;
                                            }
                                            break;


                                        case "article":
                                        case "article_shared":
                                        case "article_like":
                                        case "article_comment":
                                            if (currentItem.origin.articlesId == null
                                                    || currentItem.origin.articlesId.isEmpty()) {
                                                continue;
                                            }
                                            break;

                                        case "question":
                                        case "question_shared":
                                            if (currentItem.origin.sessionsQaId == null
                                                    || currentItem.origin.sessionsQaId.isEmpty()) {
                                                continue;
                                            }
                                            break;

                                        case "answer_shared":
                                            if (currentItem.origin.sessionsQaId == null
                                                    || currentItem.origin.sessionsQaId.isEmpty()
                                                    || currentItem.answer.sessionsQaId == null) {
                                                continue;
                                            }
                                            break;

                                        /*case "photos":
                                        case "":
                                            if (currentItem.postId == null
                                                    || currentItem.postId.isEmpty()) {
                                                continue;
                                            }
                                            break;*/

                                        case "post_like":
                                        case "post_comment":
                                        case "post_shared":
                                            //todo revert after post item is fixed
                                            if (currentItem.origin != null) {
                                                if (currentItem.origin.postId == null
                                                        || currentItem.origin.postId.isEmpty()) {
                                                    if (currentItem.origin.articlesId == null) {
                                                        continue;
                                                    }
                                                }
                                                if (currentItem.origin.origin != null && currentItem.origin.origin.postId == null) {
                                                    continue;
                                                }
                                            }
                                            if (currentItem.liked_by == null) {
                                                continue;
                                            }
                                            break;

                                      /*  case "poll":
                                            if(currentItem.postId==null||currentItem.postId.isEmpty()){
                                                continue;
                                            }
                                            break;*/

                                        case "interest_shared":
                                            if (currentItem.origin.interestId == null
                                                    || currentItem.origin.interestId.isEmpty()) {
                                                continue;
                                            }
                                            break;

                                        case "venue_shared":
                                            if (currentItem.origin.venueId == null
                                                    || currentItem.origin.venueId.isEmpty()) {
                                                continue;
                                            }
                                            break;


                                        case "shared":
                                            if (currentItem.origin.postType.equals("poll")
                                                    || currentItem.origin.postType.equals("photos")
                                                    || currentItem.origin.postType.equals("")) {
                                                if (currentItem.origin.postId == null
                                                        || currentItem.origin.postId.isEmpty()) {
                                                    continue;
                                                }
                                            }
                                            break;
                                    }
                                    filteredList.add(currentItem);
                                }
                            }

                            int b = 1;
                            //this step is needed because sometimes we get duplicate post
                            try {
                                if (!newsFeedList.isEmpty() && !filteredList.isEmpty()) {
                                    String postIdToCompare = newsFeedList.get(newsFeedList.size() - 1).postId;
                                    if (postIdToCompare == null && newsFeedList.size() > 1) {
                                        postIdToCompare = newsFeedList.get(newsFeedList.size() - 2).postId;
                                    }
                                    if (postIdToCompare == null) {
                                        postIdToCompare = "";
                                    }
                                    if (filteredList.get(0).postId == null) {
                                        filteredList.get(0).postId = "";
                                    }
                                    if (postIdToCompare.equals(filteredList.get(0).postId)) {
                                        filteredList.remove(0);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            newsFeedList.addAll(filteredList);
                            if (score.equals("0")) {
                                preferenceUtils.saveNewsFeedList(newsFeedList);
                            }

                            if (!isSuggestedListAdded && filter.equals("all") && newsFeedList.size() >= 9) {
                                isSuggestedListAdded = true;
                                PojoGetNewsFeedResponseData suggested = new PojoGetNewsFeedResponseData();
                                suggested.postType = "suggested_users";
                                newsFeedList.add(9, suggested);
                            }
                            if (score.equals("0") && newsFeedList.isEmpty()) {
                                newsFeedAdapter.setIsListEmpty(true);
                            } else {
                                if (filteredList.isEmpty()) {
                                    newsFeedAdapter.setShowNoMoreResults(true, getString(R.string.all_are_caught_up));
                                }
                            }
                            newsFeedAdapter.notifyDataSetChanged();

                        } else {
                            if (getActivity() != null)
                                commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                    }
                    srl_refresh.setRefreshing(false);
                    isNewsFeedApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoGetNewsFeedResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    srl_refresh.setRefreshing(false);
                    isNewsFeedApiCalled = false;
                    newsFeedAdapter.setShowProgressBar(false);
                    if (newsFeedList.isEmpty()) {
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }


    @Override
    public void newsFeedItemClickCallback(final int position, PojoGetNewsFeedResponseData currentItem1, String type, String data, String sub_data) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);
        Intent intent;
        /*if (user_id.equals(AppKeys.GUEST_USER_ID)) {
            switch (type) {
                case AppKeys.OPEN_PROFILE:
                    intent = new Intent(getActivity(), ProfileInfoActivity.class);
                    intent.putExtra("target_user_id", data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_SESSION:
                    intent = new Intent(getActivity(), SessionLoaderActivity.class);
                    intent.putExtra("id", data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_POST:
                    intent = new Intent(getActivity(), PostLoaderActivity.class);
                    intent.putExtra(AppKeys.POST_ID, data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_POST_COMMENT:
                    intent = new Intent(getActivity(), PostLoaderActivity.class);
                    intent.putExtra(AppKeys.POST_ID, data);
                    intent.putExtra(AppKeys.COMMENT_ID, sub_data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_ARTICLE:
                    intent = new Intent(getActivity(), ArticleActivity.class);
                    intent.putExtra("id", data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_QUESTION:
                    intent = new Intent(getActivity(), QuestionActivity.class);
                    intent.putExtra("id", data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_ORGANISATION:
                    intent = new Intent(getActivity(), OrganisationProfileInfoActivity.class);
                    intent.putExtra(AppKeys.ORGANISATION_ID, data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_INTEREST:
                    intent = new Intent(getActivity(), InterestActivity.class);
                    intent.putExtra("id", data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_VENUE:
                    intent = new Intent(getActivity(), VenueActivity.class);
                    intent.putExtra("id", data);
                    startActivity(intent);
                    break;

                case AppKeys.SHARE_TO_SESSION:
                    shareToSession(currentItem);
                    break;

                case AppKeys.SHARE_TO_ARTICLE:
                    shareToArticle(currentItem);
                    break;

                case AppKeys.SHARE_TO_INTEREST:
                    ShareContentOnMediaApps.shareContent(getActivity(),
                            AppKeys.SHARE_INTEREST, currentItem.origin.interestId,
                            currentItem.origin.text, null);
                    break;

                case AppKeys.SHARE_TO_QUESTION:
                    ShareContentOnMediaApps.shareContent(getActivity(),
                            AppKeys.SHARE_QUESTION, currentItem.origin.sessionsQaId, currentItem.origin.post,
                            null);
                    break;

                case AppKeys.SHARE_TO_ANSWER:
                    ShareContentOnMediaApps.shareContent(getActivity(),
                            AppKeys.SHARE_ANSWER, currentItem.answer.parentQuestionId + "/" + currentItem.answer.sessionsQaId,
                            currentItem.origin.post, null);
                    break;

                case AppKeys.SHARE_TO_VENUE:
                    shareToVenue(currentItem);
                    break;

                case AppKeys.SHARE_TO_POST:
                    if (currentItem.origin != null && currentItem.origin.origin != null) {
                        ShareContentOnMediaApps.shareContent(getActivity(), AppKeys.SHARE_TO_POST,
                                currentItem.origin.postId, getString(R.string.check_this_on_luneblaze), null);
                    } else {
                        ShareContentOnMediaApps.shareContent(getActivity(), AppKeys.SHARE_TO_POST,
                                currentItem.postId, getString(R.string.check_this_on_luneblaze), null);
                    }
                    break;

                default:
                    commonFunctions.showLoginRequestDialog();
                    break;
            }

        } else*/ {

            switch (type) {
                case AppKeys.OPEN_PROFILE:
                    intent = new Intent(getActivity(), ProfileInfoActivity.class);
                    intent.putExtra("target_user_id", data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_SESSION:
                    intent = new Intent(getActivity(), SessionLoaderActivity.class);
                    intent.putExtra("id", data);
                    startActivity(intent);
                    break;

                case AppKeys.DISCUSSION_SESSION:
                    intent = new Intent(getActivity(), SessionLoaderActivity.class);
                    intent.putExtra("id", data);
                    intent.putExtra("type", sub_data);
                    startActivity(intent);
                    break;

                case AppKeys.ACCEPT_SESSION:
                    intent = new Intent(getActivity(), SessionAcceptFormActivity.class);
                    intent.putExtra("id", data);
                    startActivity(intent);
                    break;

                case AppKeys.SESSION_ACTION:
                    intent = new Intent(getActivity(), SessionLoaderActivity.class);
                    intent.putExtra("id", data);
                    intent.putExtra("action", sub_data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_POST:
                    intent = new Intent(getActivity(), PostLoaderActivity.class);
                    intent.putExtra(AppKeys.POST_ID, data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_POST_COMMENT:
                    intent = new Intent(getActivity(), PostLoaderActivity.class);
                    intent.putExtra(AppKeys.POST_ID, data);
                    intent.putExtra(AppKeys.COMMENT_ID, sub_data);
                    startActivity(intent);
                    break;

                case AppKeys.ADD_POST_COMMENT:
                    intent = new Intent(getActivity(), PostLoaderActivity.class);
                    intent.putExtra(AppKeys.POST_ID, data);
                    intent.putExtra(AppKeys.ACTION, sub_data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_ARTICLE:
                    intent = new Intent(getActivity(), ArticleActivity.class);
                    intent.putExtra("id", data);
                    startActivity(intent);
                    break;

                case AppKeys.ARTICLE_COMMENT:
                    intent = new Intent(getActivity(), ArticleActivity.class);
                    intent.putExtra("id", data);
                    intent.putExtra(AppKeys.ACTION, sub_data);
                    startActivity(intent);
                    break;

                case AppKeys.ARTICLE_COMMENT_LIST:
                    intent = new Intent(getActivity(), ArticleCommentListActivity.class);
                    intent.putExtra("id", data);
                    intent.putExtra(AppKeys.COMMENT_ID, sub_data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_QUESTION:
                    intent = new Intent(getActivity(), QuestionActivity.class);
                    intent.putExtra("id", data);
                    startActivity(intent);
                    break;

                case AppKeys.ADD_ANSWER:
                    intent = new Intent(getActivity(), QuestionActivity.class);
                    intent.putExtra(AppKeys.ID, data);
                    intent.putExtra(AppKeys.ANSWER_ID, sub_data);
                    intent.putExtra(AppKeys.ACTION, AppKeys.ADD_ANSWER);
                    startActivity(intent);
                    break;

                case AppKeys.ANSWER_ACTION:
                    intent = new Intent(getActivity(), AnswerReplyListActivity.class);
                    intent.putExtra(AppKeys.ID, data);
                    intent.putExtra(AppKeys.ACTION, sub_data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_ORGANISATION:
                    intent = new Intent(getActivity(), OrganisationProfileInfoActivity.class);
                    intent.putExtra(AppKeys.ORGANISATION_ID, data);
                    startActivity(intent);
                    break;

                case AppKeys.BOOST_POST:
                    intent = new Intent(getActivity(), AdTargetAudienceActivity.class);
                    intent.putExtra(AppKeys.POST_ID, data);
                    intent.putExtra(AppKeys.ORGANISATION_ID, sub_data);
                    startActivityForResult(intent, AppKeys.BOOST_POST_REQUEST_CODE);
                    break;


                case AppKeys.OPEN_INTEREST:
                    intent = new Intent(getActivity(), InterestActivity.class);
                    intent.putExtra("id", data);
                    startActivity(intent);
                    break;

                case AppKeys.OPEN_VENUE:
                    intent = new Intent(getActivity(), VenueActivity.class);
                    intent.putExtra("id", data);
                    startActivity(intent);
                    break;


////////////////////////////////////////////
                case AppKeys.HIDE:
                    commonFunctions.hitPostReactionApi(user_id, currentItem.postId, "hide_post");
                    newsFeedList.get(position).isHidden = true;
                    newsFeedAdapter.notifyDataSetChanged();
                    break;

                case AppKeys.UNHIDE_POST:
                    commonFunctions.hitPostReactionApi(user_id, currentItem.postId, "unhide_post");
                    newsFeedList.get(position).isHidden = false;
                    newsFeedAdapter.notifyDataSetChanged();
                    break;

                case AppKeys.REPORT_SESSION:
                    openReportDialog(position, user_id, "report_session", currentItem.originId);
                    break;

                case AppKeys.SESSION_POST_DELETE:

                    break;

                case AppKeys.INVITE_SESSION:
                    Intent inviteIntent = new Intent(getActivity(), InviteActivity.class);
                    inviteIntent.putExtra("userType", "invite_session_user_list");
                    inviteIntent.putExtra("id", currentItem.origin.sessionsId);
                    startActivity(inviteIntent);
                    break;

                case AppKeys.SHARE_TO_SESSION:
                    shareToSession(currentItem);
                    break;

                case AppKeys.PIN_ARTICLE:
                    if (newsFeedList.get(position).pinned) {
                        commonFunctions.hitPostReactionApi(user_id, currentItem.postId, "unpin_post");
                    } else {
                        commonFunctions.hitPostReactionApi(user_id, currentItem.postId, "pin_post");
                    }
                    newsFeedList.get(position).pinned = !newsFeedList.get(position).pinned;
                    newsFeedAdapter.notifyDataSetChanged();
                    break;

                case AppKeys.SAVE_ARTICLE:
                    if (newsFeedList.get(position).iSave) {
                        commonFunctions.hitPostReactionApi(user_id, currentItem.postId, "unsave_post");
                    } else {
                        commonFunctions.hitPostReactionApi(user_id, currentItem.postId, "save_post");
                    }
                    newsFeedList.get(position).iSave = !newsFeedList.get(position).iSave;
                    newsFeedAdapter.notifyDataSetChanged();
                    break;

                case AppKeys.REPORT_ARTICLE:
                    openReportDialog(position, user_id, "report_article", currentItem.originId);
                    break;


                case AppKeys.ARTICLE_LIKE:
                    if (newsFeedList.get(position).origin.iLike == 1) {
                        newsFeedList.get(position).origin.iLike = 0;
                        hitArticleReactionApi(user_id, currentItem.origin.articlesId, "unlike_article");
                    } else {
                        newsFeedList.get(position).origin.iLike = 1;
                        hitArticleReactionApi(user_id, currentItem.origin.articlesId, "like_article");
                    }
                    newsFeedAdapter.notifyDataSetChanged();
                    break;

                case AppKeys.EDIT_ARTICLE:
                    //  openReportDialog(position,user_id, "report_post", currentItem.postId);
                    break;

                case AppKeys.DELETE_ARTICLE:
                    deleteArticleConfirmation(currentItem, position);
                    break;

                case AppKeys.SHARE_TO_ARTICLE:
                    shareToArticle(currentItem);
                    break;


                case AppKeys.INTEREST_FOLLOW:
                    if (newsFeedList.get(position).origin.iFollow == 1) {
                        newsFeedList.get(position).origin.iFollow = 0;
                        hitFollowInterestApi(user_id, currentItem.originId, "interest_unfollow");
                    } else {
                        newsFeedList.get(position).origin.iFollow = 1;
                        hitFollowInterestApi(user_id, currentItem.originId, "interest_follow");
                    }
                    newsFeedAdapter.notifyDataSetChanged();
                    break;


                case AppKeys.SHARE_TO_INTEREST:
                    ShareContentOnMediaApps.shareContent(getActivity(),
                            AppKeys.SHARE_INTEREST, currentItem.origin.interestId,
                            currentItem.origin.text, null);
                    break;

                case AppKeys.VENUE_FOLLOW:
                    if (newsFeedList.get(position).origin.iFollow == 1) {
                        newsFeedList.get(position).origin.iFollow = 0;
                        hitFollowVenueApi(user_id, currentItem.origin.venueId, "remove_notify_me_venue", position);
                    } else {
                        newsFeedList.get(position).origin.iFollow = 1;
                        hitFollowVenueApi(user_id, currentItem.origin.venueId, "notify_me_venue", position);
                    }
                    newsFeedAdapter.notifyDataSetChanged();
                    break;

                case AppKeys.POST_LIKE:
                    int likes = newsFeedList.get(position).totalLikes;
                    if (newsFeedList.get(position).iLike == 1) {
                        newsFeedList.get(position).iLike = 0;
                        likes--;
                        commonFunctions.hitPostReactionApi(user_id, currentItem.postId, "unlike_post");
                    } else {
                        newsFeedList.get(position).iLike = 1;
                        likes++;
                        commonFunctions.hitPostReactionApi(user_id, currentItem.postId, "like_post");
                    }
                    newsFeedList.get(position).totalLikes = likes;
                    newsFeedAdapter.notifyDataSetChanged();
                    break;

                case AppKeys.THANK:
                    if (newsFeedList.get(position).origin.iThanks == 0) {
                        hitSessionReactionApi(user_id, currentItem.originId, "session_thanks");
                        newsFeedList.get(position).origin.iThanks = 1;
                        int thankCount = Integer.parseInt(currentItem.origin.thanksCount);
                        thankCount++;
                        currentItem.origin.thanksCount = String.valueOf(thankCount);
                    }
                    newsFeedAdapter.notifyDataSetChanged();
                    break;

                case AppKeys.EDIT_POST: {
                    String dataStr = "";
                    Type postType = new TypeToken<PojoGetNewsFeedResponseData>() {
                    }.getType();
                    dataStr = new Gson().toJson(newsFeedList.get(position), postType);
                    Intent editPostIntent = new Intent(getActivity(), PostCreationActivity.class);
                    editPostIntent.putExtra("data", dataStr);
                    startActivity(editPostIntent);
                }
                break;

                case AppKeys.EDIT_POST_TITLE:
                    openShareDialog(position, "", currentItem, "edit");
                    break;

                case AppKeys.POLL_EDIT: {
                    String dataStr = "";
                    Type postType = new TypeToken<PojoGetNewsFeedResponseData>() {
                    }.getType();
                    dataStr = new Gson().toJson(newsFeedList.get(position), postType);
                    Intent editPostIntent = new Intent(getActivity(), CreatePollActivity.class);
                    editPostIntent.putExtra("data", dataStr);
                    startActivity(editPostIntent);
                }
                break;

                case AppKeys.POLL_DELETE:
                case AppKeys.POST_DELETE:
                    postDeleteConfirmation(currentItem, position, type);
                    break;

                case AppKeys.UNFOLLOW_USER:
                    unfollowUserConfirmation(currentItem, type);
                    break;


                case AppKeys.EDIT_QUESTION:
                    openReportDialog(position, user_id, "report_question", currentItem.originId);
                    break;

                case AppKeys.DELETE_QUESTION:
                    deleteQuestionConfirmation(currentItem, position);
                    break;

                case AppKeys.SHARE_TO_QUESTION:
                    ShareContentOnMediaApps.shareContent(getActivity(),
                            AppKeys.SHARE_QUESTION, currentItem.origin.sessionsQaId, currentItem.origin.post,
                            null);
                    break;

                case AppKeys.QUESTION_UPVOTE:
                    if (newsFeedList.get(position).origin.iLike == 1) {
                        hitUpvoteQuestionApi(user_id, "unlike_qacomment",
                                newsFeedList.get(position).origin.sessionsQaId,
                                newsFeedList.get(position).origin.sessionsId);
                        newsFeedList.get(position).origin.iLike = 0;
                    } else {
                        hitUpvoteQuestionApi(user_id, "like_qacomment",
                                newsFeedList.get(position).origin.sessionsQaId,
                                newsFeedList.get(position).origin.sessionsId);
                        newsFeedList.get(position).origin.iLike = 1;

                    }
                    newsFeedAdapter.notifyDataSetChanged();
                    break;

                case AppKeys.QUESTION_FOLLOW:
                    if (newsFeedList.get(position).origin.iFollow == 1) {
                        hitFollowInterestApi(user_id, newsFeedList.get(position).origin.sessionsQaId,
                                "remove_question_notify");
                        newsFeedList.get(position).origin.iFollow = 0;
                    } else {
                        hitFollowInterestApi(user_id, newsFeedList.get(position).origin.sessionsQaId,
                                "question_notify");
                        newsFeedList.get(position).origin.iFollow = 1;
                    }

                    newsFeedAdapter.notifyDataSetChanged();
                    break;

                case AppKeys.REPORT_QUESTION:
                    openReportDialog(position, user_id, "report_question", currentItem.originId);
                    break;

                case AppKeys.ANSWER_UPVOTE:
                    int totalLikes = Integer.parseInt(newsFeedList.get(position).answer.totalLikes);
                    if (newsFeedList.get(position).answer.iLike != null && newsFeedList.get(position).answer.iLike == 0) {
                        hitUpvoteAnswerApi(user_id, "like_qacomment", newsFeedList.get(position).answer.sessionsId,
                                newsFeedList.get(position).answer.sessionsQaId, position);
                        totalLikes++;
                        newsFeedList.get(position).answer.iLike = 1;
                    } else {
                        hitUpvoteAnswerApi(user_id, "unlike_qacomment", newsFeedList.get(position).answer.sessionsId,
                                newsFeedList.get(position).answer.sessionsQaId, position);
                        totalLikes--;
                        newsFeedList.get(position).answer.iLike = 0;

                    }
                    newsFeedList.get(position).answer.totalLikes = String.valueOf(totalLikes);
                    newsFeedAdapter.notifyDataSetChanged();
                    // openReportDialog(position,user_id, "report_answer", currentItem.answer.sessionsQaId);
                    break;

                case AppKeys.ANSWER_SHARE:
                    openShareDialog(position, "share_answer", currentItem, "share");
                    break;

                case AppKeys.EDIT_ANSWER:
                    // openReportDialog(position,user_id, "report_answer", currentItem.answer.sessionsQaId);
                    break;

                case AppKeys.DELETE_ANSWER:
                    deleteAnswerConfirmation(currentItem, position);
                    break;

                case AppKeys.SHARE_TO_ANSWER:
                    ShareContentOnMediaApps.shareContent(getActivity(),
                            AppKeys.SHARE_ANSWER, currentItem.answer.parentQuestionId + "/" + currentItem.answer.sessionsQaId,
                            currentItem.origin.post, null);
                    break;

                case AppKeys.REPORT_ANSWER:
                    openReportDialog(position, user_id, "report_answer", currentItem.answer.sessionsQaId);
                    break;

                case AppKeys.REPORT_INTEREST:
                    openReportDialog(position, user_id, "report_interest", currentItem.originId);
                    break;

                case AppKeys.REPORT_VENUE:
                    openReportDialog(position, user_id, "report_venue", currentItem.originId);
                    break;

                case AppKeys.REPORT_POST:
                    openReportDialog(position, user_id, "report_post", currentItem.postId);
                    break;

                case AppKeys.SHARE_TO_VENUE:
                    shareToVenue(currentItem);
                    break;


                case AppKeys.SESSION_SHARE:
                    openShareDialog(position, "share_session", currentItem, "share");
                    break;

                case AppKeys.ARTICLE_SHARE:
                    openShareDialog(position, "share_article", currentItem, "share");
                    break;

                case AppKeys.ORIGIN_ARTICLE_SHARE:
                    openShareDialog(position, "share_article", currentItem, "share");
                    break;

                case AppKeys.QUESTION_SHARE:
                    openShareDialog(position, "share_question", currentItem, "share");
                    break;

                case AppKeys.VENUE_SHARE:
                    openShareDialog(position, "share_venue", currentItem, "share");
                    break;

                case AppKeys.INTEREST_SHARE:
                    openShareDialog(position, "share_interest", currentItem, "share");
                    break;

                case AppKeys.POST_SHARE:
                    openShareDialog(position, "share", currentItem, "share");
                    break;


                case AppKeys.SHARE_TO_POST:
                    if (currentItem.origin != null && currentItem.origin.origin != null) {
                        ShareContentOnMediaApps.shareContent(getActivity(), AppKeys.SHARE_TO_POST,
                                currentItem.origin.postId, getString(R.string.check_this_on_luneblaze), null);
                    } else {
                        ShareContentOnMediaApps.shareContent(getActivity(), AppKeys.SHARE_TO_POST,
                                currentItem.postId, getString(R.string.check_this_on_luneblaze), null);
                    }
                    break;


            }
            if (newsFeedList != null && !newsFeedList.isEmpty()) {
                preferenceUtils.saveNewsFeedList(newsFeedList);
            }
        }


    }

    private void deleteAnswerConfirmation(PojoGetNewsFeedResponseData currentItem, int position) {

        TextView tv_dialog_description, tv_no, tv_yes;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(getActivity());
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonFunctions.hitPostReactionApi(user_id, currentItem.answer.sessionsQaId, "delete_answer");
                newsFeedList.remove(position);
                newsFeedAdapter.notifyDataSetChanged();
                confirmationDialog.dismiss();
            }
        });
        tv_dialog_description.setText(R.string.do_you_want_to_delete_this_answer);
        confirmationDialog.show();

    }

    private void deleteQuestionConfirmation(PojoGetNewsFeedResponseData currentItem, int position) {

        TextView tv_dialog_description, tv_no, tv_yes;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(getActivity());
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonFunctions.hitPostReactionApi(user_id, currentItem.origin.sessionsQaId, "delete_question");
                newsFeedList.remove(position);
                newsFeedAdapter.notifyDataSetChanged();
                confirmationDialog.dismiss();
            }
        });
        tv_dialog_description.setText(R.string.do_you_want_to_delete_this_question);
        confirmationDialog.show();

    }

    private void unfollowUserConfirmation(PojoGetNewsFeedResponseData currentItem, String type) {
        TextView tv_dialog_description, tv_no, tv_yes;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(getActivity());
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userToUnfollow = "";
                if (currentItem.postType.equals("post_like")
                        || currentItem.postType.equals("post_comment")
                        || currentItem.postType.equals("article_like")
                        || currentItem.postType.equals("article_comment")
                        || currentItem.postType.equals("poll_vote")) {
                    //todo uncomment below after newsfeed is fixed
                    userToUnfollow = currentItem.liked_by.userId;
                    hitSessionReactionApi(user_id, userToUnfollow, "unfollow");
                } else if (currentItem.postType.equals("organisation_post")) {
                    hitSessionReactionApi(user_id, currentItem.organizationData.id, "organizer_unfollow");
                } else {
                    userToUnfollow = currentItem.authorId;
                    hitSessionReactionApi(user_id, userToUnfollow, "unfollow");
                }
                confirmationDialog.dismiss();
            }
        });
        tv_dialog_description.setText(R.string.unfollow_confirmation_message);
        confirmationDialog.show();
    }

    private void postDeleteConfirmation(PojoGetNewsFeedResponseData currentItem, int position, String type) {
        TextView tv_dialog_description, tv_no, tv_yes;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(getActivity());
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });
        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonFunctions.hitPostReactionApi(user_id, currentItem.postId, "delete_post");
                newsFeedList.remove(position);
                newsFeedAdapter.notifyDataSetChanged();
                confirmationDialog.dismiss();
            }
        });

        if (type.equals(AppKeys.POST_DELETE)) {
            tv_dialog_description.setText(R.string.do_you_want_to_delete_this_post);
        } else if (type.equals(AppKeys.POLL_DELETE)) {
            tv_dialog_description.setText(R.string.do_you_want_to_delete_this_poll);
        }
        confirmationDialog.show();

    }

    private void deleteArticleConfirmation(PojoGetNewsFeedResponseData currentItem, int position) {

        TextView tv_dialog_description, tv_no, tv_yes;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(getActivity());
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonFunctions.hitPostReactionApi(user_id, currentItem.origin.articlesId, "delete_article");
                newsFeedList.remove(position);
                newsFeedAdapter.notifyDataSetChanged();
                confirmationDialog.dismiss();
            }
        });
        tv_dialog_description.setText(R.string.do_you_want_to_delete_this_article);
        confirmationDialog.show();

    }

    private void shareToSession(PojoGetNewsFeedResponseData currentItem) {
        if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                .asBitmap()
                .load(currentItem.origin.coverPhoto)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {

                                  ShareContentOnMediaApps.shareContent(getActivity(),
                                          AppKeys.SHARE_SESSION, currentItem.origin.sessionsId,
                                          currentItem.origin.title, bitmap);

                                  return false;
                              }
                          }
                ).submit();
    }

    private void shareToArticle(PojoGetNewsFeedResponseData currentItem) {
        if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                .asBitmap()
                .load(currentItem.origin.coverPhoto)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o,
                                                          Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o,
                                                             Target<Bitmap> target, DataSource dataSource, boolean b) {
                                  ShareContentOnMediaApps.shareContent(getActivity(),
                                          AppKeys.SHARE_ARTICLE, currentItem.origin.articlesId,
                                          currentItem.origin.title, bitmap);

                                  return false;
                              }
                          }
                ).submit();
    }

    private void shareToVenue(PojoGetNewsFeedResponseData currentItem) {
        if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                .asBitmap()
                .load(currentItem.origin.coverPhoto)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target,
                                                             DataSource dataSource, boolean b) {
                                  ShareContentOnMediaApps.shareContent(getActivity(),
                                          AppKeys.SHARE_VENUE, currentItem.origin.venueId,
                                          currentItem.origin.venueName, bitmap);

                                  return false;
                              }
                          }
                ).submit();

    }


    private void hitUpvoteAnswerApi(String user_id, final String type, String sessionsId, String sessionsQaId, final int position) {
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type, sessionsId,
                "", "", "", sessionsQaId);
        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call,
                                   Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                            t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    public void openReportDialog(final int position, final String user_id, final String type, final String id) {
        final Dialog dialog = new MyCustomThemeDialog(getActivity());
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tv_dialog_description = (TextView) dialog.findViewById(R.id.tv_dialog_description);

        tv_dialog_description.setText(getActivity().getString(R.string.report_reason_description));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonFunctions.hitReportContentApi(user_id, type, id, et_content.getText().toString().trim());
                newsFeedList.remove(position);
                newsFeedAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    public void hitFollowInterestApi(String user_id, String friend_id, String connection_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }


    private void hitArticleReactionApi(String user_id, String article_id, String reaction) {
        Call<PojoReportContentResponse> call = apiService.articleReaction(user_id, article_id, reaction);
        call.enqueue(new Callback<PojoReportContentResponse>() {
            @Override
            public void onResponse(Call<PojoReportContentResponse> call, Response<PojoReportContentResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoReportContentResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }

    private void hitSessionReactionApi(String user_id, String session_id, String reaction) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, session_id, reaction);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

    private void hitUpvoteQuestionApi(String user_id, String type, final String question_id, String session_id) {
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type, session_id, "", "", "", question_id);
        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call,
                                   Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // hitGetQuestionDetailsApi(preferenceUtils.get_user_id(), question_id, "question");
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitFollowVenueApi(String user_id, String venue_id, String connection_type, final int position) {
        {
            Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, venue_id, connection_type);
            call.enqueue(new Callback<PojoUserConnectResponse>() {
                @Override
                public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                    if (response.body() != null) {
                        String message = response.body().message;
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                    }
                }

                @Override
                public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            });


        }
    }

    public void openShareDialog(final int position, final String shareType,
                                final PojoGetNewsFeedResponseData currentItem, String actionType) {
        TextView tv_username;
        ImageView iv_user_dp;
        final EditText et_share_title;
        RecyclerView rv_share_item;
        Button tv_share_button;
        final Dialog shareDialog;
        shareDialog = new MyCustomThemeDialog(getActivity());
        shareDialog.setContentView(R.layout.share_dialog_box_layout);
        shareDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv_username = shareDialog.findViewById(R.id.tv_username);
        iv_user_dp = (ImageView) shareDialog.findViewById(R.id.iv_user_dp);
        et_share_title = (EditText) shareDialog.findViewById(R.id.et_share_title);

        if (shareType.equals("share_venue") || shareType.equals("share_interest")) {
            et_share_title.setVisibility(View.GONE);
        }

        PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
        tv_username.setText(pojoLoginResponseData.userFullname);

        if (getActivity() != null) Glide.with(getActivity().getApplicationContext())
                .load(pojoLoginResponseData.userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_user_dp);

        tv_share_button = (Button) shareDialog.findViewById(R.id.tv_share_button);

        List<PojoGetNewsFeedResponseData> shareList = new ArrayList<>();
        shareList.add(currentItem);

        rv_share_item = (RecyclerView) shareDialog.findViewById(R.id.rv_share_item);
        rv_share_item.setLayoutManager(new LinearLayoutManager(getActivity(),
                RecyclerView.VERTICAL, false));
        rv_share_item.setAdapter(new ShareNewsFeedAdapter(this, shareList));

        if (actionType.equals("share")) {
            final String share_id;
            if (currentItem.postType.equals("question") || currentItem.postType.equals("question_shared")) {
                share_id = currentItem.origin.sessionsQaId;
            } else if (currentItem.postType.equals("answer_shared")) {
                share_id = currentItem.answer.sessionsQaId;
            } else if (currentItem.origin != null) {
                if (currentItem.origin.origin != null) {
                    if (currentItem.origin.origin.articlesId != null) {
                        share_id = currentItem.origin.origin.articlesId;
                    } else {
                        share_id = currentItem.origin.originId;
                    }

                } else {
                    if (currentItem.origin.articlesId != null) {
                        share_id = currentItem.origin.articlesId;
                    } else {
                        share_id = currentItem.originId;
                    }
                }
            } else {
                share_id = currentItem.postId;
            }

            tv_share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    hitShareContentApi(user_id, share_id, shareType, et_share_title.getText().toString().trim());
                    shareDialog.dismiss();
                }
            });
        } else {
            tv_share_button.setText(getString(R.string.done));
            et_share_title.setText(currentItem.postTitle);
            et_share_title.setSelection(et_share_title.getText().length());
            tv_share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String title = et_share_title.getText().toString().trim();
                    if (!title.isEmpty()) {
                        hitEditShareTitleApi(user_id, "posttitle", currentItem.postId,
                                et_share_title.getText().toString().trim());
                        newsFeedList.get(position).postTitle = title;
                        newsFeedAdapter.notifyDataSetChanged();
                    } else {
                        if (getActivity() != null)
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                    getString(R.string.please_enter_a_post_title), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
                    shareDialog.dismiss();
                }
            });
        }
        shareDialog.show();

    }

    private void hitEditShareTitleApi(String user_id, String type, String id, String message) {

        Call<PojoNoDataResponse> call = apiService.editPostComment(user_id, type, id, message);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                getString(R.string.done), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                            AppKeys.TOAST_DEBUG);
            }
        });

    }


    private void hitShareContentApi(String user_id, String id, String reaction, String title) {
        if (title.isEmpty()) {
            title = null;
        }
        Call<PojoNoDataResponse> call = apiService.shareContent(user_id, id, reaction, title);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                getString(R.string.shared_successfully), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                            t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }


    @Override
    public void shareNewsFeedItemClickCallback(int position, PojoGetNewsFeedResponseData currentItem, String type) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);
    }


    @Override
    public void pollPostAdapterMethod(int position, int totalVotes, PojoNewsFeedPollOption pojoNewsFeedPollOption, int newsFeedItemPosition) {
        switch (newsFeedList.get(newsFeedItemPosition).postType) {
            case "photos":
            case "":
            case "post_like":
            case "post_comment":
            case "poll":
            case "poll_vote":
                if (newsFeedList.get(newsFeedItemPosition).origin != null) {
                    if (newsFeedList.get(newsFeedItemPosition).origin.poll != null) {
                        if (newsFeedList.get(newsFeedItemPosition).postType.equals("post_like")
                                || newsFeedList.get(newsFeedItemPosition).postType.equals("post_comment")
                                || newsFeedList.get(newsFeedItemPosition).postType.equals("shared")) {
                            newsFeedList.get(newsFeedItemPosition).origin.poll.votes = String.valueOf(totalVotes);
                            newsFeedList.get(newsFeedItemPosition).origin.poll.options.get(position).checked = true;
                            newsFeedList.get(newsFeedItemPosition).origin.poll.options.get(position).votes++;
                        }
                    }
                } else {
                    newsFeedList.get(newsFeedItemPosition).poll.votes = String.valueOf(totalVotes);
                    newsFeedList.get(newsFeedItemPosition).poll.options.get(position).checked = true;
                    newsFeedList.get(newsFeedItemPosition).poll.options.get(position).votes++;
                }
                break;

            case "shared":
                newsFeedList.get(newsFeedItemPosition).origin.poll.votes = String.valueOf(totalVotes);
                newsFeedList.get(newsFeedItemPosition).origin.poll.options.get(position).checked = true;
                newsFeedList.get(newsFeedItemPosition).origin.poll.options.get(position).votes++;
                break;
        }
        preferenceUtils.saveNewsFeedList(newsFeedList);
        newsFeedAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppKeys.BOOST_POST_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String post_id = data.getStringExtra(AppKeys.POST_ID);
                if (newsFeedList != null) {
                    for (int i = 0; i < newsFeedList.size(); i++) {
                        if (newsFeedList.get(i).postId.equals(post_id)) {
                            newsFeedList.get(i).postType = "ad";
                            newsFeedAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    }
}
