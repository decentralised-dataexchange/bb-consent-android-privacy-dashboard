package com.github.privacyDashboard.models.v2.dataAgreement.dataAgreementRecords

import com.github.privacyDashboard.models.v2.PaginationV2
import com.google.gson.annotations.SerializedName

class DataAgreementRecordResponseV2(
    @SerializedName("dataAgreementRecords")
    var dataAgreementRecords: ArrayList<DataAgreementRecordsV2> = arrayListOf(),
    @SerializedName("pagination")
    var pagination: PaginationV2? = PaginationV2()
)