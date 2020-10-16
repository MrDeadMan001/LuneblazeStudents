package com.avadna.luneblaze.fragments.home;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.LoginActivity;
import com.avadna.luneblaze.activities.chat.ChatActivity;
import com.avadna.luneblaze.activities.MainActivity;
import com.avadna.luneblaze.activities.chat.NewConversationActivity;
import com.avadna.luneblaze.adapters.chat.AllMessageListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetUserOnlineStatusResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoConversationListItem;
import com.avadna.luneblaze.pojo.pojoChat.PojoGetAllMessageListResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoGroupData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AllMessageListFragment extends Fragment implements AllMessageListAdapter.MsgListAdapterCallback {
    private RelativeLayout rl_parent;

    private LinearLayout ll_no_connection_wrapper;
    private TextView tv_retry;

    private RecyclerView rv_msg_list;
    private AllMessageListAdapter allMessageListAdapter;
    private LinearLayoutManager conversationListLayoutManager;
    private ApiInterface apiService;
    private TextView tv_no_conversation_present;
    private List<PojoConversationListItem> conversationList;
    private PojoGetAllMessageListResponse pojoGetAllMessageListResponse;
    private PreferenceUtils preferenceUtils;
    private String user_id;
    private CommonFunctions commonFunctions;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    //  Animation animFadeIn, animFadeOut, animScaleUp, animScaleDown;
    private boolean isAnimRunning = false;
    // private  FloatingActionButton fb_create;
    private View.OnClickListener onClickListener;

    private SwipeRefreshLayout srl_refresh;
    private boolean apiCallInProgress = false;
    private HashMap<String, String> isUserOnlineMap = new HashMap<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // register GCM registration complete receiver
        preferenceUtils = new PreferenceUtils(getActivity());
        commonFunctions = new CommonFunctions(getActivity());
        user_id = preferenceUtils.get_user_id();

        if(!user_id.equals(AppKeys.GUEST_USER_ID)){
            initBroadCastReceiver();
            registerBroadCastReceivers();
        }

        // clear the notification area when the app is opened
    }

    private void registerBroadCastReceivers() {
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.NEW_MESSAGE_FCM));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.GROUP_UPDATE_BROADCAST));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.CONVERSATION_DELETED));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.MESSAGE_SEEN));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.MESSAGE_SENT));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.MESSAGE_FORWARDED));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REFRESH_CONV_LIST_FROM_PREFS));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View baseLayout = inflater.inflate(R.layout.fragment_all_message_list, container, false);
        if (user_id.equals(AppKeys.GUEST_USER_ID)) {
            showGuestLogin(baseLayout);
        }
        else {
            initApis();
            initViews(baseLayout);
            //  initAnimations();
            initClickListener();
            setClickListener();
            setTextWatcher();
        }
        return baseLayout;
    }

    private void showGuestLogin(View baseLayout) {
        RelativeLayout rl_guest_login_wrapper = (RelativeLayout) baseLayout.findViewById(R.id.rl_guest_login_wrapper);
        rl_guest_login_wrapper.setVisibility(View.VISIBLE);
        TextView tv_login_button = (TextView) baseLayout.findViewById(R.id.tv_login_button);
        tv_login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivityIntent = new Intent(getActivity(), LoginActivity.class);
                startActivity(loginActivityIntent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //  conversationList = preferenceUtils.getAllMessageList();
        if(allMessageListAdapter!=null){
            allMessageListAdapter.notifyDataSetChanged();
        }
        NotificationManager notificationManager = (NotificationManager) getActivity()
                .getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            notificationManager.cancel(AppKeys.MESSAGE_SUMMARY_ID);
        }

    }

    private void initBroadCastReceiver() {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                } else if (intent.getAction().equals(Config.MESSAGE_SEEN)) {
                    allMessageListAdapter.updateSeenStatus();
                } else if (intent.getAction().equals(Config.MESSAGE_ADDED)) {
                    allMessageListAdapter.notifyDataSetChanged();
                } else if (intent.getAction().equals(Config.MESSAGE_SENT)) {
                    allMessageListAdapter.notifyDataSetChanged();
                } else if (intent.getAction().equals(Config.MESSAGE_FORWARDED)) {
                    hitGetAllMsgListApi(preferenceUtils.get_user_id(), "", "", false);
                } else if (intent.getAction().equals(Config.GROUP_UPDATE_BROADCAST)) {
                    // new push notification is received
                 /*   String message = intent.getStringExtra("message");
                    commonFunctions.setToastMessage(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);*/
                    hitGetAllMsgListApi(preferenceUtils.get_user_id(), "", "", false);

                } else if (intent.getAction().equals(Config.REFRESH_CONV_LIST_FROM_PREFS)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    conversationList.clear();
                    conversationList.addAll(preferenceUtils.getAllMessageList());
                    allMessageListAdapter.notifyDataSetChanged();

                } else if (intent.getAction().equals(Config.NEW_MESSAGE_FCM)) {
                    // new push notification is received
                 /*   String message = intent.getStringExtra("message");
                    commonFunctions.setToastMessage(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);*/
                    allMessageListAdapter.notifyDataSetChanged();
                    hitGetAllMsgListApi(preferenceUtils.get_user_id(), "", "", false);
                } else if (intent.getAction().equals(Config.CONVERSATION_DELETED)) {
                    // new push notification is received
                 /*   String message = intent.getStringExtra("message");
                    commonFunctions.setToastMessage(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);*/
                    String cid = intent.getStringExtra("cid");
                    for (int i = 0; i < conversationList.size(); i++) {
                        if (cid.equals(conversationList.get(i).conversationId)) {
                            conversationList.remove(i);
                            allMessageListAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                    hitGetAllMsgListApi(preferenceUtils.get_user_id(), "", "", false);
                }
            }
        };

    }


    private void initApis() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

    }

    private void initViews(View baseLayout) {
        //  fb_create = (FloatingActionButton) baseLayout.findViewById(R.id.fb_create);
        ll_no_connection_wrapper = (LinearLayout) baseLayout.findViewById(R.id.ll_no_connection_wrapper);
        ll_no_connection_wrapper.setVisibility(View.GONE);
        tv_retry = (TextView) baseLayout.findViewById(R.id.tv_retry);
        tv_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hitGetAllMsgListApi(preferenceUtils.get_user_id(), "", "", true);
            }
        });

        rl_parent = (RelativeLayout) baseLayout.findViewById(R.id.rl_parent);
        tv_no_conversation_present = (TextView) baseLayout.findViewById(R.id.tv_no_conversation_present);
        tv_no_conversation_present.setVisibility(View.GONE);
        initAllMessagesList(baseLayout);
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fb_create:
                        Intent newConversationIntent = new Intent(getActivity(), NewConversationActivity.class);
                        startActivity(newConversationIntent);
                     /*   hitGetFriendListApi(preferenceUtils.get_user_id());
                        openFriendListDialog();*/
                        break;
                }
            }
        };
    }


    private void setClickListener() {
        //fb_create.setOnClickListener(onClickListener);
    }

    private void setTextWatcher() {

    }

    private void initAllMessagesList(View baseLayout) {
        srl_refresh = (SwipeRefreshLayout) baseLayout.findViewById(R.id.srl_refresh);

        srl_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                hitGetAllMsgListApi(preferenceUtils.get_user_id(), "", "", false);
            }
        });

        conversationList = preferenceUtils.getAllMessageList();

        rv_msg_list = (RecyclerView) baseLayout.findViewById(R.id.rv_msg_list);
        allMessageListAdapter = new AllMessageListAdapter(AllMessageListFragment.this, conversationList);
        conversationListLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false);
        rv_msg_list.setLayoutManager(conversationListLayoutManager);
        rv_msg_list.setAdapter(allMessageListAdapter);
        rv_msg_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // Log.d("Scroll info", "dx = " + dy);
                if (dy > 5) {
                    if (!apiCallInProgress) {
                        if (conversationListLayoutManager
                                .findLastVisibleItemPosition() > getListTotalSize() - 3) {
                            hitGetAllMsgListApi(preferenceUtils.get_user_id(), getLastMessageId(), "", true);
                        }
                    }
                   /* if (!isAnimRunning && fb_create.getVisibility() == View.VISIBLE) {
                        fb_create.startAnimation(animScaleDown);
                    }*/

                    ((MainActivity) getActivity()).hideBars();
                } else if (dy < -5) {
                  /*  if (!isAnimRunning && fb_create.getVisibility() == View.GONE) {
                        if (rl_parent.findViewById(R.id.fb_create) == null) {
                            rl_parent.addView(fb_create);
                            fb_create.setVisibility(View.VISIBLE);
                            fb_create.startAnimation(animScaleUp);
                        }
                    }*/
                    ((MainActivity) getActivity()).showBars();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        hitGetAllMsgListApi(preferenceUtils.get_user_id(), "", "", true);
    }

    public int getListTotalSize() {
        return conversationList.size();
    }

    private void hitGetAllMsgListApi(final String user_id, final String last_message_id, final String query,
                                     boolean showProgress) {
        Call<PojoGetAllMessageListResponse> call = apiService.getUserAllMsgList(user_id,
                last_message_id, query);
        ll_no_connection_wrapper.setVisibility(View.GONE);

        if (!apiCallInProgress) {
            apiCallInProgress = true;
            allMessageListAdapter.setShowProgressBar(showProgress);
            call.enqueue(new Callback<PojoGetAllMessageListResponse>() {
                @Override
                public void onResponse(Call<PojoGetAllMessageListResponse> call, Response<PojoGetAllMessageListResponse> response) {
                    if (response.body() != null) {

                        srl_refresh.setRefreshing(false);
                        pojoGetAllMessageListResponse = response.body();

                        String message = response.body().message;

                        for (int i = 0; i < pojoGetAllMessageListResponse.data.messages.size(); i++) {
                            pojoGetAllMessageListResponse.data.messages.get(i).isOnline = "0";
                        }

                        if (response.body().status == 1) {
                            if (last_message_id == null || last_message_id.isEmpty()) {
                                conversationList.clear();
                                if (pojoGetAllMessageListResponse.data.messages.isEmpty()) {
                                    tv_no_conversation_present.setVisibility(View.VISIBLE);
                                }
                            } else {
                                if (pojoGetAllMessageListResponse.data.messages.isEmpty()) {
                                    allMessageListAdapter.setShowNoMoreResults(true);
                                }
                            }
                            for (int i = 0; i < pojoGetAllMessageListResponse.data.messages.size(); i++) {
                                if (!pojoGetAllMessageListResponse.data.messages.get(i).conversationId.isEmpty()) {
                                    conversationList.add(pojoGetAllMessageListResponse.data.messages.get(i));
                                }
                            }
                            for (int i = 0; i < conversationList.size(); i++) {
                                //if its a group then add all the members in hashmap
                                if (conversationList.get(i).groupData.groupId != null) {
                                    PojoGroupData groupData = conversationList.get(i).groupData;
                                    for (int j = 0; j < groupData.members.size(); j++) {
                                        if (!isUserOnlineMap.containsKey(groupData.members.get(j).userId)) {
                                            isUserOnlineMap.put(groupData.members.get(j).userId, "0");
                                        }
                                    }
                                }

                                //if its not group then add only the single user id of a conversation

                                if (!conversationList.get(i).ids.contains("_")
                                        && !conversationList.get(i).ids.isEmpty()
                                        && !isUserOnlineMap.containsKey(conversationList.get(i).ids)) {
                                    isUserOnlineMap.put(conversationList.get(i).ids, "0");
                                }
                            }
                            hitGetUserOnlineStatusApi(isUserOnlineMap);
                            preferenceUtils.saveAllMessageList(conversationList);
                            //   getApplicationContext().startService(new Intent(getContext(), MessagingService.class));
                            allMessageListAdapter.setShowProgressBar(false);
                            allMessageListAdapter.notifyDataSetChanged();
                        } else {
                            commonFunctions.setToastMessage(getContext(), "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                        }
                    }
                    apiCallInProgress = false;
                }

                @Override
                public void onFailure(Call<PojoGetAllMessageListResponse> call, Throwable t) {
                    // Log error here since request failed
                    //commonFunctions.setToastMessage(getContext(), t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                    apiCallInProgress = false;
                    if (conversationList.isEmpty()) {
                        allMessageListAdapter.setShowProgressBar(false);
                        ll_no_connection_wrapper.setVisibility(View.VISIBLE);
                        tv_no_conversation_present.setVisibility(View.GONE);
                    }
                    srl_refresh.setRefreshing(false);
                    allMessageListAdapter.setShowProgressBar(false);
                    allMessageListAdapter.notifyDataSetChanged();

                }
            });
        }
    }


    public String getLastMessageId() {
        if (conversationList.isEmpty()) {
            return "";
        } else {
            return conversationList.get(conversationList.size() - 1).lastMessageId;
        }
    }

    private void hitGetUserOnlineStatusApi(HashMap<String, String> hashmap) {

        HashMap<String, String> userIdMap = new HashMap<>();

        int i = 0;
        for (HashMap.Entry<String, String> entry : hashmap.entrySet()) {
            userIdMap.put("user_ids[" + i + "]", entry.getKey());
            i++;
        }

        Call<PojoGetUserOnlineStatusResponse> call = apiService.getUserOnlineStatus(userIdMap);
        call.enqueue(new Callback<PojoGetUserOnlineStatusResponse>() {
            @Override
            public void onResponse(Call<PojoGetUserOnlineStatusResponse> call,
                                   Response<PojoGetUserOnlineStatusResponse> response) {
                if (response.body() != null && response.body().data != null) {
                    String message = response.body().message;
                    for (int i = 0; i < response.body().data.size(); i++) {
                        isUserOnlineMap.put(response.body().data.get(i).userId,
                                response.body().data.get(i).isOnline);
                    }
                }

                updateOnlineStatusFromMap();
            }

            @Override
            public void onFailure(Call<PojoGetUserOnlineStatusResponse> call, Throwable t) {
                // Log error here since request failed
                if (getActivity() != null)
                    commonFunctions.setToastMessage(getActivity().getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void updateOnlineStatusFromMap() {
        for (int i = 0; i < conversationList.size(); i++) {
            if (conversationList.get(i).groupData.groupId != null) {
                PojoGroupData groupData = conversationList.get(i).groupData;
                for (int j = 0; j < groupData.members.size(); j++) {
                    if (isUserOnlineMap.get(groupData.members.get(j).userId).equals("1")) {
                        conversationList.get(i).isOnline = "1";
                        break;
                    }
                }
            }

            if (isUserOnlineMap.containsKey(conversationList.get(i).ids)) {
                if (isUserOnlineMap.get(conversationList.get(i).ids).equals("1")) {
                    conversationList.get(i).isOnline = "1";
                }
            }
        }
        allMessageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMsgListMethodCallback(int position) {
        Intent chatIntent = new Intent(getActivity(), ChatActivity.class);
    /*    String jsonString;
        Gson gson;
        gson = new Gson();
        jsonString = gson.toJson(conversationList.get(position));*/
        conversationList.get(position).unreadCount = 0;
        //   preferenceUtils.saveAllMessageList(conversationList);
        allMessageListAdapter.notifyDataSetChanged();
        chatIntent.putExtra("cid", conversationList.get(position).conversationId);
        if (!conversationList.get(position).ids.contains("_")) {
            chatIntent.putExtra(AppKeys.TARGET_USER_ID, conversationList.get(position).ids);
        }
        chatIntent.putExtra(AppKeys.CHAT_BLOCK_STATUS, conversationList.get(position).blocked);
        startActivity(chatIntent);
    }


    public boolean canUserSendMessage(PojoGroupData groupData) {
        if (groupData != null && groupData.groupId != null) {
            for (int i = 0; i < groupData.members.size(); i++) {
                if (groupData.members.get(i).groupStatus.equals("1")
                        && groupData.members.get(i).userId.equals(user_id)) {
                    return false;
                }
            }
        }

        return true;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}
