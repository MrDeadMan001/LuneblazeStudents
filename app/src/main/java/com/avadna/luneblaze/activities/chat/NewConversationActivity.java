package com.avadna.luneblaze.activities.chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;

import com.avadna.luneblaze.activities.LoginActivity;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.ConversationAddedUserListAdapter;
import com.avadna.luneblaze.adapters.chat.RecipientSearchResultListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetFriendsListResponse;
import com.avadna.luneblaze.pojo.PojoGetUserOnlineStatusResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoConversationListItem;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateNewConversationResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewConversationActivity extends AppBaseActivity
        implements RecipientSearchResultListAdapter.RecipientListAdapterCallback,
        ConversationAddedUserListAdapter.AddedRecipientListAdapterCallback {

    ActionBar actionBar;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;
    ApiInterface apiService;
    String user_id;


    View.OnClickListener onClickListener;
    Toolbar tl_top_bar;
    ImageButton ib_back, ib_search_gray;
    FloatingActionButton fab_next;

    EditText et_search_bar;
    int offset = 0;
    boolean isGetFriendApiCalled = false;

    RecyclerView rv_my_friends_list;
    RecipientSearchResultListAdapter recipientSearchResultListAdapter;
    LinearLayoutManager friendListLayoutManager;
    List<PojoUserData> friendList;

    ProgressBar pb_loading_users;

    RecyclerView rv_added_userList;
    ConversationAddedUserListAdapter conversationAddedUserListAdapter;
    List<PojoUserData> addedUserList;

    Boolean groupCreate = false;

    TextView tv_create_group;
    RelativeLayout rl_group_info_wrapper;

    LinearLayout ll_content_wrapper;
    ProgressBar pb_creating_conversation;
    BroadcastReceiver broadcastReceiver;

    PojoLoginResponseData pojoLoginResponseData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        pojoLoginResponseData=preferenceUtils.getUserLoginData();
        if (pojoLoginResponseData == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        user_id = preferenceUtils.get_user_id();
        setUpActionBar();
        initViews();
        initClickListeners();
        setClickListeners();
        hitGetFriendListApi(user_id, "", offset);
        initBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter(Config.GROUP_CREATED));


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initBroadcastReceiver() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.GROUP_CREATED)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    finish();
                }
            }
        };
    }

    private void setUpActionBar() {
        tl_top_bar = findViewById(R.id.tl_top_bar);
        setSupportActionBar(tl_top_bar);
        actionBar = getSupportActionBar();
        if (tl_top_bar != null) {
            // Show the Up button in the action bar.
            tl_top_bar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(NewConversationActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(NewConversationActivity.this, R.color.status_bar_color));
            }
        }
    }

    private void initViews() {
        tv_create_group = (TextView) findViewById(R.id.tv_create_group);
        //tv_create_group.setVisibility(View.GONE);
        rl_group_info_wrapper = (RelativeLayout) findViewById(R.id.rl_group_info_wrapper);
        rl_group_info_wrapper.setVisibility(View.GONE);
        pb_loading_users = (ProgressBar) findViewById(R.id.pb_loading_users);
        pb_creating_conversation = (ProgressBar) findViewById(R.id.pb_creating_conversation);
        pb_creating_conversation.setVisibility(View.VISIBLE);
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        ib_search_gray = (ImageButton) findViewById(R.id.ib_search_gray);
        fab_next = (FloatingActionButton) findViewById(R.id.fab_next);
        fab_next.hide();
        ll_content_wrapper = (LinearLayout) findViewById(R.id.ll_content_wrapper);
        ll_content_wrapper.setVisibility(View.GONE);
        et_search_bar = (EditText) findViewById(R.id.et_search_bar);

        et_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                offset = 0;
                hitGetFriendListApi(user_id, s.toString().trim(), offset);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (!et_search_bar.getText().toString().trim().isEmpty()) {
                        offset = 0;
                        hitGetFriendListApi(user_id, et_search_bar.getText().toString().trim(), offset);
                    }
                    return true;
                }
                return false;
            }
        });

        setUpMyFriendsList();
        setUpAddedUserList();
    }

    private void setUpAddedUserList() {
        addedUserList = new ArrayList<>();
        rv_added_userList = (RecyclerView) findViewById(R.id.rv_added_userList);
        conversationAddedUserListAdapter = new ConversationAddedUserListAdapter(NewConversationActivity.this, addedUserList);

        rv_added_userList.setLayoutManager(new LinearLayoutManager(NewConversationActivity.this,
                LinearLayoutManager.HORIZONTAL, false));
        rv_added_userList.setAdapter(conversationAddedUserListAdapter);

    }


    private void setUpMyFriendsList() {
        friendList = new ArrayList<>();
        rv_my_friends_list = (RecyclerView) findViewById(R.id.rv_my_friends_list);
        recipientSearchResultListAdapter = new RecipientSearchResultListAdapter(NewConversationActivity.this, friendList);
        friendListLayoutManager = new LinearLayoutManager(NewConversationActivity.this,
                RecyclerView.VERTICAL, false);
        rv_my_friends_list.setLayoutManager(friendListLayoutManager);
        rv_my_friends_list.setAdapter(recipientSearchResultListAdapter);

        rv_my_friends_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //    Log.d("Scroll info", "dx = " + dy);
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5) {
                    if (!isGetFriendApiCalled && friendListLayoutManager.findLastVisibleItemPosition() > friendList.size() - 3) {
                        hitGetFriendListApi(preferenceUtils.get_user_id(),
                                et_search_bar.getText().toString().trim(), ++offset);
                    }
                }
            }
        });
    }

    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.ib_back:
                        NewConversationActivity.this.onBackPressed();
                        break;

                    case R.id.ib_search_gray:
                        if (et_search_bar.requestFocus()) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                            imm.showSoftInput(et_search_bar, InputMethodManager.SHOW_FORCED);
                        }
                        break;

                    case R.id.fab_next:
                        String listString = new Gson().toJson(
                                addedUserList,
                                new TypeToken<List<PojoUserData>>() {
                                }.getType());
                        Intent createGroupIntent = new Intent(NewConversationActivity.this,
                                CreateChatGroupActivity.class);
                        createGroupIntent.putExtra("group_list", listString);
                        startActivity(createGroupIntent);
                        break;

                    case R.id.iv_group_photo:
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(NewConversationActivity.this);
                        break;

                    case R.id.tv_create_group:
                        if (groupCreate) {
                            rl_group_info_wrapper.setVisibility(View.GONE);
                        } else {
                            rl_group_info_wrapper.setVisibility(View.VISIBLE);
                        }
                        groupCreate = !groupCreate;
                        recipientSearchResultListAdapter.checkBoxVisible(groupCreate);
                        tv_create_group.setVisibility(View.GONE);
                        break;
                }
            }
        };
    }


    private void setClickListeners() {
        ib_back.setOnClickListener(onClickListener);
        ib_search_gray.setOnClickListener(onClickListener);
        tv_create_group.setOnClickListener(onClickListener);
        fab_next.setOnClickListener(onClickListener);
    }


    private void hitGetFriendListApi(String user_id, String query, final int offset) {
        Call<PojoGetFriendsListResponse> call = apiService.getFriendListApi(user_id, user_id, query,
                "0", "1", String.valueOf(offset));
        if (!isGetFriendApiCalled) {
            isGetFriendApiCalled = true;
            pb_loading_users.setVisibility(View.VISIBLE);
            call.enqueue(new Callback<PojoGetFriendsListResponse>() {
                @Override
                public void onResponse(Call<PojoGetFriendsListResponse> call, Response<PojoGetFriendsListResponse> response) {
                    String message = response.body().message;

                    if (response.body().data != null) {
                        if (offset == 0) {
                            friendList.clear();
                        }

                        for (int i = 0; i < response.body().data.friends.size(); i++) {
                            response.body().data.friends.get(i).selected = false;
                        }

                        friendList.addAll(response.body().data.friends);

                        List<String> userList = new ArrayList<>();
                        for (int j = 0; j < friendList.size(); j++) {
                            userList.add(friendList.get(j).userId);
                        }
                        hitGetUserOnlineStatusApi(userList);

                     /*   for (int i = 0; i < friendList.size(); i++) {
                            //set all new as false as this field is not received from api and is null
                            for (int j = 0; j < addedUserList.size(); j++) {
                                //set the previously added users as selected
                                if (addedUserList.get(j).userId.equals(friendList.get(i).userId)) {
                                    friendList.get(i).selected = addedUserList.get(j).selected;
                                }
                            }
                        }*/

                        recipientSearchResultListAdapter.notifyDataSetChanged();
                    } else {
                        commonFunctions.setToastMessage(NewConversationActivity.this, "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    }
                    pb_loading_users.setVisibility(View.GONE);
                    pb_creating_conversation.setVisibility(View.GONE);
                    ll_content_wrapper.setVisibility(View.VISIBLE);

                    isGetFriendApiCalled = false;

                }

                @Override
                public void onFailure(Call<PojoGetFriendsListResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(NewConversationActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isGetFriendApiCalled = false;
                    if (friendList.isEmpty()) {
                        openNoConnectionDialog("");
                    }
                }
            });
        }
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setTitle(title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetFriendListApi(user_id, "", offset);
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
        }
        catch (Exception e){

        }

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
                        for (int i = 0; i < friendList.size(); i++) {
                            for (int j = 0; j < response.body().data.size(); j++) {
                                if (friendList.get(i).userId.equals(response.body().data.get(j).userId)) {
                                    friendList.get(i).isOnline = response.body().data.get(j).isOnline;
                                }
                            }
                        }
                        recipientSearchResultListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(Call<PojoGetUserOnlineStatusResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(NewConversationActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }


    private void hitCreateIndividualConversationApi(String user_id, String recipient) {
        Call<PojoCreateNewConversationResponse> call = apiService.createConversation(user_id, recipient);
        pb_creating_conversation.setVisibility(View.VISIBLE);
        ll_content_wrapper.setVisibility(View.GONE);
        call.enqueue(new Callback<PojoCreateNewConversationResponse>() {
            @Override
            public void onResponse(Call<PojoCreateNewConversationResponse> call,
                                   Response<PojoCreateNewConversationResponse> response) {
                String message = response.body().message;
                if (response.body().data != null) {
                    Intent chatIntent = new Intent(NewConversationActivity.this, ChatActivity.class);
                    chatIntent.putExtra("cid", response.body().data);
                    chatIntent.putExtra(AppKeys.TARGET_USER_ID, recipient);
                    startActivity(chatIntent);
                    finish();
                } else {
                    commonFunctions.setToastMessage(NewConversationActivity.this, "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    pb_creating_conversation.setVisibility(View.GONE);
                    ll_content_wrapper.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<PojoCreateNewConversationResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(NewConversationActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                pb_creating_conversation.setVisibility(View.GONE);
                ll_content_wrapper.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onRecipientListMethodCallback(int position, PojoUserData item) {
        if (groupCreate) {
            if (!addedUserList.contains(friendList.get(position))) {
                friendList.get(position).selected = true;
                addedUserList.add(friendList.get(position));
            } else {
                friendList.get(position).selected = false;
                addedUserList.remove(friendList.get(position));
            }
            recipientSearchResultListAdapter.notifyDataSetChanged();
            conversationAddedUserListAdapter.notifyDataSetChanged();
            if (addedUserList.isEmpty()) {
                fab_next.hide();
            } else {
                fab_next.show();
            }
        } else {
            boolean found = false;
            List<PojoConversationListItem> conversationList = preferenceUtils.getAllMessageList();
            for (int i = 0; i < conversationList.size(); i++) {
                if (conversationList.get(i).groupData.groupId == null) {
                    if (conversationList.get(i).ids.equals(item.userId)) {
                        found = true;
                        Intent chatIntent = new Intent(NewConversationActivity.this, ChatActivity.class);
                        chatIntent.putExtra("cid", conversationList.get(i).conversationId);
                        chatIntent.putExtra(AppKeys.TARGET_USER_ID, item.userId);

                        startActivity(chatIntent);
                    }
                }
            }
            if (!found) {
                hitCreateIndividualConversationApi(user_id, item.userId);
            }
        }
    }

    @Override
    public void onAddedRecipientListMethodCallback(int position) {
        int index = friendList.indexOf(addedUserList.get(position));
        friendList.get(index).selected = false;

        addedUserList.remove(position);
        recipientSearchResultListAdapter.notifyDataSetChanged();
        conversationAddedUserListAdapter.notifyDataSetChanged();
        if (addedUserList.isEmpty()) {
            fab_next.hide();
        } else {
            fab_next.show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);
    }


}
