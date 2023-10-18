package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.models.consent.ResultResponseV1
import com.github.privacyDashboard.models.uiModels.ResultResponse
import com.github.privacyDashboard.models.uiModels.userRequests.UserRequestGenResponse
import retrofit2.http.Path

class CancelDataRequestApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun cancelDataRequest(
        orgId: String?,
        requestId: String?
    ): Result<UserRequestGenResponse?>? {
        return try {
            val response = apiService.dataDownloadCancelRequest(
                orgId = orgId,
                requestId = requestId
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

    suspend fun cancelDeleteDataRequest(
        orgId: String?,
        requestId: String?
    ): Result<UserRequestGenResponse?>? {
        return try {
            val response = apiService.dataDeleteCancelRequest(
                orgId = orgId,
                requestId = requestId
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