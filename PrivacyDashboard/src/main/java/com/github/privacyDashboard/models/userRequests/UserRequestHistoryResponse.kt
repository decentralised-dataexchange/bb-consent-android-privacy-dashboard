package com.github.privacyDashboard.models.userRequests

import com.google.gson.annotations.SerializedName

data class UserRequestHistoryResponse(
    @SerializedName("DataRequests")
    var dataRequests: ArrayList<UserRequest>? = null,
    @SerializedName("IsRequestsOngoing")
    var requestsOngoing: Boolean? = null,
    @SerializedName("IsDataDownloadRequestOngoing")
    var dataDownloadRequestOngoing: Boolean? = null,
    @SerializedName("IsDataDeleteRequestOngoing")
    var dataDeleteRequestOngoing: Boolean? = null
)
