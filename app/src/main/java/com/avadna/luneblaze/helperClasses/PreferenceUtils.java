package com.avadna.luneblaze.helperClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.avadna.luneblaze.pojo.PojoGetNotificationListResponseData;
import com.avadna.luneblaze.pojo.PojoLoginResponseData;
import com.avadna.luneblaze.pojo.assessment.PojoAssessmentCategory;
import com.avadna.luneblaze.pojo.pojoChat.PojoChatMessage;
import com.avadna.luneblaze.pojo.pojoChat.PojoConversationListItem;
import com.avadna.luneblaze.pojo.pojoInterestHierarchy.PojoAllInterestListItem;
import com.avadna.luneblaze.pojo.pojoNewsFeed.PojoGetNewsFeedResponseData;
import com.avadna.luneblaze.pojo.pojoProfileInfo.PojoProfileInfoResponseData;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.avadna.luneblaze.pojo.pojoSettings.PojoUserSettingsResponseData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by Sunny on 16-03-2018.
 */

public class PreferenceUtils {
    Context context;
    public SharedPreferences prefs;
    ProgressDialog dialog;
    Gson gson;

    int MAX_NOTIFICATION_COUNT = 40; //max number of notifications stored in prefs
    int MAX_MESSAGE_COUNT = 100; //max number of chat messages stored in prefs

    public PreferenceUtils(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(AppKeys.PREF_NAME, 0);
        gson = new Gson();
    }

    public void logOut() {
        prefs.edit().clear().commit();
        // (Activity(context)).finish();
    }

    public boolean showAppTour() {
        return prefs.getBoolean(AppKeys.SHOW_APP_TOUR_PREF_KEY, true);
    }

    public String getLocaleSettings() {
        return prefs.getString(AppKeys.LOCALE_SETTINGS, "en");
    }


    public void setLocaleSettings(String locale) {
        prefs.edit().putString(AppKeys.LOCALE_SETTINGS, locale).commit();
    }

    public void setAppTourStatus(boolean status) {
        prefs.edit().putBoolean(AppKeys.SHOW_APP_TOUR_PREF_KEY, status).commit();
    }

    public void saveUserProfileInfo(PojoProfileInfoResponseData pojoProfileInfoResponseData) {
        String listString = gson.toJson(
                pojoProfileInfoResponseData,
                new TypeToken<PojoProfileInfoResponseData>() {
                }.getType());
        prefs.edit().putString(AppKeys.USER_PROFILE_PREF_KEY, listString).commit();
    }

    public PojoProfileInfoResponseData getUserProfileInfo() {
        Type type = new TypeToken<PojoProfileInfoResponseData>() {
        }.getType();
        String str = prefs.getString(AppKeys.USER_PROFILE_PREF_KEY, "");
        if (str.isEmpty()) {
            return null;
        } else {
            return gson.fromJson(str, type);
        }
    }

    public void saveUserSettings(PojoUserSettingsResponseData pojoUserSettingsResponseData) {

        String listString = gson.toJson(
                pojoUserSettingsResponseData,
                new TypeToken<PojoUserSettingsResponseData>() {
                }.getType());
        prefs.edit().putString(AppKeys.USER_SETTINGS_KEY, listString).commit();
    }


    public PojoUserSettingsResponseData getUserSettings() {
        Type type = new TypeToken<PojoUserSettingsResponseData>() {
        }.getType();
        String str = prefs.getString(AppKeys.USER_SETTINGS_KEY, "");
        if (str.isEmpty()) {
            return null;
        } else {
            return gson.fromJson(str, type);
        }
    }


    public void saveSoundSettings(boolean status) {
        prefs.edit().putBoolean(AppKeys.APP_SOUND_SETTING, status).commit();
        CommonFunctions commonFunctions = new CommonFunctions(context);
        commonFunctions.createNotificationChannel();
    }


    public boolean getSoundSettings() {
        return prefs.getBoolean(AppKeys.APP_SOUND_SETTING, true);
    }


    public void saveSmsHash(String hash) {
        prefs.edit().putString("sms_hash", hash).commit();
    }

    public String getSmsHash() {
        return prefs.getString("sms_hash", "");
    }


    public void saveVibrationSettings(boolean status) {
        prefs.edit().putBoolean(AppKeys.APP_VIBRATION_SETTING, status).commit();
        CommonFunctions commonFunctions = new CommonFunctions(context);
        commonFunctions.createNotificationChannel();
    }

    public boolean getVibrationSettings() {
        return prefs.getBoolean(AppKeys.APP_VIBRATION_SETTING, true);
    }

    public void saveUserCurrentLocation(MyLocationObject location) {
        String listString = gson.toJson(
                location,
                new TypeToken<MyLocationObject>() {
                }.getType());
        prefs.edit().putString(AppKeys.USER_LOCATION_KEY, listString).commit();
    }

    public MyLocationObject getUserCurrentLocation() {
        Type type = new TypeToken<MyLocationObject>() {
        }.getType();
        String str = prefs.getString(AppKeys.USER_LOCATION_KEY, "");
        if (str.isEmpty()) {
            return null;
        } else {
            return gson.fromJson(str, type);
        }
    }

    public void saveUserLoginData(PojoLoginResponseData pojoLoginResponseData) {
        String listString = gson.toJson(
                pojoLoginResponseData,
                new TypeToken<PojoLoginResponseData>() {
                }.getType());
        prefs.edit().putString(AppKeys.USER_DATA_KEY, listString).commit();
    }

    public PojoLoginResponseData getUserLoginData() {
        Type type = new TypeToken<PojoLoginResponseData>() {
        }.getType();
        String str = prefs.getString(AppKeys.USER_DATA_KEY, "");
        if (str.isEmpty()) {
            return null;
        } else {
            return gson.fromJson(str, type);
        }
    }

    public void saveAttendingSessionCalenderIds(HashMap<String, Long> eventList) {
        String listString = gson.toJson(
                eventList,
                new TypeToken<HashMap<String, Long>>() {
                }.getType());
        prefs.edit().putString(AppKeys.SESSION_CALENDER_KEY, listString).commit();
    }

    public HashMap<String, Long> getAttendingSessionCalenderIds() {
        Type type = new TypeToken<HashMap<String, Long>>() {
        }.getType();
        String str = prefs.getString(AppKeys.SESSION_CALENDER_KEY, "");
        if (str.isEmpty()) {
            return new HashMap<String, Long>();
        } else {
            return gson.fromJson(str, type);
        }
    }


    public void saveChatImagesMap(HashMap<String, String> imgMap) {
        String listString = gson.toJson(
                imgMap,
                new TypeToken<HashMap<String, String>>() {
                }.getType());
        prefs.edit().putString(AppKeys.CHAT_IMAGE_MAP, listString).commit();
    }

    public HashMap<String, String> getChatImagesMap() {
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        String str = prefs.getString(AppKeys.CHAT_IMAGE_MAP, "");
        if (str.isEmpty()) {
            return new HashMap<String, String>();
        } else {
            return gson.fromJson(str, type);
        }
    }


    public void saveTempMessageIdCounter(int counter) {
        if (counter >= 3200000) {
            counter = 0;
        }
        prefs.edit().putInt(AppKeys.MESSAGE_TEMP_ID, counter).commit();
    }

    public int getTempMessageIdCounter() {
        return prefs.getInt(AppKeys.MESSAGE_TEMP_ID, 0);
    }

    public void saveTempConvMessage(String cid, String message) {
        if (cid != null && !cid.isEmpty()) {
            prefs.edit().putString(AppKeys.LAST_UNSENT_TEMP_MESSAGE + cid, message).commit();
        }
    }

    public String getTempConvMessage(String cid) {
        if (cid != null && !cid.isEmpty()) {
            return prefs.getString(AppKeys.LAST_UNSENT_TEMP_MESSAGE + cid, "");
        } else {
            return "";
        }
    }

    public void saveConversation(String cid, List<PojoChatMessage> msgList) {
        if (msgList.size() > MAX_MESSAGE_COUNT) {
            msgList = msgList.subList(0, MAX_MESSAGE_COUNT - 2);
        }

        String listString = gson.toJson(
                msgList,
                new TypeToken<List<PojoChatMessage>>() {
                }.getType());
        if (!cid.isEmpty()) {
            prefs.edit().putString(AppKeys.CONVERSATION_KEY + cid, listString).commit();

        }
    }

    public List<PojoChatMessage> getConversation(String cid) {
        Type type = new TypeToken<List<PojoChatMessage>>() {
        }.getType();
        String str = prefs.getString(AppKeys.CONVERSATION_KEY + cid, "");
        if (str.isEmpty() || cid.isEmpty()) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(str, type);
        }
    }

    //below two methods are used o keep track of count of unread conversations
    public void saveUnreadConversationsMap(HashMap<String, String> cList) {
        String data = gson.toJson(cList, new TypeToken<HashMap<String, String>>() {
        }.getType());
        prefs.edit().putString(AppKeys.UNREAD_CONVERSATIONS_MAP, data).commit();
    }

    public HashMap<String, String> getUnreadConversationsMap() {
        String data = prefs.getString(AppKeys.UNREAD_CONVERSATIONS_MAP, "");
        if (data.isEmpty()) {
            return new HashMap<>();
        } else {
            return gson.fromJson(data, new TypeToken<HashMap<String, String>>() {
            }.getType());
        }
    }

    //below two methods are used o keep track of unread messages count in each conversation
    public void saveUnreadConversations(HashMap<String, Integer> cList) {
        String data = gson.toJson(cList, new TypeToken<HashMap<String, Integer>>() {
        }.getType());
        prefs.edit().putString(AppKeys.UNREAD_CONVERSATIONS, data).commit();
    }


    public HashMap<String, Integer> getUnreadConversations() {
        String data = prefs.getString(AppKeys.UNREAD_CONVERSATIONS, "");
        if (data.isEmpty()) {
            return new HashMap<>();
        } else {
            return gson.fromJson(data, new TypeToken<HashMap<String, Integer>>() {
            }.getType());
        }
    }

    public void saveFcmMessageList(List<String> messages, String cid) {
        String data = gson.toJson(messages, new TypeToken<List<String>>() {
        }.getType());
        prefs.edit().putString(cid, data).commit();
    }

    public List<String> getFcmMessageList(String cid) {
        String data = prefs.getString(cid, "");
        if (data.isEmpty()) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(data, new TypeToken<List<String>>() {
            }.getType());
        }
    }

    public void removeFcmMessageList(String cid) {
        prefs.edit().remove(cid).commit();
    }


    //this map is used to keep track of notifications related to messages currently shown in the notification drawe
    public void saveAppMessageNotificationMap(HashMap<Integer, String> map) {
        String data = gson.toJson(map, new TypeToken<HashMap<Integer, String>>() {
        }.getType());
        prefs.edit().putString(AppKeys.MESSAGE_NOTIFICATION_MAP_KEY, data).commit();
    }

    public HashMap<Integer, String> getAppMessageNotificationMap() {
        String data = prefs.getString(AppKeys.MESSAGE_NOTIFICATION_MAP_KEY, "");
        if (data.isEmpty()) {
            return new HashMap<Integer, String>();
        } else {
            return gson.fromJson(data, new TypeToken<HashMap<Integer, String>>() {
            }.getType());
        }
    }


    //below two methods are used to keep track of which of my conversations have been seen by others
    public void saveSeenConversations(HashMap<String, String> cList) {
        String data = gson.toJson(cList, new TypeToken<HashMap<String, String>>() {
        }.getType());
        prefs.edit().putString(AppKeys.SEEN_CONVERSATIONS, data).commit();
    }

    public HashMap<String, String> getSeenConversations() {
        String data = prefs.getString(AppKeys.SEEN_CONVERSATIONS, "");
        if (data.isEmpty()) {
            return new HashMap<>();
        } else {
            return gson.fromJson(data, new TypeToken<HashMap<String, String>>() {
            }.getType());
        }
    }


    public void saveUnsentConversationsMap(HashMap<String, Integer> cidMap) {
        String listString = gson.toJson(cidMap,
                new TypeToken<HashMap<String, Integer>>() {
                }.getType());
        prefs.edit().putString(AppKeys.UNSENT_CONV_KEY, listString).commit();
    }

    public HashMap<String, Integer> getUnsentConversationsMap() {
        Type type = new TypeToken<HashMap<String, Integer>>() {
        }.getType();
        String str = prefs.getString(AppKeys.UNSENT_CONV_KEY, "");
        if (str.isEmpty()) {
            return new HashMap<String, Integer>();
        } else {
            return gson.fromJson(str, type);
        }
    }

    public void saveAllMessageList(List<PojoConversationListItem> msgList) {
        String listString = gson.toJson(msgList,
                new TypeToken<List<PojoConversationListItem>>() {
                }.getType());
        prefs.edit().putString(AppKeys.ALL_MSG_KEY, listString).apply();
    }

    public List<PojoConversationListItem> getAllMessageList() {
        Type type = new TypeToken<List<PojoConversationListItem>>() {
        }.getType();
        String str = prefs.getString(AppKeys.ALL_MSG_KEY, "");
        if (str.isEmpty()) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(str, type);
        }
    }

    public void saveNewsFeedList(List<PojoGetNewsFeedResponseData> feedList) {
        String listString = gson.toJson(
                feedList,
                new TypeToken<List<PojoGetNewsFeedResponseData>>() {
                }.getType());
        prefs.edit().putString(AppKeys.NEWS_FEED_PREF_KEY, listString).apply();
    }

    public List<PojoGetNewsFeedResponseData> getNewsFeedList() {
        Type type = new TypeToken<List<PojoGetNewsFeedResponseData>>() {
        }.getType();
        String str = prefs.getString(AppKeys.NEWS_FEED_PREF_KEY, "");
        if (str.isEmpty()) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(str, type);
        }
    }

    public void saveNewsFeedLastDate(Date date) {
        if (date == null) {
            prefs.edit().putString(AppKeys.NEWS_FEED_LAST_DATE, null).commit();
        } else {
            String listString = gson.toJson(date, new TypeToken<Date>() {
            }.getType());
            prefs.edit().putString(AppKeys.NEWS_FEED_LAST_DATE, listString).commit();
        }
    }

    public Date getNewsFeedLastDate() {
        Type type = new TypeToken<Date>() {
        }.getType();
        String str = prefs.getString(AppKeys.NEWS_FEED_LAST_DATE, "");
        if (str.isEmpty()) {
            return null;
        } else {
            return gson.fromJson(str, type);
        }
    }


    public void saveSearchPostLastDate(Date date) {
        if (date == null) {
            prefs.edit().putString(AppKeys.SEARCH_POST_LAST_DATE, null).commit();
        } else {
            String listString = gson.toJson(date, new TypeToken<Date>() {
            }.getType());
            prefs.edit().putString(AppKeys.SEARCH_POST_LAST_DATE, listString).commit();
        }
    }

    public Date getSearchPostLastDate() {
        Type type = new TypeToken<Date>() {
        }.getType();
        String str = prefs.getString(AppKeys.SEARCH_POST_LAST_DATE, "");
        if (str.isEmpty()) {
            return null;
        } else {
            return gson.fromJson(str, type);
        }
    }


    public void saveAllInterestHierarchyList(List<PojoGetInterestListResponseDataListItem> allInterestList) {
        String listString = gson.toJson(
                allInterestList,
                new TypeToken<List<PojoGetInterestListResponseDataListItem>>() {}.getType());
        if (listString != null && !listString.isEmpty()) {
            prefs.edit().putString(AppKeys.ALL_INTEREST_HIERARCHY_LIST, listString).apply();
        }
    }

    public List<PojoGetInterestListResponseDataListItem> getAllInterestHierarchyList() {
        Type type = new TypeToken<List<PojoGetInterestListResponseDataListItem>>() {
        }.getType();
        String str = prefs.getString(AppKeys.ALL_INTEREST_HIERARCHY_LIST, "");
        if (str.equals("null") || str.isEmpty()) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(str, type);
        }
    }


    public void saveAssessmentCategoriesList(List<PojoAssessmentCategory> categories) {
        String listString = gson.toJson(
                categories,
                new TypeToken<List<PojoAssessmentCategory>>() {
                }.getType());
        if (listString != null && !listString.isEmpty()) {
            prefs.edit().putString(AppKeys.ASSESSMENT_CATEGORIES_LIST, listString).apply();
        }
    }

    public List<PojoAssessmentCategory> getAssessmentCategoriesList() {
        Type type = new TypeToken<List<PojoAssessmentCategory>>() {
        }.getType();
        String str = prefs.getString(AppKeys.ASSESSMENT_CATEGORIES_LIST, "");
        if (str.equals("null") || str.isEmpty()) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(str, type);
        }
    }

    public void saveSuggestedUserList(List<PojoUserData> userList) {
        String listString = gson.toJson(
                userList,
                new TypeToken<List<PojoUserData>>() {
                }.getType());
        prefs.edit().putString(AppKeys.SUGGESTED_USER_PREF_KEY, listString).apply();
    }

    public List<PojoUserData> getSuggestedUserList() {
        Type type = new TypeToken<List<PojoUserData>>() {
        }.getType();
        String str = prefs.getString(AppKeys.SUGGESTED_USER_PREF_KEY, "");
        if (str.isEmpty()) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(str, type);
        }

    }

    public void saveNotificationList(List<PojoGetNotificationListResponseData> notifList) {
        if (notifList.size() > MAX_NOTIFICATION_COUNT) {
            notifList = notifList.subList(0, MAX_NOTIFICATION_COUNT - 2);
        }

        String listString = gson.toJson(notifList,
                new TypeToken<List<PojoGetNotificationListResponseData>>() {
                }.getType());
        prefs.edit().putString(AppKeys.SAVE_NOTIFICATION_KEY, listString).apply();
    }

    public List<PojoGetNotificationListResponseData> getNotificationList() {
        Type type = new TypeToken<List<PojoGetNotificationListResponseData>>() {
        }.getType();
        String str = prefs.getString(AppKeys.SAVE_NOTIFICATION_KEY, "");
        if (str.isEmpty()) {
            return new ArrayList<>();
        } else {
            return gson.fromJson(str, type);
        }
    }

    public void saveNotificationIdMap(HashMap<String, String> notifIdMap) {

        String listString = gson.toJson(notifIdMap,
                new TypeToken<HashMap<String, String>>() {
                }.getType());
        prefs.edit().putString(AppKeys.SAVE_NOTIFICATION_ID_MAP_KEY, listString).commit();
    }

    public HashMap<String, String> getNotificationIdMap() {
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        String str = prefs.getString(AppKeys.SAVE_NOTIFICATION_ID_MAP_KEY, "");
        if (str.isEmpty()) {
            return new HashMap<String, String>();
        } else {
            return gson.fromJson(str, type);
        }
    }


    public void saveNewNotificationCount(int num) {
        prefs.edit().putInt(AppKeys.NOTIFICATION_COUNT_KEY, num).commit();

    }

    public int getNewNotificationCount() {
        return prefs.getInt(AppKeys.NOTIFICATION_COUNT_KEY, 0);

    }


    public void saveUnseenConvCount(int num) {
        prefs.edit().putInt(AppKeys.UNSEEN_CONV_COUNT_KEY, num).commit();

    }

    public int getUnseenConvCount() {
        return prefs.getInt(AppKeys.UNSEEN_CONV_COUNT_KEY, 0);

    }


    //this one is used to show badge on request tab on home page
    public void saveNewFriendReqCount(int num) {
        prefs.edit().putInt(AppKeys.FRIEND_REQ_COUNT_KEY, num).commit();
    }

    public int getNewFriendReqCount() {
        return prefs.getInt(AppKeys.FRIEND_REQ_COUNT_KEY, 0);
    }

    public void saveSentRequestCount(int num) {
        prefs.edit().putInt(AppKeys.SENT_REQ_COUNT_KEY, num).commit();
    }

    public int getSentRequestCount() {
        return prefs.getInt(AppKeys.SENT_REQ_COUNT_KEY, 0);
    }

    public void saveReceivedRequestCount(int num) {
        prefs.edit().putInt(AppKeys.RECEIVED_REQ_COUNT_KEY, num).commit();
    }

    public int getReceivedRequestCount() {
        return prefs.getInt(AppKeys.RECEIVED_REQ_COUNT_KEY, 0);
    }

    public void savePref(String key, String value) {
        prefs.edit().putString(key, value).commit();
    }

    public String getPref(String key) {
        return prefs.getString(key, "");
    }

    public String getSessionSerialize(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public void setSessionBool(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public void distroySession(String key) {
        prefs.edit().remove(key).apply();
    }

    public boolean getSessionBool(String key) {
        return prefs.getBoolean(key, false);
    }


    public String get_user_id() {
        return getPref(AppKeys.USER_ID_KEY);
    }

    public String get_user_name() {
        return getPref(AppKeys.USER_NAME_KEY);
    }

    public String get_user_type() {
        return getPref(AppKeys.USER_TYPE_KEY);
    }

    public boolean is_user_login() {
        String key = getPref(AppKeys.USER_ID_KEY);
        if (key == null || key.equalsIgnoreCase("")) {
            return false;
        } else {
            return true;
        }
    }

    public String get_user_data(String key) {
        if (is_user_login()) {
            try {
                JSONObject jsonObject = new JSONObject(getPref(AppKeys.USER_INFO_KEY));
                if (jsonObject.has(key)) {
                    return jsonObject.getString(key);
                } else {
                    return "";
                }

            } catch (JSONException e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";
    }


}
