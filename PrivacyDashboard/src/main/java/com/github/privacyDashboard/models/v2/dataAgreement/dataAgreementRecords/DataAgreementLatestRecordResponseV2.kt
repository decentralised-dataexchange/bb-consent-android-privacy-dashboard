package com.github.privacyDashboard.models.v2.dataAgreement.dataAgreementRecords

import com.google.gson.annotations.SerializedName

data class DataAgreementLatestRecordResponseV2(
    @SerializedName("dataAgreementRecord")
    var dataAgreementRecord: DataAgreementRecordsV2? = null,
)
