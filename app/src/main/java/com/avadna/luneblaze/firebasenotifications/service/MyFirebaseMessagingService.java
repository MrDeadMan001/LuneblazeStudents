package com.avadna.luneblaze.firebasenotifications.service;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Html;
import android.text.SpannableString;
import android.util.Log;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.LoggedOutAlertActivity;
import com.avadna.luneblaze.activities.MainActivity;
import com.avadna.luneblaze.activities.WebViewActivity;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.activities.chat.ChatActivity;
import com.avadna.luneblaze.activities.sessions.SessionLoaderActivity;
import com.avadna.luneblaze.firebasenotifications.config.Config;
import com.avadna.luneblaze.helperClasses.AppKeys;
import com.avadna.luneblaze.helperClasses.CommonFunctions;
import com.avadna.luneblaze.helperClasses.NotificationLoader;
import com.avadna.luneblaze.helperClasses.PreferenceUtils;
import com.avadna.luneblaze.pojo.PojoGetNotificationListResponseData;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.PojoUpdateTokenResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;
import com.avadna.luneblaze.utils.NotificationUtils;
import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static androidx.core.app.NotificationCompat.GROUP_ALERT_CHILDREN;
import static com.bumptech.glide.request.RequestOptions.bitmapTransform;

/**
 * Created by Sunny on 13-03-2018.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    ApiInterface apiService;

    private NotificationUtils notificationUtils;
    static PreferenceUtils preferenceUtils;
    NotificationLoader notificationLoader;
    Boolean isSoundTurnedOn;

    static String NOTIFICATION_GROUP_TYPE_MESSAGE = "message";
    String NOTIFICATION_GROUP_TYPE_APP = "app";

    static NotificationManagerCompat notificationManager;


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "fcm token : " + refreshedToken);

        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.d(TAG, "sendRegistrationToServer: " + token);
        hitUpdateTokenApi();
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }

    private void hitUpdateTokenApi() {
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);
        String user_id = pref.getString(AppKeys.USER_ID_KEY, "");
        Call<PojoUpdateTokenResponse> call = apiService.updateFcmToken(user_id, android_id, "A", regId);
        call.enqueue(new Callback<PojoUpdateTokenResponse>() {
            @Override
            public void onResponse(Call<PojoUpdateTokenResponse> call, Response<PojoUpdateTokenResponse> response) {
                String message = "";
                if (response != null && response.body() != null) {
                    message = response.body().message;

                }
            }

            @Override
            public void onFailure(Call<PojoUpdateTokenResponse> call, Throwable t) {
                // Log error here since request failed
                // commonFunctions.setToastMessage(ArticleActivity.this, t.toString(), Toast.LENGTH_LONG,AppKeys.TOAST_DEBUG);

            }
        });
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (preferenceUtils == null) {
            preferenceUtils = new PreferenceUtils(getApplicationContext());
        }

        // this.registerReceiver(new MessageNotifDeleteBroadcastReceiver(), new IntentFilter());

        if (preferenceUtils.is_user_login()) {
            if (remoteMessage == null)
                return;

            // Check if message contains a notification payload.
            if (remoteMessage.getNotification() != null) {
                Log.d(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
                handleNotification(remoteMessage.getNotification().getBody());
            }

            // Check if message contains a data payload.
            if (remoteMessage.getData().size() > 0) {
                Log.d(TAG, "Data Payload: " + remoteMessage.getData().toString());
                try {
                    String str = remoteMessage.getData().toString();
                    //  Map<String, String> params = remoteMessage.getData();
                    JSONObject json = new JSONObject(str);
                    handleDataMessage(json);
                } catch (Exception e) {
                    Log.d(TAG, "Exception: " + e.getMessage());
                }
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message

            // play notification sound

            if (isSoundTurnedOn == null) {
                preferenceUtils = new PreferenceUtils(getApplicationContext());
                isSoundTurnedOn = preferenceUtils.getSoundSettings();
            }
        /*    if (isSoundTurnedOn) {
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            }*/

        } else {
            // If the app is in background, firebase itself handles the notification
        }
    }

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());
        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            //   boolean isBackground = data.getBoolean("is_background");
            //   String imageUrl = data.getString("image");
            //  String timestamp = data.getString("timestamp");
            String type = data.getString("type");

            //   JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            //    Log.e(TAG, "isBackground: " + isBackground);
            //    Log.e(TAG, "payload: " + payload.toString());
            //    Log.e(TAG, "imageUrl: " + imageUrl);
            //   Log.e(TAG, "timestamp: " + timestamp);
            // app is in foreground, broadcast the push message

            switch (type) {
                case "group_name_changed":
                case "group_photo_changed":
                case "group_member_left":
                case "group_member_add":
                case "group_member_remove":
                case "add_admin":
                    Intent groupUpdate = new Intent(Config.GROUP_UPDATE_BROADCAST);
                    groupUpdate.putExtra("group_id", data.getString("group_id"));
                    groupUpdate.putExtra("conversation_id", data.getString("conversation_id"));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(groupUpdate);

                    if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                        notificationLoader = new NotificationLoader(getApplicationContext());
                        Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                        chatIntent.putExtra("cid", data.getString("conversation_id"));
                        PendingIntent notifPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                0, chatIntent, FLAG_UPDATE_CURRENT);

                        preferenceUtils = new PreferenceUtils(getApplicationContext());
                        PojoLoginResponseData userData = preferenceUtils.getUserLoginData();

                        if (type.equals("group_member_left")) {
                            if (data.getString("actor_id").equals(userData.userId)) {
                                message = message.replace(userData.userFullname,
                                        getApplicationContext().getString(R.string.you));
                                message = message.replace("has", "");

                            }
                        } else {
                            String firstHalf = message.substring(0, message.length() / 2);
                            String secondHalf = message.substring(message.length() / 2);


                            if (data.getString("actor_id").equals(userData.userId)) {
                                secondHalf = secondHalf.replace(userData.userFullname,
                                        getApplicationContext().getString(R.string.you));

                            }
                            if (data.getString("actee_id").equals(userData.userId)) {
                                firstHalf = firstHalf.replace(userData.userFullname,
                                        getApplicationContext().getString(R.string.you));
                                firstHalf = firstHalf.replace("has", "have");
                                firstHalf = firstHalf.replace("was", "were");

                            }

                            message = firstHalf + secondHalf;
                        }

                        message = Html.fromHtml(message).toString();

                        showNotification(notifPendingIntent, data.getString("group_name"),
                                message, data.getString("image"), "", AppKeys.channel_ids[1]);
                    }
                    break;

                case "message":
                    HashMap<String, Integer> unreadConversations;
                    preferenceUtils = new PreferenceUtils(getApplicationContext());
                    unreadConversations = preferenceUtils.getUnreadConversations();
                    String conversation_id = data.getString("conversation_id");
                    if (unreadConversations.containsKey(conversation_id)) {
                        int currCount = unreadConversations.get(conversation_id);
                        currCount++;
                        //unreadConversations.remove(conversation_id);
                        unreadConversations.put(conversation_id, currCount);
                    } else {
                        unreadConversations.put(conversation_id, 1);
                    }
                    preferenceUtils.saveUnreadConversations(unreadConversations);

                    Intent newMsg = new Intent(Config.NEW_MESSAGE_FCM);
                    newMsg.putExtra("message", message);
                    newMsg.putExtra("last_message_id", data.getString("last_message_id"));
                    newMsg.putExtra("conversation_id", data.getString("conversation_id"));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(newMsg);

                    if (NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                        notificationLoader = new NotificationLoader(getApplicationContext());

                        HashMap<Integer, String> notifMap = preferenceUtils.getAppMessageNotificationMap();
                        int a = 5;
                        notifMap.put(Integer.parseInt(conversation_id), conversation_id);
                        preferenceUtils.saveAppMessageNotificationMap(notifMap);

                        String groupName = data.getString("group_name");

                        if (groupName.isEmpty()) {
                            String fcmMessage = data.getString("message");
                            if (!data.getString("image_path").isEmpty()) {
                                fcmMessage = "[" + getString(R.string.image) + "] " + fcmMessage;
                            }
                            String msg = fcmMessage;
                            List<String> msgList = preferenceUtils.getFcmMessageList(conversation_id);
                            msgList.add(msg);
                            preferenceUtils.saveFcmMessageList(msgList, conversation_id);
                            showMessageNotification(data.getString("user_fullname"),
                                    msgList, conversation_id, data.getString("image"),
                                    data.getString("image_path"), AppKeys.channel_ids[1]);

                        /*    showNotification(notifPendingIntent, data.getString("user_fullname"),
                                    msg, data.getString("image"), data.getString("image_path"));*/
                        } else {
                            String fcmMessage = data.getString("message");
                            if (!data.getString("image_path").isEmpty()) {
                                fcmMessage = "[" + getString(R.string.image) + "] " + fcmMessage;
                            }
                            String msg = data.getString("user_fullname") + " : " + fcmMessage;
                            List<String> msgList = preferenceUtils.getFcmMessageList(conversation_id);
                            msgList.add(msg);
                            preferenceUtils.saveFcmMessageList(msgList, conversation_id);
                            showMessageNotification(data.getString("group_name"),
                                    msgList, conversation_id, data.getString("image"),
                                    data.getString("image_path"), AppKeys.channel_ids[1]);
                        }
                    }
                    break;

                case "new_post":
                    Intent feedIntent = new Intent(Config.NEW_FEED_ITEM);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(feedIntent);
                    break;

                case "notification":
                    String notificationJsonStr = data.getString("notification");
                    notificationLoader = new NotificationLoader(getApplicationContext());
                    Type jsonType = new TypeToken<PojoGetNotificationListResponseData>() {
                    }.getType();
                    PojoGetNotificationListResponseData pojoGetNotificationListResponseData;
                    pojoGetNotificationListResponseData = new Gson().fromJson(notificationJsonStr, jsonType);
                    if (pojoGetNotificationListResponseData.action.equalsIgnoreCase("vote")
                            && pojoGetNotificationListResponseData.nodeType.equalsIgnoreCase("session")) {
                        break;
                    }
                    if (pojoGetNotificationListResponseData.action.equals("friend-add")) {
                        Intent newFriendReq = new Intent(Config.NEW_FRIEND_REQUEST);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(newFriendReq);
                        Intent homeIntent = new Intent(this, MainActivity.class);
                        homeIntent.putExtra("tab", "requests");
                        PendingIntent pi = PendingIntent.getActivity(this, 0, homeIntent, 0);
                        showNotification(pi, title, message, "", "", AppKeys.channel_ids[5]);
                    } else if (data.getString("message").contains("logged out")) {
                        if (preferenceUtils.is_user_login()) {
                            // openLoggedOutAlertActivity();
                        }
                    } else {
                        if (pojoGetNotificationListResponseData != null
                                && pojoGetNotificationListResponseData.notificationId != null) {
                            Intent newNotif = new Intent(Config.NEW_NOTIFICATION);
                            newNotif.putExtra("notification", message);
                            newNotif.putExtra("notification_id", pojoGetNotificationListResponseData.notificationId);
                            LocalBroadcastManager.getInstance(this).sendBroadcast(newNotif);
                            notificationLoader.openTarget(pojoGetNotificationListResponseData, 1);
                        }
                    }


                   /* Intent notifIntent = new Intent(this, MainActivity.class);
                    PendingIntent notifPendingIntent = PendingIntent.getActivity(this, 0, notifIntent, 0);
                    showNotification(notifPendingIntent, title, message);*/
                    break;

                case "mass_notification":
                    JSONObject notif = data.getJSONObject("notification");
                    String url = notif.getString("url");
                    Intent intent;
                    if (url.contains("luneblaze.com")) {
                        Uri uri = Uri.parse(url);
                        intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(uri);
                    } else {
                        intent = new Intent(this, WebViewActivity.class);
                        intent.putExtra(AppKeys.URL, url);
                    }
                    PendingIntent massPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                            0, intent, FLAG_UPDATE_CURRENT);
                    showNotification(massPendingIntent, title, message, "", "", AppKeys.channel_ids[6]);
                    break;


              /*  case "session_conducted":
                    Intent conductedIntent = new Intent(this, SessionLoaderActivity.class);
                    conductedIntent.putExtra("id", data.getString("id"));
                    conductedIntent.putExtra("action", AppKeys.SESSION_RATING);
                    conductedIntent.putExtra("notifId", AppKeys.RATING_NOTIFICATION);
                    PendingIntent conductedPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                            0, conductedIntent, FLAG_UPDATE_CURRENT);
                    showNotification(conductedPendingIntent, title, message, "", "", AppKeys.channel_ids[2]);
                    break;*/


                case "date_time":
                    addSessionToCalender(json);
                    break;

                case "session_finalised":
                    int notificationId = 1729;
                    PojoGetNotificationListResponseData notificationItem;
                    notificationItem = new Gson().fromJson(data.getString("notification"),
                            new TypeToken<PojoGetNotificationListResponseData>() {
                            }.getType());

                    Intent yesIntent = new Intent(this, SessionLoaderActivity.class);
                    yesIntent.putExtra("id", notificationItem.nodeUrl);
                    // yesIntent.putExtra("type","poll");
                    yesIntent.putExtra("action", AppKeys.FINALISE_YES);
                    yesIntent.putExtra("notifId", notificationId);
                    PendingIntent yesPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                            0, yesIntent, FLAG_UPDATE_CURRENT);

                    Intent noIntent = new Intent(this, SessionLoaderActivity.class);
                    noIntent.putExtra("id", notificationItem.nodeUrl);
                    //  noIntent.putExtra("type","poll");
                    noIntent.putExtra("action", AppKeys.FINALISE_NO);
                    noIntent.putExtra("notifId", notificationId);
                    PendingIntent noPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                            0, noIntent, FLAG_UPDATE_CURRENT);

                    showYesNoNotification(notificationId, "Luneblaze", Html.fromHtml(notificationItem.message).toString(),
                            yesPendingIntent, noPendingIntent, AppKeys.channel_ids[2]);
                    break;


                case "new_group":
                    Intent homeIntent = new Intent(this, MainActivity.class);
                    homeIntent.putExtra("tab", "chat");
                    homeIntent.putExtra("refresh", true);
                    PendingIntent pi = PendingIntent.getActivity(this, 0, homeIntent, 0);
                    showNotification(pi, title, message, "", "", AppKeys.channel_ids[1]);
                    Intent newGroup = new Intent(Config.NEW_MESSAGE_FCM);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(newGroup);
                    // showNotificationMessage(getApplicationContext(), title, message, ""+new Date().getTime(), homeIntent);
                    break;

                case "message_seen":
                    preferenceUtils = new PreferenceUtils(getApplicationContext());
                    HashMap<String, String> cList = preferenceUtils.getSeenConversations();
                    cList.put(data.getString("conversation_id"), data.getString("message_id"));
                    preferenceUtils.saveSeenConversations(cList);
                    Intent msgSeenIntent = new Intent(Config.MESSAGE_SEEN);
                    msgSeenIntent.putExtra("user_id", data.getString("user_id"));
                    msgSeenIntent.putExtra("message_id", data.getString("message_id"));
                    msgSeenIntent.putExtra("conversation_id", data.getString("conversation_id"));
                    msgSeenIntent.putExtra("seen_time", data.getString("seen_time"));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(msgSeenIntent);
                    break;

              /*  case "new_feed":
                    Intent feedIntent = new Intent(Config.NEW_FEED_ITEM);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(feedIntent);
                    break;
*/
            }

            // play notification sound
         /*   NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();*/

            // app is in background, show the notification in notification tray
            Intent resultIntent = new Intent(getApplicationContext(), VenueActivity.class);
            resultIntent.putExtra("message", message);

             /*   // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }*/

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    private void openLoggedOutAlertActivity() {
        Intent logoutAlertIntent = new Intent(getApplicationContext(), LoggedOutAlertActivity.class);
        if (preferenceUtils.getUserLoginData() != null) {
            startActivity(logoutAlertIntent);
        }
    }

    private void showYesNoNotification(int notifId, String title, String message, PendingIntent yesIntent,
                                       PendingIntent noIntent, String channel_id) {
        Uri soundUri;
        if (preferenceUtils.getSoundSettings()) {
            soundUri = Settings.System.DEFAULT_NOTIFICATION_URI;
        } else {
            soundUri = null;
        }
        long[] vibrate = new long[1];
        if (preferenceUtils.getVibrationSettings()) {
            vibrate[0] = 0;
        } else {
            vibrate[0] = 500;
        }

        NotificationCompat.Builder mBuilder;
        CommonFunctions commonFunctions = new CommonFunctions(getApplicationContext());
        mBuilder = new NotificationCompat.Builder(getApplicationContext(),
                channel_id)
                .setTicker(title)
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setContentTitle(title)
                .addAction(R.drawable.ic_thanks_filled, getString(R.string.yes), yesIntent)
                .addAction(R.drawable.ic_no_cross, getString(R.string.no), noIntent)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(vibrate)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(notifId, mBuilder.build());
    }


    public void showNotification(final PendingIntent pi, final String title, final String message,
                                 final String userImage, String bigImage, String channel_id) {
        NotificationCompat.Builder mBuilder;
        Uri soundUri;
        if (preferenceUtils.getSoundSettings()) {
            soundUri = Settings.System.DEFAULT_NOTIFICATION_URI;
        } else {
            soundUri = null;
        }
        long[] vibrate = new long[1];
        if (preferenceUtils.getVibrationSettings()) {
            vibrate[0] = 0;
        } else {
            vibrate[0] = 500;
        }

        CommonFunctions commonFunctions = new CommonFunctions(getApplicationContext());
        if (!userImage.isEmpty()) {
            try {
                Bitmap userImageBitmap = Glide
                        .with(getApplicationContext())
                        .asBitmap()
                        .load(userImage)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .submit()
                        .get();

                if (!bigImage.isEmpty()) {
                    Bitmap bigImageBitmap = Glide
                            .with(getApplicationContext())
                            .asBitmap()
                            .load(bigImage)
                            .submit()
                            .get();

                    mBuilder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                            .setTicker(title)
                            .setSmallIcon(R.drawable.ic_notification_logo)
                            .setColor(ContextCompat.getColor(getApplicationContext(), R.color.app_theme_medium))
                            .setLargeIcon(userImageBitmap)
                            .setContentTitle(title)
                            .setContentIntent(pi)
                            .setSound(soundUri)
                            .setVibrate(vibrate)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bigImageBitmap))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                } else {
                    mBuilder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                            .setTicker(title)
                            .setSmallIcon(R.drawable.ic_notification_logo)
                            .setColor(ContextCompat.getColor(getApplicationContext(), R.color.app_theme_medium))
                            .setLargeIcon(userImageBitmap)
                            .setContentTitle(title)
                            .setContentIntent(pi)
                            .setSound(soundUri)
                            .setVibrate(vibrate)
                            .setAutoCancel(true)
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(message))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                }

                int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(m, mBuilder.build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mBuilder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                    .setTicker(title)
                    .setSmallIcon(R.drawable.ic_notification_logo)
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.app_theme_medium))
                    .setContentTitle(title)
                    .setContentIntent(pi)
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setVibrate(vibrate)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify(0, mBuilder.build());
        }
    }


    public void showMessageNotification(final String title, List<String> messages,
                                        String conversation_id, final String userImage, String bigImage, String channel_id) {

        CommonFunctions commonFunctions = new CommonFunctions(getApplicationContext());
        Uri soundUri;
        if (preferenceUtils.getSoundSettings()) {
            soundUri = Settings.System.DEFAULT_NOTIFICATION_URI;
        } else {
            soundUri = null;
        }
        long[] vibrate = new long[1];
        if (preferenceUtils.getVibrationSettings()) {
            vibrate[0] = 0;
        } else {
            vibrate[0] = 500;
        }

        int notificationId = Integer.parseInt(conversation_id);

        NotificationCompat.Builder mBuilder;
        NotificationCompat.Builder publicBuilder;

        Intent clickedIntent = new Intent(getApplicationContext(), MessageNotifClickedBroadcastReceiver.class);
        clickedIntent.putExtra("cid", conversation_id);
        PendingIntent clickedPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                notificationId, clickedIntent, FLAG_UPDATE_CURRENT);

        Intent deleteIntent = new Intent(getApplicationContext(), MessageNotifDeleteBroadcastReceiver.class);
        deleteIntent.putExtra("cid", conversation_id);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                notificationId, deleteIntent, FLAG_UPDATE_CURRENT);

     /*   String replyLabel = getResources().getString(R.string.reply);
        RemoteInput remoteInput = new RemoteInput.Builder(AppKeys.notif_reply_key)
                .setLabel(replyLabel)
                .build();*/

        if (!userImage.isEmpty()) {
            try {
                Bitmap userImageBitmap = Glide
                        .with(getApplicationContext())
                        .asBitmap()
                        .load(userImage)
                        .apply(bitmapTransform(new CropCircleTransformation()))
                        .submit()
                        .get();


                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

                if (messages.size() <= 3) {
                    for (int i = 0; i < messages.size(); i++) {
                        SpannableString spannableString = new SpannableString(Html.fromHtml(messages.get(i)));
                     /*   if (messages.get(i).contains("[Image]")) {
                            ImageSpan imageSpan = new ImageSpan(getApplicationContext(), R.drawable.ic_add_image_gray);
                            spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }*/
                        inboxStyle.addLine(spannableString);
                    }
                } else {
                    for (int i = messages.size() - 3; i < messages.size(); i++) {
                        SpannableString spannableString = new SpannableString(Html.fromHtml(messages.get(i)));
                      /*  if (messages.get(i).contains("[Image]")) {
                            ImageSpan imageSpan = new ImageSpan(getApplicationContext(), R.drawable.ic_add_image_gray);
                            spannableString.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }*/
                        inboxStyle.addLine(spannableString);
                    }
                }


                publicBuilder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                        .setTicker(title)
                        .setSmallIcon(R.drawable.ic_notification_logo)
                        .setColor(ContextCompat.getColor(getApplicationContext(), R.color.app_theme_medium))
                        .setContentIntent(clickedPendingIntent)
                        .setContentText(Html.fromHtml(messages.get(messages.size() - 1)))
                        .setContentTitle(title + " (" + messages.size() + " " + getString(R.string.messages) + ")")
                        .setLargeIcon(userImageBitmap)
                        .setStyle(inboxStyle)
                        .setSound(soundUri)
                        .setVibrate(vibrate)
                        .setAutoCancel(true)
                        .setGroup(NOTIFICATION_GROUP_TYPE_MESSAGE)
                        .setGroupAlertBehavior(GROUP_ALERT_CHILDREN)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setDeleteIntent(deletePendingIntent);


                mBuilder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                        .setTicker(title)
                        .setSmallIcon(R.drawable.ic_notification_logo)
                        .setColor(ContextCompat.getColor(getApplicationContext(), R.color.app_theme_medium))
                        .setContentIntent(clickedPendingIntent)
                        .setContentTitle(title + " (" + messages.size() + " " + getString(R.string.messages) + ")")
                        .setContentText(Html.fromHtml(messages.get(messages.size() - 1)))
                        .setLargeIcon(userImageBitmap)
                        .setStyle(inboxStyle)
                        .setAutoCancel(true)
                        .setSound(soundUri)
                        .setVibrate(vibrate)
                        .setGroup(NOTIFICATION_GROUP_TYPE_MESSAGE)
                        .setGroupAlertBehavior(GROUP_ALERT_CHILDREN)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                        .setPublicVersion(publicBuilder.build())
                        .setDeleteIntent(deletePendingIntent);


                notificationManager = NotificationManagerCompat.from(getApplicationContext());
                notificationManager.notify(notificationId, mBuilder.build());

                showMessageSummaryNotification(getApplicationContext(), AppKeys.channel_ids[1]);


            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            for (int i = 0; i < messages.size(); i++) {
                inboxStyle.addLine(Html.fromHtml(messages.get(i)));
            }

            publicBuilder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                    .setTicker(title)
                    .setSmallIcon(R.drawable.ic_notification_logo)
                    .setContentIntent(clickedPendingIntent)
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.app_theme_medium))
                    .setContentTitle(title + " (" + messages.size() + " " + getString(R.string.new_messages) + ")")
                    .setContentText(messages.get(messages.size() - 1))
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setVibrate(vibrate)
                    .setGroup(NOTIFICATION_GROUP_TYPE_MESSAGE)
                    .setGroupAlertBehavior(GROUP_ALERT_CHILDREN)
                    .setStyle(inboxStyle)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setDeleteIntent(deletePendingIntent);


            mBuilder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                    .setTicker(title)
                    .setSmallIcon(R.drawable.ic_notification_logo)
                    .setContentIntent(clickedPendingIntent)
                    .setColor(ContextCompat.getColor(getApplicationContext(), R.color.app_theme_medium))
                    .setContentTitle(title + " (" + messages.size() + " " + getString(R.string.new_messages) + ")")
                    .setContentText(messages.get(messages.size() - 1))
                    .setAutoCancel(true)
                    .setSound(soundUri)
                    .setVibrate(vibrate)
                    .setGroup(NOTIFICATION_GROUP_TYPE_MESSAGE)
                    .setGroupAlertBehavior(GROUP_ALERT_CHILDREN)
                    .setStyle(inboxStyle)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setPublicVersion(publicBuilder.build())
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setDeleteIntent(deletePendingIntent);


            int m = Integer.parseInt(conversation_id);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify(m, mBuilder.build());
        }
    }

    private static void showMessageSummaryNotification(Context context, String channel_id) {
        NotificationCompat.Builder mBuilder;

        CommonFunctions commonFunctions = new CommonFunctions(context);
        Uri soundUri;
        if (preferenceUtils.getSoundSettings()) {
            soundUri = Settings.System.DEFAULT_NOTIFICATION_URI;
        } else {
            soundUri = null;
        }
        long[] vibrate = new long[1];
        if (preferenceUtils.getVibrationSettings()) {
            vibrate[0] = 0;
        } else {
            vibrate[0] = 500;
        }

        int messageCount = 0;
        HashMap<Integer, String> notifMap = preferenceUtils.getAppMessageNotificationMap();
        int convCount = notifMap.size();
        for (HashMap.Entry<Integer, String> entry : notifMap.entrySet()) {
            messageCount = messageCount + preferenceUtils.getFcmMessageList(entry.getValue()).size();
        }

        String summaryText = messageCount + " " + context.getString(R.string.messages) + " "
                + context.getString(R.string.from) + " " + convCount + " " + context.getString(R.string.conversations);


        Intent clickedIntent = new Intent(context, MessageNotifClickedBroadcastReceiver.class);
        clickedIntent.putExtra("type", "summary");
        PendingIntent clickedPendingIntent = PendingIntent.getBroadcast(context,
                AppKeys.MESSAGE_SUMMARY_ID, clickedIntent, FLAG_UPDATE_CURRENT);

        mBuilder =
                new NotificationCompat.Builder(context, channel_id)
                        .setContentTitle("Summary")
                        .setContentIntent(clickedPendingIntent)
                        //set content text to support devices running API level < 24
                        .setContentText("new messages")
                        .setSmallIcon(R.drawable.ic_notification_logo)
                        .setColor(ContextCompat.getColor(context, R.color.app_theme_medium))
                        //build summary info into InboxStyle template
                        .setStyle(new NotificationCompat.InboxStyle()
                                .setBigContentTitle(messageCount + " " + context.getString(R.string.new_messages))
                                .setSummaryText(summaryText))
                        //specify which group this notification belongs to
                        .setGroup(NOTIFICATION_GROUP_TYPE_MESSAGE)
                        .setGroupAlertBehavior(GROUP_ALERT_CHILDREN)
                        .setSound(soundUri)
                        .setVibrate(vibrate)
                        //set this notification as the summary for the group
                        .setGroupSummary(true)
                        .setOnlyAlertOnce(true);
        //   .setSound(null)

        notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(AppKeys.MESSAGE_SUMMARY_ID, mBuilder.build());

    }

    public static class MessageNotifDeleteBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (preferenceUtils == null) {
                preferenceUtils = new PreferenceUtils(context);
            }
            HashMap<Integer, String> notifMap = preferenceUtils.getAppMessageNotificationMap();
            String cid = intent.getStringExtra("cid");
            int conv_id = Integer.parseInt(cid);
            notifMap.remove(conv_id);
            preferenceUtils.saveAppMessageNotificationMap(notifMap);
            if (!notifMap.isEmpty()) {
                showMessageSummaryNotification(context, AppKeys.channel_ids[1]);
            }
            if (notifMap.isEmpty()) {
                try {
                    notificationManager.cancel(AppKeys.MESSAGE_SUMMARY_ID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class MessageNotifClickedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (preferenceUtils == null) {
                preferenceUtils = new PreferenceUtils(context);
            }
            if (intent.getStringExtra("type") != null
                    && !intent.getStringExtra("type").isEmpty()) {
                Intent allConvListIntent = new Intent(context, MainActivity.class);
                allConvListIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT/* | Intent.FLAG_ACTIVITY_NO_HISTORY*/);
                allConvListIntent.putExtra("tab", "chat");
                context.startActivity(allConvListIntent);
            } else {
                HashMap<Integer, String> notifMap = preferenceUtils.getAppMessageNotificationMap();
                String cid = intent.getStringExtra("cid");
                int conv_id = Integer.parseInt(cid);
                notifMap.remove(conv_id);
                preferenceUtils.saveAppMessageNotificationMap(notifMap);
                showMessageSummaryNotification(context, AppKeys.channel_ids[1]);
                if (notifMap.isEmpty()) {
                    try {
                        notificationManager.cancel(AppKeys.MESSAGE_SUMMARY_ID);
                        Intent chatIntent = new Intent(context, ChatActivity.class);
                        chatIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        chatIntent.putExtra("cid", intent.getStringExtra("cid"));
                        //  chatIntent.putExtra("source", "notif");
                        context.startActivity(chatIntent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }

    private void addSessionToCalender(JSONObject json) {

        PreferenceUtils preferenceUtils = new PreferenceUtils(this);

        long calenderEventID;
        HashMap<String, Long> calenderEventMap = new HashMap<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(json.getString("date"));
        } catch (Exception e) {
            e.printStackTrace();
        }


        int year = convertedDate.getYear();
        int month = convertedDate.getMonth();
        int day = convertedDate.getDate();

        long calID = 1;
        long startMillis = 0;
        long endMillis = 0;

        Calendar beginTime = Calendar.getInstance();
        beginTime.set(1900 + year, month + 1, day);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(1900 + year, month + 1, day);
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        // values.put(CalendarContract.Events.TITLE, pojoSessionDetailsResponseData.title);
        // values.put(CalendarContract.Events.DESCRIPTION, "Group workout");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
     /*   if(!pojoSessionDetailsResponseData.venues.isEmpty()){
            values.put(CalendarContract.Events.EVENT_LOCATION, pojoSessionDetailsResponseData.venues.get(0).venueName);
        }
*/
        String timezone = beginTime.getTimeZone().getID();
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timezone);

        @SuppressLint("MissingPermission")
        Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
        if (uri != null) {
            calenderEventID = Long.parseLong(uri.getLastPathSegment());
            calenderEventMap = preferenceUtils.getAttendingSessionCalenderIds();
            calenderEventMap.put("121", calenderEventID);
            preferenceUtils.saveAttendingSessionCalenderIds(calenderEventMap);
        }

    }
}
