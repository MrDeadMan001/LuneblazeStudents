package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 16-03-2018.
 */

public class PojoLoginResponseData {

    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_phone")
    @Expose
    public String userPhone;
    @SerializedName("user_fullname")
    @Expose
    public String userFullname;
    @SerializedName("user_email")
    @Expose
    public String userEmail;
    @SerializedName("user_started")
    @Expose
    public String userStarted;
    @SerializedName("user_picture")
    @Expose
    public String userPicture;
    @SerializedName("user_gender")
    @Expose
    public String userGender;
    @SerializedName("two_step_verifications")
    @Expose
    public String twoStepVerifications;
    @SerializedName("two_step")
    @Expose
    public Boolean twoStep;
    @SerializedName("message")
    @Expose
    public String message;


    @SerializedName("user_work_place")
    @Expose
    public String userWorkPlace;
    @SerializedName("user_work_title")
    @Expose
    public String userWorkTitle;

    @SerializedName("logged_in")
    @Expose
    public Boolean loggedIn;

    @SerializedName("session_id")
    @Expose
    public Integer sessionId;

    @SerializedName("oraganisation_id")
    @Expose
    public String oraganisationId;


    @SerializedName("dob")
    @Expose
    public String dob="1991-01-02";



}
