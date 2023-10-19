package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.consentHistory.ConsentHistoryResponseV1
import com.github.privacyDashboard.models.uiModels.consentHistory.ConsentHistoryResponse
import com.github.privacyDashboard.models.uiModels.userRequests.UserRequestHistoryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Path
import retrofit2.http.Query

class UserRequestsApiRepository(private var apiService: BBConsentAPIServices) {

    suspend fun getUserRequests(
        orgId: String?,
        startid: String?
    ): Result<UserRequestHistoryResponse?>? {

        return try {
            val response = apiService.getOrgRequestStatus(
                orgId = orgId,
                startid = startid
            )
            if (response.isSuccessful) {
                val data = response.body()
                if (response.code() ==200 && data != null) {
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