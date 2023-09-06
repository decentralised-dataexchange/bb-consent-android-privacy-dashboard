package com.github.privacyDashboard.models.attributes

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataAttribute(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("Description")
    var description: String? = null,
    @SerializedName("Value")
    var value: String? = null,
    @SerializedName("Status")
    var status: Status? = null
): Serializable {}
