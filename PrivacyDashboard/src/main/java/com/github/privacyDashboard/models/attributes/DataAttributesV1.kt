package com.github.privacyDashboard.models.attributes

import com.github.privacyDashboard.models.Count
import com.github.privacyDashboard.models.PurposeV1
import com.github.privacyDashboard.models.uiModels.dataAttributesList.DataAttribute
import com.github.privacyDashboard.models.uiModels.dataAttributesList.DataAttributes
import com.github.privacyDashboard.models.uiModels.dataAttributesList.Purpose
import com.google.gson.annotations.SerializedName

class DataAttributesV1(
    @SerializedName("Purpose")
    var purpose: PurposeV1? = null,
    @SerializedName("Count")
    var count: Count? = null,
    @SerializedName("Consents")
    var consents: ArrayList<DataAttributeV1?>? = null
) : DataAttributes {
    override val mPurpose: Purpose?
        get() = purpose
    override val mConsents: ArrayList<DataAttribute?>?
        get() = convertToList(consents)

    private fun convertToList(consents: ArrayList<DataAttributeV1?>?): ArrayList<DataAttribute?>? {
        return consents?.filterNotNull()?.toCollection(ArrayList())
    }

}
