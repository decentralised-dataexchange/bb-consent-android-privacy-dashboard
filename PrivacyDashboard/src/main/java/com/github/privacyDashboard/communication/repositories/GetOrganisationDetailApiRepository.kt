package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.*
import com.github.privacyDashboard.models.v2.dataAgreement.DataAgreementResponseV2
import com.github.privacyDashboard.models.v2.dataAgreement.DataAgreementV2
import com.github.privacyDashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementRecordsResponseV2
import com.github.privacyDashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementRecordsV2
import com.github.privacyDashboard.models.v2.dataAgreement.organization.OrganizationResponseV2
import retrofit2.Response

class GetOrganisationDetailApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun getOrganizationDetail(
        userId: String?,
    ): Result<OrganizationDetailResponse?> {
        return try {
            //v2
            val organizationResponse = apiService.getOrganizationDetailV2(userId)

            val org = convertV2toBaseModel(
                organizationResponse
            )

            if (organizationResponse.isSuccessful) {
                val data = organizationResponse.body()
                if (data != null) {
                    Result.success(org)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${organizationResponse.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun convertV2toBaseModel(
        organizationResponse: Response<OrganizationResponseV2>
    ): OrganizationDetailResponse {
        return OrganizationDetailResponse(
            organization = Organization(
                iD = organizationResponse.body()?.organisation?.id,
                name = organizationResponse.body()?.organisation?.name,
                coverImageURL = organizationResponse.body()?.organisation?.coverImageUrl ?: "",
                logoImageURL = organizationResponse.body()?.organisation?.logoImageUrl ?: "",
                type = organizationResponse.body()?.organisation?.sector,
                description = organizationResponse.body()?.organisation?.description,
                location = organizationResponse.body()?.organisation?.location,
                policyURL = organizationResponse.body()?.organisation?.policyUrl
            ), consentID = null
        )
    }
}