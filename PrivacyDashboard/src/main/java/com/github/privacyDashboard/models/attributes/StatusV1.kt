package com.github.privacyDashboard.models.attributes

import com.github.privacyDashboard.models.uiModels.dataAttributesList.Status
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StatusV1(
    @SerializedName("Consented")
    var consented: String? = null,
    @SerializedName("TimeStamp")
    var timeStamp: String? = null,
    @SerializedName("Days")
    var days: Int? = null,
    @SerializedName("Remaining")
    var remaining: Int? = 0
) : Status {
    override var mConsented: String?
        get() = consented
        set(value) {
            mConsented = value
        }
    override var mRemaining: Int?
        get() = remaining
        set(value) {
            mRemaining = value
        }

}
