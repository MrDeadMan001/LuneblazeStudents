package com.avadna.luneblaze.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sunny on 30-01-2018.
 */

public class PojoGetInterestListResponseDataListItem {
    @SerializedName("interest_id")
    @Expose
    public String interestId = "";
    @SerializedName("parent_id")
    @Expose
    public String parentId = "";
    @SerializedName("text")
    @Expose
    public String text = "";
    @SerializedName("has_sub_list")
    @Expose
    public Integer hasSubList = 0;
    @SerializedName("image")
    @Expose
    public String image;
    @SerializedName("description")
    @Expose
    public String description;
    @SerializedName("no_of_followers")
    @Expose
    public Integer noOfFollowers;
    @SerializedName("i_follow_related")
    @Expose
    public Integer iFollowRelated;


    @SerializedName("has_children")
    @Expose
    public String hasChildren;
    @SerializedName("total_children")
    @Expose
    public Integer totalChildren;


    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("child")
    @Expose
    public List<PojoGetInterestListResponseDataListItem> child;

    transient  public PojoGetInterestListResponseDataListItem parentRef = null;
    //not a part of response data
    //added to keep track of checkbox status checked or unchecked
    public Boolean isCbChecked = false;


    public PojoGetInterestListResponseDataListItem() {

    }

    public PojoGetInterestListResponseDataListItem(String interestId, String text, String parentId, String status,
                                                   List<PojoGetInterestListResponseDataListItem> data) {
        super();
        this.interestId = interestId;
        this.text = text;
        this.parentId = parentId;
        this.status = status;
        this.child = data;
    }

    public void setParent(PojoGetInterestListResponseDataListItem parentRef) {
        this.parentRef = parentRef;
    }

    public PojoGetInterestListResponseDataListItem(String interestId, String parentId, String text, Integer hasSubList) {
        super();
        this.interestId = interestId;
        this.parentId = parentId;
        this.text = text;
        this.hasSubList = hasSubList;
        this.image = "";

    }


}
