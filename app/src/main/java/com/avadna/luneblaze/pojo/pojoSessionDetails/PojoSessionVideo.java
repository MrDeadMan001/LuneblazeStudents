package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.avadna.luneblaze.helperClasses.AppKeys;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSessionVideo {
    @SerializedName("video")
    @Expose
    public String video;

    public String uploadStatus="";

    public String videoId="";

    public PojoSessionVideo(String path, String videoId,String uploadStatus) {
        this.video = path;
        this.videoId = videoId;
        this.uploadStatus= uploadStatus;
    }


}
