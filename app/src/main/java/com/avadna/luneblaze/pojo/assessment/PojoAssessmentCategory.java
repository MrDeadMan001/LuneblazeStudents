package com.avadna.luneblaze.pojo.assessment;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PojoAssessmentCategory {
    @SerializedName("category_id")
    @Expose
    public String categoryId;
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
    public List<PojoAssessmentCategory> child = null;

    public boolean marksDependency=false;
    public int and_or=0;
    public boolean isCbChecked=false;
    public String cb_type;

    public PojoAssessmentCategory parentRef;

    public PojoAssessmentCategory(String categoryId, String text, String parentId, String status,
                                                   List<PojoAssessmentCategory> data) {
        super();
        this.categoryId = categoryId;
        this.text = text;
        this.parentId = parentId;
        this.status = status;
        this.child = data;
        this.isCbChecked=false;
    }

    public void setParent(PojoAssessmentCategory parentRef) {
        this.parentRef = parentRef;
    }
}
