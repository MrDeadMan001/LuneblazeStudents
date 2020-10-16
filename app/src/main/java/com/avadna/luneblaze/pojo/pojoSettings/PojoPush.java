package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoPush {
    @SerializedName("privacy_notification_push_invites_messages")
    @Expose
    public String privacyNotificationPushInvitesMessages;
    @SerializedName("privacy_notification_push_sessions")
    @Expose
    public String privacyNotificationPushSessions;
    @SerializedName("privacy_notification_push_qa")
    @Expose
    public String privacyNotificationPushQa;
    @SerializedName("privacy_notification_push_articles")
    @Expose
    public String privacyNotificationPushArticles;
    @SerializedName("privacy_notification_push_update")
    @Expose
    public String privacyNotificationPushUpdate;
}
