package com.avadna.luneblaze.helperClasses;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.text.Html;
import android.widget.Toast;

import com.avadna.luneblaze.R;
import com.avadna.luneblaze.activities.MainActivity;
import com.avadna.luneblaze.activities.WebViewActivity;
import com.avadna.luneblaze.activities.article.ArticleActivity;
import com.avadna.luneblaze.activities.article.ArticleCommentListActivity;
import com.avadna.luneblaze.activities.article.ArticleCommentReplyListActivity;
import com.avadna.luneblaze.activities.interest.InterestActivity;
import com.avadna.luneblaze.activities.normalpost.NormalPostActivity;
import com.avadna.luneblaze.activities.normalpost.PostCommentRepliesListActivity;
import com.avadna.luneblaze.activities.PostLoaderActivity;
import com.avadna.luneblaze.activities.organisation.OrganisationProfileInfoActivity;
import com.avadna.luneblaze.activities.polls.PollActivity;
import com.avadna.luneblaze.activities.profileInfo.ProfileInfoActivity;
import com.avadna.luneblaze.activities.question.AnswerReplyListActivity;
import com.avadna.luneblaze.activities.question.QuestionActivity;
import com.avadna.luneblaze.activities.sessions.SessionCommentRepliesListActivity;
import com.avadna.luneblaze.activities.sessions.SessionDiscussionRepliesListActivity;
import com.avadna.luneblaze.activities.sessions.SessionLoaderActivity;
import com.avadna.luneblaze.activities.venue.VenueActivity;
import com.avadna.luneblaze.activities.sessions.SessionCommentsListActivity;
import com.avadna.luneblaze.pojo.PojoGetNotificationListResponseData;
import com.avadna.luneblaze.pojo.PojoNoDataResponse;
import com.avadna.luneblaze.rest.ApiClientShortDuration;
import com.avadna.luneblaze.rest.ApiInterface;

import org.json.JSONObject;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class NotificationLoader {
    Context context;
    String user_id = "";
    String username = "";
    ApiInterface apiService;
    PreferenceUtils preferenceUtils;
    CommonFunctions commonFunctions;

    public NotificationLoader(Context context) {
        this.context = context;
        preferenceUtils = new PreferenceUtils(context);
        commonFunctions = new CommonFunctions(context);
        user_id = preferenceUtils.get_user_id();
        username = preferenceUtils.get_user_name();
        apiService = ApiClientShortDuration.getClient().create(ApiInterface.class);
    }

    public String openTarget(PojoGetNotificationListResponseData notificationListResponseData,
                             int targetType) {
        /*if(targetType!=1){
            hitNotificationSeenApi(notificationListResponseData.notificationId);
        }*/

        int a = 5;
        switch (notificationListResponseData.action) {
            case "mass_notification":
                String url = notificationListResponseData.nodeUrl;
                Intent intent;
                if (url.contains("luneblaze.com")) {
                    Uri uri = Uri.parse(url);
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                } else {
                    intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra(AppKeys.URL, url);
                }
                context.startActivity(intent);

                return notificationListResponseData.message;

            case "upvoted_reply":
                if (notificationListResponseData.nodeType.equals("session")) {
                    openAnswerReplyPage(notificationListResponseData, targetType, AppKeys.session_upvoted_reply);
                }

                return notificationListResponseData.message;

            case "upvoted_answer":
                if (notificationListResponseData.nodeType.equals("session")) {
                    openQuestionPage(notificationListResponseData, targetType, AppKeys.session_upvoted_answer);
                }
                return notificationListResponseData.message;

            case "presentor_added_qnas":
                if (notificationListResponseData.nodeType.equals("session")) {
                    openQuestionPage(notificationListResponseData, targetType, AppKeys.session_presentor_added_qnas);
                }
                return notificationListResponseData.message;

            case "join_added_qnas":
                if (notificationListResponseData.nodeType.equals("session")) {
                    openQuestionPage(notificationListResponseData, targetType, AppKeys.session_presentor_added_qnas);
                }
                return notificationListResponseData.message;

            case "attende_added_qnas":
                if (notificationListResponseData.nodeType.equals("session")) {
                    openQuestionPage(notificationListResponseData, targetType, AppKeys.session_attende_added_qnas);
                }
                return notificationListResponseData.message;

            case "share_answer":
                if (notificationListResponseData.nodeType.equals("question")) {
                    openQuestionPage(notificationListResponseData, targetType, AppKeys.question_share_answer);
                }
                return notificationListResponseData.message;

            case "added_discussion_reply":
                if (notificationListResponseData.nodeType.equals("session")) {
                    //  // "url" = $system "system_url". "/posts/". "node_url";
                    openDiscussionReplyActivity(notificationListResponseData, targetType, AppKeys.session_added_discussion_reply);

                }
                return notificationListResponseData.message;

            case "like_discussion_reply":
                if (notificationListResponseData.nodeType.equals("session")) {
                    //  // "url" = $system "system_url". "/posts/". "node_url";
                    openDiscussionReplyActivity(notificationListResponseData, targetType, AppKeys.session_like_discussion_reply);

                }
                if (notificationListResponseData.nodeType.equals("article")) {
                    //  // "url" = $system "system_url". "/posts/". "node_url";
                    openArticleCommentReply(notificationListResponseData, targetType, AppKeys.article_like_discussion_reply);

                }
                return notificationListResponseData.message;


            case "comment_reply":
                if (notificationListResponseData.nodeType.equals("article")) {
                    //  // "url" = $system "system_url". "/posts/". "node_url";
                    openArticleCommentReply(notificationListResponseData, targetType, AppKeys.article_comment_reply);

                }
                return notificationListResponseData.message;

            case "added_comment_reply":
                if (notificationListResponseData.nodeType.equals("session")) {
                    //  // "url" = $system "system_url". "/posts/". "node_url";
                    openSessionCommentReplyActivity(notificationListResponseData, targetType, AppKeys.session_added_comment_reply);

                }
                return notificationListResponseData.message;


            case "like_comment_reply":
                if (notificationListResponseData.nodeType.equals("session")) {
                    //  // "url" = $system "system_url". "/posts/". "node_url";
                    openSessionCommentReplyActivity(notificationListResponseData, targetType, AppKeys.session_like_comment_reply);
                }
                return notificationListResponseData.message;

            // break;

            case "like_comment":
                if (notificationListResponseData.nodeType.equals("session")) {
                    //  // "url" = $system "system_url". "/posts/". "node_url";
                    openSessionCommentActivity(notificationListResponseData, targetType, AppKeys.session_like_comment);

                }
                return notificationListResponseData.message;


            case "like_discussion":
                if (notificationListResponseData.nodeType.equals("session")) {
                    //  // "url" = $system "system_url". "/posts/". "node_url";
                    openSessionLoader(notificationListResponseData, "discussion", targetType, AppKeys.session_like_discussion);

                } else if (notificationListResponseData.nodeType.equals("article")) {
                    openArticleCommentPage(notificationListResponseData, targetType, AppKeys.article_comment);

                }
                return notificationListResponseData.message;

            case "session_attend":
            case "session_decline":
                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_attend);
                return notificationListResponseData.message;
            // break;

            case "session_thanks":
                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_thanks);
                return notificationListResponseData.message;
            // break;

            case "session_ended":
                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_ended);
                return notificationListResponseData.message;


            case "friend":
                openUserProfile(notificationListResponseData, targetType);
                return "Accepted your friend request";


            case "organization_approve":
                openOrganisationPage(notificationListResponseData, targetType);
                return "Organization_approved";

            // break;

            case "follow":
                openUserProfile(notificationListResponseData, targetType);
                return "Now following you";
            // break;


            case "tagged_user":
                openPostLoaderActivity(notificationListResponseData, targetType, AppKeys.post_like);
                return "";

            case "like":
                //// "icon" = "fa-thumbs-o-up";
                if (notificationListResponseData.nodeType.equals("post")) {
                    //// "url" = $system"system_url" . "/posts/" . "node_url";
                    openPostLoaderActivity(notificationListResponseData, targetType, AppKeys.post_like);
                    //  openPostScreen(notificationListResponseData, targetType);

                    return "Likes your post";
                } else if (notificationListResponseData.nodeType.equals("post_comment")) {
                    // // "url" = $system "system_url". "/posts/". "node_url";
                    openPostLoaderActivity(notificationListResponseData, targetType, AppKeys.post_comment_like);

                    //openPostScreen(notificationListResponseData, targetType);
                    return "Likes your comment";
                } else if (notificationListResponseData.nodeType.equals("post_comment_reply")) {
                    // // "url" = $system "system_url". "/posts/". "node_url";
                    openPostCommentReplyScreen(notificationListResponseData, targetType, AppKeys.post_comment_reply_like);
                    return "Likes your comment";
                } else if (notificationListResponseData.nodeType.equals("article")) {
                    // // "url" = $system "system_url". "/posts/". "node_url";
                    openArticlePage(notificationListResponseData, targetType, AppKeys.article_like);
                    return "Likes your comment";
                } else if (notificationListResponseData.nodeType.equals("photo")) {
                    // // "url" = $system "system_url". "/photos/". "node_url";
                    openPostLoaderActivity(notificationListResponseData, targetType, AppKeys.photo_like);

                    // openPostScreen(notificationListResponseData, targetType);
                    return "Likes your photo";
                } else if (notificationListResponseData.nodeType.equals("photo_comment")) {
                    // // "url" = $system "system_url". "/photos/". "node_url";
                    return "Likes your comment";
                } else if (notificationListResponseData.nodeType.equals("session_photo_comment")) {
                    // // "url" = $system "system_url". "/session/". "node_url". "/photos";
                    return "Likes your comment";
                }
                // break;

            case "comment":
                //// "icon" = "fa-comment";
                if (notificationListResponseData.nodeType.equals("post")) {
                    //  // "url" = $system "system_url". "/posts/". "node_url";
                    openPostLoaderActivity(notificationListResponseData, targetType, AppKeys.post_comment);

                    //  openPostScreen(notificationListResponseData, targetType);

                    return "Commented on your post";

                } else if (notificationListResponseData.nodeType.equals("photo")) {
                    //  // "url" = $system "system_url". "/photos/". "node_url";
                    openPostLoaderActivity(notificationListResponseData, targetType, AppKeys.photo_comment);

                    //  openPostScreen(notificationListResponseData, targetType);
                    return "Commented on your photo";
                } else if (notificationListResponseData.nodeType.equals("article")) {

                    openArticleCommentPage(notificationListResponseData, targetType, AppKeys.article_comment);

                    return "Commented on your article";

                    // }
                }
                // break;

            case "reply":
                //// "icon" = "fa-comment";
                if (notificationListResponseData.nodeType.equals("post")) {
                    openPostCommentReplyScreen(notificationListResponseData, targetType, AppKeys.post_reply);

                    return "Commented on your post";
                } else if (notificationListResponseData.nodeType.equals("photo")) {
                    openPostLoaderActivity(notificationListResponseData, targetType, AppKeys.photo_reply);

                    return "Commented on your photo";
                } else if (notificationListResponseData.nodeType.equals("article")) {
                    openArticleCommentReply(notificationListResponseData, targetType, AppKeys.article_reply);
                    return "replied to your comment on article";
                }
                // break;

            case "share":
                openPostLoaderActivity(notificationListResponseData, targetType, AppKeys.share);
                return "Shared your post";
            // break;

            case "vote":
                if (notificationListResponseData.nodeType.equals("post")) {
                    openPollScreen(notificationListResponseData, targetType, AppKeys.post_vote);
                    return "Voted on your poll";
                }
                if (notificationListResponseData.nodeType.equals("session")) {
                    openSessionLoader(notificationListResponseData, "poll", targetType, AppKeys.session_vote);
                    return "is going to attend your session ";
                }
                // break;

            case "venue_teams":
                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.venue_teams);
                return "Congratulations! Sharer has finalised your venue for session" + " Session name";
            // break;

            case "venue_follwer":
                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.venue_follwer);
                return "Congratulations! Sharer has finalised your venue for session" + " Session name";
            // break;

            case "session_poll_venues_added":
                openSessionLoader(notificationListResponseData, "poll", targetType, AppKeys.session_poll_venues_added);
                return notificationListResponseData.userFullname + " added new venues on discussion poll for session " + " Session name";
            // break;

            case "session_poll_date_added":
                openSessionLoader(notificationListResponseData, "poll", targetType, AppKeys.session_poll_date_added);
                return "New date event_date is added to the venue " + " venue name " + " in session " + " Session name";
            // break;

            case "venue_changed_confirmation":
                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.venue_changed_confirmation);
                return "Venue location, event_date and Time has been finalized for session " + "Session name" + " by " + notificationListResponseData.userFullname + "and are different from your choices. Do You Still want to attend the session?";
            // break;

            case "remove_attendee":
                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.remove_attendee);
                return "Session" + " Session name " + "has been organised successfully. Unfortunately you did not attend but you can also get in touch with it.";
            // break;

            case "mention":
                // "icon" = "fa-comment";
                if (notificationListResponseData.nodeType.equals("post")) {
                    // "url" = $system "system_url". "/posts/". "node_url";
                    return "Mentioned you in a post";
                } else if (notificationListResponseData.nodeType.equals("comment")) {
                    // "url" = $system "system_url". "/posts/". "node_url";
                    return "Mentioned you in a comment";
                } else if (notificationListResponseData.nodeType.equals("photo")) {
                    // "url" = $system "system_url". "/photos/". "node_url";
                    return "Mentioned you in a comment";
                } else if (notificationListResponseData.nodeType.equals("question")) {
                    // "url" = $system "system_url". "/question/". "node_url";
                    openQuestionPage(notificationListResponseData, targetType, AppKeys.session_upvoted_answer);
                    return "Invite you to give ans of a question";
                } else if (notificationListResponseData.nodeType.equals("interest")) {
                    // "url" = $system "system_url". "/interest/". "node_url";
                    openInterestPage(notificationListResponseData, targetType);
                    return "Invite you to follow a interest";
                } else if (notificationListResponseData.nodeType.equals("venue")) {
                    // "url" = $system "system_url". "/venue/". "node_url";
                    openVenuePage(notificationListResponseData, targetType);
                    return "Invite you to follow a venue";
                } else if (notificationListResponseData.nodeType.equals("session")) {
                    // "url" = $system "system_url". "/session/". "node_url";
                    openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_like_discussion);
                    return "Invite you to join a session";
                } else if (notificationListResponseData.nodeType.equals("session_conduct_request")) {
                    // "url" = $system "system_url". "/session/". "node_url";
                    openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_like_discussion);
                    return "Invite you to conduct a session";
                }
                // break;

            case "profile_visit":
                openUserProfile(notificationListResponseData, targetType);
                return "Visited your profile";
            // break;

            case "wall":
                return "Posted on your wall";
            // break;

            case "report_thanks":

                return "We have taken action against the content you have reported. Thank you for contributation.";
            // break;

            case "attend_invitation":

                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.attend_invitation);
                return "Congratulations, Your request for attending the session " + "Session name" + "is approved.";
            // break;

            case "venue_changed":

                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.venue_changed);
                return "Venue, Date and Time for the session " + "Session name" + " is finalised by Sharer";
            // break;

            case "active_venue_changed":

                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.active_venue_changed);
                return "Some data of the session " + "Session name" + "  is edited by admin";
            // break;

            case "entered_in_organized_initiator":

                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.entered_in_organized_initiator);
                return "Thank you very much for initiating the session" + " Session name";
            // break;

            case "entered_in_organized":

                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.entered_in_organized);
                return "Thank you very much for Attending " + "Session name" + " Hope You learnt the expected.";
            // break;

            case "entered_in_organized_thanks":

                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.entered_in_organized_thanks);
                return "Thank you very much for sharing in " + "Session name" + " You have done a great job.";
            // break;

            case "added_discussion_comment":
            case "attende_added_discussion_comment":

                openSessionLoader(notificationListResponseData, "discussion", targetType, AppKeys.attende_added_discussion_comment);
                return "Added a Query to Session " + "Session Name";
            // break;

            case "friend_added_discussion_comment":
                openSessionLoader(notificationListResponseData, "comment", targetType, AppKeys.friend_added_discussion_comment);
                return "Commented on session " + "Session Name";
            // break;

            case "session_verify":
                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_verify);
                return "A Session " + "Session Name" + " of your interest is created.";
            // break;

            case "session_postpone":
                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_postpone);
                return "Session " + "Session Name" + " postponed by Sharer";
            // break;

            case "activate_session":
                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.activate_session);
                return "Session " + "Session Name" + " has proposed a new date by Sharer";
            // break;

            case "session_venue":
                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_venue);
                return "A Session " + "Session Name" + " is created at venue you followed.";
            // break;

            case "session_qa_followers":
                openQuestionPage(notificationListResponseData, targetType, AppKeys.session_qa_followers);
                return "added a new answer for a question you followed.";
            // break;

            case "article_added":

                openArticlePage(notificationListResponseData, targetType, AppKeys.article_added);
                return "An Article " + "Session Name" + " of your interest is created.";
            // break;

            case "session_verify_initiator":

                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_verify_initiator);
                return "";

            case "session_assign_initiator":

                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_assign_initiator);
                return "Congratulations " + notificationListResponseData.userFullname + " has Accepted your Request.";

            case "session_assign_sharer":

                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_assign_sharer);
                return "Congratulations, You request for Sharing the Session " + "Session Name" + " is accepted.";

            case "session_assign_attendees":

                openSessionLoader(notificationListResponseData, "comments", targetType, AppKeys.session_assign_attendees);
                return "Congratulations " + notificationListResponseData.userFullname + " has Accepted, you can discuss further in Discussion Forum.";

            case "session_assign_interest":

                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.session_assign_interest);
                return "A Session of your interest is created.";
            // break;

            case "added_topics":

                openSessionLoader(notificationListResponseData, "details", targetType, AppKeys.added_topics);
                return "Sharer added some more topics for session " + "Session Name";
            // break;

            case "added_comments":
            case "presentor_added_comments":
            case "attende_added_comments":

                if (notificationListResponseData.nodeType.equals("session")) {
                    openSessionCommentActivity(notificationListResponseData, targetType, AppKeys.session_added_comments);

                }
                return "commented on a " + "Session Name" + " session you attended or conducted.";
            // break;

            case "added_qnas":

                openQuestionPage(notificationListResponseData, targetType, AppKeys.added_qnas);
                return "asked a Doubt in Session " + "Session Name";
            // break;

            case "added_answer":
                if (notificationListResponseData.nodeType.equals("session")) {
                    openQuestionPage(notificationListResponseData, targetType, AppKeys.added_answer);
                }
                return "answered your question in " + "Session Name" + " Session";
            // break;

            case "added_answers_reply":

                openAnswerReplyPage(notificationListResponseData, targetType, AppKeys.added_answers_reply);
                return "replied on your answer in " + "Session Name" + " Session";
            // break;

            case "upvoted_question":

                openQuestionPage(notificationListResponseData, targetType, AppKeys.upvoted_question);
                return "Upvoted your doubt(s)";

            // break;

            case "interest_approved":

                openInterestPage(notificationListResponseData, targetType);
                return "Your Interest Approved";
            // break;

            case "interest_declined":

                return "Sorry!,Your Interest Not Approved";
            // break;
            default:
                return "";
        }

    }


    private void openVenuePage(PojoGetNotificationListResponseData notificationListResponseData,
                               int targetType) {
        Intent intent = new Intent(context, VenueActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);
        notificationListResponseData.nodeUrl = parseNodeUrl(notificationListResponseData.nodeUrl);
        intent.putExtra("id", notificationListResponseData.nodeUrl);

        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            Intent[] intents = new Intent[]{mainActivityIntent, intent};
            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0,
                    intents, 0);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, 0, AppKeys.channel_ids[0]);
        } else {
            context.startActivity(intent);
        }
    }

    private void openInterestPage(PojoGetNotificationListResponseData notificationListResponseData,
                                  int targetType) {
        Intent intent = new Intent(context, InterestActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);
        notificationListResponseData.nodeUrl = parseNodeUrl(notificationListResponseData.nodeUrl);
        intent.putExtra("id", notificationListResponseData.nodeUrl);
        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            Intent[] intents = new Intent[]{mainActivityIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0, intents, 0);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, 0, AppKeys.channel_ids[0]);
        } else {
            context.startActivity(intent);

        }
    }

    private void openQuestionPage(PojoGetNotificationListResponseData notificationItem,
                                  int targetType, int notif_id) {
        Intent intent = new Intent(context, QuestionActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationItem.notificationId);

        String id;

        if (notificationItem.nodeType.equals("session") && notificationItem.action.equals("added_answer")) {
            String nodeUrl = notificationItem.nodeUrl;
            id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
            String answer_id = notificationItem.reply_id;
            intent.putExtra("answer_id", answer_id);
            intent.putExtra("id", id);
        } else if (notificationItem.nodeType.equals("session") && notificationItem.action.equals("upvoted_answer")) {
            String nodeUrl = notificationItem.nodeUrl;
            id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
            String answer_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
            intent.putExtra("answer_id", answer_id);
            intent.putExtra("id", id);
        } else {
            String nodeUrl = notificationItem.nodeUrl;
            id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
            intent.putExtra("id", id);
        }

        int mergedNotifId = Integer.parseInt("" + id + notif_id);

        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            Intent[] intents = new Intent[]{mainActivityIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0, intents,
                    FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationItem.message, mergedNotifId, AppKeys.channel_ids[3]);

        } else {
            context.startActivity(intent);
        }
    }


    private void openSessionLoader(PojoGetNotificationListResponseData notificationListResponseData,
                                   String type, int targetType, int notif_id) {
        Intent intent = new Intent(context, SessionLoaderActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);

        notificationListResponseData.nodeUrl = parseNodeUrl(notificationListResponseData.nodeUrl);

        String nodeUrl = notificationListResponseData.nodeUrl;
        String session_id = "0";
        if (nodeUrl.contains("/")) {
            session_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
        } else {
            session_id = nodeUrl;
        }

        intent.putExtra("id", session_id);
        intent.putExtra("type", type);
        if (notif_id == AppKeys.session_ended) {
            intent.putExtra(AppKeys.ACTION, AppKeys.SESSION_RATING);
        }

        if (type.equals("comment")) {
            String comment_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
            intent.putExtra("comment_id", comment_id);
        }

        if (type.equals("discussion")) {
            String discussion_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
            intent.putExtra("discussion_id", discussion_id);
        }

        int mergedNotifId = Integer.parseInt("" + session_id + notif_id);

        if (targetType == 1) {

            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            Intent[] intents = new Intent[]{mainActivityIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0, intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, mergedNotifId, AppKeys.channel_ids[2]);
        } else {
            context.startActivity(intent);
        }
    }


    private void openAnswerReplyPage(PojoGetNotificationListResponseData notificationListResponseData,
                                     int targetType, int notif_id) {
        Intent intent = new Intent(context, AnswerReplyListActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);

        String nodeUrl = notificationListResponseData.nodeUrl;
        String question_id = "";
        String answer_id;
        if (nodeUrl.contains("#")) {
            //  session_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
            answer_id = nodeUrl.substring(nodeUrl.indexOf("ans") + 3, nodeUrl.length());
        } else {
            question_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
            answer_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
        }

        String reply_id = notificationListResponseData.reply_id;

        //  intent.putExtra(AppKeys.SESSION_ID, session_id);
        intent.putExtra(AppKeys.ANSWER_ID, answer_id);
        intent.putExtra(AppKeys.REPLY_ID, reply_id);
        int mergedNotifId = Integer.parseInt("" + answer_id + notif_id);

        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            Intent questionIntent = new Intent(context, QuestionActivity.class);
            questionIntent.putExtra("answer_id", answer_id);
            questionIntent.putExtra("id", question_id);

            Intent[] intents = new Intent[]{mainActivityIntent, questionIntent, intent};
            PendingIntent notifPendingIntent;

          /*  TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntentWithParentStack(intent);

            notifPendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);*/

            notifPendingIntent = PendingIntent.getActivities(context, 0,
                    intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, mergedNotifId, AppKeys.channel_ids[3]);
        } else {
            context.startActivity(intent);
        }
    }

    private void openSessionCommentReplyActivity(PojoGetNotificationListResponseData notificationListResponseData,
                                                 int targetType, int notif_id) {
        Intent intent = new Intent(context, SessionCommentRepliesListActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);

        String nodeUrl = notificationListResponseData.nodeUrl;
        String session_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
        String comment_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
        String reply_id = notificationListResponseData.reply_id;

        intent.putExtra(AppKeys.SESSION_ID, session_id);
        intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        intent.putExtra(AppKeys.REPLY_ID, reply_id);

        int mergedNotifId = Integer.parseInt("" + session_id + notif_id);


        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            Intent sessionLoaderIntent = new Intent(context, SessionLoaderActivity.class);
            sessionLoaderIntent.putExtra("id", session_id);
            sessionLoaderIntent.putExtra("type", "details");

            Intent[] intents = new Intent[]{mainActivityIntent, sessionLoaderIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0, intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, mergedNotifId, AppKeys.channel_ids[2]);
        } else {
            context.startActivity(intent);
        }

    }


    private void openDiscussionReplyActivity(PojoGetNotificationListResponseData notificationListResponseData,
                                             int targetType, int notif_id) {
        Intent intent = new Intent(context, SessionDiscussionRepliesListActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);

        String nodeUrl = notificationListResponseData.nodeUrl;
        String session_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
        String discussion_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
        String reply_id = notificationListResponseData.reply_id;

        intent.putExtra(AppKeys.SESSION_ID, session_id);
        intent.putExtra(AppKeys.DISCUSSION_ID, discussion_id);
        intent.putExtra(AppKeys.REPLY_ID, reply_id);

        int mergedNotifId = Integer.parseInt("" + session_id + notif_id);


        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            Intent sessionLoaderIntent = new Intent(context, SessionLoaderActivity.class);
            sessionLoaderIntent.putExtra("id", session_id);
            sessionLoaderIntent.putExtra("type", "discussion");

            Intent[] intents = new Intent[]{mainActivityIntent, sessionLoaderIntent, intent};
            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0,
                    intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, mergedNotifId, AppKeys.channel_ids[2]);
        } else {
            context.startActivity(intent);
        }
    }


    private void openPostCommentReplyScreen(PojoGetNotificationListResponseData notificationListResponseData,
                                            int targetType, int notif_id) {
        Intent intent = new Intent(context, PostCommentRepliesListActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);

        intent = new Intent(context, PostCommentRepliesListActivity.class);
        String nodeUrl = notificationListResponseData.nodeUrl;
        String post_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
        intent.putExtra(AppKeys.POST_ID, post_id);

        String comment_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
        intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        String reply_id = notificationListResponseData.reply_id;

        intent.putExtra(AppKeys.REPLY_ID, reply_id);
        intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        intent.putExtra(AppKeys.POST_ID, notificationListResponseData.nodeUrl);

        int mergedNotifId = Integer.parseInt("" + comment_id + notif_id);


        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            Intent postLoaderIntent = new Intent(context, PostLoaderActivity.class);
            postLoaderIntent.putExtra(AppKeys.POST_ID, post_id);
            postLoaderIntent.putExtra(AppKeys.COMMENT_ID, comment_id);

            Intent[] intents = new Intent[]{mainActivityIntent, postLoaderIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0,
                    intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, mergedNotifId, AppKeys.channel_ids[5]);
        } else {
            context.startActivity(intent);
        }
    }


    private void openSessionCommentActivity(PojoGetNotificationListResponseData notificationListResponseData,
                                            int targetType, int notif_id) {
        Intent intent = new Intent(context, SessionCommentsListActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);

        String nodeUrl = notificationListResponseData.nodeUrl;
        String session_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
        String commentId = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());

        intent.putExtra("id", session_id);
        intent.putExtra("comment_id", commentId);

        int mergedNotifId = Integer.parseInt("" + session_id + notif_id);

        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            Intent sessionLoaderIntent = new Intent(context, SessionLoaderActivity.class);
            sessionLoaderIntent.putExtra("id", session_id);
            sessionLoaderIntent.putExtra("type", "details");

            Intent[] intents = new Intent[]{mainActivityIntent, sessionLoaderIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0,
                    intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, mergedNotifId, AppKeys.channel_ids[2]);
        } else {
            context.startActivity(intent);
        }
    }


    private void openArticlePage(PojoGetNotificationListResponseData notificationListResponseData, int targetType, int notif_id) {
        Intent intent = new Intent(context, ArticleActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);

        intent.putExtra("id", notificationListResponseData.nodeUrl);

        int mergedNotifId = Integer.parseInt("" + notificationListResponseData.nodeUrl + notif_id);

        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            Intent[] intents = new Intent[]{mainActivityIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0,
                    intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, mergedNotifId, AppKeys.channel_ids[4]);
        } else {
            context.startActivity(intent);
        }
    }

    private void openArticleCommentReply(PojoGetNotificationListResponseData notificationListResponseData,
                                         int targetType, int notif_id) {
        Intent intent = new Intent(context, ArticleCommentReplyListActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);

        String article_id = "";
        String comment_id = "";
        String reply_id = "";
        String nodeUrl = notificationListResponseData.nodeUrl;

        if (notificationListResponseData.action.equals("comment_reply")) {
            article_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
            comment_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
        }

        if (notificationListResponseData.action.equals("like_discussion_reply")) {
            article_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
            comment_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
        }

        reply_id = notificationListResponseData.reply_id;
        intent.putExtra(AppKeys.ARTICLE_ID, article_id);
        intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        intent.putExtra(AppKeys.REPLY_ID, reply_id);

        int mergedNotifId = Integer.parseInt("" + comment_id + notif_id);

        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            Intent articleIntent = new Intent(context, ArticleActivity.class);
            articleIntent.putExtra("id", article_id);

            Intent[] intents = new Intent[]{mainActivityIntent, articleIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0,
                    intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, mergedNotifId, AppKeys.channel_ids[4]);
        } else {
            context.startActivity(intent);
        }

    }


    private void openArticleCommentPage(PojoGetNotificationListResponseData notificationListResponseData,
                                        int targetType, int notif_id) {
        Intent intent = new Intent(context, ArticleCommentListActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);

        String article_id = "";
        String comment_id = "";

        String nodeUrl = notificationListResponseData.nodeUrl;
        if (notificationListResponseData.action.equals("comment")) {
            article_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
            comment_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
        } else if (notificationListResponseData.action.equals("like_discussion")) {
            article_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
            comment_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
        }

        //  intent.putExtra("target", AppKeys.COMMENT);
        intent.putExtra("id", article_id);
        intent.putExtra(AppKeys.COMMENT_ID, comment_id);

        int mergedNotifId = Integer.parseInt("" + article_id + notif_id);

        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            Intent articleIntent = new Intent(context, ArticleActivity.class);
            articleIntent.putExtra("id", article_id);

            Intent[] intents = new Intent[]{mainActivityIntent, articleIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0,
                    intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, mergedNotifId, AppKeys.channel_ids[4]);
        } else {
            context.startActivity(intent);

        }
    }


  /*  private void openPostCommentReplyScreen(PojoGetNotificationListResponseData notificationListResponseData, int targetType) {
        Intent intent = new Intent(context, NormalPostActivity.class);
        intent.putExtra(AppKeys.POST_ID, notificationListResponseData.nodeUrl);
        if(notificationListResponseData.action.equals("reply")){
            String comment_id=notificationListResponseData.notifyId;
            comment_id=comment_id.substring(comment_id.indexOf("nt_")+3,comment_id.length());
            intent.putExtra(AppKeys.COMMENT_ID,comment_id);
        }if(notificationListResponseData.action.equals("like")){
            String comment_id=notificationListResponseData.notifyId;
            comment_id=comment_id.substring(comment_id.indexOf("=")+1,comment_id.length());
            intent.putExtra(AppKeys.COMMENT_ID,comment_id);
        }
        if (targetType == 1) {
            PendingIntent notifPendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message);
        } else {
            context.startActivity(intent);
        }
    }*/

    private void openPollScreen(PojoGetNotificationListResponseData notificationListResponseData, int targetType, int notif_id) {
        Intent intent = new Intent(context, PollActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);

        intent.putExtra(AppKeys.POST_ID, notificationListResponseData.nodeUrl);
        if (notificationListResponseData.action.equals("reply")) {
            String comment_id = notificationListResponseData.notifyId;
            comment_id = comment_id.substring(comment_id.indexOf("nt_") + 3, comment_id.length());
            intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        }
        if (notificationListResponseData.action.equals("like")) {
            String comment_id = notificationListResponseData.notifyId;
            comment_id = comment_id.substring(comment_id.indexOf("=") + 1, comment_id.length());
            intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        }

        int mergedNotifId = Integer.parseInt("" + notificationListResponseData.nodeUrl + notif_id);

        if (targetType == 1) {

            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            Intent[] intents = new Intent[]{mainActivityIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0, intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, mergedNotifId, AppKeys.channel_ids[5]);
        } else {
            context.startActivity(intent);
        }
    }

    private void openPostScreen(PojoGetNotificationListResponseData notificationListResponseData, int targetType) {
        Intent intent = new Intent(context, NormalPostActivity.class);
        if (notificationListResponseData.action.equals("reply")) {
            String comment_id = notificationListResponseData.notifyId;
            comment_id = comment_id.substring(comment_id.indexOf("nt_") + 3, comment_id.length());
            intent.putExtra(AppKeys.COMMENT_ID, comment_id);
            intent.putExtra(AppKeys.POST_ID, notificationListResponseData.nodeUrl);

        }
        if (notificationListResponseData.action.equals("like")) {
            if (notificationListResponseData.nodeType.equals("post_comment")) {
                String post_id = notificationListResponseData.nodeUrl;
                intent.putExtra(AppKeys.POST_ID, post_id);
                String notifyId = notificationListResponseData.notifyId;

                String comment_id = notifyId.substring(notifyId.indexOf("=") + 1, notifyId.length());
                intent.putExtra(AppKeys.COMMENT_ID, comment_id);
            } else if (notificationListResponseData.nodeType.equals("post")) {
                String post_id = notificationListResponseData.nodeUrl;
                intent.putExtra(AppKeys.POST_ID, post_id);
            }

        }
        if (notificationListResponseData.action.equals("comment")) {
            if (notificationListResponseData.message.contains("poll")) {
                intent = new Intent(context, PollActivity.class);
            }

            String post_id = notificationListResponseData.nodeUrl;
            intent.putExtra(AppKeys.POST_ID, post_id);

            String notifyId = notificationListResponseData.notifyId;
            String comment_id = notifyId.substring(notifyId.indexOf("=") + 1, notifyId.length());
            intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        }

        if (targetType == 1) {
            PendingIntent notifPendingIntent = PendingIntent.getActivity(context, 0, intent, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, 0, AppKeys.channel_ids[0]);
        } else {
            context.startActivity(intent);
        }
    }

    private void openPostLoaderActivity(PojoGetNotificationListResponseData notificationListResponseData,
                                        int targetType, int notif_id) {
        Intent intent = new Intent(context, PostLoaderActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);
        String post_id = "";
        if (notificationListResponseData.action.equals("like")) {
            if (notificationListResponseData.nodeType.equals("post_comment")) {
                String nodeUrl = notificationListResponseData.nodeUrl;
                post_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
                intent.putExtra(AppKeys.POST_ID, post_id);

                String comment_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
                intent.putExtra(AppKeys.COMMENT_ID, comment_id);
            } else if (notificationListResponseData.nodeType.equals("post")) {
                post_id = notificationListResponseData.nodeUrl;
                intent.putExtra(AppKeys.POST_ID, post_id);
            }
        }
        if (notificationListResponseData.action.equals("comment")) {
            String nodeUrl = notificationListResponseData.nodeUrl;

            post_id = nodeUrl.substring(0, nodeUrl.indexOf("/"));
            intent.putExtra(AppKeys.POST_ID, post_id);

            String comment_id = nodeUrl.substring(nodeUrl.indexOf("/") + 1, nodeUrl.length());
            intent.putExtra(AppKeys.COMMENT_ID, comment_id);
        }
        if (notificationListResponseData.action.equals("share")
                || notificationListResponseData.action.equals("tagged_user")) {
            post_id = notificationListResponseData.nodeUrl;

            intent.putExtra(AppKeys.POST_ID, post_id);
        }

        int mergedNotifId = Integer.parseInt("" + post_id + notif_id);

        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);
            Intent[] intents = new Intent[]{mainActivityIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0, intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, mergedNotifId, AppKeys.channel_ids[5]);
        } else {
            context.startActivity(intent);
        }
    }


    private void openOrganisationPage(PojoGetNotificationListResponseData notificationListResponseData, int targetType) {
        Intent intent = new Intent(context, OrganisationProfileInfoActivity.class);
        intent.putExtra(AppKeys.ORGANISATION_ID, notificationListResponseData.nodeUrl);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);
        intent.putExtra(AppKeys.OWNER_ID, user_id);

        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            Intent[] intents = new Intent[]{mainActivityIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0,
                    intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, 0, AppKeys.channel_ids[7]);
        } else {
            context.startActivity(intent);
        }
    }

    private void openUserProfile(PojoGetNotificationListResponseData notificationListResponseData, int targetType) {
        Intent intent = new Intent(context, ProfileInfoActivity.class);
        intent.putExtra(AppKeys.NOTIFICATION_ID, notificationListResponseData.notificationId);

        intent.putExtra("target_user_id", notificationListResponseData.userId);


        if (targetType == 1) {
            Intent mainActivityIntent = new Intent(context, MainActivity.class);

            Intent[] intents = new Intent[]{mainActivityIntent, intent};

            PendingIntent notifPendingIntent = PendingIntent.getActivities(context, 0,
                    intents, FLAG_UPDATE_CURRENT);
            showNotification(context, notifPendingIntent, notificationListResponseData.message, 0, AppKeys.channel_ids[5]);
        } else {
            context.startActivity(intent);
        }
    }

    public String parseNodeUrl(String nodeUrl) {
        if (nodeUrl.contains(",")) {
            nodeUrl = nodeUrl.substring(0, nodeUrl.indexOf(","));
        } else if (nodeUrl.contains("#")) {
            nodeUrl = nodeUrl.substring(0, nodeUrl.indexOf("#"));
        }
        return nodeUrl;
    }

    public void showNotification(Context context, PendingIntent pi, String message, int mergedNotifId, String channelId) {

        message = Html.fromHtml(message).toString();
        message = message.replace("<strong>", "");
        message = message.replace("<br />", "");

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

        String title = context.getString(R.string.app_name);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId)
                .setTicker(title)
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setColor(ContextCompat.getColor(context, R.color.app_theme_medium))
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setVibrate(vibrate)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                // .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(null))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int notificationId;
        if (mergedNotifId == 0) {
            notificationId = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        } else {
            notificationId = mergedNotifId;
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, mBuilder.build());
    }


    private void hitNotificationSeenApi(String notificationId) {
        Call<PojoNoDataResponse> call = apiService.seenNotification(user_id, notificationId);
        call.enqueue(new Callback<PojoNoDataResponse>() {
            @Override
            public void onResponse(Call<PojoNoDataResponse> call, Response<PojoNoDataResponse> response) {
                String message = "";
                if (response != null) {

                }
            }

            @Override
            public void onFailure(Call<PojoNoDataResponse> call, Throwable t) {
                // Log error here since request failed
                commonFunctions.setToastMessage(context, t.toString(), Toast.LENGTH_LONG, AppKeys.TOAST_DEBUG);
            }
        });
    }

}
