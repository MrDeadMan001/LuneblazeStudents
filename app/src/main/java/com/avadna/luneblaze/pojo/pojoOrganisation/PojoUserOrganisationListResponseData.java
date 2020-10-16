package com.avadna.luneblaze.pojo.pojoOrganisation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PojoUserOrganisationListResponseData {
    @SerializedName("organization")
    @Expose
    public List<PojoMyOrganizationListItem> organization = null;
}
