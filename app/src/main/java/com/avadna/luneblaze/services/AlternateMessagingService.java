package com.avadna.luneblaze.services;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.widget.Toast;

import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoChat.PojoChatMessage;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateMessageResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateMessageResponseData;
import com.avadna.luneblaze.pojo.pojoChat.PojoGetChatMessagesResponse;
import com.avadna.luneblaze.rest.ApiClientLongDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.utils.GlobalVariables;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AlternateMessagingService extends Service {

    ApiInterface apiService;

    HashMap<String, Integer> unsentConversationIdMap;
    int THREAD_COUNT = 3;

    //array of threads that send the messages on each conversation
    Thread threadArr[] = new Thread[THREAD_COUNT];
    //  boolean[] isThreadFree = new boolean[THREAD_COUNT];

    //array that keeps track of assigned cid to each thread
    String[] threadConversationId = new String[THREAD_COUNT];

    //array to keep track of message send api called for each thread
    boolean[] isApiCalled = new boolean[THREAD_COUNT];

    //this array is used to keep track of api when the message is timed out on its first try
    boolean[] isRetryApiCalled = new boolean[THREAD_COUNT];

    List<PojoChatMessage>[] conversationArray = new List[THREAD_COUNT];
    PreferenceUtils preferenceUtils;
    private boolean anyThreadFree;
    CommonFunctions commonFunctions;
    HashMap<String, String> imageMap;

    IBinder mBinder = new LocalBinder();

    //  boolean isMutexAvailable = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public AlternateMessagingService getMessagingServiceInstance() {
            return AlternateMessagingService.this;
        }
    }

    @Override
    public void onCreate() {
        apiService = ApiClientLongDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getApplicationContext());
        commonFunctions = new CommonFunctions(getApplicationContext());
        unsentConversationIdMap = preferenceUtils.getUnsentConversationsMap();
        initArrays();
        if (threadArr[0] == null) {
            initThreads();
            assignConversationToThread();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (threadArr[0] == null) {
            initThreads();
            assignConversationToThread();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void informNewUnsentConversation() {
        assignConversationToThread();
    }


    private void initArrays() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            //  isThreadFree[i] = true;
            //   threadConversationId[i] = "";
            isApiCalled[i] = false;
            conversationArray[i] = new ArrayList<>();
        }
    }


    public void assignConversationToThread() {
       /* String cid = "";
        for (int i = 0; i < THREAD_COUNT; i++) {
            cid = cid + " " + threadConversationId[i];
        }
        Log.d("messaging_service", "threads have convs" + cid);*/

        while (true) {
            if (GlobalVariables.isConversationMapMutexAvailable) {
                GlobalVariables.isConversationMapMutexAvailable = false;
                unsentConversationIdMap = preferenceUtils.getUnsentConversationsMap();
                String log = "";
                for (HashMap.Entry<String, Integer> entry : unsentConversationIdMap.entrySet()) {
                    log = log + " " + entry.getKey();
                }
                //  Log.d("messaging_service", "assign map has value" + log);
                for (HashMap.Entry<String, Integer> entry : unsentConversationIdMap.entrySet()) {
                    if (!isConversationAssigned(entry.getKey())) {
                        int freeThreadIndex = getFreeThreadIndex();
                        if (freeThreadIndex >= 0) {
                            threadConversationId[freeThreadIndex] = entry.getKey();
                        }
                    }
                }
                GlobalVariables.isConversationMapMutexAvailable = true;
                break;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void initThreads() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int finalI = i;
            threadArr[i] = new Thread() {
                int threadNum = finalI;

                public void run() {
                    //   Log.d("thread info", "thread " + finalI + " running");

                    while (true) {
                        try {
                            sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                        int a = 5;
                        if (commonFunctions.is_internet_connected()
                                && threadConversationId[threadNum] != null
                                && !threadConversationId[threadNum].isEmpty()
                                && isUnsentMessagePresentInConversation(threadConversationId[threadNum],
                                threadNum, "outer if condition thread " + threadNum)) {

                            int size = 0;

                            while (true) {
                                if (GlobalVariables.isIndividualChatMutexAvailable) {
                                    GlobalVariables.isIndividualChatMutexAvailable = false;
                                    size = conversationArray[threadNum].size();
                                    GlobalVariables.isIndividualChatMutexAvailable = true;
                                    break;
                                } else {
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            /* Log.d("messaging_service", "size= " + size);*/
                            Log.d("messaging_service", "size= " + size);

                            String messages = "";
                            messages = threadConversationId[threadNum] + "has messages ";

                            //for each msg in this conversation

                            for (int i = 0; i < size; i++) {
                                messages = messages + " " + conversationArray[threadNum].get(i).message;
                            }

                            Log.d("messaging_service", messages);


                            for (int messageNum = size - 1; messageNum >= 0 && threadConversationId[threadNum] != null; messageNum--) {
                                //if the message is unsent and api is not called yet

                                /*boolean net=commonFunctions.is_internet_connected();
                                Log.d("messaging_service","network connected "+net);*/
                                if (commonFunctions.is_internet_connected()
                                        && conversationArray[threadNum].get(messageNum).messageType
                                        == AppKeys.MESSAGE_TYPE_USER) {

                                    while (true) {
                                        if (isApiCalled[threadNum]) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        } else {

                                            if (conversationArray[threadNum].get(messageNum).sentState
                                                    != AppKeys.MESSAGE_STATE_SENT) {

                                             /*   String unsentmsgs = "";
                                                for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                                                    if (conversationArray[threadNum].get(j).sentState == AppKeys.MESSAGE_STATE_UNSENT) {
                                                        unsentmsgs = unsentmsgs + " " + conversationArray[threadNum].get(j).message;
                                                    }
                                                }
                                                Log.d("messaging_service", "unsent in cid "
                                                        + threadConversationId[threadNum] + " are " + unsentmsgs);*/

                                                if (conversationArray[threadNum].get(messageNum).image.isEmpty()) {
                                                    Log.d("messaging_service", "sending index" + messageNum);
                                                    hitReplyMessageApi(messageNum, threadNum,
                                                            threadConversationId[threadNum]);
                                                } else {
                                                    Log.d("messaging_service", "sending index" + messageNum);
                                                    hitUploadImageApi(conversationArray[threadNum], messageNum, threadNum,
                                                            threadConversationId[threadNum]);
                                                }
                                            }
                                            break;
                                        }
                                    }

                                    while (true) {
                                        if (isApiCalled[threadNum]) {
                                            try {
                                                Thread.sleep(100);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        } else {
                                            break;
                                        }
                                    }

                                   /* if (conversationArray[threadNum].get(messageNum).sentState
                                            == AppKeys.MESSAGE_STATE_FAILED
                                            && conversationArray[threadNum].get(messageNum).isRetried) {
                                      *//*  if (conversationArray[threadNum].get(messageNum).image.isEmpty()) {
                                            Log.d("messaging_service", "message failed reverting back");
                                            messageNum++;
                                        }*//*
                                        messageNum++;
                                    }*/
                                }
                                if (messageNum == 0 && threadConversationId[threadNum] != null &&
                                        !isUnsentMessagePresentInConversation(threadConversationId[threadNum],
                                                threadNum, "inner for loop")) {
                                    removeConvFromPref(threadConversationId[threadNum], threadNum);
                                }
                            }

                        }

                    }
                }
            };
            threadArr[i].start();
        }

    }

    private boolean isUnsentMessagePresentInConversation(String cid, int threadNum, String actor) {
        boolean isPresent = false;

        //Images are not supposed to be tried again if they fail once

        Log.d("messaging_service", "is unsent called by " + actor);


        if (threadConversationId[threadNum] == null || threadConversationId[threadNum].isEmpty()) {
            return false;
        }

        while (true) {
            if (GlobalVariables.isIndividualChatMutexAvailable) {
              /*  while (true) {
                    if (isApiCalled[threadNum]) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }*/
                GlobalVariables.isIndividualChatMutexAvailable = false;
                conversationArray[threadNum] = preferenceUtils.getConversation(cid);


                for (int i = 0; i < conversationArray[threadNum].size(); i++) {
                    if (conversationArray[threadNum].get(i).messageType == AppKeys.MESSAGE_TYPE_USER) {
                        //if message has not been tried to send even once then try to send
                        if (conversationArray[threadNum].get(i).sentState == AppKeys.MESSAGE_STATE_UNSENT) {
                            isPresent = true;
                            break;
                        }

                        //if message has been tried to be sent earlier
                        if (conversationArray[threadNum].get(i).sentState == AppKeys.MESSAGE_STATE_FAILED) {
                            //if its only text message try to send again
                            if (conversationArray[threadNum].get(i).image.isEmpty()) {
                                isPresent = true;
                                break;
                            }
                            //if it has image dont try to send again
                            else {
                                isPresent = false;
                            }
                        }
                    }
                }
                GlobalVariables.isIndividualChatMutexAvailable = true;
                break;
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }


        return isPresent;
    }

    public void removeConvFromPref(String cid, int threadNum) {


        while (true) {
            if (GlobalVariables.isConversationMapMutexAvailable) {
                GlobalVariables.isConversationMapMutexAvailable = false;

                 /*   int unsentCnt = 0;
                    for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                        if (conversationArray[threadNum].get(j).sentState == AppKeys.MESSAGE_STATE_UNSENT) {
                            unsentCnt++;
                        }
                    }*/
                //  Log.d("messaging_service", "conversation " + threadConversationId[threadNum]
                //         + "size is" + unsentCnt);

                if (threadConversationId[threadNum] != null) {
                    unsentConversationIdMap = preferenceUtils.getUnsentConversationsMap();
                    unsentConversationIdMap.remove(threadConversationId[threadNum]);
                    preferenceUtils.saveUnsentConversationsMap(unsentConversationIdMap);
                    Log.d("messaging_service", threadConversationId[threadNum] + " is removed by thread" + threadNum);
                    threadConversationId[threadNum] = null;
                }
                GlobalVariables.isConversationMapMutexAvailable = true;

                //  isThreadFree[threadNum] = true;
                assignConversationToThread();
                break;
            } else {
                try {
                    Thread.sleep(50);
                    //    Log.d("messaging_service", "mutex waiting in service");
                     /*   String log = "";
                        for (HashMap.Entry<String, Integer> entry : unsentConversationIdMap.entrySet()) {
                            log = log + " " + entry.getKey();
                        }*/

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void hitReplyMessageApi(final int messageNum, final int threadNum, final String cid) {

        isApiCalled[threadNum] = true;
        final PojoChatMessage newMsg;
        while (true) {
            if (GlobalVariables.isIndividualChatMutexAvailable) {
                GlobalVariables.isIndividualChatMutexAvailable = false;
                newMsg = conversationArray[threadNum].get(messageNum);
                GlobalVariables.isIndividualChatMutexAvailable = true;
                break;
            } else {
                try {
                    Thread.sleep(50);
                    Log.d("messaging_service", "api response waiting");

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        Call<PojoCreateMessageResponse> call = apiService.replyToMessage(newMsg.userId, newMsg.message,
                String.valueOf(newMsg.tempMsgId), "", cid);

        Log.d("messaging_service", "api called for " + newMsg.message + " of cid " + cid + "by" + threadNum);

        call.enqueue(new Callback<PojoCreateMessageResponse>() {
            @Override
            public void onResponse(Call<PojoCreateMessageResponse> call, Response<PojoCreateMessageResponse> response) {
                if (response.body() != null && response.body().data != null) {
                    Log.d("messaging_service", newMsg.message + " of cid " + cid + "sent by" + threadNum);

                    List<PojoChatMessage> savedList;
                    PojoCreateMessageResponseData item = response.body().data;

                    // preferenceUtils.saveConversation(cid,messageList);

                    while (true) {
                        if (GlobalVariables.isIndividualChatMutexAvailable) {
                            GlobalVariables.isIndividualChatMutexAvailable = false;
                            savedList = preferenceUtils.getConversation(cid);
                            for (int j = 0; j < savedList.size(); j++) {
                                if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                        && savedList.get(j).tempMsgId.equals(newMsg.tempMsgId)) {

                                    savedList.get(j).setMessageId(item.lastMessageId);
                                    savedList.get(j).setImage(item.image);
                                    //   savedList.get(j)wMsg.setTime(item.time);
                                    savedList.get(j).setUserName(item.name);
                                    savedList.get(j).setUserFullname(item.name);
                                    savedList.get(j).setUserGender("male");
                                    savedList.get(j).setUserPicture(item.picture);
                                    savedList.get(j).setCid(cid);
                                    savedList.get(j).setSentState(AppKeys.MESSAGE_STATE_SENT);
                                    savedList.get(j).setTempMsgId(item.tempMsgId);

                                    break;
                                }
                            }

                            preferenceUtils.saveConversation(cid, savedList);

                            for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                                if (conversationArray[threadNum].get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                        && conversationArray[threadNum].get(j).tempMsgId.equals(newMsg.tempMsgId)) {

                                    conversationArray[threadNum].get(j).setMessageId(item.lastMessageId);
                                    conversationArray[threadNum].get(j).setImage(item.image);
                                    //   conversationArray[threadNum]dList.get(j)wMsg.setTime(item.time);
                                    conversationArray[threadNum].get(j).setUserName(item.name);
                                    conversationArray[threadNum].get(j).setUserFullname(item.name);
                                    conversationArray[threadNum].get(j).setUserGender("male");
                                    conversationArray[threadNum].get(j).setUserPicture(item.picture);
                                    conversationArray[threadNum].get(j).setCid(cid);
                                    conversationArray[threadNum].get(j).setSentState(AppKeys.MESSAGE_STATE_SENT);
                                    conversationArray[threadNum].get(j).setTempMsgId(item.tempMsgId);

                                    Log.d("messaging_service", conversationArray[threadNum].get(j).message + " updated sent state");
                                    break;
                                }
                            }

                  /*          conversationArray[threadNum].get(messageNum);
                            conversationArray[threadNum].add(messageNum, newMsg);*/


                            GlobalVariables.isIndividualChatMutexAvailable = true;
                            break;
                        } else {
                            try {
                                Thread.sleep(50);
                                Log.d("messaging_service", "api response waiting");

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                    }
/*
                    String unsentmsgs = "";
                    for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                        if (conversationArray[threadNum].get(j).sentState == AppKeys.MESSAGE_STATE_UNSENT) {
                            unsentmsgs = unsentmsgs + " " + conversationArray[threadNum].get(j).message;
                        }
                    }

                    Log.d("messaging_service", "unsent in cid "
                            + threadConversationId[threadNum] + " are " + unsentmsgs);*/


                    Intent pushNotification = new Intent(Config.MESSAGE_SENT);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);


                }
                isApiCalled[threadNum] = false;
            }

            @Override
            public void onFailure(Call<PojoCreateMessageResponse> call, Throwable t) {
                //in case failed set its state to failed
                Log.d("messaging_service", newMsg.message + " of cid " + cid + "failed by" + threadNum);
                List<PojoChatMessage> savedList;

                while (true) {
                    if (GlobalVariables.isIndividualChatMutexAvailable) {
                        GlobalVariables.isIndividualChatMutexAvailable = false;
                        savedList = preferenceUtils.getConversation(cid);
                        for (int j = 0; j < savedList.size(); j++) {
                            if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                    && savedList.get(j).tempMsgId.equals(newMsg.tempMsgId)) {
                                savedList.get(j).setSentState(AppKeys.MESSAGE_STATE_FAILED);
                                break;
                            }
                        }

                        preferenceUtils.saveConversation(cid, savedList);

                        for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                            if (conversationArray[threadNum].get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                    && conversationArray[threadNum].get(j).tempMsgId.equals(newMsg.tempMsgId)) {
                                conversationArray[threadNum].get(j).setSentState(AppKeys.MESSAGE_STATE_FAILED);
                                break;
                            }
                        }

                        GlobalVariables.isIndividualChatMutexAvailable = true;
                        break;
                    } else {
                        try {
                            Thread.sleep(50);
                            Log.d("messaging_service", "api response waiting");

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //  preferenceUtils.saveConversation(cid, messageList);
                Intent pushNotification = new Intent(Config.MESSAGE_FAILED);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                isApiCalled[threadNum] = false;
            }
        });
    }


    public void hitUploadImageApi(final List<PojoChatMessage> messageList,
                                  final int messageNum, final int threadNum, final String cid) {
        isApiCalled[threadNum] = true;

        final PojoChatMessage newMsg;
        newMsg = messageList.get(messageNum);

        final File file = commonFunctions.getScaledDownImage(newMsg.image);


        RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(file)), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), mFile);
        final RequestBody userIdBody = RequestBody.create(MediaType.parse("text/plain"), newMsg.userId);
        RequestBody message = RequestBody.create(MediaType.parse("text/plain"), newMsg.message);
        final RequestBody tempIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(newMsg.tempMsgId));

        //  RequestBody recipient = RequestBody.create(MediaType.parse("text/plain"), newMsg.recipient);
        RequestBody conversation_id = RequestBody.create(MediaType.parse("text/plain"), cid);

        Call<PojoCreateMessageResponse> call = apiService.uploadChatImage(fileToUpload, userIdBody,
                message, tempIdBody, null, conversation_id);

        Log.d("messaging_service", "api called for image " + newMsg.message + " of cid " + cid + "by" + threadNum);

        call.enqueue(new Callback<PojoCreateMessageResponse>() {
            @Override
            public void onResponse(Call<PojoCreateMessageResponse> call, Response<PojoCreateMessageResponse> response) {
                Log.d("messaging_service", newMsg.message + " image of cid " + cid + "sent by" + threadNum);

                if (response.body() != null && response.body().data != null) {
                    List<PojoChatMessage> savedList;
                    PojoCreateMessageResponseData item = response.body().data;

                    while (true) {
                        if (GlobalVariables.isIndividualChatMutexAvailable) {
                            GlobalVariables.isIndividualChatMutexAvailable = false;
                            savedList = preferenceUtils.getConversation(cid);
                            for (int j = 0; j < savedList.size(); j++) {
                                if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                        && savedList.get(j).tempMsgId.equals(newMsg.tempMsgId)) {

                                    savedList.get(j).setMessageId(item.lastMessageId);
                                    savedList.get(j).setImage(item.image);
                                    //   savedList.get(j)wMsg.setTime(item.time);
                                    savedList.get(j).setUserName(item.name);
                                    savedList.get(j).setUserFullname(item.name);
                                    savedList.get(j).setUserGender("male");
                                    savedList.get(j).setUserPicture(item.picture);
                                    savedList.get(j).setCid(cid);
                                    savedList.get(j).setSentState(AppKeys.MESSAGE_STATE_SENT);
                                    savedList.get(j).setTempMsgId(item.tempMsgId);

                                    Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
                                    imageMap = preferenceUtils.getChatImagesMap();
                                    saveBitmapToStorage(getImageName(item.image), b);
                                    preferenceUtils.saveChatImagesMap(imageMap);

                                    break;
                                }
                            }

                            preferenceUtils.saveConversation(cid, savedList);

                            for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                                if (conversationArray[threadNum].get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                        && conversationArray[threadNum].get(j).tempMsgId.equals(newMsg.tempMsgId)) {

                                    conversationArray[threadNum].get(j).setMessageId(item.lastMessageId);
                                    conversationArray[threadNum].get(j).setImage(item.image);
                                    //   conversationArray[threadNum]dList.get(j)wMsg.setTime(item.time);
                                    conversationArray[threadNum].get(j).setUserName(item.name);
                                    conversationArray[threadNum].get(j).setUserFullname(item.name);
                                    conversationArray[threadNum].get(j).setUserGender("male");
                                    conversationArray[threadNum].get(j).setUserPicture(item.picture);
                                    conversationArray[threadNum].get(j).setCid(cid);
                                    conversationArray[threadNum].get(j).setSentState(AppKeys.MESSAGE_STATE_SENT);
                                    conversationArray[threadNum].get(j).setTempMsgId(item.tempMsgId);

                                    Log.d("messaging_service", conversationArray[threadNum].get(j).message + " updated sent state");
                                    break;
                                }
                            }

                  /*          conversationArray[threadNum].get(messageNum);
                            conversationArray[threadNum].add(messageNum, newMsg);*/

                          /*  String unsentmsgs = "";
                            for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                                if (conversationArray[threadNum].get(j).sentState == AppKeys.MESSAGE_STATE_UNSENT) {
                                    unsentmsgs = unsentmsgs + " " + conversationArray[threadNum].get(j).message;
                                }
                            }

                            Log.d("messaging_service", "unsent in cid " + cid + " are " + unsentmsgs);*/
                            GlobalVariables.isIndividualChatMutexAvailable = true;
                            break;
                        } else {
                            try {
                                Thread.sleep(50);
                                Log.d("messaging_service", "api response waiting");

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    Intent pushNotification = new Intent(Config.MESSAGE_SENT);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);
                }

                isApiCalled[threadNum] = false;
            }

            @Override
            public void onFailure(Call<PojoCreateMessageResponse> call, Throwable t) {

                //set the state as failed if this message has been tried to be sent before

                if (true) {
                    // if (newMsg.isRetried) {
                    Log.d("messaging_service", newMsg.message + " of cid " + cid + "failed by" + threadNum);

                    List<PojoChatMessage> savedList;
                    while (true) {
                        if (GlobalVariables.isIndividualChatMutexAvailable) {
                            GlobalVariables.isIndividualChatMutexAvailable = false;
                            savedList = preferenceUtils.getConversation(cid);
                            for (int j = 0; j < savedList.size(); j++) {
                                if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                        && savedList.get(j).tempMsgId.equals(newMsg.tempMsgId)) {
                                    savedList.get(j).setSentState(AppKeys.MESSAGE_STATE_FAILED);
                                    break;
                                }
                            }

                            preferenceUtils.saveConversation(cid, savedList);

                            for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                                if (conversationArray[threadNum].get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                        && conversationArray[threadNum].get(j).tempMsgId.equals(newMsg.tempMsgId)) {
                                    conversationArray[threadNum].get(j).setSentState(AppKeys.MESSAGE_STATE_FAILED);
                                    break;
                                }
                            }

                            GlobalVariables.isIndividualChatMutexAvailable = true;
                            break;
                        } else {
                            try {
                                Thread.sleep(50);
                                Log.d("messaging_service", "api response waiting");

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    Intent pushNotification = new Intent(Config.MESSAGE_FAILED);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);
                    commonFunctions.setToastMessage(getApplicationContext(), t.toString(),
                            Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                }

                //if the message has not been retried to be sent
                else {
                    List<PojoChatMessage> savedList;
                    while (true) {
                        if (GlobalVariables.isIndividualChatMutexAvailable) {
                            GlobalVariables.isIndividualChatMutexAvailable = false;
                            savedList = preferenceUtils.getConversation(cid);
                            for (int j = 0; j < savedList.size(); j++) {
                                if (savedList.get(j).tempMsgId.equals(newMsg.tempMsgId)) {
                                    savedList.get(j).isRetried = true;
                                    break;
                                }
                            }

                            preferenceUtils.saveConversation(cid, savedList);

                            for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                                if (conversationArray[threadNum].get(j).tempMsgId.equals(newMsg.tempMsgId)) {
                                    conversationArray[threadNum].get(j).isRetried = true;
                                    break;
                                }
                            }

                            GlobalVariables.isIndividualChatMutexAvailable = true;
                            break;
                        } else {
                            try {
                                Thread.sleep(50);
                                Log.d("messaging_service", "api response waiting");

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    //  hitGetMessagesApi(newMsg.userId, cid, newMsg.tempMsgId, threadNum, file);

                }

                isApiCalled[threadNum] = false;

            }
        });
    }


    private void hitGetMessagesApi(final String user_id, final String cid, final String tempId, final int threadNum, final File file) {
        Call<PojoGetChatMessagesResponse> call = apiService.getUserChatMessages(user_id, cid,
                null, String.valueOf(10));

        isRetryApiCalled[threadNum] = true;
        call.enqueue(new Callback<PojoGetChatMessagesResponse>() {
            @Override
            public void onResponse(Call<PojoGetChatMessagesResponse> call, Response<PojoGetChatMessagesResponse> response) {
                if (response.body() != null && response.body().data != null) {
                    if (response.body().data.messages != null) {

                        boolean isPresent = false;
                        for (int i = response.body().data.messages.size() - 1; i >= 0; i--) {
                            if (tempId.equals(response.body().data.messages.get(i).tempMsgId)) {
                                isPresent = true;
                                break;
                            }
                        }

                        if (isPresent) {
                            List<PojoChatMessage> savedList;

                            while (true) {
                                if (GlobalVariables.isIndividualChatMutexAvailable) {
                                    GlobalVariables.isIndividualChatMutexAvailable = false;
                                    savedList = preferenceUtils.getConversation(cid);

                                    PojoChatMessage apiMessage = null;

                                    for (int i = 0; i < response.body().data.messages.size(); i++) {
                                        if (tempId.equals(response.body().data.messages.get(i).tempMsgId)) {
                                            apiMessage = response.body().data.messages.get(i);
                                            break;
                                        }
                                    }

                                    for (int j = 0; j < savedList.size(); j++) {
                                        if (apiMessage != null
                                                && savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                                && savedList.get(j).tempMsgId.equals(tempId)) {

                                            savedList.get(j).setMessageId(apiMessage.messageId);
                                            savedList.get(j).setImage(apiMessage.image);
                                            //   savedList.get(j)wMsg.setTime(item.time);
                                            savedList.get(j).setUserName(apiMessage.userFullname);
                                            savedList.get(j).setUserFullname(apiMessage.userFullname);
                                            savedList.get(j).setUserGender("male");
                                            savedList.get(j).setUserPicture(apiMessage.userPicture);
                                            savedList.get(j).setCid(cid);
                                            savedList.get(j).setSentState(AppKeys.MESSAGE_STATE_SENT);
                                            savedList.get(j).setTempMsgId(apiMessage.tempMsgId);

                                            Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
                                            imageMap = preferenceUtils.getChatImagesMap();
                                            saveBitmapToStorage(getImageName(apiMessage.image), b);
                                            preferenceUtils.saveChatImagesMap(imageMap);

                                            break;
                                        }
                                    }

                                    preferenceUtils.saveConversation(cid, savedList);

                                    for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                                        if (apiMessage != null
                                                && conversationArray[threadNum].get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                                && conversationArray[threadNum].get(j).tempMsgId.equals(tempId)) {

                                            conversationArray[threadNum].get(j).setMessageId(apiMessage.messageId);
                                            conversationArray[threadNum].get(j).setImage(apiMessage.image);
                                            //   conversationArray[threadNum]dList.get(j)wMsg.setTime(item.time);
                                            conversationArray[threadNum].get(j).setUserName(apiMessage.userFullname);
                                            conversationArray[threadNum].get(j).setUserFullname(apiMessage.userFullname);
                                            conversationArray[threadNum].get(j).setUserGender("male");
                                            conversationArray[threadNum].get(j).setUserPicture(apiMessage.userPicture);
                                            conversationArray[threadNum].get(j).setCid(cid);
                                            conversationArray[threadNum].get(j).setSentState(AppKeys.MESSAGE_STATE_SENT);
                                            conversationArray[threadNum].get(j).setTempMsgId(apiMessage.tempMsgId);

                                            Log.d("messaging_service", conversationArray[threadNum].get(j).message + " updated sent state");
                                            break;
                                        }
                                    }

                  /*          conversationArray[threadNum].get(messageNum);
                            conversationArray[threadNum].add(messageNum, newMsg);*/

                                    String unsentmsgs = "";
                                    for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                                        if (conversationArray[threadNum].get(j).sentState == AppKeys.MESSAGE_STATE_UNSENT) {
                                            unsentmsgs = unsentmsgs + " " + conversationArray[threadNum].get(j).message;
                                        }
                                    }

                                    Log.d("messaging_service", "unsent in cid " + cid + " are " + unsentmsgs);
                                    GlobalVariables.isIndividualChatMutexAvailable = true;
                                    break;
                                } else {
                                    try {
                                        Thread.sleep(50);
                                        Log.d("messaging_service", "api response waiting");

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            Intent pushNotification = new Intent(Config.MESSAGE_SENT);
                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);
                        } else {

                        }
                    }
                }

                isRetryApiCalled[threadNum] = false;

            }

            @Override
            public void onFailure(Call<PojoGetChatMessagesResponse> call, Throwable t) {
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                isRetryApiCalled[threadNum] = false;

                List<PojoChatMessage> savedList;

                while (true) {
                    if (GlobalVariables.isIndividualChatMutexAvailable) {
                        GlobalVariables.isIndividualChatMutexAvailable = false;
                        savedList = preferenceUtils.getConversation(cid);
                        for (int j = 0; j < savedList.size(); j++) {
                            if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                    && savedList.get(j).tempMsgId.equals(tempId)) {
                                savedList.get(j).setSentState(AppKeys.MESSAGE_STATE_FAILED);
                                break;
                            }
                        }

                        preferenceUtils.saveConversation(cid, savedList);

                        for (int j = 0; j < conversationArray[threadNum].size(); j++) {
                            if (conversationArray[threadNum].get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                    && conversationArray[threadNum].get(j).tempMsgId.equals(tempId)) {
                                conversationArray[threadNum].get(j).setSentState(AppKeys.MESSAGE_STATE_FAILED);
                                break;
                            }
                        }

                        GlobalVariables.isIndividualChatMutexAvailable = true;
                        break;
                    } else {
                        try {
                            Thread.sleep(50);
                            Log.d("messaging_service", "api response waiting");

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                Intent pushNotification = new Intent(Config.MESSAGE_FAILED);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);
            }
        });
    }

    public String getImageName(String path) {
        StringBuilder sb = new StringBuilder(path);
        String revStr = sb.reverse().toString();
        String imgName = revStr.substring(revStr.indexOf(".") + 1, revStr.indexOf("/"));
        sb = new StringBuilder(imgName);
        path = sb.reverse().toString();
        return path;
    }

    private void saveBitmapToStorage(String imageName, Bitmap bitmap) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/Luneblaze/chat/images");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Luneblaze/chat/images/");
            wallpaperDirectory.mkdirs();
        }

        File destination = new File(new File("/sdcard/Luneblaze/chat/images/"), imageName);
        if (destination.exists()) {
            destination.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(destination);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!imageMap.containsKey(imageName)) {
            imageMap.put(imageName, destination.getAbsolutePath());
            preferenceUtils.saveChatImagesMap(imageMap);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < THREAD_COUNT; i++) {
            if (threadArr[i] != null) {
                threadArr[i].interrupt();

                threadArr[i] = null;
            }
        }
    }

    private int getFreeThreadIndex() {
        for (int i = 0; i < THREAD_COUNT; i++) {
            if (threadConversationId[i] == null || threadConversationId[i].isEmpty()) {
                //   Log.d("messaging_service", "free thread index is " + i);
                return i;
            }
        }
        return -1;
    }

    public boolean isConversationAssigned(String cid) {
        for (int i = 0; i < THREAD_COUNT; i++) {
            if (threadConversationId[i] != null && threadConversationId[i].equals(cid)) {
                //   Log.d("messaging_service", cid + " is still on thread" + i);
                return true;
            }
        }
        return false;
    }


}
