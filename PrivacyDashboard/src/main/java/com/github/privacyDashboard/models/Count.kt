package com.github.privacyDashboard.models

import com.google.gson.annotations.SerializedName

data class Count(
    @SerializedName("Total")
    var total: Int? = null,
    @SerializedName("Consented")
    var consented: Int? = null
) {}
