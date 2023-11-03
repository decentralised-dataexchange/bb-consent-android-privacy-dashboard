package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.models.interfaces.consent.UpdateConsentStatusResponse
import com.github.privacyDashboard.models.v2.consent.ConsentStatusRequestV2
import com.github.privacyDashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementLatestRecordResponseV2
import retrofit2.Response

class UpdateDataAgreementStatusApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun updateDataAgreementStatus(
        userId: String?,
        dataAgreementId: String?,
        body: ConsentStatusRequestV2?
    ): Result<DataAgreementLatestRecordResponseV2?> {
        return try {
            val dataAgreementRecord = apiService.getDataAgreementRecordV2(userId, dataAgreementId)
            var response: Response<DataAgreementLatestRecordResponseV2?>? = null
            if (dataAgreementRecord?.isSuccessful != true) {
                response = apiService.createDataAgreementRecordV2(userId, dataAgreementId)
            } else {
                response = apiService.setOverallStatusV2(
                    userID = userId,
                    dataAgreementRecordId = dataAgreementRecord?.body()?.dataAgreementRecord?.id,
                    dataAgreementId = dataAgreementId,
                    body = body
                )
            }
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}