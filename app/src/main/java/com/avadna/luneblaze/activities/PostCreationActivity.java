package com.avadna.luneblaze.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;

import com.avadna.luneblaze.pojo.pojoOrganisation.PojoGetOrganisationInfoResponseData;
import com.avadna.luneblaze.pojo.pojoOrganisation.PojoOrganizationData;
import com.avadna.luneblaze.rest.ApiClientLongDuration;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.PostCreationAddedImageAdapter;
import com.avadna.luneblaze.adapters.normalPost.TagUserSuggestionPopUpAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.helperClasses.SpannableClickItem;
import com.avadna.luneblaze.pojo.PojoCreatePostResponse;
import com.avadna.luneblaze.pojo.PojoGetFriendsListResponse;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.utils.EditTextCursorWatcher;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.common.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.leocardz.link.preview.library.LinkPreviewCallback;
import com.leocardz.link.preview.library.SourceContent;
import com.leocardz.link.preview.library.TextCrawler;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;


public class PostCreationActivity extends AppBaseActivity implements
        PostCreationAddedImageAdapter.PostCreationImageAdapterCallback {
    private ApiInterface apiService;
    private ActionBar actionBar;

    private PreferenceUtils preferenceUtils;
    private CommonFunctions commonFunctions;
    private String user_id;
    private PojoLoginResponseData pojoLoginResponseData;

    private ImageView iv_writer_dp;
    private TextView tv_writer_name;
    private TextView tv_place_and_designation;

    private PopupMenu popupMenu;
    private TextView tv_privacy;
    private String currentPrivacy = "";

    private RelativeLayout rl_parent;
    private EditTextCursorWatcher et_content;
    private TextView tv_create, tv_add_image, tv_add_tag;
    private ProgressBar pb_creating_post;
    private View.OnClickListener onClickListener;
    private int PLACE_PICKER_REQUEST = 1;
    private int statusBarHeight = 0, actionBarHeight = 0;
    private RelativeLayout rl_content_wrapper;
    private static final int PICKIMAGE_RESULT_CODE = 1;
    private List<Uri> imagesUri;
    private List<String> imagePaths;

    private RecyclerView rv_images;
    private GridLayoutManager gridLayoutManager;
    private PostCreationAddedImageAdapter postCreationAddedImageAdapter;

    private RelativeLayout rl_web_preview_wrapper;
    private ImageView iv_link_image;
    private TextView tv_link_title;
    private TextView tv_website_name;
    private TextView tv_link_description;
    private TextWatcher textWatcher;
    private TextCrawler textCrawler;
    private ProgressBar pb_load_preview;

    private PojoGetNewsFeedResponseData pojoGetNewsFeedResponseData;
    private DisplayMetrics displayMetrics;

    private List<String> taggedUserList = new ArrayList<>();

    private ListPopupWindow popupWindow;


    private List<ForegroundColorSpan> colorSpanList = new ArrayList<>();
    private BackgroundColorSpan appBGSpan;
    private int previousLength;
    private Spannable nonModifiedSpannable = null;

    private static final int POST_MAX_LIMIT = 2000;
    private BottomSheetDialog exitConfirmationDialog;

    private boolean actionEnabled = true;

    private final int SHARED_INTENT_IMAGE_PERMISSION = 168;
    private final int PICK_IMAGE_PERMISSION = 169;

    private String organisationId;
    private PojoGetOrganisationInfoResponseData pojoGetOrganisationInfoResponseData;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_post_creation);
        preferenceUtils = new PreferenceUtils(PostCreationActivity.this);
        commonFunctions = new CommonFunctions(this);
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        if (pojoLoginResponseData == null||commonFunctions.isGuestUser()) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientLongDuration.getClient().create(ApiInterface.class);
        organisationId = getIntent().getStringExtra(AppKeys.ORGANISATION_ID);
        String orgData = getIntent().getStringExtra(AppKeys.ORGANISATION_DATA);
        pojoGetOrganisationInfoResponseData = new Gson().fromJson(orgData,
                new TypeToken<PojoGetOrganisationInfoResponseData>() {
                }.getType());

        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        String postDataStr = getIntent().getStringExtra("data");
        Type type = new TypeToken<PojoGetNewsFeedResponseData>() {
        }.getType();
        pojoGetNewsFeedResponseData = new Gson().fromJson(postDataStr, type);
        appBGSpan = new BackgroundColorSpan(ContextCompat
                .getColor(PostCreationActivity.this, R.color.app_theme_extra_light));
        setUpActionBar();
        initBarSizes();
        initViews();
        initClickListeners();
        setClickListeners();
        initTextWatcher();
        setTextListener();
        if (pojoGetNewsFeedResponseData != null) {
            setEditData();
        }
        currentPrivacy = "public";
        initPopUpMenu();
        if (preferenceUtils.getUserLoginData() != null) {
            initShareBroadcastReceiver();
        }
    }

    private void initShareBroadcastReceiver() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                requestStoragePermission(SHARED_INTENT_IMAGE_PERMISSION);// Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                requestStoragePermission(SHARED_INTENT_IMAGE_PERMISSION);// Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
            String text = getIntent().getStringExtra("text");
            if (text != null) {
                et_content.setText(text);
            }
        }
    }


    private void initPopUpMenu() {
        Context wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);

        popupMenu = new PopupMenu(wrapper, tv_privacy);
        MenuInflater inflater = popupMenu.getMenuInflater();
        popupMenu.getMenu().clear();
        inflater.inflate(R.menu.post_privacy_options, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.privacy_public:
                        currentPrivacy = "public";
                        tv_privacy.setText(item.getTitle());
                        break;

                    case R.id.friends:
                        currentPrivacy = "friends";
                        tv_privacy.setText(item.getTitle());
                        break;

                    case R.id.me:
                        currentPrivacy = "me";
                        tv_privacy.setText(item.getTitle());
                        break;

                }
                return false;
            }
        });
    }


    private void initTextWatcher() {
        textCrawler = new TextCrawler();
        textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                previousLength = s.length();
                //select whole word if its a tagged word
          /*      {
                    String initialStr = et_content.getText().toString();
                    String htmlStr = Html.toHtml(et_content.getText());
                    int indexStart = htmlStr.indexOf(htmlSpanUnderlineTagStart);
                    int indexEnd = htmlStr.indexOf(htmlSpanUnderlineTagEnd);
                    if (indexStart > 0 && indexEnd > 0) {
                        indexStart = indexStart + htmlSpanUnderlineTagStart.length();
                        String firstHalf = htmlStr.substring(0, indexStart);
                        String lastHalf = htmlStr.substring(indexEnd, htmlStr.length() - 1);
                        String fullStr = firstHalf + lastHalf;
                        //    et_content.setText(Html.fromHtml(fullStr));
                        String finalStr = fullStr;
                        et_content.setSelection(compareStrings(initialStr, finalStr),
                                compareStrings(initialStr, finalStr) + indexEnd - indexStart);

                    }
                }*/


            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                Editable editable = et_content.getText();
                String testStr = Html.toHtml(editable);

                if (s.length() > POST_MAX_LIMIT) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.post_no_larger_than_2000), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                    et_content.setText(s.subSequence(0, POST_MAX_LIMIT));
                    et_content.setSelection(et_content.getText().length());
                }

                LinkPreviewCallback linkPreviewCallback = new LinkPreviewCallback() {
                    @Override
                    public void onPre() {
                        // Any work that needs to be done before generating the preview. Usually inflate
                        // your custom preview layout here.
                        //commonFunctions.setToastMessage(activity,"link preview called",Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                        rl_web_preview_wrapper.setVisibility(View.GONE);
                        pb_load_preview.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPos(SourceContent sourceContent, boolean b) {
                        // Populate your preview layout with the results of sourceContent.
                        pb_load_preview.setVisibility(View.GONE);
                        String title = sourceContent.getTitle();
                        String name = sourceContent.getCannonicalUrl();
                        String description = sourceContent.getCannonicalUrl();

                        if (title.isEmpty() || name.isEmpty()) {
                            rl_web_preview_wrapper.setVisibility(View.GONE);
                            pb_load_preview.setVisibility(View.GONE);
                            popupWindow.dismiss();
                        } else {
                            et_content.setMinimumHeight(30);
                            rl_web_preview_wrapper.setVisibility(View.VISIBLE);
                            tv_link_title.setText(sourceContent.getTitle());
                            tv_website_name.setText(sourceContent.getCannonicalUrl());
                            tv_link_description.setText(sourceContent.getDescription());

                            for (int i = 0; i < sourceContent.getImages().size(); i++) {
                                if (sourceContent.getImages().get(i).contains("png")
                                        || sourceContent.getImages().get(i).contains("jpg")) {
                                    Glide.with(PostCreationActivity.this.getApplicationContext())
                                            .load(sourceContent.getImages().get(i))
                                            .into(iv_link_image);
                                    break;
                                }
                            }

                        }
                    }
                };

                String text = Html.fromHtml(s.toString()).toString();

                String url = "";

                String[] words = text.split("\\s+");
                for (final String word : words) {
                    if (!word.contains(AppKeys.WEBSITE_URL) && Patterns.WEB_URL.matcher(word).matches()) {
                        url = word;
                        break;
                    }
                }

                if (actionEnabled) {
                    if (Patterns.WEB_URL.matcher(url).matches()) {
                        textCrawler.makePreview(linkPreviewCallback, url);
                        popupWindow.dismiss();
                    } else if (s.toString().contains("@")) {
                        StringBuilder revStr = new StringBuilder(s);
                        revStr = revStr.reverse();
                        StringBuilder revQuery = new StringBuilder(revStr.substring(0, revStr.indexOf("@")));
                        revQuery = revQuery.reverse();
                        if (!revQuery.toString().contains("\\s")) {
                            hitGetTagListApi(user_id, revQuery.toString(), 0);
                        }
                    } else {
                        rl_web_preview_wrapper.setVisibility(View.GONE);
                        pb_load_preview.setVisibility(View.GONE);
                        popupWindow.dismiss();
                    }
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                int currentLength = s.length();
                //if a char was deleted
                if (previousLength > currentLength) {
                    Spannable spannable = et_content.getText();
                    if (spannable.getSpans(0, spannable.length(), BackgroundColorSpan.class).length > 0) {
                        int startBgIndex = spannable.getSpanStart(appBGSpan);
                        int endBgIndex = spannable.getSpanEnd(appBGSpan);
                        if (et_content.getSelectionStart() > startBgIndex && et_content.getSelectionStart() <= endBgIndex) {

                            //remove the item from foreground span list as well
                            for (int i = 0; i < colorSpanList.size(); i++) {
                                int startSpan = spannable.getSpanStart(colorSpanList.get(i));
                                int a = 5;
                                if (startSpan == startBgIndex) {
                                    colorSpanList.remove(i);
                                    taggedUserList.remove(i);
                                    break;
                                }
                            }

                            ((Editable) spannable).delete(startBgIndex, endBgIndex);
                        }
                    }
                }
            }
        };
    }

    private void hitGetTagListApi(String user_id, String query, int offset) {
        query = query.replace("\n", "");

        Call<PojoGetFriendsListResponse> call = apiService.getFriendListApi(user_id, user_id, query,
                "0", "1", String.valueOf(offset));

        call.enqueue(new Callback<PojoGetFriendsListResponse>() {
            @Override
            public void onResponse(Call<PojoGetFriendsListResponse> call, final Response<PojoGetFriendsListResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        popupWindow.dismiss();

                        response.body().data.friends = removeAlreadyTaggedUsers(response.body().data.friends);
                        TagUserSuggestionPopUpAdapter tagUserSuggestionPopUpAdapter =
                                new TagUserSuggestionPopUpAdapter(PostCreationActivity.this,
                                        response.body().data.friends, "");

                        popupWindow.setAnchorView(et_content);
                        int popUpWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                200, getResources().getDisplayMetrics());
                        popupWindow.setWidth(popUpWidth);
                        popupWindow.setAdapter(tagUserSuggestionPopUpAdapter);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            popupWindow.setDropDownGravity(Gravity.BOTTOM);
                        }
                        popupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                taggedUserList.add(response.body().data.friends.get(i).userId);
                                Spannable spannable = et_content.getText();
                                String simpleStr = spannable.toString();
                                int indexOfAt = simpleStr.indexOf("@");
                                if (indexOfAt > 0 && indexOfAt < simpleStr.length()) {
                                    spannable = ((Editable) spannable).delete(indexOfAt, simpleStr.length());
                                }
                                int index = spannable.length();
                                spannable = ((Editable) spannable).append(response.body().data.friends.get(i).userFullname);

                                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat
                                        .getColor(PostCreationActivity.this, R.color.app_theme_medium));
                                colorSpanList.add(foregroundColorSpan);

                                spannable.setSpan(foregroundColorSpan, index, spannable.length(),
                                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                                spannable = ((Editable) spannable).append(" ");

                                // String originalStr = Html.toHtml(et_content.getText());
                                //  originalStr = originalStr.replace("<p dir=\"ltr\">", "");
                                //  originalStr = originalStr.replace("</p>", "");
                                et_content.setText(spannable);
                                et_content.setSelection(et_content.getText().length());
                                popupWindow.dismiss();
                            }
                        });


                        int line = et_content.getLayout().getLineForOffset(et_content.getSelectionStart());
                        int column = et_content.getSelectionStart() - et_content.getLayout().getLineStart(line);
                        float textSize = et_content.getTextSize();
                        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
                        if (line < 4) {
                            popupWindow.setVerticalOffset((int) (-px + (textSize) * (1 + line)));
                        }
                        popupWindow.setHorizontalOffset((int) (textSize / 3) * column);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            popupWindow.setDropDownGravity(Gravity.BOTTOM);
                        }

                        try {
                            popupWindow.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoGetFriendsListResponse> call, Throwable t) {
                // Log error here since request failed

                int a = 5;
            }
        });
    }

    private List<PojoUserData> removeAlreadyTaggedUsers(List<PojoUserData> friends) {
        List<PojoUserData> tempList = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++) {
            boolean present = false;
            for (int j = 0; j < taggedUserList.size(); j++) {
                if (friends.get(i).userId.equals(taggedUserList.get(j))) {
                    present = true;
                    break;
                }
            }
            if (!present) {
                tempList.add(friends.get(i));
            }
        }
        return tempList;
    }


    private void setTextListener() {
        et_content.addTextChangedListener(textWatcher);
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.create_post));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(PostCreationActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(PostCreationActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        rl_parent = (RelativeLayout) findViewById(R.id.rl_parent);
        tv_add_image = (TextView) findViewById(R.id.tv_add_image);
        tv_add_tag = (TextView) findViewById(R.id.tv_add_tag);

        //  rl_parent.getLayoutParams().height=(int)(displayMetrics.heightPixels*0.5f);

        iv_writer_dp = (ImageView) findViewById(R.id.iv_writer_dp);
        tv_writer_name = (TextView) findViewById(R.id.tv_writer_name);
        tv_place_and_designation = (TextView) findViewById(R.id.tv_place_and_designation);
        tv_privacy = (TextView) findViewById(R.id.tv_privacy);

        if (pojoGetOrganisationInfoResponseData != null) {
            Glide.with(PostCreationActivity.this.getApplicationContext())
                    .load(pojoGetOrganisationInfoResponseData.logo)
                    .apply(new RequestOptions().override(128, 128))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(iv_writer_dp);

            tv_writer_name.setText(pojoGetOrganisationInfoResponseData.name);
            tv_place_and_designation.setText(pojoGetOrganisationInfoResponseData.typename);
        } else {
            if (pojoGetNewsFeedResponseData != null
                    && (pojoGetNewsFeedResponseData.postType.equals("organisation_post")
                    || pojoGetNewsFeedResponseData.postType.equals("ad"))) {
                Glide.with(PostCreationActivity.this.getApplicationContext())
                        .load(pojoGetNewsFeedResponseData.organizationData.logo)
                        .apply(new RequestOptions().override(128, 128))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(iv_writer_dp);

                tv_writer_name.setText(pojoGetNewsFeedResponseData.organizationData.name);
                tv_place_and_designation.setText(pojoGetNewsFeedResponseData.organizationData.typename);
            } else {
                Glide.with(PostCreationActivity.this.getApplicationContext())
                        .load(pojoLoginResponseData.userPicture)
                        .apply(new RequestOptions().override(128, 128))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(iv_writer_dp);

                tv_writer_name.setText(pojoLoginResponseData.userFullname);
                tv_place_and_designation.setText(pojoLoginResponseData.userWorkTitle);
            }
        }

        popupWindow = new ListPopupWindow(PostCreationActivity.this);
        et_content = (EditTextCursorWatcher) findViewById(R.id.et_content);
        if (et_content.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(et_content, InputMethodManager.SHOW_FORCED);
        }


        //this is need to allow scrolling of edittext inside a scrollview
        /*et_content.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.getId() == R.id.et_content) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_UP:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }
                }
                return false;
            }
        });*/

        et_content.addOnSelectionChangedListener(new EditTextCursorWatcher.onSelectionChangedListener() {
            @Override
            public void onSelectionChanged(int selStart, int selEnd) {
                Spannable spannable = et_content.getText();
                for (int i = 0; i < colorSpanList.size(); i++) {
                    int startSpanIndex = spannable.getSpanStart(colorSpanList.get(i));
                    int endSpanIndex = spannable.getSpanEnd(colorSpanList.get(i));
                    if (endSpanIndex > 0 && startSpanIndex >= 0) {
                        if (selStart > startSpanIndex && selEnd <= endSpanIndex) {
                            // if (selEnd > startSpanIndex && selStart <= endSpanIndex) {
                            spannable.setSpan(appBGSpan, startSpanIndex, endSpanIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            et_content.setSelection(endSpanIndex);
                            break;

                        } else {
                            if (spannable.getSpans(0, spannable.length(), BackgroundColorSpan.class).length > 0) {
                                spannable.removeSpan(appBGSpan);
                            }
                 /*       spannable.setSpan(new BackgroundColorSpan(ContextCompat
                                        .getColor(PostCreationActivity.this, R.color.transparent)),
                                0, spannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
                        }
                    }
                }

              /*  int next=0;
                for(int i=0;i<spannable.length();i=next){
                    next = spannable.nextSpanTransition(i, spannable.length(), CharacterStyle.class);
                    ForegroundColorSpan[] colorSpans=spannable.getSpans(i,next,ForegroundColorSpan.class);
                    if(colorSpans.length>0&&colorSpans[0].getForegroundColor()==getResources().getColor(R.color.app_theme_medium)){
                        if(selStart>=i&&selStart<=next){
                            et_content.setSelection(i,next);
                        }
                    }
                }*/
            }
        });

        /*Spanned spanned = Html.fromHtml("This is <font color=\"#18AEB9\"> color text</font> and this is not");
        et_content.setText(spanned);
        et_content.getText().setSpan(spanWatcher,0, 0, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);*/

        Editable editable = et_content.getText();
        String htmlStr = Html.toHtml(editable);
        String str = editable.toString();


        tv_create = (TextView) findViewById(R.id.tv_create);
        pb_creating_post = (ProgressBar) findViewById(R.id.pb_creating_post);
        pb_creating_post.setVisibility(View.GONE);

        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);

        rl_web_preview_wrapper = (RelativeLayout) findViewById(R.id.rl_web_preview_wrapper);
        rl_web_preview_wrapper.setVisibility(View.GONE);
        iv_link_image = (ImageView) findViewById(R.id.iv_link_image);
        tv_link_title = (TextView) findViewById(R.id.tv_link_title);
        tv_website_name = (TextView) findViewById(R.id.tv_website_name);
        tv_link_description = (TextView) findViewById(R.id.tv_link_description);
        pb_load_preview = (ProgressBar) findViewById(R.id.pb_load_preview);
        setUpImageGallery();
    }

    private void setEditData() {
        boolean tagged = false;
        if (pojoGetNewsFeedResponseData.tagged.equals("1")) {
            tagged = true;
        }
        SpannableClickItem spannableClickItem = commonFunctions.setClickSpans(pojoGetNewsFeedResponseData.text,
                pojoGetNewsFeedResponseData.textPlain, tagged, null);

        taggedUserList = spannableClickItem.taggedUserList;
        SpannableString spannableString = new SpannableString(spannableClickItem.spannableString.toString());
        colorSpanList = new ArrayList<>();

        for (int i = 0; i < spannableClickItem.indexToIdList.size(); i++) {
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(ContextCompat
                    .getColor(PostCreationActivity.this, R.color.app_theme_medium));
            colorSpanList.add(foregroundColorSpan);
            spannableString.setSpan(foregroundColorSpan,
                    spannableClickItem.indexToIdList.get(i).spannedIndexStart,
                    spannableClickItem.indexToIdList.get(i).spannedIndexEnd,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        et_content.setText(spannableString, TextView.BufferType.SPANNABLE);

        et_content.setSelection(et_content.getText().length());

        //  et_content.setText(pojoGetNewsFeedResponseData.text);

        imagesUri.clear();
        if (pojoGetNewsFeedResponseData.photos != null) {
            for (int i = 0; i < pojoGetNewsFeedResponseData.photos.size(); i++) {
                imagesUri.add(Uri.parse(pojoGetNewsFeedResponseData.photos.get(i).source));
                imagePaths.add(pojoGetNewsFeedResponseData.photos.get(i).source);
            }
            if (imagesUri.size() == 1) {
                gridLayoutManager.setSpanCount(1);
            } else {
                gridLayoutManager.setSpanCount(2);
            }
            if (!imagesUri.isEmpty()) {
                rv_images.setVisibility(View.VISIBLE);
            }
        } else {
            rv_images.setVisibility(View.GONE);
        }
        postCreationAddedImageAdapter.notifyDataSetChanged();
        tv_create.setText(getString(R.string.done));
    }

    private void setUpImageGallery() {
        imagesUri = new ArrayList<>();
        imagePaths = new ArrayList<>();
        rv_images = (RecyclerView) findViewById(R.id.rv_images);

        rv_images.setNestedScrollingEnabled(false);
        gridLayoutManager = new GridLayoutManager(this, 1);
        rv_images.setLayoutManager(gridLayoutManager);
        postCreationAddedImageAdapter = new PostCreationAddedImageAdapter(this, imagesUri, imagePaths);
        rv_images.setAdapter(postCreationAddedImageAdapter);
        if (imagePaths.isEmpty()) {
            rv_images.setVisibility(View.GONE);
        } else {
            rv_images.setVisibility(View.VISIBLE);
        }

    }


    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (actionEnabled) {

                    switch (v.getId()) {
                        case R.id.tv_add_image:
                            requestStoragePermission(PICK_IMAGE_PERMISSION);

                        /*CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(PostCreationActivity.this);*/
                            break;

                        case R.id.tv_add_tag:
                            Spannable spannable = et_content.getText();
                            ((Editable) spannable).append("@");
                            break;

                        case R.id.tv_create:
                            createPost();
                            break;

                        case R.id.iv_cover:

                            break;


                        case R.id.tv_privacy:
                            popupMenu.show();
                            break;


                    }
                }

            }
        };
    }

    private void openCloseConfirmationDialog() {
        TextView tv_dialog_description, tv_no, tv_yes;
        exitConfirmationDialog = new BottomSheetDialog(PostCreationActivity.this);
        exitConfirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) exitConfirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_no = (TextView) exitConfirmationDialog.findViewById(R.id.tv_no);
        tv_yes = (TextView) exitConfirmationDialog.findViewById(R.id.tv_yes);

        tv_no.setText(getString(R.string.keep_editing));
        tv_yes.setText(getString(R.string.discard));

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitConfirmationDialog.dismiss();
            }
        });

        tv_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_dialog_description.setText(R.string.do_you_want_to_exit_dicard_post);
        exitConfirmationDialog.show();
    }

    private void setClickListeners() {
        tv_add_image.setOnClickListener(onClickListener);
        tv_add_tag.setOnClickListener(onClickListener);
        tv_create.setOnClickListener(onClickListener);
        tv_privacy.setOnClickListener(onClickListener);
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
    }


    private void createPost() {
        if (et_content.getText().toString().trim().isEmpty()) {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_content),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        } else {
            if (pojoGetNewsFeedResponseData == null) {
                if (organisationId != null) {
                    hitCreateOrganisationPostApi(user_id, organisationId, "organization",
                            et_content.getText().toString().trim(), "true", currentPrivacy,
                            imagePaths);
                } else {
                    hitCreatePostApi(user_id, "me", et_content.getText().toString().trim(),
                            "true", currentPrivacy, imagePaths);
                }

            } else {
                hitEditPostAPi(user_id, "post", et_content.getText().toString().trim(),
                        pojoGetNewsFeedResponseData.postId, imagePaths);
            }
        }
    }

    private void hitCreatePostApi(String user_id, String handle, String message,
                                  String multiple, String privacy, List<String> imagePaths) {

        HashMap<String, RequestBody> taggedUserMap = new HashMap<>();
        HashMap<String, RequestBody> posMap = new HashMap<>();
        RequestBody taggedBody = null;


        if (!taggedUserList.isEmpty()) {
            Spannable spannable = et_content.getText();
            nonModifiedSpannable = new SpannableString(spannable);

            for (int i = 0; i < spannable.length(); i++) {
                if (spannable.charAt(i) == '\n') {
                    if (i + 1 < spannable.length() && spannable.charAt(i + 1) == '\n') {
                        int j = i + 1;
                        while (j < spannable.length() && spannable.charAt(j) == '\n') {
                            j++;
                        }
                        ((Editable) spannable).replace(i + 2, j, "");
                    }
                }
            }


            ForegroundColorSpan colorSpan[] = spannable.getSpans(0, spannable.length(), ForegroundColorSpan.class);
            colorSpanList.clear();
            for (int i = 0; i < colorSpan.length; i++) {
                colorSpanList.add(colorSpan[i]);
            }

            for (int i = 0; i < taggedUserList.size(); i++) {
                int startSpanIndex = spannable.getSpanStart(colorSpanList.get(i));
                int endSpanIndex = spannable.getSpanEnd(colorSpanList.get(i));
                spannable = ((Editable) spannable).replace(startSpanIndex, endSpanIndex, "[" + taggedUserList.get(i) + "]");
            }

            message = spannable.toString().trim();
            taggedUserMap = new HashMap<>();
            for (int i = 0; i < taggedUserList.size(); i++) {
                taggedUserMap.put("tagged_users[" + i + "]", RequestBody.create(MediaType.parse("text/plain"),
                        taggedUserList.get(i)));
            }
            taggedBody = RequestBody.create(MediaType.parse("text/plain"), "1");


            for (int i = 0; i < colorSpanList.size(); i++) {
                int pos = spannable.getSpanStart(colorSpanList.get(i));
                posMap.put("pos[" + i + "]",
                        RequestBody.create(MediaType.parse("text/plain"), String.valueOf(pos)));
            }


        } else {
            String temp = "";
            for (int i = 0; i < message.length(); i++) {
                if (message.charAt(i) == '\n') {
                    if (i + 1 < message.length() && message.charAt(i + 1) == '\n') {
                        temp = temp + "\n";
                        int j = i + 1;
                        while (message.charAt(j) == '\n') {
                            i++;
                            j++;
                        }
                    }
                }
                temp = temp + message.charAt(i);

            }

            message = temp;
        }


        RequestBody userBody = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody handleBody = RequestBody.create(MediaType.parse("text/plain"), handle);
        RequestBody messageBody = RequestBody.create(MediaType.parse("text/plain"), message);
        RequestBody multipleBody = RequestBody.create(MediaType.parse("text/plain"), multiple);
        RequestBody privacyBody = RequestBody.create(MediaType.parse("text/plain"), privacy);

        List<MultipartBody.Part> imagePartList = new ArrayList<>();
        for (int i = 0; i < imagePaths.size(); i++) {
            MultipartBody.Part fileToUpload;
            if (!imagePaths.get(i).contains("http")) {
                File imgFile = new File(imagePaths.get(i));
                Uri contentUri = commonFunctions.getImageContentUri(getApplicationContext(), imgFile);
                RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(contentUri)), imgFile);
                fileToUpload = MultipartBody.Part.createFormData("photos[" + (imagePaths.size() - i - 1) + "]",
                        imgFile.getName(), mFile);
                imagePartList.add(fileToUpload);
            }
        }
        if (nonModifiedSpannable != null) {
            et_content.setText(nonModifiedSpannable);
        }

        Call<PojoCreatePostResponse> call = apiService.createPost(userBody, handleBody, messageBody,
                multipleBody, privacyBody, taggedBody, taggedUserMap, posMap, imagePartList);
        pb_creating_post.setVisibility(View.VISIBLE);
        et_content.setEnabled(false);
        rl_content_wrapper.setAlpha(0.5f);
        actionEnabled = false;

        call.enqueue(new Callback<PojoCreatePostResponse>() {
            @Override
            public void onResponse(Call<PojoCreatePostResponse> call, Response<PojoCreatePostResponse> response) {
                et_content.setEnabled(true);
                rl_content_wrapper.setAlpha(1f);
                actionEnabled = true;

                if (response.body() != null && response.body().data != null) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.post_created_succesfully),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    response.body().data.total = "0";
                    response.body().data.userPicture = "";
                    if (taggedUserList.isEmpty()) {
                        response.body().data.tagged = "0";
                    } else {
                        response.body().data.tagged = "1";
                    }
                    response.body().data.userWorkTitle = pojoLoginResponseData.userWorkTitle;
                    response.body().data.userWorkPlace = pojoLoginResponseData.userWorkPlace;
                    response.body().data.iLike = 0;
                    response.body().data.totalLikes = 0;
                    response.body().data.updatedAt = response.body().data.time;

                    if (response.body().data.photos != null) {
                        for (int i = 0; i < response.body().data.photos.size() && i < imagePaths.size(); i++) {
                            response.body().data.photos.get(i).source = imagePaths.get(i);
                        }
                    }

                    String dataStr = new Gson().toJson(
                            response.body().data, new TypeToken<PojoGetNewsFeedResponseData>() {
                            }.getType());

                    Intent newsFeedIntent = new Intent(Config.ADD_NEWS_FEED_ITEM);
                    newsFeedIntent.putExtra("data", dataStr);
                    LocalBroadcastManager.getInstance(PostCreationActivity.this)
                            .sendBroadcast(newsFeedIntent);

                    pb_creating_post.setVisibility(View.GONE);
                    setResult(RESULT_OK);

                    if (et_content.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    }
                    finish();
                } else {
                    et_content.setEnabled(true);
                    rl_content_wrapper.setAlpha(1f);
                    actionEnabled = true;
                    // Log error here since request failed
                    pb_creating_post.setVisibility(View.GONE);
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.something_went_wrong_try_again),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }

            }

            @Override
            public void onFailure(Call<PojoCreatePostResponse> call, Throwable t) {
                et_content.setEnabled(true);
                rl_content_wrapper.setAlpha(1f);
                actionEnabled = true;
                // Log error here since request failed
                pb_creating_post.setVisibility(View.GONE);
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void hitCreateOrganisationPostApi(String user_id, String organisation_id, String handle, String message,
                                              String multiple, String privacy, List<String> imagePaths) {

        HashMap<String, RequestBody> taggedUserMap = new HashMap<>();
        HashMap<String, RequestBody> posMap = new HashMap<>();
        RequestBody taggedBody = null;


        if (!taggedUserList.isEmpty()) {
            Spannable spannable = et_content.getText();
            nonModifiedSpannable = new SpannableString(spannable);

            for (int i = 0; i < spannable.length(); i++) {
                if (spannable.charAt(i) == '\n') {
                    if (i + 1 < spannable.length() && spannable.charAt(i + 1) == '\n') {
                        int j = i + 1;
                        while (j < spannable.length() && spannable.charAt(j) == '\n') {
                            j++;
                        }
                        ((Editable) spannable).replace(i + 2, j, "");
                    }
                }
            }


            ForegroundColorSpan colorSpan[] = spannable.getSpans(0, spannable.length(), ForegroundColorSpan.class);
            colorSpanList.clear();
            for (int i = 0; i < colorSpan.length; i++) {
                colorSpanList.add(colorSpan[i]);
            }

            for (int i = 0; i < taggedUserList.size(); i++) {
                int startSpanIndex = spannable.getSpanStart(colorSpanList.get(i));
                int endSpanIndex = spannable.getSpanEnd(colorSpanList.get(i));
                spannable = ((Editable) spannable).replace(startSpanIndex, endSpanIndex, "[" + taggedUserList.get(i) + "]");
            }

            message = spannable.toString().trim();
            taggedUserMap = new HashMap<>();
            for (int i = 0; i < taggedUserList.size(); i++) {
                taggedUserMap.put("tagged_users[" + i + "]", RequestBody.create(MediaType.parse("text/plain"),
                        taggedUserList.get(i)));
            }
            taggedBody = RequestBody.create(MediaType.parse("text/plain"), "1");


            for (int i = 0; i < colorSpanList.size(); i++) {
                int pos = spannable.getSpanStart(colorSpanList.get(i));
                posMap.put("pos[" + i + "]",
                        RequestBody.create(MediaType.parse("text/plain"), String.valueOf(pos)));
            }


        } else {
            String temp = "";
            for (int i = 0; i < message.length(); i++) {
                if (message.charAt(i) == '\n') {
                    if (i + 1 < message.length() && message.charAt(i + 1) == '\n') {
                        temp = temp + "\n";
                        int j = i + 1;
                        while (message.charAt(j) == '\n') {
                            i++;
                            j++;
                        }
                    }
                }
                temp = temp + message.charAt(i);

            }

            message = temp;
        }


        RequestBody userBody = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody organisationBody = RequestBody.create(MediaType.parse("text/plain"), organisation_id);
        RequestBody handleBody = RequestBody.create(MediaType.parse("text/plain"), handle);
        RequestBody messageBody = RequestBody.create(MediaType.parse("text/plain"), message);
        RequestBody multipleBody = RequestBody.create(MediaType.parse("text/plain"), multiple);
        RequestBody privacyBody = RequestBody.create(MediaType.parse("text/plain"), privacy);

        List<MultipartBody.Part> imagePartList = new ArrayList<>();
        for (int i = 0; i < imagePaths.size(); i++) {
            MultipartBody.Part fileToUpload;
            if (!imagePaths.get(i).contains("http")) {
                File imgFile = new File(imagePaths.get(i));
                Uri contentUri = commonFunctions.getImageContentUri(getApplicationContext(), imgFile);
                RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(contentUri)), imgFile);
                fileToUpload = MultipartBody.Part.createFormData("photos[" + (imagePaths.size() - i - 1) + "]",
                        imgFile.getName(), mFile);
                imagePartList.add(fileToUpload);
            }
        }
        if (nonModifiedSpannable != null) {
            et_content.setText(nonModifiedSpannable);
        }

        Call<PojoCreatePostResponse> call = apiService.createOrganisationPost(userBody, organisationBody
                , handleBody, messageBody,
                multipleBody, privacyBody, taggedBody, taggedUserMap, posMap, imagePartList);
        pb_creating_post.setVisibility(View.VISIBLE);
        et_content.setEnabled(false);
        rl_content_wrapper.setAlpha(0.5f);
        actionEnabled = false;

        call.enqueue(new Callback<PojoCreatePostResponse>() {
            @Override
            public void onResponse(Call<PojoCreatePostResponse> call, Response<PojoCreatePostResponse> response) {
                et_content.setEnabled(true);
                rl_content_wrapper.setAlpha(1f);
                actionEnabled = true;

                if (response.body() != null && response.body().data != null) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.post_created_succesfully),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    response.body().data.total = "0";
                    response.body().data.userPicture = "";
                    if (taggedUserList.isEmpty()) {
                        response.body().data.tagged = "0";
                    } else {
                        response.body().data.tagged = "1";
                    }
                    response.body().data.iLike = 0;
                    response.body().data.totalLikes = 0;
                    response.body().data.postType = "organisation_post";
                    response.body().data.organizationData = new PojoOrganizationData();
                    response.body().data.organizationData.id = pojoGetOrganisationInfoResponseData.id;
                    response.body().data.organizationData.logo = pojoGetOrganisationInfoResponseData.logo;
                    response.body().data.organizationData.city = pojoGetOrganisationInfoResponseData.city;
                    response.body().data.organizationData.description = pojoGetOrganisationInfoResponseData.description;
                    response.body().data.organizationData.name = pojoGetOrganisationInfoResponseData.name;
                    response.body().data.organizationData.typename = pojoGetOrganisationInfoResponseData.typename;
                    response.body().data.organizationData.type = pojoGetOrganisationInfoResponseData.type;

                    response.body().data.updatedAt = response.body().data.time;
                    if (response.body().data.photos != null) {
                        for (int i = 0; i < response.body().data.photos.size() && i < imagePaths.size(); i++) {
                            response.body().data.photos.get(i).source = imagePaths.get(i);
                        }
                    }

                    String dataStr = new Gson().toJson(
                            response.body().data, new TypeToken<PojoGetNewsFeedResponseData>() {
                            }.getType());

                    Intent newsFeedIntent = new Intent(Config.ADD_NEWS_FEED_ITEM);
                    newsFeedIntent.putExtra("data", dataStr);
                    LocalBroadcastManager.getInstance(PostCreationActivity.this)
                            .sendBroadcast(newsFeedIntent);

                    pb_creating_post.setVisibility(View.GONE);
                    setResult(RESULT_OK);

                    if (et_content.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    }
                    finish();
                } else {
                    et_content.setEnabled(true);
                    rl_content_wrapper.setAlpha(1f);
                    actionEnabled = true;
                    // Log error here since request failed
                    pb_creating_post.setVisibility(View.GONE);
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.something_went_wrong_try_again),
                            Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                }

            }

            @Override
            public void onFailure(Call<PojoCreatePostResponse> call, Throwable t) {
                et_content.setEnabled(true);
                rl_content_wrapper.setAlpha(1f);
                actionEnabled = true;
                // Log error here since request failed
                pb_creating_post.setVisibility(View.GONE);
                commonFunctions.setToastMessage(getApplicationContext(),
                        getString(R.string.something_went_wrong_try_again),
                        Toast.LENGTH_LONG, AppKeys.TOAST_USER);
            }
        });
    }


    private void hitEditPostAPi(String user_id, String type, String message, String id,
                                List<String> imagePaths) {
        HashMap<String, RequestBody> taggedUserMap = new HashMap<>();
        HashMap<String, RequestBody> posMap = new HashMap<>();
        RequestBody taggedBody = null;


        if (!taggedUserList.isEmpty()) {
            Spannable spannable = et_content.getText();
            nonModifiedSpannable = new SpannableString(spannable);

            for (int i = 0; i < spannable.length(); i++) {
                if (spannable.charAt(i) == '\n') {
                    if (i + 1 < spannable.length() && spannable.charAt(i + 1) == '\n') {
                        int j = i + 1;
                        while (j < spannable.length() && spannable.charAt(j) == '\n') {
                            j++;
                        }
                        ((Editable) spannable).replace(i + 2, j, "");
                    }
                }
            }

            ForegroundColorSpan colorSpan[] = spannable.getSpans(0, spannable.length(), ForegroundColorSpan.class);
            colorSpanList.clear();
            for (int i = 0; i < colorSpan.length; i++) {
                colorSpanList.add(colorSpan[i]);
            }

            for (int i = 0; i < taggedUserList.size(); i++) {
                int startSpanIndex = spannable.getSpanStart(colorSpanList.get(i));
                int endSpanIndex = spannable.getSpanEnd(colorSpanList.get(i));
                spannable = ((Editable) spannable).replace(startSpanIndex, endSpanIndex, "[" + taggedUserList.get(i) + "]");
            }

            message = spannable.toString().trim();
            taggedUserMap = new HashMap<>();
            for (int i = 0; i < taggedUserList.size(); i++) {
                taggedUserMap.put("tagged_users[" + i + "]", RequestBody.create(MediaType.parse("text/plain"),
                        taggedUserList.get(i)));
            }
            taggedBody = RequestBody.create(MediaType.parse("text/plain"), "1");


            for (int i = 0; i < colorSpanList.size(); i++) {
                posMap.put("pos[" + i + "]",
                        RequestBody.create(MediaType.parse("text/plain"), String.valueOf(spannable.getSpanStart(colorSpanList.get(i)))));
            }


        } else {
            String temp = "";
            for (int i = 0; i < message.length(); i++) {
                if (message.charAt(i) == '\n') {
                    if (i + 1 < message.length() && message.charAt(i + 1) == '\n') {
                        temp = temp + "\n";
                        int j = i + 1;
                        while (message.charAt(j) == '\n') {
                            i++;
                            j++;
                        }
                    }
                }
                temp = temp + message.charAt(i);

            }

            message = temp;
        }

        if (nonModifiedSpannable != null) {
            et_content.setText(nonModifiedSpannable);
        }

        RequestBody userBody = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody typeBody = RequestBody.create(MediaType.parse("text/plain"), type);
        RequestBody messageBody = RequestBody.create(MediaType.parse("text/plain"), message);
        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), id);


        List<MultipartBody.Part> imagePartList = null;
        for (int i = 0; i < imagePaths.size(); i++) {
            imagePartList = new ArrayList<>();
            MultipartBody.Part fileToUpload;
            if (!imagePaths.get(i).contains("http")) {
                File imgFile = new File(imagePaths.get(i));
                RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(imgFile)), imgFile);
                fileToUpload = MultipartBody.Part.createFormData("photos[" + i + "]", imgFile.getName(), mFile);
                imagePartList.add(fileToUpload);
            }
        }

        Call<PojoCreatePostResponse> call = apiService.editPost(userBody, typeBody, idBody, messageBody,
                taggedBody, taggedUserMap, posMap, imagePartList);
        pb_creating_post.setVisibility(View.VISIBLE);
        et_content.setEnabled(false);
        rl_content_wrapper.setAlpha(0.6f);
        actionEnabled = false;
        call.enqueue(new Callback<PojoCreatePostResponse>() {
            @Override
            public void onResponse(Call<PojoCreatePostResponse> call, Response<PojoCreatePostResponse> response) {
                et_content.setEnabled(true);
                rl_content_wrapper.setAlpha(1f);
                actionEnabled = true;

                if (response.body() != null && response.body().data != null) {
                    //commonFunctions.setToastMessage(CreateChatGroupActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    response.body().data.total = "0";
                    response.body().data.userPicture = "";
                    if (taggedUserList.isEmpty()) {
                        response.body().data.tagged = "0";
                    } else {
                        response.body().data.tagged = "1";
                    }
                    response.body().data.userWorkTitle = pojoLoginResponseData.userWorkTitle;
                    response.body().data.userWorkPlace = pojoLoginResponseData.userWorkPlace;
                    response.body().data.iLike = 0;
                    response.body().data.totalLikes = 0;
                    if (organisationId != null) {
                        response.body().data.postType = "organisation_post";

                    }

                    String dataStr = new Gson().toJson(
                            response.body().data, new TypeToken<PojoGetNewsFeedResponseData>() {
                            }.getType());

                    Intent newsFeedIntent = new Intent(Config.EDIT_NEWS_FEED_ITEM);
                    newsFeedIntent.putExtra("data", dataStr);
                    LocalBroadcastManager.getInstance(PostCreationActivity.this)
                            .sendBroadcast(newsFeedIntent);

                    pb_creating_post.setVisibility(View.GONE);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("data", dataStr);
                    setResult(RESULT_OK, resultIntent);
                    if (et_content.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_content.getWindowToken(), 0);
                    }
                    finish();

                }
            }

            @Override
            public void onFailure(Call<PojoCreatePostResponse> call, Throwable t) {
                et_content.setEnabled(true);
                rl_content_wrapper.setAlpha(1f);
                actionEnabled = true;

                // Log error here since request failed
                pb_creating_post.setVisibility(View.GONE);
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });

    }

    private void hitDeletePostPhoto(String user_id, String post_id, String photo_id) {
        Call<PojoNoDataResponse> call = apiService.deletePostPhoto(user_id, post_id, photo_id);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = response.body().message;
                if (response.body() != null) {
                    //commonFunctions.setToastMessage(CreateChatGroupActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });

    }

    private void requestStoragePermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
           /* if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else*/
            {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        requestCode);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            switch (requestCode) {
                case PICK_IMAGE_PERMISSION:
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(PostCreationActivity.this);
                    break;

                case SHARED_INTENT_IMAGE_PERMISSION:
                    handleSharedBroadCast();
                    break;
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PICK_IMAGE_PERMISSION:
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(PostCreationActivity.this);
                    break;

                case SHARED_INTENT_IMAGE_PERMISSION:
                    handleSharedBroadCast();
                    break;

            }
            return;


            // other 'case' lines to check for other
            // permissions this app might request.

        }
    }

    public void handleSharedBroadCast() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent); // Handle text being sent
            } else if (type.startsWith("image/")) {
                handleSendImage(intent); // Handle single image being sent
            }
        } else if (Intent.ACTION_SEND_MULTIPLE.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                handleSendMultipleImages(intent); // Handle multiple images being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            et_content.setText(sharedText);

        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (text != null && !text.isEmpty()) {
                et_content.setText(text);
            }
            setUpImageGallery();
            imagesUri.clear();
            imagePaths.clear();
            File photo = commonFunctions.getScaledDownImage(getFilePathFromURI(PostCreationActivity.this, imageUri));
            imagesUri.add(Uri.fromFile(photo));
            imagePaths.add(Uri.fromFile(photo).getPath());
            postCreationAddedImageAdapter.notifyDataSetChanged();
            if (imagePaths.isEmpty()) {
                rv_images.setVisibility(View.GONE);
            } else {
                rv_images.setVisibility(View.VISIBLE);
            }
            // Update UI to reflect image being shared
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> imageUris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (imageUris != null) {
            String text = intent.getStringExtra(Intent.EXTRA_TEXT);
            if (text != null && !text.isEmpty()) {
                et_content.setText(text);
            }
            setUpImageGallery();
            imagesUri.clear();
            imagePaths.clear();
            for (int i = 0; i < imageUris.size(); i++) {
                File photo = commonFunctions.getScaledDownImage(getFilePathFromURI(PostCreationActivity.this, imageUris.get(i)));
                imagesUri.add(Uri.fromFile(photo));
                imagePaths.add(Uri.fromFile(photo).getPath());
            }
            gridLayoutManager.setSpanCount(2);
            postCreationAddedImageAdapter.notifyDataSetChanged();
            if (imagePaths.isEmpty()) {
                rv_images.setVisibility(View.GONE);
            } else {
                rv_images.setVisibility(View.VISIBLE);
            }
            // Update UI to reflect multiple images being shared
        }
    }

    public String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path
        String fileName = getFileName(contentUri);
        if (!TextUtils.isEmpty(fileName)) {
            File copyFile = new File(getCacheDir(), fileName);
            copy(context, contentUri, copyFile);
            return copyFile.getAbsolutePath();
        }
        return null;
    }

    public static String getFileName(Uri uri) {
        if (uri == null) return null;
        String fileName = null;
        String path = uri.getPath();
        int cut = path.lastIndexOf('/');
        if (cut != -1) {
            fileName = path.substring(cut + 1);
        }
        return fileName;
    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            IOUtils.copyStream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                rv_images.setVisibility(View.VISIBLE);
                File photo = commonFunctions.getScaledDownImage(result.getUri().getPath());

                imagePaths.add(Uri.fromFile(photo).getPath());
                imagesUri.add(Uri.fromFile(photo));
                if (imagesUri.size() == 1) {
                    gridLayoutManager.setSpanCount(1);
                } else {
                    gridLayoutManager.setSpanCount(2);
                }
                postCreationAddedImageAdapter.notifyDataSetChanged();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


   /* @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.create_post_menu_options, menu);
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            /*case R.id.post:
                createPost();
                break;*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!et_content.getText().toString().isEmpty()) {
            openCloseConfirmationDialog();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void postCreationImageMethodCallback(int position, String type) {
        switch (type) {
            case "remove":
                if (imagePaths.get(position).contains("http")) {
                    if (pojoGetNewsFeedResponseData != null
                            && pojoGetNewsFeedResponseData.photos != null) {
                        hitDeletePostPhoto(user_id, pojoGetNewsFeedResponseData.postId,
                                pojoGetNewsFeedResponseData.photos.get(position).photoId);
                        pojoGetNewsFeedResponseData.photos.remove(position);
                    }
                }

                imagesUri.remove(position);
                imagePaths.remove(position);
                postCreationAddedImageAdapter.notifyDataSetChanged();
                if (imagePaths.isEmpty()) {
                    rv_images.setVisibility(View.GONE);
                } else {
                    rv_images.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    public int compareStrings(String str1, String str2) {
        int len = (str1.length() > str2.length()) ? str2.length() : str1.length();
        for (int i = 0; i < len; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    public void onPause() {
        if (exitConfirmationDialog != null) {
            exitConfirmationDialog.dismiss();
        }


        super.onPause();
    }

}
