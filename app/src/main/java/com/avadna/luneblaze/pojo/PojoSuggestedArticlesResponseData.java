package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSuggestedArticlesResponseData {
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
    @SerializedName("created_by")
    @Expose
    public PojoSuggestedArticlesResponseDataCreatedBy createdBy;
    @SerializedName("likes")
    @Expose
    public String likes;
    @SerializedName("shares")
    @Expose
    public String shares;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("added_on")
    @Expose
    public String addedOn;
    @SerializedName("modified_on")
    @Expose
    public String modifiedOn;


    //below fields used in search result model class
    @SerializedName("page_picture_default")
    @Expose
    public Boolean pagePictureDefault;
    @SerializedName("page_picture")
    @Expose
    public String pagePicture;
    @SerializedName("discussions_cnt")
    @Expose
    public String discussionsCnt;
    @SerializedName("i_like")
    @Expose
    public Boolean iLike;
}
