package com.avadna.luneblaze.activities.article;

import android.app.Dialog;

import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.InputFilter;
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
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.adapters.article.ArticleCommentsAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoAddArticleCommentResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoArticleCommentsResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoArticleCommentsResponseData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ArticleCommentListActivity extends AppBaseActivity implements
        ArticleCommentsAdapter.ArticleCommentAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback{
    private static final int API_COMMMENT_COUNT = 15;
    ActionBar actionBar;
    String user_id = "";
    String username = "";
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    PojoLoginResponseData pojoLoginResponseData;

    String articleId;
    String target_comment_id;
    String top_comment_id;
    String creator_id;
    String action;
    int comment_count;

    ProgressBar pb_loading_comments;
    RelativeLayout rl_content_wrapper;
    EditText et_comment;
    ImageButton ib_send;
    RelativeLayout rl_bottom_bar;


    RecyclerView rv_upvoters;
    LinearLayoutManager upvoterListLayoutManager;
    UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    List<PojoUserData> upvoterList;
    boolean isGetUpvotersApiCalled = false;
    int upvoterListOffset = 0;

    RecyclerView rv_comments_list;
    List<PojoArticleCommentsResponseData> commentList;
    boolean isGetCommentApiCalled = false;
    ArticleCommentsAdapter articleCommentsAdapter;
    LinearLayoutManager commentListLayoutManager;
    boolean listBottomEndReached = false;
    boolean listTopEndReached = false;

    View.OnClickListener onClickListener;

    private BroadcastReceiver broadcastReceiver;
    String notification_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_comment_list);
        preferenceUtils = new PreferenceUtils(ArticleCommentListActivity.this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        username = preferenceUtils.get_user_name();
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

        articleId = getIntent().getStringExtra("id");
        if (articleId == null) {
            articleId = "30";
        }

        creator_id = getIntent().getStringExtra(AppKeys.CREATOR_ID);
        if (creator_id == null || creator_id.isEmpty()) {
            creator_id = "0";
        }

        comment_count = getIntent().getIntExtra(AppKeys.COMMENT_COUNT, 0);

        action = getIntent().getStringExtra(AppKeys.TARGET);

        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }

        setUpActionBar();
        initViews();
        initCommentList();
        initClickListener();
        setClickListeners();
        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.ARTICLE_COMMENT_DELETED));
        target_comment_id = getIntent().getStringExtra(AppKeys.COMMENT_ID);
        top_comment_id = target_comment_id;
        if (target_comment_id == null || target_comment_id.isEmpty()) {
            top_comment_id = "0";
            hitGetArticleCommentsApi(user_id, articleId, "prev", "0", "0", false);
        } else {
            hitGetArticleCommentsApi(user_id, articleId, "prev", target_comment_id, "1", false);
        }
    }


    private void initBroadCastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.ARTICLE_COMMENT_DELETED)) {
                    String comment_id = intent.getStringExtra(AppKeys.COMMENT_ID);
                    if (commentList != null) {
                        for (int i = 0; i < commentList.size(); i++) {
                            if (commentList.get(i).articlesDiscussionId.equals(comment_id)) {
                                commentList.remove(i);
                                articleCommentsAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        };
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            //   actionBar.setTitle(getString(R.string.comments) + " (" + comment_count + ")");
            actionBar.setTitle(getString(R.string.comments));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(ArticleCommentListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(ArticleCommentListActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void initViews() {
        pb_loading_comments = (ProgressBar) findViewById(R.id.pb_loading_comments);
        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        rl_content_wrapper.setVisibility(View.GONE);
        et_comment = (EditText) findViewById(R.id.et_comment);
        et_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppKeys.COMMENT_AND_REPLY_MAX_LENGTH)});
        ib_send = (ImageButton) findViewById(R.id.ib_send);
        rl_bottom_bar = (RelativeLayout) findViewById(R.id.rl_bottom_bar);
    }

    private void initCommentList() {
        commentList = new ArrayList<>();
        rv_comments_list = (RecyclerView) findViewById(R.id.rv_comments_list);
        commentListLayoutManager = new LinearLayoutManager(ArticleCommentListActivity.this,
                RecyclerView.VERTICAL, false);
        rv_comments_list.setLayoutManager(commentListLayoutManager);
        articleCommentsAdapter = new ArticleCommentsAdapter(ArticleCommentListActivity.this,
                commentList);
        rv_comments_list.setAdapter(articleCommentsAdapter);

        rv_comments_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 4) {
                    if (!listBottomEndReached && !isGetCommentApiCalled
                            && commentListLayoutManager.findLastVisibleItemPosition() > commentList.size() - 3) {
                        hitGetArticleCommentsApi(user_id, articleId, "prev",
                                getLatestCommentId(), "0", false);
                    }
                }
            }
        });
        listBottomEndReached = false;
        listTopEndReached = false;

       /* rv_comments_list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rv_comments_list.getWindowVisibleDisplayFrame(r);
                int screenHeight = rv_comments_list.getRootView().getHeight();
                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    if (!commentList.isEmpty()) {
                        rv_comments_list.smoothScrollToPosition(commentList.size());
                    }

                } else {
                    // keyboard is closed
                }
            }
        });*/
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ib_send:
                        if (!et_comment.getText().toString().trim().isEmpty()) {
                            PojoArticleCommentsResponseData tempComment =
                                    new PojoArticleCommentsResponseData(et_comment.getText().toString(),
                                            user_id, getString(R.string.just_now));
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            tempComment.addedOn = mdformat.format(calendar.getTime());
                            tempComment.articlesDiscussionId = "";
                            tempComment.user = new PojoUserData();
                            tempComment.user.userId = user_id;
                            tempComment.user.userFullname = username;
                            tempComment.user.userPicture = pojoLoginResponseData.userPicture;
                            tempComment.modifiedOn = getString(R.string.just_now);
                            tempComment.totalReplies = 0;
                            tempComment.totalLikes = 0;

                            commentList.add(0, tempComment);
                            articleCommentsAdapter.notifyDataSetChanged();
                            hitAddCommentApi(user_id, articleId, et_comment.getText().toString(), "dialog");
                            et_comment.getText().clear();
                            InputMethodManager imm = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);
                            rv_comments_list.smoothScrollToPosition(0);

                        }
                        break;
                }
            }
        };
    }

    private void setClickListeners() {
        ib_send.setOnClickListener(onClickListener);
    }

    private void hitGetArticleCommentsApi(String user_id, String article_id, final String order,
                                          final String comment_id, String target, final boolean scrollToBottom) {
        Call<PojoArticleCommentsResponse> call = apiService.getArticleComments(user_id, article_id,
                order, comment_id, target);
        if (!isGetCommentApiCalled) {
            articleCommentsAdapter.setShowProgressBar(true);
            articleCommentsAdapter.notifyDataSetChanged();
            isGetCommentApiCalled = true;
            call.enqueue(new Callback<PojoArticleCommentsResponse>() {
                @Override
                public void onResponse(Call<PojoArticleCommentsResponse> call, Response<PojoArticleCommentsResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null && response.body().data != null) {
                        message = response.body().message;

                        if (target_comment_id != null) {
                            boolean present = false;
                            for (int i = 0; i < response.body().data.size(); i++) {
                                if (target_comment_id.equals(response.body().data.get(i).articlesDiscussionId)) {
                                    present = true;
                                    target_comment_id = null;
                                    break;
                                }
                            }

                            if (!present) {
                                target_comment_id = null;
                                showMessageDialog(getString(R.string.the_comment_has_been_deleted));
                            }
                        }

                        if (comment_id.equals("0")) {
                            commentList.clear();
                        }
                        if (order.equals("next")) {
                            if (response.body().data.size() < API_COMMMENT_COUNT) {
                                articleCommentsAdapter.setMoreNextAvailable(false);
                                listBottomEndReached = true;
                            }

                           /* else if (!commentList.isEmpty()) {
                                commentList.remove(commentList.size() - 1);
                            }*/

                            if (!comment_id.equals("0") && !commentList.isEmpty()) {
                                response.body().data.remove(0);
                            }
                            commentList.addAll(removeReportedComments(response.body().data));
                        } else if (order.equals("prev")) {
                            articleCommentsAdapter.setMorePreviousAvailable(false);
                            articleCommentsAdapter.setShowTopMoreButton(false);
                            if (response.body().morePresent == 0) {
                                articleCommentsAdapter.setMoreNextAvailable(false);
                                articleCommentsAdapter.setShowBottomMoreButton(false);
                            }
                            if (response.body().loadMore != null && response.body().loadMore == 0) {
                                articleCommentsAdapter.setMorePreviousAvailable(false);
                                articleCommentsAdapter.setShowTopMoreButton(false);
                            }
                            if (comment_id.equals("0")) {
                                if (response.body().data.size() < API_COMMMENT_COUNT) {
                                    articleCommentsAdapter.setMorePreviousAvailable(false);
                                    articleCommentsAdapter.setShowTopMoreButton(false);
                                    articleCommentsAdapter.setShowBottomMoreButton(false);
                                    listTopEndReached = true;
                                } else {
                                    articleCommentsAdapter.setMoreNextAvailable(false);
                                    articleCommentsAdapter.setShowBottomMoreButton(false);
                                }
                            } else {
                                if (response.body().data.size() < API_COMMMENT_COUNT) {
                                    articleCommentsAdapter.setMorePreviousAvailable(false);
                                    articleCommentsAdapter.setShowTopMoreButton(false);
                                    listTopEndReached = true;
                                }
                                if (!response.body().data.isEmpty() && !commentList.isEmpty()) {
                                    response.body().data.remove(0);
                                }

                                if (target != null && target.equals("0")) {
                                    for (int i = 0; i < response.body().data.size(); i++) {
                                        if (response.body().data.get(i).articlesDiscussionId.equals(top_comment_id)) {
                                            response.body().data.remove(i);
                                            break;
                                        }
                                    }
                                }


                            }
                            /*    pb_loading_previous.setVisibility(View.INVISIBLE);
                                tv_load_previous.setVisibility(View.VISIBLE);*/

                         /*   if (!commentList.isEmpty()) {
                                commentList.remove(0);
                            }*/

                            commentList.addAll(removeReportedComments(response.body().data));
                        }

                        articleCommentsAdapter.setCreator_id(creator_id);
                        articleCommentsAdapter.setShowProgressBar(false);
                        if (action != null && action.equals(AppKeys.COMMENT)) {
                            action = null;
                            if (et_comment.requestFocus()) {
                                InputMethodManager imm = (InputMethodManager)
                                        getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(et_comment, InputMethodManager.SHOW_FORCED);
                            }
                        }

                        articleCommentsAdapter.notifyDataSetChanged();
                        if (scrollToBottom) {
                            if (/*target_reply_id.equals("0") && */order.equals("prev")) {
                                rv_comments_list.scrollToPosition(commentList.size());

                            } else {
                                rv_comments_list.scrollToPosition(commentList.size() + 1);

                            }
                        }

                    } else {
                        commonFunctions.setToastMessage(ArticleCommentListActivity.this,
                                "Null body Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    isGetCommentApiCalled = false;
                    pb_loading_comments.setVisibility(View.GONE);
                    rl_content_wrapper.setVisibility(View.VISIBLE);
                }


                @Override
                public void onFailure(Call<PojoArticleCommentsResponse> call, Throwable t) {
                    if (commentList.isEmpty()) {
                        openNoConnectionDialog(getString(R.string.comments), comment_id);
                    }
                    // Log error here since request failed
                    commonFunctions.setToastMessage(ArticleCommentListActivity.this, t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isGetCommentApiCalled = false;
                }
            });
        }
    }

    private void openNoConnectionDialog(String title, String comment_id) {
        try {
            connectionErrorDialog.setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetArticleCommentsApi(user_id, articleId, "prev", comment_id, "1", false);
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
        confirmationDialog = new MyCustomThemeDialog(ArticleCommentListActivity.this, R.style.NoTitleDialogTheme);
        ;
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

    private List<PojoArticleCommentsResponseData> removeReportedComments(List<PojoArticleCommentsResponseData> comments) {
        List<PojoArticleCommentsResponseData> moddedList = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).iReported == null || comments.get(i).iReported == 0) {
                moddedList.add(comments.get(i));
            }
        }
        return moddedList;
    }


    private void hitAddCommentApi(final String user_id, final String article_id, final String message, final String type) {
        Call<PojoAddArticleCommentResponse> call = apiService.addCommentOnArticle(user_id, article_id, message);
        call.enqueue(new Callback<PojoAddArticleCommentResponse>() {
            @Override
            public void onResponse(Call<PojoAddArticleCommentResponse> call, Response<PojoAddArticleCommentResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(ArticleCommentListActivity.this, ""
                            + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    comment_count++;
                    //  actionBar.setTitle(getString(R.string.comments) + " (" + comment_count + ")");
                    if (response.body().data.commentId != null && response.body().data.time != null) {
                        for (int i = 0; i < commentList.size(); i++) {
                            if (commentList.get(i).articlesDiscussionId.isEmpty()) {
                                commentList.get(i).articlesDiscussionId = String.valueOf(response.body().data.commentId);
                                commentList.get(i).modifiedOn = response.body().data.time;
                                articleCommentsAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }

                } else {
                    commonFunctions.setToastMessage(ArticleCommentListActivity.this,
                            "Url Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoAddArticleCommentResponse> call, Throwable t) {
                // Log error here since request failed
                et_comment.setText(message);

                if (commentList != null) {

                    for (int i = commentList.size() - 1; i >= 0; i--) {
                        if (commentList.get(i).articlesDiscussionId.isEmpty()) {
                            commentList.remove(i);
                        }
                    }
                    if (et_comment.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(et_comment, InputMethodManager.SHOW_FORCED);
                    }
                    articleCommentsAdapter.notifyDataSetChanged();
                }
                commonFunctions.setToastMessage(ArticleCommentListActivity.this,
                        getString(R.string.comment_not_added), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
            }
        });

    }

    private String getLatestCommentId() {
        if (!commentList.isEmpty()) {
            return commentList.get(commentList.size() - 1).articlesDiscussionId;
        } else {
            return "0";
        }
    }

    private String getOldestCommentId() {
        if (!commentList.isEmpty()) {
            return commentList.get(0).articlesDiscussionId;
        } else {
            return "0";
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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

    @Override
    public void onCommentActionPerformed(final int position, String actionType) {
        switch (actionType) {
            case AppKeys.EDIT:
                openEditCommentDialog(position);
                break;

            case AppKeys.DELETE:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(ArticleCommentListActivity.this, R.style.NoTitleDialogTheme);
                ;
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
                        hitDeleteArticleCommentApi(user_id, commentList.get(position).articlesDiscussionId);
                        commentList.remove(position);
                        articleCommentsAdapter.notifyDataSetChanged();
                        confirmationDialog.dismiss();

                    }
                });
                confirmationDialog.show();
                break;

            case AppKeys.REPORT_COMMENT:
                openReportDialog(position, user_id, "report_article_comment",
                        commentList.get(position).articlesDiscussionId);
                break;

            case AppKeys.LIKE_COMMENT:
                if (commentList.get(position).iLike == 1) {
                    commentList.get(position).iLike = 0;
                    commentList.get(position).totalLikes--;
                    hitDiscussionLikeApi(user_id, commentList.get(position).articlesDiscussionId,
                            "unlike_article_discussion");

                } else {
                    commentList.get(position).iLike = 1;
                    commentList.get(position).totalLikes++;
                    hitDiscussionLikeApi(user_id, commentList.get(position).articlesDiscussionId,
                            "like_article_discussion");
                }
                articleCommentsAdapter.notifyDataSetChanged();
                break;

            case AppKeys.LIKE_COUNT:
                openUserListDialog(commentList.get(position).articlesDiscussionId, "articlecommentreply");
                break;

            case AppKeys.LOAD_NEXT:
                if (!listBottomEndReached) {
                    hitGetArticleCommentsApi(user_id, articleId, "next", getLatestCommentId(),
                            "0", false);
                }
                break;

            case AppKeys.LOAD_PREVIOUS:
                if (!listTopEndReached) {
                    hitGetArticleCommentsApi(user_id, articleId, "prev", getOldestCommentId(),
                            "0", false);
                }
                break;

        }
    }


    private void openUserListDialog(String id, String type) {
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
        userListDialog.setTitle(this.getString(R.string.likes));

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
                        hitGetUpvotersListApi(user_id, id, type, ++upvoterListOffset);
                    }
                }
            }
        });

        upvoterListOffset = 0;
        hitGetUpvotersListApi(user_id, id, type, upvoterListOffset);
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


    private void openEditCommentDialog(final int position) {
        final Dialog dialog = new MyCustomThemeDialog(ArticleCommentListActivity.this, R.style.NoTitleDialogTheme);

        dialog.setContentView(R.layout.edit_content_dialog);

        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);

        et_content.setText(Html.fromHtml(commentList.get(position).post));

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
                    commonFunctions.setToastMessage(ArticleCommentListActivity.this,
                            getString(R.string.please_enter_data), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                } else {
                    if (commentList.get(position).articlesDiscussionId != null) {
                        hitEditArticleCommentApi(user_id, commentList.get(position).articlesDiscussionId,
                                newData);
                        commentList.get(position).post = newData;
                        //tv_question.setText(response.body().data.post);
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                        articleCommentsAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    public void openReportDialog(final int position, final String user_id, final String type, final String id) {
        final Dialog dialog = new MyCustomThemeDialog(ArticleCommentListActivity.this, R.style.NoTitleDialogTheme);
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tv_dialog_description = (TextView) dialog.findViewById(R.id.tv_dialog_description);

        tv_dialog_description.setText(getString(R.string.report_reason_description));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hitReportContentApi(user_id, type, id, et_content.getText().toString().trim());
                commentList.remove(position);
                articleCommentsAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void hitReportContentApi(String user_id, String report_type, String content_id, String reason) {
        Call<PojoNoDataResponse> call = apiService.reportContent(user_id, report_type,
                content_id, reason);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (ArticleCommentListActivity.this != null) {
                        commonFunctions.setToastMessage(getApplicationContext(),
                                "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(ArticleCommentListActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
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
                    commonFunctions.setToastMessage(ArticleCommentListActivity.this,
                            "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(ArticleCommentListActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitDeleteArticleCommentApi(String user_id, String comment_id) {
        Call<PojoNoDataResponse> call = apiService.deleteArticleComment(user_id, comment_id);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(ArticleCommentListActivity.this,
                            "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(ArticleCommentListActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitDiscussionLikeApi(String user_id, String sessionsDiscussionId, String reaction) {
        Call<PojoNoDataResponse> call = apiService.likeUnlikeSessionDiscussion(user_id, sessionsDiscussionId, reaction);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(ArticleCommentListActivity.this,
                            "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(ArticleCommentListActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    @Override
    public void onUpvoterAndAttendeesAdapterMethod(int position) {
        if (upvoterList.get(position).connection.equals("add")
                || upvoterList.get(position).connection.equals("remove")) {
            hitUpdateRequestStatusApi(user_id, upvoterList.get(position).userId, "follow", position);
            upvoterList.get(position).connection = "followed";

        } else if (upvoterList.get(position).connection.equals("followed")) {
            hitUpdateRequestStatusApi(user_id, upvoterList.get(position).userId, "unfollow", position);
            upvoterList.get(position).connection = "add";
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

    @Override
    public void onPause() {
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

}
