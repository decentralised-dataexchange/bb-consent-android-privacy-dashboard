package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.attributes.DataAttributesResponseV1
import com.github.privacyDashboard.models.base.Purpose
import com.github.privacyDashboard.models.base.attribute.DataAttribute
import com.github.privacyDashboard.models.base.attribute.DataAttributes
import com.github.privacyDashboard.models.base.attribute.DataAttributesResponse
import com.github.privacyDashboard.models.base.attribute.Status

class GetConsentsByIdApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun getConsentsById(
        orgId: String?,
        userId: String?,
        consentId: String?,
        purposeId: String?,
    ): Result<DataAttributesResponse?> {

        return try {
            val response = apiService.getConsentList(
                orgID = orgId, userId = userId,
                consentId = consentId, purposeId = purposeId
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    val processedData = v1ToModel(data)
                    Result.success(processedData)
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

    private fun v1ToModel(data: DataAttributesResponseV1): DataAttributesResponse {
        val newList: List<DataAttribute?>? = data.consents?.consents?.map { original ->
            DataAttribute(
                original?.iD,
                original?.description,
                Status(original?.status?.consented, original?.status?.remaining)
            )
        }
        return DataAttributesResponse(
            data.iD, data.consentID, data.orgID,
            DataAttributes(
                purpose = Purpose(
                    data.consents?.purpose?.iD,
                    name = data.consents?.purpose?.name,
                    description = data.consents?.purpose?.description,
                    lawfulUsage = data.consents?.purpose?.lawfulUsage,
                    policyURL = data.consents?.purpose?.policyURL
                ),
                consents = ArrayList(newList)
            )
        )
    }
}