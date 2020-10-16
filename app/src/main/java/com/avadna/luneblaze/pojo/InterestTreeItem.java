package com.avadna.luneblaze.pojo;

import java.util.ArrayList;

public class InterestTreeItem {


    public String interestId;

    public String parentId;

    public String text;

    public Integer hasSubList;

    public String image;

    public Integer noOfFollowers;

    public Integer iFollowRelated;

    //not a part of response data
    //added to keep track of checkbox status checked or unchecked
    public Boolean isCbChecked =false;


    ArrayList<InterestTreeItem> children;

    public InterestTreeItem(PojoGetInterestListResponseDataListItem item){
       interestId=item.interestId;
       parentId=item.parentId;
       text=item.text;
       hasSubList=item.hasSubList;
       image=item.image;
       noOfFollowers=item.noOfFollowers;
       iFollowRelated=item.iFollowRelated;
    }

    public void addChildrenList(ArrayList<InterestTreeItem> children){
        this.children=children;
    }
}
