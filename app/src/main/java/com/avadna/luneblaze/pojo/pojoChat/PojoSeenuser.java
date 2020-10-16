package com.avadna.luneblaze.pojo.pojoChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSeenuser {
    @SerializedName("userid")
    @Expose
    public String userid;
    @SerializedName("seentime")
    @Expose
    public String seentime;


    public PojoSeenuser(String user_id, String seentime) {
        this.userid = user_id;
        this.seentime = seentime;
    }
}
