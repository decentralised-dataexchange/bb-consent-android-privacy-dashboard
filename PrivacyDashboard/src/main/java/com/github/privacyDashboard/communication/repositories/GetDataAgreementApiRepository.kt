package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.v2.dataAgreement.DataAgreementResponseV2
import com.github.privacyDashboard.models.v2.dataAgreement.DataAgreementsResponseV2

class GetDataAgreementApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun getDataAgreement(
        userId: String?,
        dataAgreementId: String?
    ): Result<DataAgreementResponseV2?> {
        return try {
            val dataAgreementRecord = apiService.getDataAgreementV2(userId, dataAgreementId)

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