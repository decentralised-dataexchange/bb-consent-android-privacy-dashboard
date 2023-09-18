package com.github.privacyDashboard.modules.userRequest

import com.github.privacyDashboard.models.userRequests.UserRequest

interface BBConsentUserRequestClickListener {
    fun onRequestClick(request: UserRequest?)
    fun onRequestCancel(request: UserRequest?)
}