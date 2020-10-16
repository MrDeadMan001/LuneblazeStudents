package com.avadna.luneblaze.activities.normalpost;

import android.annotation.SuppressLint;
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
import com.avadna.luneblaze.activities.PostCreationActivity;
import com.avadna.luneblaze.adapters.ShareNewsFeedAdapter;
import com.avadna.luneblaze.adapters.normalPost.NormalPostCommentAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoReportContentResponse;
import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoVenueDetails.PojoVenuePhoto;
import com.avadna.luneblaze.pojo.pojonormalpost.PojoAddPostCommentResponse;
import com.avadna.luneblaze.pojo.pojonormalpost.PojoNormalPostComment;
import com.avadna.luneblaze.pojo.pojonormalpost.PojoNormalPostResponse;
import com.avadna.luneblaze.pojo.pojonormalpost.PojoNormalPostResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class NormalPostActivity extends AppBaseActivity implements
        NormalPostCommentAdapter.NormalPostCommentListAdapterCallback,
        ShareNewsFeedAdapter.ShareNewsFeedAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {

    private static final int EDIT_POST_REQUEST_CODE = 9057;
    private PojoNormalPostResponseData pojoNormalPostResponseData;
    private ActionBar actionBar;
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private String userName;
    private String post_id;
    private String action = ""; //this action variable is used to define action like comment etc
    private String target_comment_id;
    private String top_comment_id;
    private PojoLoginResponseData pojoLoginResponseData;

    private ProgressBar pb_loading_content;

    private RecyclerView rv_upvoters;
    private LinearLayoutManager upvoterListLayoutManager;
    private UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    private List<PojoUserData> upvoterList;
    private boolean isGetUpvotersApiCalled = false;
    private int upvoterListOffset = 0;

    private RecyclerView rv_comments_list;
    private NormalPostCommentAdapter normalPostCommentAdapter;
    private LinearLayoutManager commentListLayoutManager;
    private List<PojoNormalPostComment> commentList;

    private RelativeLayout rl_bottom_bar;
    private ImageButton ib_send;
    private EditText et_comment;

    private boolean isGetCommentApiCalled = false;

    private View.OnClickListener onClickListener;

    private boolean isSaved = false;
    private boolean isPinned = false;
    private boolean isHidden = false;
    private boolean isDataSetOnViews = false;
    private Menu menu;

    private InputMethodManager imm;

    private TextView tv_load_previous;
    private boolean listBottomEndReached = false;
    private boolean listTopEndReached = false;

    private BroadcastReceiver broadcastReceiver;

    private final int API_COMMMENT_COUNT = 15;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_post);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        userName = preferenceUtils.get_user_name();

        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        post_id = getIntent().getStringExtra(AppKeys.POST_ID);

        imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (post_id == null || post_id.isEmpty()) {
            post_id = "2193";
        }

        action = getIntent().getStringExtra(AppKeys.ACTION);

        setUpActionBar();
        initViews();
        initClickListeners();
        setClickListeners();

        initBroadCastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.POST_COMMENT_DELETED));

        isDataSetOnViews = false;

        Type type = new TypeToken<PojoNormalPostResponse>() {
        }.getType();
        String dataStr = getIntent().getStringExtra(AppKeys.DATA);
        target_comment_id = getIntent().getStringExtra(AppKeys.COMMENT_ID);
        top_comment_id = target_comment_id;

        if (dataStr != null) {
            PojoNormalPostResponse pojoNormalPostResponse = new Gson().fromJson(dataStr, type);
            String order = "prev";
            if (target_comment_id == null || target_comment_id.isEmpty()) {
                top_comment_id = "0";
                handleApiResponse(pojoNormalPostResponse, "0", order, "0", false);
            } else {
                handleApiResponse(pojoNormalPostResponse, target_comment_id, order, "1", false);
            }
            pb_loading_content.setVisibility(View.GONE);
        } else {
            if (target_comment_id == null || target_comment_id.isEmpty()) {
                top_comment_id = "0";
                hitGetNormalPostApi(user_id, post_id, "0", "prev", "0", false);
            } else {
                hitGetNormalPostApi(user_id, post_id, target_comment_id, "prev", "1", false);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

   /* @Override
    public void onResume() {
        super.onResume();
        target_comment_id = "0";
        hitGetNormalPostApi(user_id, post_id, target_comment_id, "prev", true);
    }*/


    private void initBroadCastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.POST_COMMENT_DELETED)) {
                    String comment_id = intent.getStringExtra(AppKeys.COMMENT_ID);
                    if (commentList != null) {
                        for (int i = 0; i < commentList.size(); i++) {
                            if (commentList.get(i).commentId.equals(comment_id)) {
                                commentList.remove(i);
                                normalPostCommentAdapter.notifyDataSetChanged();
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
            actionBar.setTitle(getString(R.string.post));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(NormalPostActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(NormalPostActivity.this, R.color.status_bar_color));
            }
        }
    }


    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.ib_send:
                        if (!et_comment.getText().toString().trim().isEmpty()) {
                            String message = et_comment.getText().toString().trim();
                            if (!message.isEmpty()) {
                                hitAddCommentApi(user_id, "post", post_id, message);
                                PojoNormalPostComment tempComment = new PojoNormalPostComment();
                      /*          Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                tempComment.time = mdformat.format(calendar.getTime());*/

                                tempComment.time = getString(R.string.just_now);
                                tempComment.userFullname = userName;
                                tempComment.userId = user_id;
                                tempComment.authorPicture = pojoLoginResponseData.userPicture;
                                tempComment.text = et_comment.getText().toString().trim();
                                tempComment.commentId = "";
                                tempComment.nodeId = post_id;
                                tempComment.iLike = false;
                                tempComment.likes = "0";
                                tempComment.replies = "0";

                                commentList.add(0, tempComment);
                                normalPostCommentAdapter.notifyDataSetChanged();
                                rv_comments_list.scrollToPosition(1);
                                et_comment.getText().clear();


                                imm.hideSoftInputFromWindow(et_comment.getWindowToken(), 0);

                                int numComments = Integer.parseInt(pojoNormalPostResponseData.post.comments);
                                numComments++;
                                pojoNormalPostResponseData.post.comments = String.valueOf(numComments);
                            }
                        }
                        break;

                }
            }


        };
    }


    @Override
    public void newsFeedItemClickCallback(int position, PojoGetNewsFeedResponseData currentItem, String type) {

        if (user_id.equals(AppKeys.GUEST_USER_ID)) {
            switch (type) {
                case AppKeys.PHOTO_CLICK:
                    if (pojoNormalPostResponseData != null) {
                        openPhotoViewerActivity(position);
                    }
                case AppKeys.POST_LIKE:
                default:
                    commonFunctions.showLoginRequestDialog();
                    break;
            }
        } else {
            switch (type) {
                case AppKeys.POST_LIKE:
                    int likes = pojoNormalPostResponseData.post.totalLikes;
                    if (pojoNormalPostResponseData.post.iLike == 1) {
                        hitPostReactionApi(user_id, post_id, "unlike_post");
                        pojoNormalPostResponseData.post.iLike = 0;
                        likes--;

                    } else {
                           /* for(int i=1;i<30;i++){
                                hitPostReactionApi(String.valueOf(i), post_id, "like_post");
                            }*/
                        hitPostReactionApi(user_id, post_id, "like_post");
                        pojoNormalPostResponseData.post.iLike = 1;
                        likes++;
                    }
                    pojoNormalPostResponseData.post.totalLikes = likes;
                    normalPostCommentAdapter.notifyDataSetChanged();
                    break;

                case AppKeys.LIKE_COUNT:
                    openUserListDialog(pojoNormalPostResponseData.post.postId, "post");
                    break;

                case AppKeys.BOOST_POST:

                    break;

                case AppKeys.POST_SHARE:
                    openShareDialog("share", pojoNormalPostResponseData.post);

                    break;

                case AppKeys.COMMENT:
                    openCommentBar();
                    break;

                case AppKeys.PHOTO_CLICK:
                    if (pojoNormalPostResponseData != null) {
                        openPhotoViewerActivity(position);
                    }
                    break;
            }
        }
    }

    private void openCommentBar() {
        if (user_id.equals(AppKeys.GUEST_USER_ID)) {
            commonFunctions.showLoginRequestDialog();
        } else {
            rl_bottom_bar.setVisibility(View.VISIBLE);
            et_comment.requestFocus();
            imm.showSoftInput(et_comment, InputMethodManager.SHOW_FORCED);
        }
    }

    private void openPhotoViewerActivity(int position) {
        Type type = new TypeToken<List<PojoVenuePhoto>>() {
        }.getType();
        String dataStr = new Gson().toJson(pojoNormalPostResponseData.post.photos, type);
        Intent imgViewerIntent = new Intent(NormalPostActivity.this,
                NormalPostPhotoGalleryViewerActivity.class);
        imgViewerIntent.putExtra("data", dataStr);
        imgViewerIntent.putExtra("position", position);
        startActivity(imgViewerIntent);
    }

    private void setClickListeners() {
        ib_send.setOnClickListener(onClickListener);

    }

    private void initViews() {
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        rl_bottom_bar = (RelativeLayout) findViewById(R.id.rl_bottom_bar);
        rl_bottom_bar.setVisibility(View.GONE);
        ib_send = (ImageButton) findViewById(R.id.ib_send);
        et_comment = (EditText) findViewById(R.id.et_comment);
        et_comment.setFilters(new InputFilter[]{new InputFilter.LengthFilter(AppKeys.COMMENT_AND_REPLY_MAX_LENGTH)});

        initCommentsList();

     /*   tv_load_previous = (TextView) findViewById(R.id.tv_load_previous);
        tv_load_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitGetNormalPostApi(user_id, post_id, getOldestCommentId(), "prev", true);

                pb_loading_previous.setVisibility(View.VISIBLE);
                tv_load_previous.setVisibility(View.INVISIBLE);
            }
        });
        pb_loading_previous = (ProgressBar) findViewById(R.id.pb_loading_previous);
        pb_loading_previous.setVisibility(View.INVISIBLE);*/
    }

    private void initCommentsList() {
        commentList = new ArrayList<>();
        rv_comments_list = (RecyclerView) findViewById(R.id.rv_comments_list);
        commentListLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_comments_list.setLayoutManager(commentListLayoutManager);
        normalPostCommentAdapter = new NormalPostCommentAdapter(this,
                commentList, "comment");
        rv_comments_list.setAdapter(normalPostCommentAdapter);

        /*nsv_parent.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (v.getChildAt(v.getChildCount() - 1) != null) {
                        if ((scrollY >= (v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight())) &&
                                scrollY > oldScrollY) {
                            if (!listBottomEndReached) {
                                if (commentListLayoutManager.findLastVisibleItemPosition() > commentList.size() - 3) {
                                    hitGetNormalPostApi(user_id, post_id, getOldestCommentId(),
                                            "prev", "0", true);
                                }
                            }
                            //code to fetch more data for endless scrolling
                        }
                    }
                });*/

        rv_comments_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!listBottomEndReached && dy > 4) {
                    if (commentListLayoutManager.findLastVisibleItemPosition() > commentList.size() - 3) {
                        hitGetNormalPostApi(user_id, post_id, getOldestCommentId(),
                                "prev", "0", false);
                    }
                }/* else if (dy < -4) {
                    if (commentListLayoutManager.findFirstVisibleItemPosition() < 2) {
                        hitGetSessionComments(user_id, sessionId, "comments", "prev",
                                getOldestCommentId(), false);
                    }
                }*/
            }
        });

    }

    private void hitGetNormalPostApi(String user_id, final String post_id, final String comment_id,
                                     final String order, String target, final boolean scrollToEnd) {
        Call<PojoNormalPostResponse> call = apiService.getNormalPostData(user_id, post_id,
                comment_id, order, target);
        if (!isGetCommentApiCalled) {
            normalPostCommentAdapter.setShowProgressBar(true);
            normalPostCommentAdapter.notifyDataSetChanged();
            isGetCommentApiCalled = true;
            call.enqueue(new Callback<PojoNormalPostResponse>() {
                @Override
                public void onResponse(Call<PojoNormalPostResponse> call, Response<PojoNormalPostResponse> response) {
                    if (response != null && response.body() != null && response.body().data != null) {
                        handleApiResponse(response.body(), comment_id, order, target, scrollToEnd);

                        // tv_dialog_comment_number.setText(response.body().data.commentsCnt + " " + getString(R.string.comments));
                    }
                    isGetCommentApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoNormalPostResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    // rl_parent.setVisibility(View.VISIBLE);
                    isGetCommentApiCalled = false;

                    if (commentList.isEmpty()) {
                        openNoConnectionDialog(getString(R.string.post), comment_id);
                    }
                    //   ll_content_wrapper.setVisibility(View.VISIBLE);

                }
            });
        }
    }

    private void openNoConnectionDialog(String title, String comment_id) {

        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetNormalPostApi(user_id, post_id, comment_id, "prev", "1", false);
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


    public void handleApiResponse(PojoNormalPostResponse body, String comment_id, String order,
                                  String target, boolean scrollToEnd) {
        String message = "";
        message = body.message;
        if (body.data.post.postId.isEmpty()) {
            commonFunctions.showNotFoundDialog(NormalPostActivity.this);
        } else {
            pb_loading_content.setVisibility(View.GONE);
            pojoNormalPostResponseData = body.data;

            invalidateOptionsMenu();
            if (action != null && action.equals(AppKeys.COMMENT)) {
                action = null;
                openCommentBar();
            }

            if (body.status == 1 && body.data.comments != null) {
                normalPostCommentAdapter.setPostData(pojoNormalPostResponseData.post);
                if (target_comment_id != null) {
                    boolean present = false;
                    for (int i = 0; i < body.data.comments.size(); i++) {
                        if (target_comment_id.equals(body.data.comments.get(i).commentId)) {
                            present = true;
                            target_comment_id = null;
                            break;
                        }
                    }

                    if (!present) {
                        showMessageDialog(getString(R.string.the_comment_has_been_deleted));
                        target_comment_id = null;
                    }
                }

                if (comment_id.equals("0")) {
                    commentList.clear();
                }
                if (order.equals("next")) {
                    Collections.reverse(body.data.comments);

                    if (body.data.comments.size() < API_COMMMENT_COUNT) {
                        normalPostCommentAdapter.setMoreNextAvailable(false);
                        listBottomEndReached = true;
                    }

                    if (!comment_id.equals("0") && !commentList.isEmpty()) {
                        body.data.comments.remove(0);
                    }

                              /*  if (!commentList.isEmpty()) {
                                    commentList.remove(commentList.size() - 1);
                                }*/
                    commentList.addAll(commonFunctions.removeReportedPostComments(body.data.comments));
                } else if (order.equals("prev")) {

                    if (body.data.morePresent == 0) {
                        normalPostCommentAdapter.setMoreNextAvailable(false);
                        normalPostCommentAdapter.setShowBottomMoreButton(false);
                    }

                    if (body.data.loadMore == 0) {
                        normalPostCommentAdapter.setMorePreviousAvailable(false);
                        normalPostCommentAdapter.setShowTopMoreButton(false);
                    }

                    if (comment_id.equals("0")) {
                        if (body.data.comments.size() < API_COMMMENT_COUNT) {
                            normalPostCommentAdapter.setMorePreviousAvailable(false);
                            normalPostCommentAdapter.setShowTopMoreButton(false);
                            normalPostCommentAdapter.setShowBottomMoreButton(false);
                            listTopEndReached = true;
                        } else {
                            normalPostCommentAdapter.setMoreNextAvailable(false);
                            normalPostCommentAdapter.setShowBottomMoreButton(false);
                        }

                    } else {
                        if (body.data.comments.size() < API_COMMMENT_COUNT) {
                            normalPostCommentAdapter.setMorePreviousAvailable(false);
                            normalPostCommentAdapter.setShowTopMoreButton(false);
                            listTopEndReached = true;
                        }

                        if (!commentList.isEmpty() && !body.data.comments.isEmpty()) {
                            body.data.comments.remove(0);
                        }

                        if (target != null && target.equals("0")) {
                            for (int i = 0; i < body.data.comments.size(); i++) {
                                if (body.data.comments.get(i).commentId.equals(top_comment_id)) {
                                    body.data.comments.remove(i);
                                    break;
                                }
                            }
                        }
                    }
                            /*    pb_loading_previous.setVisibility(View.INVISIBLE);
                                tv_load_previous.setVisibility(View.VISIBLE);*/
                               /* if (!commentList.isEmpty()) {
                                    commentList.remove(0);
                                }*/
                    commentList.addAll(commonFunctions.removeReportedPostComments(body.data.comments));
                }

                normalPostCommentAdapter.setCreator_id(body.data.post.authorId);
                normalPostCommentAdapter.setShowProgressBar(false);

                normalPostCommentAdapter.notifyDataSetChanged();
                if (scrollToEnd) {
                    if (/*target_reply_id.equals("0") && */order.equals("prev")) {
                        rv_comments_list.scrollToPosition(commentList.size());

                    } else {
                        rv_comments_list.scrollToPosition(commentList.size() + 1);

                    }
                }


            } else {

                commonFunctions.setToastMessage(getApplicationContext(), message,
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }

        }
    }

    public void showMessageDialog(String message) {
        TextView tv_dialog_description;
        Button bt_ok;
        final Dialog confirmationDialog;
        confirmationDialog = new MyCustomThemeDialog(NormalPostActivity.this);
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


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        if (pojoNormalPostResponseData != null) {
            if (pojoNormalPostResponseData.post.authorId.equals(user_id)) {
                getMenuInflater().inflate(R.menu.normal_post_side_options_creator, menu);
            } else {
                getMenuInflater().inflate(R.menu.normal_post_side_options_other, menu);

            }
        }
        if(user_id.equals(AppKeys.GUEST_USER_ID)){
            menu.clear();
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
                Intent postCreationIntent = new Intent(NormalPostActivity.this, PostCreationActivity.class);
                String dataStr = new Gson().toJson(
                        pojoNormalPostResponseData.post,
                        new TypeToken<PojoGetNewsFeedResponseData>() {
                        }.getType());
                postCreationIntent.putExtra("data", dataStr);
                startActivityForResult(postCreationIntent, EDIT_POST_REQUEST_CODE);
                break;

            case R.id.delete:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(NormalPostActivity.this);
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
                        hitPostReactionApi(user_id, post_id, "delete_post");
                        confirmationDialog.dismiss();
                        finish();
                    }
                });
                confirmationDialog.show();
                break;

            case R.id.share_to:
                ShareContentOnMediaApps.shareContent(NormalPostActivity.this, AppKeys.SHARE_TO_POST,
                        post_id, getString(R.string.check_this_on_luneblaze), null);

                break;
/*

            case R.id.save:
                if (isSaved) {
                    hitPostReactionApi(user_id, post_id, "unsave_post");
                    isSaved = false;
                    item.setTitle(R.string.save);
                } else {
                    hitPostReactionApi(user_id, post_id, "save_post");
                    isSaved = true;
                    item.setTitle(R.string.unsave);
                }
                break;

            case R.id.pin:
                if (isPinned) {
                    hitPostReactionApi(user_id, post_id, "unpin_post");
                    isPinned = false;
                    item.setTitle(R.string.pin);
                } else {
                    hitPostReactionApi(user_id, post_id, "pin_post");
                    isPinned = true;
                    item.setTitle(R.string.unpin);
                }
                break;*/
/*
            case R.id.hide:
                if (isHidden) {
                    hitPostReactionApi(user_id, post_id, "unhide_post");
                    isHidden = false;
                    item.setTitle(R.string.hide);
                } else {
                    hitPostReactionApi(user_id, post_id, "hide_post");
                    isHidden = true;
                    item.setTitle(R.string.unhide);
                }
                break;*/


            case R.id.report:
                commonFunctions.openReportDialog(user_id, "report_post", post_id);

                break;
        }
        return true;
    }


    private void hitPostReactionApi(String user_id, String post_id, String reaction) {
        Call<PojoReportContentResponse> call = apiService.postReaction(user_id, post_id, reaction);
        call.enqueue(new Callback<PojoReportContentResponse>() {
            @Override
            public void onResponse(Call<PojoReportContentResponse> call, Response<PojoReportContentResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoReportContentResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }


    private void hitLikeUnlikePostComment(String user_id, String comment_id, String reaction) {
        Call<PojoNoDataResponse> call = apiService.likeNormalPostComment(user_id, comment_id, reaction);
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

    private void hitAddCommentApi(final String user_id, String handle, final String post_id, final String message) {
        Call<PojoAddPostCommentResponse> call = null;

        call = apiService.addCommentOnPost(user_id, handle, post_id, message);

        call.enqueue(new Callback<PojoAddPostCommentResponse>() {
            @Override
            public void onResponse(Call<PojoAddPostCommentResponse> call, Response<PojoAddPostCommentResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    for (int i = 0; i < commentList.size(); i++) {
                        if (commentList.get(i).commentId.isEmpty()) {
                            commentList.get(i).commentId = String.valueOf(response.body().data.commentId);
                            commentList.get(i).time = response.body().data.time;
                            normalPostCommentAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    //hitGetNormalPostApi(user_id, post_id, "0", "prev", true);

                    commonFunctions.setToastMessage(getApplicationContext(),
                            "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoAddPostCommentResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(),
                        getString(R.string.comment_not_added), Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                et_comment.setText(message);
                if (commentList != null) {
                    for (int i = commentList.size() - 1; i >= 0; i--) {
                        if (commentList.get(i).commentId.isEmpty()) {
                            commentList.remove(i);
                        }
                    }
                }
                normalPostCommentAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void normalPostCommentListItemClickCallback(final int position, final String type) {
        switch (type) {
            case AppKeys.EDIT:
                openEditDialog(position);
                break;

            case AppKeys.DELETE:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(NormalPostActivity.this);
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
                        hitDeletePostCommentApi(user_id, commentList.get(position).commentId);
                        commentList.remove(position);
                        normalPostCommentAdapter.notifyDataSetChanged();
                        confirmationDialog.dismiss();

                    }
                });
                confirmationDialog.show();
                break;

            case AppKeys.REPORT_COMMENT:
                openReportDialog(position, user_id, "report_comment", commentList.get(position).commentId);
                break;

            case AppKeys.LIKE:
                int likes = Integer.parseInt(commentList.get(position).likes);

                if (commentList.get(position).iLike) {
                    hitLikeUnlikePostComment(user_id, commentList.get(position).commentId, "unlike_comment");
                    likes--;

                } else {
                    hitLikeUnlikePostComment(user_id, commentList.get(position).commentId, "like_comment");
                    likes++;

                }
                commentList.get(position).likes = String.valueOf(likes);

                commentList.get(position).iLike = !commentList.get(position).iLike;
                normalPostCommentAdapter.notifyDataSetChanged();
                break;

            case AppKeys.LIKE_COUNT:
                openUserListDialog(commentList.get(position).commentId, "commentreply");
                break;

            case AppKeys.LOAD_NEXT:
                if (!listBottomEndReached) {
                    hitGetNormalPostApi(user_id, post_id, getLatestCommentId(), "next", "0", false);
                }
                break;

            case AppKeys.LOAD_PREVIOUS:
                if (!listTopEndReached) {
                    hitGetNormalPostApi(user_id, post_id, getOldestCommentId(), "prev", "0", false);
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
                hitReportContentApi(user_id, type, id, et_content.getText().toString().trim());
                commentList.remove(position);
                normalPostCommentAdapter.notifyDataSetChanged();

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (et_content.requestFocus()) {
                    imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                }

                dialog.dismiss();


            }
        });
        dialog.show();
    }


    public void hitReportContentApi(String user_id, String report_type, String content_id, String reason) {
        Call<PojoNoDataResponse> call = apiService.reportContent(user_id, report_type, content_id, reason);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    if (NormalPostActivity.this != null) {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
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
        final Dialog dialog = new MyCustomThemeDialog(this);
        dialog.setContentView(R.layout.edit_content_dialog);
        final EditText et_content = (EditText) dialog.findViewById(R.id.et_content);
        TextView tv_done = (TextView) dialog.findViewById(R.id.tv_done);
        TextView tv_cancel = (TextView) dialog.findViewById(R.id.tv_cancel);
        et_content.setText(Html.fromHtml(commentList.get(position).text));
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
                    if (commentList.get(position).commentId != null) {
                        hitEditCommentApi(user_id, "comment", commentList.get(position).commentId,
                                newData);
                        commentList.get(position).text = newData;
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                        //tv_question.setText(response.body().data.post);
                        normalPostCommentAdapter.notifyDataSetChanged();
                    }
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
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

    private String getLatestCommentId() {
        if (!commentList.isEmpty()) {
            return commentList.get(0).commentId;
        } else {
            return "0";
        }
    }

    private String getOldestCommentId() {
        if (!commentList.isEmpty()) {
            return commentList.get(commentList.size() - 1).commentId;
        } else {
            return "0";
        }
    }

    public void openShareDialog(final String type, final PojoGetNewsFeedResponseData currentItem) {
        TextView tv_username;
        ImageView iv_user_dp;
        final EditText et_share_title;
        RecyclerView rv_share_item;
        Button tv_share_button;
        final Dialog shareDialog;
        shareDialog = new MyCustomThemeDialog(this);
        shareDialog.setContentView(R.layout.share_dialog_box_layout);
        shareDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv_username = shareDialog.findViewById(R.id.tv_username);
        iv_user_dp = (ImageView) shareDialog.findViewById(R.id.iv_user_dp);
        et_share_title = (EditText) shareDialog.findViewById(R.id.et_share_title);

        PojoLoginResponseData pojoLoginResponseData = preferenceUtils.getUserLoginData();
        tv_username.setText(pojoLoginResponseData.userFullname);

        Glide.with(getApplicationContext())
                .load(pojoLoginResponseData.userPicture)
                .apply(new RequestOptions().override(128, 128))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_user_dp);

        tv_share_button = (Button) shareDialog.findViewById(R.id.tv_share_button);

        List<PojoGetNewsFeedResponseData> shareList = new ArrayList<>();
        shareList.add(currentItem);

        rv_share_item = (RecyclerView) shareDialog.findViewById(R.id.rv_share_item);
        rv_share_item.setLayoutManager(new LinearLayoutManager(this,
                RecyclerView.VERTICAL, false));
        rv_share_item.setAdapter(new ShareNewsFeedAdapter(this, shareList));

        final String share_id;
        if (currentItem.postType.equals("question") || currentItem.postType.equals("question_shared")) {
            share_id = currentItem.origin.sessionsQaId;
        } else if (currentItem.origin != null) {
            if (currentItem.origin.origin != null) {
                share_id = currentItem.origin.originId;
            } else {
                share_id = currentItem.originId;
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


    private void hitShareContentApi(String user_id, String article_id, String reaction, String title) {
        if (title.isEmpty()) {
            title = null;
        }
        Call<PojoNoDataResponse> call = apiService.shareContent(user_id, article_id, reaction, title);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.shared_post_successfully),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);

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

    @Override
    public void shareNewsFeedItemClickCallback(int position, PojoGetNewsFeedResponseData currentItem, String type) {


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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_POST_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                isDataSetOnViews = false;
                target_comment_id = null;
                String dataStr = data.getStringExtra("data");
                PojoGetNewsFeedResponseData newItem = new Gson().fromJson(
                        dataStr, new TypeToken<PojoGetNewsFeedResponseData>() {
                        }.getType());
                pojoNormalPostResponseData.post = newItem;
                invalidateOptionsMenu();
                //   hitGetNormalPostApi(user_id, post_id, "0", "prev", true);
            }
        }

        if (requestCode == AppKeys.BOOST_POST_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                pb_loading_content.setVisibility(View.VISIBLE);
                hitGetNormalPostApi(user_id, post_id, "0", "prev", "0",
                        false);
            }
        }


    }//onActivityResult


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
