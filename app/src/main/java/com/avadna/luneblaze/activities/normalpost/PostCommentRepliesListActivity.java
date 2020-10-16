package com.avadna.luneblaze.activities.normalpost;

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
import com.avadna.luneblaze.adapters.normalPost.PostCommentReplyListAdapter;
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
import com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist.PojoAddNewCommentReplyResponse;
import com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist.PojoNormalPostCommentReplyListItem;
import com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist.PojoGetNormalPostReplyResponse;
import com.avadna.luneblaze.pojo.pojonormalpost.pojoreplylist.PojoGetNormalPostReplyResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostCommentRepliesListActivity extends AppBaseActivity
        implements PostCommentReplyListAdapter.PostCommentReplyListAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {

    PreferenceUtils preferenceUtils;
    ActionBar actionBar;

    String user_id;
    String userName;
    PojoLoginResponseData pojoLoginResponseData;
    String parent_comment_id;
    String post_id;
    String target_reply_id;
    String action;
    ApiInterface apiService;

    RelativeLayout rl_content_wrapper;
    ProgressBar pb_loading_comments;
    RecyclerView rv_reply_list;
    PostCommentReplyListAdapter postCommentReplyListAdapter;

    PojoGetNormalPostReplyResponseData pojoGetNormalPostReplyResponseData;
    List<PojoNormalPostCommentReplyListItem> repliesList;
    LinearLayoutManager repliesListLayoutManager;
    boolean isGetCommentApiCalled = false;
    RelativeLayout rl_bottom_bar;
    EditText et_comment;
    ImageButton ib_send;

    final String TYPE_COMMENT="comment";
    final String TYPE_REPLY="reply";

    View.OnClickListener onClickListener;

    CommonFunctions commonFunctions;
    boolean listBottomEndReached = false;
    boolean listTopEndReached = false;
    String notification_id;

    RecyclerView rv_upvoters;
    LinearLayoutManager upvoterListLayoutManager;
    UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    List<PojoUserData> upvoterList;
    boolean isGetUpvotersApiCalled = false;
    int upvoterListOffset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_comment_replies_list);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        userName = preferenceUtils.get_user_name();
        user_id = preferenceUtils.get_user_id();
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        parent_comment_id = getIntent().getStringExtra(AppKeys.COMMENT_ID);
        post_id = getIntent().getStringExtra(AppKeys.POST_ID);
        target_reply_id = getIntent().getStringExtra(AppKeys.REPLY_ID);
        setUpActionBar();
        initViews();
        initRepliesList();
        initClickListener();
        setClickListeners();
      /*  if (target_reply_id == null || target_reply_id.isEmpty()) {
            target_reply_id = "0";
        }*/
        action = getIntent().getStringExtra(AppKeys.TARGET);

        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }

        if (target_reply_id == null || target_reply_id.isEmpty()) {
            hitGetPostCommentRepliesApi(user_id, parent_comment_id, "0", "prev", "0",false);

        } else {
            hitGetPostCommentRepliesApi(user_id, parent_comment_id, target_reply_id, "prev", "1",false);
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
                    .getColor(PostCommentRepliesListActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(PostCommentRepliesListActivity.this, R.color.status_bar_color));
            }
        }

    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ib_send:
                        if (!et_comment.getText().toString().trim().isEmpty()) {

                            hitAddReplyOnPostComment(user_id, post_id,
                                    et_comment.getText().toString().trim(), parent_comment_id);

                            PojoNormalPostCommentReplyListItem tempReply = new PojoNormalPostCommentReplyListItem();
                          /*  Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                            tempReply.time = mdformat.format(calendar.getTime());*/
                            tempReply.time = getString(R.string.just_now);
                            tempReply.iLike = 0;
                            tempReply.userFullname = userName;
                            tempReply.userId = user_id;
                            tempReply.userPicture = pojoLoginResponseData.userPicture;
                            tempReply.text = et_comment.getText().toString().trim();
                            tempReply.commentId = "";
                            tempReply.likes = "0";

                            repliesList.add(0, tempReply);
                            postCommentReplyListAdapter.notifyDataSetChanged();
                            rv_reply_list.smoothScrollToPosition(0);
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
    public void commentItemClickCallback(int position, String type) {
        switch (type) {
            case AppKeys.EDIT:
                openEditDialog(TYPE_COMMENT,position);
                break;

            case AppKeys.DELETE:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(PostCommentRepliesListActivity.this);
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
                        hitDeletePostCommentApi(user_id, pojoGetNormalPostReplyResponseData.comment.commentId);
                        confirmationDialog.dismiss();
                        finish();
                    }
                });
                confirmationDialog.show();
                break;

            case AppKeys.REPORT_COMMENT:
                commonFunctions.openReportDialog(user_id, "report_comment", pojoGetNormalPostReplyResponseData.comment.commentId);
                break;

            case AppKeys.LIKE:
                if (pojoGetNormalPostReplyResponseData != null) {
                    if (pojoGetNormalPostReplyResponseData.comment.iLike == 1) {
                        pojoGetNormalPostReplyResponseData.comment.iLike = 0;
                        pojoGetNormalPostReplyResponseData.comment.likes--;
                        hitDiscussionLikeApi(user_id, pojoGetNormalPostReplyResponseData.comment.commentId,
                                "unlike_comment");
                    } else {
                        pojoGetNormalPostReplyResponseData.comment.iLike = 1;
                        pojoGetNormalPostReplyResponseData.comment.likes++;
                        hitDiscussionLikeApi(user_id, pojoGetNormalPostReplyResponseData.comment.commentId,
                                "like_comment");

                    }
                    postCommentReplyListAdapter.notifyDataSetChanged();
                }
                break;

            case AppKeys.REPLY:
                openAddCommentBar();
                break;


            case AppKeys.LIKE_COUNT:
                openUserListDialog(pojoGetNormalPostReplyResponseData.comment.likedUsers);
                break;

            case AppKeys.REPLY_COUNT:
                break;

        }
    }


    private void setClickListeners() {
        ib_send.setOnClickListener(onClickListener);

    }

    private void initViews() {
        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        rl_content_wrapper.setVisibility(View.GONE);
        pb_loading_comments = (ProgressBar) findViewById(R.id.pb_loading_comments);
        rl_bottom_bar = (RelativeLayout) findViewById(R.id.rl_bottom_bar);
        rl_bottom_bar.setVisibility(View.GONE);
        ib_send = (ImageButton) findViewById(R.id.ib_send);
        et_comment = (EditText) findViewById(R.id.et_comment);
        et_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppKeys.COMMENT_AND_REPLY_MAX_LENGTH)});

    }

    private void initRepliesList() {
        repliesList = new ArrayList<>();
        rv_reply_list = (RecyclerView) findViewById(R.id.rv_reply_list);
        repliesListLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL,
                false);
        rv_reply_list.setLayoutManager(repliesListLayoutManager);
        postCommentReplyListAdapter = new PostCommentReplyListAdapter(this, repliesList,
                TYPE_COMMENT);
        rv_reply_list.setAdapter(postCommentReplyListAdapter);

        rv_reply_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!listBottomEndReached && dy > 4) {
                    if (!isGetCommentApiCalled && repliesListLayoutManager.findLastVisibleItemPosition() > repliesList.size() - 3) {
                        hitGetPostCommentRepliesApi(user_id, parent_comment_id, getLatestReplyId(),
                                "prev","0", false);
                    }
                } /*else if (dy < -4) {
                    if (!isGetCommentApiCalled && repliesListLayoutManager.findFirstVisibleItemPosition() < 2) {
                        hitGetPostCommentRepliesApi(user_id, parent_comment_id, getOldestReplyId(),
                                "prev", false);
                    }
                }*/
            }
        });


      /*  rv_reply_list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                rv_reply_list.getWindowVisibleDisplayFrame(r);
                int screenHeight = rv_reply_list.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;


                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.

                    if (!repliesList.isEmpty()) {
                        if (postCommentReplyListAdapter.getBottomGapAllowed()) {
                            postCommentReplyListAdapter.setBottomGapAllowed(false);
                            postCommentReplyListAdapter.notifyDataSetChanged();
                        }
                        rv_reply_list.smoothScrollToPosition(repliesList.size() - 1);
                    }

                } else if (!postCommentReplyListAdapter.getBottomGapAllowed()) {
                    postCommentReplyListAdapter.setBottomGapAllowed(true);
                    postCommentReplyListAdapter.notifyDataSetChanged();
                }
            }
        });*/
    }

    private void hitGetPostCommentRepliesApi(String user_id, final String parent_comment_id, final String reply_id,
                                             final String order, String target,final boolean scrollToBottom) {
        Call<PojoGetNormalPostReplyResponse> call = apiService.getPostCommentReplies(user_id, parent_comment_id,
                reply_id, order,target);
        if (!isGetCommentApiCalled) {
            postCommentReplyListAdapter.setShowProgressBar(true);
            postCommentReplyListAdapter.notifyDataSetChanged();
            isGetCommentApiCalled = true;
            call.enqueue(new Callback<PojoGetNormalPostReplyResponse>() {
                @Override
                public void onResponse(Call<PojoGetNormalPostReplyResponse> call, Response<PojoGetNormalPostReplyResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null && response.body().data != null) {
                        message = response.body().message;
                        pojoGetNormalPostReplyResponseData = response.body().data;
                        postCommentReplyListAdapter.setCommentItem(pojoGetNormalPostReplyResponseData.comment);
                        if (response.body().status == 1 && response.body().data.replies != null) {

                            if (target_reply_id != null) {
                                boolean present = false;
                                for (int i = 0; i < response.body().data.replies.size(); i++) {
                                    if (target_reply_id.equals(response.body().data.replies.get(i).commentId)) {
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
                            if (order.equals("next")) {
                                if (response.body().data.replies.size() < 15) {
                                    postCommentReplyListAdapter.setMorePreviousAvailable(false);
                                    postCommentReplyListAdapter.setShowTopMoreButton(false);
                                    listTopEndReached = true;
                                }
                                if (!repliesList.isEmpty()) {
                                    repliesList.remove(0);
                                }
                                Collections.reverse(response.body().data.replies);
                                repliesList.addAll(0, removeReportedReplies(response.body().data.replies));
                            } else if (order.equals("prev")) {

                                if (response.body().data.morePresent == 0) {
                                    postCommentReplyListAdapter.setMorePreviousAvailable(false);
                                    postCommentReplyListAdapter.setShowTopMoreButton(false);
                                }

                                if (response.body().data.morePresent != null && response.body().data.morePresent == 0) {
                                    postCommentReplyListAdapter.setMoreNextAvailable(false);
                                    postCommentReplyListAdapter.setShowBottomMoreButton(false);
                                }

                                if (reply_id.equals("0")) {
                                    if (response.body().data.replies.size() < 15) {
                                        postCommentReplyListAdapter.setMorePreviousAvailable(false);
                                        postCommentReplyListAdapter.setShowTopMoreButton(false);
                                        postCommentReplyListAdapter.setShowBottomMoreButton(false);
                                        listBottomEndReached = true;
                                    } else {
                                        postCommentReplyListAdapter.setMorePreviousAvailable(false);
                                        postCommentReplyListAdapter.setShowTopMoreButton(false);
                                    }


                                } else {
                                    if (response.body().data.replies.size() < 15) {
                                        postCommentReplyListAdapter.setMoreNextAvailable(false);
                                        postCommentReplyListAdapter.setShowBottomMoreButton(false);
                                        listBottomEndReached = true;
                                    }

                                    if (!repliesList.isEmpty()&&!response.body().data.replies.isEmpty()) {
                                        response.body().data.replies.remove(0);
                                    }

                                    if (target != null && target.equals("0")) {
                                        for (int i = 0; i <response.body().data.replies.size(); i++) {
                                            if (response.body().data.replies.get(i).replyCommentId.equals(reply_id)) {
                                                response.body().data.replies.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                }
                            /*    pb_loading_previous.setVisibility(View.INVISIBLE);
                                tv_load_previous.setVisibility(View.VISIBLE);*/
                              /*  if (!repliesList.isEmpty()) {
                                    repliesList.remove(repliesList.size() - 1);
                                }*/
                                repliesList.addAll(removeReportedReplies(response.body().data.replies));
                            }
                            postCommentReplyListAdapter.setCreator_id(pojoGetNormalPostReplyResponseData.comment.userId);
                            postCommentReplyListAdapter.setShowProgressBar(false);

                            postCommentReplyListAdapter.notifyDataSetChanged();
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
                public void onFailure(Call<PojoGetNormalPostReplyResponse> call, Throwable t) {
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
                    hitGetPostCommentRepliesApi(user_id, parent_comment_id, reply_id, "prev", "1",false);
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
        confirmationDialog = new MyCustomThemeDialog(PostCommentRepliesListActivity.this);
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

    private List<PojoNormalPostCommentReplyListItem>
    removeReportedReplies(List<PojoNormalPostCommentReplyListItem> replies) {
        List<PojoNormalPostCommentReplyListItem> moddedReplies = new ArrayList<>();
        for (int i = 0; i < replies.size(); i++) {
            if (replies.get(i).iReported == 0) {
                moddedReplies.add(replies.get(i));
            }
        }
        return moddedReplies;
    }

    private void openAddCommentBar() {
        rl_bottom_bar.setVisibility(View.VISIBLE);
        if (et_comment.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_comment, InputMethodManager.SHOW_FORCED);
        }
    }

    private void hitAddReplyOnPostComment(final String user_id, String post_id, final String text,
                                          final String parent_comment_id) {
        Call<PojoAddNewCommentReplyResponse> call = apiService.addReplyOnPostComment(user_id, "add", text,
                "comment", "user", parent_comment_id);

        call.enqueue(new Callback<PojoAddNewCommentReplyResponse>() {
            @Override
            public void onResponse(Call<PojoAddNewCommentReplyResponse> call, Response<PojoAddNewCommentReplyResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(),
                            "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                    if (response.body().data.replyId != null && response.body().data.time != null) {
                        for (int i = repliesList.size() - 1; i >= 0; i--) {
                            if (repliesList.get(i).commentId.isEmpty()) {
                                repliesList.get(i).commentId = response.body().data.replyId;
                                repliesList.get(i).time = response.body().data.time;
                                postCommentReplyListAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoAddNewCommentReplyResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(),
                        getString(R.string.comment_not_added), Toast.LENGTH_LONG,
                        AppKeys.TOAST_USER);
                et_comment.setText(text);
                if (repliesList != null) {
                    for (int i = repliesList.size() - 1; i >= 0; i--) {
                        if (repliesList.get(i).commentId.isEmpty()) {
                            repliesList.remove(i);
                        }
                    }
                }
                postCommentReplyListAdapter.notifyDataSetChanged();
            }
        });
    }


    private String getLatestReplyId() {
        if (!repliesList.isEmpty()) {
            return repliesList.get(repliesList.size() - 1).commentId;
        } else {
            return "0";
        }
    }

    private String getOldestReplyId() {
        if (!repliesList.isEmpty()) {
            return repliesList.get(0).commentId;
        } else {
            return "0";
        }
    }

    @Override
    public void replyListItemClickCallback(final int position, String type) {
        switch (type) {
            case AppKeys.EDIT:
                openEditDialog(TYPE_REPLY,position);
                break;

            case AppKeys.LIKE_COUNT:
                openUserDialog(position);
                break;

            case AppKeys.DELETE:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(PostCommentRepliesListActivity.this);
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
                        hitDeleteSessionCommentReplyApi(user_id, repliesList.get(position).commentId);
                        repliesList.remove(position);
                        postCommentReplyListAdapter.notifyDataSetChanged();
                        confirmationDialog.show();

                    }
                });
                confirmationDialog.show();

                break;

            case AppKeys.REPORT:
                commonFunctions.hitReportContentApi(user_id, "report_post_comment_reply",
                        repliesList.get(position).replyCommentId, repliesList.get(position).text);
                repliesList.remove(position);
                postCommentReplyListAdapter.notifyDataSetChanged();
                break;

            case AppKeys.LIKE_REPLY:
                int likes = Integer.parseInt(repliesList.get(position).likes);

                if (repliesList.get(position).iLike == 1) {
                    repliesList.get(position).iLike = 0;
                    likes--;
                    hitDiscussionLikeApi(user_id, repliesList.get(position).replyCommentId,
                            "unlike_comment");

                } else {
                    repliesList.get(position).iLike = 1;
                    likes++;

                    hitDiscussionLikeApi(user_id, repliesList.get(position).replyCommentId,
                            "like_comment");
                }
                repliesList.get(position).likes = String.valueOf(likes);
                postCommentReplyListAdapter.notifyDataSetChanged();
                break;

            case AppKeys.LOAD_NEXT:
                if (!listBottomEndReached) {
                    hitGetPostCommentRepliesApi(user_id, parent_comment_id, getLatestReplyId(),
                            "prev", "0",false);
                }
                break;

            case AppKeys.LOAD_PREVIOUS:
                if (!listTopEndReached) {
                    hitGetPostCommentRepliesApi(user_id, parent_comment_id, getOldestReplyId(),
                            "next", "0",false);

                }
                break;
        }
    }


    private void openEditDialog(String type,final int position) {
        final Dialog dialog = new MyCustomThemeDialog(PostCommentRepliesListActivity.this);
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        if(type.equals(TYPE_COMMENT)){
            et_content.setText(Html.fromHtml(pojoGetNormalPostReplyResponseData.comment.text));
        }
        else {
            et_content.setText(Html.fromHtml(repliesList.get(position).text));
        }
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
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_data)
                            , Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    String id="";
                    if(type.equals(TYPE_COMMENT)){
                        id=pojoGetNormalPostReplyResponseData.comment.commentId;
                        pojoGetNormalPostReplyResponseData.comment.text=newData;
                    }
                    else {
                        if (repliesList.get(position).commentId != null) {
                            id=repliesList.get(position).commentId;
                            repliesList.get(position).text = newData;
                            //tv_question.setText(response.body().data.post);
                           }
                    }
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    postCommentReplyListAdapter.notifyDataSetChanged();
                    hitPostCommentEditApi(user_id, id, newData);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void hitDiscussionLikeApi(String user_id, String sessionsDiscussionId, String reaction) {
        Call<PojoNoDataResponse> call = apiService.likeNormalPostComment(user_id, sessionsDiscussionId, reaction);
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

    private void hitPostCommentEditApi(String user_id, String reply_id, final String newData) {
        Call<PojoNoDataResponse> call = apiService.editReplyOnPostComment(user_id, "edit", reply_id, newData);
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


    private void hitDeleteSessionCommentReplyApi(String user_id, String reply_id) {
        Call<PojoNoDataResponse> call = apiService.deleteReplyOnPostComment(user_id, "delete", reply_id);
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

    private void openUserListDialog(List<PojoUserData> userList) {
        TextView tv_title;
        ImageView iv_back;

        final Dialog userListDialog = new MyCustomThemeDialog(this);
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        ProgressBar pb_loading_users = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);
        pb_loading_users.setVisibility(View.GONE);
        tv_title = userListDialog.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.upvotes));
        iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListDialog.dismiss();
            }
        });
        userListDialog.setTitle(getString(R.string.upvotes));
        RecyclerView rv_list;

        rv_list = (RecyclerView) userListDialog.findViewById(R.id.rv_list);
        //rv_list.setPadding(16,32,16,32);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);
        UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter =
                new UpvotersAndAttendeesListAdapter(this, userList);
        rv_list.setAdapter(upvotersAndAttendeesListAdapter);
        userListDialog.show();
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pojoGetNormalPostReplyResponseData != null) {
            if (user_id.equals(pojoGetNormalPostReplyResponseData.comment.userId)) {
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
                final Dialog confirmationDialog = new MyCustomThemeDialog(PostCommentRepliesListActivity.this);
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
                        hitDeletePostCommentApi(user_id, pojoGetNormalPostReplyResponseData.comment.commentId);
                        Intent pushNotification = new Intent(Config.POST_COMMENT_DELETED);
                        pushNotification.putExtra(AppKeys.COMMENT_ID, pojoGetNormalPostReplyResponseData.comment.commentId);
                        LocalBroadcastManager.getInstance(PostCommentRepliesListActivity.this).sendBroadcast(pushNotification);
                        finish();

                    }
                });
                confirmationDialog.show();

                break;

            case R.id.share_to:
                // shareTextUrl(answerList.get(position).sessionsQaId);
                break;

            case R.id.report:
                commonFunctions.openReportDialog(user_id, "report_comment",
                        pojoGetNormalPostReplyResponseData.comment.commentId);
                break;
        }
        return true;
    }

    private void hitDeletePostCommentApi(String user_id, String comment_id) {
        Call<PojoNoDataResponse> call = apiService.deletePostComment(user_id, comment_id);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

    private void openEditCommentDialog() {
        final Dialog dialog = new MyCustomThemeDialog(PostCommentRepliesListActivity.this);
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        et_content.setText(pojoGetNormalPostReplyResponseData.comment.text);
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
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_data),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    hitEditCommentApi(user_id, "comment", pojoGetNormalPostReplyResponseData.comment.commentId,
                            newData);
                    pojoGetNormalPostReplyResponseData.comment.text = newData;
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    postCommentReplyListAdapter.notifyDataSetChanged();
                    //tv_question.setText(response.body().data.post);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void hitEditCommentApi(String user_id, String type, String comment_id, String message) {
        Call<PojoNoDataResponse> call = apiService.editPostComment(user_id, type, comment_id
                , message);
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

    private void openUserDialog(int position) {
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
                        hitGetUpvotersListApi(user_id, repliesList.get(position).replyCommentId,
                                "commentreply", ++upvoterListOffset);
                    }
                }
            }
        });

        upvoterListOffset = 0;
        hitGetUpvotersListApi(user_id, repliesList.get(position).replyCommentId,
                "commentreply", upvoterListOffset);
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
