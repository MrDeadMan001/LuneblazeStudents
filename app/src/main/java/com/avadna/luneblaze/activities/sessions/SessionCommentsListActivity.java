package com.avadna.luneblaze.activities.sessions;

import android.app.Activity;
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
import com.avadna.luneblaze.adapters.session.DiscussionAdapter;
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
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionDiscussionResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoComment;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionCommentsListActivity extends AppBaseActivity implements
        DiscussionAdapter.DiscussionListAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback{

    ActionBar actionBar;

    ProgressBar pb_loading_comments;
    RelativeLayout rl_content_wrapper;
    RelativeLayout rl_bottom_bar;
    RecyclerView rv_discussion_comments_list;
    DiscussionAdapter commentAdapter;
    List<PojoComment> commentList;
    LinearLayoutManager commentListLayoutManager;
    boolean isGetCommentApiCalled = false;
    EditText et_comment;
    TextView tv_dialog_like_number, tv_dialog_comment_number;
    int totalCommentCount = 0;

    String sessionId;
    String target_comment_id;
    String top_comment_id;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    String user_id;
    String user_name;
    PojoLoginResponseData pojoLoginResponseData;
    ApiInterface apiService;
    ImageButton ib_send;

    RecyclerView rv_upvoters;
    LinearLayoutManager upvoterListLayoutManager;
    UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    List<PojoUserData> upvoterList;
    boolean isGetUpvotersApiCalled = false;
    int upvoterListOffset = 0;

    boolean listBottomEndReached = false;
    boolean listTopEndReached = false;

    private BroadcastReceiver broadcastReceiver;

    String notification_id;
    String action;

    final int API_COMMMENT_COUNT = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_comments);
        setUpActionBar();

        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        user_name = preferenceUtils.get_user_name();
        sessionId = getIntent().getStringExtra("id");

        action = getIntent().getStringExtra(AppKeys.TARGET);
        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }

        initViews();
        initCommentList();

        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.SESSION_COMMENT_DELETED));
        target_comment_id = getIntent().getStringExtra(AppKeys.COMMENT_ID);
        top_comment_id = target_comment_id;

        if (target_comment_id == null || target_comment_id.isEmpty()) {
            top_comment_id = "0";
            hitGetSessionComments(user_id, sessionId, "comments", "prev", "0",
                    "0", false);
        } else {
            hitGetSessionComments(user_id, sessionId, "comments", "prev", target_comment_id,
                    "1", false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void initBroadCastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.SESSION_COMMENT_DELETED)) {
                    String comment_id = intent.getStringExtra(AppKeys.COMMENT_ID);
                    if (commentList != null) {
                        for (int i = 0; i < commentList.size(); i++) {
                            if (commentList.get(i).sessionsCommentId.equals(comment_id)) {
                                commentList.remove(i);
                                commentAdapter.notifyDataSetChanged();
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
            actionBar.setTitle(getString(R.string.comments));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SessionCommentsListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SessionCommentsListActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void initViews() {
        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        rl_bottom_bar = (RelativeLayout) findViewById(R.id.rl_bottom_bar);
        rl_bottom_bar.setVisibility(View.GONE);
        rl_content_wrapper.setVisibility(View.GONE);
        pb_loading_comments = (ProgressBar) findViewById(R.id.pb_loading_comments);
        et_comment = (EditText) findViewById(R.id.et_comment);
        et_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppKeys.COMMENT_AND_REPLY_MAX_LENGTH)});

        ib_send = (ImageButton) findViewById(R.id.ib_send);
        ib_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!et_comment.getText().toString().trim().isEmpty()) {
                    hitAddCommentApi(user_id, "add_comment", sessionId, et_comment.getText().toString().trim());
                    PojoComment tempComment = new PojoComment();
                    tempComment.addedOn = getString(R.string.just_now);
                    tempComment.userData = new PojoUserData();
                    tempComment.userData.userFullname = user_name;
                    tempComment.userData.userId = user_id;
                    tempComment.userData.userPicture = pojoLoginResponseData.userPicture;
                    tempComment.post = et_comment.getText().toString().trim();
                    tempComment.sessionsCommentId = "";
                    tempComment.sessionsId = sessionId;
                    tempComment.totalLikes = 0;
                    tempComment.totalReplies = 0;

                    commentList.add(0, tempComment);
                    commentAdapter.notifyDataSetChanged();
                    rv_discussion_comments_list.scrollToPosition(0);
                    et_comment.getText().clear();
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);
                    totalCommentCount++;
                    updateCommentCount();
                }
            }
        });

        tv_dialog_like_number = (TextView) findViewById(R.id.tv_dialog_like_number);
        tv_dialog_comment_number = (TextView) findViewById(R.id.tv_dialog_comment_number);
        commentList = new ArrayList<>();
        tv_dialog_like_number.setVisibility(View.GONE);
    }

    private void initCommentList() {
        rv_discussion_comments_list = (RecyclerView) findViewById(R.id.rv_comments_list);
        commentListLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        rv_discussion_comments_list.setLayoutManager(commentListLayoutManager);
        commentAdapter = new DiscussionAdapter(this, commentList, "comment");
        rv_discussion_comments_list.setAdapter(commentAdapter);

        rv_discussion_comments_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!listBottomEndReached && dy > 4) {
                    if (commentListLayoutManager.findLastVisibleItemPosition() > commentList.size() - 3) {
                        hitGetSessionComments(user_id, sessionId, "comments", "prev",
                                getLatestCommentId(), "0", false);
                    }
                } /*else if (dy < -4) {
                    if (commentListLayoutManager.findFirstVisibleItemPosition() < 2) {
                        hitGetSessionComments(user_id, sessionId, "comments", "prev",
                                getOldestCommentId(), false);
                    }
                }*/
            }
        });
       /* rv_discussion_comments_list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                rv_discussion_comments_list.getWindowVisibleDisplayFrame(r);
                int screenHeight = rv_discussion_comments_list.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;


                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.

                    if (!commentList.isEmpty()) {
                        rv_discussion_comments_list.smoothScrollToPosition(commentList.size() - 1);
                    }

                } else {
                    // keyboard is closed
                }
            }
        });*/


    }

    private void hitGetSessionComments(final String user_id, String session_id, String view, final String order,
                                       final String sessions_comment_id, String target, final boolean scrollToEnd) {
        Call<PojoSessionDetailsResponse> call = apiService.getSessionDetails(user_id, session_id,
                view, order, "", sessions_comment_id, target);

        if (!isGetCommentApiCalled) {
            commentAdapter.setShowProgressBar(true);
            commentAdapter.notifyDataSetChanged();
            isGetCommentApiCalled = true;
            call.enqueue(new Callback<PojoSessionDetailsResponse>() {
                @Override
                public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null && response.body().data != null) {
                        if (response.body().data.iAttend) {
                            rl_bottom_bar.setVisibility(View.VISIBLE);
                        }
                        if (!response.body().data.presentors.isEmpty()
                                && response.body().data.presentors.get(0).userId.equals(user_id)) {
                            rl_bottom_bar.setVisibility(View.VISIBLE);
                        }
                        message = response.body().message;
                        if (response.body().status == 1 && response.body().data.comments != null) {

                            if (target_comment_id != null) {
                                boolean present = false;
                                for (int i = 0; i < response.body().data.comments.size(); i++) {
                                    if (target_comment_id.equals(response.body().data.comments.get(i).sessionsCommentId)) {
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

                            if (sessions_comment_id.equals("0")) {
                                commentList.clear();
                            }
                           /* if (order.equals("next")) {
                                if (response.body().data.comments.size() < API_COMMMENT_COUNT) {
                                    commentAdapter.setMoreNextAvailable(false);
                                    listBottomEndReached = true;
                                }
                                if (!sessions_comment_id.equals("0") && !commentList.isEmpty()) {
                                    response.body().data.comments.remove(0);
                                }

                               *//* if (!commentList.isEmpty()) {
                                    commentList.remove(commentList.size() - 1);
                                }*//*
                                commentList.addAll(removeReportedPostComments(response.body().data.comments));
                            } else */
                            if (order.equals("prev")) {
                                commentAdapter.setMorePreviousAvailable(false);
                                commentAdapter.setShowTopMoreButton(false);
                                if (response.body().data.morePresent == 0) {
                                    commentAdapter.setMoreNextAvailable(false);
                                    commentAdapter.setShowBottomMoreButton(false);
                                }
                                if (response.body().data.loadMore != null && response.body().data.loadMore == 0) {
                                    commentAdapter.setMorePreviousAvailable(false);
                                    commentAdapter.setShowTopMoreButton(false);
                                }


                                if (sessions_comment_id.equals("0")) {
                                    if (response.body().data.comments.size() < API_COMMMENT_COUNT) {
                                        commentAdapter.setMorePreviousAvailable(false);
                                        commentAdapter.setShowTopMoreButton(false);
                                        commentAdapter.setShowBottomMoreButton(false);
                                        listTopEndReached = true;
                                    } else {
                                        commentAdapter.setMoreNextAvailable(false);
                                        commentAdapter.setShowBottomMoreButton(false);
                                    }
                                } else {
                                    if (response.body().data.comments.size() < API_COMMMENT_COUNT) {
                                        commentAdapter.setMorePreviousAvailable(false);
                                        commentAdapter.setShowTopMoreButton(false);
                                        listTopEndReached = true;
                                    }

                                    if (!response.body().data.comments.isEmpty() && !commentList.isEmpty()) {
                                        response.body().data.comments.remove(0);
                                    }

                                    if (target != null && target.equals("0")) {
                                        for (int i = 0; i < response.body().data.comments.size(); i++) {
                                            if (response.body().data.comments.get(i).sessionsCommentId.equals(top_comment_id)) {
                                                response.body().data.comments.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                }
                            /*    pb_loading_previous.setVisibility(View.INVISIBLE);
                                tv_load_previous.setVisibility(View.VISIBLE);*/
                              /*  if (!commentList.isEmpty()) {
                                    commentList.remove(0);
                                }*/
                                commentList.addAll(removeReportedPostComments(response.body().data.comments));
                            }

                            commentAdapter.setCreator_id(response.body().data.presentors.get(0).userId);
                            commentAdapter.setShowProgressBar(false);

                            commentAdapter.notifyDataSetChanged();
                            if (scrollToEnd) {
                                /* if (*//*target_reply_id.equals("0") && *//*order.equals("prev")) {
                                    rv_discussion_comments_list.scrollToPosition(commentList.size());

                                } else */

                                rv_discussion_comments_list.scrollToPosition(commentList.size() + 1);

                            }

                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                        totalCommentCount = Integer.parseInt(response.body().data.commentsCnt);
                        updateCommentCount();

                        pb_loading_comments.setVisibility(View.GONE);
                        rl_content_wrapper.setVisibility(View.VISIBLE);
                    }
                    isGetCommentApiCalled = false;

                    if (action != null && action.equals(AppKeys.ADD_COMMENT)) {
                        action = null;
                        if (et_comment.requestFocus()) {
                            InputMethodManager imm = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et_comment, InputMethodManager.SHOW_IMPLICIT);
                        }
                    }

                }

                @Override
                public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // rl_parent.setVisibility(View.VISIBLE);
                    if (commentList.isEmpty()) {
                        openNoConnectionDialog(getString(R.string.comments), sessions_comment_id);
                    }
                    isGetCommentApiCalled = false;
                    /// rl_content_wrapper.setVisibility(View.VISIBLE);
                }
            });
        }

    }

    private void updateCommentCount() {
        if (totalCommentCount == 0) {
            tv_dialog_comment_number.setVisibility(View.GONE);
        } else {
            tv_dialog_comment_number.setVisibility(View.VISIBLE);
            tv_dialog_comment_number.setText(totalCommentCount + " " + getString(R.string.comments));
        }
    }

    private void openNoConnectionDialog(String title, String comment_id) {
        try {
            connectionErrorDialog.setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetSessionComments(user_id, sessionId, "comments", "prev", comment_id,
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
            connectionErrorDialog.setTitle(title);
        } catch (Exception e) {

        }

    }


    public void showMessageDialog(String message) {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(SessionCommentsListActivity.this);
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

    public List<PojoComment> removeReportedPostComments(List<PojoComment> comments) {
        List<PojoComment> moddedComments = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            if (comments.get(i).iReported == 0) {
                moddedComments.add(comments.get(i));
            }
        }
        return moddedComments;
    }


    private void hitAddCommentApi(final String user_id, final String type, String session_id, final String comment_post) {
        Call<PojoAddSessionDiscussionResponse> call = null;

        call = apiService.sessionConnects(user_id, type, session_id, "", comment_post, "", "");

        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call,
                                   Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), ""
                            + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    if (sessionId != null) {
                        if (response.body().data.id != null && response.body().data.time != null) {
                            for (int i = 0; i < commentList.size(); i++) {
                                if (commentList.get(i).sessionsCommentId.isEmpty()) {
                                    commentList.get(i).sessionsCommentId = String.valueOf(response.body().data.id);
                                    commentList.get(i).modifiedOn = response.body().data.time;
                                    commentAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(),
                        getString(R.string.comment_not_added), Toast.LENGTH_LONG,
                        AppKeys.TOAST_USER);
                et_comment.setText(comment_post);
                if (commentList != null) {
                    for (int i = commentList.size() - 1; i >= 0; i--) {
                        if (commentList.get(i).sessionsCommentId.isEmpty()) {
                            commentList.remove(i);
                        }
                    }
                }
                commentAdapter.notifyDataSetChanged();

            }
        });
    }


    private String getLatestCommentId() {
        if (!commentList.isEmpty()) {
            return commentList.get(commentList.size() - 1).sessionsCommentId;
        } else {
            return "0";
        }
    }

    private String getOldestCommentId() {
        if (!commentList.isEmpty()) {
            return commentList.get(0).sessionsCommentId;
        } else {
            return "0";
        }
    }

    @Override
    public void discussionListItemClickCallback(final int position, String type) {
        switch (type) {
            case AppKeys.LIKE:
                if (commentList.get(position).iLike) {
                    hitDiscussionLikeApi(user_id, commentList.get(position).sessionsCommentId,
                            "unlike_session_comment");
                    commentList.get(position).totalLikes--;

                } else {
                    hitDiscussionLikeApi(user_id, commentList.get(position).sessionsCommentId,
                            "like_session_comment");
                    commentList.get(position).totalLikes++;

                }
                commentList.get(position).iLike = !commentList.get(position).iLike;
                commentAdapter.notifyDataSetChanged();
                break;

            case AppKeys.EDIT:
                openEditDialog(position);
                break;

            case AppKeys.REPORT_COMMENT:
                openReportDialog(position, user_id, "report_session_comment", commentList.get(position).sessionsCommentId);
                break;

            case AppKeys.LIKE_COUNT:
                openUserListDialog(commentList.get(position).sessionsCommentId, "sessioncommentreply");
                break;

            case AppKeys.LOAD_NEXT:
                if (!listBottomEndReached) {
                    hitGetSessionComments(user_id, sessionId, "comments", "next",
                            getLatestCommentId(), "0", false);
                }
                break;

            case AppKeys.LOAD_PREVIOUS:
                if (!listTopEndReached) {
                    hitGetSessionComments(user_id, sessionId, "comments", "prev",
                            getOldestCommentId(), "0", false);
                }
                break;

            case AppKeys.DELETE:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(this);
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
                        hitDeleteSessionCommentApi(user_id, commentList.get(position).sessionsCommentId, position);
                        commentList.remove(position);
                        commentAdapter.notifyDataSetChanged();
                        confirmationDialog.dismiss();
                        totalCommentCount--;
                        updateCommentCount();
                    }
                });
                confirmationDialog.show();

                break;
        }
    }

    private void openReportDialog(final int position, final String user_id, final String type, final String id) {
        final Dialog dialog = new MyCustomThemeDialog(this);
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        TextView tv_dialog_description = (TextView) dialog.findViewById(R.id.tv_dialog_description);

        tv_dialog_description.setText(getString(R.string.report_reason_description));
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commonFunctions.hitReportContentApi(user_id, type, id, et_content.getText().toString().trim());
                commentList.remove(position);
                commentAdapter.notifyDataSetChanged();
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void hitDiscussionLikeApi(String user_id, String sessionsDiscussionId, String reaction) {
        Call<PojoNoDataResponse> call = apiService.likeUnlikeSessionDiscussion(user_id, sessionsDiscussionId, reaction);
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

    private void openEditDialog(final int position) {
        final Dialog dialog = new MyCustomThemeDialog(SessionCommentsListActivity.this);
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
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.please_enter_data), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    if (commentList.get(position).sessionsCommentId != null) {
                        hitEditSessionCommentApi(user_id, commentList.get(position).sessionsCommentId,
                                newData, position);
                        commentList.get(position).post = newData;
                        //tv_question.setText(response.body().data.post);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                        commentAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }


    private void hitEditSessionCommentApi(String user_id, String reply_id, final String newData, final int position) {
        Call<PojoNoDataResponse> call = apiService.editSessionCommentAndRepliy(user_id, reply_id, newData);
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


    private void hitDeleteSessionCommentApi(String user_id, String discussion_id, final int position) {
        Call<PojoNoDataResponse> call = apiService.deleteSessionCommentAndReply(user_id, discussion_id);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return true;
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
