package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoEmail {
    @SerializedName("privacy_notification_email_invites_messages")
    @Expose
    public String privacyNotificationEmailInvitesMessages;
    @SerializedName("privacy_notification_email_sessions")
    @Expose
    public String privacyNotificationEmailSessions;
    @SerializedName("privacy_notification_email_qa")
    @Expose
    public String privacyNotificationEmailQa;
    @SerializedName("privacy_notification_email_articles")
    @Expose
    public String privacyNotificationEmailArticles;
    @SerializedName("privacy_notification_email_update")
    @Expose
    public String privacyNotificationEmailUpdate;
}
