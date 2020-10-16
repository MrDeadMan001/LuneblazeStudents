package com.avadna.luneblaze.activities.chat;

import android.annotation.SuppressLint;
import android.app.Dialog;

import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.chat.AddChatMemberListAdapter;
import com.avadna.luneblaze.adapters.chat.GroupMemberListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetFriendsListResponse;
import com.avadna.luneblaze.pojo.PojoGetUserOnlineStatusResponse;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoChatGroupMember;
import com.avadna.luneblaze.pojo.pojoChat.PojoGetGroupDetailsResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
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

public class GroupInfoActivity extends AppBaseActivity implements
        GroupMemberListAdapter.GroupMemberListAdapterCallback,
        AddChatMemberListAdapter.AddChatMemberAdapterCallback {

    private static final int GROUP_NAME_MAX_LENGTH = 50;
    private static final int ADD_MEMBER_REQUEST_CODE = 422;

    ActionBar actionBar;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    ApiInterface apiService;
    String user_id;
    String group_id;

    LinearLayout ll_content_wrapper;
    ProgressBar pb_loading;

    PojoGetGroupDetailsResponse pojoGetGroupDetailsResponse;
    RecyclerView rv_member_list;
    GroupMemberListAdapter groupMemberListAdapter;
    List<PojoChatGroupMember> memberList;

    Dialog userListDialog;
    List<PojoUserData> userList;
    AddChatMemberListAdapter addChatMemberListAdapter;
    ProgressBar pb_loading_users;

    View.OnClickListener onClickListener;


    ImageView iv_group_photo, iv_edit_name, iv_done_editing;
    EditText et_group_name;
    TextView tv_member_count;
    String groupImagePath = "";
    boolean isGetFriendApiCalled = false;
    LinearLayoutManager friendListLayoutManager;

    int friendListOffset = 0;


    private BroadcastReceiver mRegistrationBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        preferenceUtils = new PreferenceUtils(GroupInfoActivity.this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        group_id = getIntent().getStringExtra(AppKeys.GROUP_ID);
        initViews();
        initOnClickListener();
        setClickListeners();
        setUpActionBar();
        hitGetGroupInfoApi(group_id);
        initBroadCastReceiver();

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.GROUP_UPDATE_BROADCAST));
    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.GROUP_UPDATE_BROADCAST)) {
                    // new push notification is received
                 /*   String message = intent.getStringExtra("message");
                    commonFunctions.setToastMessage(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);*/
                    if (intent.getStringExtra("group_id").equals(group_id)) {
                        hitGetGroupInfoApi(group_id);
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
            actionBar.setTitle(getString(R.string.group_info));
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(GroupInfoActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(GroupInfoActivity.this, R.color.status_bar_color));
            }
        }

    }

    private void initViews() {
        ll_content_wrapper = (LinearLayout) findViewById(R.id.ll_content_wrapper);
        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        pb_loading.setVisibility(View.GONE);

        iv_group_photo = (ImageView) findViewById(R.id.iv_group_photo);
        Glide.with(getApplicationContext())
                .load(R.drawable.placeholder)
                .apply(bitmapTransform(new CropCircleTransformation()))
                .into(iv_group_photo);
        iv_edit_name = (ImageView) findViewById(R.id.iv_edit_name);
        iv_done_editing = (ImageView) findViewById(R.id.iv_done_editing);
        iv_done_editing.setVisibility(View.GONE);
        et_group_name = (EditText) findViewById(R.id.et_group_name);
        tv_member_count = (TextView) findViewById(R.id.tv_member_count);
        setUpMemberList();
    }

    public void setTextWatcher() {
        et_group_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > GROUP_NAME_MAX_LENGTH) {
                    commonFunctions.setToastMessage(GroupInfoActivity.this,
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

    private void setUpMemberList() {
        memberList = new ArrayList<>();
        rv_member_list = (RecyclerView) findViewById(R.id.rv_member_list);
        groupMemberListAdapter = new GroupMemberListAdapter(GroupInfoActivity.this, memberList,
                group_id);
        rv_member_list.setLayoutManager(new LinearLayoutManager(GroupInfoActivity.this,
                RecyclerView.VERTICAL, false));
        rv_member_list.setAdapter(groupMemberListAdapter);
    }

    private void initOnClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.iv_group_photo:
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(GroupInfoActivity.this);
                        break;

                    case R.id.iv_edit_name:
                        et_group_name.setEnabled(true);
                        if (et_group_name.requestFocus()) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et_group_name, InputMethodManager.SHOW_FORCED);
                        }
                        iv_done_editing.setVisibility(View.VISIBLE);
                        iv_edit_name.setVisibility(View.GONE);
                        break;

                    case R.id.iv_done_editing:
                        et_group_name.setEnabled(false);
                        iv_done_editing.setVisibility(View.GONE);
                        iv_edit_name.setVisibility(View.VISIBLE);
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_group_name.getWindowToken(), 0);
                        hitEditGroupApi(user_id, group_id, et_group_name.getText().toString().trim(), "");
                        break;

                }
            }
        };

    }

    private void setClickListeners() {
        iv_group_photo.setOnClickListener(onClickListener);
        iv_edit_name.setOnClickListener(onClickListener);
        iv_done_editing.setOnClickListener(onClickListener);
    }

    private void hitGetGroupInfoApi(String group_id) {
        pb_loading.setVisibility(View.VISIBLE);
        ll_content_wrapper.setVisibility(View.GONE);
        Call<PojoGetGroupDetailsResponse> call = apiService.getGroupInfo(group_id);
        call.enqueue(new Callback<PojoGetGroupDetailsResponse>() {
            @Override
            public void onResponse(Call<PojoGetGroupDetailsResponse> call, Response<PojoGetGroupDetailsResponse> response) {
                if (response != null && response.body() != null) {
                    pb_loading.setVisibility(View.GONE);
                    ll_content_wrapper.setVisibility(View.VISIBLE);
                    String message = response.body().message;
                    pojoGetGroupDetailsResponse = response.body();
                    //  pojoGetGroupDetailsResponse.data.members=deleteRemovedMembers( pojoGetGroupDetailsResponse.data.members);
                    memberList.clear();
                    memberList.addAll(deleteRemovedMembers(pojoGetGroupDetailsResponse.data.members));
                    groupMemberListAdapter.notifyDataSetChanged();
                    groupMemberListAdapter.updateOwnerId(response.body().data.groupOwner);
                    List<String> userList = new ArrayList<>();
                    for (int j = 0; j < memberList.size(); j++) {
                        userList.add(memberList.get(j).userId);
                    }
                    hitGetUserOnlineStatusApi(userList);
                    setDataOnViews();

                }

            }

            @Override
            public void onFailure(Call<PojoGetGroupDetailsResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(GroupInfoActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.group_info));
            }
        });
    }


    private void openNoConnectionDialog(String title) {

        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetGroupInfoApi(group_id);
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

    private List<PojoChatGroupMember> deleteRemovedMembers(List<PojoChatGroupMember> members) {
        List<PojoChatGroupMember> moddedMembers = new ArrayList<>();
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).groupStatus.equals("0")) {
                moddedMembers.add(members.get(i));
            }
        }
        return moddedMembers;
    }

    private void setDataOnViews() {
        invalidateOptionsMenu();

        tv_member_count.setText(memberList.size() + " " + getString(R.string.participants));

        if (pojoGetGroupDetailsResponse.data.groupPhoto.isEmpty()) {
            Glide.with(getApplicationContext())
                    .load(R.drawable.placeholder)
                    .apply(new RequestOptions().override(256, 256))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(iv_group_photo);
        } else {
            Glide.with(getApplicationContext())
                    .load(pojoGetGroupDetailsResponse.data.groupPhoto)
                    .apply(new RequestOptions().override(256, 256))
                    .apply(bitmapTransform(new CropCircleTransformation()))
                    .into(iv_group_photo);
        }


        et_group_name.setText(pojoGetGroupDetailsResponse.data.groupName);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_group_activity_menu, menu);
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

            case R.id.add_member:
                Intent addMemberIntent = new Intent(GroupInfoActivity.this, AddGroupMembersActivity.class);
                addMemberIntent.putExtra(AppKeys.GROUP_ID, group_id);
                startActivityForResult(addMemberIntent, ADD_MEMBER_REQUEST_CODE);
                //openUserListDialog("add_group_members");
                break;

            case R.id.leave_group:
                hitLeaveGroupApi(user_id, group_id);
                break;

        }
        return true;
    }

    private void openUserListDialog(String type) {
        userListDialog = new MyCustomThemeDialog(GroupInfoActivity.this, R.style.NoTitleDialogTheme);
        userListDialog.setContentView(R.layout.contact_list_dialog);
        userListDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        userListDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        pb_loading_users = (ProgressBar) userListDialog.findViewById(R.id.pb_loading_users);

        TextView tv_title = (TextView) userListDialog.findViewById(R.id.tv_title);
        pb_loading_users.setVisibility(View.GONE);
        ImageView iv_close = (ImageView) userListDialog.findViewById(R.id.iv_close);
        iv_close.setVisibility(View.VISIBLE);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userListDialog.dismiss();
            }
        });

        switch (type) {
            case "add_group_members":
                tv_title.setText(R.string.add_members);
                friendListOffset = 0;
                hitGetFriendListApi(user_id, "", friendListOffset);
                break;

            case "assign_admin":
                break;

            case "remove_member":
                break;

        }

        ImageView iv_back = (ImageView) userListDialog.findViewById(R.id.iv_back);
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userListDialog.dismiss();
            }
        });

        RecyclerView rv_list;
        rv_list = (RecyclerView) userListDialog.findViewById(R.id.rv_list);
        rv_list.setPadding(16, 32, 16, 32);
        friendListLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_list.setLayoutManager(friendListLayoutManager);
        userList = new ArrayList<>();
        addChatMemberListAdapter = new AddChatMemberListAdapter(GroupInfoActivity.this,
                userList, type);
        rv_list.setAdapter(addChatMemberListAdapter);

        rv_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //    Log.d("Scroll info", "dx = " + dy);
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    if (!isGetFriendApiCalled && friendListLayoutManager.findLastVisibleItemPosition() > userList.size() - 3) {
                        hitGetFriendListApi(user_id, "", ++friendListOffset);
                    }
                }
            }
        });
        userListDialog.show();
    }

    private void hitGetUserOnlineStatusApi(List<String> user_id_list) {

        HashMap<String, String> userIdMap = new HashMap<>();
        for (int i = 0; i < user_id_list.size(); i++) {
            userIdMap.put("user_ids[" + i + "]", user_id_list.get(i));
        }

        Call<PojoGetUserOnlineStatusResponse> call = apiService.getUserOnlineStatus(userIdMap);
        call.enqueue(new Callback<PojoGetUserOnlineStatusResponse>() {
            @Override
            public void onResponse(Call<PojoGetUserOnlineStatusResponse> call,
                                   Response<PojoGetUserOnlineStatusResponse> response) {
                if (response.body() != null) {
                    String message = response.body().message;
                    if (response.body().data != null) {
                        for (int i = 0; i < memberList.size(); i++) {
                            for (int j = 0; j < response.body().data.size(); j++) {
                                if (memberList.get(i).userId.equals(response.body().data.get(j).userId)) {
                                    memberList.get(i).isOnline = response.body().data.get(j).isOnline;
                                }
                            }
                        }
                        groupMemberListAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onFailure(Call<PojoGetUserOnlineStatusResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(GroupInfoActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitGetFriendListApi(String user_id, String query, int offset) {
        Call<PojoGetFriendsListResponse> call = apiService.getFriendsNotInGroup(user_id, user_id,
                group_id, query, "0", "0", String.valueOf(offset));
        if (!isGetFriendApiCalled) {
            if (offset == 0) {
                pb_loading_users.setVisibility(View.VISIBLE);
            }
            isGetFriendApiCalled = true;
            call.enqueue(new Callback<PojoGetFriendsListResponse>() {
                @Override
                public void onResponse(Call<PojoGetFriendsListResponse> call, Response<PojoGetFriendsListResponse> response) {
                    if (response.body() != null) {
                        String message = response.body().message;
                        if (response.body().data != null) {
                            //dont addChatMemberListAdapter already present members
                            if (offset == 0) {
                                userList.clear();
                            }

                            for (int j = 0; j < response.body().data.friends.size(); j++) {
                                boolean present = false;
                                for (int i = 0; i < memberList.size(); i++) {
                                    if (memberList.get(i).userId.equals(response.body().data.friends.get(j).userId)) {
                                        present = true;
                                        break;
                                    }
                                }
                                if (!present) {
                                    userList.add(response.body().data.friends.get(j));
                                }

                            }
                            addChatMemberListAdapter.notifyDataSetChanged();
                        } else {
                            commonFunctions.setToastMessage(GroupInfoActivity.this,
                                    "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                        pb_loading_users.setVisibility(View.GONE);
                    }
                    isGetFriendApiCalled = false;
                }

                @Override
                public void onFailure(Call<PojoGetFriendsListResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(GroupInfoActivity.this, t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    pb_loading_users.setVisibility(View.GONE);
                    isGetFriendApiCalled = false;
                }
            });
        }
    }


    private void hitLeaveGroupApi(String user_id, String cid) {
        Call<PojoNoDataResponse> call = apiService.leaveGroup(user_id, cid);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {
                    message = response.body().message;
                    if (response.body() != null) {

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

    private void hitEditGroupApi(String userID, String groupID, String groupName, String imagePath) {
        MultipartBody.Part fileToUpload;
        if (imagePath.isEmpty()) {
            fileToUpload = null;
        } else {
            File file = new File(imagePath);
            RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(file)), file);
            fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), mFile);
        }

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), userID);
        RequestBody group_id = RequestBody.create(MediaType.parse("text/plain"), groupID);

        RequestBody group_name;
        if (groupName.isEmpty()) {
            group_name = null;
        } else {
            group_name = RequestBody.create(MediaType.parse("text/plain"), groupName);
        }
        //addChatMemberListAdapter the creator to group as well
        //  recipientMap.put("recipients[" + recipients.size() + "]", RequestBody.create(MediaType.parse("text/plain"), user));

        Call<PojoNoDataResponse> call = apiService.editChatGroup(user_id, group_id,
                group_name, fileToUpload);
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
                commonFunctions.setToastMessage(GroupInfoActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                File imgFile = commonFunctions.getScaledDownImage(result.getUri().getPath());

                Glide.with(GroupInfoActivity.this.getApplicationContext())
                        .load(imgFile)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .into(iv_group_photo);
                groupImagePath = imgFile.getPath();
                hitEditGroupApi(user_id, group_id, "", groupImagePath);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
        if (requestCode == ADD_MEMBER_REQUEST_CODE) {
            hitGetGroupInfoApi(group_id);
        }
    }

    @Override
    public void onGroupMemberListMethodCallback(int position) {
        hitGetGroupInfoApi(group_id);
    }

    @Override
    public void onAddChatMemberMethodCallback(int position, PojoUserData item, String type) {
        switch (type) {
            case "add_group_members":
                hitAddGroupMemberApi(user_id, group_id, item.userId, position);
                break;

            case "assign_admin":
                break;

            case "remove_member":
                break;
        }
    }


    private void hitAddGroupMemberApi(String user_id, final String group_id, String memberId, final int position) {
        commonFunctions.openProgressDialog();
        Call<PojoNoDataResponse> call = apiService.addMemberToGroup(user_id, group_id, memberId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
                    userList.remove(position);
                    addChatMemberListAdapter.notifyDataSetChanged();
                    hitGetGroupInfoApi(group_id);
                }
                commonFunctions.closeProgressDialog();
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(GroupInfoActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                commonFunctions.closeProgressDialog();

            }
        });
    }

    @Override
    protected void onPause() {
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);

    }


}
