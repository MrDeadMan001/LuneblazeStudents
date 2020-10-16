package com.avadna.luneblaze.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.registration.InterestHierarchyActivity;
import com.avadna.luneblaze.adapters.InterestSearchResultAdapter;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponseData;
import com.avadna.luneblaze.pojo.pojoArticle.PojoCreateArticleResponse;
import com.avadna.luneblaze.pojo.pojoArticle.PojoGetArticleDetailsResponse;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoOrigin;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateArticleActivity extends AppBaseActivity implements
        InterestSearchResultAdapter.InterestResultAdapterCallback,
        TaggedInterestAdapter.TaggedInterestAdapterCallback {
    ApiInterface apiService;
    ActionBar actionBar;

    EditText et_article_title, et_article_content;
    TextView tv_create_article;
    TextView tv_tag_interests, tv_add_image;
    ImageButton ib_remove_photo;
    RelativeLayout rl_image_parent;
    ImageView iv_article_cover;
    TextView tv_auto_gen_image;
    File cover_photo;


    View.OnClickListener onClickListener;
    View.OnFocusChangeListener onFocusChangeListener;
    int PLACE_PICKER_REQUEST = 1;


    List<PojoGetInterestListResponseDataListItem> selectedInterestList;
    TaggedInterestAdapter taggedInterestAdapter;
    int[] location = new int[2];
    RecyclerView rv_added_interests;
    GridLayoutManager gridLayoutManager;
    TextView tv_length_getter;
    DisplayMetrics displayMetrics;
    int screenHeight, screenWidth;
    int statusBarHeight = 0, actionBarHeight = 0;


    //  RecyclerView rv_interest_search_result;
    // InterestSearchResultAdapter interestSearchResultAdapter;
    //  List<PojoSearchInterestWithTextResponseData> interestSearchResultList;
    //  EditText et_search_interest;
    //  LinearLayout ll_interest_search_wrapper;
    NestedScrollView sv_parent_wrapper;
    ProgressBar pb_creating;
    String type = "new";
    PojoGetArticleDetailsResponse pojoGetArticleDetailsResponse;

    boolean imgChanged = false;

    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    PojoLoginResponseData pojoLoginResponseData;
    String user_id = "";
    private static final int PICKIMAGE_RESULT_CODE = 1;
    //  String imagePath = "";
    boolean isCreateApiCalled = false;
    private static final int SELECT_INTEREST_CODE = 2;

    final int ARTICLE_TITLE_MAX_LENGTH = 125;
    final int ARTICLE_CONTENT_MAX_LENGTH = 3000;

    final int PICK_IMAGE_REQUEST = 9124;
    final int AUTO_GENERATE_IMAGE_REQUEST = 4070;

    NotificationManagerCompat notificationManager;
    NotificationCompat.Builder builder;


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_article);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(CreateArticleActivity.this);
        commonFunctions = new CommonFunctions(this);
        pojoLoginResponseData = preferenceUtils.getUserLoginData();
        if (pojoLoginResponseData == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        user_id = preferenceUtils.get_user_id();

        type = getIntent().getStringExtra("userType");
        if (type != null) {
            Gson gson = new Gson();
            pojoGetArticleDetailsResponse = gson.fromJson(getIntent()
                    .getStringExtra("data"), PojoGetArticleDetailsResponse.class);
        } else {
            type = "new";
        }

        initDispMetrics();
        setUpActionBar();
        initBarSizes();
        initViews();
        initClickListeners();
        setClickListeners();
        setTextWatcher();

        notificationManager = NotificationManagerCompat.from(this);


        //  setTextChangedListeners();
        // initFocusChangeListener();
        //  setFocusChangeListener();

    }

    private void setTextWatcher() {
        et_article_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > ARTICLE_TITLE_MAX_LENGTH) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.article_title_cannot_be_longer_than_125), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                    et_article_title.setText(charSequence.subSequence(0, ARTICLE_TITLE_MAX_LENGTH));
                    et_article_title.setSelection(et_article_title.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_article_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > ARTICLE_CONTENT_MAX_LENGTH) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.article_cannot_be_longer_than_3000), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                    et_article_content.setText(charSequence.subSequence(0, ARTICLE_CONTENT_MAX_LENGTH));
                    et_article_content.setSelection(et_article_content.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
    }

    private void setUpActionBar() {
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.create_article));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(CreateArticleActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(CreateArticleActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        initAddedInterestList();
        //   initInterestResultList();
        et_article_title = (EditText) findViewById(R.id.et_article_title);
        et_article_content = (EditText) findViewById(R.id.et_article_content);

        //this is need to allow scrolling of edittext inside a scrollview
       /* et_article_content.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (et_article_content.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });*/

        tv_create_article = (TextView) findViewById(R.id.tv_create_article);
        tv_tag_interests = (TextView) findViewById(R.id.tv_tag_interests);
        tv_add_image = (TextView) findViewById(R.id.tv_add_image);
        ib_remove_photo = (ImageButton) findViewById(R.id.ib_remove_photo);

        rl_image_parent = (RelativeLayout) findViewById(R.id.rl_image_parent);
        rl_image_parent.setVisibility(View.GONE);

        iv_article_cover = (ImageView) findViewById(R.id.iv_article_cover);
        tv_auto_gen_image = (TextView) findViewById(R.id.tv_auto_gen_image);
        tv_auto_gen_image.setDrawingCacheEnabled(true);

        //  et_search_interest = (EditText) findViewById(R.id.et_search_interest);
        tv_tag_interests = (TextView) findViewById(R.id.tv_tag_interests);
        //  ll_interest_search_wrapper = (LinearLayout) findViewById(R.id.ll_interest_search_wrapper);

        if (type.equals("edit")) {
            //  ll_interest_search_wrapper.setVisibility(View.GONE);
            setDataOnViews();
            tv_create_article.setText(R.string.update);
        }

        sv_parent_wrapper = (NestedScrollView) findViewById(R.id.sv_parent_wrapper);
        pb_creating = (ProgressBar) findViewById(R.id.pb_creating);
        pb_creating.setVisibility(View.GONE);
    }


    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_add_image:
                        requestStoragePermission(PICK_IMAGE_REQUEST);
                       /* CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(CreateArticleActivity.this);*/
                        break;
                    case R.id.tv_create_article:

                        requestStoragePermission(AUTO_GENERATE_IMAGE_REQUEST);


                        break;

                    case R.id.ib_remove_photo:
                        rl_image_parent.setVisibility(View.GONE);
                        cover_photo = null;
                        imgChanged = true;
                        // imagePath="";
                        break;


                    case R.id.tv_tag_interests:
                        Intent pickInterestIntent = new Intent(CreateArticleActivity.this,
                                InterestHierarchyActivity.class);
                        Type type = new TypeToken<ArrayList<PojoGetInterestListResponseDataListItem>>() {
                        }.getType();
                        Gson gson = new Gson();
                        String dataStr = gson.toJson(selectedInterestList, type);
                        pickInterestIntent.putExtra("data", dataStr);
                        pickInterestIntent.putExtra("type", AppKeys.SELECT_INTEREST);
                        startActivityForResult(pickInterestIntent, SELECT_INTEREST_CODE);
                        break;
                }
            }
        };
    }

    private void initAddedInterestList() {
        selectedInterestList = new ArrayList<>();
        tv_length_getter = (TextView) findViewById(R.id.tv_length_getter);
        rv_added_interests = (RecyclerView) findViewById(R.id.rv_added_interests);
        updateSelectedInterestList();
    }

    private void updateSelectedInterestList() {
        gridLayoutManager = (new GridLayoutManager(CreateArticleActivity.this, 200));
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                if (!selectedInterestList.isEmpty()) {
                    tv_length_getter.setText(selectedInterestList.get(position).text);
                }
                tv_length_getter.measure(size.x, size.y);
                int textWidth = tv_length_getter.getMeasuredWidth();

                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (screenWidth * 0.9f);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });

        rv_added_interests.setLayoutManager(gridLayoutManager);
        taggedInterestAdapter = new TaggedInterestAdapter(selectedInterestList.size(),
                CreateArticleActivity.this, selectedInterestList, 2);
        rv_added_interests.setAdapter(taggedInterestAdapter);
    }

    private void setDataOnViews() {
        et_article_title.setText(pojoGetArticleDetailsResponse.data.title);
        et_article_content.setText(Html.fromHtml(pojoGetArticleDetailsResponse.data.description));
        if (!pojoGetArticleDetailsResponse.data.coverPhoto.isEmpty()) {
            rl_image_parent.setVisibility(View.VISIBLE);
            Glide.with(CreateArticleActivity.this.getApplicationContext())
                    .load(pojoGetArticleDetailsResponse.data.coverPhoto)
                    .into(iv_article_cover);


        }
    }

    private void setClickListeners() {
        tv_create_article.setOnClickListener(onClickListener);
        tv_add_image.setOnClickListener(onClickListener);
        ib_remove_photo.setOnClickListener(onClickListener);
        iv_article_cover.setOnClickListener(onClickListener);
        tv_tag_interests.setOnClickListener(onClickListener);
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

      /*  // status bar height
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        // action bar height
        int actionBarHeight = 0;
        final TypedArray styledAttributes = getActivity().getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize }
        );
        actionBarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        // navigation bar height
        int navigationBarHeight = 0;
        int resourceId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId);
        }*/
    }

    private void initFocusChangeListener() {
        onFocusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                switch (v.getId()) {
                    case R.id.et_interest_name:
                        if (hasFocus) {
                            //  ll_interest_search_wrapper.getLocationOnScreen(location);
                            int x = location[0];
                            int y = location[1];
                            sv_parent_wrapper.smoothScrollBy(0, y - statusBarHeight - actionBarHeight);
                        }
                        break;
                }
            }
        };
    }

    /*private void setFocusChangeListener() {
        et_search_interest.setOnFocusChangeListener(onFocusChangeListener);
    }

    private void setTextChangedListeners() {
        et_search_interest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (et_search_interest.getText().toString().trim().isEmpty()) {
                    interestSearchResultList.clear();
                    interestSearchResultAdapter.notifyDataSetChanged();
                } else {
                    hitInterestSearchWithTextApi(et_search_interest.getText().toString().trim());
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }*/




  /*  private void initInterestResultList() {
        interestSearchResultList = new ArrayList<>();
        rv_interest_search_result = (RecyclerView) findViewById(R.id.rv_interest_search_result);
        rv_interest_search_result.setLayoutManager
                (new LinearLayoutManager(CreateArticleActivity.this,
                        LinearLayoutManager.VERTICAL, false));
        rv_interest_search_result.setNestedScrollingEnabled(false);
        interestSearchResultAdapter = new InterestSearchResultAdapter(this, interestSearchResultList);
        rv_interest_search_result.setAdapter(interestSearchResultAdapter);


    }*/

    private void updateArticle() {
        String articleTitle = et_article_title.getText().toString().trim();
        String articleContent = et_article_content.getText().toString().trim();

        if (articleTitle.isEmpty()) {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_article_title),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        } else if (articleContent.isEmpty()) {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_article_content),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        } else {
            hitUpdateArticlePhotoApi(user_id, "article", pojoGetArticleDetailsResponse.data.articlesId,
                    articleTitle, articleContent);
        }
    }


    private void createArticle() {
      /*  if (cover_photo == null) {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_add_a_cover_photo),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        } else*/
        if (et_article_title.getText().toString().trim().isEmpty()) {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_article_title),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        } else if (et_article_content.getText().toString().trim().isEmpty()) {
            commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.please_enter_article_content),
                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
        } else {
            hitCreateArticleApi(user_id, et_article_title.getText().toString().trim(),
                    et_article_content.getText().toString().trim(), selectedInterestList);
        }
    }

    private void hitCreateArticleApi(String user_id, String title, String description,
                                     List<PojoGetInterestListResponseDataListItem> selectedInterestList) {
        MultipartBody.Part fileToUploadBody;
        if (cover_photo == null) {
            cover_photo = textAsBitmap(title, 60);
        }
        // file = getScaledDownImage(file);
        Uri contentUri = commonFunctions.getImageContentUri(getApplicationContext(), cover_photo);

        //ProgressRequestBody fileBody = new ProgressRequestBody(cover_photo, this);
        RequestBody fileBody = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(contentUri)), cover_photo);
        fileToUploadBody = MultipartBody.Part.createFormData("cover_photo", cover_photo.getName(), fileBody);

        final RequestBody userBody = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);

        HashMap<String, RequestBody> interestIdMap = new HashMap<>();
        for (int i = 0; i < selectedInterestList.size(); i++) {
            interestIdMap.put("interest_dropdown[" + i + "]", RequestBody.create(MediaType.parse("text/plain"),
                    selectedInterestList.get(i).interestId));
        }

        Call<PojoCreateArticleResponse> call = apiService.createArticle(userBody, titleBody,
                descriptionBody, fileToUploadBody, interestIdMap);
        if (!isCreateApiCalled) {
            isCreateApiCalled = true;
            pb_creating.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<PojoCreateArticleResponse>() {
                @Override
                public void onResponse(Call<PojoCreateArticleResponse> call, Response<PojoCreateArticleResponse> response) {
                    if (response.body() != null) {
                        commonFunctions.setToastMessage(getApplicationContext(),
                                getString(R.string.article_created_successfully), Toast.LENGTH_LONG,
                                AppKeys.TOAST_USER);
                        addTempArticleItemToNewsFeed(response.body().articleId);
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(),
                                getString(R.string.something_went_wrong_try_again),
                                Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                    }
                    isCreateApiCalled = false;
                    pb_creating.setVisibility(View.GONE);

                }

                @Override
                public void onFailure(Call<PojoCreateArticleResponse> call, Throwable t) {

                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isCreateApiCalled = false;
                    pb_creating.setVisibility(View.GONE);

                }
            });
        }
    }


    public File textAsBitmap(String text, float textSize) {

      /*  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(textSize);
        paint.setColor(ContextCompat.getColor(this,R.color.app_theme_medium));
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.5f); // round
        int height = (int) (baseline + paint.descent() + 0.5f);
        Bitmap image = Bitmap.createBitmap(720, 512, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawColor(ContextCompat.getColor(this,R.color.white));

        int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;
        canvas.drawText(text, (720-width)/2f, yPos, paint);*/

        tv_auto_gen_image.setText(text);
        tv_auto_gen_image.buildDrawingCache();
        Bitmap image = tv_auto_gen_image.getDrawingCache();

        // we create an scaled bitmap so it reduces the image, not just trim it
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // compress to the format you want, JPEG, PNG...
        // 70 is the 0-100 quality percentage
        image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        // we save the file, at least until we have made use of it
        Calendar cal = Calendar.getInstance();
        File f = new File(getCacheDir(), cal.getTime() + "article_temp_img.jpg");
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //write the bytes in file
        FileOutputStream fo = null;
        try {
            fo = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fo.write(outStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        // remember close de FileOutput
        try {
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f;
    }


    private void addTempArticleItemToNewsFeed(Integer articleId) {
        PojoGetNewsFeedResponseData pojoGetNewsFeedResponseData = new PojoGetNewsFeedResponseData();
        pojoGetNewsFeedResponseData.postType = "article";
        pojoGetNewsFeedResponseData.origin = new PojoOrigin();
        pojoGetNewsFeedResponseData.origin.createdBy = new PojoUserData();
        pojoGetNewsFeedResponseData.origin.createdBy.userFullname = pojoLoginResponseData.userFullname;
        pojoGetNewsFeedResponseData.origin.createdBy.userWorkTitle = pojoLoginResponseData.userWorkTitle;
        pojoGetNewsFeedResponseData.origin.createdBy.userWorkPlace = pojoLoginResponseData.userWorkPlace;
        pojoGetNewsFeedResponseData.origin.createdBy.userPicture = pojoLoginResponseData.userPicture;
        pojoGetNewsFeedResponseData.origin.createdBy.userId = pojoLoginResponseData.userId;

        pojoGetNewsFeedResponseData.total = "0";
        pojoGetNewsFeedResponseData.origin.articlesId = String.valueOf(articleId);
        pojoGetNewsFeedResponseData.origin.coverPhoto = cover_photo.getAbsolutePath();
        pojoGetNewsFeedResponseData.origin.title = et_article_title.getText().toString().trim();
        pojoGetNewsFeedResponseData.origin.addedOn = getString(R.string.just_now);
        pojoGetNewsFeedResponseData.userFullname = pojoLoginResponseData.userFullname;
        pojoGetNewsFeedResponseData.origin.likes = "0";
        pojoGetNewsFeedResponseData.origin.totalComments = 0;
        pojoGetNewsFeedResponseData.origin.shares = "0";
        pojoGetNewsFeedResponseData.origin.iLike = 0;
        pojoGetNewsFeedResponseData.authorId = user_id;
        pojoGetNewsFeedResponseData.postAuthorName = pojoLoginResponseData.userFullname;
        pojoGetNewsFeedResponseData.postAuthorPicture = pojoLoginResponseData.userPicture;

        String dataStr = new Gson().toJson(
                pojoGetNewsFeedResponseData, new TypeToken<PojoGetNewsFeedResponseData>() {
                }.getType());

        Intent newsFeedIntent = new Intent(Config.ADD_NEWS_FEED_ITEM);
        newsFeedIntent.putExtra("data", dataStr);
        LocalBroadcastManager.getInstance(CreateArticleActivity.this)
                .sendBroadcast(newsFeedIntent);


    }


    private void hitUpdateArticlePhotoApi(String user_id, String type, String articlesId, String title,
                                          String articleContent) {
        RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), user_id);
        RequestBody typeBody = RequestBody.create(MediaType.parse("text/plain"), type);
        RequestBody articleIdBody = RequestBody.create(MediaType.parse("text/plain"), articlesId);
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody contentBOdy = RequestBody.create(MediaType.parse("text/plain"), articleContent);

        MultipartBody.Part fileToUpload = null;

        if (imgChanged) {
            if (cover_photo == null) {
                cover_photo = textAsBitmap(title, 60);
            }
            RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(cover_photo)), cover_photo);
            fileToUpload = MultipartBody.Part.createFormData("photo", cover_photo.getName(), mFile);
        }

        Call<PojoNoDataResponse> call = apiService.editArticlephoto(userIdBody, typeBody, articleIdBody,
                titleBody, contentBOdy, fileToUpload);
        commonFunctions.openProgressDialog(getString(R.string.updating_article));

        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                commonFunctions.closeProgressDialog();
                if (response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                commonFunctions.closeProgressDialog();
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });

    }


   /* private void hitInterestSearchWithTextApi(String text) {
        String offset = "0";
        boolean allFieldsOk = !offset.isEmpty() && !text.isEmpty();

        if (allFieldsOk) {
            Call<PojoSearchInterestWithTextResponse> call = apiService.searchInterestWithTextApi(text, offset);
            call.enqueue(new Callback<PojoSearchInterestWithTextResponse>() {
                @Override
                public void onResponse(Call<PojoSearchInterestWithTextResponse> call, Response<PojoSearchInterestWithTextResponse> response) {

                    String message = response.body().message;
                    if (response.body().data != null) {
                        interestSearchResultList.clear();
                        for (int i = 0; i < response.body().data.size() && i < 4; i++) {
                            interestSearchResultList.add(response.body().data.get(i));
                        }
                        // interestSearchResultList.addAll(response.body().data);
                        interestSearchResultAdapter.notifyDataSetChanged();
                        ll_interest_search_wrapper.getLocationOnScreen(location);
                        int x = location[0];
                        int y = location[1];
                        rl_content_wrapper.smoothScrollBy(0, y - statusBarHeight - actionBarHeight);
                    } else {
                        commonFunctions.setToastMessage(getApplicationContext(), "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    }

                }

                @Override
                public void onFailure(Call<PojoSearchInterestWithTextResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

                }
            });
        }
    }*/

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
                                android.Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            {
                if (requestCode == PICK_IMAGE_REQUEST) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(CreateArticleActivity.this);
                } else if (requestCode == AUTO_GENERATE_IMAGE_REQUEST) {
                    if (type.equals("edit")) {
                        updateArticle();
                    } else {
                        createArticle();
                    }
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PICK_IMAGE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .start(CreateArticleActivity.this);
                } else {

                }
                return;
            }

            case AUTO_GENERATE_IMAGE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (type.equals("edit")) {
                        updateArticle();
                    } else {
                        createArticle();
                    }
                } else {

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                cover_photo = commonFunctions.getScaledDownImage(result.getUri().getPath());
                iv_article_cover.setImageURI(Uri.fromFile(cover_photo));
                imgChanged = true;
                rl_image_parent.setVisibility(View.VISIBLE);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == SELECT_INTEREST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Type type = new TypeToken<ArrayList<PojoGetInterestListResponseDataListItem>>() {
                }.getType();
                String dataStr = data.getStringExtra("data");
                ArrayList<PojoGetInterestListResponseDataListItem> tempList;
                Gson gson = new Gson();
                tempList = gson.fromJson(dataStr, type);
                selectedInterestList.clear();
                selectedInterestList.addAll(tempList);
                taggedInterestAdapter.notifyDataSetChanged();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void interestResultItemClickCallback(int position, PojoSearchInterestWithTextResponseData item) {
        PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem
                = new PojoGetInterestListResponseDataListItem(item.interestId, item.parentId, item.text, 0);

        for (int i = 0; i < selectedInterestList.size(); i++) {
            //if item already present in list then return
            if (selectedInterestList.get(i).interestId
                    .equals(item.interestId)) {
                return;
            }
        }

        //add 10 interests at max
        if (selectedInterestList.size() < 10) {
            selectedInterestList.add(pojoGetInterestListResponseDataListItem);
            taggedInterestAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem
            pojoGetInterestListResponseDataListItem) {
        selectedInterestList.remove(position);
        taggedInterestAdapter.notifyDataSetChanged();
    }


}

