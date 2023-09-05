package com.github.privacyDashboard.models

import com.google.gson.annotations.SerializedName

data class OrganizationDetailResponse(
    @SerializedName("Organization")
    var organization: Organization? = null,
    @SerializedName("ConsentID")
    var consentID: String? = null,
    @SerializedName("PurposeConsents")
    var purposeConsents: ArrayList<PurposeConsent>? = null
) {}
