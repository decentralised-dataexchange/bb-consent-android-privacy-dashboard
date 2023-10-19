package com.github.privacyDashboard.models.attributes

import com.github.privacyDashboard.models.interfaces.dataAttributesList.DataAttribute
import com.github.privacyDashboard.models.interfaces.dataAttributesList.Status
import com.google.gson.annotations.SerializedName

class DataAttributeV1(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("Description")
    var description: String? = null,
    @SerializedName("Value")
    var value: String? = null,
    @SerializedName("Status")
    var status: StatusV1? = null
) : DataAttribute {
    override val mId: String?
        get() = iD
    override val mDescription: String?
        get() = description
    override var mStatus: Status?
        get() = status
        set(value) {
            mStatus = value
        }
}
