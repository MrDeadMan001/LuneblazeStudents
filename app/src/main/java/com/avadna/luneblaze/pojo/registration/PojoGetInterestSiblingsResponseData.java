package com.avadna.luneblaze.pojo.registration;

import com.avadna.luneblaze.pojo.PojoGetInterestListResponseDataListItem;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoGetInterestSiblingsResponseData {
    @SerializedName("parent")
    @Expose
    public PojoGetInterestListResponseDataListItem parent;
    @SerializedName("interests")
    @Expose
    public List<PojoGetInterestListResponseDataListItem> parentHierarchy = null;
    @SerializedName("siblings")
    @Expose
    public List<PojoGetInterestListResponseDataListItem> siblings = null;
}
