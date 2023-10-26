package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.*
import com.github.privacyDashboard.models.v2.dataAgreement.DataAgreementResponseV2
import com.github.privacyDashboard.models.v2.dataAgreement.DataAgreementV2
import com.github.privacyDashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementRecordsResponseV2
import com.github.privacyDashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementRecordsV2
import com.github.privacyDashboard.models.v2.dataAgreement.organization.OrganizationResponseV2
import retrofit2.Response

class GetOrganizationDetailApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun getOrganizationDetail(
        userId: String?,
    ): Result<OrganizationDetailResponse?> {
        return try {
            //v2
            val organizationResponse = apiService.getOrganizationDetailV2(userId)

            val dataAgreementsResponse = apiService.getDataAgreementsV2(userId)

            val dataAgreementRecordResponseV2 = apiService.getDataAgreementRecordsV2(userId)

            val org = convertV2toBaseModel(
                organizationResponse,
                dataAgreementsResponse,
                dataAgreementRecordResponseV2,
                userId
            )

            if (dataAgreementsResponse?.isSuccessful == true) {
                val data = dataAgreementsResponse.body()
                if (data != null) {
                    Result.success(org)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${dataAgreementsResponse?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun convertV2toBaseModel(
        organizationResponse: Response<OrganizationResponseV2>,
        dataAgreementsResponse: Response<DataAgreementResponseV2>,
        dataAgreementRecordResponseV2: Response<DataAgreementRecordsResponseV2>,
        userId: String?
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
            ), consentID = null,
            purposeConsents = convertV2ListToBaseModel(
                dataAgreementsResponse.body()?.dataAgreements,
                dataAgreementRecordResponseV2.body()?.dataAgreementRecords, userId
            )
        )
    }

    private suspend fun convertV2ListToBaseModel(
        consents: ArrayList<DataAgreementV2>?,
        dataAgreementRecords: ArrayList<DataAgreementRecordsV2>?,
        userId: String?
    ): ArrayList<PurposeConsent> {
        var purposeConsents = consents?.map {

            var dataAgreementRecordsV2: DataAgreementRecordsV2? = null
            try {
                dataAgreementRecordsV2 =
                    dataAgreementRecords?.last { dataAgreementRecordsV2 -> dataAgreementRecordsV2.dataAgreementId == it.id }
            } catch (e: Exception) {
                if (!(it.lawfulBasis == "consent" || it.lawfulBasis == "legitimate_interest")) {
                    val createDataAgreementResponse =
                        apiService.createDataAgreementRecordV2(userId, it.id)
                    dataAgreementRecordsV2 = createDataAgreementResponse.body()?.dataAgreementRecord
                }
            }
            val dataAgreementRecordResponseV2 = apiService.getAttributeListV2(userId, it.id)

            PurposeConsent(
                purpose = PurposeV1(
                    iD = it.id,
                    name = it.purpose,
                    description = it.purposeDescription,
                    lawfulUsage = !(it.lawfulBasis == "consent" || it.lawfulBasis == "legitimate_interest"),
                    policyURL = it.policy?.url
                ),
                count = Count(
                    total = dataAgreementRecordResponseV2?.body()?.dataAttributes?.size,
                    consented = if (dataAgreementRecordsV2?.optIn == true) dataAgreementRecordResponseV2?.body()?.dataAttributes?.size else 0
                )
            )
        }
        return ArrayList(purposeConsents)
    }
}