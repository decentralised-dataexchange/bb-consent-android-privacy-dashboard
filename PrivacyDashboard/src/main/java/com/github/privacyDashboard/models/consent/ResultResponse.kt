package com.github.privacyDashboard.models.consent

import com.google.gson.annotations.SerializedName

data class ResultResponse(
    @SerializedName("Result")
    var result: Boolean? = null,
    @SerializedName("Message")
    var message: String? = null
) {}
