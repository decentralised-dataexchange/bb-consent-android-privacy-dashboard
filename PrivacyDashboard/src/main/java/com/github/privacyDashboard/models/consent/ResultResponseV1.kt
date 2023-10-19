package com.github.privacyDashboard.models.consent

import com.github.privacyDashboard.models.interfaces.consent.ResultResponse
import com.google.gson.annotations.SerializedName

class ResultResponseV1(
    @SerializedName("Result")
    var result: Boolean? = null,
    @SerializedName("Message")
    var message: String? = null
) : ResultResponse {
    override val mResult: Boolean?
        get() = result
    override val mMessage: String?
        get() = message
}
