package com.avadna.luneblaze.activities.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.adapters.chat.SearchConversationListAdapter;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.activities.AppBaseActivity;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.ConnectionErrorDialog;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetFriendsListResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoConversationListItem;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateMultiMessageResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateNewConversationResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoGetAllMessageListResponse;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.google.android.gms.common.util.IOUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchConversationsActivity extends AppBaseActivity
        implements SearchConversationListAdapter.SearchConversationAdapterCallback {

    RecyclerView rv_result_list;
    SearchConversationListAdapter searchConversationListAdapter;
    LinearLayoutManager conversationListLayoutManager;
    boolean conversationListEnded = false;

    EditText et_search_bar;
    ImageButton ib_close_gray;
    ImageButton ib_back_gray;

    FloatingActionButton fab_forward;


    ApiInterface apiService;
    List<PojoConversationListItem> conversationList;
    List<PojoUserData> userList;
    int offset = 0;
    PojoGetAllMessageListResponse pojoGetAllMessageListResponse;
    PreferenceUtils preferenceUtils;
    String user_id;
    CommonFunctions commonFunctions;
    View.OnClickListener onClickListener;
    boolean apiCallInProgress = false;
    private boolean selectionEnabled = false;

    List<String> messageIdList = new ArrayList<>();

    String sharedText = "";
    String sharedImage = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_conversations);
        preferenceUtils = new PreferenceUtils(this);
        commonFunctions = new CommonFunctions(this);
        user_id = preferenceUtils.get_user_id();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);

        selectionEnabled = getIntent().getBooleanExtra(AppKeys.SELECTION_ENABLED, false);

        Type type = new TypeToken<List<String>>() {
        }.getType();
        String dataStr = getIntent().getStringExtra(AppKeys.MESSAGE_IDS);
        messageIdList = new Gson().fromJson(dataStr, type);

        initViews();
        initClickListener();
        setClickListener();

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
                handleSendImage(intent); // Handle single image being sent
            }
        } else {
            // Handle other intents, such as being started from the home screen
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            this.sharedText = sharedText;
        }
    }

    void handleSendImage(Intent intent) {
        Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (imageUri != null) {
            sharedImage = getFilePathFromURI(SearchConversationsActivity.this, imageUri);
            if (intent.getStringExtra(Intent.EXTRA_TEXT) != null) {
                this.sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
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

    private void initViews() {
        et_search_bar = (EditText) findViewById(R.id.et_search_bar);
        ib_close_gray = (ImageButton) findViewById(R.id.ib_close_gray);
        ib_back_gray = (ImageButton) findViewById(R.id.ib_back_gray);
        fab_forward = (FloatingActionButton) findViewById(R.id.fab_forward);
        fab_forward.hide();

        et_search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                userList.clear();
                conversationList.clear();
                searchConversationListAdapter.notifyDataSetChanged();
                hitGetAllMsgListApi(preferenceUtils.get_user_id(), getLastMessageId(), charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        initAllMessagesList();
    }

    private void initClickListener() {
        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.ib_close_gray:
                        et_search_bar.getText().clear();
                        break;

                    case R.id.ib_back_gray:
                        finish();
                        break;

                    case R.id.fab_forward:
                        List<String> conversationIdList = new ArrayList<>();
                        List<String> recipientList = new ArrayList<>();

                        for (int i = 0; i < conversationList.size(); i++) {
                            if (conversationList.get(i).checked) {
                                conversationIdList.add(conversationList.get(i).conversationId);
                            }
                        }

                        for (int i = 0; i < userList.size(); i++) {
                            if (userList.get(i).checked) {
                                recipientList.add(userList.get(i).userId);
                            }
                        }

                        hitForwardMessageApi(user_id, messageIdList, conversationIdList, recipientList);
                        break;
                }
            }
        };
    }

    private void setClickListener() {
        ib_back_gray.setOnClickListener(onClickListener);
        ib_close_gray.setOnClickListener(onClickListener);
        fab_forward.setOnClickListener(onClickListener);
    }


    public void initAllMessagesList() {
        conversationList = new ArrayList<>();
        userList = new ArrayList<>();
        rv_result_list = (RecyclerView) findViewById(R.id.rv_result_list);
        searchConversationListAdapter = new SearchConversationListAdapter(this, conversationList, userList, selectionEnabled);
        conversationListLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rv_result_list.setLayoutManager(conversationListLayoutManager);
        rv_result_list.setAdapter(searchConversationListAdapter);
        rv_result_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                // Log.d("Scroll info", "dx = " + dy);
                if (dy > 5) {
                    if (conversationListLayoutManager
                            .findLastVisibleItemPosition() > getListTotalSize() - 3) {
                        if (conversationListEnded) {
                            hitGetFriendListApi(user_id, et_search_bar.getText().toString().trim(), ++offset);
                        } else {
                            hitGetAllMsgListApi(preferenceUtils.get_user_id(), getLastMessageId(), "");
                        }
                    }

                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        conversationList.addAll(preferenceUtils.getAllMessageList());
        searchConversationListAdapter.notifyDataSetChanged();
        // hitGetAllMsgListApi(preferenceUtils.get_user_id(), getLastMessageId(), "");
    }


    private void hitForwardMessageApi(final String user_id, List<String> message_ids,
                                      List<String> conversation_ids, List<String> recipient_ids) {
        commonFunctions.openProgressDialog(getString(R.string.forwarding_messages));

        HashMap<String, String> messageIdMap = new HashMap<>();
        for (int i = 0; i < message_ids.size(); i++) {
            messageIdMap.put("message_id[" + i + "]", message_ids.get(i));
        }

        HashMap<String, String> conversationIdMap = new HashMap<>();
        for (int i = 0; i < conversation_ids.size(); i++) {
            conversationIdMap.put("conversation_id[" + i + "]", conversation_ids.get(i));
        }

        HashMap<String, String> recipientMap = new HashMap<>();
        for (int i = 0; i < recipient_ids.size(); i++) {
            recipientMap.put("recipients[" + i + "]", recipient_ids.get(i));
        }

        Call<PojoCreateMultiMessageResponse> call = apiService.forwardChatMessage(user_id, messageIdMap, conversationIdMap, recipientMap);
        call.enqueue(new Callback<PojoCreateMultiMessageResponse>() {
            @Override
            public void onResponse(Call<PojoCreateMultiMessageResponse> call, Response<PojoCreateMultiMessageResponse> response) {
                if (response != null && response.body() != null) {
                    String message = response.body().message;
                    commonFunctions.setToastMessage(SearchConversationsActivity.this, message,
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    if (et_search_bar.requestFocus()) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(et_search_bar.getWindowToken(), 0);
                    }
                    setResult(RESULT_OK);

                    Intent pushNotification = new Intent(Config.MESSAGE_FORWARDED);
                    LocalBroadcastManager.getInstance(SearchConversationsActivity.this).sendBroadcast(pushNotification);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<PojoCreateMultiMessageResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(SearchConversationsActivity.this, t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                commonFunctions.closeProgressDialog();
            }
        });

    }

    private void hitGetAllMsgListApi(final String user_id, final String last_message_id, final String query) {
        Call<PojoGetAllMessageListResponse> call = apiService.getUserAllMsgList(user_id,
                last_message_id, query);
        if (last_message_id.isEmpty()) {
            conversationList.clear();
            userList.clear();
            searchConversationListAdapter.setShowNoMoreResults(false);
            conversationListEnded = false;
        }
        if (!apiCallInProgress) {
            apiCallInProgress = true;
            searchConversationListAdapter.setShowProgressBar(true);
            call.enqueue(new Callback<PojoGetAllMessageListResponse>() {
                @Override
                public void onResponse(Call<PojoGetAllMessageListResponse> call, Response<PojoGetAllMessageListResponse> response) {
                    if (response.body() != null) {
                        pojoGetAllMessageListResponse = response.body();

                        String message = response.body().message;
                        for (int i = 0; i < pojoGetAllMessageListResponse.data.messages.size(); i++) {
                            pojoGetAllMessageListResponse.data.messages.get(i).isOnline = "0";
                        }
                        if (response.body().status == 1) {
                            if (last_message_id.isEmpty()) {
                                conversationList.clear();
                            }
                            if (pojoGetAllMessageListResponse.data.messages.size() < 15) {
                                conversationListEnded = true;
                                offset = 0;
                                apiCallInProgress = false;
                                hitGetFriendListApi(user_id, et_search_bar.getText().toString().trim(), offset);
                            }
                                searchConversationListAdapter.setShowProgressBar(false);
                                for (int i = 0; i < pojoGetAllMessageListResponse.data.messages.size(); i++) {
                                    if (!pojoGetAllMessageListResponse.data.messages.get(i).conversationId.isEmpty()) {
                                        conversationList.add(pojoGetAllMessageListResponse.data.messages.get(i));
                                    }
                                }

                            //   getApplicationContext().startService(new Intent(getContext(), MessagingService.class));
                            searchConversationListAdapter.notifyDataSetChanged();
                        } else {
                            commonFunctions.setToastMessage(SearchConversationsActivity.this,
                                    "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
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
                        openNoConnectionDialog(getString(R.string.conversations), last_message_id, query);
                    }
                }
            });
        }
    }

    private void openNoConnectionDialog(String title, String last_message_id, String query) {
        try {
            connectionErrorDialog =
                    new ConnectionErrorDialog(SearchConversationsActivity.this,
                            R.style.AppTheme, title);
            connectionErrorDialog.setConnectionErrorDialogListener(new ConnectionErrorDialog.ConnectionErrorDialogListener() {
                @Override
                public void onRetry() {
                    hitGetAllMsgListApi(preferenceUtils.get_user_id(), last_message_id, query);
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


    private void hitGetFriendListApi(final String user_id, final String query, int localOffset) {
        Call<PojoGetFriendsListResponse> call = apiService.getFriendListApi(user_id, user_id, query,
                "0", "1", String.valueOf(localOffset));

        if (localOffset == 0) {
            userList.clear();
            searchConversationListAdapter.setShowNoMoreResults(false);
            searchConversationListAdapter.notifyDataSetChanged();
        }
        if (!apiCallInProgress) {
            apiCallInProgress = true;
            if (localOffset != 0) {
                searchConversationListAdapter.setShowProgressBar(true);
            }
            call.enqueue(new Callback<PojoGetFriendsListResponse>() {
                @Override
                public void onResponse(Call<PojoGetFriendsListResponse> call, Response<PojoGetFriendsListResponse> response) {
                    if (response.body() != null && response.body().data != null) {
                        String message = response.body().message;
                        if (query.equals(et_search_bar.getText().toString().trim())) {
                            for (int i = 0; i < response.body().data.friends.size(); i++) {
                                response.body().data.friends.get(i).selected = false;
                            }
                            if (response.body().data.friends.isEmpty()) {
                                searchConversationListAdapter.setShowNoMoreResults(true);
                            }
                            userList.addAll(removeAlreadyPresentUsers(response.body().data.friends));
                            searchConversationListAdapter.notifyDataSetChanged();
                        } else {
                            apiCallInProgress = false;
                            offset = 0;
                            hitGetFriendListApi(user_id, et_search_bar.getText().toString().trim(), offset);
                        }

                    }
                    searchConversationListAdapter.setShowProgressBar(false);
                    apiCallInProgress = false;
                }

                @Override
                public void onFailure(Call<PojoGetFriendsListResponse> call, Throwable t) {
                    // Log error here since request failed
                    commonFunctions.setToastMessage(SearchConversationsActivity.this, t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                    apiCallInProgress = false;
                }
            });
        }

    }

    private List<PojoUserData> removeAlreadyPresentUsers(List<PojoUserData> friends) {
        List<PojoUserData> temp = new ArrayList<>();
        for (int i = 0; i < friends.size(); i++) {
            boolean isPresent = false;
            for (int j = 0; j < conversationList.size(); j++) {
                if (conversationList.get(j).groupData.groupId == null) {
                    if (!conversationList.get(j).recipients.isEmpty()
                            && conversationList.get(j).recipients.get(0).userId.equals(friends.get(i).userId)) {
                        isPresent = true;
                    }
                }
            }
            if (!isPresent) {
                temp.add(friends.get(i));
            }
        }

        return temp;
    }


    public String getLastMessageId() {
        if (conversationList.isEmpty()) {
            return "";
        } else {
            return conversationList.get(conversationList.size() - 1).lastMessageId;
        }
    }

    public int getListTotalSize() {
        return conversationList.size() + userList.size();
    }


    @Override
    public void onSearchConversationItemClickMethod(int position, String type) {
        if (selectionEnabled) {
            if (type.equals("conv")) {
                conversationList.get(position).checked = !conversationList.get(position).checked;
            } else if (type.equals("user")) {
                userList.get(position).checked = !userList.get(position).checked;
            }
            searchConversationListAdapter.notifyDataSetChanged();
            checkIfAnySelected();
        } else {
            if (type.equals("conv")) {
                Intent chatIntent = new Intent(SearchConversationsActivity.this, ChatActivity.class);
                chatIntent.putExtra("cid", conversationList.get(position).conversationId);
                if (!conversationList.get(position).ids.contains("_")) {
                    chatIntent.putExtra(AppKeys.TARGET_USER_ID, conversationList.get(position).ids);
                }
                chatIntent.putExtra(AppKeys.SHARED_TEXT, sharedText);
                chatIntent.putExtra(AppKeys.SHARED_IMAGE, sharedImage);
                startActivity(chatIntent);
                finish();
            } else if (type.equals("user")) {
                hitCreateIndividualConversationApi(user_id, userList.get(position).userId);
            }
        }
    }

    private void hitCreateIndividualConversationApi(String user_id, String recipient) {
        Call<PojoCreateNewConversationResponse> call = apiService.createConversation(user_id, recipient);
        call.enqueue(new Callback<PojoCreateNewConversationResponse>() {
            @Override
            public void onResponse(Call<PojoCreateNewConversationResponse> call,
                                   Response<PojoCreateNewConversationResponse> response) {
                String message = response.body().message;
                if (response.body().data != null) {
                    Intent chatIntent = new Intent(SearchConversationsActivity.this,
                            ChatActivity.class);
                    chatIntent.putExtra("cid", response.body().data);
                    chatIntent.putExtra(AppKeys.SHARED_TEXT, sharedText);
                    chatIntent.putExtra(AppKeys.SHARED_IMAGE, sharedImage);
                    startActivity(chatIntent);
                    finish();
                } else {
                    commonFunctions.setToastMessage(SearchConversationsActivity.this, "" + message, Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }
            }

            @Override
            public void onFailure(Call<PojoCreateNewConversationResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(SearchConversationsActivity.this, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

    private void checkIfAnySelected() {
        boolean selected = false;
        for (int i = 0; i < conversationList.size(); i++) {
            if (conversationList.get(i).checked) {
                selected = true;
                break;
            }
        }
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).checked) {
                selected = true;
                break;
            }
        }

        if (selected) {
            fab_forward.show();
        } else {
            fab_forward.hide();
        }
    }


}
