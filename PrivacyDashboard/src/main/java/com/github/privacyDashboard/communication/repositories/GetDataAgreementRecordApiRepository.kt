package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.v2.consent.ConsentStatusRequestV2
import com.github.privacyDashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementLatestRecordResponseV2
import retrofit2.Response

class GetDataAgreementRecordApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun getDataAgreementRecord(
        userId: String?,
        dataAgreementId: String?
    ): Result<DataAgreementLatestRecordResponseV2?> {
        return try {
            val dataAgreementRecord = apiService.getDataAgreementRecordV2(userId, dataAgreementId)

            if (dataAgreementRecord?.isSuccessful == true) {
                val data = dataAgreementRecord.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${dataAgreementRecord?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}