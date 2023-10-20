package com.github.privacyDashboard.models.base.attribute

import com.github.privacyDashboard.models.PurposeV1
import com.github.privacyDashboard.models.base.Purpose
import com.github.privacyDashboard.models.interfaces.dataAttributesList.DataAttributes
import com.google.gson.annotations.SerializedName

class DataAttributes(
    @SerializedName("Purpose")
    var purpose: Purpose? = null,
    @SerializedName("Consents")
    var consents: ArrayList<DataAttribute?>? = null
) : DataAttributes {
    override val mPurpose: Purpose?
        get() = purpose

}
