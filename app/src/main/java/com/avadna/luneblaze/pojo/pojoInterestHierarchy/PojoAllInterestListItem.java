package com.avadna.luneblaze.pojo.pojoInterestHierarchy;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PojoAllInterestListItem {
    @SerializedName("interest_id")
    @Expose
    public String interestId;

    @SerializedName("text")
    @Expose
    public String text;

    @SerializedName("parent_id")
    @Expose
    public String parentId;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("child")
    @Expose
    public List<PojoAllInterestListItem> child;

    public PojoAllInterestListItem parentRef = null;

    public int matchScore=0;
    public boolean isCbChecked;


    public PojoAllInterestListItem(String interestId, String text, String parentId, String status,
                                   List<PojoAllInterestListItem> data) {
        this.interestId = interestId;
        this.text = text;
        this.parentId = parentId;
        this.status = status;
        this.child = data;
    }

    public void setParent(PojoAllInterestListItem parentRef) {
        this.parentRef=parentRef;
    }
}
