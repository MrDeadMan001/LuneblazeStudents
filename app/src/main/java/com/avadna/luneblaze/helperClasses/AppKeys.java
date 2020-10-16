package com.avadna.luneblaze.helperClasses;

import android.os.Environment;

/**
 * Created by Sunny on 16-03-2018.
 */

//encrypted keys are AES encrypted  using BASE64.DEFAULT need to encrypt with BASE64.URL_SAFE
public class AppKeys {

    public static final int REQUEST_CODE_READ_SMS = 100;
    public static final int CONTACTS_READ_PERMISSION = 101;
    public static final int STORAGE_READ_PERMISSION = 102;
    public static final int STORAGE_WRITE_PERMISSION = 103;
    public static final int READ_WRITE_CALENDER = 104;
    public static final int ADD_TO_CALENDER = 105;
    public static final int REMOVE_FROM_CALENDER = 106;
    public static final int GET_LOCATION_PERMISSION = 107;

    /*public static final int API_TIMEOUT_SHORT = 60;
    public static final int API_TIMEOUT_MEDIUM = 90;
    public static final int API_TIMEOUT_LONG = 120;*/

    //todo revert back to original when releasing
    public static final int API_TIMEOUT_SHORT = 300;
    public static final int API_TIMEOUT_MEDIUM = 300;
    public static final int API_TIMEOUT_LONG = 300;


    public static final String GUEST_USER_ID = "1";
    public static final String PREF_NAME = "lune_prefs";
    public static final String USER_ID_KEY = "user_id";
    public static final String USER_NAME_KEY = "user_name";

    public static final String USER_TYPE_KEY = "user_type";
    public static final String USER_INFO_KEY = "user_info";

    public static final String SHARED_TEXT = "shared_text";
    public static final String SHARED_IMAGE = "shared_image";
    public static final String SHARE_USER = "share_user";
    public static final String SHOW_APP_TOUR_PREF_KEY = "has_app_run_before";
    //public static final String BASE_URL = "http://strotam.com/api/";
    public static final String BASE_URL = "https://www.luneblaze.com/fzXJ2cLG8aB9Yv0YGmu6w/api/";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String LOCALE_SETTINGS = "locale_settings";
    public static final String enKey = "LuneblazeAesKey1";

    public static final String LANGUAGE_CHANGED = "language_changed";
    public static final String ADD = "add";
    public static final String SUBTRACT = "subtract";
    public static final String COUNT_CHANGED = "count_changed";
    public static final String CREATE = "create";
    public static final String EDIT = "edit";
    public static final String VERIFIED_SESSION = "verified_session";
    public static final String OWNER_ID = "owner_id";
    public static final String BOOST_POST = "boost_post";
    public static final String PHOTO_CLICK = "photo_click";
    public static final String NUM_VOTES = "num_voters";
    public static final String REPLY_COUNT = "num_replies";
    public static final String REPLY = "reply";
    public static final String COMMENT_REPLY = "comment_reply";
    public static final String DISCUSSION_REPLY = "discussion_reply";
    public static final String CAMPUS_DRIVE_DATA = "campus_drive_data";
    public static final String CAMPUS_DRIVE_PAYMENT = "campus_drive_payment";
    public static final String PAYMENT_TYPE = "payment_type";
    public static final String ORDER_ID = "order_id";
    public static final String TRANSACTION_ID = "transaction_id";
    public static final String PAYMENT_ID = "payment_id";
    public static final String PAYMENT_STATUS = "payment_status";
    public static final int SPONSOR_PLAN_SET = 5905;
    public static final String POST_BOOST_PAYMENT = "post_boost_payment";
    public static final int BOOST_COST_HIGH = 25;
    public static final int BOOST_COST_LOW = 20;
    public static final int AD_DAYS = 5;

    public static final String PRICE = "price";
    public static final String PAYMENT_REMARKS = "payment_remarks";
    public static final int BOOST_POST_REQUEST_CODE = 8002;
    public static final String GROUP_ID = "group_id";
    public static final String SESSION_RATING = "session_rating";
    public static final int RATING_NOTIFICATION = 4471;
    public static final long OTP_WAITING_TIME = 40000;
    public static final String SEARCH_POST_LAST_DATE = "search_post_last_date";
    public static final String ARTICLE_DELETED = "article_deleted";
    public static final String PATH = "path";
    public static final String ASSESSMENT_PAYMENT = "assessment_payment";
    public static final String ASSESSMENT_CATEGORIES_LIST = "assessment_categories";
    public static final String SELECT_CATEGORIES = "select_categories";
    public static final String DOMAIN = "domain";
    public static final String NON_DOMAIN = "non_domain";
    public static final String ANY = "any";
    public static final String ALL = "all";
    public static final String ABOVE = "above";
    public static final String UPLOAD_ID = "upload_id";
    public static final String TYPE = "type";
    public static final String TYPE_FILE = "file";
    public static final String TYPE_IMAGE = "image";
    public static final String TYPE_VIDEO = "video";
    public static final String UPLOADING = "uploading";
    public static final String UPLOAD_FINISH = "upload_finished";
    public static final String UPLOAD_FAILED = "upload_failed";
    public static final String ACTION_CANCEL_UPLOAD = "cancel_upload";
    public static final String ACTION_RETRY_UPLOAD = "retry_upload";
    public static final String ACTION_DELETE_PHOTO = "delete_photo";
    public static final String APPLY_DRIVE = "apply_drive";
    public static final String CANCEL_DRIVE_APPLY = "cancel_drive_apply";
    public static final String ELIGIBILITY_FILTERS = "eligibility_filters";
    public static final String ACTION_DOWNLOAD_FILE = "download_file";
    public static final String DRIVE_ORGANIZATION = "organization";
    public static final String DRIVE_COLLEGE = "college";
    public static final String PAYMENT_COMPLETED = "Completed";
    public static final String PAYMENT_FAILED = "fail";
    public static final int QUIZ_QUESTION_CHARACTER_LIMIT = 140;


    public static String otpSmsSuffix = " ngS71wnwItu"; //home
    // public static String otpSmsSuffix = " vOCBL1bHBMG"; //work
    public static final String PLACES_API_KEY = "wtTGLiB2kTtubFMZRUxWQmFbTsYWfmt4GW50AJXihLICUjInNVqCezDrP5rR9-EC";
    public static final String WEBSITE_URL = "https://www.luneblaze.com/";
    public static final String ALTERNATE_WEBSITE_URL = "http://strotam.com/";

    public static final String privacy_policy_link = "https://luneblaze.com/privacy.html";
    public static final String session_policy_link = "https://luneblaze.com/session_policy.html";
    public static final String terms_of_use_link = "https://luneblaze.com/terms_of_use.html";
    public static final String SMS_UNAME = "BUcIr8GlKn8BsgxupT535g==";
    public static final String SMS_PASS = "z3fOAxJpQWPA40XZgYtlWQ==";
    public static final String SMS_COMPANY_NAME = "LUNBLZ";
    public static final String SMS_COMPANY_NAME_SMS_FILTER = "IM-LUNBLZ";
    public static final String SMS_COMPANY_NAME_SECONDARY_SMS_FILTER = "IM-ANDCLC";

    public static final String API_AUTH_USERNAME = "euYzJaZBF7SdPJkqmY0eWw==";
    public static final String API_AUTH_PASSWORD = "gI4sODzrUiu2bdSE4ofsxQ==";

    public static final String RAZ_KEY_TEST = "_rIq_rnU_9ydVzJKLsXVR1rMXjlldRAGjZ12MUkgsMg=";
    public static final String RAZ_SEC_TEST = "F6-2QkE-0ICuLo1PuUsG3YhGueMwCP2BuwZakzpY0qo=";

    public static final String RAZ_KEY_LIVE = "2wo3cXNd0uI2JvE6-MgaAhqD3suKZNxmYhYpi5qZy7E=";
    public static final String RAZ_SEC_LIVE = "Qc6vIfFiaJdUa5VB3mXpde1l-w7vFarwl4sVa-JfNHo=";


    public static final String SENT_REQ_COUNT_KEY = "sent_request_count";
    public static final String RECEIVED_REQ_COUNT_KEY = "received_request_count";

    public static final String TEST_USER_ID = "97";
    public static final String UNSENT_CONV_KEY = "unsent_conv";
    public static final String ALL_MSG_KEY = "all_msg";

    public static final String CONVERSATION_KEY = "conversation";

    public static final int MESSAGE_STATE_SENT = 1;
    public static final int MESSAGE_STATE_UNSENT = 2;
    public static final int MESSAGE_STATE_FAILED = 3;

    public static final int MESSAGE_TYPE_USER = 0;
    public static final int MESSAGE_TYPE_SYSTEM = 1;
    public static final int MESSAGE_TYPE_TIME_DIVIDER = 2;

    public static final String NOTIFICATION_COUNT_KEY = "notification_count";
    public static final String FRIEND_REQ_COUNT_KEY = "friend_req_key";
    public static final String UNREAD_CONVERSATIONS = "unread_conversations";
    public static final String PUBLIC = "public";
    public static final String FRIENDS = "friends";
    public static final String ME = "me";


    public static final String CONDUCT_SESSION = "conduct_session";
    public static final String INVITE_SESSION = "invite_session";
    public static final String SESSION_POST_DELETE = "delete_session_post";
    public static final String INVITE_INTEREST = "invite_interest";
    public static final String PROFILE_IMG = "profile_img";
    public static final String COVER_IMG = "cover_img";

    public static final String VERIFIED = "verified";
    public static final String ORGANISED = "organised";

    public static final long SESSIONCALENDERID = 123;

    public static final String SESSION_CALENDER_KEY = "calender_event_id";
    public static final String CHAT_IMAGE_MAP = "chat_img_map";
    public static final String SAVE_NOTIFICATION_KEY = "save_notification";


    public static final int TYPE_EMAIL = 1;
    public static final int TYPE_PHONE = 2;

    public static final String USER_SETTINGS_KEY = "user_settings_key";
    public static final String USER_DATA_KEY = "user_data";


    //these type are to fetch the invitee list
    public static final String ANSWER_INVITATION_KEY = "ans_invitation_user_list";
    public static final String INTEREST_INVITATION_KEY = "interest_invitation";
    public static final String VENUE_INVITATION_KEY = "venue_invitation";
    public static final String SESSION_CONDUCT_INVITATION_KEY = "session_conduct_user_list";
    public static final String SESSION_ATTEND_INVITATION_KEY = "invite_session_user_list";

    public static final String USER_LOCATION_KEY = "user_location";

    public static final String CHANNEL_ID_SOUND_ON_VIBRATION_ON = "1729";
    public static final String CHANNEL_ID_SOUND_ON_VIBRATION_OFF = "1730";
    public static final String CHANNEL_ID_SOUND_OFF_VIBRATION_ON = "1731";
    public static final String CHANNEL_ID_SOUND_OFF_VIBRATION_OFF = "1732";

    public static final String[] channel_ids = {"InvitesId", "MessagesId", "SessionsId", "QnAId", "ArticlesId", "ActivitiesId", "MassNotifId", "OrganisationId"};

    public static final String SEEN_CONVERSATIONS = "seen_conversations";
    public static final String LIKE = "like";
    public static final String DELETE = "delete";
    public static final String REPORT = "report";
    public static final String HIDE = "hide";

    public static final String REPORT_SESSION = "report_session";
    public static final String REPORT_ARTICLE = "report_article";
    public static final String PIN_ARTICLE = "pin_article";
    public static final String SAVE_ARTICLE = "save_article";
    public static final String REPORT_POST = "report_post";
    public static final String ARTICLE_LIKE = "article_like";
    public static final String POST_LIKE = "post_like";
    public static final String COMMENT = "comment";
    public static final String DISCUSSION = "discussion";
    public static final String QUESTION = "question";
    public static final String POLL = "poll";
    public static final String ASK_QUESTION = "ask_question";
    public static final String ATTEND = "attend";
    public static final String UNATTEND = "unattend";
    public static final String THANK = "thank";
    public static final String QUESTION_UPVOTE = "question_upvote";
    public static final String SESSION_SHARE = "session_share";
    public static final String ARTICLE_SHARE = "article_share";
    public static final String QUESTION_SHARE = "question_share";
    public static final String VENUE_SHARE = "venue_share";
    public static final String INTEREST_SHARE = "interest_share";
    public static final String POST_SHARE = "post_share";
    public static final String INTEREST_FOLLOW = "interest_follow";
    public static final String VENUE_FOLLOW = "venue_follow";
    public static final String REPORT_QUESTION = "report_question";
    public static final String REPORT_INTEREST = "report_interest";
    public static final String REPORT_VENUE = "report_venue";
    public static final String COMMENT_ID = "comment_id";
    public static final String POST_ID = "post_id";
    public static final String ANSWER_ID = "answer_id";
    public static final String REPLY_ID = "reply_id";
    public static final String DISCUSSION_ID = "discussion_id";
    public static final String SESSION_ID = "session_id";
    public static final String ANSWER_SHARED = "answer_shared";
    public static final String SPONSOR = "sponsor";
    public static final String SPONSORSHIP_PLAN = "sponsorship_plan";


    public static final String SEND_VERIFICATION_REQUEST = "send_verification_request";
    public static final String CANCEL_VERIFICATION_REQUEST = "cancel_verification_request";
    public static final String ORIGIN_ARTICLE_SHARE = "origin_article_share";
    public static final String MAKE_PRIMARY = "make_primary";
    public static final String VERIFY = "verify";
    public static final String UPVOTER = "upvoter";
    public static final String VENUE_ID = "venue_id";
    public static final String INTEREST_ID = "interest_id";
    public static final String ORGANISATION_ID = "organisation_id";
    public static final String ORGANISATION_DATA = "organisation_data";

    public static final String QUESTION_FOLLOW = "question_follow";
    public static final String TARGET = "target";
    public static final String ADD_ANSWER = "add_answer";
    public static final String ID = "id";
    public static final String TARGET_USER_ID = "target_user_id";
    public static final String LIKE_REPLY = "like_reply";
    public static final String LIKE_COMMENT = "like_comment";
    public static final String REPORT_ANSWER = "report_answer";
    public static final String ADD_REPLY = "add_reply";
    public static final String LOAD_NEXT = "load_next";
    public static final String LOAD_PREVIOUS = "load_previous";
    public static final String ARTICLE_ID = "article_id";
    public static final String ADD_COMMENT = "add_comment";
    public static final String FINALISE_YES = "finalise_yes";
    public static final String FINALISE_NO = "finalise_no";
    public static final String NOTIFICATION_SEEN = "notification_seen";
    public static final String SESSION_CONDUCT = "session_conduct";
    public static final String SESSION_INITIATE = "session_initiate";
    public static final String ANSWER_UPVOTE = "answer_upvote";
    public static final String DATA = "data";
    public static final String MESSAGE = "message";
    public static final String BLOCK = "block";
    public static final String UNFOLLOW = "unfollow";
    public static final String PROFILE = "profile";
    public static final String UNFRIEND = "unfriend";
    public static final String TYPE_BLOCK = "block";
    public static final String TYPE_SESSION_BLOCK = "session_block";
    public static final String TYPE_FELLOW = "fellow";
    public static final String REMOVE = "remove";
    public static final String LIKE_COUNT = "like_count";

    public static final String CREATOR_ID = "creator_id";
    public static final String COMMENT_COUNT = "comment_count";
    public static final String SAVE_NOTIFICATION_ID_MAP_KEY = "notification_id_map_key";
    public static final String UNREAD_CONVERSATIONS_MAP = "unread_conversation_map";
    public static final String SHARE_TO_POST = "share_to_post";
    public static final String UNFOLLOW_USER = "unfollow_user";
    public static final String EDIT_POST = "edit_post";
    public static final String POST_DELETE = "post_deleted";
    public static final String USER_UNFOLLOW = "user_unfollow";
    public static final String HIDE_POST = "hide_post";
    public static final String UNHIDE_POST = "unhide_post";
    public static final String POLL_EDIT = "edit_poll";
    public static final String POLL_DELETE = "delete_poll";
    public static final String OPEN_SESSION = "open_session";
    public static final String OPEN_PROFILE = "open_profile";
    public static final String DISCUSSION_SESSION = "discussion_session";
    public static final String ACCEPT_SESSION = "session_accept";
    public static final String SESSION_ACTION = "session_action";
    public static final String OPEN_POST = "open_post";
    public static final String OPEN_POST_COMMENT = "open_post_comment";
    public static final String ADD_POST_COMMENT = "add_post_comment";
    public static final String OPEN_ARTICLE = "open_article";
    public static final String ARTICLE_COMMENT = "article_comment";
    public static final String ARTICLE_COMMENT_LIST = "article_comment_list";
    public static final String ANSWER_ACTION = "answer_action";
    public static final String OPEN_QUESTION = "open_question";
    public static final String OPEN_ORGANISATION = "open_organisation";
    public static final String OPEN_INTEREST = "open_interest";
    public static final String OPEN_VENUE = "open_venue";

    public static final String SHARE_SESSION = "share_session";
    public static final String SHARE_ARTICLE = "share_article";
    public static final String SHARE_INTEREST = "share_interest";
    public static final String SHARE_QUESTION = "share_question";
    public static final String SHARE_VENUE = "share_venue";
    public static final String SHARE_TEXT = "share_text";


    public static final String ACTIVITY_NAME = "activity_name";
    public static final String EDIT_ARTICLE = "edit_article";
    public static final String DELETE_ARTICLE = "delete_article";
    public static final String SHARE_TO_ARTICLE = "share_to_article";
    public static final String ARTICLE = "article";
    public static final String SESSION = "session";
    public static final String NORMAL = "normal";
    public static final String VENUE = "venue";
    public static final String SHARE_TO_SESSION = "share_to_session";
    public static final String EDIT_QUESTION = "edit_question";
    public static final String DELETE_QUESTION = "delete_question";
    public static final String SHARE_TO_QUESTION = "share_to_question";
    public static final String SHARE_TO_VENUE = "share_to_venue";
    public static final String SHARE_TO_INTEREST = "share_to_interest";
    public static final String SELECT_INTEREST = "select_interest";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String SHARE_ANSWER = "share_answer";
    public static final String DELETE_ANSWER = "delete_answer";
    public static final String EDIT_ANSWER = "edit_answer";
    public static final String SHARE_TO_ANSWER = "share_to_answer";
    public static final String ANSWER_SHARE = "answer_share";
    public static final String REPORT_COMMENT = "report_comment";
    public static final String REPORT_REPLY = "report_reply";
    public static final String USER = "user";
    public static final String ORGANISATION = "organisation";
    public static final String POSTS = "posts";
    public static final String INTEREST = "interest";
    public static final int TOAST_DEBUG = 704571;
    public static final int TOAST_USER = 704572;

    public static final String APP_SOUND_SETTING = "app_sound_setting";
    public static final String APP_VIBRATION_SETTING = "app_vibration_setting";

    public static final String ATTEND_STATUS = "attend_status";
    public static final String USER_FOLLOW = "user_follow";
    public static final String QUESTION_ID = "question_id";
    public static final String FELLOW_REQUEST_CANCEL = "fellow_request_cancel";
    public static final String CHAT_MESSAGE_LONG_CLICK = "chat_message_long_click";
    public static final String CHAT_MESSAGE_CLICK = "chat_message_click";
    public static final String SELECTION_ENABLED = "selection_enabled";
    public static final String MESSAGE_IDS = "message_ids";
    public static final String URL = "url";
    public static final String NEWS_FEED_PREF_KEY = "news_feed_pref_list";
    public static final String USER_PROFILE_PREF_KEY = "user_profile_pref_key";
    public static final String MESSAGE_NOTIFICATION_MAP_KEY = "message_notification_map";
    public static final int MESSAGE_SUMMARY_ID = 1234;
    public static final int CREATE_POST_REQUEST_CODE = 9057;
    public static final String SUGGESTED_USER_PREF_KEY = "suggested_pref_users_list";
    public static final String EDIT_POST_TITLE = "edit_post_title";
    public static final String UNSEEN_CONV_COUNT_KEY = "unseen_conv_pref_key";

    public static final String MESSAGE_TEMP_ID = "message_temp_id";
    public static final int CREATE_POLL_REQUEST_CODE = 9011;
    public static final String CHAT_BLOCK_STATUS = "chat_block_status";
    public static final String SHARE_SESSION_GALLERY_IMAGE = "share_session_gallery_image";
    public static final String SESSION_NAME = "session_name";
    public static final String SESSION_PHOTO_URL = "session_photo_url";
    public static final int COMMENT_AND_REPLY_MAX_LENGTH = 500;
    public static final String LAST_UNSENT_TEMP_MESSAGE = "last_unsent_temp_message";
    public static final String NEWS_FEED_LAST_DATE = "news_feed_last_date";
    public static final String ALL_INTEREST_HIERARCHY_LIST = "all_interest_hierarchy_list";


    public static final String SHOW_KEYBOARD = "show_keyboard";

    public static final String USER_ID = "user_id";
    public static final String USER_NAME = "user_name";
    public static final String USER_FULL_NAME = "user_full_name";
    public static final String USER_PICTURE = "user_picture";
    public static final String USER_WORK = "user_work";
    public static final String USER_WORK_TITLE = "user_work_title";
    public static final String USER_WORK_PLACE = "user_work_place";
    public static final String USER_MUTUAL_FRIEND_COUNT = "user_mutual_friend_count";
    public static final String USER_CONNECTION = "user_connection";
    public static String notif_reply_key = "notif_reply_key";
    public static String ACTION = "action";

    public static final String TIME_OUT = "timeout";
    public static final String ANSWER_SELECTED = "answer_selected";
    public static final String POSITION = "position";




    //firebase analytics events
    public static final String APP_ACTIVE_DURATION_EVENT = "app_active_event";


    //firebase analytics params
    public static final String APP_ACTIVE_DURATION = "app_active_duration";


    //notification ids
    static int session_upvoted_reply = 1001;
    static int session_upvoted_answer = 1002;
    static int session_presentor_added_qnas = 1003;
    static int session_attende_added_qnas = 1004;
    static int session_added_discussion_reply = 1005;
    static int session_like_discussion_reply = 1006;
    static int session_added_comment_reply = 1007;
    static int session_like_comment_reply = 1008;
    static int session_like_comment = 1009;
    static int session_like_discussion = 1010;
    static int session_attend = 1011;
    static int session_thanks = 1012;

    static int session_photo_comment_like = 1013;
    static int session_vote = 1014;
    static int session_poll_venues_added = 1015;
    static int session_poll_date_added = 1016;
    static int remove_attendee = 1017;
    static int attend_invitation = 1018;
    static int venue_changed = 1019;
    static int active_venue_changed = 1020;
    static int entered_in_organized_initiator = 1021;
    static int entered_in_organized = 1022;
    static int entered_in_organized_thanks = 1023;
    static int attende_added_discussion_comment = 10024;
    static int friend_added_discussion_comment = 1025;
    static int session_verify = 1026;
    static int session_postpone = 1027;
    static int activate_session = 1028;
    static int session_venue = 1029;
    static int session_verify_initiator = 1030;
    static int session_assign_initiator = 1031;
    static int session_assign_sharer = 1032;
    static int session_assign_attendees = 1033;
    static int session_assign_interest = 1034;
    static int added_topics = 1035;
    static int session_added_comments = 1036;
    static int session_presentor_added_comments = 1037;
    static int session_attende_added_comments = 1038;
    static int session_ended = 1039;


    static int venue_teams = 1101;
    static int venue_follwer = 1102;
    static int venue_changed_confirmation = 1103;


    static int question_share_answer = 1201;
    static int session_qa_followers = 1202;
    static int added_qnas = 1203;
    static int added_answer = 1204;
    static int added_answers_reply = 1205;
    static int upvoted_question = 1206;


    static int article_like_discussion_reply = 1301;
    static int article_comment_reply = 1302;
    static int article_like = 1303;
    static int article_comment = 1304;
    static int article_reply = 1305;
    static int article_added = 1306;


    static int interest_approved = 1401;
    static int interest_declined = 1402;


    static int post_like = 1501;
    static int post_comment_like = 1502;
    static int post_comment_reply_like = 1503;
    static int post_comment = 1504;
    static int post_mention = 1505;
    static int post_reply = 1506;


    static int share = 1601;
    static int post_vote = 1602;

    static int photo_like = 1701;
    static int photo_comment_like = 1702;
    static int photo_comment = 1703;
    static int photo_reply = 1704;


}

