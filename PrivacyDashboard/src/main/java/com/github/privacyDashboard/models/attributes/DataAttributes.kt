package com.github.privacyDashboard.models.attributes

import com.github.privacyDashboard.models.Count
import com.github.privacyDashboard.models.Purpose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class DataAttributes (
    @SerializedName("Purpose")
    var purpose: Purpose? = null,
    @SerializedName("Count")
    var count: Count? = null,
    @SerializedName("Consents")
    var consents: ArrayList<DataAttribute>? = null
): Serializable {}
