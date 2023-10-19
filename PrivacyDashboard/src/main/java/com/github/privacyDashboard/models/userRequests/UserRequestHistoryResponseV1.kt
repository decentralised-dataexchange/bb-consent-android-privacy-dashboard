package com.github.privacyDashboard.models.userRequests

import com.github.privacyDashboard.models.consentHistory.ConsentHistoryV1
import com.github.privacyDashboard.models.uiModels.consentHistory.ConsentHistory
import com.github.privacyDashboard.models.uiModels.userRequests.UserRequest
import com.github.privacyDashboard.models.uiModels.userRequests.UserRequestHistoryResponse
import com.google.gson.annotations.SerializedName

class UserRequestHistoryResponseV1(
    @SerializedName("DataRequests")
    var dataRequests: ArrayList<UserRequestV1>? = null,
    @SerializedName("IsRequestsOngoing")
    var requestsOngoing: Boolean? = null,
    @SerializedName("IsDataDownloadRequestOngoing")
    var dataDownloadRequestOngoing: Boolean? = null,
    @SerializedName("IsDataDeleteRequestOngoing")
    var dataDeleteRequestOngoing: Boolean? = null
) : UserRequestHistoryResponse {
    override val mDataRequests: ArrayList<UserRequest?>?
        get() = convertToList(dataRequests)
    override val mRequestsOngoing: Boolean?
        get() = requestsOngoing
    override val mDataDownloadRequestOngoing: Boolean?
        get() = dataDownloadRequestOngoing
    override val mDataDeleteRequestOngoing: Boolean?
        get() = dataDeleteRequestOngoing

    private fun convertToList(consents: ArrayList<UserRequestV1>?): ArrayList<UserRequest?>? {
        return consents?.filterNotNull()?.toCollection(ArrayList())
    }
}
