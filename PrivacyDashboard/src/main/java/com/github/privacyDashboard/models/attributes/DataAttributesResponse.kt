package com.github.privacyDashboard.models.attributes

import com.github.privacyDashboard.models.interfaces.dataAttributesList.DataAgreement
import com.github.privacyDashboard.models.interfaces.dataAttributesList.DataAttributes
import com.google.gson.annotations.SerializedName

class DataAttributesResponse(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("ConsentID")
    var consentID: String? = null,
    @SerializedName("OrgID")
    var orgID: String? = null,
    @SerializedName("UserID")
    var userID: String? = null,
    @SerializedName("Consents")
    var consents: DataAttributesV1? = null
) : DataAgreement {
    override val mId: String?
        get() = iD
    override val mConsentId: String?
        get() = consentID
    override val mOrgId: String?
        get() = orgID
    override val mConsents: DataAttributes?
        get() = consents

}
