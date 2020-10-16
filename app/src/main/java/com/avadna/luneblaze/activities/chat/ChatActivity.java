package com.avadna.luneblaze.activities.chat;

import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;

import com.avadna.luneblaze.adapters.chat.SeenUserListAdapter;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.MyCustomThemeDialog;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;


import com.avadna.luneblaze.pojo.pojoChat.PojoCreateNewConversationResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoGroupMember;
import com.avadna.luneblaze.pojo.pojoChat.PojoSeendetail;
import com.avadna.luneblaze.pojo.pojoChat.PojoSeenuser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.appcompat.app.ActionBar;

import android.os.Bundle;

import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.activities.settingActivities.BlockingListActivity;
import com.avadna.luneblaze.adapters.chat.GroupChatMessageAdapter;
import com.avadna.luneblaze.adapters.chat.AddChatMemberListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ImageDimensions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetUserOnlineStatusResponse;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.pojo.PojoUserConnectResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoChatMessage;
import com.avadna.luneblaze.pojo.pojoChat.PojoConversationListItem;
import com.avadna.luneblaze.pojo.pojoChat.PojoGetChatMessagesResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoGetAllMessageListResponseData;
import com.avadna.luneblaze.pojo.pojoChat.PojoGetChatMessagesResponseData;
import com.avadna.luneblaze.pojo.pojoChat.PojoGroupData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.services.AlternateMessagingService;
import com.avadna.luneblaze.utils.GlobalVariables;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.messaging.FirebaseMessaging;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

public class

ChatActivity extends AppBaseActivity implements
        AddChatMemberListAdapter.AddChatMemberAdapterCallback,
        GroupChatMessageAdapter.GroupChatAdapterCallback {
    private static final int FORWARD_MESSAGE_CODE = 101;
    private static final int CHAT_MESSAGE_MAX_LENGTH = 5000;
    private ActionBar actionBar;
    private Toolbar tl_top_bar;

    private ImageView iv_user_dp;
    private TextView tv_user_name;
    private TextView tv_online_status;

    //private SwipeRefreshLayout srl_refresh;
    private TextView tv_list_status;
    private ProgressBar pb_api_status;
    private RecyclerView rv_message_list;
    private GroupChatMessageAdapter chatMessageAdapter;
    private LinearLayoutManager chatListLayoutManager;
    public List<PojoChatMessage> messageList;
    private List<Integer> searchResultIndexList;

    private int offset = 0;
    private boolean enableApiCall = true;

    private CommonFunctions commonFunctions;
    private PreferenceUtils preferenceUtils;
    private String user_id = "";
    private ApiInterface apiService;

    private RelativeLayout rl_content_wrapper;
    private ProgressBar pb_loading_content;
    private RelativeLayout rl_user_data_wrapper;

    private RelativeLayout rl_search_wrapper;
    private ImageButton ib_previous_result, ib_next_result, ib_back_gray;
    private EditText et_search_bar;

    //this bar contains two layouts one for chat box and one for blocking status
    private RelativeLayout rl_bottom_bar;

    private RelativeLayout rl_chat_bar;
    private LinearLayout ll_block_wrapper;
    private LinearLayout ll_unblock_wrapper;
    private TextView tv_block_message;
    private TextView tv_unblock;

    private EditText et_new_message;
    private FloatingActionButton fab_send;
    private ImageButton ib_back;
    private ImageView iv_attach;
    private Dialog imageUploadDialog;

    private View.OnClickListener onClickListener;
    private PojoGetAllMessageListResponseData chatInfo;
    //   String RECIPIENT = "";
    private List<PojoGroupMember> recipientList;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private String android_id;

    private static final String TAG = ChatActivity.class.getSimpleName();
    private String latest_sent_msg_id = "";
    private String latest_message_user_id = "";
    private String cid = "";
    private HashMap<String, Integer> unreadConversations;

    private boolean updateSeenPending = false;
    private boolean isActivityVisible = false;

    //private List<PojoGetChatMessagesResponseData> unsentMessages;
    private boolean unsentInProgress = false;

    private int prefImgHeight = 0;
    private int prefImgWidth = 0;

    private boolean mBounded;
    private AlternateMessagingService messagingService;
    private boolean isGroup = false;
    private String groupId = "";

    private PojoGroupData groupData = null;


    private PojoGetChatMessagesResponse pojoGetChatMessagesResponse;


    private boolean groupNeedsUpdated = false;
    private final String BLOCK_STATUS_I_BLOCK = "has_blocked";
    private final String BLOCK_STATUS_HE_BLOCK = "blocked";
    private final String BLOCK_STATUS_NO_BLOCK = "";
    private String blockStatus = BLOCK_STATUS_NO_BLOCK;
    private boolean blockedFromChat = false;


    private Handler onlineApiHandler;
    private Runnable onlineApiRunnable;
    private int onlineStatusDelay = 50000;

    private boolean isOnlineStatusApiCalled = false;

    private final int MENU_NORMAL = 0;
    private final int MENU_HIGHLIGHTED = 1;

    private int menuType;

    private String target_user_name = "";
    private String target_user_id;

    private DisplayMetrics displayMetrics = new DisplayMetrics();
    private int screenHeight;
    private int screenWidth;

    private boolean searchOpened = false;
    private int currentHighlightedResultPos = -1;

    private final static int MESSAGE_API_LIMIT = 20;
    private final static int MESSAGE_SEARCH_API_LIMIT = 100;

    private int BLOCKING_INTENT_CODE = 8102;

    private final int SHARED_INTENT_IMAGE_PERMISSION = 168;
    private final int INIT_ACTIVITY_PERMISSION = 169;

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            messagingService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            AlternateMessagingService.LocalBinder mLocalBinder = (AlternateMessagingService.LocalBinder) service;
            messagingService = mLocalBinder.getMessagingServiceInstance();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setUpActionBar();
        preferenceUtils = new PreferenceUtils(ChatActivity.this);
        commonFunctions = new CommonFunctions(ChatActivity.this);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;
        onlineApiHandler = new Handler();

        cid = getIntent().getStringExtra("cid");
        target_user_id = getIntent().getStringExtra(AppKeys.TARGET_USER_ID);

        recipientList = new ArrayList<>();
        blockStatus = getIntent().getStringExtra(AppKeys.CHAT_BLOCK_STATUS);
        if (blockStatus == null) {
            blockStatus = "";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat
                    .getColor(ChatActivity.this, R.color.status_bar_color));
        }

        initViews();

        if (cid == null || cid.isEmpty()) {
            hitCreateIndividualConversationApi(user_id, target_user_id);
        } else {
            initActivity();
        }
        //  displayFirebaseRegId();
    }

    private void hitCreateIndividualConversationApi(String user_id, String recipient) {
        rl_content_wrapper.setVisibility(View.GONE);
        pb_loading_content.setVisibility(View.VISIBLE);
        Call<PojoCreateNewConversationResponse> call = apiService.createConversation(user_id, recipient);
        call.enqueue(new Callback<PojoCreateNewConversationResponse>() {
            @Override
            public void onResponse(Call<PojoCreateNewConversationResponse> call,
                                   Response<PojoCreateNewConversationResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().data != null) {
                    cid = response.body().data;
                    initActivity();
                    rl_content_wrapper.setVisibility(View.VISIBLE);
                    pb_loading_content.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PojoCreateNewConversationResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(ChatActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                openNoConnectionDialog(getString(R.string.message));
            }
        });
    }

    private void openNoConnectionDialog(String title) {
        try {
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitCreateIndividualConversationApi(user_id, target_user_id);
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

    private void initActivity() {
        updateBottomBar();
        setTopBarData();
        initClickListeners();
        setTextWatcher();
        setClickListeners();
        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        initMessageList(cid);
        unreadConversations = preferenceUtils.getUnreadConversations();
        unreadConversations.remove(cid);
        preferenceUtils.saveUnreadConversations(unreadConversations);

        preferenceUtils.removeFcmMessageList(cid);

        initBroadCastReceiver();
        checkBlocking();

        checkIfMessageShared();

    }

    private void checkIfMessageShared() {
        if (!blockedFromChat) {
            String text = getIntent().getStringExtra(AppKeys.SHARED_TEXT);
            String image = getIntent().getStringExtra(AppKeys.SHARED_IMAGE);
            if (image != null && !image.isEmpty()) {
                requestStoragePermission(SHARED_INTENT_IMAGE_PERMISSION);
            } else if (text != null && !text.isEmpty()) {
                et_new_message.setText(text);
            } else {
                et_new_message.setText(preferenceUtils.getTempConvMessage(cid));
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent mIntent = new Intent(this, AlternateMessagingService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    private void updateBottomBar() {
        if (!searchOpened && !blockedFromChat) {
            rl_chat_bar.setVisibility(View.VISIBLE);
        } else {
            rl_chat_bar.setVisibility(View.GONE);
        }
    }

    private void setUpActionBar() {
        tl_top_bar = findViewById(R.id.tl_top_bar);
        setSupportActionBar(tl_top_bar);
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setTitle("");
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat
                    .getColor(ChatActivity.this, R.color.action_bar_color)));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(ContextCompat
                        .getColor(ChatActivity.this, R.color.status_bar_color));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActivityVisible = true;
        //    unsentMsgHandler.postDelayed(unsentMsgRunnable, 5000);

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.NEW_MESSAGE_FCM));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.MESSAGE_SEEN));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.GROUP_UPDATE_BROADCAST));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.MESSAGE_SENT));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.MESSAGE_FAILED));

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


        // clear the notification area when the app is opened
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        try {
          /*  if(getIntent().getStringExtra("source").equals("notif")){
                int count=preferenceUtils.getMessageNotificationCount();
                count--;
                preferenceUtils.saveMessageNotificationCount(count);
                if(count<=0){
                    notificationManager.cancel(AppKeys.MESSAGE_SUMMARY_ID);
                }
            }*/
            //   notificationManager.cancel(Integer.parseInt(cid));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (updateSeenPending) {
            updateSeenStatus();
        }
    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for userType intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                } else if (intent.getAction().equals(Config.NEW_MESSAGE_FCM)) {
                    if (cid.equals(intent.getStringExtra("conversation_id"))) {
                        hitGetNewMessagesApi(user_id, intent.getStringExtra("conversation_id"));
                        HashMap<String, Integer> unreadConversations;
                        unreadConversations = preferenceUtils.getUnreadConversations();
                        unreadConversations.remove(intent.getStringExtra("conversation_id"));
                        preferenceUtils.saveUnreadConversations(unreadConversations);

                    }
                } else if (intent.getAction().equals(Config.MESSAGE_SEEN)) {
                    String message_id = intent.getStringExtra("message_id");
                    String seen_time = intent.getStringExtra("seen_time");
                    String seen_user_id = intent.getStringExtra("user_id");
                    if (cid.equals(intent.getStringExtra("conversation_id"))) {
                        updateSeenStatusInList(seen_user_id, cid, message_id, seen_time);
                    }
                }
                //todo use different api to fetch group data
                else if (intent.getAction().equals(Config.GROUP_UPDATE_BROADCAST)) {

                    if (cid.equals(intent.getStringExtra("conversation_id"))) {
                        hitGetNewMessagesApi(user_id, intent.getStringExtra("conversation_id"));
                        groupNeedsUpdated = true;
                    }

                } else if (intent.getAction().equals(Config.MESSAGE_SENT)) {
                    messageList.clear();
                    messageList.addAll(addTimeDividerToList(preferenceUtils.getConversation(cid)));
                    chatMessageAdapter.notifyDataSetChanged();

                } else if (intent.getAction().equals(Config.MESSAGE_FAILED)) {
                    messageList.clear();
                    messageList.addAll(addTimeDividerToList(preferenceUtils.getConversation(cid)));
                    chatMessageAdapter.notifyDataSetChanged();

                }
            }
        };
    }

    private void updateSeenStatusInList(String seen_user_id, String cid, String message_id, String seen_time) {

        int foundIndex = 0;
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).messageId != null && messageList.get(i).messageId.equals(message_id)) {
                foundIndex = i;
                break;
            }
        }

        for (int j = foundIndex; j < messageList.size(); j++) {
            PojoChatMessage currentItem = messageList.get(j);
            boolean isSeen = commonFunctions.isMessageSeen(currentItem);

            if (currentItem.messageType == AppKeys.MESSAGE_TYPE_USER && !isSeen) {
                if (messageList.get(j).isMe) {
                    messageList.get(j).seen = "1";
                    messageList.get(j).seenTime = seen_time;
                    if (messageList.get(j).seendetail != null) {
                        if (!isAlreadyPresentInSeen(messageList.get(j).seendetail.get(0).seenusers, seen_user_id)) {
                            messageList.get(j).seendetail.get(0).seenusers.add(new PojoSeenuser(seen_user_id, seen_time));
                            for (int i = 0; i < messageList.get(j).seendetail.get(0).notseenusers.size(); i++) {
                                if (messageList.get(j).seendetail.get(0).notseenusers.get(i).equals(seen_user_id)) {
                                    messageList.get(j).seendetail.get(0).notseenusers.remove(i);
                                    break;
                                }
                            }
                        }

                    }
                }
                if (messageList.get(j).seen != null && messageList.get(j).seen.equals("0")) {
                    break;
                }
            }
        }
        chatMessageAdapter.notifyDataSetChanged();
        preferenceUtils.saveConversation(cid, messageList);
    }

    public boolean isAlreadyPresentInSeen(List<PojoSeenuser> seenusers, String seen_user_id) {
        for (int i = 0; i < seenusers.size(); i++) {
            if (seenusers.get(i).userid.equals(seen_user_id)) {
                return true;
            }
        }
        return false;
    }

    private List<PojoChatMessage> addTimeDividerToList(List<PojoChatMessage> messageList) {
        if (!messageList.isEmpty()) {
            for (int i = messageList.size() - 1; i >= 0; i--) {
                if (messageList.get(i).messageType == AppKeys.MESSAGE_TYPE_TIME_DIVIDER) {
                    messageList.remove(i);
                }
            }
            String dividerValue = "";
            if (messageList.get(0).messageType == AppKeys.MESSAGE_TYPE_USER) {
                dividerValue = commonFunctions.getChatDividerTime(messageList.get(0).time);
            }
            for (int i = 0; i < messageList.size(); i++) {
                String currentDividerValue = "";
                if (messageList.get(i).messageType == AppKeys.MESSAGE_TYPE_USER) {
                    currentDividerValue = commonFunctions.getChatDividerTime(messageList.get(i).time);
                    if (!currentDividerValue.equals(dividerValue)) {
                        PojoChatMessage timeMsg = new PojoChatMessage(dividerValue, AppKeys.MESSAGE_TYPE_TIME_DIVIDER);
                        messageList.add(i, timeMsg);
                        dividerValue = currentDividerValue;
                    }
                }
            }
        }
        return messageList;
    }


    private void initViews() {
        rl_search_wrapper = (RelativeLayout) findViewById(R.id.rl_search_wrapper);
        ib_previous_result = (ImageButton) findViewById(R.id.ib_previous_result);
        ib_next_result = (ImageButton) findViewById(R.id.ib_next_result);
        ib_back_gray = (ImageButton) findViewById(R.id.ib_back_gray);
        et_search_bar = (EditText) findViewById(R.id.et_search_bar);

        et_search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (currentHighlightedResultPos >= 0) {
                        //if the current hightlighted is teh last to be highlighted item
                        //then there are more results that can be highlighted
                        if (currentHighlightedResultPos != searchResultIndexList.size() - 1) {
                            unHighLightAllMessages();
                            messageList.get(searchResultIndexList
                                    .get(currentHighlightedResultPos)).isHighLighted = true;

                            if (chatListLayoutManager.findLastVisibleItemPosition() < searchResultIndexList
                                    .get(currentHighlightedResultPos)) {
                                rv_message_list.scrollToPosition(searchResultIndexList.get(currentHighlightedResultPos));
                            }
                        }
                        //there are no results to be highlighted so we call api for more messages
                        else {
                            // chatMessageAdapter.setShowProgressBar(true);
                            commonFunctions.openProgressDialog(getString(R.string.searching));
                            hitGetMessagesApi(user_id, cid, getLastMessageId(), MESSAGE_SEARCH_API_LIMIT);
                            // rv_message_list.smoothScrollToPosition(messageList.size());
                        }
                    } else {
                        if (!searchResultIndexList.isEmpty()) {
                            unHighLightAllMessages();
                            currentHighlightedResultPos = 0;
                            messageList.get(searchResultIndexList.get(currentHighlightedResultPos))
                                    .isHighLighted = true;
                        } else {
                            //   chatMessageAdapter.setShowProgressBar(true);
                            commonFunctions.openProgressDialog(getString(R.string.searching));
                            hitGetMessagesApi(user_id, cid, getLastMessageId(), MESSAGE_SEARCH_API_LIMIT);
                        }
                    }
                    chatMessageAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });

        rl_user_data_wrapper = (RelativeLayout) findViewById(R.id.rl_user_data_wrapper);
        rl_bottom_bar = (RelativeLayout) findViewById(R.id.rl_bottom_bar);
        ll_block_wrapper = (LinearLayout) findViewById(R.id.ll_block_wrapper);

        ll_unblock_wrapper = (LinearLayout) findViewById(R.id.ll_unblock_wrapper);
        tv_block_message = (TextView) findViewById(R.id.tv_block_message);
        tv_unblock = (TextView) findViewById(R.id.tv_unblock);
        rl_chat_bar = (RelativeLayout) findViewById(R.id.rl_chat_bar);

        rl_content_wrapper = (RelativeLayout) findViewById(R.id.rl_content_wrapper);
        pb_loading_content = (ProgressBar) findViewById(R.id.pb_loading_content);
        pb_loading_content.setVisibility(View.GONE);

        iv_user_dp = (ImageView) findViewById(R.id.iv_user_dp);
        tv_user_name = (TextView) findViewById(R.id.tv_user_name);
        tv_online_status = (TextView) findViewById(R.id.tv_online_status);
        et_new_message = (EditText) findViewById(R.id.et_new_message);
   /*     EmojIconActions emojIcon = new EmojIconActions(this, rl_parent, et_new_message, iv_emoji);
        emojIcon.ShowEmojIcon();
        emojIcon.setUseSystemEmoji(true);
        et_new_message.setUseSystemDefault(true);*/
        ib_back = (ImageButton) findViewById(R.id.ib_back);
        fab_send = (FloatingActionButton) findViewById(R.id.fab_send);
        iv_attach = (ImageView) findViewById(R.id.iv_attach);
        pb_api_status = (ProgressBar) findViewById(R.id.pb_api_status);
        tv_list_status = (TextView) findViewById(R.id.tv_list_status);
    }


    private void setTopBarData() {
        List<PojoConversationListItem> allConvList;
        allConvList = preferenceUtils.getAllMessageList();
        for (int i = 0; i < allConvList.size(); i++) {
            if (allConvList.get(i).conversationId.equals(cid)) {
                groupData = allConvList.get(i).groupData;
                if (groupData.groupId == null) {
                    PojoConversationListItem currentItem = allConvList.get(i);
                    setIndividualData(null, currentItem);
                    break;
                }
            }
        }
        setGroupData(groupData);
    }

    private void setGroupData(PojoGroupData groupData) {
        if (groupData != null) {
            if (groupData.groupId != null) {
                recipientList.clear();
                this.groupData = groupData;
                recipientList.addAll(groupData.members);
                groupNeedsUpdated = false;
                isGroup = true;
                if (chatMessageAdapter != null) {
                    chatMessageAdapter.setCreatorId(groupData.groupOwner);
                    chatMessageAdapter.notifyDataSetChanged();
                }
                final List<String> userList = new ArrayList<>();
                for (int j = 0; j < groupData.members.size(); j++) {
                    userList.add(groupData.members.get(j).userId);
                }
                onlineApiRunnable = new Runnable() {
                    public void run() {
                        if (!isOnlineStatusApiCalled) {
                            hitGetUserOnlineStatusApi(userList);
                        }
                        onlineApiHandler.postDelayed(this, onlineStatusDelay);
                    }
                };
                onlineApiHandler.postDelayed(onlineApiRunnable, 15000);
                if (!groupData.groupPhoto.isEmpty()) {
                    Glide.with(getApplicationContext())
                            .load(groupData.groupPhoto)
                            .apply(new RequestOptions().override(96, 96)
                                    .placeholder(R.drawable.blank_profile_male))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(iv_user_dp);
                } else {
                    Glide.with(getApplicationContext())
                            .load(R.drawable.placeholder)
                            .apply(new RequestOptions().override(96, 96)
                                    .placeholder(R.drawable.blank_profile_male))
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .into(iv_user_dp);
                }
                tv_user_name.setText(groupData.groupName);
            } else {

            }
        }
    }

    private void initClickListeners() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fab_send:
                        if (!et_new_message.getText().toString().isEmpty()) {
                            // addMsgToList(user_id, et_new_message.getText().toString(), "", RECIPIENT);
                            addMsgToList(user_id, et_new_message.getText().toString(), "");
                            et_new_message.getText().clear();
                        }
                        break;
                    case R.id.iv_attach:
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .start(ChatActivity.this);
                        break;

                    case R.id.ib_back:
                        finish();
                        break;

                   /* case R.id.ib_next_result:
                        if (currentHighlightedResultPos >= 0) {
                            if (currentHighlightedResultPos != 0) {
                                unHighLightAllMessages();
                                messageList.get(chatMessageAdapter.getResultIndexList()
                                        .get(--currentHighlightedResultPos)).isHighLighted = true;
                            }
                        } else {
                            unHighLightAllMessages();
                            currentHighlightedResultPos = 0;
                            messageList.get(chatMessageAdapter.getResultIndexList().get(currentHighlightedResultPos)).isHighLighted = true;
                        }
                        chatMessageAdapter.notifyDataSetChanged();
                        break;

                    case R.id.ib_previous_result:
                        if (currentHighlightedResultPos >= 0) {
                            if (currentHighlightedResultPos != chatMessageAdapter.getResultIndexList().size() - 1) {
                                unHighLightAllMessages();
                                messageList.get(chatMessageAdapter.getResultIndexList()
                                        .get(++currentHighlightedResultPos)).isHighLighted = true;
                            }
                            else {
                                hitGetMessagesApi(user_id, cid, ++offset);
                            }
                        } else {
                            unHighLightAllMessages();
                            currentHighlightedResultPos = 0;
                            messageList.get(chatMessageAdapter.getResultIndexList().get(currentHighlightedResultPos)).isHighLighted = true;
                        }
                        chatMessageAdapter.notifyDataSetChanged();
                        break;*/


                    case R.id.ib_next_result:
                        if (currentHighlightedResultPos >= 0) {
                            if (currentHighlightedResultPos != 0) {
                                unHighLightAllMessages();
                                messageList.get(searchResultIndexList
                                        .get(--currentHighlightedResultPos)).isHighLighted = true;
                                rv_message_list.scrollToPosition(searchResultIndexList.get(currentHighlightedResultPos));
                            }
                        } else {
                            if (!searchResultIndexList.isEmpty()) {
                                unHighLightAllMessages();
                                currentHighlightedResultPos = 0;
                                messageList.get(searchResultIndexList.get(currentHighlightedResultPos)).isHighLighted = true;
                                rv_message_list.scrollToPosition(searchResultIndexList.get(currentHighlightedResultPos));
                            }
                        }
                        chatMessageAdapter.notifyDataSetChanged();
                        break;

                    case R.id.ib_previous_result:
                        if (currentHighlightedResultPos >= 0) {
                            //if the current hightlighted is teh last to be highlighted item
                            //then there are more results that can be highlighted
                            if (currentHighlightedResultPos != searchResultIndexList.size() - 1) {
                                unHighLightAllMessages();
                                messageList.get(searchResultIndexList
                                        .get(++currentHighlightedResultPos)).isHighLighted = true;

                                if (chatListLayoutManager.findLastVisibleItemPosition() < searchResultIndexList
                                        .get(currentHighlightedResultPos)) {
                                    rv_message_list.scrollToPosition(searchResultIndexList.get(currentHighlightedResultPos));
                                }
                            }
                            //there are no results to be highlighted so we call api for more messages
                            else {
                                // chatMessageAdapter.setShowProgressBar(true);
                                commonFunctions.openProgressDialog(getString(R.string.searching));
                                hitGetMessagesApi(user_id, cid, getLastMessageId(), MESSAGE_SEARCH_API_LIMIT);
                                // rv_message_list.smoothScrollToPosition(messageList.size());
                            }
                        } else {
                            if (!searchResultIndexList.isEmpty()) {
                                unHighLightAllMessages();
                                currentHighlightedResultPos = 0;
                                messageList.get(searchResultIndexList.get(currentHighlightedResultPos))
                                        .isHighLighted = true;
                            } else {
                                //   chatMessageAdapter.setShowProgressBar(true);
                                commonFunctions.openProgressDialog(getString(R.string.searching));
                                hitGetMessagesApi(user_id, cid, getLastMessageId(), MESSAGE_SEARCH_API_LIMIT);
                            }
                        }
                        chatMessageAdapter.notifyDataSetChanged();
                        break;


                    case R.id.ib_back_gray:
                        hideSearchAnim();
                        break;

                    case R.id.rl_user_data_wrapper:
                        if (groupId != null && !groupId.isEmpty()) {
                            Intent groupIntent = new Intent(ChatActivity.this, GroupInfoActivity.class);
                            groupIntent.putExtra("group_id", groupId);
                            startActivity(groupIntent);
                        } else if (target_user_id != null) {
                            Intent profileIntent = new Intent(ChatActivity.this, ProfileInfoActivity.class);
                            profileIntent.putExtra(AppKeys.TARGET_USER_ID, target_user_id);
                            startActivity(profileIntent);
                        }
                        break;
                }
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void revealSearchAnim() {
        searchOpened = true;
        invalidateOptionsMenu();
        updateBottomBar();
        Animator anim;
        anim = ViewAnimationUtils.createCircularReveal(rl_search_wrapper, screenWidth, 0,
                0, screenWidth);
        anim.setDuration((long) 250);
        anim.start();
        rl_search_wrapper.setVisibility(View.VISIBLE);
        et_search_bar.requestFocus();
        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (et_search_bar.requestFocus()) {
                    InputMethodManager imm = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(et_search_bar, InputMethodManager.SHOW_FORCED);
                }
                chatMessageAdapter.setSelectionEnabled(true);
                getQueryContainingMesssages(et_search_bar.getText().toString());

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        //  sv_search.setIconified(false);

       /* ImageView icon = sv_search.findViewById(android.support.v7.appcompat.R.id.search_button);
        icon.setColorFilter(Color.TRANSPARENT);*/

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void hideSearchAnim() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_search_bar.getWindowToken(), 0);

        Animator anim;

        anim = ViewAnimationUtils.createCircularReveal(rl_search_wrapper, screenWidth, 0, screenWidth, 0);
        anim.setDuration((long) 250);
        anim.start();

        anim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                rl_search_wrapper.setVisibility(View.GONE);
                et_search_bar.getText().clear();
                searchResultIndexList.clear();
                currentHighlightedResultPos = -1;
                searchOpened = false;
                invalidateOptionsMenu();
                updateBottomBar();
                chatMessageAdapter.setSelectionEnabled(false);
                chatMessageAdapter.updateSearchQuery("");
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void setClickListeners() {
        fab_send.setOnClickListener(onClickListener);
        iv_attach.setOnClickListener(onClickListener);
        ib_back.setOnClickListener(onClickListener);
        ib_back_gray.setOnClickListener(onClickListener);

        ib_next_result.setOnClickListener(onClickListener);
        ib_previous_result.setOnClickListener(onClickListener);
        rl_user_data_wrapper.setOnClickListener(onClickListener);
    }

    public void setTextWatcher() {
        et_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (searchOpened) {
                    chatMessageAdapter.setSelectionEnabled(true);
                    chatMessageAdapter.updateSearchQuery(charSequence.toString());
                    getQueryContainingMesssages(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_new_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > CHAT_MESSAGE_MAX_LENGTH) {
                    commonFunctions.setToastMessage(getApplicationContext(),
                            getString(R.string.message_no_longer_than_5000), Toast.LENGTH_LONG,
                            AppKeys.TOAST_USER);
                    et_new_message.setText(charSequence.subSequence(0, CHAT_MESSAGE_MAX_LENGTH));
                    et_new_message.setSelection(et_new_message.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void initMessageList(final String cid) {
        rl_bottom_bar.setVisibility(View.GONE);
        messageList = new ArrayList<>();
        rv_message_list = (RecyclerView) findViewById(R.id.rv_message_list);
        chatMessageAdapter = new GroupChatMessageAdapter(ChatActivity.this, messageList, cid);
        rv_message_list.getRecycledViewPool().setMaxRecycledViews(0, 0);
        chatListLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, true);
        //  chatListLayoutManager.setStackFromEnd(true);
        rv_message_list.setLayoutManager(chatListLayoutManager);
        rv_message_list.setAdapter(chatMessageAdapter);

/*        if (!cid.isEmpty()) {
            messageList.clear();
            if(preferenceUtils.getConversation(cid)!=null){
                messageList.addAll(addTimeDividerToList(preferenceUtils.getConversation(cid)));
                chatMessageAdapter.notifyDataSetChanged();
                for (int i = messageList.size() - 1; i >= 0; i--) {
                    if (messageList.get(i).messageId != null && !messageList.get(i).messageId.isEmpty()) {
                        currentTempMsgId = Long.parseLong(messageList.get(i).messageId);
                        break;
                    }
                }
            }
          *//*  if(!messageList.isEmpty()){
                currentTempMsgId=messageList.get(messageList.size()-1).tempMsgId;
            }*//*

            // rv_message_list.scrollToPosition(0);
        }*/

        rv_message_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy < 0) {
                    if (enableApiCall && chatListLayoutManager.findLastVisibleItemPosition() >= messageList.size() - 2) {
                        hitGetMessagesApi(user_id, cid, getLastMessageId(), MESSAGE_API_LIMIT);
                    }
                } else if (dy > 0) {
                    if (tv_list_status.getText().toString().equals(getString(R.string.no_more_messages))) {
                        tv_list_status.setVisibility(View.GONE);
                    }
                }

            }
        });

        //api call for get messages is inside the below function
        requestStoragePermission(INIT_ACTIVITY_PERMISSION);

    }

    private void addMsgToList(String USER_ID, String message, String image) {

      /*  PojoGetChatMessagesResponseData newMsg =
                new PojoGetChatMessagesResponseData(USER_ID, message, image, currentTime.toString(), RECIPIENT);*/


        String imgDimensions = "";
        if (!image.isEmpty()) {
            calculateFinalDimensions(image);
            imgDimensions = prefImgHeight + "X" + prefImgWidth;

        }
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String newDateStr = source.format(currentTime);

        Date date = null;
        try {
            date = source.parse(newDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //converted to GMT as the time field from backend is alos in GMT as we have parsed it to local time in adapter
        source.setTimeZone(TimeZone.getTimeZone("GMT"));

        newDateStr = source.format(date);


        int tempMsgId = preferenceUtils.getTempMessageIdCounter();
        tempMsgId++;
        preferenceUtils.saveTempMessageIdCounter(tempMsgId);


        PojoSeendetail seendetail = new PojoSeendetail("");
        if (groupData != null && groupData.members != null) {
            for (int i = 0; i < groupData.members.size(); i++) {
                seendetail.notseenusers.add(groupData.members.get(i).userId);
            }
        } else {
            seendetail.notseenusers.add(target_user_id);
        }

        PojoChatMessage newMsg =
                new PojoChatMessage(USER_ID, message.trim(), image, imgDimensions, newDateStr,
                        String.valueOf(tempMsgId), seendetail);
        if (!image.isEmpty()) {
            newMsg.localImagePath = image;
        }

        newMsg.isImageUploading = true;


        while (true) {
            if (GlobalVariables.isIndividualChatMutexAvailable) {
                GlobalVariables.isIndividualChatMutexAvailable = false;
                messageList.clear();
                messageList.addAll(addTimeDividerToList(preferenceUtils.getConversation(cid)));
                messageList.add(0, newMsg);
                preferenceUtils.saveConversation(cid, messageList);
                GlobalVariables.isIndividualChatMutexAvailable = true;
                break;
            } else {
                try {
                    Thread.sleep(50);
                    Log.d("messaging_service", "individual chat mutex waiting");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


        //mutex used to prevent concurrent updating of unsent conv map in preference
        while (true) {
            if (GlobalVariables.isConversationMapMutexAvailable) {
                GlobalVariables.isConversationMapMutexAvailable = false;
                HashMap<String, Integer> unsentConversationIdMap;
                unsentConversationIdMap = preferenceUtils.getUnsentConversationsMap();
                unsentConversationIdMap.put(cid, Integer.parseInt(cid));
                preferenceUtils.saveUnsentConversationsMap(unsentConversationIdMap);
                Log.d("messaging_service", message + " added to " + cid + "by chat");
                GlobalVariables.isConversationMapMutexAvailable = true;

                messagingService.informNewUnsentConversation();
                break;
            } else {
                try {
                    Thread.sleep(100);
                    Log.d("messaging_service", "conversation map mutex waiting");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        chatMessageAdapter.notifyDataSetChanged();

        ///the bottom code is to move the conversation to top of the list
        List<PojoConversationListItem> conversationList = preferenceUtils.getAllMessageList();
        PojoConversationListItem targetConv;
        for (int i = 0; i < conversationList.size(); i++) {
            if (conversationList.get(i).conversationId.equals(cid)) {
                targetConv = conversationList.get(i);
                conversationList.remove(i);
                conversationList.add(0, targetConv);
                break;
            }
        }
        preferenceUtils.saveAllMessageList(conversationList);
        Intent pushNotification = new Intent(Config.REFRESH_CONV_LIST_FROM_PREFS);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);

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
                case INIT_ACTIVITY_PERMISSION:
                    if (!cid.isEmpty() && enableApiCall) {
                        hitGetMessagesApi(user_id, cid, getLastMessageId(), MESSAGE_API_LIMIT);
                    }
                    break;

                case SHARED_INTENT_IMAGE_PERMISSION:
                    String image = getIntent().getStringExtra(AppKeys.SHARED_IMAGE);
                    if (image != null && !image.isEmpty()) {
                        File photo = getScaledDownImage(image);
                        openImageUploadDialog(photo.getPath(), getIntent().getStringExtra(AppKeys.SHARED_TEXT));

                    }
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
                case INIT_ACTIVITY_PERMISSION:
                    if (!cid.isEmpty() && enableApiCall) {
                        hitGetMessagesApi(user_id, cid, getLastMessageId(), MESSAGE_API_LIMIT);
                    }
                    break;

                case SHARED_INTENT_IMAGE_PERMISSION:
                    String image = getIntent().getStringExtra(AppKeys.SHARED_IMAGE);
                    if (image != null && !image.isEmpty()) {
                        File photo = getScaledDownImage(image);
                        openImageUploadDialog(photo.getPath(), getIntent().getStringExtra(AppKeys.SHARED_TEXT));
                    }
                    break;
            }

        } else {
            finish();
        }

        // other 'case' lines to check for other
        // permissions this app might request.

    }


    private List<PojoChatMessage> modMsgList(List<PojoChatMessage> messageList) {
        for (int i = messageList.size() - 1; i >= 0; i--) {
            PojoChatMessage currentMsg = messageList.get(i);

            if (currentMsg.userId.equals("0")) {
                currentMsg.messageType = AppKeys.MESSAGE_TYPE_SYSTEM;
            } else {
                if (currentMsg.messageType != AppKeys.MESSAGE_TYPE_TIME_DIVIDER) {
                    currentMsg.messageType = AppKeys.MESSAGE_TYPE_USER;
                }
            }

            currentMsg.setSentState(AppKeys.MESSAGE_STATE_SENT);
            if (groupData == null || groupData.groupId == null) {
                currentMsg.isFromNewUser = false;

            } else {
                if (currentMsg.userId.equals(latest_message_user_id)) {
                    currentMsg.isFromNewUser = false;
                } else {
                    currentMsg.isFromNewUser = true;
                    latest_message_user_id = currentMsg.userId;
                }
            }

        }
        return messageList;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                File scaledDownImage = getScaledDownImage(result.getUri().getPath());
                openImageUploadDialog(scaledDownImage.getPath(), et_new_message.getText().toString());

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

        if (requestCode == FORWARD_MESSAGE_CODE) {
            if (resultCode == RESULT_OK) {
                disableSelections();
                hitGetNewMessagesApi(user_id, cid);
            }
        }

        if (requestCode == BLOCKING_INTENT_CODE) {
            initMessageList(cid);

        }
    }

    private File getScaledDownImage(String path) {
// we'll start with the original picture already open to a cover_photo
        File imgFileOrig = new File(path); //change "getPic()" for whatever you need to open the image cover_photo.
        Bitmap b = BitmapFactory.decodeFile(imgFileOrig.getAbsolutePath());
// original measurements
        prefImgWidth = b.getWidth();
        prefImgHeight = b.getHeight();

        final int destWidth = 720;//or the width you need

        if (prefImgWidth > destWidth) {
            // picture is wider than we want it, we calculate its target height
            int destHeight = (int) ((1f * destWidth * prefImgHeight) / prefImgWidth);
            // we create an scaled bitmap so it reduces the image, not just trim it
            Bitmap b2 = Bitmap.createScaledBitmap(b, destWidth, destHeight, false);

            prefImgHeight = destHeight;
            prefImgWidth = destWidth;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            // compress to the format you want, JPEG, PNG...
            // 70 is the 0-100 quality percentage
            b2.compress(Bitmap.CompressFormat.JPEG, 70, outStream);
            // we save the cover_photo, at least until we have made use of it
            Calendar calendar = Calendar.getInstance();

            File f = new File(getCacheDir(), calendar.getTime() + "temp_chat_image.jpg");

            try {
                if (f.exists()) {
                    f.delete();
                }
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //write the bytes in cover_photo
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
        return imgFileOrig;


    }

    private void calculateFinalDimensions(String path) {
        File imgFileOrig = new File(path); //change "getPic()" for whatever you need to open the image file.
        Bitmap b = BitmapFactory.decodeFile(imgFileOrig.getAbsolutePath());
// original measurements
        int origWidth = b.getWidth();
        int origHeight = b.getHeight();

        prefImgWidth = origWidth;
        prefImgHeight = origHeight;

        final int destWidth = 720;//or the width you need

        if (origWidth > destWidth) {

            // picture is wider than we want it, we calculate its target height
            float scale = 1f * destWidth / origWidth;
            int destHeight = (int) (origHeight * scale);
            prefImgWidth = destWidth;
            prefImgHeight = destHeight;
        }
    }


    private void openImageUploadDialog(final String imgPath, String text) {
        imageUploadDialog = new MyCustomThemeDialog(ChatActivity.this, R.style.Custom_Dialog);
        imageUploadDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        imageUploadDialog.setContentView(R.layout.image_upload_dialog);
        imageUploadDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        FloatingActionButton fab_upload;
        ImageView iv_photo;
        final EditText et_message;
        RelativeLayout rl_parent_wrapper;

        rl_parent_wrapper = (RelativeLayout) imageUploadDialog.findViewById(R.id.rl_venue_wrapper);
        fab_upload = (FloatingActionButton) imageUploadDialog.findViewById(R.id.fab_upload);
        iv_photo = (ImageView) imageUploadDialog.findViewById(R.id.iv_photo);
        et_message = (EditText) imageUploadDialog.findViewById(R.id.et_message);


        ImageDimensions dimen = getScreenScaledDimensions(AppKeys.ARTICLE);

        iv_photo.getLayoutParams().height = dimen.height;
        iv_photo.getLayoutParams().width = dimen.width;

        Glide.with(ChatActivity.this)
                .load(new File(imgPath))
                .apply(new RequestOptions().override(dimen.width, dimen.height))
                .into(iv_photo);

        et_message.setText(text);

        fab_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                addMsgToList(user_id, et_message.getText().toString(), result.getUri().getPath(), RECIPIENT);

                addMsgToList(user_id, et_message.getText().toString(), imgPath);
                imageUploadDialog.dismiss();
                et_new_message.getText().clear();
            }
        });
        imageUploadDialog.show();
    }

    public ImageDimensions getScreenScaledDimensions(String type) {
        ImageDimensions dimen = new ImageDimensions();
        int height = prefImgHeight;
        int width = prefImgWidth;
        float imgRatio = 1f * width / height;
        int targetWidth = (int) (displayMetrics.widthPixels);
        int targetHeight = (int) (1f * targetWidth / imgRatio);

        if (targetHeight > displayMetrics.heightPixels) {
            targetHeight = displayMetrics.heightPixels;
        }

        dimen.width = targetWidth;
        dimen.height = targetHeight;
        return dimen;
    }


    private void hitGetMessagesApi(final String user_id, final String cid,
                                   final String last_message_id, final int limit) {
        Call<PojoGetChatMessagesResponse> call = apiService.getUserChatMessages(user_id, cid, last_message_id, String.valueOf(limit));
        enableApiCall = false;
        if (last_message_id == null) {
            pb_api_status.setVisibility(View.VISIBLE);
        } else {
            chatMessageAdapter.setShowProgressBar(true);
        }
        tv_list_status.setVisibility(View.GONE);

        call.enqueue(new Callback<PojoGetChatMessagesResponse>() {
            @Override
            public void onResponse(Call<PojoGetChatMessagesResponse> call, Response<PojoGetChatMessagesResponse> response) {

                if (response.body() != null && response.body().data != null) {

                    if (tv_list_status.getText().toString().equals(getString(R.string.cannot_connect_server))) {
                        tv_list_status.setText(getString(R.string.back_online));
                        tv_list_status.setBackgroundColor(ContextCompat.getColor(ChatActivity.this,
                                R.color.online_green));
                        tv_list_status.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tv_list_status.setVisibility(View.GONE);
                            }
                        }, 1500);
                    }
                    pojoGetChatMessagesResponse = response.body();
                    blockStatus = pojoGetChatMessagesResponse.data.blocked;
                    checkBlocking();

                /*    if (offset == 0 && !response.body().data.messages.isEmpty()
                            && response.body().data.messages.get(0).isMe != null
                            && !response.body().data.messages.get(0).isMe
                            && response.body().data.messages.get(0).seen.equals("0")) {
                        hitConversationSeenApi(user_id, cid, response.body().data.messages.get(0).messageId);
                    }*/

                    String message = response.body().message;
                    invalidateOptionsMenu();
                    response.body().data.messages = modMsgList(response.body().data.messages);

                    if (response.body().data.messages.isEmpty()) {
                        tv_list_status.setText(R.string.no_more_messages);
                        tv_list_status.setVisibility(View.VISIBLE);
                        tv_list_status.setBackgroundResource(R.color.app_theme_medium);
                        if (searchOpened) {
                            commonFunctions.closeProgressDialog();
                        }
                    }

                    // Collections.reverse(response.body().data.messages);
                    if (last_message_id == null) {
                        if (pojoGetChatMessagesResponse.data.groupData != null
                                && pojoGetChatMessagesResponse.data.groupData.groupId != null) {
                            setGroupData(pojoGetChatMessagesResponse.data.groupData);
                            groupId = response.body().data.groupData.groupId;
                        } else {
                            setIndividualData(pojoGetChatMessagesResponse.data, null);
                        }
                        messageList.clear();
                        messageList.addAll(response.body().data.messages);
                        if (!messageList.isEmpty()) {
                            latest_message_user_id = messageList.get(0).userId;
                            latest_sent_msg_id = messageList.get(0).messageId;
                        }
                        List<PojoChatMessage> savedList = preferenceUtils.getConversation(cid);

                        for (int j = savedList.size() - 1; j >= 0; j--) {
                            if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT) {
                                messageList.add(0, savedList.get(j));
                            }
                        }
                    } else {
                     /*   response.body().data.addAll(messageList);
                        messageList.clear();*/
                        chatMessageAdapter.setShowProgressBar(false);

                        if (!response.body().data.messages.isEmpty() && messageList.get((messageList.size() - 1)).userId
                                .equals(response.body().data.messages.get(0).userId)) {
                            messageList.get((messageList.size() - 1)).isFromNewUser = false;
                        }
                        messageList.addAll(response.body().data.messages);
                        if (!messageList.isEmpty()) {
                            latest_message_user_id = messageList.get(0).userId;
                            latest_sent_msg_id = messageList.get(0).messageId;
                        }

                    }
                    preferenceUtils.saveConversation(cid, messageList);
                    List<PojoChatMessage> tempList = new ArrayList<>();
                    tempList.addAll(addTimeDividerToList(messageList));
                    messageList.clear();
                    messageList.addAll(tempList);
                    chatMessageAdapter.notifyDataSetChanged();

                    if (last_message_id == null) {
                        if (isActivityVisible) {
                            updateSeenStatus();
                        } else {
                            updateSeenPending = true;
                        }
                    }

                    //if we are currently searching
                    if (searchOpened) {
                        //if the received list is not empty
                        if (!response.body().data.messages.isEmpty()) {
                            chatMessageAdapter.setSelectionEnabled(true);
                            chatMessageAdapter.updateSearchQuery(et_search_bar.getText().toString());
                            getQueryContainingMesssages(et_search_bar.getText().toString());


                            if (!searchResultIndexList.isEmpty()) {
                                if (++currentHighlightedResultPos > searchResultIndexList.size() - 1) {
                                    currentHighlightedResultPos = searchResultIndexList.size() - 1;
                                }
                                unHighLightAllMessages();
                                messageList.get(searchResultIndexList
                                        .get(currentHighlightedResultPos)).isHighLighted = true;
                                if (chatListLayoutManager.findLastVisibleItemPosition() < searchResultIndexList
                                        .get(currentHighlightedResultPos)) {
                                    rv_message_list.scrollToPosition(searchResultIndexList.get(currentHighlightedResultPos));
                                }
                            }

                            //if we cannot find any match in current list then call api for previous messages
                            if (!isResultPresentInResponse(response.body().data.messages,
                                    et_search_bar.getText().toString())) {
                                enableApiCall = true;
                                hitGetMessagesApi(user_id, cid, getLastMessageId(), MESSAGE_SEARCH_API_LIMIT);
                            } else {
                                pb_api_status.setVisibility(View.GONE);
                                commonFunctions.closeProgressDialog();
                            }
                        } else {
                            commonFunctions.showMessageDialog("No results found");
                        }
                    } else {
                        pb_api_status.setVisibility(View.GONE);
                    }

                }

                enableApiCall = true;

            }

            @Override
            public void onFailure(Call<PojoGetChatMessagesResponse> call, Throwable t) {

                //todo why was this code snippet needed?
                if (!cid.isEmpty()) {
                    messageList.clear();
                    if (preferenceUtils.getConversation(cid) != null) {
                        messageList.addAll(addTimeDividerToList(preferenceUtils.getConversation(cid)));
                        chatMessageAdapter.notifyDataSetChanged();
                     /*   for (int i = messageList.size() - 1; i >= 0; i--) {
                            if (messageList.get(i).messageId != null && !messageList.get(i).messageId.isEmpty()) {
                                temp = Long.parseLong(messageList.get(i).messageId);
                                break;
                            }
                        }*/
                    }
                }

                checkBlocking();

                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                enableApiCall = true;
                pb_api_status.setVisibility(View.GONE);
                tv_list_status.setText(getString(R.string.cannot_connect_server));
                tv_list_status.setBackgroundColor(ContextCompat.getColor(ChatActivity.this, R.color.error_red));
                tv_list_status.setVisibility(View.VISIBLE);

                if (searchOpened) {
                    commonFunctions.closeProgressDialog();
                }
            }
        });
    }

    private void checkBlocking() {
        rl_bottom_bar.setVisibility(View.VISIBLE);

        if (blockStatus == null) {
            blockStatus = "";
        }
        if (blockStatus.equals(BLOCK_STATUS_HE_BLOCK)) {
            rl_chat_bar.setVisibility(View.GONE);
            ll_block_wrapper.setVisibility(View.VISIBLE);
            ll_unblock_wrapper.setVisibility(View.GONE);
            tv_block_message.setText(getString(R.string.you_cannot_send_messages_in_conversation));
            blockedFromChat = true;
        } else if (blockStatus.equals(BLOCK_STATUS_I_BLOCK)) {
            rl_chat_bar.setVisibility(View.GONE);
            ll_unblock_wrapper.setVisibility(View.VISIBLE);
            ll_block_wrapper.setVisibility(View.VISIBLE);
            blockedFromChat = true;
            tv_block_message.setText(getString(R.string.this_user_is_blocked));
            tv_unblock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent blockIntent = new Intent(ChatActivity.this, BlockingListActivity.class);
                    startActivityForResult(blockIntent, BLOCKING_INTENT_CODE);
                }
            });
        } else {
            ll_block_wrapper.setVisibility(View.GONE);
        }
        updateBottomBar();
    }

    private boolean isResultPresentInResponse(List<PojoChatMessage> messages, String s) {
        boolean present = false;
        s = s.toLowerCase();
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).messageType == AppKeys.MESSAGE_TYPE_USER) {
                String msgLower = messages.get(i).message.toLowerCase();
                if (msgLower.contains(s)) {
                    present = true;
                    break;
                }
            }
        }
        return present;
    }

    public String getLastMessageId() {
        if (messageList == null || messageList.isEmpty()) {
            return null;
        } else {
            if (messageList.get(messageList.size() - 1).messageId == null
                    || messageList.get(messageList.size() - 1).messageId.equals("-1")) {
                return null;
            } else {
                return messageList.get(messageList.size() - 1).messageId;
            }
        }
    }

    private void setIndividualData(PojoGetChatMessagesResponseData data,
                                   PojoConversationListItem conversationItem) {
        String recipientId = "";

        if (data != null) {
            try {
                Glide.with(getApplicationContext())
                        .load(data.userPicture)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .apply(new RequestOptions().override(96, 96)
                                .placeholder(R.drawable.blank_profile_male))
                        .into(iv_user_dp);
            } catch (Exception e) {
                e.printStackTrace();
            }

            tv_user_name.setText(data.userFullname);
            recipientId = data.userId;
            target_user_id = data.userId;
            target_user_name = data.userFullname;

            recipientList.clear();
            recipientList.add(new PojoGroupMember(data.userId, data.userFullname, data.userPicture, ""));

        } else if (conversationItem != null) {
            String picPath = "";
            recipientId = conversationItem.ids;
            target_user_id = conversationItem.ids;
            target_user_name = conversationItem.name;
            if (conversationItem.pictureLeft != null && !conversationItem.pictureLeft.isEmpty()) {
                picPath = conversationItem.pictureLeft;
                Glide.with(getApplicationContext())
                        .load(conversationItem.pictureLeft)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .apply(new RequestOptions().override(96, 96)
                                .placeholder(R.drawable.blank_profile_male))
                        .into(iv_user_dp);
                tv_user_name.setText(conversationItem.name);
            } else {
                if (!conversationItem.picture.isEmpty()) {
                    picPath = conversationItem.picture;
                    Glide.with(getApplicationContext())
                            .load(conversationItem.picture)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .apply(new RequestOptions().override(96, 96)
                                    .placeholder(R.drawable.blank_profile_male))
                            .into(iv_user_dp);
                } else {
                    Glide.with(getApplicationContext())
                            .load(R.drawable.placeholder)
                            .apply(bitmapTransform(new CropCircleTransformation()))
                            .apply(new RequestOptions().override(96, 96)
                                    .placeholder(R.drawable.blank_profile_male))
                            .into(iv_user_dp);
                }
                tv_user_name.setText(conversationItem.name);
            }
            recipientList.clear();
            recipientList.add(new PojoGroupMember(conversationItem.ids, conversationItem.name,
                    picPath, "", ""));
        }

        final List<String> onlineList = new ArrayList<>();
        onlineList.add(recipientId);

        onlineApiRunnable = new Runnable() {
            public void run() {
                if (!isOnlineStatusApiCalled) {
                    hitGetUserOnlineStatusApi(onlineList);
                }
                onlineApiHandler.postDelayed(this, onlineStatusDelay);
            }
        };
        onlineApiHandler.postDelayed(onlineApiRunnable, 5000);
        invalidateOptionsMenu();

    }

    private void hitGetNewMessagesApi(final String user_id, final String cid) {

        final List<PojoChatMessage> moddedList = preferenceUtils.getConversation(cid);
        for (int i = 0; i < moddedList.size(); i++) {
            if (moddedList.get(i).sentState == AppKeys.MESSAGE_STATE_SENT) {
                latest_sent_msg_id = moddedList.get(i).messageId;
                break;
            }
        }

        Call<PojoGetChatMessagesResponse> call = apiService.getUserChatNewMessages(user_id, cid,
                latest_sent_msg_id, "next");
        call.enqueue(new Callback<PojoGetChatMessagesResponse>() {
            @Override
            public void onResponse(Call<PojoGetChatMessagesResponse> call, Response<PojoGetChatMessagesResponse> response) {
                if (response.body() != null && response.body().data != null) {
                    if (tv_list_status.getText().toString().equals(getString(R.string.cannot_connect_server))) {
                        tv_list_status.setText(getString(R.string.back_online));
                        tv_list_status.setBackgroundColor(ContextCompat.getColor(ChatActivity.this,
                                R.color.online_green));
                        tv_list_status.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tv_list_status.setVisibility(View.GONE);
                            }
                        }, 1500);
                    }
                    String message = response.body().message;

                   /* if (offset == 0 && !response.body().data.messages.isEmpty()
                            && response.body().data.messages.get(0).isMe != null
                            && !response.body().data.messages.get(0).isMe
                            && response.body().data.messages.get(0).seen.equals("0")) {
                        hitConversationSeenApi(user_id, cid, response.body().data.messages.get(0).messageId);
                    }

                    if (!message.isEmpty()
                            && response.body().data.messages.get(0).isMe != null
                            && !response.body().data.messages.get(0).isMe
                            && response.body().data.messages.get(0).seen.equals("0")) {
                        hitConversationSeenApi(user_id, cid, response.body().data.messages.get(0).messageId);
                    }*/

                    blockStatus = response.body().data.blocked;
                    checkBlocking();

                    if (groupNeedsUpdated) {
                        if (response.body().data.groupData.groupId != null) {
                            setGroupData(response.body().data.groupData);
                            updateBottomBar();
                        }
                    }

                    if (!messageList.isEmpty()) {
                        latest_message_user_id = messageList.get(0).userId;
                    }

                    List<PojoChatMessage> newMsgList = modMsgList(response.body().data.messages);

                    //  Collections.reverse(response.body().data.messages);
                    messageList.clear();
                    messageList.addAll(preferenceUtils.getConversation(cid));
                    for (int i = 0; i < newMsgList.size(); i++) {
                        //only addChatMemberListAdapter new msgs that were not sent by sender
                        if (!newMsgList.get(i).userId.equals(user_id)) {
                            boolean alreadyPresent = false;
                            for (int j = messageList.size() - 1; j >= 0; j--) {
                                if (newMsgList.get(i).messageId != null && messageList.get(j).messageId != null) {
                                    if (newMsgList.get(i).messageId.equals(messageList.get(j).messageId)) {
                                        alreadyPresent = true;
                                        break;
                                    }
                                }
                            }
                            if (!alreadyPresent) {
                                messageList.add(0, newMsgList.get(i));
                            }
                        }
                    }
                    latest_sent_msg_id = messageList.get(0).messageId;

                    // messageList=modMsgList(messageList);
                    preferenceUtils.saveConversation(cid, messageList);
                    List<PojoChatMessage> tempList = new ArrayList<>();
                    tempList.addAll(addTimeDividerToList(messageList));
                    messageList.clear();
                    messageList.addAll(tempList);
                    chatMessageAdapter.notifyDataSetChanged();

                    if (isActivityVisible) {
                        updateSeenStatus();
                    } else {
                        updateSeenPending = true;
                    }

                    /*HashMap<String, String> seenCidMap = preferenceUtils.getSeenConversations();
                    if (seenCidMap.get(cid) != null) {
                        int latestMessageId = Integer.parseInt(response.body().data.messages
                                .get(response.body().data.messages.size() - 1).messageId);
                        int seenMsgId = Integer.parseInt(seenCidMap.get(cid));
                        if (latestMessageId > seenMsgId) {
                        }
                    }*/
                    commonFunctions.setToastMessage(getApplicationContext(), "" + message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);


                }
            }

            @Override
            public void onFailure(Call<PojoGetChatMessagesResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

            }
        });
    }

    private void updateSeenStatus() {
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).isMe != null && !messageList.get(i).isMe) {
                if (!commonFunctions.isMessageSeen(messageList.get(i))) {
                    hitConversationSeenApi(user_id, cid, messageList.get(i).messageId);
                    break;
                }
            }
        }
    }

    private void hitConversationSeenApi(String user_id, final String cid, String latestMessageId) {
        Call<PojoNoDataResponse> call = apiService.conversationSeen(user_id, cid, latestMessageId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call,
                                   Response<PojoNoDataResponse> response) {

                commonFunctions.setToastMessage(getApplicationContext(), "seen ", Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);

           /*     String message = response.body().message;
                HashMap<String, String> seenCidMap = preferenceUtils.getSeenConversations();
                seenCidMap.put(cid,String.valueOf(latestMessageId));
                preferenceUtils.saveSeenConversations(seenCidMap);*/
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void hitGetUserOnlineStatusApi(List<String> user_id_list) {

        if (!isOnlineStatusApiCalled) {
            isOnlineStatusApiCalled = true;
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
                            if (response.body().data.size() > 1) {
                                int onlineCount = 0;
                                for (int i = 0; i < response.body().data.size(); i++) {
                                    if (response.body().data.get(i).isOnline.equals("1")) {
                                        onlineCount++;
                                    }
                                }
                                tv_online_status.setText(onlineCount + " "
                                        + getString(R.string.members) + " " + getString(R.string.online));
                            } else {
                                if (response.body().data.get(0).isOnline.equals("1")) {
                                    tv_online_status.setText(getString(R.string.online));
                                } else {
                                    tv_online_status.setText(getString(R.string.last_seen) + " "
                                            + commonFunctions.parseLastSeenTime(response.body().data.get(0).lastSeen));
                                }
                            }
                        }
                    }
                    isOnlineStatusApiCalled = false;

                }

                @Override
                public void onFailure(Call<PojoGetUserOnlineStatusResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    isOnlineStatusApiCalled = true;
                }
            });
        }

    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (searchOpened) {
            menu.clear();
        } else {
            if (menuType == MENU_NORMAL) {
                getMenuInflater().inflate(R.menu.chat_activity_menu_normal, menu);
                rl_user_data_wrapper.setVisibility(View.VISIBLE);
                if (groupData == null || groupData.groupId == null) {
                    String firstName = target_user_name;
                    if (firstName == null) {
                        firstName = "";
                    }
                    if (firstName.contains(" ")) {
                        firstName = firstName.substring(0, firstName.indexOf(" "));
                    }

                    String blockOrUnblock = "";
                    if (blockStatus.equals(BLOCK_STATUS_I_BLOCK)) {
                        blockOrUnblock = getString(R.string.unblock);
                    } else {
                        blockOrUnblock = getString(R.string.block);
                    }
                    menu.getItem(3).setTitle(blockOrUnblock + " " + firstName);

                    menu.removeItem(R.id.group_info);
                } else {
                    menu.removeItem(R.id.block);
                }

            } else if (menuType == MENU_HIGHLIGHTED) {
                rl_user_data_wrapper.setVisibility(View.GONE);
                getMenuInflater().inflate(R.menu.chat_activity_menu_highlighted, menu);

                if (!shouldShowInfoIcon()) {
                    menu.removeItem(R.id.info);
                }
            }

        }

        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        return true;
    }

    /*private int getHighlightedMessageCount() {
        int count = 0;
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).isHighLighted) {
                count++;
            }
        }
        return count;
    }
*/
    private boolean shouldShowInfoIcon() {
        int count = 0;
        int index = 0;
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).isHighLighted) {
                count++;
                index = i;
            }
        }
        if (count == 1 && messageList.get(index).isMe) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent editArticleIntent;

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.search:
                revealSearchAnim();
                break;
            case R.id.delete:
                openConfirmationDialog(AppKeys.DELETE);
                break;

            case R.id.block:
                if (blockStatus.equals(BLOCK_STATUS_I_BLOCK)) {
                    Intent blockIntent = new Intent(ChatActivity.this, BlockingListActivity.class);
                    startActivityForResult(blockIntent, BLOCKING_INTENT_CODE);
                } else {
                    openConfirmationDialog(AppKeys.BLOCK);
                }
                break;

            case R.id.info:
                openSeenInfoDialog(getHighlightedMessagePosition());
                break;

            case R.id.group_info:
                if (groupId != null && !groupId.isEmpty()) {
                    Intent groupIntent = new Intent(ChatActivity.this, GroupInfoActivity.class);
                    groupIntent.putExtra("group_id", groupId);
                    startActivity(groupIntent);
                }
                break;

            case R.id.copy:
                String text = "";
                for (int i = 0; i < messageList.size(); i++) {
                    if (messageList.get(i).isHighLighted) {
                        text = text + messageList.get(i).userFullname + "\n<BR>" + messageList.get(i).message + "\n<BR>" + messageList.get(i).time + "\n<BR>\n<BR>";
                    }
                }

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newHtmlText("message", Html.fromHtml(text), text);
                clipboard.setPrimaryClip(clip);
                commonFunctions.setToastMessage(getApplicationContext(), getString(R.string.copied_to_clipboard)
                        , Toast.LENGTH_LONG, AppKeys.TOAST_USER);

                break;

            case R.id.forward:
                Intent searchConvIntent = new Intent(ChatActivity.this, SearchConversationsActivity.class);
                searchConvIntent.putExtra(AppKeys.SELECTION_ENABLED, true);
                List<String> messageIdList = new ArrayList<>();
                for (int i = 0; i < messageList.size(); i++) {
                    if (messageList.get(i).isHighLighted
                            && messageList.get(i).messageType == AppKeys.MESSAGE_TYPE_USER
                            && messageList.get(i).sentState != AppKeys.MESSAGE_STATE_SENT) {
                        commonFunctions.setToastMessage(getApplicationContext(),
                                getString(R.string.cannot_forward_unsent_messages), Toast.LENGTH_LONG,
                                AppKeys.TOAST_USER);
                    }
                    if (messageList.get(i).isHighLighted) {
                        messageIdList.add(messageList.get(i).messageId);
                    }
                }
                Type type = new TypeToken<List<String>>() {
                }.getType();
                String dataStr = new Gson().toJson(messageIdList, type);
                searchConvIntent.putExtra(AppKeys.MESSAGE_IDS, dataStr);
                startActivityForResult(searchConvIntent, FORWARD_MESSAGE_CODE);
                break;
        }
        return true;
    }

    private int getHighlightedMessagePosition() {
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).isHighLighted) {
                return i;
            }
        }
        return 0;
    }


    private void openSeenInfoDialog(int position) {
        TextView tv_dialog_description;
        TextView tv_done;
        final Dialog messageDialog;
        messageDialog = new MyCustomThemeDialog(ChatActivity.this, R.style.NoTitleDialogTheme);
        messageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        messageDialog.setContentView(R.layout.seen_info_dialog);
        messageDialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        tv_done = (TextView) messageDialog.findViewById(R.id.tv_done);

        List<PojoGroupMember> seenUserList = new ArrayList<>();

        for (int i = 0; i < messageList.get(position).seendetail.get(0).seenusers.size(); i++) {
            for (int j = 0; j < recipientList.size(); j++) {
                String idToCompare = messageList.get(position).seendetail.get(0).seenusers.get(i).userid;
                if (!idToCompare.equals(user_id) && idToCompare.equals(recipientList.get(j).userId)) {
                    seenUserList.add(recipientList.get(j));
                    seenUserList.get(seenUserList.size() - 1).seenTime
                            = messageList.get(position).seendetail.get(0).seenusers.get(i).seentime;
                }
            }
        }

        RecyclerView rv_seen_user_list = (RecyclerView) messageDialog.findViewById(R.id.rv_seen_user_list);
        rv_seen_user_list.setLayoutManager(new LinearLayoutManager(ChatActivity.this,
                RecyclerView.VERTICAL, false));
        rv_seen_user_list.setAdapter(new SeenUserListAdapter(seenUserList, ChatActivity.this));

        tv_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageDialog.dismiss();
            }
        });
        messageDialog.show();
    }


    private void openConfirmationDialog(String type) {
        final Dialog confirmationDialog;
        TextView tv_yes, tv_no, tv_dialog_description;
        confirmationDialog = new MyCustomThemeDialog(ChatActivity.this, R.style.NoTitleDialogTheme);
        confirmationDialog.setContentView(R.layout.yes_no_dialog);
        tv_dialog_description = (TextView) confirmationDialog.findViewById(R.id.tv_dialog_description);
        tv_yes = (TextView) confirmationDialog.findViewById(R.id.tv_yes);
        tv_no = (TextView) confirmationDialog.findViewById(R.id.tv_no);

        if (type.equals(AppKeys.DELETE)) {
            tv_dialog_description.setText(getString(R.string.confirm_coversation_delete));
            tv_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hitDeleteConversationApi(user_id, cid);
                    Intent newMsg = new Intent(Config.CONVERSATION_DELETED);
                    newMsg.putExtra("cid", cid);
                    LocalBroadcastManager.getInstance(ChatActivity.this).sendBroadcast(newMsg);
                    if (!ChatActivity.this.isFinishing()) {
                        confirmationDialog.dismiss();
                        finish();
                    }
                }
            });
        }

        if (type.equals(AppKeys.BLOCK)) {
            tv_dialog_description.setText(getString(R.string.block_chat_user_confirmation));
            tv_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hitUpdateUserConnectionApi(user_id, target_user_id, "block");
                    confirmationDialog.dismiss();
                    finish();
                }
            });
        }

        tv_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationDialog.dismiss();
            }
        });


        confirmationDialog.show();

    }

    private void hitUpdateUserConnectionApi(String user_id, String id, String connect_type) {
        Call<PojoUserConnectResponse> call = apiService.changeUserConnectionApi(user_id, id, connect_type);

        call.enqueue(new Callback<PojoUserConnectResponse>() {
            @Override
            public void onResponse(Call<PojoUserConnectResponse> call, Response<PojoUserConnectResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
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

    private void hitDeleteConversationApi(String user_id, final String cid) {
        Call<PojoNoDataResponse> call = apiService.deleteConversation(user_id, cid);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;
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


    public boolean getCanUserSendMessage(PojoGroupData groupData) {
        for (int i = 0; i < groupData.members.size(); i++) {
            if (groupData.members.get(i).groupStatus.equals("1")
                    && groupData.members.get(i).userId.equals(user_id)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onAddChatMemberMethodCallback(int position, PojoUserData item, String type) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        isActivityVisible = false;
        onlineApiHandler.removeCallbacks(onlineApiRunnable);
        preferenceUtils.saveTempConvMessage(cid, et_new_message.getText().toString().trim());
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
        onlineApiHandler.removeCallbacks(onlineApiRunnable);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        //    unsentMsgHandler.removeCallbacks(unsentMsgRunnable);
        onlineApiHandler.removeCallbacks(onlineApiRunnable);

    }

    @Override
    public void groupChatClickMethod(int position, PojoChatMessage pojoChatMessage, String
            type) {
        switch (type) {
            case AppKeys.CHAT_MESSAGE_LONG_CLICK:
                if (!searchOpened && pojoChatMessage.sentState == AppKeys.MESSAGE_STATE_SENT) {
                    messageList.get(position).isHighLighted = true;
                    chatMessageAdapter.setSelectionEnabled(true);
                    chatMessageAdapter.notifyDataSetChanged();
                    menuType = MENU_HIGHLIGHTED;
                    invalidateOptionsMenu();
                }

                break;

            case AppKeys.CHAT_MESSAGE_CLICK:
                if (!searchOpened && chatMessageAdapter.getSelectionEnabled() && pojoChatMessage.sentState == AppKeys.MESSAGE_STATE_SENT) {
                    messageList.get(position).isHighLighted = !messageList.get(position).isHighLighted;
                    chatMessageAdapter.notifyDataSetChanged();
                    invalidateOptionsMenu();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (chatMessageAdapter.getSelectionEnabled()) {
            disableSelections();
        } else if (searchOpened) {
            hideSearchAnim();
        } else {
            super.onBackPressed();
        }
    }

    private void disableSelections() {
        chatMessageAdapter.setSelectionEnabled(false);
        unHighLightAllMessages();
        chatMessageAdapter.notifyDataSetChanged();
        menuType = MENU_NORMAL;
        invalidateOptionsMenu();
    }

    private void unHighLightAllMessages() {
        for (int i = 0; i < messageList.size(); i++) {
            messageList.get(i).isHighLighted = false;
        }
    }

    public List<Integer> getQueryContainingMesssages(String query) {

        currentHighlightedResultPos = -1;
        searchResultIndexList = new ArrayList<>();
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).messageType == AppKeys.MESSAGE_TYPE_USER) {
                String messageLower = messageList.get(i).message.toLowerCase();
                String queryLower = query.toLowerCase();
                if (messageLower.contains(queryLower)) {
                    searchResultIndexList.add(i);
                }
            }
        }
        return searchResultIndexList;
    }


}
