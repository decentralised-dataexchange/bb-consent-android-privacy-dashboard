package com.github.privacyDashboard.models.consent

import com.github.privacyDashboard.models.PurposeConsent
import com.google.gson.annotations.SerializedName

data class UpdateConsentStatusResponse(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("OrgID")
    var orgID: String? = null,
    @SerializedName("UserID")
    var userID: String? = null,
    @SerializedName("ConsentsAndPurposes")
    var ConsentsAndPurposes: ArrayList<PurposeConsent>? = null
)
