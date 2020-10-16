package com.avadna.luneblaze.activities.sessionCreation;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.registration.InterestHierarchyActivity;
import com.avadna.luneblaze.adapters.InterestSearchResultAdapter;
import com.avadna.luneblaze.adapters.TaggedInterestAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.pojo.PojoCreateSession;
import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoSearchInterestWithTextResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
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
import java.util.List;

public class SessionCreationPart2 extends AppBaseActivity implements
        InterestSearchResultAdapter.InterestResultAdapterCallback,
        TaggedInterestAdapter.TaggedInterestAdapterCallback {
    String type;
    ApiInterface apiService;
    CommonFunctions commonFunctions;
    ScrollView sv_wrapper;
    View.OnClickListener onClickListener;
    View.OnFocusChangeListener onFocusChangeListener;
    int statusBarHeight = 0, actionBarHeight = 0;
    int PLACE_PICKER_REQUEST = 1;
    TextView tv_auto_gen_image;
    ImageView iv_cover;
    TextView tv_add_image;
    ImageButton ib_remove_photo;

    RecyclerView rv_added_interests;
    TaggedInterestAdapter taggedInterestAdapter;
    TextView tv_interests;
    GridLayoutManager gridLayoutManager;
    TextView tv_length_getter;
    DisplayMetrics displayMetrics;
    List<PojoGetInterestListResponseDataListItem> selectedInterestList;
    int screenHeight, screenWidth;

    Spinner sp_purpose_list;
    ArrayAdapter<String> purposeSpinnerAdapter;
    List<String> purpose_options;

    TextView tv_next;
    PojoCreateSession pojoCreateSession;
    String cover_photo_path = "";
    File cover_photo;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private static final int SELECT_INTEREST_CODE = 2;

    final int AUTO_GENERATE_IMAGE = 4270;
    final int PICK_IMAGE_REQUEST = 9120;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_creation_part2);
        type = getIntent().getStringExtra("type");

        Type jsonType = new TypeToken<PojoCreateSession>() {
        }.getType();
        pojoCreateSession = new Gson().fromJson(getIntent().getStringExtra("data"), jsonType);

        commonFunctions = new CommonFunctions(this);
        setUpActionBar();
        initBarSizes();
        initDispMetrics();
        initApis();
        initViews();
        initLists();
        initClickListener();
        setClickListener();

        initBroadCastReceiver();


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.SESSION_CREATION_FINISHED));


    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.SESSION_CREATION_FINISHED)) {
                    finish();
                }
            }
        };
    }

    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setTitle(getString(R.string.create_session));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(SessionCreationPart2.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(SessionCreationPart2.this, R.color.status_bar_color));
            }
        }
    }

    private void initDispMetrics() {
        displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
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

    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {

                    case R.id.tv_interests: {
                        Intent pickInterestIntent = new Intent(SessionCreationPart2.this,
                                InterestHierarchyActivity.class);
                        Type type = new TypeToken<ArrayList<PojoGetInterestListResponseDataListItem>>() {
                        }.getType();
                        Gson gson = new Gson();
                        String dataStr = gson.toJson(selectedInterestList, type);
                        pickInterestIntent.putExtra("data", dataStr);
                        pickInterestIntent.putExtra("type", AppKeys.SELECT_INTEREST);
                        startActivityForResult(pickInterestIntent, SELECT_INTEREST_CODE);
                    }
                    break;

                    case R.id.tv_next:
                        if (cover_photo_path.isEmpty()) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_add_a_cover_photo),
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        }
                        if (selectedInterestList.isEmpty()) {
                            commonFunctions.setToastMessage(getApplicationContext(),
                                    getString(R.string.please_add_related_interests),
                                    Toast.LENGTH_LONG, AppKeys.TOAST_USER);
                        } else {
                            pojoCreateSession.cover_photo_path = cover_photo_path;
                            for (int i = 0; i < selectedInterestList.size(); i++) {
                                pojoCreateSession.interest_ids.add(selectedInterestList.get(i).interestId);
                            }
                            pojoCreateSession.description = sp_purpose_list.getSelectedItem().toString();
                            Intent part3Intent = new Intent(SessionCreationPart2.this,
                                    SessionCreationPart3.class);
                            Type jsonType = new TypeToken<PojoCreateSession>() {
                            }.getType();
                            part3Intent.putExtra("data", new Gson().toJson(pojoCreateSession, jsonType));
                            part3Intent.putExtra("type", type);
                            startActivity(part3Intent);
                        }

                        break;

                    case R.id.iv_cover:
                        /*if (cover_photo == null) {
                            requestStoragePermission(PICK_IMAGE_REQUEST);
                        }*/
                        break;

                    case R.id.ib_remove_photo:
                        cover_photo = null;
                        iv_cover.setImageResource(R.drawable.placeholder);
                        ib_remove_photo.setVisibility(View.GONE);
                        tv_add_image.setVisibility(View.VISIBLE);
                        break;

                    case R.id.tv_add_image:
                        requestStoragePermission(PICK_IMAGE_REQUEST);

                        //  requestStoragePermission();
                  /*      CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(SessionCreationPart2.this);*/
                        break;
                }
            }
        };
    }


    public String textAsBitmapPath(PojoCreateSession text) {

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
        tv_auto_gen_image.setDrawingCacheEnabled(true);

        tv_auto_gen_image.setBackgroundResource(R.drawable.blackboard_with_chalk);
        tv_auto_gen_image.setTypeface(Typeface.create("casual", Typeface.NORMAL));
        String title = "<h2>" + pojoCreateSession.title + "</h2>";

        String topics = "";

        for (int i = 0; i < pojoCreateSession.topics.size() && i < 4; i++) {
            topics = topics + "<li>" + pojoCreateSession.topics.get(i).value + "</li>";
        }

        tv_auto_gen_image.setText(Html.fromHtml(title + "<ol> " + topics + "</ol>"));
        tv_auto_gen_image.buildDrawingCache();
        Bitmap image = tv_auto_gen_image.getDrawingCache();

        // we create an scaled bitmap so it reduces the image, not just trim it
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        // compress to the format you want, JPEG, PNG...
        // 70 is the 0-100 quality percentage
        image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        // we save the file, at least until we have made use of it
        Calendar calendar = Calendar.getInstance();

        File f = new File(getCacheDir(), calendar.getTime() + "temp_session_image.jpg");

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
        return f.getPath();
    }

    private void requestStoragePermission(int requestType) {
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
                        requestType);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {

            if (requestType == PICK_IMAGE_REQUEST) {
                CropImage.activity()
                        .setAspectRatio(3,2)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SessionCreationPart2.this);
            } else if (requestType == AUTO_GENERATE_IMAGE) {
                cover_photo_path = textAsBitmapPath(pojoCreateSession);
                /*Glide.with(getApplicationContext())
                        .load(cover_photo_path)
                        .into(iv_cover);*/
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
                            .start(SessionCreationPart2.this);
                } /*else {
                    cover_photo_path = textAsBitmapPath(pojoCreateSession);
                }*/
                return;
            }
            case AUTO_GENERATE_IMAGE:
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cover_photo_path = textAsBitmapPath(pojoCreateSession);
                    /*Glide.with(getApplicationContext())
                            .load(cover_photo_path)
                            .into(iv_cover);*/
                }

                // other 'case' lines to check for other
                // permissions this app might request.
        }
    }

    private void setClickListener() {
        tv_next.setOnClickListener(onClickListener);
        tv_add_image.setOnClickListener(onClickListener);
        ib_remove_photo.setOnClickListener(onClickListener);
        ib_remove_photo.setVisibility(View.GONE);
        iv_cover.setOnClickListener(onClickListener);
        tv_interests.setOnClickListener(onClickListener);
    }

    private void initViews() {
        tv_interests = (TextView) findViewById(R.id.tv_interests);
        tv_add_image = (TextView) findViewById(R.id.tv_add_image);
        ib_remove_photo = (ImageButton) findViewById(R.id.ib_remove_photo);
        iv_cover = (ImageView) findViewById(R.id.iv_cover);
        tv_auto_gen_image = (TextView) findViewById(R.id.tv_auto_gen_image);

        sv_wrapper = (ScrollView) findViewById(R.id.sv_wrapper);
        sp_purpose_list = (Spinner) findViewById(R.id.sp_purpose_list);
        tv_next = (TextView) findViewById(R.id.tv_next);

        ViewTreeObserver vto = tv_auto_gen_image.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tv_auto_gen_image.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                requestStoragePermission(AUTO_GENERATE_IMAGE);
            }
        });


    }

    private void initLists() {
        initAddedInterestList();
        initPurposeListSpinner();
    }

    private void initAddedInterestList() {
        selectedInterestList = new ArrayList<>();
        tv_length_getter = (TextView) findViewById(R.id.tv_length_getter);
        rv_added_interests = (RecyclerView) findViewById(R.id.rv_added_interests);
        updateSelectedInterestList();
    }

    private void updateSelectedInterestList() {
        gridLayoutManager = (new GridLayoutManager(SessionCreationPart2.this, 200));
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


                float adjustMarginPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, displayMetrics);

                float spanCount = (200 * (textWidth + adjustMarginPx)) / (screenWidth * 0.9f);

                if (spanCount > 200) {
                    spanCount = 200;
                }
                return (int) spanCount;
            }
        });

        rv_added_interests.setLayoutManager(gridLayoutManager);
        taggedInterestAdapter = new TaggedInterestAdapter(selectedInterestList.size(),
                SessionCreationPart2.this, selectedInterestList, 1);
        rv_added_interests.setAdapter(taggedInterestAdapter);
    }


    private void initPurposeListSpinner() {
        purpose_options = new ArrayList<String>();
        purpose_options.add("Exam");
        purpose_options.add("Out of Interest");
        purpose_options.add("Profession");
        purpose_options.add("Other");


        purposeSpinnerAdapter = new ArrayAdapter<String>(SessionCreationPart2.this,
                R.layout.custom_spinner_dropdown_item, purpose_options);
        purposeSpinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        sp_purpose_list.setAdapter(purposeSpinnerAdapter);
    }


    @Override
    public void interestResultItemClickCallback(int position, PojoSearchInterestWithTextResponseData data) {
        PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem
                = new PojoGetInterestListResponseDataListItem(data.interestId, data.parentId, data.text, 0);
        for (int i = 0; i < selectedInterestList.size(); i++) {
            //if item already present in list then return
            if (selectedInterestList.get(i).interestId
                    .equals(data.interestId)) {
                return;
            }
        }

        //add 10 interests at max
        if (selectedInterestList.size() < 10) {
            selectedInterestList.add(pojoGetInterestListResponseDataListItem);
            updateSelectedInterestList();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                //  cover_photo = new File(result.getUri().getPath());
                cover_photo = commonFunctions.getScaledDownImage(result.getUri().getPath());
                //RequestBody mFile = RequestBody.create(MediaType.parse("multipart/form-data"), cover_photo);
                //   Uri contentUri = getImageContentUri(CreateArticleActivity.this, cover_photo);
                //  mimeType = getContentResolver().getType(contentUri);
                iv_cover.setVisibility(View.VISIBLE);
                tv_add_image.setVisibility(View.GONE);
                // tv_add_poll_right.setVisibility(View.GONE);
                // tv_add_poll_bottom.setVisibility(View.VISIBLE);

                iv_cover.setImageURI(Uri.fromFile(cover_photo));
                cover_photo_path = cover_photo.getAbsolutePath();
                ib_remove_photo.setVisibility(View.VISIBLE);
                tv_add_image.setVisibility(View.GONE);

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
    public void taggedInterestItemClickCallback(int position, PojoGetInterestListResponseDataListItem pojoGetInterestListResponseDataListItem) {
        selectedInterestList.remove(position);
        taggedInterestAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);

    }
}
