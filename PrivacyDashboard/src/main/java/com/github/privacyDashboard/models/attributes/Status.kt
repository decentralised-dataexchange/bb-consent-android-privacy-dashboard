package com.github.privacyDashboard.models.attributes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Status(
    @SerializedName("Consented")
    var consented: String? = null,
    @SerializedName("TimeStamp")
    var timeStamp: String? = null,
    @SerializedName("Days")
    var days: Int? = null,
    @SerializedName("Remaining")
    var remaining: Int? = 0
) : Serializable {}
