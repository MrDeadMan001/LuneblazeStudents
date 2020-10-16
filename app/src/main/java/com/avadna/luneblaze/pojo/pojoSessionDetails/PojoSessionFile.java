package com.avadna.luneblaze.pojo.pojoSessionDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoSessionFile {
    @SerializedName("file")
    @Expose
    public String file;

    @SerializedName("id")
    @Expose
    public String id;

    public String uploadStatus="";

    public PojoSessionFile(String path,String id,String uploadStatus){
        this.file=path;
        this.id=id;
        this.uploadStatus=uploadStatus;

    }
}
