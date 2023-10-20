package com.github.privacyDashboard.models.base.attribute

import com.github.privacyDashboard.models.interfaces.dataAttributesList.DataAttribute
import com.google.gson.annotations.SerializedName

class DataAttribute(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("Description")
    var description: String? = null,
    @SerializedName("Status")
    var status: Status? = null
) : DataAttribute {
    override val mId: String?
        get() = iD
    override val mDescription: String?
        get() = description
    override val mStatus: Status?
        get() = status
}
