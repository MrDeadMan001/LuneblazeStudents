package com.avadna.luneblaze.activities.chat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.ConversationAddedUserListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateChatGroupResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
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

public class CreateChatGroupActivity extends AppBaseActivity
        implements ConversationAddedUserListAdapter.AddedRecipientListAdapterCallback {

    ActionBar actionBar;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    ApiInterface apiService;
    String user_id;

    View.OnClickListener onClickListener;

    FloatingActionButton fab_next;

    LinearLayout ll_content_wrapper;
    ProgressBar pb_api_status;

    RecyclerView rv_added_userList;
    ConversationAddedUserListAdapter conversationAddedUserListAdapter;
    List<PojoUserData> addedUserList;

    Boolean groupCreate = false;

    RelativeLayout rl_group_info_wrapper;

    ImageView iv_group_photo;
    EditText et_group_name;
    String groupImagePath = "";

    final int PICK_IMAGE_REQUEST = 9120;


    private static final int GROUP_NAME_MAX_LENGTH = 50;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_chat_group);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        String userList = getIntent().getStringExtra("group_list");
        addedUserList = new Gson().fromJson(userList, new TypeToken<List<PojoUserData>>() {
        }.getType());
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initViews();
        initClickListeners();
        setClickListeners();
        setTextWatcher();

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
            actionBar.setTitle(addedUserList.size() + " " + getString(R.string.participants));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(CreateChatGroupActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(CreateChatGroupActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        iv_group_photo = (ImageView) findViewById(R.id.iv_group_photo);
        et_group_name = (EditText) findViewById(R.id.et_group_name);
        rl_group_info_wrapper = (RelativeLayout) findViewById(R.id.rl_group_info_wrapper);

        fab_next = (FloatingActionButton) findViewById(R.id.fab_next);

        ll_content_wrapper = (LinearLayout) findViewById(R.id.ll_content_wrapper);
        pb_api_status = (ProgressBar) findViewById(R.id.pb_api_status);
        pb_api_status.setVisibility(View.GONE);
        setUpAddedUserList();
    }

    public void setTextWatcher() {
        et_group_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > GROUP_NAME_MAX_LENGTH) {
                    commonFunctions.setToastMessage(CreateChatGroupActivity.this,
                            getString(R.string.group_name_no_longer_than_50), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                    et_group_name.setText(charSequence.subSequence(0, GROUP_NAME_MAX_LENGTH));
                    et_group_name.setSelection(et_group_name.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setUpAddedUserList() {
        // addedUserList = new ArrayList<>();

        rv_added_userList = (RecyclerView) findViewById(R.id.rv_added_userList);
        conversationAddedUserListAdapter = new ConversationAddedUserListAdapter(CreateChatGroupActivity.this, addedUserList);
        conversationAddedUserListAdapter.showRemoveButton(true);
        rv_added_userList.setLayoutManager(new GridLayoutManager(this, 4));
        rv_added_userList.setAdapter(conversationAddedUserListAdapter);
    }


    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ib_back:
                        CreateChatGroupActivity.this.onBackPressed();
                        break;

                    case R.id.fab_next:
                        if (et_group_name.getText().toString().isEmpty()) {
                            commonFunctions.setToastMessage(CreateChatGroupActivity.this,
                                    getString(R.string.please_enter_group_name), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        } else {
                            hitCreateChatGroupApi(user_id, et_group_name.getText().toString(), groupImagePath, addedUserList);
                        }
                        // createConversationGroup(addedUserList);
                       /* Intent chatIntent = new Intent(CreateChatGroupActivity.this, ChatActivity.class);
                        chatIntent.putExtra("userType","new");
                        Gson gson = new Gson();
                        String listString = gson.toJson(
                                addedUserList,
                                new TypeToken<ArrayList<PojoUserData>>() {}.getType());
                        chatIntent.putExtra("recipient",listString);
                        startActivity(chatIntent);*/

                        break;
                    case R.id.iv_group_photo:

                        requestStoragePermission(PICK_IMAGE_REQUEST);


                        break;

                    case R.id.tv_create_group:
                        if (groupCreate) {
                            rl_group_info_wrapper.setVisibility(View.GONE);
                        } else {
                            rl_group_info_wrapper.setVisibility(View.VISIBLE);
                        }
                        groupCreate = !groupCreate;
                        break;
                }
            }
        };
    }


    private void setClickListeners() {
        fab_next.setOnClickListener(onClickListener);
        iv_group_photo.setOnClickListener(onClickListener);
        Glide.with(CreateChatGroupActivity.this.getApplicationContext())
                .load(R.drawable.placeholder)
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_group_photo);
    }


    private void hitCreateChatGroupApi(final String user, String group, String imagePath,
                                       List<PojoUserData> recipients) {

        pb_api_status.setVisibility(View.VISIBLE);
        ll_content_wrapper.setVisibility(View.GONE);
        fab_next.hide();

        MultipartBody.Part fileToUpload;
        if (imagePath.isEmpty()) {
            fileToUpload = null;
        } else {
            File file = new File(imagePath);
            // file = getScaledDownImage(file);
            RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(file)), file);
            fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), mFile);
        }

        final RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), user);
        RequestBody group_name = RequestBody.create(MediaType.parse("text/plain"), group);


        HashMap<String, RequestBody> recipientMap = new HashMap<>();
        for (int i = 0; i < recipients.size(); i++) {
            recipientMap.put("recipients[" + i + "]", RequestBody.create(MediaType.parse("text/plain"),
                    recipients.get(i).userId));
        }

        //addChatMemberListAdapter the creator to group as well
        //  recipientMap.put("recipients[" + recipients.size() + "]", RequestBody.create(MediaType.parse("text/plain"), user));

        Call<PojoCreateChatGroupResponse> call = apiService.createChatGroup(user_id, group_name,
                fileToUpload, recipientMap);
        call.enqueue(new Callback<PojoCreateChatGroupResponse>() {
            @Override
            public void onResponse(Call<PojoCreateChatGroupResponse> call, Response<PojoCreateChatGroupResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;

                   /* List<PojoConversationListItem> conversationList=new ArrayList<>();
                    conversationList.addAll(preferenceUtils.getAllMessageList());

                    List<PojoGetMessageListResponseDataRecipient> recipientList=new ArrayList<>();
                    for(int i=0;i<addedUserList.size();i++){
                        PojoUserData currentUser=addedUserList.get(i);
                        recipientList.addChatMemberListAdapter(new PojoGetMessageListResponseDataRecipient(currentUser.userId,
                                currentUser.userPicture,currentUser.userFullname));
                    }

                    List<PojoGroupMember> memberList=new ArrayList<>();
                    for(int i=0;i<addedUserList.size();i++){
                        PojoUserData currentUser=addedUserList.get(i);
                        memberList.addChatMemberListAdapter(new PojoGroupMember(currentUser.userId,
                                currentUser.userFullname,currentUser.userPicture,"member"));
                    }

                    PojoGroupData pojoGroupData=new PojoGroupData(response.body().data.groupId.toString(),
                            response.body().data.conversationId.toString(),user,
                            et_group_name.getText().toString().trim(),groupImagePath,addedUserList.size(),
                            memberList);


                    PojoConversationListItem tempConv=
                            new PojoConversationListItem(response.body().data.conversationId.toString(),"",
                                    groupImagePath, Calendar.getInstance().getTime().toString(),
                                    recipientList,true, groupImagePath,groupImagePath,
                                    et_group_name.getText().toString(), pojoGroupData);

                    conversationList.addChatMemberListAdapter(0,tempConv);
                    preferenceUtils.saveAllMessageList(conversationList);

                    List<PojoChatMessage> tempMessageList=new ArrayList<>();
                    Date currentTime = Calendar.getInstance().getTime();

                    PojoChatMessage newMsg =
                            new PojoChatMessage(user, getString(R.string.you_created_group)+" "
                                    +et_group_name.getText().toString().trim(),
                                    "", currentTime.toString(), "9999");
                    newMsg.isImageUploading = true;

                    tempMessageList.addChatMemberListAdapter(newMsg);

                    preferenceUtils.saveConversation(response.body().data.conversationId.toString(), tempMessageList);*/

            /*        getApplicationContext().startService(new Intent(CreateChatGroupActivity.this,
                            MessagingService.class));*/


                    //commonFunctions.setToastMessage(CreateChatGroupActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);


                    Intent registrationComplete = new Intent(Config.GROUP_CREATED);
                    LocalBroadcastManager.getInstance(CreateChatGroupActivity.this).sendBroadcast(registrationComplete);

                    Intent chatIntent = new Intent(CreateChatGroupActivity.this, ChatActivity.class);
                    chatIntent.putExtra("cid", response.body().data.conversationId.toString());
                    startActivity(chatIntent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PojoCreateChatGroupResponse> call, Throwable t) {

                // Log error here since request failed
                commonFunctions.setToastMessage(CreateChatGroupActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

                pb_api_status.setVisibility(View.GONE);
                ll_content_wrapper.setVisibility(View.VISIBLE);
                fab_next.show();
            }
        });

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
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(CreateChatGroupActivity.this);
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
                            .start(CreateChatGroupActivity.this);
                }
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                File imgFile = commonFunctions.getScaledDownImage(result.getUri().getPath());
                Glide.with(CreateChatGroupActivity.this.getApplicationContext())
                        .load(imgFile)
                        .apply(new RequestOptions().override(256, 256))
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(iv_group_photo);

                groupImagePath = imgFile.getPath();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }


    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    public void onAddedRecipientListMethodCallback(int position) {
        addedUserList.remove(position);
        actionBar.setTitle(addedUserList.size() + " " + getString(R.string.participants));
        conversationAddedUserListAdapter.notifyItemRemoved(position);
        conversationAddedUserListAdapter.notifyItemRangeChanged(position, addedUserList.size());
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

}
