package com.github.privacyDashboard.models.userRequests

import com.google.gson.annotations.SerializedName

data class UserRequestStatus(
    @SerializedName("RequestOngoing")
    var requestOngoing: Boolean? = null,
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("State")
    var state: Int? = 0,
    @SerializedName("StateStr")
    var stateStr: String? = null,
    @SerializedName("RequestedDate")
    var requestedDate: String? = null
)
