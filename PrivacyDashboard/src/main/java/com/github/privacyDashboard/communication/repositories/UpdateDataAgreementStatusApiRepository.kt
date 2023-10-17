package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.models.uiModels.consent.ResultResponse
import com.github.privacyDashboard.models.uiModels.consent.UpdateConsentStatusResponse
import retrofit2.http.Body
import retrofit2.http.Path

class UpdateDataAgreementStatusApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun updateDataAgreementStatus(
        orgID: String?,
        userId: String?,
        consentId: String?,
        purposeId: String?,
        body: ConsentStatusRequest?
    ): Result<UpdateConsentStatusResponse?> {
        return try {
            val response = apiService.setOverallStatus(
                orgID = orgID,
                userId = userId,
                consentId = consentId,
                purposeId = purposeId,
                body = body
            )
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