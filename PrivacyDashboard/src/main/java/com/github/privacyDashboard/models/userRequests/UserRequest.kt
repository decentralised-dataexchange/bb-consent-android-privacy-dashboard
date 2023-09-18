package com.github.privacyDashboard.models.userRequests

import com.google.gson.annotations.SerializedName

data class UserRequest(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("UserID")
    var userID: String? = null,
    @SerializedName("UserName")
    var userName: String? = null,
    @SerializedName("OrgID")
    var orgID: String? = null,
    @SerializedName("Type")
    var type: Int? = null,
    @SerializedName("TypeStr")
    var typeStr: String? = null,
    @SerializedName("State")
    var state: Int? = null,
    @SerializedName("RequestedDate")
    var requestedDate: String? = null,
    @SerializedName("ClosedDate")
    var closedDate: String? = null,
    @SerializedName("StateStr")
    var stateStr: String? = null,
    @SerializedName("Comment")
    var comment: String? = null,
    var isOnGoing: Boolean? = false
)
