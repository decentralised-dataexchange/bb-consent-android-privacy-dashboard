package com.github.privacyDashboard.models.logging

import com.google.gson.annotations.SerializedName

data class ConsentHistory(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("OrgID")
    var orgID: String? = null,
    @SerializedName("PurposeID")
    var purposeIDl: String? = null,
    @SerializedName("Log")
    var log: String? = null,
    @SerializedName("TimeStamp")
    var timeStamp: String? = null
) {}

