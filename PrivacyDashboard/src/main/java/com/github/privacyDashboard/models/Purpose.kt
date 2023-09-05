package com.github.privacyDashboard.models

import com.google.gson.annotations.SerializedName

data class Purpose (
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("Name")
    var name: String? = null,
    @SerializedName("Description")
    var description: String? = null,
    @SerializedName("LawfulUsage")
    var lawfulUsage: Boolean? = null,
    @SerializedName("PolicyURL")
    var policyURL: String? = null
){}
