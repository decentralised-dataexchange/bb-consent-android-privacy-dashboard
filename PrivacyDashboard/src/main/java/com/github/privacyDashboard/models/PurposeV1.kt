package com.github.privacyDashboard.models

import com.github.privacyDashboard.models.uiModels.dataAttributesList.Purpose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PurposeV1 (
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
): Purpose {
    override val mId: String?
        get() = iD
    override val mName: String?
        get() = name
    override val mDescription: String?
        get() = description
    override val mLawfulUsage: Boolean?
        get() = lawfulUsage
    override val mPolicyUrl: String?
        get() = policyURL
}
