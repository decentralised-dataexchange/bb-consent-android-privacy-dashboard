package com.github.privacyDashboard.models.userRequests

import com.google.gson.annotations.SerializedName

data class UserRequestGenResponse(
    @SerializedName("iD")
    var iD: String? = null,
    @SerializedName("userID")
    var userID: String? = null,
    @SerializedName("userName")
    var userName: String? = null,
    @SerializedName("orgID")
    var orgID: String? = null,
    @SerializedName("type")
    var type: Int? = 0,
    @SerializedName("typeStr")
    var typeStr: String? = null,
    @SerializedName("state")
    var state: Int? = 0,
    @SerializedName("requestedDate")
    var requestedDate: String? = null,
    @SerializedName("closedDate")
    var closedDate: String? = null,
    @SerializedName("stateStr")
    var stateStr: String? = null,
    @SerializedName("comment")
    var comment: String? = null
)
