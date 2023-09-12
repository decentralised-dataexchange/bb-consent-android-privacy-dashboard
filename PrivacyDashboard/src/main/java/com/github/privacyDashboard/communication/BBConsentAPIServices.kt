package com.github.privacyDashboard.communication

import com.github.privacyDashboard.models.OrganizationDetailResponse
import com.github.privacyDashboard.models.attributes.DataAttributesResponse
import retrofit2.Call
import retrofit2.http.*
import com.github.privacyDashboard.models.logging.ConsentHistoryResponse

interface BBConsentAPIServices {
    @GET("v1/GetUserOrgsAndConsents")
    fun getOrganizationDetail(
        @Query("orgID") orgID: String?
    ): Call<OrganizationDetailResponse?>?

    @GET("v1/users/{userID}/consenthistory")
    fun getConsentHistory(
        @Path("userID") userID: String?,
        @Query("limit") limit: Int,
        @Query("orgid") orgId: String?,
        @Query("startid") startid: String?
    ): Call<ConsentHistoryResponse>

    @GET("v1/organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}")
    fun getConsentList(
        @Path("orgID") orgID: String?,
        @Path("userId") userId: String?,
        @Path("consentId") consentId: String?,
        @Path("purposeId") purposeId: String?
    ): Call<DataAttributesResponse?>?
}