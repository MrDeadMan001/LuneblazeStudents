package com.avadna.luneblaze.activities.article;

import android.annotation.SuppressLint;
import android.app.Dialog;

import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.article.ArticleReplyAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoAddArticleReplyResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoArticleReply;
import com.avadna.luneblaze.pojo.pojoArticle.PojoGetArticleRepliesResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoGetArticleRepliesResponseData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleCommentReplyListActivity extends AppBaseActivity
        implements ArticleReplyAdapter.ArticleReplyAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {

    private PreferenceUtils preferenceUtils;
    private ActionBar actionBar;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String userName;
    private PojoLoginResponseData pojoLoginResponseData;
    private String parent_comment_id;
    private String article_id;
    private String target_reply_id;
    private String top_reply_id;
    private String action;
    private ApiInterface apiService;
    private RelativeLayout rl_content_wrapper;

    private RecyclerView rv_reply_list;
    private LinearLayoutManager linearLayoutManager;
    private List<PojoArticleReply> replyList;
    private ArticleReplyAdapter articleReplyAdapter;
    private boolean listBottomEndReached = false;
    private boolean listTopEndReached = false;
    private boolean isGetCommentApiCalled = false;
    private RelativeLayout rl_bottom_bar;
    private EditText et_comment;
    private ImageButton ib_send;
    private ProgressBar pb_loading_comments;

    private View.OnClickListener onClickListener;

    private PojoGetArticleRepliesResponseData pojoGetArticleRepliesResponseData;
    private String notification_id;

    private RecyclerView rv_upvoters;
    private LinearLayoutManager upvoterListLayoutManager;
    private UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    private List<PojoUserData> upvoterList;
    private boolean isGetUpvotersApiCalled = false;
    private int upvoterListOffset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_comment_reply_list);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        userName = preferenceUtils.get_user_name();
        user_id = preferenceUtils.get_user_id();
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        parent_comment_id = getIntent().getStringExtra(AppKeys.COMMENT_ID);
        article_id = getIntent().getStringExtra(AppKeys.ARTICLE_ID);
        target_reply_id = getIntent().getStringExtra(AppKeys.REPLY_ID);
        top_reply_id = target_reply_id;
        action = getIntent().getStringExtra(AppKeys.TARGET);
        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }
        setUpActionBar();
        initViews();
        initreplyList();
        initClickListener();
        setClickListeners();
        if (target_reply_id == null || target_reply_id.isEmpty()) {
            top_reply_id = "0";
            hitGetArticleCommentRepliesApi(user_id, parent_comment_id, "prev", "0",
                    "0", true);
        } else {
            hitGetArticleCommentRepliesApi(user_id, parent_comment_id, "prev", target_reply_id,
                    "1", true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.replies));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(ArticleCommentReplyListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(ArticleCommentReplyListActivity.this, R.color.status_bar_color));
            }
        }

    }

    private void initViews() {
        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        rl_content_wrapper.setVisibility(View.GONE);
        rl_bottom_bar = (RelativeLayout) findViewById(R.id.rl_bottom_bar);
        rl_bottom_bar.setVisibility(View.GONE);
        et_comment = (EditText) findViewById(R.id.et_comment);
        et_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppKeys.COMMENT_AND_REPLY_MAX_LENGTH)});
        ib_send = (ImageButton) findViewById(R.id.ib_send);

        pb_loading_comments = (ProgressBar) findViewById(R.id.pb_loading_comments);
        pb_loading_comments.setVisibility(View.VISIBLE);

    }


    private void initreplyList() {
        rv_reply_list = (RecyclerView) findViewById(R.id.rv_reply_list);
        linearLayoutManager = new LinearLayoutManager(ArticleCommentReplyListActivity.this,
                RecyclerView.VERTICAL, false);
        rv_reply_list.setLayoutManager(linearLayoutManager);
        replyList = new ArrayList<>();
        articleReplyAdapter = new ArticleReplyAdapter(ArticleCommentReplyListActivity.this, replyList);
        rv_reply_list.setAdapter(articleReplyAdapter);
        rv_reply_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!listBottomEndReached && dy > 4) {
                    if (!isGetCommentApiCalled && linearLayoutManager.findLastVisibleItemPosition() > replyList.size() - 3) {
                        hitGetArticleCommentRepliesApi(user_id, parent_comment_id, "prev",
                                getLatestReplyId(), "0", false);
                    }
                } /*else if (dy < -4) {
                    if (!isGetCommentApiCalled && replyListLayoutManager.findFirstVisibleItemPosition() < 2) {
                        hitGetPostCommentRepliesApi(user_id, parent_comment_id, getOldestReplyId(),
                                "prev", false);
                    }
                }*/
            }
        });

    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ib_send:
                        if (!et_comment.getText().toString().trim().isEmpty()) {

                            hitAddReplyOnArticleComment(user_id, article_id, parent_comment_id,
                                    et_comment.getText().toString().trim());

                            PojoArticleReply tempReply = new PojoArticleReply();
                            /*Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                            tempReply.modifiedOn = mdformat.format(calendar.getTime());
                            tempReply.addedOn = mdformat.format(calendar.getTime());*/
                            tempReply.modifiedOn = getString(R.string.just_now);
                            tempReply.addedOn = getString(R.string.just_now);
                            tempReply.iLike = 0;
                            PojoUserData userData = new PojoUserData();
                            tempReply.user = userData;
                            tempReply.user.userFullname = userName;
                            tempReply.user.userId = user_id;
                            tempReply.user.userPicture = pojoLoginResponseData.userPicture;
                            tempReply.post = et_comment.getText().toString().trim();
                            tempReply.articlesDiscussionId = "";
                            tempReply.totalLikes = "0";


                            replyList.add(0, tempReply);
                            pojoGetArticleRepliesResponseData.totalReplies++;
                            articleReplyAdapter.notifyDataSetChanged();
                            rv_reply_list.scrollToPosition(0);
                            et_comment.getText().clear();
                            InputMethodManager imm = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);
                            setDataOnViews();

                        }
                        break;

                }
            }
        };
    }

    @Override
    public void onCommentActionPerformed(int position, String type) {
        switch (type) {
            case AppKeys.ADD_REPLY:
                openAddCommentBar();
                break;

            case AppKeys.LIKE_COUNT:
                openUserDialog(pojoGetArticleRepliesResponseData.articlesDiscussionId, "articlecommentreply");
                break;

            case AppKeys.LIKE_COMMENT:
                if (pojoGetArticleRepliesResponseData != null) {
                    int likeCount = Integer.parseInt(pojoGetArticleRepliesResponseData.totalLikes);
                    if (pojoGetArticleRepliesResponseData.iLike == 1) {
                        pojoGetArticleRepliesResponseData.iLike = 0;
                        likeCount--;
                        hitDiscussionLikeApi(user_id, pojoGetArticleRepliesResponseData.articlesDiscussionId,
                                "unlike_article_discussion");

                    } else {
                        pojoGetArticleRepliesResponseData.iLike = 1;
                        likeCount++;
                        hitDiscussionLikeApi(user_id, pojoGetArticleRepliesResponseData.articlesDiscussionId,
                                "like_article_discussion");
                    }
                    pojoGetArticleRepliesResponseData.totalLikes = String.valueOf(likeCount);
                    articleReplyAdapter.notifyDataSetChanged();
                }
                break;
        }
    }


    private void setClickListeners() {
        ib_send.setOnClickListener(onClickListener);
    }

    private void hitGetArticleCommentRepliesApi(String user_id, String parent_comment_id, final String order,
                                                final String reply_id, String target, final boolean scrollToBottom) {
        Call<PojoGetArticleRepliesResponse> call = apiService.getArticleCommentsReplies(user_id, parent_comment_id,
                order, reply_id, target);
        if (!isGetCommentApiCalled) {
            articleReplyAdapter.setShowProgressBar(true);
            articleReplyAdapter.notifyDataSetChanged();
            isGetCommentApiCalled = true;
            call.enqueue(new Callback<PojoGetArticleRepliesResponse>() {
                @Override
                public void onResponse(Call<PojoGetArticleRepliesResponse> call, Response<PojoGetArticleRepliesResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null && response.body().data != null) {
                        message = response.body().message;
                        pojoGetArticleRepliesResponseData = response.body().data;
                        articleReplyAdapter.setCommentItem(pojoGetArticleRepliesResponseData);
                        if (response.body().status == 1 && response.body().data.replies != null) {
                            if (target_reply_id != null) {
                                boolean present = false;
                                for (int i = 0; i < response.body().data.replies.size(); i++) {
                                    if (target_reply_id.equals(response.body().data.replies.get(i).articlesDiscussionId)) {
                                        present = true;
                                        target_reply_id = null;
                                        break;
                                    }
                                }
                                if (!present) {
                                    target_reply_id = null;
                                    showMessageDialog(getString(R.string.the_reply_has_been_deleted));
                                }
                            }

                            if (reply_id.equals("0")) {
                                replyList.clear();
                            }
                            if (order.equals("next")) {
                                if (response.body().data.replies.size() < 15) {
                                    articleReplyAdapter.setMorePreviousAvailable(false);
                                    articleReplyAdapter.setShowTopMoreButton(false);
                                    listTopEndReached = true;
                                }

                                if (!replyList.isEmpty()) {
                                    replyList.remove(0);
                                }
                                Collections.reverse(response.body().data.replies);

                                replyList.addAll(0, removeReportedReplies(response.body().data.replies));
                            } else if (order.equals("prev")) {
                                // Collections.reverse(response.body().data.replies);


                                if (response.body().data.morePresent == 0) {
                                    articleReplyAdapter.setMorePreviousAvailable(false);
                                    articleReplyAdapter.setShowTopMoreButton(false);
                                }

                                if (response.body().data.loadMore == 0) {
                                    articleReplyAdapter.setMoreNextAvailable(false);
                                    articleReplyAdapter.setShowBottomMoreButton(false);
                                }

                                if (reply_id.equals("0")) {
                                    if (response.body().data.replies.size() < 15) {
                                        articleReplyAdapter.setMorePreviousAvailable(false);
                                        articleReplyAdapter.setShowTopMoreButton(false);
                                        articleReplyAdapter.setShowBottomMoreButton(false);
                                        listTopEndReached = true;
                                    } else {
                                        articleReplyAdapter.setMorePreviousAvailable(false);
                                        articleReplyAdapter.setShowTopMoreButton(false);
                                    }
                                } else {
                                    if (response.body().data.replies.size() < 15) {
                                        articleReplyAdapter.setMoreNextAvailable(false);
                                        articleReplyAdapter.setShowBottomMoreButton(false);
                                        listBottomEndReached = true;
                                    }

                                    if (!replyList.isEmpty() && !response.body().data.replies.isEmpty()) {
                                        response.body().data.replies.remove(0);
                                    }

                                    if (target != null && target.equals("0")) {
                                        for (int i = 0; i < response.body().data.replies.size(); i++) {
                                            if (response.body().data.replies.get(i).articlesDiscussionId.equals(top_reply_id)) {
                                                response.body().data.replies.remove(i);
                                                break;
                                            }
                                        }
                                    }


                                }
                            /*    pb_loading_previous.setVisibility(View.INVISIBLE);
                                tv_load_previous.setVisibility(View.VISIBLE);*/

                                replyList.addAll(removeReportedReplies(response.body().data.replies));
                            }
                            articleReplyAdapter.setCreator_id(pojoGetArticleRepliesResponseData.userId);
                            articleReplyAdapter.setShowProgressBar(false);

                            articleReplyAdapter.notifyDataSetChanged();
                            if (scrollToBottom) {

                                if (/*target_reply_id.equals("0") && */order.equals("prev")) {
                                    rv_reply_list.scrollToPosition(0);

                                } else {
                                    rv_reply_list.scrollToPosition(0);

                                }
                            }
                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                        setDataOnViews();
                    }
                    pb_loading_comments.setVisibility(View.GONE);
                    rl_content_wrapper.setVisibility(View.VISIBLE);
                    isGetCommentApiCalled = false;

                    if (action != null && action.equals(AppKeys.ADD_REPLY)) {
                        openAddCommentBar();
                        action = null;
                    }
                }

                @Override
                public void onFailure(Call<PojoGetArticleRepliesResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // rl_parent.setVisibility(View.VISIBLE);
                    if (replyList.isEmpty()) {
                        openNoConnectionDialog(getString(R.string.replies), reply_id);
                    }
                    isGetCommentApiCalled = false;

                }
            });
        }
    }


    private void openNoConnectionDialog(String title, String reply_id) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetArticleCommentRepliesApi(user_id, parent_comment_id, "prev", reply_id,
                            "1", true);
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


    public void showMessageDialog(String message) {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(ArticleCommentReplyListActivity.this, R.style.NoTitleDialogTheme);
        confirmationDialog.setContentView(R.layout.message_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        bt_ok = (Button) confirmationDialog.findViewById(R.id.bt_ok);

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });
        tv_dialog_description.setText(message);
        confirmationDialog.show();
    }

    private List<PojoArticleReply> removeReportedReplies(List<PojoArticleReply> replies) {
        List<PojoArticleReply> moddedReplies = new ArrayList<>();
        for (int i = 0; i < replies.size(); i++) {
            if (replies.get(i).iReported == null || replies.get(i).iReported == 0) {
                moddedReplies.add(replies.get(i));
            }
        }
        return moddedReplies;
    }


    private void setDataOnViews() {
        invalidateOptionsMenu();
        if (pojoGetArticleRepliesResponseData != null) {
            if (action != null && action.equals(AppKeys.ADD_REPLY)) {
                openAddCommentBar();
                action = null;
            }
        }
    }

    private void openAddCommentBar() {
        rl_bottom_bar.setVisibility(View.VISIBLE);
        if (et_comment.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_comment, InputMethodManager.SHOW_FORCED);
        }
    }


    private void hitDiscussionLikeApi(String user_id, String replyId, String reaction) {
        Call<PojoNoDataResponse> call = apiService.likeUnlikeSessionDiscussion(user_id, replyId, reaction);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    @Override
    public void onReplyActionPerformed(final int replyPosition, String actionType) {
        switch (actionType) {
            case AppKeys.EDIT:
                openEditReplyDialog(replyPosition);
                break;


            case AppKeys.LIKE_COUNT:
                openUserDialog(replyList.get(replyPosition).replyId, "articlecommentreply");
                break;


            case AppKeys.DELETE:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(ArticleCommentReplyListActivity.this, R.style.NoTitleDialogTheme);
                confirmationDialog.setContentView(R.layout.yes_no_dialog);
                tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
                tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);

                tv_dialog_description.setText(getString(R.string.delete_confirm));
                tv_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmationDialog.dismiss();
                    }
                });

                tv_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hitDeleteArticleCommentApi(user_id, replyList.get(replyPosition).articlesDiscussionId);
                        replyList.remove(replyPosition);
                        articleReplyAdapter.notifyDataSetChanged();
                        confirmationDialog.dismiss();

                    }
                });
                confirmationDialog.show();

                break;

            case AppKeys.REPORT_REPLY:

                hitReportContentApi(user_id, "report_article_reply",
                        replyList.get(replyPosition).articlesDiscussionId, replyList.get(replyPosition).post);
                replyList.remove(replyPosition);
                articleReplyAdapter.notifyDataSetChanged();
                break;

            case AppKeys.LIKE_REPLY:
                int likes = Integer.parseInt(replyList.get(replyPosition).totalLikes);

                if (replyList.get(replyPosition).iLike == 1) {
                    replyList.get(replyPosition).iLike = 0;
                    likes--;
                    hitDiscussionLikeApi(user_id, replyList.get(replyPosition).articlesDiscussionId,
                            "unlike_article_discussion");

                } else {
                    replyList.get(replyPosition).iLike = 1;
                    hitDiscussionLikeApi(user_id, replyList.get(replyPosition).articlesDiscussionId,
                            "like_article_discussion");
                    likes++;
                }
                replyList.get(replyPosition).totalLikes = "" + likes;
                articleReplyAdapter.notifyDataSetChanged();
                break;


            case AppKeys.LOAD_NEXT:
                if (!listBottomEndReached) {
                    hitGetArticleCommentRepliesApi(user_id, parent_comment_id, "prev", getLatestReplyId(),
                            "0", false);
                }
                break;

            case AppKeys.LOAD_PREVIOUS:
                if (!listTopEndReached) {
                    hitGetArticleCommentRepliesApi(user_id, parent_comment_id, "next", getOldestReplyId(),
                            "0", false);

                }
        }
    }


    private String getLatestReplyId() {
        if (!replyList.isEmpty()) {
            return replyList.get(replyList.size() - 1).articlesDiscussionId;
        } else {
            return "0";
        }
    }

    private String getOldestReplyId() {
        if (!replyList.isEmpty()) {
            return replyList.get(0).articlesDiscussionId;
        } else {
            return "0";
        }
    }

    public void hitReportContentApi(String user_id, String report_type, String content_id, String reason) {
        Call<PojoNoDataResponse> call = apiService.reportContent(user_id, report_type,
                content_id, reason);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (ArticleCommentReplyListActivity.this != null) {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void hitAddReplyOnArticleComment(final String user_id, String article_id,
                                             final String comment_id, final String message) {
        Call<PojoAddArticleReplyResponse> call = apiService.addReplyOnArticleComment(user_id, article_id,
                comment_id, message);

        call.enqueue(new Callback<PojoAddArticleReplyResponse>() {
            @Override
            public void onResponse(Call<PojoAddArticleReplyResponse> call, Response<PojoAddArticleReplyResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(),
                            "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    if (response.body().data.commentId != null && response.body().data.time != null) {
                        for (int i = replyList.size() - 1; i >= 0; i--) {
                            if (replyList.get(i).articlesDiscussionId.isEmpty()) {
                                replyList.get(i).modifiedOn = response.body().data.time;
                                replyList.get(i).articlesDiscussionId = String.valueOf(response.body().data.commentId);
                                articleReplyAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoAddArticleReplyResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(),
                        getString(R.string.comment_not_added), Toast.LENGTH_LONG,
                        AppKeys.TOAST_USER);
                et_comment.setText(message);
                if (replyList != null) {
                    for (int i = replyList.size() - 1; i >= 0; i--) {
                        if (replyList.get(i).articlesDiscussionId.isEmpty()) {
                            replyList.remove(i);
                        }
                    }
                }
                articleReplyAdapter.notifyDataSetChanged();

            }
        });
    }

    private void openEditReplyDialog(final int replyPosition) {
        final Dialog dialog = new MyCustomThemeDialog(ArticleCommentReplyListActivity.this, R.style.NoTitleDialogTheme);
        dialog.setContentView(R.layout.edit_content_dialog);

        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        et_content.setText(Html.fromHtml(replyList.get(replyPosition).post));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newData = et_content.getText().toString().trim();
                if (newData.isEmpty()) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.please_enter_data), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    if (replyList.get(replyPosition).articlesDiscussionId != null) {
                        hitEditArticleCommentApi(user_id, replyList
                                .get(replyPosition).articlesDiscussionId, newData);
                        replyList.get(replyPosition).post = newData;
                        //tv_question.setText(response.body().data.post);
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                        articleReplyAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void hitDeleteArticleCommentApi(String user_id, String comment_id) {
        Call<PojoNoDataResponse> call = apiService.deleteArticleComment(user_id, comment_id);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitEditArticleCommentApi(String user_id, String comment_id, final String newData) {
        Call<PojoNoDataResponse> call = apiService.editArticleComment(user_id, comment_id, newData);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pojoGetArticleRepliesResponseData != null) {
            if (user_id.equals(pojoGetArticleRepliesResponseData.userId)) {
                getMenuInflater().inflate(R.menu.comment_popup_poster_menu, menu);
            } else {
                getMenuInflater().inflate(R.menu.comment_popup_other_menu, menu);
            }
        }

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

            case R.id.edit:
                openEditCommentDialog();
                break;

            case R.id.delete:

                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(ArticleCommentReplyListActivity.this, R.style.NoTitleDialogTheme);
                confirmationDialog.setContentView(R.layout.yes_no_dialog);
                tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
                tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
                tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);

                tv_dialog_description.setText(getString(R.string.delete_confirm));
                tv_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmationDialog.dismiss();
                    }
                });

                tv_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        hitDeleteArticleCommentApi(user_id, pojoGetArticleRepliesResponseData.articlesDiscussionId);
                        Intent pushNotification = new Intent(Config.ARTICLE_COMMENT_DELETED);
                        pushNotification.putExtra(AppKeys.COMMENT_ID, pojoGetArticleRepliesResponseData.articlesDiscussionId);
                        LocalBroadcastManager.getInstance(ArticleCommentReplyListActivity.this).sendBroadcast(pushNotification);
                        finish();

                    }
                });
                confirmationDialog.show();

                break;

            case R.id.share_to:
                // shareTextUrl(answerList.get(position).sessionsQaId);
                break;

            case R.id.report:
                commonFunctions.openReportDialog(user_id, "report_article_comment",
                        pojoGetArticleRepliesResponseData.articlesDiscussionId);
                break;
        }
        return true;
    }

    private void openEditCommentDialog() {
        final Dialog dialog = new MyCustomThemeDialog(ArticleCommentReplyListActivity.this, R.style.NoTitleDialogTheme);
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        et_content.setText(pojoGetArticleRepliesResponseData.post);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newData = et_content.getText().toString().trim();
                if (newData.isEmpty()) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.please_enter_data), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                } else {
                    hitEditArticleCommentApi(user_id, pojoGetArticleRepliesResponseData.articlesDiscussionId,
                            newData);
                    pojoGetArticleRepliesResponseData.post = newData;
                    setDataOnViews();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    //tv_question.setText(response.body().data.post);
                    dialog.dismiss();
                    articleReplyAdapter.notifyDataSetChanged();
                }
            }
        });
        dialog.show();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    private void openUserDialog(String id, String reaction) {
        TextView tv_title;
        ImageView iv_back;

        final Dialog userListDialog = new MyCustomThemeDialog(this);
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ProgressBar pb_loading_users = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);
        pb_loading_users.setVisibility(View.GONE);
        tv_title = userListDialog.findViewById(R.id.tv_title);
        tv_title.setText(this.getString(R.string.likes));
        iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListDialog.dismiss();
            }
        });
        userListDialog.setTitle(this.getString(R.string.upvotes));

        rv_upvoters = (RecyclerView) userListDialog.findViewById(R.id.rv_list);
        //rv_list.setPadding(16,32,16,32);
        upvoterListLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        upvoterList = new ArrayList<>();
        rv_upvoters.setLayoutManager(upvoterListLayoutManager);
        upvotersAndAttendeesListAdapter = new UpvotersAndAttendeesListAdapter(this, upvoterList);

        rv_upvoters.setAdapter(upvotersAndAttendeesListAdapter);
        rv_upvoters.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 2) {
                    if (!isGetUpvotersApiCalled && upvoterListLayoutManager.findLastVisibleItemPosition() > upvoterList.size() - 2) {
                        hitGetUpvotersListApi(user_id, id, reaction, ++upvoterListOffset);
                    }
                }
            }
        });

        upvoterListOffset = 0;
        hitGetUpvotersListApi(user_id, id, reaction, upvoterListOffset);
        userListDialog.show();

    }


    private void hitGetUpvotersListApi(String user_id, final String id, String reaction, int offset) {
        Call<PojoUpvotersAndAttendeesListResponse> call = apiService.getUpvotersAndAttendees(user_id,
                id, reaction, String.valueOf(offset));
        if (!isGetUpvotersApiCalled) {
            upvotersAndAttendeesListAdapter.setShowProgressBar(true);
            isGetUpvotersApiCalled = true;
            call.enqueue(new Callback<PojoUpvotersAndAttendeesListResponse>() {
                @Override
                public void onResponse(Call<PojoUpvotersAndAttendeesListResponse> call,
                                       Response<PojoUpvotersAndAttendeesListResponse> response) {
                    if (response != null && response.body() != null && response.body().data != null) {
                        if (offset == 0) {
                            upvoterList.clear();
                        }
                        upvoterList.addAll(response.body().data);
                        upvotersAndAttendeesListAdapter.notifyDataSetChanged();
                    }
                    isGetUpvotersApiCalled = false;
                    upvotersAndAttendeesListAdapter.setShowProgressBar(false);
                }

                @Override
                public void onFailure(Call<PojoUpvotersAndAttendeesListResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    upvotersAndAttendeesListAdapter.setShowProgressBar(false);
                    isGetUpvotersApiCalled = false;

                }
            });
        }
    }


    @Override
    public void onUpvoterAndAttendeesAdapterMethod(int position) {
        if (replyList.get(position).likedUsers.get(position).connection.equals("add")
                || replyList.get(position).likedUsers.get(position).connection.equals("remove")) {
            hitUpdateRequestStatusApi(user_id, replyList.get(position).likedUsers.get(position).userId,
                    "follow", position);
            replyList.get(position).likedUsers.get(position).connection = "followed";

        } else if (replyList.get(position).likedUsers.get(position).connection.equals("followed")) {
            hitUpdateRequestStatusApi(user_id, replyList.get(position).likedUsers.get(position).userId,
                    "unfollow", position);
            replyList.get(position).likedUsers.get(position).connection = "add";
        }
        upvotersAndAttendeesListAdapter.notifyDataSetChanged();
    }


    private void hitUpdateRequestStatusApi(String user_id, String id, final String connect_type, final int position) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // venueList.remove(position);
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }
}
