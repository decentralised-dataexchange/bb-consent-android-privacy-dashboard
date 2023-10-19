package com.github.privacyDashboard.modules.userRequest

import com.github.privacyDashboard.models.uiModels.userRequests.UserRequest
import com.github.privacyDashboard.models.userRequests.UserRequestV1

interface BBConsentUserRequestClickListener {
    fun onRequestClick(request: UserRequest?)
    fun onRequestCancel(request: UserRequest?)
}