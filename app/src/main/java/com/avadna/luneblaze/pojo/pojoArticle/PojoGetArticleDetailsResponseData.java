package com.avadna.luneblaze.pojo.pojoArticle;

import com.avadna.luneblaze.pojo.pojoInterest.PojoInterestArr;
import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Sunny on 13-02-2018.
 */

public class PojoGetArticleDetailsResponseData {
    @SerializedName("articles_id")
    @Expose
    public String articlesId;
    @SerializedName("title")
    @Expose
    public String title;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("cover_photo")
    @Expose
    public String coverPhoto;

    @SerializedName("img_dimensions")
    @Expose
    public String imgDimensions;

    @SerializedName("img_width")
    @Expose
    public String imgWidth;

    @SerializedName("img_height")
    @Expose
    public String imgHeight;

    @SerializedName("created_by")
    @Expose
    public PojoUserData createdBy;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("shares")
    @Expose
    public Object shares;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;
    @SerializedName("page_picture_default")
    @Expose
    public Boolean pagePictureDefault;
    @SerializedName("discussions_cnt")
    @Expose
    public String discussionsCnt;
    @SerializedName("intrests")
    @Expose
    public List<PojoInterestArr> intrests = null;

    @SerializedName("liked_users")
    @Expose
    public List<PojoUserData> likedUsers = null;




    @SerializedName("edit")
    @Expose
    public Boolean edit;


    @SerializedName("page_picture")
    @Expose
    public String pagePicture;

    @SerializedName("i_like")
    @Expose
    public Boolean iLike;

    @SerializedName("i_save")
    @Expose
    public String iSave;
    @SerializedName("i_pin ")
    @Expose
    public Boolean iPin;


}
