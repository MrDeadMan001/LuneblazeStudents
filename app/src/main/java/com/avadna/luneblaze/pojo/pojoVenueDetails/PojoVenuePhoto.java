package com.avadna.luneblaze.pojo.pojoVenueDetails;

import com.avadna.luneblaze.helperClasses.AppKeys;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoVenuePhoto {

    @SerializedName("photo")
    @Expose
    public String photo;

    @SerializedName("photoname")
    @Expose
    public String photoname;

    //photoid is not received in api response
    @SerializedName("photoid")
    @Expose
    public String photoid;

    public String uploadStatus="";

    public PojoVenuePhoto(String path, String photoId) {
        this.photo = path;
        this.photoid = photoId;
        uploadStatus= AppKeys.UPLOADING;
    }
}
