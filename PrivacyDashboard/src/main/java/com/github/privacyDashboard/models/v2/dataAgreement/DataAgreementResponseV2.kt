package com.github.privacyDashboard.models.v2.dataAgreement

import com.github.privacyDashboard.models.v2.PaginationV2
import com.google.gson.annotations.SerializedName

class DataAgreementResponseV2(
    @SerializedName("dataAgreements") var dataAgreements: ArrayList<DataAgreementV2> = arrayListOf(),
    @SerializedName("pagination") var pagination: PaginationV2? = PaginationV2()
)