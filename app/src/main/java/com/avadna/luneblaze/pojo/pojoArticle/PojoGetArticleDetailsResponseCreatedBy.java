package com.avadna.luneblaze.pojo.pojoArticle;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Sunny on 13-02-2018.
 */

public class PojoGetArticleDetailsResponseCreatedBy {
    @SerializedName("user_id")
    @Expose
    public String userId;
    @SerializedName("user_name")
    @Expose
    public String userName;
    @SerializedName("user_fullname")
    @Expose
    public String userFullname;
    @SerializedName("user_gender")
    @Expose
    public String userGender;
    @SerializedName("user_picture")
    @Expose
    public String userPicture;
}
