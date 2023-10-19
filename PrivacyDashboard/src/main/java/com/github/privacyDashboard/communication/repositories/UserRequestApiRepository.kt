package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices

class UserRequestApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun dataDownloadRequest(
        orgId: String?
    ): Result<Void?>? {
        return try {
            val response = apiService.dataDownloadRequest(
                orgId = orgId
            )
            if (response.isSuccessful) {
                val data = response.body()
                if (response.code() == 200) {
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

    suspend fun deleteDataRequest(
        orgId: String?
    ): Result<Void?>? {
        return try {
            val response = apiService.dataDeleteRequest(
                orgId = orgId
            )
            if (response.isSuccessful) {
                val data = response.body()
                if (response.code() == 200) {
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