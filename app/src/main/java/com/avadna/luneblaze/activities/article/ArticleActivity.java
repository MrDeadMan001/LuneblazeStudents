package com.avadna.luneblaze.activities.article;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;

import com.avadna.luneblaze.adapters.UpvotersAndAttendeesListAdapter;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.avadna.luneblaze.pojo.PojoUpvotersAndAttendeesListResponse;
import com.google.android.material.appbar.AppBarLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.activities.CreateArticleActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoGetSuggestedFriendListResponse;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoReportContentResponse;
import com.avadna.luneblaze.pojo.PojoSuggestedArticlesResponse;
import com.avadna.luneblaze.pojo.PojoSuggestedArticlesResponseData;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestArr;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.bumptech.glide.Glide;
import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.RelatedArticleListAdapter;
import com.avadna.luneblaze.pojo.pojoArticle.PojoGetArticleDetailsResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class ArticleActivity extends AppBaseActivity
        implements TaggedInterestAdapter.TaggedInterestAdapterCallback,
        UpvotersAndAttendeesListAdapter.UpvoterAndAttendeesAdapterCallback {

    private CoordinatorLayout cl_parent;
    private ProgressBar pb_loading_content;
    private ProgressBar pb_loading_related_articles;

    private ActionBar actionBar;
    private String user_id = "";
    private String username = "";
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private PojoLoginResponseData pojoLoginResponseData;

    private AppBarLayout appBarLayout;
    private PojoGetArticleDetailsResponse pojoGetArticleDetailsResponse;

    private RecyclerView rv_interest_tag_list;
    private List<PojoInterestArr> taggedInterestList;
    private TaggedInterestAdapter taggedInterestAdapter;


    private RecyclerView rv_upvoters;
    private LinearLayoutManager upvoterListLayoutManager;
    private UpvotersAndAttendeesListAdapter upvotersAndAttendeesListAdapter;
    private List<PojoUserData> upvoterList;
    private boolean isGetUpvotersApiCalled = false;
    private int upvoterListOffset = 0;

    private List<PojoUserData> sharersList;
    private UpvotersAndAttendeesListAdapter sharerListAdapter;


    private DisplayMetrics displayMetrics;
    private int screenWidth, screenHeight;


    private ProgressBar pb_loading_comments;
    private ProgressBar pb_loading_users;

    private boolean listBottomEndReached = false;
    private boolean listTopEndReached = false;


/*private   List<String> more_options_list;
  private ArrayAdapter<String> spinnerAdapter;
  private Spinner sp_more_options;
  private ImageView iv_more_options;*/


    private RecyclerView rv_other_article_list;
    private List<PojoSuggestedArticlesResponseData> suggestedArticles;
    private RelatedArticleListAdapter relatedArticleListAdapter;

    private TextView tv_article_name, tv_publisher_name, tv_publisher_designation, tv_follow_button;
    private RelativeLayout rl_writer_data_wrapper;
    private ImageView iv_article_image, iv_publisher_dp;
    private TextView tv_publish_time_stamp, tv_article;
    private TextView tv_num_likes, tv_num_comments, tv_num_shares;
    private TextView tv_like_button, tv_comment_button, tv_share_button;


    private View.OnClickListener onClickListener;
    private int statusBarHeight;
    private int actionBarHeight;
    private int navigationBarHeight = 0;

    private String USER = "user";
    private String OTHER = "other";
    private String type = OTHER;

    private String articleId = "";
    private String comment_id;

    private boolean isSaved = false;
    private boolean isPinned = false;
    private boolean writerFollowed = false;

    private Gson gson;
    private String jsonString;

    private String notification_id;

    private final int EDIT_ARTICLE_REQUEST_CODE = 3217;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        preferenceUtils = new PreferenceUtils(ArticleActivity.this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        username = preferenceUtils.get_user_name();
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        setUpActionBar();
        initBarSizes();
        initDispMetrics();
        initViews();
        initLists();
        initClickListener();
        setClickListener();
        setTextChangedListeners();
        initFocusChangeListener();
        setFocusChangeListener();
        articleId = getIntent().getStringExtra("id");
        if (articleId == null) {
            articleId = "30";
        }
        comment_id = getIntent().getStringExtra(AppKeys.COMMENT_ID);
        /*if (comment_id == null || comment_id.isEmpty()) {
            comment_id = "0";
        }*/

        notification_id = getIntent().getStringExtra(AppKeys.NOTIFICATION_ID);
        if (notification_id != null) {
            commonFunctions.hitNotificationSeenApi(user_id, notification_id);
        }

        hitGetArticleDetailsApi(preferenceUtils.get_user_id(), articleId);
        hitGetSuggestedArticles(preferenceUtils.get_user_id(), "articles");


    }


    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(ArticleActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(ArticleActivity.this, R.color.status_bar_color));
            }
            actionBar.setElevation(0);
        }

        appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                float maxRange = appBarLayout.getTotalScrollRange();
                actionBar.setBackgroundDrawable(new ColorDrawable
                        (Color.argb(((int) (25 + (Math.abs(verticalOffset) / maxRange) * 230)), 11, 108, 115)));
                int adjustPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70,
                        getResources().getDisplayMetrics());

                if (verticalOffset < adjustPx - maxRange) {
                    if (pojoGetArticleDetailsResponse != null) {
                        actionBar.setTitle(Html.fromHtml(pojoGetArticleDetailsResponse.data.title));
                    }
                } else {
                    actionBar.setTitle("");
                }
            }
        });
    }

    private void initBarSizes() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        final TypedArray styledAttributes = getTheme().obtainStyledAttributes(
                new int[]{android.R.attr.actionBarSize}
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        // navigation bar height
        resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = getResources().getDimensionPixelSize(resourceId);

        }
    }

    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    }


    private void initViews() {
        cl_parent = (CoordinatorLayout) findViewById(R.id.cl_parent);
        cl_parent.setVisibility(View.GONE);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        pb_loading_related_articles = (ProgressBar) findViewById(R.id.pb_loading_related_articles);
        tv_article_name = (TextView) findViewById(R.id.tv_article_name);
        iv_article_image = (ImageView) findViewById(R.id.iv_article_image);
        tv_publisher_name = (TextView) findViewById(R.id.tv_publisher_name);
        iv_publisher_dp = (ImageView) findViewById(R.id.iv_publisher_dp);
        tv_publisher_designation = (TextView) findViewById(R.id.tv_publisher_designation);
        rl_writer_data_wrapper = (RelativeLayout) findViewById(R.id.rl_writer_data_wrapper);
        tv_follow_button = (TextView) findViewById(R.id.tv_follow_button);
        tv_publish_time_stamp = (TextView) findViewById(R.id.tv_publish_time_stamp);
        tv_article = (TextView) findViewById(R.id.tv_article);
        tv_num_likes = (TextView) findViewById(R.id.tv_num_likes);
        tv_num_comments = (TextView) findViewById(R.id.tv_num_comments);
        tv_num_shares = (TextView) findViewById(R.id.tv_num_shares);
        tv_like_button = (TextView) findViewById(R.id.tv_like_button);
        tv_comment_button = (TextView) findViewById(R.id.tv_comment_button);
        tv_share_button = (TextView) findViewById(R.id.tv_share_button);
    }

    private void initLists() {
        //   initMoreOptionSpinner();
        initMoreArticleList();
        initTaggedInterestList();
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_id.equals(AppKeys.GUEST_USER_ID)) {
                    switch (v.getId()) {
                        case R.id.rl_writer_data_wrapper:
                            Intent profileIntent = new Intent(ArticleActivity.this, ProfileInfoActivity.class);
                            profileIntent.putExtra(AppKeys.TARGET_USER_ID, pojoGetArticleDetailsResponse.data.createdBy.userId);
                            startActivity(profileIntent);
                            break;

                        case R.id.iv_article_image:
                            if (pojoGetArticleDetailsResponse != null) {
                                openImageViewerDialog(pojoGetArticleDetailsResponse.data.coverPhoto);
                            }
                            break;

                        case R.id.tv_like_button:
                        default:
                            commonFunctions.showLoginRequestDialog();
                            break;
                    }
                } else {
                    switch (v.getId()) {
                        case R.id.tv_like_button:
                            if (pojoGetArticleDetailsResponse.data.iLike) {
                                int likes = Integer.parseInt(pojoGetArticleDetailsResponse.data.likes);
                                likes--;
                                pojoGetArticleDetailsResponse.data.likes = "" + likes;
                                tv_num_likes.setText(pojoGetArticleDetailsResponse.data.likes + " " + getString(R.string.likes));
                                hitArticleReactionApi(user_id, articleId, "unlike_article");
                                pojoGetArticleDetailsResponse.data.iLike = false;
                                tv_like_button.setTextColor(getResources().getColor(R.color.med_grey));
                                tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled,
                                        0, 0, 0);
                            } else {
                                int likes = Integer.parseInt(pojoGetArticleDetailsResponse.data.likes);
                                likes++;
                                pojoGetArticleDetailsResponse.data.likes = "" + likes;
                                tv_num_likes.setText(pojoGetArticleDetailsResponse.data.likes + " " + getString(R.string.likes));
                                hitArticleReactionApi(user_id, articleId, "like_article");
                                pojoGetArticleDetailsResponse.data.iLike = true;
                                tv_like_button.setTextColor(getResources().getColor(R.color.app_theme_medium));
                                tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled,
                                        0, 0, 0);
                            }
                            if (pojoGetArticleDetailsResponse.data.likes.equals("0")) {
                                tv_num_likes.setVisibility(View.GONE);
                            } else {
                                tv_num_likes.setVisibility(View.VISIBLE);
                            }
                            break;

                        case R.id.tv_share_button:
                            openShareArticleOnProfileDialog();
                            break;

                        case R.id.rl_writer_data_wrapper:
                            Intent profileIntent = new Intent(ArticleActivity.this, ProfileInfoActivity.class);
                            profileIntent.putExtra(AppKeys.TARGET_USER_ID, pojoGetArticleDetailsResponse.data.createdBy.userId);
                            startActivity(profileIntent);
                            break;

                        case R.id.tv_follow_button:
                            if (pojoGetArticleDetailsResponse.data.createdBy.connection.equals("followed")) {
                                hitUserConnectionApi(user_id, pojoGetArticleDetailsResponse.data.createdBy.userId, "unfollow");
                                pojoGetArticleDetailsResponse.data.createdBy.connection = "add";
                            } else {
                                hitUserConnectionApi(user_id, pojoGetArticleDetailsResponse.data.createdBy.userId, "follow");
                                pojoGetArticleDetailsResponse.data.createdBy.connection = "followed";
                            }
                            setFollowStatus();
                            break;

                        case R.id.tv_comment_button:
                            openArticleCommentListActivity(comment_id, AppKeys.COMMENT);

                            //  openAddCommentDialog();
                            break;

                        case R.id.tv_num_comments:
                            openArticleCommentListActivity(comment_id, "");
                            // openCommentListDialog(target_comment_id);
                            break;

                        case R.id.tv_num_likes:
                            openUpvoterListDialog("article");
                            break;

                        case R.id.tv_num_shares:
                            openSharerListDialog();
                            break;

                        case R.id.iv_article_image:
                            if (pojoGetArticleDetailsResponse != null) {
                                openImageViewerDialog(pojoGetArticleDetailsResponse.data.coverPhoto);
                            }
                            break;
                    }
                }
            }
        };
    }

    private void openImageViewerDialog(String path) {
        final Dialog imageViewerDialog;
        imageViewerDialog = new MyCustomThemeDialog(ArticleActivity.this, android.R.style.Theme_Black_NoTitleBar);
        imageViewerDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        imageViewerDialog.setContentView(R.layout.image_viewer_dialog);
        ImageView iv_back, iv_edit;
        PhotoView iv_photo;
        iv_photo = (PhotoView) imageViewerDialog.findViewById(R.id.iv_photo);

        iv_back = (ImageView) imageViewerDialog.findViewById(R.id.iv_back);
        iv_edit = (ImageView) imageViewerDialog.findViewById(R.id.iv_edit);

        iv_edit.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageViewerDialog.dismiss();
            }
        });
        Glide.with(ArticleActivity.this.getApplicationContext())
                .load(path)
                .into(iv_photo);

        imageViewerDialog.show();


    }


    private void openShareArticleOnProfileDialog() {
        TextView tv_username;
        ImageView iv_user_dp;
        final EditText et_share_title;
        Button tv_share_button;
        final Dialog shareDialog;
        RelativeLayout rl_item_holder;
        shareDialog = new MyCustomThemeDialog(this, R.style.NoTitleDialogTheme);

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
        View view = inflater.inflate(R.layout.share_article_item, rl_item_holder, true);

        ImageView iv_writer_dp = (ImageView) view.findViewById(R.id.iv_user_dp);
        TextView tv_writer_name = (TextView) view.findViewById(R.id.tv_writer_name);
        TextView tv_writer_designation = (TextView) view.findViewById(R.id.tv_writer_designation);
        TextView tv_time_stamp = (TextView) view.findViewById(R.id.tv_time_stamp);

        ImageView iv_cover_photo = (ImageView) view.findViewById(R.id.iv_cover_photo);
        TextView tv_article_name = (TextView) view.findViewById(R.id.tv_article_name);

        Glide.with(getApplicationContext())
                .load(pojoGetArticleDetailsResponse.data.createdBy.userPicture)
                .apply(new RequestOptions().override(96, 96))
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_writer_dp);

        tv_writer_name.setText(pojoGetArticleDetailsResponse.data.createdBy.userFullname);
        tv_writer_designation.setText(pojoGetArticleDetailsResponse.data.createdBy.userWorkTitle);

        int targetWidth = (int) (displayMetrics.widthPixels * 0.85f);
        int targetHeight;

        if (pojoGetArticleDetailsResponse.data.imgDimensions != null && !pojoGetArticleDetailsResponse.data.imgDimensions.isEmpty()) {
            String imgDimen = pojoGetArticleDetailsResponse.data.imgDimensions;
            int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
            int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
            targetHeight = (int) ((1f * height * targetWidth) / width);

        } else {
            targetHeight = (int) (displayMetrics.widthPixels * 0.6f);
        }

        iv_cover_photo.getLayoutParams().height = targetHeight;
        iv_cover_photo.getLayoutParams().width = targetWidth;

        Glide.with(getApplicationContext())
                .load(pojoGetArticleDetailsResponse.data.coverPhoto)
                .apply(new RequestOptions().override(targetWidth, targetHeight))
                .into(iv_cover_photo);


        tv_article_name.setText(Html.fromHtml(pojoGetArticleDetailsResponse.data.title));
        tv_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(pojoGetArticleDetailsResponse.data.addedOn));

        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                hitShareContentApi(user_id, articleId, "share_article", et_share_title.getText().toString().trim());

                shareDialog.dismiss();
            }
        });
        shareDialog.show();

    }


    private void setClickListener() {
        tv_like_button.setOnClickListener(onClickListener);
        tv_share_button.setOnClickListener(onClickListener);
        tv_follow_button.setOnClickListener(onClickListener);
        tv_comment_button.setOnClickListener(onClickListener);
        tv_num_likes.setOnClickListener(onClickListener);
        tv_num_shares.setOnClickListener(onClickListener);
        tv_num_comments.setOnClickListener(onClickListener);
        rl_writer_data_wrapper.setOnClickListener(onClickListener);
        iv_article_image.setOnClickListener(onClickListener);
    }

    private void setTextChangedListeners() {

    }

    private void initFocusChangeListener() {

    }

    private void setFocusChangeListener() {

    }

    private void openUpvoterListDialog(String reaction) {
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
                        hitGetUpvotersListApi(user_id, articleId, reaction, ++upvoterListOffset);
                    }
                }
            }
        });

        upvoterListOffset = 0;
        hitGetUpvotersListApi(user_id, articleId, reaction, upvoterListOffset);
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


    private void openSharerListDialog() {
        TextView tv_title;
        ImageView iv_back;
        final Dialog sharerListDialog;
        sharerListDialog = new MyCustomThemeDialog(ArticleActivity.this, R.style.NoTitleDialogTheme);
        ;
        sharerListDialog.setContentView(R.layout.contact_list_dialog);
        sharerListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        // sharerListDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        pb_loading_users = (ProgressBar) sharerListDialog.findViewById(R.id.pb_loading_users);
        tv_title = sharerListDialog.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.followers));

        iv_back = (ImageView) sharerListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharerListDialog.dismiss();
            }
        });

        sharerListDialog.setTitle(getString(R.string.followers));
        RecyclerView rv_list;
        rv_list = (RecyclerView) sharerListDialog.findViewById(R.id.rv_list);
        //rv_list.setPadding(16,32,16,32);
        rv_list.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        sharersList = new ArrayList<>();
        sharerListAdapter = new UpvotersAndAttendeesListAdapter(ArticleActivity.this, sharersList);
        rv_list.setAdapter(sharerListAdapter);
        hitGetArticleSharerListApi(user_id, articleId, 0);
        sharerListDialog.show();

    }

    private void openArticleCommentListActivity(String comment_id, String target) {
        Intent commentListIntent = new Intent(ArticleActivity.this, ArticleCommentListActivity.class);
        commentListIntent.putExtra(AppKeys.ID, articleId);
        commentListIntent.putExtra(AppKeys.COMMENT_ID, comment_id);
        commentListIntent.putExtra(AppKeys.CREATOR_ID, pojoGetArticleDetailsResponse.data.createdBy.userId);
        commentListIntent.putExtra(AppKeys.COMMENT_COUNT, Integer.parseInt(pojoGetArticleDetailsResponse.data.discussionsCnt));
        commentListIntent.putExtra(AppKeys.TARGET, target);
        startActivity(commentListIntent);
    }


    private void hitGetArticleDetailsApi(String user_id, String article_id) {
        boolean allFieldsOk = !user_id.isEmpty() && !article_id.isEmpty();

        if (allFieldsOk) {
            Call<PojoGetArticleDetailsResponse> call = apiService.getArticleDetailsApi(user_id, article_id);
            call.enqueue(new Callback<PojoGetArticleDetailsResponse>() {
                @Override
                public void onResponse(Call<PojoGetArticleDetailsResponse> call, Response<PojoGetArticleDetailsResponse> response) {
                    String message = "";
                    if (response != null && response.body() != null) {
                        message = response.body().message;
                        if (response.body() != null) {
                            pojoGetArticleDetailsResponse = response.body();
                            setDataOnViews();
                          /*  if(target_comment_id!=null&&!target_comment_id.isEmpty()){
                                openCommentListDialog(target_comment_id);
                            }*/
                           /* if (action != null && action.equals(AppKeys.COMMENT)) {
                                openCommentListDialog(target_comment_id);
                            }*/
                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), "Null body Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "Url Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    cl_parent.setVisibility(View.VISIBLE);
                    pb_loading_content.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(Call<PojoGetArticleDetailsResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_SHORT, AppKeys.TOAST_DEBUG);
                    //  sv_parent.setVisibility(View.VISIBLE);
                    openNoConnectionDialog(getString(R.string.article));
                }
            });
        }

    }


    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetArticleDetailsApi(preferenceUtils.get_user_id(), articleId);
                    hitGetSuggestedArticles(preferenceUtils.get_user_id(), "articles");
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


    private void hitGetArticleSharerListApi(String user_id, String articleId, int offset) {
        Call<PojoGetSuggestedFriendListResponse> call = apiService.articleSharer(user_id, articleId, String.valueOf(offset));
        call.enqueue(new Callback<PojoGetSuggestedFriendListResponse>() {
            @Override
            public void onResponse(Call<PojoGetSuggestedFriendListResponse> call, Response<PojoGetSuggestedFriendListResponse> response) {
                pb_loading_users.setVisibility(View.GONE);
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (response.body() != null) {
                        sharersList.clear();
                        sharersList.addAll(response.body().data);
                        sharerListAdapter.notifyDataSetChanged();
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "Null body Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }

                } else {
                    commonFunctions.setToastMessage(getApplicationContext(), "Url Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoGetSuggestedFriendListResponse> call, Throwable t) {
                pb_loading_comments.setVisibility(View.GONE);

                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }

  /*  private void hitGetArticleUpvoterListApi(String user_id, String articleId, int offSet) {
        Call<PojoGetSuggestedFriendListResponse> call = apiService.articleUpvoter(user_id, articleId, String.valueOf(offSet));
        call.enqueue(new Callback<PojoGetSuggestedFriendListResponse>() {
            @Override
            public void onResponse(Call<PojoGetSuggestedFriendListResponse> call, Response<PojoGetSuggestedFriendListResponse> response) {
                pb_loading_users.setVisibility(View.GONE);
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (response.body() != null) {
                        upvotersList.clear();
                        upvotersList.addAll(response.body().data);
                        upvoterListAdapter.notifyDataSetChanged();
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "Null body Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }

                } else {
                    commonFunctions.setToastMessage(getApplicationContext(), "Url Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

            }

            @Override
            public void onFailure(Call<PojoGetSuggestedFriendListResponse> call, Throwable t) {
                pb_loading_comments.setVisibility(View.GONE);

                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }*/

    private void hitGetSuggestedArticles(String user_id, String type) {

        Call<PojoSuggestedArticlesResponse> call = apiService.getSuggestedArticles(user_id, type);
        call.enqueue(new Callback<PojoSuggestedArticlesResponse>() {
            @Override
            public void onResponse(Call<PojoSuggestedArticlesResponse> call, Response<PojoSuggestedArticlesResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (response.body() != null) {
                        suggestedArticles.clear();
                        suggestedArticles.addAll(response.body().data);
                        relatedArticleListAdapter.notifyDataSetChanged();
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "Null body Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }

                } else {
                    commonFunctions.setToastMessage(getApplicationContext(), "Url Error" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
                pb_loading_related_articles.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<PojoSuggestedArticlesResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_loading_related_articles.setVisibility(View.GONE);

            }
        });

    }


    private void setDataOnViews() {
        taggedInterestList.clear();
        taggedInterestList.addAll(pojoGetArticleDetailsResponse.data.intrests);
        if (taggedInterestList.isEmpty()) {
            rv_interest_tag_list.setVisibility(View.GONE);
        } else {
            rv_interest_tag_list.setVisibility(View.VISIBLE);
        }
        taggedInterestAdapter.notifyDataSetChanged();
        invalidateOptionsMenu();

        //actionBar.setTitle(pojoGetArticleDetailsResponse.data.title);
        tv_article_name.setText(Html.fromHtml(pojoGetArticleDetailsResponse.data.title));
        tv_article.setText(Html.fromHtml(pojoGetArticleDetailsResponse.data.description));

        try {
            Glide.with(getApplicationContext())
                    .load(pojoGetArticleDetailsResponse.data.coverPhoto)
                    .into(iv_article_image);

            Glide.with(getApplicationContext())
                    .load(pojoGetArticleDetailsResponse.data.createdBy.userPicture)
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(iv_publisher_dp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_publisher_name.setText(pojoGetArticleDetailsResponse.data.createdBy.userFullname);

        String placeAndDesignation = "";
        //if both fields are in data list then show both
        if ((!pojoGetArticleDetailsResponse.data.createdBy.userWorkTitle.isEmpty())
                && (!pojoGetArticleDetailsResponse.data.createdBy.userWorkPlace.isEmpty())) {
            placeAndDesignation = pojoGetArticleDetailsResponse.data.createdBy.userWorkTitle
                    + " (" + pojoGetArticleDetailsResponse.data.createdBy.userWorkPlace + ")";
        }
        //if work title not available show only work place
        else if (pojoGetArticleDetailsResponse.data.createdBy.userWorkTitle.isEmpty()) {
            placeAndDesignation = pojoGetArticleDetailsResponse.data.createdBy.userWorkPlace;
        }
        //if work place not available show only work title
        else if (pojoGetArticleDetailsResponse.data.createdBy.userWorkPlace.isEmpty()) {
            placeAndDesignation = pojoGetArticleDetailsResponse.data.createdBy.userWorkTitle;
        }
        //if none is present then remove visibility of the field
        else if (pojoGetArticleDetailsResponse.data.createdBy.userWorkTitle.isEmpty()
                && pojoGetArticleDetailsResponse.data.createdBy.userWorkPlace.isEmpty()) {
            tv_publisher_designation.setVisibility(View.GONE);
        }
        tv_publisher_designation.setText(placeAndDesignation);

        tv_publish_time_stamp.setText(getString(R.string.published_on) + " "
                + commonFunctions.parseDateToName(pojoGetArticleDetailsResponse.data.addedOn));

        setFollowStatus();


        if (pojoGetArticleDetailsResponse.data.iLike) {
            tv_like_button.setTextColor(getResources().getColor(R.color.app_theme_medium));
            tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_filled,
                    0, 0, 0);

        } else {
            tv_like_button.setTextColor(getResources().getColor(R.color.med_grey));
            tv_like_button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_unfilled,
                    0, 0, 0);
        }

        if (pojoGetArticleDetailsResponse.data.likes.equals("0")) {
            tv_num_likes.setVisibility(View.GONE);
        } else {
            tv_num_likes.setVisibility(View.VISIBLE);
        }
        tv_num_likes.setText(pojoGetArticleDetailsResponse.data.likes + " " + getString(R.string.likes));

        /*   if (pojoGetArticleDetailsResponse.data.shares.equals("0")) */
        {
            tv_num_shares.setVisibility(View.GONE);
        } /*else {
            tv_num_shares.setVisibility(View.VISIBLE);
        }*/
        tv_num_shares.setText(pojoGetArticleDetailsResponse.data.shares + " " + getString(R.string.shares));

        if (pojoGetArticleDetailsResponse.data.discussionsCnt.equals("0")) {
            tv_num_comments.setVisibility(View.GONE);
        } else {
            tv_num_comments.setVisibility(View.VISIBLE);
        }
        tv_num_comments.setText(pojoGetArticleDetailsResponse.data.discussionsCnt + " " + getString(R.string.comments));

    }

    private void setFollowStatus() {
        if (pojoGetArticleDetailsResponse.data.createdBy.userId.equals(user_id)) {
            tv_follow_button.setVisibility(View.GONE);
        } else {
            tv_follow_button.setVisibility(View.VISIBLE);
            if (pojoGetArticleDetailsResponse.data.createdBy.connection.equals("followed")) {
                tv_follow_button.setText(getString(R.string.following));
            }
            if (pojoGetArticleDetailsResponse.data.createdBy.connection.equals("add")) {
                tv_follow_button.setText(getString(R.string.follow));
            }
        }


    }

    private void initMoreArticleList() {
        suggestedArticles = new ArrayList<>();
        rv_other_article_list = (RecyclerView) findViewById(R.id.rv_other_article_list);
        relatedArticleListAdapter = new RelatedArticleListAdapter(ArticleActivity.this, suggestedArticles);
        rv_other_article_list.setLayoutManager(new LinearLayoutManager(ArticleActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rv_other_article_list.setAdapter(relatedArticleListAdapter);
    }

    private void initTaggedInterestList() {
        taggedInterestList = new ArrayList<>();
        rv_interest_tag_list = (RecyclerView) findViewById(R.id.rv_interest_tag_list);
        rv_interest_tag_list.setLayoutManager(new LinearLayoutManager(ArticleActivity.this, LinearLayoutManager.HORIZONTAL, false));
        taggedInterestAdapter = new TaggedInterestAdapter(ArticleActivity.this, taggedInterestList, 2);
        rv_interest_tag_list.setAdapter(taggedInterestAdapter);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (pojoGetArticleDetailsResponse != null) {
            if (pojoGetArticleDetailsResponse.data.createdBy.userId.equals(user_id)) {
                menu.clear();
                getMenuInflater().inflate(R.menu.article_page_writer_menu, menu);

            } else {
                menu.clear();
                getMenuInflater().inflate(R.menu.article_page_other_menu, menu);
            }
        }

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        if (user_id.equals(AppKeys.GUEST_USER_ID)) {
            menu.clear();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent editArticleIntent;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.edit:
                editArticleIntent = new Intent(ArticleActivity.this, CreateArticleActivity.class);
                gson = new Gson();
                jsonString = gson.toJson(pojoGetArticleDetailsResponse);
                editArticleIntent.putExtra("data", jsonString);
                editArticleIntent.putExtra("userType", "edit");
                startActivityForResult(editArticleIntent, EDIT_ARTICLE_REQUEST_CODE);

                //openEditDialog(pojoGetArticleDetailsResponse.data.description);

                break;
            case R.id.delete:
                TextView tv_dialog_description, tv_yes, tv_no;
                final Dialog confirmationDialog = new MyCustomThemeDialog(ArticleActivity.this, R.style.NoTitleDialogTheme);
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
                        hitArticleReactionApi(user_id, articleId, "delete_article");
                        confirmationDialog.dismiss();

                    }
                });
                confirmationDialog.show();


                break;
           /* case R.id.other_suggest_edit:
                editArticleIntent = new Intent(ArticleActivity.this, CreateArticleActivity.class);
                gson = new Gson();
                jsonString = gson.toJson(pojoGetArticleDetailsResponse);
                editArticleIntent.putExtra("data", jsonString);
                editArticleIntent.putExtra("userType", "edit");
                startActivity(editArticleIntent);
                break;*/
            case R.id.share_to:
                // hitArticleReactionApi(user_id,articleId,"share");
                shareTextUrl();
                break;

            case R.id.create_article:
                Intent articleIntent = new Intent(ArticleActivity.this, CreateArticleActivity.class);
                startActivity(articleIntent);
                break;



           /* case R.id.save:
                if (isSaved) {
                    hitArticleReactionApi(user_id, articleId, "unsave_article");
                    isSaved = false;
                    item.setTitle(R.string.save);
                } else {
                    hitArticleReactionApi(user_id, articleId, "save_article");
                    isSaved = true;
                    item.setTitle(R.string.unsave);
                }
                break;


            case R.id.pin:
                if (isPinned) {
                    hitArticleReactionApi(user_id, articleId, "unpin_article");
                    isPinned = false;
                    item.setTitle(R.string.pin);
                } else {
                    hitArticleReactionApi(user_id, articleId, "pin_article");
                    isPinned = true;
                    item.setTitle(R.string.unpin);
                }

                break;*/


            case R.id.report:
                commonFunctions.openReportDialog(user_id, "report_article", articleId);
                break;
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EDIT_ARTICLE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                cl_parent.setVisibility(View.GONE);
                pb_loading_content.setVisibility(View.VISIBLE);
                hitGetArticleDetailsApi(preferenceUtils.get_user_id(), articleId);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
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
                            getString(R.string.shared_article_successfully),
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


    private void hitArticleReactionApi(String user_id, String article_id, String reaction) {
        Call<PojoReportContentResponse> call = apiService.articleReaction(user_id, article_id, reaction);
        if (reaction.equals("delete_article")) {
            commonFunctions.openProgressDialog(getString(R.string.please_wait));
        }
        call.enqueue(new Callback<PojoReportContentResponse>() {
            @Override
            public void onResponse(Call<PojoReportContentResponse> call, Response<PojoReportContentResponse> response) {
                commonFunctions.closeProgressDialog();
                if (response != null && response.body() != null) {
                    if (reaction.equals("delete_article")) {
                        commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.deleted_successfully),
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        Intent registrationComplete = new Intent(AppKeys.ARTICLE_DELETED);
                        LocalBroadcastManager.getInstance(ArticleActivity.this)
                                .sendBroadcast(registrationComplete);
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoReportContentResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.closeProgressDialog();
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });

    }

    private void hitUserConnectionApi(String user_id, String id, String connect_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

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

    private void shareTextUrl() {
        final Context context = this;
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(pojoGetArticleDetailsResponse.data.coverPhoto)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {
                                  ShareContentOnMediaApps.shareContent(ArticleActivity.this,
                                          AppKeys.SHARE_ARTICLE, articleId, pojoGetArticleDetailsResponse.data.title,
                                          bitmap);

                                  return false;
                              }
                          }
                ).submit();
    }


    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem) {

    }


    public void hitAddFriendApi(String user_id, String friend_id, String connection_type) {
        Call<PojoUserConnectResponse> call = apiService.addFriendApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {

                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        if (response.body().status.equals("1")) {

                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    public void hitCancelFriendApi(String user_id, String friend_id, String connection_type) {

        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, friend_id, connection_type);
        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {

                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        if (response.body().status.equals("1")) {

                        } else {
                            commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
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
}
