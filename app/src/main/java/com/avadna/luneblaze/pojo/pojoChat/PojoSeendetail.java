package com.avadna.luneblaze.pojo.pojoChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PojoSeendetail {
    @SerializedName("seenusers")
    @Expose
    public List<PojoSeenuser> seenusers = null;
    @SerializedName("notseenusers")
    @Expose
    public List<String> notseenusers = null;

    public PojoSeendetail(String s){
        this.seenusers=new ArrayList<>();
        this.notseenusers=new ArrayList<>();
    }
}
