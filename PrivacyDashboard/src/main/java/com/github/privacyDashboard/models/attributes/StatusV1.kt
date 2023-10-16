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
    override val mConsented: String?
        get() = consented
    override val mRemaining: Int?
        get() = remaining

}
