 package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.models.consent.ResultResponseV1
import com.github.privacyDashboard.models.uiModels.ResultResponse

class UpdateDataAttributeStatusApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun updateAttributeStatus(
        orgID: String?,
        userId: String?,
        consentId: String?,
        purposeId: String?,
        attributeId: String?,
        body: ConsentStatusRequest?
    ): Result<ResultResponse?> {
        return try {
            val response = apiService.setAttributeStatus(
                orgID = orgID,
                userId = userId,
                consentId = consentId,
                purposeId = purposeId,
                attributeId = attributeId,
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