package com.github.privacyDashboard.models.logging

import com.google.gson.annotations.SerializedName

data class ConsentHistoryResponse (
    @SerializedName("ConsentHistory")
    var consentHistory: ArrayList<ConsentHistory>? = null
)
