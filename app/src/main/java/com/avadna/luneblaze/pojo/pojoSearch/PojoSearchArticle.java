package com.avadna.luneblaze.pojo.pojoSearch;

import com.avadna.luneblaze.pojo.pojoQuestion.PojoUserData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSearchArticle {

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
    @SerializedName("created_by")
    @Expose
    public PojoUserData createdBy;
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
