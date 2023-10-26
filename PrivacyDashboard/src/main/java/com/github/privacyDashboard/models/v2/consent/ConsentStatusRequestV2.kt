package com.github.privacyDashboard.models.v2.consent

import com.google.gson.annotations.SerializedName

data class ConsentStatusRequestV2(
    @SerializedName("optIn") var optIn: Boolean? = null
)