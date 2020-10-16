package com.avadna.luneblaze.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.widget.Toast;

import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.pojoChat.PojoChatMessage;
import com.avadna.luneblaze.pojo.pojoChat.PojoConversationListItem;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateMessageResponse;
import com.avadna.luneblaze.pojo.pojoChat.PojoCreateMessageResponseData;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessagingService extends Service {
    ApiInterface apiService;

    int THREAD_COUNT = 4;
    Thread threadArr[] = new Thread[THREAD_COUNT];
    List<PojoConversationListItem> allConversationList;
    boolean[] isApiCalled = new boolean[THREAD_COUNT];
    String[][] cidList;
    int[] cidSubListCount = new int[THREAD_COUNT];
    int totalConversations = 0;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;

    boolean areThreadsRunning = false;

    public MessagingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
        //  throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // commonFunctions.setToastMessage(getApplicationContext(), "messaging service running", Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        preferenceUtils = new PreferenceUtils(getApplicationContext());
        commonFunctions = new CommonFunctions(getApplicationContext());
        allConversationList = preferenceUtils.getAllMessageList();
        totalConversations = allConversationList.size();
        cidList = new String[THREAD_COUNT][totalConversations];

        Thread mainThread = new Thread() {
            boolean isAlreadyPresent = false;

            public void run() {

                //   while(true){
                      /*  try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                for (int i = 0; i < totalConversations; i++) {
                    isAlreadyPresent = false;
                    int threadNumber = i % THREAD_COUNT;

                    //check if it is already present in list
                    if (cidSubListCount[threadNumber] > 0) {
                        for (int j = 0; j < cidSubListCount[threadNumber]; j++) {
                            if (allConversationList.get(i).conversationId.equals(cidList[threadNumber][j])) {
                                isAlreadyPresent = true;
                                break;
                            }
                        }
                    }

                    //if not present then add it
                    if (!isAlreadyPresent) {
                        cidList[threadNumber][cidSubListCount[threadNumber]] = allConversationList.get(i).conversationId;
                        //increment cidCount
                        cidSubListCount[threadNumber]++;
                    }

                }
            }

            //   }
        };
        mainThread.start();

        if (!areThreadsRunning) {
            areThreadsRunning = true;

            for (int i = 0; i < THREAD_COUNT; i++) {
                final int finalI = i;
                threadArr[i] = new Thread() {
                    int threadNum = finalI;
                    List<PojoChatMessage> messageList;

                    public void run() {
                        Log.d("thread info", "thread " + finalI + " running");

                        while (true) {
                            try {
                                sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            //for each conversation in list of this thread
                            for (int convNum = 0; convNum < cidSubListCount[threadNum]; convNum++) {
                                messageList = preferenceUtils.getConversation(cidList[threadNum][convNum]);

                                //if the conversation contains messages
                                if (!messageList.isEmpty()) {
                                    //if there are unsent messages

                                    //for each message in this conversation
                                    for (int messageNum = messageList.size() - 1; messageNum >= 0; messageNum--) {
                                        //if the message is unsent and api is not called yet
                                        if (messageList.get(messageNum).sentState != AppKeys.MESSAGE_STATE_SENT
                                                && messageList.get(messageNum).image.isEmpty()
                                                && !isApiCalled[threadNum]) {
                                            hitReplyMessageApi(messageList, messageNum, threadNum,
                                                    cidList[threadNum][convNum]);
                                        } else if ((messageList.get(messageNum).sentState == AppKeys.MESSAGE_STATE_UNSENT
                                                && !isApiCalled[threadNum])) {
                                            hitUploadImageApi(messageList, messageNum, threadNum,
                                                    cidList[threadNum][convNum]);

                                        }


                                   /*     if (messageList.get(messageNum).sentState != AppKeys.MESSAGE_STATE_SENT && !isApiCalled[threadNum]) {
                                            if (messageList.get(messageNum).image.isEmpty()) {
                                                hitReplyMessageApi(messageList, messageNum, threadNum, cidList[threadNum][convNum]);
                                            } else {
                                                hitUploadImageApi(messageList, messageNum, threadNum, cidList[threadNum][convNum]);
                                            }
                                        }*/

                                    }
                                }

                            }
                        }
                    }
                };
                threadArr[i].start();
            }
        }


        return super.onStartCommand(intent, flags, startId);
    }

    private void hitReplyMessageApi(final List<PojoChatMessage> messageList,
                                    final int messageNum, final int threadNum, final String cid) {

        isApiCalled[threadNum] = true;
        final PojoChatMessage newMsg;
        newMsg = messageList.get(messageNum);
        Call<PojoCreateMessageResponse> call =
                apiService.replyToMessage(newMsg.userId, newMsg.message,
                        String.valueOf(newMsg.tempMsgId), "", cid);
        call.enqueue(new Callback<PojoCreateMessageResponse>() {
            @Override
            public void onResponse(Call<PojoCreateMessageResponse> call, Response<PojoCreateMessageResponse> response) {
                if (response.body() != null && response.body().data != null) {
                    PojoCreateMessageResponseData item = response.body().data;

                    newMsg.setMessageId(item.lastMessageId);
                    newMsg.setImage(item.image);
                    newMsg.setTime(item.time);
                    newMsg.setUserName(item.name);
                    newMsg.setUserFullname(item.name);
                    newMsg.setUserGender("male");
                    newMsg.setUserPicture(item.picture);
                    newMsg.setCid(cid);
                    newMsg.setSentState(AppKeys.MESSAGE_STATE_SENT);
                    newMsg.setTempMsgId(item.tempMsgId);

                    // preferenceUtils.saveConversation(cid,messageList);

                    List<PojoChatMessage> savedList = preferenceUtils.getConversation(cid);

                    for (int j = 0; j < savedList.size(); j++) {
                        if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                && savedList.get(j).tempMsgId == newMsg.tempMsgId) {
                            savedList.remove(j);
                            savedList.add(j, newMsg);
                            break;
                        }
                    }


              /*      for (int j = 0; j < savedList.size(); j++) {
                        //replace the first unsent msg that we find
                        if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                &&savedList.get(j).message.equals(newMsg.message)) {
                            savedList.remove(j);
                            savedList.add(j, newMsg);
                            break;
                            //  messageList.add(savedList.get(j));
                        }
                    }*/

                    preferenceUtils.saveConversation(cid, savedList);
                    Intent pushNotification = new Intent(Config.MESSAGE_SENT);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);
                } /*else {
                    commonFunctions.setToastMessage(ChatActivity.this, "" + message, Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);
                }*/
                isApiCalled[threadNum] = false;

            }

            @Override
            public void onFailure(Call<PojoCreateMessageResponse> call, Throwable t) {
                //in case failed set its state to failed
                newMsg.setSentState(AppKeys.MESSAGE_STATE_FAILED);

                List<PojoChatMessage> savedList = preferenceUtils.getConversation(cid);

                for (int j = 0; j < savedList.size(); j++) {
                    if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                            && savedList.get(j).tempMsgId == newMsg.tempMsgId) {
                        savedList.remove(j);
                        savedList.add(j, newMsg);
                        break;
                    }
                }

                preferenceUtils.saveConversation(cid, savedList);

                //  preferenceUtils.saveConversation(cid, messageList);
                Intent pushNotification = new Intent(Config.MESSAGE_FAILED);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                isApiCalled[threadNum] = false;

            }
        });


    }


    public void hitUploadImageApi(final List<PojoChatMessage> messageList,
                                  final int messageNum, final int threadNum, final String cid) {

        final PojoChatMessage newMsg;
        newMsg = messageList.get(messageNum);

        File file = commonFunctions.getScaledDownImage(newMsg.image);
        // file = getScaledDownImage(file);

        RequestBody mFile = RequestBody.create(MediaType.parse(commonFunctions.getMimeType(file)), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("photo", file.getName(), mFile);
        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), newMsg.userId);
        RequestBody message = RequestBody.create(MediaType.parse("text/plain"), newMsg.message);
        RequestBody temp_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(newMsg.tempMsgId));

        //  RequestBody recipient = RequestBody.create(MediaType.parse("text/plain"), newMsg.recipient);
        RequestBody conversation_id = RequestBody.create(MediaType.parse("text/plain"), cid);

        Call<PojoCreateMessageResponse> call = apiService.uploadChatImage(fileToUpload, user_id,
                message, temp_id, null, conversation_id);
        isApiCalled[threadNum] = true;
        call.enqueue(new Callback<PojoCreateMessageResponse>() {
            @Override
            public void onResponse(Call<PojoCreateMessageResponse> call, Response<PojoCreateMessageResponse> response) {
                if (response.body() != null && response.body().data != null) {
                    PojoCreateMessageResponseData item = response.body().data;
                    newMsg.setMessageId(item.lastMessageId);
                    newMsg.setImage(item.image);
                    newMsg.setTime(item.time);
                    newMsg.setUserName(item.name);
                    newMsg.setUserFullname(item.name);
                    newMsg.setUserGender("male");
                    newMsg.setUserPicture(item.picture);
                    newMsg.isImageUploading = false;
                    newMsg.setCid(cid);
                    newMsg.setSentState(AppKeys.MESSAGE_STATE_SENT);
                    newMsg.setTempMsgId(item.tempMsgId);

                    //  preferenceUtils.saveConversation(cid, messageList);


                    List<PojoChatMessage> savedList = preferenceUtils.getConversation(cid);
                  /*  for (int j = 0; j < savedList.size(); j++) {
                        //add any unsent messages
                        if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT) {
                            messageList.add(savedList.get(j));
                        }
                    }*/

                    for (int j = 0; j < savedList.size(); j++) {
                        if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                && savedList.get(j).tempMsgId == newMsg.tempMsgId) {
                            savedList.remove(j);
                            savedList.add(j, newMsg);
                        }
                    }

                 /*   for (int j = 0; j < savedList.size(); j++) {
                        //replace the first unsent msg that we find
                        if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                                &&savedList.get(j).message.equals(newMsg.message)) {
                            savedList.remove(j);
                            savedList.add(j, newMsg);
                            break;
                            //  messageList.add(savedList.get(j));
                        }
                    }*/
                    preferenceUtils.saveConversation(cid, savedList);
                    Intent pushNotification = new Intent(Config.MESSAGE_SENT);
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(pushNotification);
                }

                isApiCalled[threadNum] = false;
            }

            @Override
            public void onFailure(Call<PojoCreateMessageResponse> call, Throwable t) {

                // Log error here since request failed
                newMsg.setSentState(AppKeys.MESSAGE_STATE_FAILED);

                List<PojoChatMessage> savedList = preferenceUtils.getConversation(cid);

                for (int j = 0; j < savedList.size(); j++) {
                    if (savedList.get(j).sentState != AppKeys.MESSAGE_STATE_SENT
                            && savedList.get(j).tempMsgId == newMsg.tempMsgId) {
                        savedList.remove(j);
                        savedList.add(j, newMsg);
                        break;
                    }
                }

                preferenceUtils.saveConversation(cid, savedList);
                Intent msgFailed = new Intent(Config.MESSAGE_FAILED);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(msgFailed);
                commonFunctions.setToastMessage(getApplicationContext(), t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
                isApiCalled[threadNum] = false;

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < THREAD_COUNT; i++) {
            threadArr[i].destroy();
        }
    }
}
