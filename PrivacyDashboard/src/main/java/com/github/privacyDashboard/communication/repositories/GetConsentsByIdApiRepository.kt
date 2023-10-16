package com.github.privacyDashboard.communication.repositories

import android.content.Context
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.OrganizationDetailResponse
import com.github.privacyDashboard.models.attributes.DataAttributesResponse
import com.github.privacyDashboard.models.logging.ConsentHistoryResponse
import com.github.privacyDashboard.models.uiModels.dataAttributesList.DataAgreement
import com.github.privacyDashboard.utils.BBConsentDataUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetConsentsByIdApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun getConsentsById(
        orgId: String?,
        userId: String?,
        consentId: String?,
        purposeId: String?,
    ): Result<DataAgreement?> {

        return try {
            val response = apiService.getConsentList(
                orgID = orgId, userId = userId,
                consentId = consentId, purposeId = purposeId
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