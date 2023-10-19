package com.github.privacyDashboard.models.uiModels.userRequests

interface UserRequestStatus {
    val mRequestOngoing: Boolean?
    val mId: String?
    val mState: Int?
    val mStateStr: String?
    val mRequestedDate: String?
}