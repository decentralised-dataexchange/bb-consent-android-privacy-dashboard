package com.github.privacyDashboard.models

import com.google.gson.annotations.SerializedName

data class PurposeConsent (
    @SerializedName("Purpose")
    var purpose: Purpose? = null,
    @SerializedName("Count")
    var count: Count? = null
){}
