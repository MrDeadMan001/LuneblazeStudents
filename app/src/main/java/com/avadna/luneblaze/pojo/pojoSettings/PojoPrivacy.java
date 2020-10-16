package com.avadna.luneblaze.pojo.pojoSettings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoPrivacy {
    @SerializedName("system_chat_enabled")
    @Expose
    public String systemChatEnabled;
    @SerializedName("user_chat_enabled")
    @Expose
    public String userChatEnabled;
    @SerializedName("privacy_connection_request")
    @Expose
    public String privacyConnectionRequest;
    @SerializedName("user_privacy_friends")
    @Expose
    public String userPrivacyFriends;
    @SerializedName("privacy_message")
    @Expose
    public String privacyMessage;
    @SerializedName("privacy_session")
    @Expose
    public String privacySession;
    @SerializedName("privacy_choices")
    @Expose
    public String privacyChoices;
    @SerializedName("user_privacy_basic")
    @Expose
    public String userPrivacyBasic;
    @SerializedName("privacy_content")
    @Expose
    public String privacyContent;
    @SerializedName("privacy_tag")
    @Expose
    public String privacyTag;
    @SerializedName("privacy_invite_session")
    @Expose
    public String privacyInviteSession;
    @SerializedName("privacy_invite_conduct_session")
    @Expose
    public String privacyInviteConductSession;
    @SerializedName("privacy_invite_answer")
    @Expose
    public String privacyInviteAnswer;

    @SerializedName("privacy_invite_interest")
    @Expose
    public String privacyInviteInterest;

    @SerializedName("delete_request_on")
    @Expose
    public String deleteRequestOn;

    @SerializedName("withdraw_delete_request")
    @Expose
    public String withdrawDeleteRequest;

    @SerializedName("two_step_verifications")
    @Expose
    public String twoStepVerifications;

    @SerializedName("assessment_allowed")
    @Expose
    public String assessmentAllowed;


}
