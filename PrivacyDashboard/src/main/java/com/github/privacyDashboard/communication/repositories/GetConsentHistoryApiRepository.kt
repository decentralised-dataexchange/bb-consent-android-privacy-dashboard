package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.interfaces.consentHistory.ConsentHistoryResponse

class GetConsentHistoryApiRepository(private var apiService: BBConsentAPIServices) {

    suspend fun getConsentHistory(
        userID: String?,
        limit: Int,
        orgId: String?,
        startid: String?
    ): Result<ConsentHistoryResponse?>? {

        return try {
            val response = apiService.getConsentHistory(
                userID = userID,
                limit = limit ?: 10,
                orgId = orgId,
                startid = startid
            )
            if (response.isSuccessful) {
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