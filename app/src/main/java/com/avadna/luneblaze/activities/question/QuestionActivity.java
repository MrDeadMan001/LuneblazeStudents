package com.avadna.luneblaze.activities.question;

import android.annotation.SuppressLint;
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

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.InviteActivity;
import com.avadna.luneblaze.adapters.venue.AnswerListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;
import com.avadna.luneblaze.helperClasses.SpannableClickItem;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoReportContentResponse;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoAnswer;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoEditQaResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoGetQuestionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoGetQuestionDetailsResponseData;
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

public class QuestionActivity extends AppBaseActivity implements
        AnswerListAdapter.AnswerListAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {

    ProgressBar pb_loading_content;

    ActionBar actionBar;
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    PojoLoginResponseData pojoLoginResponseData;

    RecyclerView rv_answer_list;
    AnswerListAdapter answerListAdapter;
    List<PojoAnswer> answerList;
    LinearLayoutManager answerListLayoutManager;
    boolean isAnswerListApiCalled = false;

    RecyclerView rv_upvoters;
    LinearLayoutManager upvoterListLayoutManager;
    UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    List<PojoUserData> upvoterList;
    boolean isGetUpvotersApiCalled = false;
    int upvoterListOffset = 0;

    String QUESTIONER = "questioner";
    String OTHER = "other";
    String type = OTHER;
    PojoGetQuestionDetailsResponseData questionData;


    Menu menu;
    View.OnClickListener onClickListener;

    String question_id;
    String user_id = "";
    String target_answer_id;
    String top_answer_id;
    String action = "";
    Dialog upvoterListDialog;

    RelativeLayout rl_comment_bar;
    EditText et_comment;
    ImageButton ib_send;
    boolean addedAnswer = false;

    boolean listBottomEndReached = false;
    boolean listTopEndReached = false;

    BroadcastReceiver broadcastReceiver;

    PopupMenu popupMenu;
    String sortBy = "date";
    String notification_id;

    final int API_COMMMENT_COUNT = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(QuestionActivity.this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        setUpActionBar();
        // initQuestionOptionSpinner();
        initViews();
        initClickListener();
        setClickListener();
        question_id = getIntent().getStringExtra("id");
        if (question_id == null || question_id.isEmpty()) {
            question_id = "2";
        }

        action = getIntent().getStringExtra(AppKeys.ACTION);
        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }
        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.ANSWER_DELETED));
        target_answer_id = getIntent().getStringExtra(AppKeys.ANSWER_ID);
        top_answer_id = target_answer_id;
        if (target_answer_id == null || target_answer_id.isEmpty()) {
            top_answer_id = "0";
            hitGetQuestionDetailsApi(preferenceUtils.get_user_id(), question_id, "question", sortBy,
                    "prev", "0", "0", false);
        } else {
            hitGetQuestionDetailsApi(preferenceUtils.get_user_id(), question_id, "question", sortBy,
                    "prev", target_answer_id, "1", true);
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
                if (intent.getAction().equals(Config.ANSWER_DELETED)) {
                    String comment_id = intent.getStringExtra(AppKeys.ANSWER_ID);
                    if (answerList != null) {
                        for (int i = 0; i < answerList.size(); i++) {
                            if (answerList.get(i).sessionsQaId.equals(comment_id)) {
                                answerList.remove(i);
                                answerListAdapter.notifyDataSetChanged();
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
            actionBar.setTitle(getString(R.string.question));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(QuestionActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(QuestionActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        initAnswerList();
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        rl_comment_bar = (RelativeLayout) findViewById(R.id.rl_comment_bar);
        rl_comment_bar.setVisibility(View.GONE);
        et_comment = (EditText) findViewById(R.id.et_comment);
        et_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppKeys.COMMENT_AND_REPLY_MAX_LENGTH)});

        ib_send = (ImageButton) findViewById(R.id.ib_send);

      /*  tv_sort = (TextView) findViewById(R.id.tv_sort);
        popupMenu = new PopupMenu(this, tv_sort);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.date:
                        sortBy = "date";
                        listBottomEndReached = false;
                        listTopEndReached = false;
                        answerListAdapter.setMorePreviousAvailable(true);
                        answerListAdapter.setMoreNextAvailable(true);
                        answerListAdapter.setShowBottomMoreButton(true);
                        answerListAdapter.setShowTopMoreButton(true);
                        hitGetQuestionDetailsApi(preferenceUtils.get_user_id(), question_id, "question",
                                sortBy, "prev", "0", false);
                        break;

                    case R.id.upvotes:
                        sortBy = "nooflikes";
                        listBottomEndReached = false;
                        listTopEndReached = false;
                        answerListAdapter.setMorePreviousAvailable(true);
                        answerListAdapter.setMoreNextAvailable(true);
                        answerListAdapter.setShowBottomMoreButton(true);
                        answerListAdapter.setShowTopMoreButton(true);
                        hitGetQuestionDetailsApi(preferenceUtils.get_user_id(), question_id, "question",
                                sortBy, "prev", "0", false);
                        break;
                }
                return false;
            }
        });*/

    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ib_send:
                        hitAddAnswerApi(user_id, "add_qa_answer", question_id,
                                questionData.qas.sessionsId, et_comment.getText().toString());
                        PojoAnswer comment = new PojoAnswer();
                        comment.post = et_comment.getText().toString().trim();
                        comment.userData = new PojoUserData();
                        comment.userId = user_id;
                        comment.userData.userId = user_id;
                        comment.sessionsQaId = "";
                        comment.iLike = false;
                        comment.userData.userFullname = preferenceUtils.get_user_name();
                        comment.userData.userPicture = pojoLoginResponseData.userPicture;
                        comment.modifiedOn = getString(R.string.just_now);
                        comment.addedOn = getString(R.string.just_now);
                        comment.totalLikes = 0;
                        comment.total_replies = "0";
                        answerList.add(0, comment);
                        answerListAdapter.notifyDataSetChanged();

                        rv_answer_list.scrollToPosition(1);

                        et_comment.getText().clear();
                        InputMethodManager imm = (InputMethodManager)
                                getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(rl_comment_bar.getWindowToken(), 0);
                        rl_comment_bar.setVisibility(View.GONE);
                        if (questionData != null) {
                            questionData.qas.totalAns++;
                            answerListAdapter.notifyDataSetChanged();
                        }
                        break;

                    case R.id.tv_sort:
                        MenuInflater inflater = popupMenu.getMenuInflater();
                        popupMenu.getMenu().clear();
                        inflater.inflate(R.menu.answer_list_sort_menu, popupMenu.getMenu());
                        popupMenu.show();
                        break;
                }
            }
        };
    }


    private void setClickListener() {
        ib_send.setOnClickListener(onClickListener);
    }

    private void openShareQuestionOnProfileDialog() {
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
        View view = inflater.inflate(R.layout.share_question_item, rl_item_holder, true);

        TextView tv_question = (TextView) view.findViewById(R.id.tv_question);
        TextView tv_question_time_stamp = (TextView) view.findViewById(R.id.tv_question_time_stamp);

        tv_question.setText(Html.fromHtml(questionData.qas.post));
        tv_question_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(questionData.qas.addedOn));

        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitShareContentApi(user_id, question_id, "share_question",
                        et_share_title.getText().toString().trim());
                shareDialog.dismiss();
            }
        });
        shareDialog.show();

    }


    private void initAnswerList() {
        answerList = new ArrayList<>();
        rv_answer_list = (RecyclerView) findViewById(R.id.rv_answer_list);
        answerListAdapter = new AnswerListAdapter(this, answerList);
        answerListLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_answer_list.setLayoutManager(answerListLayoutManager);
        rv_answer_list.setAdapter(answerListAdapter);
        rv_answer_list.setNestedScrollingEnabled(false);

        rv_answer_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (!listBottomEndReached && dy > 4) {
                    if (!isAnswerListApiCalled
                            && answerListLayoutManager.findLastVisibleItemPosition() > answerList.size() - 3) {
                        hitGetQuestionDetailsApi(preferenceUtils.get_user_id(), question_id,
                                "question", sortBy, "prev", getLatestAnswerId(), "0", false);
                    }
                }/* else if (dy < -4) {
                    if (!isAnswerListApiCalled &&!topEndReached
                            && answerListLayoutManager.findFirstVisibleItemPosition() < 2) {
                        hitGetQuestionDetailsApi(preferenceUtils.get_user_id(), question_id,
                                "question", "prev", getOldestAnswerId(), false);
                    }
                }*/
            }
        });
    }


    private void hitGetQuestionDetailsApi(String user_id, String question_id, String type, String sort_by,
                                          final String order, final String answer_id, String target, final boolean scrollToEnd) {
        Call<PojoGetQuestionDetailsResponse> call = apiService.getQuestionDetails(user_id, question_id,
                type, sort_by, order, answer_id, target);
        if (!isAnswerListApiCalled) {
            answerListAdapter.setShowProgressBar(true);
            answerListAdapter.notifyDataSetChanged();
            isAnswerListApiCalled = true;
            //  pb_loading_more_answers.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<PojoGetQuestionDetailsResponse>() {
                @Override
                public void onResponse(Call<PojoGetQuestionDetailsResponse> call, Response<PojoGetQuestionDetailsResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null) {
                        message = response.body().message;
                        if (response.body().data != null) {
                            questionData = response.body().data;
                            questionData.qas.sessionName = response.body().data.sessionData.title;
                            answerListAdapter.setQuestionData(questionData.qas);
                            // Collections.reverse(response.body().data.qas.answers);
                            if (target_answer_id != null && target.equals("1")) {
                                boolean present = false;
                                for (int i = 0; i < response.body().data.qas.answers.size(); i++) {
                                    if (target_answer_id.equals(response.body().data.qas.answers.get(i).sessionsQaId)) {
                                        present = true;
                                        target_answer_id = null;
                                        break;
                                    }
                                }
                                if (!present) {
                                    target_answer_id = null;
                                    showMessageDialog(getString(R.string.the_answer_has_been_deleted));
                                }
                            }

                            if (answer_id.equals("0")) {
                                answerList.clear();
                            }
                            if (order.equals("next")) {
                                if (response.body().data.qas.answers.size() < API_COMMMENT_COUNT) {
                                    answerListAdapter.setMorePreviousAvailable(false);
                                    answerListAdapter.setShowTopMoreButton(false);
                                    listTopEndReached = true;
                                }

                                if (!answerList.isEmpty()) {
                                    answerList.remove(0);
                                }
                                Collections.reverse(response.body().data.qas.answers);
                                answerList.addAll(0, removeReportedAnswers(response.body().data.qas.answers));
                            } else if (order.equals("prev")) {

                                if (response.body().data.qas.morePresent != null && response.body().data.qas.morePresent == 0) {
                                    answerListAdapter.setMorePreviousAvailable(false);
                                    answerListAdapter.setShowTopMoreButton(false);
                                }

                                if (response.body().data.qas.loadMore != null && response.body().data.qas.loadMore == 0) {
                                    answerListAdapter.setMoreNextAvailable(false);
                                    answerListAdapter.setShowBottomMoreButton(false);
                                }

                                if (answer_id.equals("0")) {
                                    if (response.body().data.qas.answers.size() < API_COMMMENT_COUNT) {
                                        answerListAdapter.setMorePreviousAvailable(false);
                                        answerListAdapter.setShowTopMoreButton(false);
                                        answerListAdapter.setShowBottomMoreButton(false);
                                        listTopEndReached = true;
                                    } else {
                                        answerListAdapter.setMorePreviousAvailable(false);
                                        answerListAdapter.setShowTopMoreButton(false);
                                    }
                                } else {
                                    if (response.body().data.qas.answers.size() < API_COMMMENT_COUNT) {
                                        answerListAdapter.setMoreNextAvailable(false);
                                        answerListAdapter.setShowBottomMoreButton(false);
                                        listBottomEndReached = true;
                                    }

                                    if (!answerList.isEmpty() && !response.body().data.qas.answers.isEmpty()) {
                                        response.body().data.qas.answers.remove(0);
                                    }

                                    if (target != null && target.equals("0")) {
                                        for (int i = 0; i < response.body().data.qas.answers.size(); i++) {
                                            if (response.body().data.qas.answers.get(i).sessionsQaId.equals(top_answer_id)) {
                                                response.body().data.qas.answers.remove(i);
                                                break;
                                            }
                                        }
                                    }
                                }
                            /*    pb_loading_previous.setVisibility(View.INVISIBLE);
                                tv_load_previous.setVisibility(View.VISIBLE);*/
                               /* if (!answerList.isEmpty()) {
                                    answerList.remove(answerList.size() - 1);
                                }*/
                                //Collections.reverse(response.body().data.qas.answers);
                                answerList.addAll(removeReportedAnswers(response.body().data.qas.answers));
                            }

                            answerListAdapter.setShowProgressBar(false);
                            answerListAdapter.notifyDataSetChanged();
                           /* if (scrollToEnd) {
                                if (*//*target_reply_id.equals("0") && *//*order.equals("prev")) {
                                    rv_answer_list.scrollToPosition(answerList.size());

                                } else {
                                    rv_answer_list.scrollToPosition(answerList.size() + 1);
                                }
                            }*/
                            setDataOnViews();
                            answerListAdapter.setCreator_id(response.body().data.qas.userId);
                            answerListAdapter.notifyDataSetChanged();

                            commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                    }
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    pb_loading_content.setVisibility(View.GONE);
                    isAnswerListApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoGetQuestionDetailsResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // ll_parent.setVisibility(View.VISIBLE);
                    pb_loading_content.setVisibility(View.GONE);
                    isAnswerListApiCalled = false;


                }
            });
        }

    }

    public void showMessageDialog(String message) {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(QuestionActivity.this);
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

    private List<PojoAnswer> removeReportedAnswers(List<PojoAnswer> answers) {
        List<PojoAnswer> moddedList = new ArrayList<>();
        for (int i = 0; i < answers.size(); i++) {
            if (answers.get(i).iReported == null || answers.get(i).iReported == 0) {
                moddedList.add(answers.get(i));
            }
        }
        return moddedList;
    }

    private void setDataOnViews() {
        invalidateOptionsMenu();

        SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(questionData.qas.post,
                questionData.qas.post, false, null);
        questionData.qas.clickableSpansList = spannableClickItem.clickableSpansList;

        if (action != null && action.equals(AppKeys.ADD_ANSWER)) {
            action = null;
            openAddAnswerDialog();
        }
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (questionData != null) {
            if (questionData.qas.userId.equals(preferenceUtils.get_user_id())) {
                getMenuInflater().inflate(R.menu.question_page_asker_menu, menu);
            } else {
                getMenuInflater().inflate(R.menu.question_page_other_menu, menu);
                if (questionData.qas.iFollow) {
                    menu.getItem(0).setTitle(R.string.following);
                    menu.getItem(0).setIcon(R.drawable.ic_follow_filled);
                } else {
                    menu.getItem(0).setTitle(R.string.follow);
                    menu.getItem(0).setIcon(R.drawable.ic_follow_unfilled);

                }

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
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rl_comment_bar.getWindowToken(), 0);
                rl_comment_bar.setVisibility(View.GONE);
                finish();
                break;
            case R.id.edit:
                openEditQuestionDialog(questionData.qas.post);
                break;
            case R.id.delete:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(QuestionActivity.this);
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
                        hitDeleteQuestionApi(user_id, question_id, "delete_question");
                        Intent pushNotification = new Intent(Config.QUESTION_DELETED);
                        pushNotification.putExtra(AppKeys.QUESTION_ID, question_id);
                        LocalBroadcastManager.getInstance(QuestionActivity.this).sendBroadcast(pushNotification);
                        finish();

                    }
                });
                confirmationDialog.show();
                break;
            case R.id.share:

                shareTextUrl();
                break;

            case R.id.request_answer:
                Intent inviteIntent = new Intent(QuestionActivity.this, InviteActivity.class);
                inviteIntent.putExtra("userType", "ans_invitation_user_list");
                inviteIntent.putExtra("id", question_id);
                startActivity(inviteIntent);
                break;
            case R.id.report:
                commonFunctions.openReportDialog(user_id, "report_question", question_id);
                break;
            case R.id.follow:
                if (questionData.qas.iFollow) {
                    item.setTitle(R.string.follow);
                    hitQuestionNotifyApi(user_id, question_id, "remove_question_notify");
                } else {
                    item.setTitle(R.string.following);
                    hitQuestionNotifyApi(user_id, question_id, "question_notify");
                }
                questionData.qas.iFollow = !questionData.qas.iFollow;
                break;
            case R.id.share_to:
                shareTextUrl();
                break;

        }
        return true;
    }


    private void openUpvoterListDialog(String id,String type) {
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

    private void openEditQuestionDialog(String data) {
        final Dialog dialog = new MyCustomThemeDialog(QuestionActivity.this);
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        et_content.setText(data);
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_content.getText().toString().isEmpty()) {
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_data),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    hitEditQuestionApi(user_id, "question", questionData.qas.sessionsQaId,
                            et_content.getText().toString().trim());
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    dialog.dismiss();
                    questionData.qas.post = et_content.getText().toString().trim();
                    answerListAdapter.notifyDataSetChanged();
                }
            }
        });
        dialog.show();
    }


    private void openAddAnswerDialog() {
        rl_comment_bar.setVisibility(View.VISIBLE);
        if (et_comment.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_comment, InputMethodManager.SHOW_FORCED);
        }
    }


    private void hitEditQuestionApi(String user_id, String type, String sessionsQaId, String newData) {
        Call<PojoEditQaResponse> call = apiService.editQa(user_id, type, sessionsQaId, newData);
        call.enqueue(new Callback<PojoEditQaResponse>() {
            @Override
            public void onResponse(Call<PojoEditQaResponse> call, Response<PojoEditQaResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        questionData.qas.post = response.body().data.post;
                        answerListAdapter.notifyDataSetChanged();
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoEditQaResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });

    }


    private void hitAddAnswerApi(String user_id, String type, final String question_id,
                                 String session_id, String comment_post) {
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type,
                session_id, "", comment_post, "", question_id);
        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call,
                                   Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    if (response.body().data.id != null && response.body().data.time != null) {
                        for (int i = answerList.size() - 1; i >= 0; i--) {
                            if (answerList.get(i).sessionsQaId.isEmpty()) {
                                answerList.get(i).sessionsQaId = String.valueOf(response.body().data.id);
                                answerList.get(i).addedOn = response.body().data.time;
                                answerListAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                    addedAnswer = true;
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,
                        AppKeys.TOAST_USER);
                if (answerList != null) {
                    et_comment.setText(answerList.get(answerList.size() - 1).post);
                    for (int i = answerList.size() - 1; i >= 0; i--) {
                        if (answerList.get(i).sessionsQaId.isEmpty()) {
                            answerList.remove(i);
                        }
                    }
                }
                answerListAdapter.notifyDataSetChanged();
                rl_comment_bar.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hitUpvoteQuestionApi(String user_id, String type, final String question_id, String session_id) {
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type,
                session_id, "", "", "", question_id);
        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call
                    , Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // hitGetQuestionDetailsApi(preferenceUtils.get_user_id(), question_id, "question");
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void shareTextUrl() {
        ShareContentOnMediaApps.shareContent(QuestionActivity.this,
                AppKeys.SHARE_QUESTION, question_id, questionData.qas.post, null);
    }


    private void hitQuestionNotifyApi(String user_id, String id, String reaction) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, reaction);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }


    private void hitDeleteQuestionApi(String user_id, String content_id, String reaction) {
        Call<PojoReportContentResponse> call = apiService.postReaction(user_id, content_id, reaction);
        call.enqueue(new Callback<PojoReportContentResponse>() {
            @Override
            public void onResponse(Call<PojoReportContentResponse> call, Response<PojoReportContentResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    finish();
                    Intent questionDeleteIntent = new Intent(Config.QUESTION_CHANGED);
                    questionDeleteIntent.putExtra("type", "delete");
                    questionDeleteIntent.putExtra("id", question_id);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(questionDeleteIntent);
                    if (response.body().data != null) {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoReportContentResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private String getLatestAnswerId() {
        if (!answerList.isEmpty()) {
            return answerList.get(answerList.size() - 1).sessionsQaId;
        } else {
            return "0";
        }
    }

    private String getOldestAnswerId() {
        if (!answerList.isEmpty()) {
            return answerList.get(0).sessionsQaId;
        } else {
            return "0";
        }
    }


    @Override
    public void onBackPressed() {
        if (rl_comment_bar.getVisibility() == View.VISIBLE) {
            rl_comment_bar.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void answerListItemClickMethod(final int position, String type) {
        switch (type) {
            case AppKeys.LIKE:
                if (answerList.get(position).iLike) {
                    hitUpvoteAnswerApi(user_id, "unlike_qacomment", answerList.get(position).sessionsId,
                            answerList.get(position).sessionsQaId, position);
                    answerList.get(position).totalLikes--;


                } else {
                    hitUpvoteAnswerApi(user_id, "like_qacomment", answerList.get(position).sessionsId,
                            answerList.get(position).sessionsQaId, position);
                    answerList.get(position).totalLikes++;

                }
                answerList.get(position).iLike = !answerList.get(position).iLike;
                answerListAdapter.notifyDataSetChanged();
                break;

            case AppKeys.EDIT:
                openEditAnswerDialog(position);
                break;

            case AppKeys.ANSWER_SHARED:
                openShareAnswerOnProfileDialog(answerList.get(position));
                break;

            case AppKeys.LIKE_COUNT:
                openUpvoterListDialog(answerList.get(position).sessionsQaId,"answerreply");
                break;

            case AppKeys.SHARE_TO_ANSWER:
                ShareContentOnMediaApps.shareContent(QuestionActivity.this,
                        AppKeys.SHARE_ANSWER, answerList.get(position).parentQuestionId + "/"
                                + answerList.get(position).sessionsQaId, questionData.qas.post, null);
                break;

            case AppKeys.DELETE:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(QuestionActivity.this);
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
                        hitDeleteAnswerApi(user_id, answerList.get(position).sessionsQaId);
                        answerList.remove(position);
                        answerListAdapter.notifyDataSetChanged();
                        confirmationDialog.dismiss();

                    }
                });
                confirmationDialog.show();
                break;

            case AppKeys.REPORT_ANSWER:
                openReportDialog(position, user_id, "report_answer", answerList.get(position).sessionsQaId);
                break;


            case AppKeys.LOAD_NEXT:
                if (!listBottomEndReached) {
                    hitGetQuestionDetailsApi(preferenceUtils.get_user_id(), question_id, "question",
                            sortBy, "prev", getLatestAnswerId(), "0", false);
                }
                break;

            case AppKeys.LOAD_PREVIOUS:
                if (!listTopEndReached) {
                    hitGetQuestionDetailsApi(preferenceUtils.get_user_id(), question_id, "question",
                            sortBy, "next", getOldestAnswerId(), "0", false);
                }
        }
    }


    @Override
    public void questionItemClickMethod(int poistion, String type) {
        switch (type) {
            case AppKeys.LIKE:
                if (questionData != null) {
                    int likes = Integer.parseInt(questionData.qas.totalLikes);
                    if (questionData.qas.iLike) {
                        hitUpvoteQuestionApi(user_id, "unlike_qacomment", question_id,
                                questionData.qas.sessionsId);
                        likes--;

                    } else {
                        hitUpvoteQuestionApi(user_id, "like_qacomment", question_id,
                                questionData.qas.sessionsId);
                        likes++;
                    }
                    questionData.qas.totalLikes = String.valueOf(likes);
                    questionData.qas.iLike = !questionData.qas.iLike;
                    answerListAdapter.notifyDataSetChanged();
                    setDataOnViews();
                }
                break;

            case AppKeys.QUESTION_SHARE:
                openShareQuestionOnProfileDialog();

                break;

            case AppKeys.LIKE_COUNT:
                openUpvoterListDialog(questionData.qas.sessionsQaId,"answerreply");
                break;

            case AppKeys.ADD_ANSWER:
                openAddAnswerDialog();
                break;
        }
    }


    public void openReportDialog(final int position, final String user_id, final String type, final String id) {
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
                dialog.dismiss();
            }
        });

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                commonFunctions.hitReportContentApi(user_id, type, id, et_content.getText().toString().trim());
                answerList.remove(position);
                answerListAdapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void openShareAnswerOnProfileDialog(final PojoAnswer pojoAnswer) {
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

        tv_question.setText(Html.fromHtml(questionData.qas.post));
        Glide.with(getApplicationContext())
                .load(pojoAnswer.userData.userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_answer_dp);
        tv_answer_user_name.setText(pojoAnswer.userData.userFullname);
        tv_answer_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(pojoAnswer.addedOn));
        tv_answer.setText(Html.fromHtml(pojoAnswer.post));
        tv_num_upvotes.setText("" + pojoAnswer.totalLikes + " " + getString(R.string.upvotes));
        tv_num_replies.setText("" + pojoAnswer.total_replies+ " " + getString(R.string.replies));

        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitShareContentApi(user_id, pojoAnswer.sessionsQaId, "share_answer",
                        et_share_title.getText().toString().trim());
                shareDialog.dismiss();
            }
        });
        shareDialog.show();
    }


    private void hitUpvoteAnswerApi(String user_id, final String type, String sessionsId,
                                    String sessionsQaId, final int position) {
        Call<PojoAddSessionDiscussionResponse> call = apiService.sessionConnects(user_id, type,
                sessionsId, "", "", "", sessionsQaId);
        call.enqueue(new Callback<PojoAddSessionDiscussionResponse>() {
            @Override
            public void onResponse(Call<PojoAddSessionDiscussionResponse> call,
                                   Response<PojoAddSessionDiscussionResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoAddSessionDiscussionResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void openEditAnswerDialog(final int position) {
        final Dialog dialog = new MyCustomThemeDialog(QuestionActivity.this);
        dialog.setContentView(R.layout.edit_content_dialog);

        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);

        et_content.setText(Html.fromHtml(answerList.get(position).post));

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
                    commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_data),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                } else {
                    hitEditAnswerApi(user_id, answerList.get(position).sessionsQaId,
                            newData);
                    answerList.get(position).post = newData;
                    answerListAdapter.notifyDataSetChanged();
                    InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
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
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.answer_not_edited),
                        Toast.LENGTH_LONG, AppKeys.TOAST_USER);

            }
        });

    }


    private void hitShareContentApi(String user_id, String article_id, String reaction, String title) {
        Call<PojoNoDataResponse> call = apiService.shareContent(user_id, article_id, reaction, title);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.shared_question_successfully),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
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
