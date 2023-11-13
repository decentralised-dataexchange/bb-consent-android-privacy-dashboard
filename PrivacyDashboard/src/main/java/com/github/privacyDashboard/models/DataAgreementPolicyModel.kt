package com.github.privacyDashboard.models

import com.google.gson.annotations.SerializedName

data class DataAgreementPolicyModel(
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("value")
    var value: String? = null
)