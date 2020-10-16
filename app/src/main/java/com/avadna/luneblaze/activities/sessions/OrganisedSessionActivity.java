package com.avadna.luneblaze.activities.sessions;

import android.annotation.SuppressLint;
import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.annotation.Nullable;

import com.avadna.luneblaze.services.LuneBlazeUploadService;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.view.menu.MenuBuilder;

import android.provider.MediaStore;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.InviteListAdapter;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionActivityPagerAdapter;
import com.avadna.luneblaze.adapters.session.organised.OrganisedSessionTopicsListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.fragments.sessions.OrganisedSessionQuestionListFragment;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.ShareContentOnMediaApps;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponse;
import com.avadna.luneblaze.pojo.pojoSessionDetails.PojoSessionDetailsResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class OrganisedSessionActivity extends AppBaseActivity implements
        OrganisedSessionTopicsListAdapter.DiscussionTopicItemClickCallback {
    private ActionBar actionBar;
    private ApiInterface apiService;
    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private AppBarLayout appBarLayout;
    private TabLayout tl_tabs;
    private ViewPager vp_tab_item_pager;
    private OrganisedSessionActivityPagerAdapter organisedSessionActivityPagerAdapter;
    private String session_id = "113";
    private String tabType;
    private PojoSessionDetailsResponseData pojoSessionDetailsResponseData = null;
    private String dataStr;

    private TextView tv_session_name, tv_sharer_name, tv_initiator_name;
    private TextView tv_num_thanks, tv_num_questions, tv_num_shares;
    private LinearLayout ll_action_button_wrapper;
    private TextView tv_thank, tv_ask_question, tv_share;
    private ImageView iv_cover_photo;
    private Dialog inviteDialog;
    private List<PojoUserData> friendList;
    private InviteListAdapter inviteListAdapter;

    private View.OnClickListener onClickListener;


    /*  List<String> more_options_list;
  private ArrayAdapter<String> spinnerAdapter;
  private Spinner sp_more_options;
  private ImageView iv_more_options;*/
    private String ATTENDEE = "attendee";
    private String OTHER = "other";
    private String SHARER = "sharer";
    private String userType = OTHER;

    private boolean followingTaggedInterests = false;
    private boolean questionNotify = false;
    private boolean followingVenue = false;
    //  boolean iJoin = false;
    private boolean iThank = false;
    //  Menu menu;

    private DisplayMetrics displayMetrics;
    private FloatingActionButton fab_add_comment;


    private int lastOffset = 0;

    private int imageFixedWidth;
    private int imageFixedHeight;
    private int maxSessionImageHeight;
    private int minSessionImageHeight;

    private String action;

    private static final int SELECT_VIDEO_REQUEST_CODE = 1002;
    private static final int SELECT_FILE_REQUEST_CODE = 1003;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organised_session);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        imageFixedWidth = (int) (displayMetrics.widthPixels);
        imageFixedHeight = (int) (displayMetrics.widthPixels * 0.5f);
        maxSessionImageHeight = (int) (displayMetrics.widthPixels * 0.6f);
        minSessionImageHeight = (int) (displayMetrics.widthPixels * 0.4f);

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        session_id = getIntent().getStringExtra("id");
        if (session_id == null) {
            session_id = "113";
        }

        tabType = getIntent().getStringExtra("type");
        setUpActionBar();
        setUpTabLayout();
        initViews();
        initClickListener();
        setClickListener();

        if (tabType != null) {
            if (tabType.equals("comment") || tabType.equals("details")) {
                vp_tab_item_pager.setCurrentItem(0, true);
            } else if (tabType.equals("discussion")) {
                vp_tab_item_pager.setCurrentItem(0, true);
            } else if (tabType.equals("gallery")) {
                vp_tab_item_pager.setCurrentItem(1, true);
            } else if (tabType.equals("question") || tabType.equals(AppKeys.ASK_QUESTION)) {
                vp_tab_item_pager.setCurrentItem(2, true);
            }
        }

        dataStr = getIntent().getStringExtra("data");
        if (dataStr != null && !dataStr.isEmpty()) {
            Type type = new TypeToken<PojoSessionDetailsResponseData>() {
            }.getType();
            pojoSessionDetailsResponseData = new Gson().fromJson(dataStr, type);
            setDataOnViews();
        } else {
            hitGetSessionDetailsApi(user_id, session_id, "home");
        }

        action = getIntent().getStringExtra("action");
        if (action != null) {
            if (action.equals(AppKeys.SESSION_RATING)) {
                openRatingDialog();
            }
        }
        //  initMoreOptionsSpinner();

    }


    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commonFunctions.isGuestUser()) {
                    commonFunctions.showLoginRequestDialog();
                } else {
                    switch (v.getId()) {
                        case R.id.tv_thank:
                            if (pojoSessionDetailsResponseData.iThanks == 0) {
                                hitSessionReactionApi(user_id, session_id, "session_thanks");
                                pojoSessionDetailsResponseData.iThanks = 1;
                                tv_thank.setTextColor(ContextCompat.getColor(OrganisedSessionActivity.this,
                                        R.color.app_theme_medium));
                                tv_thank.setCompoundDrawablesWithIntrinsicBounds
                                        (R.drawable.ic_thanks_filled, 0, 0, 0);
                            } else {
                                hitSessionReactionApi(user_id, session_id, "session_thanks");
                                pojoSessionDetailsResponseData.iThanks = 0;
                                tv_thank.setTextColor(ContextCompat.getColor(OrganisedSessionActivity.this,
                                        R.color.med_grey));
                                tv_thank.setCompoundDrawablesWithIntrinsicBounds
                                        (R.drawable.ic_thanks_unfilled, 0, 0, 0);
                            }

                            break;
                        case R.id.tv_ask_question:
                            OrganisedSessionQuestionListFragment frag
                                    = (OrganisedSessionQuestionListFragment)
                                    organisedSessionActivityPagerAdapter.getRegisteredFragment(2);
                            frag.openAddQuestionDialog("");
                            break;

                        case R.id.tv_share:
                            openShareSessionOnProfileDialog();
                            break;

                        case R.id.fab_add_comment:
                            if (vp_tab_item_pager.getCurrentItem() == 0) {
                                Intent commentIntent = new Intent(OrganisedSessionActivity.this,
                                        SessionCommentsListActivity.class);
                                commentIntent.putExtra("id", session_id);
                                commentIntent.putExtra(AppKeys.TARGET, AppKeys.ADD_COMMENT);
                                startActivity(commentIntent);
                            } else {
                                tv_ask_question.callOnClick();
                            }

                            break;

                        case R.id.iv_cover_photo:
                            if (pojoSessionDetailsResponseData != null) {
                                openImageViewerDialog(pojoSessionDetailsResponseData.coverPhoto);
                            }
                            break;
                    }
                }
            }
        };
    }

    private void openImageViewerDialog(String path) {
        final Dialog imageViewerDialog;
        imageViewerDialog = new MyCustomThemeDialog(OrganisedSessionActivity.this, android.R.style.Theme_Black_NoTitleBar);
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

        Glide.with(getApplicationContext())
                .load(path)
                .into(iv_photo);

        imageViewerDialog.show();
    }

    public void showAddCommentFab() {
        if (vp_tab_item_pager.getCurrentItem() == 0 && !commonFunctions.isGuestUser()) {
            if (pojoSessionDetailsResponseData.iAttend
                    || (!pojoSessionDetailsResponseData.presentors.isEmpty()
                    && pojoSessionDetailsResponseData.presentors.get(0).userId.equals(user_id))) {
                fab_add_comment.show();
            }
        } else {
            fab_add_comment.show();
        }
    }

    public void hideAddCommentFab() {
        fab_add_comment.hide();
    }

    private void setClickListener() {
        tv_thank.setOnClickListener(onClickListener);
        tv_ask_question.setOnClickListener(onClickListener);
        tv_share.setOnClickListener(onClickListener);
        iv_cover_photo.setOnClickListener(onClickListener);
        fab_add_comment.setOnClickListener(onClickListener);
    }

    private void initViews() {
        tv_session_name = (TextView) findViewById(R.id.tv_session_name);
        tv_sharer_name = (TextView) findViewById(R.id.tv_sharer_name);
        tv_initiator_name = (TextView) findViewById(R.id.tv_initiator_name);
        tv_num_thanks = (TextView) findViewById(R.id.tv_num_thanks);
        tv_num_questions = (TextView) findViewById(R.id.tv_num_questions);
        tv_num_shares = (TextView) findViewById(R.id.tv_num_shares);
        ll_action_button_wrapper = (LinearLayout) findViewById(R.id.ll_action_button_wrapper);
        tv_thank = (TextView) findViewById(R.id.tv_thank);
        tv_ask_question = (TextView) findViewById(R.id.tv_ask_question);
        tv_share = (TextView) findViewById(R.id.tv_share);
        iv_cover_photo = (ImageView) findViewById(R.id.iv_cover_photo);
        fab_add_comment = (FloatingActionButton) findViewById(R.id.fab_add_comment);
        fab_add_comment.hide();
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle("");
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(OrganisedSessionActivity.this, R.color.black_translucent_20)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(OrganisedSessionActivity.this, R.color.status_bar_color));
            }
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
                    if (pojoSessionDetailsResponseData != null) {
                        actionBar.setTitle(Html.fromHtml(pojoSessionDetailsResponseData.title));
                    }
                } else {
                    actionBar.setTitle("");
                }

                int dy = verticalOffset - lastOffset;
                if (vp_tab_item_pager.getCurrentItem() == 0 && dy > 5) {
                    showAddCommentFab();
                }
                if (vp_tab_item_pager.getCurrentItem() == 0 && dy < -5) {
                    hideAddCommentFab();
                }
                lastOffset = verticalOffset;
            }
        });
    }


    public void switchTab(int position) {
        vp_tab_item_pager.setCurrentItem(position);
    }

    private void hitGetSessionDetailsApi(String user_id, String session_id, String view) {
        Call<PojoSessionDetailsResponse> call = apiService.getSessionDetails(user_id, session_id,
                view, "", "", "", "0");
        call.enqueue(new Callback<PojoSessionDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoSessionDetailsResponse> call, Response<PojoSessionDetailsResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    pojoSessionDetailsResponseData = response.body().data;
                    setDataOnViews();
                    commonFunctions.setToastMessage(getApplicationContext(), message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoSessionDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.session));

            }
        });
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetSessionDetailsApi(user_id, session_id, "home");
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

    private void setDataOnViews() {

        if (tabType != null) {
            if (tabType.equals("details")) {
                vp_tab_item_pager.setCurrentItem(0, true);
            } else if (tabType.equals("discussion")) {
                vp_tab_item_pager.setCurrentItem(1, true);
            } else if (tabType.equals("gallery")) {
                vp_tab_item_pager.setCurrentItem(2, true);
            } else if (tabType.equals("question") || tabType.equals(AppKeys.ASK_QUESTION)) {
                vp_tab_item_pager.setCurrentItem(3, true);
                vp_tab_item_pager.post(new Runnable() {
                    @Override
                    public void run() {
                        OrganisedSessionQuestionListFragment frag
                                = (OrganisedSessionQuestionListFragment)
                                organisedSessionActivityPagerAdapter.getRegisteredFragment(2);
                        frag.openAddQuestionDialog("");
                    }
                });

            }
        }

        //hide acton buttons from presentor of session
        if (!pojoSessionDetailsResponseData.presentors.isEmpty()
                && pojoSessionDetailsResponseData.presentors.get(0).userId.equals(user_id)) {
            ll_action_button_wrapper.setVisibility(View.GONE);
        }

        invalidateOptionsMenu();
        tv_session_name.setText(Html.fromHtml(pojoSessionDetailsResponseData.title));
        // tv_sharer_name.setText(pojoSessionDetailsResponseData.sharer.get(0).userFullname);
        tv_initiator_name.setText(pojoSessionDetailsResponseData.createdBy.userFullname);
        tv_num_questions.setText("" + pojoSessionDetailsResponseData.questionCount + " " + getString(R.string.questions));
        tv_num_shares.setText("" + pojoSessionDetailsResponseData.totalShares + " " + getString(R.string.shares));
        tv_num_thanks.setText("" + pojoSessionDetailsResponseData.thanksCount + " " + getString(R.string.thanks));
        showAddCommentFab();

        if (pojoSessionDetailsResponseData.iThanks == 1) {
            tv_thank.setTextColor(ContextCompat.getColor(OrganisedSessionActivity.this,
                    R.color.app_theme_medium));
            tv_thank.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_thanks_filled, 0, 0, 0);
        } else {
            tv_thank.setTextColor(ContextCompat.getColor(OrganisedSessionActivity.this,
                    R.color.med_grey));
            tv_thank.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_thanks_unfilled, 0, 0, 0);
        }

        if (!pojoSessionDetailsResponseData.coverPhoto.isEmpty()) {
            int height = imageFixedHeight;
            int width = imageFixedWidth;

            if (pojoSessionDetailsResponseData.imgDimensions != null
                    && !pojoSessionDetailsResponseData.imgDimensions.isEmpty()) {
                ImageDimensions dimen = getScaledDimensions(pojoSessionDetailsResponseData.imgDimensions);
                height = dimen.height;
                width = dimen.width;
            }

            if (height > maxSessionImageHeight) {
                height = maxSessionImageHeight;
            }

            if (height < minSessionImageHeight) {
                height = minSessionImageHeight;
            }

            iv_cover_photo.getLayoutParams().height = height;
            iv_cover_photo.getLayoutParams().width = width;

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.override(width, height);
            requestOptions.placeholder(R.drawable.placeholder);

            Glide.with(getApplicationContext())
                    .load(pojoSessionDetailsResponseData.coverPhoto)
                    .apply(requestOptions)
                    .into(iv_cover_photo);
        }


        if (tabType != null && tabType.equals("comment")) {
            vp_tab_item_pager.setCurrentItem(0, true);
            String commentId = getIntent().getStringExtra("comment_id");
            Intent newMsg = new Intent(Config.OPEN_COMMENT_DIALOG);
            newMsg.putExtra("comment_id", commentId);
            LocalBroadcastManager.getInstance(this).sendBroadcast(newMsg);
        }


        //  openRatingDialog();

    }


    public ImageDimensions getScaledDimensions(String imgDimen) {
        ImageDimensions dimen = new ImageDimensions();
        int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
        int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
        float imgRatio = 1f * width / height;
        int targetWidth = (int) (displayMetrics.widthPixels);
        int targetHeight = (int) (1f * targetWidth / imgRatio);

        if (targetHeight > maxSessionImageHeight) {
            targetHeight = maxSessionImageHeight;
        }
        dimen.width = targetWidth;
        dimen.height = targetHeight;
        return dimen;
    }


    private void setUpTabLayout() {
        tl_tabs = (TabLayout) findViewById(R.id.tl_tabs);
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.details)));
        //  tl_tabs.addTab(tl_tabs.newTab().setText("Comments"));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.gallery)));
        tl_tabs.addTab(tl_tabs.newTab().setText(getString(R.string.questions)));
        tl_tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        vp_tab_item_pager = (ViewPager) findViewById(R.id.vp_tab_item_pager);

        //userType 2 in pager adapter is to tell its Organised session
        organisedSessionActivityPagerAdapter = new OrganisedSessionActivityPagerAdapter(this,
                getSupportFragmentManager(), session_id);
        vp_tab_item_pager.setAdapter(organisedSessionActivityPagerAdapter);
        vp_tab_item_pager.setOffscreenPageLimit(3);

        tl_tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                appBarLayout.setExpanded(false, true);

                vp_tab_item_pager.setCurrentItem(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp_tab_item_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                tl_tabs.setScrollPosition(position, positionOffset, false);
            }

            @Override
            public void onPageSelected(int position) {
                TabLayout.Tab tab = tl_tabs.getTabAt(position);
                tab.select();

                int i = position;
                if (i == 0) {
                    hideAddCommentFab();
                    fab_add_comment.setImageResource(R.drawable.ic_comment_white);
                    showAddCommentFab();
                } else if (i == 2) {
                    hideAddCommentFab();
                    fab_add_comment.setImageResource(R.drawable.ic_question_white);
                    showAddCommentFab();
                } else {
                    hideAddCommentFab();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void collapseAppBar(boolean value) {
        appBarLayout.setExpanded(value, true);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //   this.menu = menu;
       /* if (userType.equals(ATTENDEE)) {
            getMenuInflater().inflate(R.menu.organised_session_attendee_menu, menu);
        } else if (userType.equals(OTHER)) {
            getMenuInflater().inflate(R.menu.organised_session_other_menu, menu);
        } else if (userType.equals(SHARER)) {
            getMenuInflater().inflate(R.menu.organised_session_sharer_menu, menu);
        }*/
        if (pojoSessionDetailsResponseData != null) {
            /*boolean iAmCoordinator = false;
            if (pojoSessionDetailsResponseData.coOrdinatorsData != null) {
                for (int i = 0; i < pojoSessionDetailsResponseData.coOrdinatorsData.size(); i++) {
                    if (user_id.equals(pojoSessionDetailsResponseData.coOrdinatorsData.get(i).userId)) {
                        iAmCoordinator = true;
                    }
                }
            }*/

            //am I creator of session
            if (pojoSessionDetailsResponseData.createdBy.userId.equals(user_id)) {
                menu.clear();
                getMenuInflater().inflate(R.menu.organised_session_creator_menu, menu);

                if (true) {
                    menu.removeItem(R.id.upload_video);
                    menu.removeItem(R.id.upload_file);
                }

                if (pojoSessionDetailsResponseData.iJoin) {
                    menu.getItem(0).setTitle(R.string.stop_notifications);
                } else {
                    menu.getItem(0).setTitle(R.string.stopped_notifications);
                }
            }

            //am I presentor of session
            if (!pojoSessionDetailsResponseData.presentors.isEmpty()
                    && pojoSessionDetailsResponseData.presentors.get(0).userId.equals(user_id)) {
                menu.clear();
                getMenuInflater().inflate(R.menu.organised_session_sharer_menu, menu);

                if (true) {
                    menu.removeItem(R.id.upload_video);
                    menu.removeItem(R.id.upload_file);
                }

                if (pojoSessionDetailsResponseData.iJoin) {
                    menu.getItem(0).setTitle(R.string.stop_notifications);
                    menu.getItem(0).setIcon(R.drawable.ic_stop_notification);

                } else {
                    menu.getItem(0).setTitle(R.string.get_notified);
                    menu.getItem(0).setIcon(R.drawable.ic_stopped_notification);
                }
            }
            //I am a normal user
            else {
                menu.clear();
                getMenuInflater().inflate(R.menu.organised_session_other_menu, menu);

                if (true) {
                    menu.removeItem(R.id.upload_video);
                    menu.removeItem(R.id.upload_file);
                }


                if (pojoSessionDetailsResponseData.iJoin) {
                    menu.getItem(1).setTitle(R.string.joined);
                    menu.getItem(1).setIcon(R.drawable.ic_attend_unfilled);

                } else {
                    menu.getItem(1).setTitle(R.string.join);
                    menu.getItem(1).setIcon(R.drawable.ic_add_fellow_unfilled);

                }

                if (!pojoSessionDetailsResponseData.presentors.isEmpty()) {
                    String name = pojoSessionDetailsResponseData.presentors.get(0).userFullname;
                    if (name.contains(" ")) {
                        name = name.substring(0, name.indexOf(" "));
                    }
                    menu.getItem(2).setTitle(getString(R.string.follow) + " " + name);
                }
            }
        }

        if (commonFunctions.isGuestUser()) {
            menu.clear();
        }
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    private void openRatingDialog() {
        Dialog ratingDialog = new MyCustomThemeDialog(OrganisedSessionActivity.this,
                R.style.NoTitleDialogTheme);
        ratingDialog.setContentView(R.layout.venue_four_rating_layout);

        RatingBar rb_seating, rb_accessibility, rb_staff, rb_ambiance;
        rb_seating = (RatingBar) ratingDialog.findViewById(R.id.rb_seating);
        rb_accessibility = (RatingBar) ratingDialog.findViewById(R.id.rb_accessibility);
        rb_staff = (RatingBar) ratingDialog.findViewById(R.id.rb_staff);
        rb_ambiance = (RatingBar) ratingDialog.findViewById(R.id.rb_ambiance);

        TextView tv_title = (TextView) ratingDialog.findViewById(R.id.tv_title);
        tv_title.setText(getString(R.string.rate_session));

        ratingDialog.setCancelable(true);

        TextView tv_submit = (TextView) ratingDialog.findViewById(R.id.tv_submit);
        tv_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String venueId = "0";
                if (!pojoSessionDetailsResponseData.venues.isEmpty()) {
                    venueId = pojoSessionDetailsResponseData.venues.get(0).venueId;
                }
                hitSubmitVenueRatingApi(user_id, session_id, venueId,
                        String.valueOf(rb_seating.getRating()), String.valueOf(rb_accessibility.getRating()),
                        String.valueOf(rb_staff.getRating()), String.valueOf(rb_ambiance.getRating()));
                ratingDialog.dismiss();
            }
        });
        ratingDialog.show();
    }

    private void hitSubmitVenueRatingApi(String user_id, String session_id, String venue_id,
                                         String seating, String access, String staff, String ambiance) {
        Call<PojoNoDataResponse> call = apiService.submitVenueRating(user_id, session_id,
                venue_id, seating, access, staff, "5", ambiance);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.thankyou_for_response), Toast.LENGTH_LONG, AppKeys.TOAST_USER);

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                //openNoConnectionDialog(getString(R.string.session));

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
       /*     case R.id.follow_venue:
                if (followingVenue) {
                    hitFollowVenueApi(user_id, pojoSessionDetailsResponseData.venues.get(0).venueId, "remove_notify_me_venue");
                    followingVenue = false;
                    item.setTitle(R.string.follow_venue);
                } else {
                    hitFollowVenueApi(user_id, pojoSessionDetailsResponseData.venues.get(0).venueId, "notify_me_venue");
                    followingVenue = true;
                    item.setTitle(R.string.unFollow_venue);
                }
                break;*/
            case R.id.share_to:
                shareTextUrl();
                break;

            case R.id.share_timeline:
                openShareSessionOnProfileDialog();
                break;

            case R.id.upload_video:
                Intent videoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(videoIntent, SELECT_VIDEO_REQUEST_CODE);
                break;

            case R.id.upload_file:
                String[] mimeTypes =
                        {"application/pdf", "application/msword", "application/vnd.ms-powerpoint",
                                "application/vnd.ms-excel", "text/plain"};

                Intent fileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                // fileIntent.addCategory(Intent.CATEGORY_OPENABLE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    fileIntent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
                    if (mimeTypes.length > 0) {
                        fileIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    }
                } else {
                    String mimeTypesStr = "";
                    for (String mimeType : mimeTypes) {
                        mimeTypesStr += mimeType + "|";
                    }
                    fileIntent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
                }
                startActivityForResult(fileIntent, SELECT_FILE_REQUEST_CODE);
                break;

         /*   case R.id.follow_interests:
                if (followingTaggedInterests) {
                    hitSessionReactionApi(user_id, session_id, "interest_unfollow");
                    followingTaggedInterests = false;
                    item.setTitle(R.string.follow_tagged_interests);
                } else {
                    hitSessionReactionApi(user_id, session_id, "interest_follow");
                    followingTaggedInterests = true;
                    item.setTitle(R.string.unfollow_tagged_interests);
                }
                break;*/
            case R.id.join:
                if (pojoSessionDetailsResponseData.iJoin) {
                    hitSessionReactionApi(user_id, session_id, "leave");
                    pojoSessionDetailsResponseData.iJoin = false;
                    item.setTitle(R.string.join);
                    item.setIcon(R.drawable.ic_add_fellow_unfilled);

                } else {
                    hitSessionReactionApi(user_id, session_id, "session_join");
                    pojoSessionDetailsResponseData.iJoin = true;
                    item.setTitle(R.string.joined);
                    item.setIcon(R.drawable.ic_attend_unfilled);
                }
                break;

            case R.id.follow_sharer:
                if (!pojoSessionDetailsResponseData.presentors.isEmpty())
                    hitSessionReactionApi(user_id, pojoSessionDetailsResponseData.presentors.get(0).userId, "follow");
                break;

            case R.id.stop_notifications:
                if (pojoSessionDetailsResponseData.iJoin) {
                    hitSessionReactionApi(user_id, session_id, "leave");
                    pojoSessionDetailsResponseData.iJoin = false;
                    item.setTitle(R.string.get_notified);
                    item.setIcon(R.drawable.ic_stopped_notification);

                } else {
                    hitSessionReactionApi(user_id, session_id, "session_join");
                    pojoSessionDetailsResponseData.iJoin = true;
                    item.setTitle(R.string.stop_notifications);
                    item.setIcon(R.drawable.ic_stop_notification);

                }
                break;


            case R.id.report:
                commonFunctions.openReportDialog(user_id, "report_session", session_id);
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void hitFollowVenueApi(String user_id, String venue_id, String connection_type) {
        {
            Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, venue_id, connection_type);
            call.enqueue(new Callback<PojoUserConnectResponse>() {
                @Override
                public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {

                    if (response.body() != null) {
                        String message = response.body().message;
                    }
                }

                @Override
                public void onFailure(Call<PojoUserConnectResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            });
        }
    }

    private void hitSessionReactionApi(String user_id, String session_id, String reaction) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, session_id, reaction);
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

    private void openShareSessionOnProfileDialog() {
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
        View view = inflater.inflate(R.layout.share_verified_session_item, rl_item_holder, true);
        ImageView iv_session_photo = (ImageView) view.findViewById(R.id.iv_session_photo);
        TextView tv_session_name = (TextView) view.findViewById(R.id.tv_session_name);
        TextView tv_session_time_stamp = (TextView) view.findViewById(R.id.tv_session_time_stamp);


        int targetWidth = (int) (displayMetrics.widthPixels * 0.85f);
        int targetHeight;

        if (pojoSessionDetailsResponseData.imgDimensions != null && !pojoSessionDetailsResponseData.imgDimensions.isEmpty()) {
            String imgDimen = pojoSessionDetailsResponseData.imgDimensions;
            int height = Integer.parseInt(imgDimen.substring(0, imgDimen.indexOf("X")));
            int width = Integer.parseInt(imgDimen.substring(imgDimen.indexOf("X") + 1, imgDimen.length()));
            targetHeight = (int) ((1f * height * targetWidth) / width);

        } else {
            targetHeight = (int) (displayMetrics.widthPixels * 0.5f);
        }

        iv_cover_photo.getLayoutParams().height = targetHeight;
        iv_cover_photo.getLayoutParams().width = targetWidth;

        Glide.with(getApplicationContext())
                .load(pojoSessionDetailsResponseData.coverPhoto)
                .apply(new RequestOptions().override(targetWidth, targetHeight))
                .into(iv_session_photo);


        tv_session_name.setText(Html.fromHtml(pojoSessionDetailsResponseData.title));

        tv_session_time_stamp.setText(commonFunctions.parseNewsFeedItemTime(pojoSessionDetailsResponseData.addedOn));

        tv_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitShareContentApi(user_id, pojoSessionDetailsResponseData.sessionsId,
                        "share_session", et_share_title.getText().toString().trim());
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(et_share_title.getWindowToken(), 0);
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
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.shared_session_successfully),
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_VIDEO_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = commonFunctions.getRealPathFromUri(data.getData());
            LuneBlazeUploadService.uploadFile(OrganisedSessionActivity.this, session_id,
                    path, "123", AppKeys.TYPE_IMAGE);
        }

        if (requestCode == SELECT_FILE_REQUEST_CODE && resultCode == RESULT_OK) {
            String path = Objects.requireNonNull(data.getData()).getPath();
            LuneBlazeUploadService.uploadFile(OrganisedSessionActivity.this,
                    session_id, path, "123", AppKeys.TYPE_IMAGE);
        }

    }

    private void shareTextUrl() {
        final Context context = this;
        Glide.with(context.getApplicationContext())
                .asBitmap()
                .load(pojoSessionDetailsResponseData.coverPhoto)
                .listener(new RequestListener<Bitmap>() {
                              @Override
                              public boolean onLoadFailed(@Nullable GlideException e, Object o, Target<Bitmap> target, boolean b) {
                                  return false;
                              }

                              @Override
                              public boolean onResourceReady(Bitmap bitmap, Object o, Target<Bitmap> target, DataSource dataSource, boolean b) {

                                  ShareContentOnMediaApps.shareContent(OrganisedSessionActivity.this,
                                          AppKeys.SHARE_SESSION, session_id, pojoSessionDetailsResponseData.title,
                                          bitmap);

                                  return false;
                              }
                          }
                ).submit();

    }

    @Override
    public void discussionTopicItemClickCallback(String action, String type, int position) {

    }

    @Override
    protected void onPause() {
        super.onPause();

    }
}
