package com.avadna.luneblaze.activities.sessions;

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
import com.avadna.luneblaze.adapters.session.SessionReplyAdapter;
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
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionCommentReplyResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoGetSessionRepliesResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoGetSessionRepliesResponseData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionReply;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.leocardz.link.preview.library.TextCrawler;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionCommentRepliesListActivity extends AppBaseActivity
        implements SessionReplyAdapter.ReplyListAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {

    PreferenceUtils preferenceUtils;
    ActionBar actionBar;
    String user_id;
    String userName;

    PojoLoginResponseData pojoLoginResponseData;
    String parent_comment_id;
    String session_id;
    String target_reply_id;
    String top_reply_id;
    String action = "";
    ApiInterface apiService;
    TextCrawler textCrawler;

    ProgressBar pb_loading_comments;
    RecyclerView rv_reply_list;
    SessionReplyAdapter sessionReplyAdapter;

    PojoGetSessionRepliesResponseData pojoGetSessionRepliesResponseData;
    List<PojoSessionReply> repliesList;
    LinearLayoutManager repliesListLayoutManager;
    boolean isGetCommentApiCalled = false;

    RelativeLayout rl_bottom_bar;
    EditText et_comment;
    ImageButton ib_send;

    View.OnClickListener onClickListener;
    CommonFunctions commonFunctions;
    String notification_id;

    /* TextView tv_load_previous;
     ProgressBar pb_loading_previous;*/
    boolean listBottomEndReached = false;
    boolean listTopEndReached = false;

    RecyclerView rv_upvoters;
    LinearLayoutManager upvoterListLayoutManager;
    UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    List<PojoUserData> upvoterList;
    boolean isGetUpvotersApiCalled = false;
    int upvoterListOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions_discussion_replies_list);
        textCrawler = new TextCrawler();
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        userName = preferenceUtils.get_user_name();
        user_id = preferenceUtils.get_user_id();
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        parent_comment_id = getIntent().getStringExtra(AppKeys.COMMENT_ID);
        session_id = getIntent().getStringExtra(AppKeys.SESSION_ID);
        target_reply_id = getIntent().getStringExtra(AppKeys.REPLY_ID);
        top_reply_id = target_reply_id;
        setUpActionBar();
        initViews();
        initRepliesList();
        initClickListener();
        setClickListeners();

        action = getIntent().getStringExtra(AppKeys.TARGET);

        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }
        if (target_reply_id == null || target_reply_id.isEmpty()) {
            top_reply_id = "0";
            hitGetSessionReply(user_id, parent_comment_id, "0", "prev", "0", false);
        } else {
            hitGetSessionReply(user_id, parent_comment_id, target_reply_id, "prev", "1", false);
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
                    .getColor(this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(this, R.color.status_bar_color));
            }
        }

    }

    private void setClickListeners() {
        ib_send.setOnClickListener(onClickListener);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ib_send:
                        if (!et_comment.getText().toString().trim().isEmpty()) {
                            hitAddReplyOnSessionCommentApi(user_id, session_id,
                                    et_comment.getText().toString().trim(), parent_comment_id);
                            PojoSessionReply tempReply = new PojoSessionReply();
                            tempReply.addedOn = getString(R.string.just_now);
                            tempReply.modifiedOn = getString(R.string.just_now);
                            tempReply.userData = new PojoUserData();
                            tempReply.userData.userFullname = userName;
                            tempReply.userData.userId = user_id;
                            tempReply.userData.userPicture = pojoLoginResponseData.userPicture;
                            tempReply.post = et_comment.getText().toString().trim();
                            tempReply.sessionsCommentId = "";
                            tempReply.iLike = 0;
                            tempReply.totalLikes = "0";
                            tempReply.sessionsId = pojoGetSessionRepliesResponseData.sessionsId;
                            repliesList.add(0, tempReply);
                            pojoGetSessionRepliesResponseData.totalReplies++;
                            sessionReplyAdapter.notifyDataSetChanged();
                            rv_reply_list.smoothScrollToPosition(1);
                            et_comment.getText().clear();
                            InputMethodManager imm = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);
                        }
                        break;
                }
            }
        };
    }

    @Override
    public void commentClickAction(int position, String type) {
        switch (type) {
            case AppKeys.COMMENT_REPLY:
                openAddCommentBar();
                break;

            case AppKeys.DISCUSSION_REPLY:
                openAddCommentBar();
                break;

            case AppKeys.LIKE:
                if (pojoGetSessionRepliesResponseData != null) {
                    if (pojoGetSessionRepliesResponseData.iLike == 1) {
                        pojoGetSessionRepliesResponseData.iLike = 0;
                        pojoGetSessionRepliesResponseData.totalLikes--;
                        hitDiscussionLikeApi(user_id, pojoGetSessionRepliesResponseData.sessionsCommentId,
                                "unlike_session_comment");
                    } else {
                        pojoGetSessionRepliesResponseData.iLike = 1;
                        pojoGetSessionRepliesResponseData.totalLikes++;
                        hitDiscussionLikeApi(user_id, pojoGetSessionRepliesResponseData.sessionsCommentId,
                                "like_session_comment");
                    }
                }
                sessionReplyAdapter.notifyDataSetChanged();
                break;

            case AppKeys.LIKE_COUNT:
                openUserDialog(pojoGetSessionRepliesResponseData.sessionsDiscussionId, "sessiondiscussionreply");
                break;
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

    private void initViews() {
        et_comment = (EditText) findViewById(R.id.et_comment);
        pb_loading_comments = (ProgressBar) findViewById(R.id.pb_loading_comments);
        pb_loading_comments.setVisibility(View.VISIBLE);
        et_comment = (EditText) findViewById(R.id.et_comment);
        et_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppKeys.COMMENT_AND_REPLY_MAX_LENGTH)});

        ib_send = (ImageButton) findViewById(R.id.ib_send);
        rl_bottom_bar = (RelativeLayout) findViewById(R.id.rl_bottom_bar);
        rl_bottom_bar.setVisibility(View.GONE);
        et_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppKeys.COMMENT_AND_REPLY_MAX_LENGTH)});
    }

    private void setDataOnViews() {
        invalidateOptionsMenu();
        String text = Html.fromHtml(pojoGetSessionRepliesResponseData.post).toString();
    }

    private void initRepliesList() {

        repliesList = new ArrayList<>();
        rv_reply_list = (RecyclerView) findViewById(R.id.rv_comments_list);
        repliesListLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        rv_reply_list.setLayoutManager(repliesListLayoutManager);
        sessionReplyAdapter = new SessionReplyAdapter(this, repliesList, "comment");
        rv_reply_list.setAdapter(sessionReplyAdapter);

        rv_reply_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!listBottomEndReached && dy > 4) {
                    if (!isGetCommentApiCalled && repliesListLayoutManager.findLastVisibleItemPosition() > repliesList.size() - 3) {
                        hitGetSessionReply(user_id, parent_comment_id, getLatestReplyId(),
                                "prev", "0", false);
                    }
                }
            }
        });
    }

    private void hitGetSessionReply(String user_id, final String parent_comment_id, final String reply_id,
                                    final String order, String target, final boolean scrollToBottom) {
        Call<PojoGetSessionRepliesResponse> call = apiService.getSessionCommentReplies(user_id,
                parent_comment_id, reply_id, order, target);

        if (!isGetCommentApiCalled) {
            sessionReplyAdapter.setShowProgressBar(true);
            sessionReplyAdapter.notifyDataSetChanged();
            isGetCommentApiCalled = true;
            call.enqueue(new Callback<PojoGetSessionRepliesResponse>() {
                @Override
                public void onResponse(Call<PojoGetSessionRepliesResponse> call, Response<PojoGetSessionRepliesResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null && response.body().data != null) {
                        message = response.body().message;
                        pojoGetSessionRepliesResponseData = response.body().data;
                        sessionReplyAdapter.setCommentItem(response.body().data);
                        if (response.body().status == 1 && response.body().data.replies != null) {
                            if (target_reply_id != null) {
                                boolean present = false;
                                for (int i = 0; i < response.body().data.replies.size(); i++) {
                                    if (target_reply_id.equals(response.body().data.replies.get(i).sessionsCommentId)) {
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
                                repliesList.clear();
                            }
                           /* if (order.equals("next")) {
                                if (response.body().data.replies.size() < 15) {
                                    sessionReplyAdapter.setMorePreviousAvailable(false);
                                    sessionReplyAdapter.setShowTopMoreButton(false);
                                    listTopEndReached = true;
                                }

                                if (!repliesList.isEmpty()) {
                                    repliesList.remove(0);
                                }
                                Collections.reverse(response.body().data.replies);
                                repliesList.addAll(0, removeReportedReplies(response.body().data.replies));
                            } else */
                            if (order.equals("prev")) {
                                // Collections.reverse(response.body().data.replies);
                                sessionReplyAdapter.setMoreNextAvailable(false);
                                sessionReplyAdapter.setShowBottomMoreButton(false);
                               /* if (response.body().data.morePresent == 0) {
                                    sessionReplyAdapter.setMorePreviousAvailable(false);
                                    sessionReplyAdapter.setShowTopMoreButton(false);
                                }

                                if (response.body().data.loadMore != null && response.body().data.loadMore == 0) {
                                    sessionReplyAdapter.setMoreNextAvailable(false);
                                    sessionReplyAdapter.setShowBottomMoreButton(false);
                                }*/
                                if (reply_id.equals("0")) {
                                    if (response.body().data.replies.size() < 15) {
                                        sessionReplyAdapter.setMorePreviousAvailable(false);
                                        sessionReplyAdapter.setShowTopMoreButton(false);
                                        sessionReplyAdapter.setShowBottomMoreButton(false);
                                        listTopEndReached = true;
                                    } else {
                                        sessionReplyAdapter.setMorePreviousAvailable(false);
                                        sessionReplyAdapter.setShowTopMoreButton(false);
                                    }
                                } else {
                                    if (response.body().data.replies.size() < 15) {
                                        sessionReplyAdapter.setMoreNextAvailable(false);
                                        sessionReplyAdapter.setShowBottomMoreButton(false);
                                        listBottomEndReached = true;
                                    }
                                    if (!repliesList.isEmpty() && !response.body().data.replies.isEmpty()) {
                                        response.body().data.replies.remove(0);
                                    }

                                    if (target != null && target.equals("0")) {
                                        for (int i = 0; i < response.body().data.replies.size(); i++) {
                                            if (response.body().data.replies.get(i).sessionsCommentId.equals(top_reply_id)) {
                                                response.body().data.replies.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                }
                            /*    pb_loading_previous.setVisibility(View.INVISIBLE);
                                tv_load_previous.setVisibility(View.VISIBLE);*/

                                repliesList.addAll(removeReportedReplies(response.body().data.replies));
                            }
                            sessionReplyAdapter.setCreator_id(pojoGetSessionRepliesResponseData.userId);
                            sessionReplyAdapter.setShowProgressBar(false);

                            sessionReplyAdapter.notifyDataSetChanged();
                            if (scrollToBottom) {

                                if (/*target_reply_id.equals("0") && */order.equals("prev")) {
                                    rv_reply_list.scrollToPosition(0);

                                }
                            }
                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), message,
                                    Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                        setDataOnViews();
                        rl_bottom_bar.setVisibility(View.VISIBLE);
                    }
                    pb_loading_comments.setVisibility(View.GONE);
                    isGetCommentApiCalled = false;

                    if (action != null && action.equals(AppKeys.ADD_REPLY)) {
                        action = null;
                        openAddCommentBar();
                    }
                }

                @Override
                public void onFailure(Call<PojoGetSessionRepliesResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // rl_parent.setVisibility(View.VISIBLE);
                    if (repliesList.isEmpty()) {
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
                    hitGetSessionReply(user_id, parent_comment_id, reply_id, "prev", "1", false);
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
        confirmationDialog = new MyCustomThemeDialog(SessionCommentRepliesListActivity.this);
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

    public List<PojoSessionReply> removeReportedReplies(List<PojoSessionReply> replies) {
        List<PojoSessionReply> moddedComments = new ArrayList<>();
        for (int i = 0; i < replies.size(); i++) {
            if (replies.get(i).iReported == null || replies.get(i).iReported == 0) {
                moddedComments.add(replies.get(i));
            }
        }
        return moddedComments;
    }

    private void hitAddReplyOnSessionCommentApi(final String user_id, String session_id, final String post, final String comment_id) {
        Call<PojoAddSessionCommentReplyResponse> call = null;

        call = apiService.addSessionCommentReply(user_id, session_id, post, comment_id);

        call.enqueue(new Callback<PojoAddSessionCommentReplyResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionCommentReplyResponse> call, Response<PojoAddSessionCommentReplyResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(),
                            "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                    if (response.body().data.sessionsCommentId != null && response.body().data.time != null) {
                        for (int i = repliesList.size() - 1; i >= 0; i--) {
                            if (repliesList.get(i).sessionsCommentId.isEmpty()) {
                                repliesList.get(i).sessionsCommentId = String.valueOf(response.body().data.sessionsCommentId);
                                repliesList.get(i).modifiedOn = response.body().data.time;
                                sessionReplyAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionCommentReplyResponse> call, Throwable t) {
                // Log error here since request failed
                try {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.comment_not_added), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                    et_comment.setText(post);
                    if (repliesList != null) {
                        for (int i = repliesList.size() - 1; i >= 0; i--) {
                            if (repliesList.get(i).sessionsDiscussionId.isEmpty()) {
                                repliesList.remove(i);
                            }
                        }
                    }
                    sessionReplyAdapter.notifyDataSetChanged();
                } catch (Exception e) {

                }

            }
        });
    }


    private String getLatestReplyId() {
        if (!repliesList.isEmpty()) {
            return repliesList.get(repliesList.size() - 1).sessionsCommentId;
        } else {
            return "0";
        }
    }

    private String getOldestReplyId() {
        if (!repliesList.isEmpty()) {
            return repliesList.get(0).sessionsCommentId;
        } else {
            return "0";
        }
    }

    @Override
    public void replyListItemClickCallback(final int position, String type) {
        switch (type) {
            case AppKeys.LIKE_REPLY:
                int likeCnt = Integer.parseInt(repliesList.get(position).totalLikes);
                if (repliesList.get(position).iLike == 1) {
                    repliesList.get(position).iLike = 0;
                    likeCnt--;
                    hitDiscussionLikeApi(user_id, repliesList.get(position).sessionsCommentId,
                            "unlike_session_comment");

                } else {
                    likeCnt++;
                    repliesList.get(position).iLike = 1;

                    hitDiscussionLikeApi(user_id, repliesList.get(position).sessionsCommentId,
                            "like_session_comment");
                }
                repliesList.get(position).totalLikes = String.valueOf(likeCnt);
                sessionReplyAdapter.notifyDataSetChanged();
                break;

            case AppKeys.EDIT:
                openEditDialog(position);
                break;

            case AppKeys.LIKE_COUNT:
                openUserDialog(repliesList.get(position).sessionsCommentId, "sessioncommentreply");
                break;

            case AppKeys.DELETE:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(SessionCommentRepliesListActivity.this);
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
                        hitDeleteSessionCommentReplyApi(user_id, repliesList.get(position).sessionsCommentId, position);
                        repliesList.remove(position);
                        pojoGetSessionRepliesResponseData.totalReplies--;
                        sessionReplyAdapter.notifyDataSetChanged();
                        confirmationDialog.dismiss();

                    }
                });
                confirmationDialog.show();
                break;

            case AppKeys.REPORT_REPLY:
                commonFunctions.hitReportContentApi(user_id, "report_session_comment_reply",
                        repliesList.get(position).sessionsCommentId, repliesList.get(position).post);
                repliesList.remove(position);
                sessionReplyAdapter.notifyDataSetChanged();
                break;

            case AppKeys.LOAD_NEXT:
                if (!listBottomEndReached) {
                    hitGetSessionReply(user_id, parent_comment_id, getLatestReplyId(),
                            "prev", "0", false);
                }
                break;

            case AppKeys.LOAD_PREVIOUS:
                if (!listTopEndReached) {
                    hitGetSessionReply(user_id, parent_comment_id, getOldestReplyId(),
                            "next", "0", false);
                }
                break;
        }
    }


    private void openEditDialog(final int position) {
        final Dialog dialog = new MyCustomThemeDialog(SessionCommentRepliesListActivity.this);
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        et_content.setText(Html.fromHtml(repliesList.get(position).post));
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
                    if (repliesList.get(position).sessionsCommentId != null) {
                        hitEditSessionCommentApi(user_id, repliesList.get(position).sessionsCommentId,
                                newData, position);
                        repliesList.get(position).post = newData;
                        //tv_question.setText(response.body().data.post);
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                        sessionReplyAdapter.notifyDataSetChanged();
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

    private void hitDeleteSessionCommentReplyApi(String user_id, String discussion_id, final int position) {
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

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pojoGetSessionRepliesResponseData != null) {
            if (user_id.equals(pojoGetSessionRepliesResponseData.userId)) {
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
                final Dialog confirmationDialog = new MyCustomThemeDialog(SessionCommentRepliesListActivity.this);
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
                        hitDeleteSessionCommentApi(user_id, pojoGetSessionRepliesResponseData.sessionsCommentId);
                        Intent pushNotification = new Intent(Config.SESSION_COMMENT_DELETED);
                        pushNotification.putExtra(AppKeys.COMMENT_ID, pojoGetSessionRepliesResponseData.sessionsCommentId);
                        LocalBroadcastManager.getInstance(SessionCommentRepliesListActivity.this).sendBroadcast(pushNotification);
                        finish();

                    }
                });
                confirmationDialog.show();
                break;

            case R.id.share_to:
                // shareTextUrl(answerList.get(position).sessionsQaId);
                break;

            case R.id.report:
                commonFunctions.openReportDialog(user_id, "report_session_comment",
                        pojoGetSessionRepliesResponseData.sessionsCommentId);
                break;
        }
        return true;
    }

    private void openEditCommentDialog() {
        final Dialog dialog = new MyCustomThemeDialog(SessionCommentRepliesListActivity.this);
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        et_content.setText(pojoGetSessionRepliesResponseData.post);
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
                    if (pojoGetSessionRepliesResponseData.sessionsCommentId != null) {
                        hitEditSessionCommentApi(user_id, pojoGetSessionRepliesResponseData.sessionsCommentId,
                                newData, 0);
                        pojoGetSessionRepliesResponseData.post = newData;
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                        setDataOnViews();
                        sessionReplyAdapter.notifyDataSetChanged();
                        //tv_question.setText(response.body().data.post);

                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }


    private void hitDeleteSessionCommentApi(String user_id, String discussion_id) {
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
        if (repliesList.get(position).likedUsers.get(position).connection.equals("add")
                || repliesList.get(position).likedUsers.get(position).connection.equals("remove")) {
            hitUpdateRequestStatusApi(user_id, repliesList.get(position).likedUsers.get(position).userId,
                    "follow", position);
            repliesList.get(position).likedUsers.get(position).connection = "followed";

        } else if (repliesList.get(position).likedUsers.get(position).connection.equals("followed")) {
            hitUpdateRequestStatusApi(user_id, repliesList.get(position).likedUsers.get(position).userId,
                    "unfollow", position);
            repliesList.get(position).likedUsers.get(position).connection = "add";
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
