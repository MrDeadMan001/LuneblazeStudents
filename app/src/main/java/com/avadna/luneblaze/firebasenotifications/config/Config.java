package com.avadna.luneblaze.firebasenotifications.config;

import android.content.Intent;

/**
 * Created by Sunny on 13-03-2018.
 */

public class Config {

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "lb_firebase";

    public static final String NEW_MESSAGE_FCM = "new_msg_fcm";
    public static final String NEW_NOTIFICATION = "new_notif";
    public static final String NEW_FRIEND_REQUEST = "new_friend_req";

    public static final String MESSAGE_ADDED = "new_message_added";
    public static final String MESSAGE_SENT = "message_sent";
    public static final String MESSAGE_FAILED = "message_failed";
    public static final String QUESTION_CHANGED = "question_changed";
    public static final String NEW_VENUE_ADDED = "new_venue_added";
    public static final String POLL_DATE_UNSELECTED = "poll_date_unselected";

    public static final String CONVERSATION_DELETED = "conversation_deleted";
    public static final String SEARCH_STRING_UPDATE = "search_string_update";
    public static final String OPEN_SESSION_FILTER = "open_session_filter";
    public static final String OPEN_QUESTION_FILTER = "open_question_filter";
    public static final String OPEN_ARTICLE_FILTER = "open_article_filter";
    public static final String OPEN_VENUE_FILTER = "open_venue_filter";


    public static final String OPEN_ADD_QUESTION_DIALOG = "open_add_question_dialog";
    public static final String SESSION_FINALISED = "session_finalised";
    public static final String MESSAGE_SEEN = "message_seen";
    public static final String NEW_MESSAGE_SENT = "new_message_sent";
    public static final String REFRESH_CONV_LIST_FROM_PREFS = "refresh_list_from_prefs";
    public static final String GROUP_UPDATE_BROADCAST = "group_update_broadcast";
    public static final String OPEN_COMMENT_DIALOG = "open_comment_dialog";
    public static final String PROFILE_UPDATED = "profile_updated";
    public static final String SESSION_CREATION_FINISHED = "session_creation_finished";
    public static final String SESSION_DETAILS_UPDATED = "session_details_updated";
    public static final String OPEN_POST_FILTER = "open_post_filter";
    public static final String OPEN_VERIFIED_SESSION_FILTER = "open_verified_session_filter";
    public static final String OPEN_ASSIGNED_SESSION_FILTER = "open_assigned_session_filter";
    public static final String OPEN_ORGANISED_SESSION_FILTER = "open_organised_session_filter";

    public static final String NEW_FEED_ITEM = "new_feed_item";
    public static final String OPEN_ALL_SESSION_FILTER = "open_all_session_filter";
    public static final String OPEN_NEWS_FEED_FILTER = "open_news_feed_filter";
    public static final String SESSION_FILTER_UPDATED = "session_filter_updated";
    public static final String ARTICLE_COMMENT_DELETED ="article_comment_deleted" ;
    public static final String POST_COMMENT_DELETED = "post_comment_deleted";
    public static final String ANSWER_DELETED = "answer_deleted";
    public static final String SESSION_COMMENT_DELETED = "session_comment_deleted";
    public static final String SESSION_DISCUSSION_DELETED = "session_discussion_deleted";
    public static final String POLL_EDITED = "poll_edited";
    public static final String ADD_NEWS_FEED_ITEM = "add_news_feed_item";
    public static final String NOTIFICATION_SEEN = "notification_seen";
    public static final String QUESTION_DELETED = "question_deleted";
    public static final String SESSION_RELOAD = "session_reload";
    public static final String GROUP_CREATED = "group_created";
    public static final String MESSAGE_FORWARDED = "message_forwarded";
    public static final String FINISH_LOGIN_ACTIVITY = "finish_login_activity";
    public static final String EDIT_NEWS_FEED_ITEM = "edit_news_feed_item";
    public static final String VENUE_PHOTOS_UPDATED = "venue_photos_updated";
    public static final String SESSION_ATTENDED_STATUS = "session_attend_status";
    public static final String ORGANISATION_UPDATED = "organisation_updated";
    public static final String NEW_CAMPUS_DRIVE_ADDED = "new_campus_drive_added";
    public static final String UPDATE_ORGANISATION_AD_LIST = "update_organisation_ad_list";
    public static final String WORK_HISTORY_UPDATED = "work_history_updated";
    public static final String UPLOAD_COMPLETE = "upload_complete";
    public static final String UPLOAD_FAILED = "upload_failed";
    public static final String SESSION_ENDED = "session_ended";
}
