package com.avadna.luneblaze.activities.question;

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
import android.view.LayoutInflater;
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
import com.avadna.luneblaze.adapters.AnswerReplyListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoAnswerReply;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoGetAnswerRepliesResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoGetAnswerRepliesResponseData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoAddSessionDiscussionResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class AnswerReplyListActivity extends AppBaseActivity implements
        AnswerReplyListAdapter.AnswerReplyListAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {

    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    String user_id;
    String userName;
    PojoLoginResponseData pojoLoginResponseData;
    ActionBar actionBar;

    ProgressBar pb_loading_comments;
    RecyclerView rv_reply_list;
    PojoGetAnswerRepliesResponseData pojoGetAnswerRepliesResponseData;
    List<PojoAnswerReply> repliesList;
    LinearLayoutManager replyListLayoutManager;
    AnswerReplyListAdapter answerReplyListAdapter;
    boolean isGetCommentApiCalled = false;
    EditText et_comment;
    RelativeLayout rl_bottom_bar;
    ImageButton ib_send;

    String answer_id;
    String target_reply_id;
    String top_reply_id;
    View.OnClickListener onClickListener;
    CommonFunctions commonFunctions;
    String action = "";
    boolean listBottomEndReached = false;
    boolean listTopEndReached = false;

    String notification_id;

    final int API_COMMMENT_COUNT = 15;

    RecyclerView rv_upvoters;
    LinearLayoutManager upvoterListLayoutManager;
    UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    List<PojoUserData> upvoterList;
    boolean isGetUpvotersApiCalled = false;
    int upvoterListOffset = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_list);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        userName = preferenceUtils.get_user_name();
        pojoLoginResponseData = preferenceUtils.getUserLoginData();

        if (getIntent() != null) {
            answer_id = getIntent().getStringExtra(AppKeys.ANSWER_ID);
            target_reply_id = getIntent().getStringExtra(AppKeys.REPLY_ID);
            top_reply_id = target_reply_id;
            action = getIntent().getStringExtra(AppKeys.TARGET);
            notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        }

        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }
        setUpActionBar();
        initViews();
        initReplyList();
        initClickListener();
        setClickListeners();

        if (target_reply_id == null || target_reply_id.isEmpty()) {
            top_reply_id = "0";
            hitGetAnswerRepliesApi(user_id, answer_id, "0", "prev", "0", false);
        } else {
            hitGetAnswerRepliesApi(user_id, answer_id, target_reply_id, "prev", "1", false);
        }

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

    private void initViews() {
        pb_loading_comments = (ProgressBar) findViewById(R.id.pb_loading_comments);
        rl_bottom_bar = (RelativeLayout) findViewById(R.id.rl_bottom_bar);
        rl_bottom_bar.setVisibility(View.GONE);
        et_comment = (EditText) findViewById(R.id.et_comment);
        et_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppKeys.COMMENT_AND_REPLY_MAX_LENGTH)});
        ib_send = (ImageButton) findViewById(R.id.ib_send);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ib_send:
                        if (!et_comment.getText().toString().trim().isEmpty()) {

                            hitAddReplyApi(user_id, "add_qa_reply",
                                    pojoGetAnswerRepliesResponseData.sessionsId,
                                    pojoGetAnswerRepliesResponseData.sessionsQaId,
                                    et_comment.getText().toString().trim());

                            PojoAnswerReply tempReply = new PojoAnswerReply();

                            tempReply.modifiedOn = getString(R.string.just_now);
                            tempReply.addedOn = getString(R.string.just_now);

                            tempReply.userData = new PojoUserData();
                            tempReply.userData.userFullname = userName;
                            tempReply.userData.userId = user_id;
                            tempReply.userData.userPicture = pojoLoginResponseData.userPicture;
                            tempReply.post = et_comment.getText().toString().trim();
                            tempReply.sessionsQaId = "";
                            tempReply.likes = "0";
                            tempReply.totalLikes = "0";

                            tempReply.iLike = 0;
                            tempReply.sessionsId = pojoGetAnswerRepliesResponseData.sessionsId;

                            repliesList.add(0, tempReply);
                            int replyCount = Integer.parseInt(pojoGetAnswerRepliesResponseData.no_of_replies);
                            replyCount++;
                            pojoGetAnswerRepliesResponseData.no_of_replies = String.valueOf(replyCount);
                            answerReplyListAdapter.notifyDataSetChanged();
                            rv_reply_list.scrollToPosition(1);
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
    public void answerClickActionMethod(int position, String type) {
        switch (type) {
            case AppKeys.LIKE:
                if (pojoGetAnswerRepliesResponseData != null) {
                    if (pojoGetAnswerRepliesResponseData.iLike == 1) {
                        hitAnswerLikeApi(user_id, "unlike_qacomment", pojoGetAnswerRepliesResponseData.sessionsId,
                                pojoGetAnswerRepliesResponseData.sessionsQaId);
                        pojoGetAnswerRepliesResponseData.iLike = 0;
                        pojoGetAnswerRepliesResponseData.totalLikes--;

                    } else {
                        hitAnswerLikeApi(user_id, "like_qacomment", pojoGetAnswerRepliesResponseData.sessionsId,
                                pojoGetAnswerRepliesResponseData.sessionsQaId);
                        pojoGetAnswerRepliesResponseData.iLike = 1;
                        pojoGetAnswerRepliesResponseData.totalLikes++;
                    }
                    setDataOnViews();
                    answerReplyListAdapter.notifyDataSetChanged();
                }
                break;

            case AppKeys.ANSWER_SHARED:
                openShareAnswerOnProfileDialog();
                break;

            case AppKeys.UPVOTER:
                openUserDialog(position, "answerreply", pojoGetAnswerRepliesResponseData.sessionsQaId);
                break;

            case AppKeys.REPLY:
                openAddReplyBar();
                break;
        }
    }


    private void initReplyList() {
        repliesList = new ArrayList<>();
        rv_reply_list = (RecyclerView) findViewById(R.id.rv_comments_list);
        replyListLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        rv_reply_list.setLayoutManager(replyListLayoutManager);
        answerReplyListAdapter = new AnswerReplyListAdapter(this, repliesList);
        rv_reply_list.setAdapter(answerReplyListAdapter);

        rv_reply_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 4) {
                    if (!isGetCommentApiCalled && !listBottomEndReached
                            && replyListLayoutManager.findLastVisibleItemPosition() > repliesList.size() - 3) {
                        hitGetAnswerRepliesApi(user_id, answer_id, getLatestReplyId(), "prev", "0", false);
                    }
                }
            }
        });
    }


    private void openShareAnswerOnProfileDialog() {
        TextView tv_username;
        ImageView iv_user_dp;
        final EditText et_share_title;
        Button tv_share_button;
        final Dialog shareDialog;
        RelativeLayout rl_item_holder;
        shareDialog = new MyCustomThemeDialog(this);
        shareDialog.setContentView(R.layout.share_dialog_box_layout);
        shareDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv_username = shareDialog.findViewById(R.id.tv_username);
        iv_user_dp = (ImageView) shareDialog.findViewById(R.id.iv_user_dp);
        et_share_title = (EditText) shareDialog.findViewById(R.id.et_share_title);
        tv_share_button = (Button) shareDialog.findViewById(R.id.tv_share_button);
        rl_item_holder = (RelativeLayout) shareDialog.findViewById(R.id.rl_item_holder);

        PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
        tv_username.setText(pojoLoginResponseData.userFullname);
        Glide.with(getApplicationContext())
                .load(pojoLoginResponseData.userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_user_dp);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.answer_share_item, rl_item_holder, true);

        TextView tv_question = (TextView) view.findViewById(R.id.tv_question);

        ImageView iv_answer_dp = (ImageView) view.findViewById(R.id.iv_answer_dp);
        TextView tv_answer_user_name = (TextView) view.findViewById(R.id.tv_answer_user_name);
        TextView tv_answer_time_stamp = (TextView) view.findViewById(R.id.tv_answer_time_stamp);
        TextView tv_answer = (TextView) view.findViewById(R.id.tv_answer);
        TextView tv_num_upvotes = (TextView) view.findViewById(R.id.tv_num_upvotes);
        TextView tv_num_replies = (TextView) view.findViewById(R.id.tv_num_replies);

        tv_question.setText(Html.fromHtml(pojoGetAnswerRepliesResponseData.question.post));
        Glide.with(getApplicationContext())
                .load(pojoGetAnswerRepliesResponseData.user.userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_answer_dp);
        tv_answer_user_name.setText(pojoGetAnswerRepliesResponseData.user.userFullname);
        tv_answer_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(pojoGetAnswerRepliesResponseData.addedOn));
        tv_answer.setText(Html.fromHtml(pojoGetAnswerRepliesResponseData.post));
        tv_num_upvotes.setText("" + pojoGetAnswerRepliesResponseData.totalLikes + " " + getString(R.string.upvotes));
        tv_num_replies.setText("" + pojoGetAnswerRepliesResponseData.no_of_replies + " " + getString(R.string.replies));

        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitShareContentApi(user_id, pojoGetAnswerRepliesResponseData.sessionsQaId, "share_answer",
                        et_share_title.getText().toString().trim());
                shareDialog.dismiss();
            }
        });
        shareDialog.show();
    }

    private void hitShareContentApi(String user_id, String article_id, String reaction, String title) {
        Call<PojoNoDataResponse> call = apiService.shareContent(user_id, article_id, reaction, title);
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

    private void hitGetAnswerRepliesApi(String user_id, String answer_id, final String reply_id, final String order,
                                        String target, final boolean scrollToBottom) {
        Call<PojoGetAnswerRepliesResponse> call = apiService.getRepliesOnAnswer(user_id, answer_id,
                reply_id, order, target);
        if (!isGetCommentApiCalled) {
            answerReplyListAdapter.setShowProgressBar(true);
            answerReplyListAdapter.notifyDataSetChanged();
            isGetCommentApiCalled = true;
            call.enqueue(new Callback<PojoGetAnswerRepliesResponse>() {
                @Override
                public void onResponse(Call<PojoGetAnswerRepliesResponse> call, Response<PojoGetAnswerRepliesResponse> response) {
                    if (response != null && response.body() != null && response.body().data != null) {
                        String message = "";
                        pojoGetAnswerRepliesResponseData = response.body().data;
                        answerReplyListAdapter.setAnswerItem(pojoGetAnswerRepliesResponseData);
                        message = response.body().message;
                        if (response.body().status == 1 && response.body().data.replies != null) {

                            if (target_reply_id != null) {
                                boolean present = false;
                                for (int i = 0; i < response.body().data.replies.size(); i++) {
                                    if (target_reply_id.equals(response.body().data.replies.get(i).sessionsQaId)) {
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
                                if (response.body().data.replies.size() < API_COMMMENT_COUNT) {
                                    answerReplyListAdapter.setMorePreviousAvailable(false);
                                    answerReplyListAdapter.setShowTopMoreButton(false);
                                    listTopEndReached = true;
                                }

                                if (!repliesList.isEmpty()) {
                                    repliesList.remove(0);
                                }
                                Collections.reverse(response.body().data.replies);

                                repliesList.addAll(0, removeReportedReplies(response.body().data.replies));
                            } else if (order.equals("prev")) {

                                if (response.body().data.morePresent == 0) {
                                    answerReplyListAdapter.setMorePreviousAvailable(false);
                                    answerReplyListAdapter.setShowTopMoreButton(false);
                                }

                                if (response.body().data.loadMore != null && response.body().data.loadMore == 0) {
                                    answerReplyListAdapter.setMoreNextAvailable(false);
                                    answerReplyListAdapter.setShowBottomMoreButton(false);
                                }

                                if (reply_id.equals("0")) {
                                    if (response.body().data.replies.size() < API_COMMMENT_COUNT) {
                                        answerReplyListAdapter.setMorePreviousAvailable(false);
                                        answerReplyListAdapter.setShowTopMoreButton(false);
                                        answerReplyListAdapter.setShowBottomMoreButton(false);
                                        listTopEndReached = true;
                                    } else {
                                        answerReplyListAdapter.setMorePreviousAvailable(false);
                                        answerReplyListAdapter.setShowTopMoreButton(false);
                                    }
                                } else {
                                    if (response.body().data.replies.size() < API_COMMMENT_COUNT) {
                                        answerReplyListAdapter.setMoreNextAvailable(false);
                                        answerReplyListAdapter.setShowBottomMoreButton(false);
                                        listBottomEndReached = true;
                                    }

                                    if (!repliesList.isEmpty() && !response.body().data.replies.isEmpty()) {
                                        response.body().data.replies.remove(0);
                                    }

                                    if (target != null && target.equals("0")) {
                                        for (int i = 0; i < response.body().data.replies.size(); i++) {
                                            if (response.body().data.replies.get(i).sessionsQaId.equals(top_reply_id)) {
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
                            answerReplyListAdapter.setCreator_id(pojoGetAnswerRepliesResponseData.userId);
                            answerReplyListAdapter.setShowProgressBar(false);

                            answerReplyListAdapter.notifyDataSetChanged();
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

                        // tv_dialog_comment_number.setText(response.body().data.commentsCnt + " " + getString(R.string.comments));
                    }
                    pb_loading_comments.setVisibility(View.GONE);
                    isGetCommentApiCalled = false;

                    if (action != null && action.equals(AppKeys.ADD_REPLY)) {
                        openAddReplyBar();
                        action = null;

                    }
                }

                @Override
                public void onFailure(Call<PojoGetAnswerRepliesResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // rl_parent.setVisibility(View.VISIBLE);
                    pb_loading_comments.setVisibility(View.GONE);
                    isGetCommentApiCalled = false;
                }
            });
        }

    }

    public void showMessageDialog(String message) {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(AnswerReplyListActivity.this);
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

    private List<PojoAnswerReply> removeReportedReplies(List<PojoAnswerReply> replies) {
        List<PojoAnswerReply> moddedReplies = new ArrayList<>();
        for (int i = 0; i < replies.size(); i++) {
            if (replies.get(i).iReported == 0) {
                moddedReplies.add(replies.get(i));
            }
        }
        return moddedReplies;
    }

    public void setDataOnViews() {
        invalidateOptionsMenu();
        if (action != null && action.equals(AppKeys.ADD_REPLY)) {
            action = null;
            openAddReplyBar();
        }
    }

    private void openUpvoterListDialog() {
        TextView tv_title;
        ImageView iv_back;
        final Dialog userListDialog;
        userListDialog = new MyCustomThemeDialog(AnswerReplyListActivity.this);
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //  interestListDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        tv_title = userListDialog.findViewById(R.id.tv_title);

        ProgressBar pb_loading_users = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);
        pb_loading_users.setVisibility(View.GONE);
        iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userListDialog.dismiss();
            }
        });
        userListDialog.setTitle(getString(R.string.followers));
        RecyclerView rv_list;

        rv_list = (RecyclerView) userListDialog.findViewById(R.id.rv_list);
        //rv_list.setPadding(16,32,16,32);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false);
        rv_list.setLayoutManager(linearLayoutManager);

        UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;

        upvotersAndAttendeesListAdapter = new UpvotersAndAttendeesListAdapter(this,
                pojoGetAnswerRepliesResponseData.likedUsers);
        tv_title.setText(getString(R.string.upvotes));

        rv_list.setAdapter(upvotersAndAttendeesListAdapter);
        userListDialog.show();

    }

    private void openAddReplyBar() {
        rl_bottom_bar.setVisibility(View.VISIBLE);
        if (et_comment.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_comment, InputMethodManager.SHOW_FORCED);
        }
    }

    private void hitAddReplyApi(final String user_id, String type, String sessionsId,
                                String sessionsQaId, final String reply) {
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type,
                sessionsId, "", reply, "", sessionsQaId);
        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call,
                                   Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data.id != null && response.body().data.time != null) {
                        for (int i = repliesList.size() - 1; i >= 0; i--) {
                            if (repliesList.get(i).sessionsQaId.isEmpty()) {
                                repliesList.get(i).sessionsQaId = String.valueOf(response.body().data.id);
                                repliesList.get(i).modifiedOn = response.body().data.time;
                                answerReplyListAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(),
                        getString(R.string.comment_not_added), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                et_comment.setText(reply);
                if (repliesList != null) {
                    for (int i = repliesList.size() - 1; i >= 0; i--) {
                        if (repliesList.get(i).sessionsQaId.isEmpty()) {
                            repliesList.remove(i);
                        }
                    }
                }
                answerReplyListAdapter.notifyDataSetChanged();
            }
        });
    }

    private String getLatestReplyId() {
        if (!repliesList.isEmpty()) {
            return repliesList.get(repliesList.size() - 1).sessionsQaId;
        } else {
            return "0";
        }
    }

    private String getOldestReplyId() {
        if (!repliesList.isEmpty()) {
            return repliesList.get(0).sessionsQaId;
        } else {
            return "0";
        }
    }

    @Override
    public void answerReplyListItemClick(final int position, String type) {
        switch (type) {
            case AppKeys.EDIT:
                openEditReplyDialog(position);
                break;

            case AppKeys.LIKE_COUNT:
                openUserDialog(position, "answerreply", repliesList.get(position).sessionsQaId);
                break;

            case AppKeys.DELETE:
                hitDeleteAnswerApi(user_id, repliesList.get(position).sessionsQaId);

                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(AnswerReplyListActivity.this);
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
                        hitDeleteAnswerApi(user_id, repliesList.get(position).sessionsQaId);
                        repliesList.remove(position);
                        answerReplyListAdapter.notifyDataSetChanged();
                        confirmationDialog.dismiss();

                    }
                });
                confirmationDialog.show();
                break;

            case AppKeys.REPORT_REPLY:
                commonFunctions.hitReportContentApi(user_id, "report_answer_reply",
                        repliesList.get(position).sessionsQaId, repliesList.get(position).post);
                repliesList.remove(position);
                answerReplyListAdapter.notifyDataSetChanged();
                break;

            case AppKeys.LIKE_REPLY:
                int likes = Integer.parseInt(repliesList.get(position).totalLikes);
                if (repliesList.get(position).iLike == 1) {
                    repliesList.get(position).iLike = 0;
                    hitAnswerLikeApi(user_id, "unlike_qacomment", pojoGetAnswerRepliesResponseData.sessionsId,
                            repliesList.get(position).sessionsQaId);
                    likes--;
                } else {
                    repliesList.get(position).iLike = 1;

                    hitAnswerLikeApi(user_id, "like_qacomment", pojoGetAnswerRepliesResponseData.sessionsId,
                            repliesList.get(position).sessionsQaId);
                    likes++;
                }
                repliesList.get(position).totalLikes = String.valueOf(likes);
                answerReplyListAdapter.notifyDataSetChanged();
                break;

            case AppKeys.LOAD_NEXT:
                if (!listBottomEndReached) {
                    hitGetAnswerRepliesApi(user_id, answer_id, getLatestReplyId(), "prev", "0", false);
                }
                break;

            case AppKeys.LOAD_PREVIOUS:
                if (!listTopEndReached) {
                    hitGetAnswerRepliesApi(user_id, answer_id, getOldestReplyId(), "next", "0", false);
                }
                break;
        }
    }


    private void openEditReplyDialog(final int position) {
        final Dialog dialog = new MyCustomThemeDialog(AnswerReplyListActivity.this);
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
                String newData = et_content.getText().toString();
                if (newData.isEmpty()) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.please_enter_data), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    hitEditReplyApi(user_id, repliesList.get(position).sessionsQaId,
                            newData);
                    repliesList.get(position).post = newData;
                    answerReplyListAdapter.notifyDataSetChanged();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }


    private void hitEditReplyApi(String user_id, String sessionsQaId, String newData) {
        Call<PojoNoDataResponse> call = apiService.editQuesAnsReply(user_id, sessionsQaId, newData);
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

    private void hitAnswerLikeApi(String user_id, String type, String session_id, String question_id) {
        Call<PojoNoDataResponse> call = apiService.likeUnlikeAnswerReply(user_id, type, session_id, question_id);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
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

    private void hitDeleteAnswerApi(String user_id, String session_qa_id) {
        Call<PojoNoDataResponse> call = apiService.deleteQuesAnsReply(user_id, session_qa_id, "");
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

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


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pojoGetAnswerRepliesResponseData != null) {
            if (user_id.equals(pojoGetAnswerRepliesResponseData.userId)) {
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
                openEditAnswerDialog();
                break;

            case R.id.delete:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(AnswerReplyListActivity.this);
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
                        confirmationDialog.show();
                        hitDeleteAnswerApi(user_id, pojoGetAnswerRepliesResponseData.sessionsQaId);
                        Intent pushNotification = new Intent(Config.ANSWER_DELETED);
                        pushNotification.putExtra(AppKeys.ANSWER_ID, pojoGetAnswerRepliesResponseData.sessionsQaId);
                        LocalBroadcastManager.getInstance(AnswerReplyListActivity.this).sendBroadcast(pushNotification);
                        finish();
                    }
                });

                break;

            case R.id.share_to:
                shareTextUrl(pojoGetAnswerRepliesResponseData.sessionsQaId);
                break;

            case R.id.report:
                commonFunctions.openReportDialog(user_id, "report_answer",
                        pojoGetAnswerRepliesResponseData.sessionsQaId);
                break;
        }
        return true;
    }

    private void shareTextUrl(String sessionsQaId) {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        String url = AppKeys.WEBSITE_URL + "question/19/#ans" + sessionsQaId;
        share.putExtra(Intent.EXTRA_TEXT, url);
        startActivity(Intent.createChooser(share, "Share link!"));
    }

    private void openEditAnswerDialog() {
        final Dialog dialog = new MyCustomThemeDialog(AnswerReplyListActivity.this);
        dialog.setContentView(R.layout.edit_content_dialog);

        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);

        et_content.setText(pojoGetAnswerRepliesResponseData.post);

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newData = et_content.getText().toString();
                if (newData.isEmpty()) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.please_enter_data), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    hitEditAnswerApi(user_id, pojoGetAnswerRepliesResponseData.sessionsQaId,
                            newData);
                    pojoGetAnswerRepliesResponseData.post = newData;
                    setDataOnViews();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    answerReplyListAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void hitEditAnswerApi(String user_id, String sessionsQaId, String newData) {
        Call<PojoNoDataResponse> call = apiService.editQuesAnsReply(user_id, sessionsQaId, newData);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_DEBUG);

                commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.answer_not_edited),
                        Toast.LENGTH_LONG, AppKeys.TOAST_USER);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    private void openUserDialog(int position, String reaction, String id) {
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
                    if (!isGetUpvotersApiCalled && upvoterListLayoutManager.findLastVisibleItemPosition()
                            > upvoterList.size() - 2) {
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
}
