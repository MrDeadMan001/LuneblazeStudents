package com.avadna.luneblaze.fragments.profileinfo;

import android.app.Dialog;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuInflater;
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
import com.avadna.luneblaze.activities.InviteActivity;
import com.avadna.luneblaze.activities.PostCreationActivity;
import com.avadna.luneblaze.activities.polls.CreatePollActivity;
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
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileInfoResponseData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionDiscussionResponse;
import com.avadna.luneblaze.rest.ApiClientLongDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class ProfileInfoPollsListFragment extends Fragment implements
        NewsFeedAdapter.NewsFeedAdapterCallback,
        ShareNewsFeedAdapter.ShareNewsFeedAdapterCallback,
        PollPostAdapter.PollPostAdapterCallback {
    RelativeLayout rl_parent_wrapper;
    //  LinearLayout ll_post_box_wrapper;

    LinearLayout ll_no_connection_wrapper;
    TextView tv_retry;


    LinearLayout ll_content_wrapper;
    RecyclerView rv_news_feed_list;
    NewsFeedAdapter newsFeedAdapter;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    List<PojoGetNewsFeedResponseData> newsFeedList;
    TextView tv_filter;
    TextView tv_no_posts, tv_create_post;

    View.OnClickListener onClickListener;
    PopupMenu.OnMenuItemClickListener onMenuItemClickListener;

    boolean isNewsFeedApiCalled = false;
    int offset = 0;
    String currentFilter = "all";

    String targetUserId;
    private LinearLayoutManager newFeedLayoutManager;
    PojoProfileInfoResponseData pojoProfileInfoResponseData;

    private BroadcastReceiver mRegistrationBroadcastReceiver;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            targetUserId = getArguments().getString("target_user_id");
            String dataStr = getArguments().getString("data");
            Type typeToken = new TypeToken<PojoProfileInfoResponseData>() {
            }.getType();
            pojoProfileInfoResponseData = new Gson().fromJson(dataStr, typeToken);
        }

        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.EDIT_NEWS_FEED_ITEM));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View baseLayout = inflater.inflate(R.layout.fragment_profile_info_posts_list, container, false);
        apiService = ApiClientLongDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();
        if (targetUserId == null || targetUserId.isEmpty()) {
            targetUserId = user_id;
        }
        initView(baseLayout);
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
            }
        };
    }

    private void initView(View baseLayout) {
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitGetUserPollsApi(user_id, targetUserId, "");
            }
        });

        ll_content_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_content_wrapper);

        tv_filter = (TextView) baseLayout.findViewById(R.id.tv_filter);
        tv_no_posts = (TextView) baseLayout.findViewById(R.id.tv_no_posts);
        tv_no_posts.setText(getString(R.string.no_polls_created));
        tv_no_posts.setVisibility(View.GONE);

        tv_create_post = (TextView) baseLayout.findViewById(R.id.tv_create_post);
        tv_create_post.setText(getString(R.string.create_poll));

        tv_create_post.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_poll_gray, 0, 0, 0);

        tv_create_post.setVisibility(View.GONE);
    }


    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.tv_filter:
                        PopupMenu filterMenu = new PopupMenu(getActivity(), tv_filter);
                        MenuInflater interestOptionMenuInflater = filterMenu.getMenuInflater();
                        interestOptionMenuInflater.inflate(R.menu.newsfeed_filter_menu,
                                filterMenu.getMenu());
                        filterMenu.setOnMenuItemClickListener(onMenuItemClickListener);
                        filterMenu.show();
                        break;

                    case R.id.tv_create_post:
                        Intent pollIntent = new Intent(getActivity(), CreatePollActivity.class);
                        //  postIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivityForResult(pollIntent, AppKeys.CREATE_POLL_REQUEST_CODE);
                        break;

                }
            }
        };


       /* onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.all:
                        offset = 0;
                        currentFilter = "all";
                        hitGetNewsFeedApi(user_id, "posts_profile", "all", offset, targetUserId);
                        break;

                    case R.id.post:
                        offset = 0;
                        currentFilter = "";

                        hitGetNewsFeedApi(user_id, "posts_profile", "", offset, targetUserId);
                        break;

                  *//*  case R.id.photos:
                        offset = 0;
                        currentFilter = "photos";

                        hitGetNewsFeedApi(user_id, "posts_profile", "photos", offset, targetUserId);
                        break;*//*

                    case R.id.sessions:
                        offset = 0;
                        currentFilter = "session";

                        hitGetNewsFeedApi(user_id, "posts_profile", "session", offset, targetUserId);
                        break;

                    case R.id.article:
                        offset = 0;
                        currentFilter = "article";

                        hitGetNewsFeedApi(user_id, "posts_profile", "article", offset, targetUserId);
                        break;

                    case R.id.question:
                        offset = 0;
                        currentFilter = "question";

                        hitGetNewsFeedApi(user_id, "posts_profile", "question", offset, targetUserId);
                        break;

                    case R.id.poll:
                        offset = 0;
                        currentFilter = "poll";

                        hitGetNewsFeedApi(user_id, "posts_profile", "poll", offset, targetUserId);
                        break;
                }
                return false;
            }
        };*/
    }


    private void setClickListeners() {
     /*   iv_settings_icon.setOnClickListener(onClickListener);
        iv_message_icon.setOnClickListener(onClickListener);
        iv_notification_icon.setOnClickListener(onClickListener);*/
        //    bt_create_article.setOnClickListener(onClickListener);
        //    bt_create_session.setOnClickListener(onClickListener);
        //  iv_profile_info.setOnClickListener(onClickListener);
        //    tv_create_post.setOnClickListener(onClickListener);
        tv_filter.setOnClickListener(onClickListener);
        tv_create_post.setOnClickListener(onClickListener);
    }

    private void initNewsFeedList(View baseLayout) {
        newsFeedList = new ArrayList<>();
        rv_news_feed_list = (RecyclerView) baseLayout.findViewById(R.id.rv_news_feed_list);
        newsFeedAdapter = new NewsFeedAdapter(this, newsFeedList,
                pojoProfileInfoResponseData.profile.userFullname, true, true);
        newFeedLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_news_feed_list.setLayoutManager(newFeedLayoutManager);
        rv_news_feed_list.setAdapter(newsFeedAdapter);


        rv_news_feed_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //  Log.d("Scroll info","dx = "+dy);
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 5) {
                    if (!isNewsFeedApiCalled
                            && newFeedLayoutManager.findLastVisibleItemPosition() > newsFeedList.size() - 2) {
                        // hitGetNewsFeedApi(user_id, "posts_profile", currentFilter, ++offset, targetUserId);
                        hitGetUserPollsApi(user_id, targetUserId, getLastPostId());

                    }

                }

            }
        });

        offset = 0;
        // hitGetNewsFeedApi(user_id, "posts_profile", currentFilter, offset, targetUserId);
        hitGetUserPollsApi(user_id, targetUserId, getLastPostId());
    }

    private String getLastPostId() {
        if (newsFeedList.isEmpty()) {
            return "";
        } else {
            return newsFeedList.get(newsFeedList.size() - 1).postId;
        }
    }


    private void hitGetUserPollsApi(final String user_id, final String targetUserId, final String lastPostId) {
        Call<PojoGetNewsFeedResponse> call = apiService.hitGetUserPollsApi(user_id, targetUserId, lastPostId);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        if (!isNewsFeedApiCalled) {
            isNewsFeedApiCalled = true;
            newsFeedAdapter.setShowProgressBar(true);
            call.enqueue(new Callback<PojoGetNewsFeedResponse>() {
                @Override
                public void onResponse(Call<PojoGetNewsFeedResponse> call, Response<PojoGetNewsFeedResponse> response) {
                    if (response.body() != null) {
                        String message = response.body().message;
                        if (response.body().data != null && getActivity() != null) {

                            if (lastPostId.equals("")) {
                                newsFeedList.clear();
                            }
                            newsFeedAdapter.setShowProgressBar(false);

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
                                            if (currentItem.origin != null) {
                                                if (currentItem.origin.postId == null
                                                        || currentItem.origin.postId.isEmpty()) {
                                                    continue;
                                                }
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
                                    newsFeedList.add(currentItem);
                                }
                            }
                            if (newsFeedList.isEmpty()) {
                                newsFeedAdapter.setShowProgressBar(false);
                                tv_no_posts.setVisibility(View.VISIBLE);
                                if (targetUserId.equals(user_id)) {
                                    tv_create_post.setVisibility(View.VISIBLE);
                                }

                            } else {
                                tv_no_posts.setVisibility(View.GONE);
                                tv_create_post.setVisibility(View.GONE);
                            }
                            newsFeedAdapter.notifyDataSetChanged();
                        } else {
                            if (getActivity() != null)
                                commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,
                                        AppKeys.TOAST_DEBUG);
                        }
                    }
                    isNewsFeedApiCalled = false;

                }

                @Override
                public void onFailure(Call<PojoGetNewsFeedResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isNewsFeedApiCalled = false;
                    newsFeedAdapter.setShowProgressBar(false);

                    if (newsFeedList.isEmpty()) {
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

   /* private void hitGetNewsFeedApi(String user_id, String get, String filter, final int offset, String targetUserId) {
        Call<PojoGetNewsFeedResponse> call = apiService.getUserActivityFeed(user_id, get, filter,
                String.valueOf(offset), targetUserId);
        if (!isNewsFeedApiCalled) {
            isNewsFeedApiCalled = true;
            call.enqueue(new Callback<PojoGetNewsFeedResponse>() {
                @Override
                public void onResponse(Call<PojoGetNewsFeedResponse> call, Response<PojoGetNewsFeedResponse> response) {
                    if (response.body() != null) {
                        String message = response.body().message;
                        if (response.body().data != null) {
                            if (offset == 0) {
                                newsFeedList.clear();
                            }

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

                                        *//*case "photos":
                                        case "":
                                            if (currentItem.postId == null
                                                    || currentItem.postId.isEmpty()) {
                                                continue;
                                            }
                                            break;*//*

                                        case "post_like":
                                        case "post_comment":
                                        case "post_shared":
                                            if (currentItem.origin != null) {
                                                if (currentItem.origin.postId == null
                                                        || currentItem.origin.postId.isEmpty()) {
                                                    continue;
                                                }
                                            }
                                            break;


                                      *//*  case "poll":
                                            if(currentItem.postId==null||currentItem.postId.isEmpty()){
                                                continue;
                                            }
                                            break;*//*

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

                                    newsFeedList.add(currentItem);
                                }
                            }
                            newsFeedAdapter.notifyDataSetChanged();
                        } else {
                            if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG,
                                    AppKeys.TOAST_DEBUG);
                        }
                    }
                    isNewsFeedApiCalled = false;

                }

                @Override
                public void onFailure(Call<PojoGetNewsFeedResponse> call, Throwable t) {
                    // Log error here since request failed
                    if(getActivity()!=null)commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    isNewsFeedApiCalled = false;

                }
            });
        }

    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppKeys.CREATE_POLL_REQUEST_CODE) {
            hitGetUserPollsApi(user_id, targetUserId, getLastPostId());
            tv_no_posts.setVisibility(View.GONE);
            tv_create_post.setVisibility(View.GONE);
        }
    }

    @Override
    public void newsFeedItemClickCallback(final int position, PojoGetNewsFeedResponseData item, String type, String data, String meta_data) {
        final PojoGetNewsFeedResponseData currentItem = newsFeedList.get(position);
        switch (type) {
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

            case AppKeys.INVITE_SESSION:
                Intent inviteIntent = new Intent(getActivity(), InviteActivity.class);
                inviteIntent.putExtra("userType", "invite_session_user_list");
                inviteIntent.putExtra("id", currentItem.origin.sessionsId);
                startActivity(inviteIntent);
                break;

            case AppKeys.SHARE_TO_SESSION:
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

            case AppKeys.DELETE_ARTICLE: {
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
            break;

            case AppKeys.SHARE_TO_ARTICLE:
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


            case AppKeys.SHARE_TO_INTEREST: /*{
                if(getActivity()!=null) Glide.with(getActivity().getApplicationContext())
                        .asBitmap()
                        .load(currentItem.origin.image)
                        .listener(new RequestListener<Bitmap>() {
                                      @Override
                                      public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                          return false;
                                      }

                                      @Override
                                      public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target,
                                                                     DataSource dataSource, boolean b) {
                                          ShareContentOnMediaApps.shareContent(getActivity(),
                                                  AppKeys.SHARE_INTEREST, currentItem.origin.interestId,
                                                  currentItem.origin.text, bitmap);
                                          return false;
                                      }
                                  }
                        ).submit();
            }*/
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

                /*  if (currentItem.postType.equals("post_like") || currentItem.postType.equals("post_comment")
                 *//*|| (currentItem.origin != null && currentItem.origin.origin != null)*//*) {
                    int likes = Integer.parseInt(newsFeedList.get(position).likes);

                    if (newsFeedList.get(position).iLike == 1) {
                        newsFeedList.get(position).iLike = 0;
                        likes--;
                        commonFunctions.hitPostReactionApi(user_id, currentItem.postId, "unlike_post");
                    } else {
                        newsFeedList.get(position).iLike = 1;
                        likes++;
                        commonFunctions.hitPostReactionApi(user_id, currentItem.postId, "like_post");
                    }
                    newsFeedList.get(position).likes = String.valueOf(likes);
                } else*/
            {
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
            }


            newsFeedAdapter.notifyDataSetChanged();

            break;

            case AppKeys.THANK:
                if (newsFeedList.get(position).origin.iThanks == 0) {
                    hitSessionReactionApi(user_id, currentItem.originId, "session_thanks");
                    newsFeedList.get(position).origin.iThanks = 1;
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
            case AppKeys.POST_DELETE: {
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
            break;

            case AppKeys.UNFOLLOW_USER: {
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
                        if (currentItem.postType.equals("post_like") || currentItem.equals("post_comment")
                                || currentItem.postType.equals("poll_vote")) {
                            //todo uncomment below after newsfeed is fixed
                            //  userToUnfollow = currentItem.liked_by.userId;
                        } else {
                            userToUnfollow = currentItem.authorId;
                        }
                        hitSessionReactionApi(user_id, userToUnfollow, "unfollow");
                        confirmationDialog.dismiss();
                    }
                });
                tv_dialog_description.setText(R.string.unfollow_confirmation_message);
                confirmationDialog.show();
            }
            break;


            case AppKeys.EDIT_QUESTION:
                openReportDialog(position, user_id, "report_question", currentItem.originId);
                break;

            case AppKeys.DELETE_QUESTION: {
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
                openShareDialog("share_answer", currentItem);
                break;

            case AppKeys.EDIT_ANSWER:
                // openReportDialog(position,user_id, "report_answer", currentItem.answer.sessionsQaId);
                break;

            case AppKeys.DELETE_ANSWER: {
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
                tv_dialog_description.setText(R.string.do_you_want_to_delete_this_question);
                confirmationDialog.show();
            }
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

            case AppKeys.SHARE_TO_VENUE: {
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
            break;


            case AppKeys.SESSION_SHARE:
                openShareDialog("share_session", currentItem);
                break;

            case AppKeys.ARTICLE_SHARE:
                openShareDialog("share_article", currentItem);
                break;

            case AppKeys.ORIGIN_ARTICLE_SHARE:
                openShareDialog("share_article", currentItem);
                break;

            case AppKeys.QUESTION_SHARE:
                openShareDialog("share_question", currentItem);
                break;

            case AppKeys.VENUE_SHARE:
                openShareDialog("share_venue", currentItem);
                break;

            case AppKeys.INTEREST_SHARE:
                openShareDialog("share_interest", currentItem);
                break;

            case AppKeys.POST_SHARE:
                openShareDialog("share", currentItem);
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

                        if (newsFeedList.get(newsFeedItemPosition).postType.equals("post_like") || newsFeedList.get(newsFeedItemPosition).postType.equals("post_comment")
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
        newsFeedAdapter.notifyDataSetChanged();
    }


    private void hitUpvoteAnswerApi(String user_id, final String type, String sessionsId, String sessionsQaId, final int position) {
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type, sessionsId, "", "", "", sessionsQaId);
        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call, Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
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
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type,
                session_id, "", "", "", question_id);
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
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                            t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
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
                            commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                    message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                    }
                }

                @Override
                public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                    // Log error here since request failed
                    if (getActivity() != null)
                        commonFunctions.setToastMessage(getActivity().getApplicationContext(),
                                t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            });


        }
    }

    public void openShareDialog(final String type, final PojoGetNewsFeedResponseData currentItem) {
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
                hitShareContentApi(user_id, share_id, type, et_share_title.getText().toString().trim());
                shareDialog.dismiss();
            }
        });

        shareDialog.show();

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
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }

    @Override
    public void shareNewsFeedItemClickCallback(int position, PojoGetNewsFeedResponseData currentItem, String type) {

    }

    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);

    }
}
