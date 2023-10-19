package com.github.privacyDashboard.models.uiModels.userRequests

import com.github.privacyDashboard.models.uiModels.consentHistory.ConsentHistory

interface UserRequestHistoryResponse {
    val mDataRequests: ArrayList<UserRequest?>?
    val mRequestsOngoing: Boolean?
    val mDataDownloadRequestOngoing: Boolean?
    val mDataDeleteRequestOngoing: Boolean?
}