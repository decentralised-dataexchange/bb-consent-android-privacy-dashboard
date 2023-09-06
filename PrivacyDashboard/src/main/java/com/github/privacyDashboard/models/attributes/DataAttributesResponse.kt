package com.github.privacyDashboard.models.attributes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataAttributesResponse (
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("ConsentID")
    var consentID: String? = null,
    @SerializedName("OrgID")
    var orgID: String? = null,
    @SerializedName("UserID")
    var userID: String? = null,
    @SerializedName("Consents")
    var consents: DataAttributes? = null
):Serializable{}
