package com.github.privacyDashboard.communication

import com.github.privacyDashboard.models.OrganizationDetailResponse
import com.github.privacyDashboard.models.attributes.DataAttributesResponseV1
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.models.consent.ResultResponseV1
import com.github.privacyDashboard.models.consent.UpdateConsentStatusResponseV1
import com.github.privacyDashboard.models.consentHistory.ConsentHistoryResponseV1
import com.github.privacyDashboard.models.userRequests.UserRequestGenResponseV1
import com.github.privacyDashboard.models.userRequests.UserRequestHistoryResponseV1
import com.github.privacyDashboard.models.userRequests.UserRequestStatusV1
import com.github.privacyDashboard.models.v2.consentHistory.ConsentHistoryResponseV2
import retrofit2.Response
import retrofit2.http.*

interface BBConsentAPIServices {
    @GET("v1/GetUserOrgsAndConsents")
    suspend fun getOrganizationDetail(
        @Query("orgID") orgID: String?
    ): Response<OrganizationDetailResponse?>?

    @GET("v1/users/{userID}/consenthistory")
    suspend fun getConsentHistory(
        @Path("userID") userID: String?,
        @Query("limit") limit: Int,
        @Query("orgid") orgId: String?,
        @Query("startid") startid: String?
    ): Response<ConsentHistoryResponseV1?>

    @GET("v2/service/individual/record/data-agreement-record/history")
    suspend fun getConsentHistoryV2(
        @Header("X-ConsentBB-IndividualId") userID: String?,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
    ): Response<ConsentHistoryResponseV2?>

    @GET("v1/organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}")
    suspend fun getConsentList(
        @Path("orgID") orgID: String?,
        @Path("userId") userId: String?,
        @Path("consentId") consentId: String?,
        @Path("purposeId") purposeId: String?
    ): Response<DataAttributesResponseV1?>?

    @PATCH("v1/organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}/attributes/{attributeId}")
    suspend fun setAttributeStatus(
        @Path("orgID") orgID: String?,
        @Path("userId") userId: String?,
        @Path("consentId") consentId: String?,
        @Path("purposeId") purposeId: String?,
        @Path("attributeId") attributeId: String?,
        @Body body: ConsentStatusRequest?
    ): Response<ResultResponseV1?>?

    @POST("v1/organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}/status")
    suspend fun setOverallStatus(
        @Path("orgID") orgID: String?,
        @Path("userId") userId: String?,
        @Path("consentId") consentId: String?,
        @Path("purposeId") purposeId: String?,
        @Body body: ConsentStatusRequest?
    ): Response<UpdateConsentStatusResponseV1?>?

    @GET("v1/user/organizations/{orgId}/data-status")
    suspend fun getOrgRequestStatus(
        @Path("orgId") orgId: String?,
        @Query("startid") startid: String?
    ): Response<UserRequestHistoryResponseV1>

    //data download and delete
    @GET("v1/user/organizations/{orgId}/data-download/status")
    suspend fun getDataDownloadStatus(
        @Path("orgId") orgId: String?
    ): Response<UserRequestStatusV1?>

    @GET("v1/user/organizations/{orgId}/data-delete/status")
    suspend fun getDataDeleteStatus(
        @Path("orgId") orgId: String?
    ): Response<UserRequestStatusV1?>

    @POST("v1/user/organizations/{orgId}/data-delete")
    suspend fun dataDeleteRequest(
        @Path("orgId") orgId: String?
    ): Response<Void>

    @POST("v1/user/organizations/{orgId}/data-download")
    suspend fun dataDownloadRequest(
        @Path("orgId") orgId: String?
    ): Response<Void>

    @POST("v1/user/organizations/{orgId}/data-delete/{requestId}/cancel")
    suspend fun dataDeleteCancelRequest(
        @Path("orgId") orgId: String?,
        @Path("requestId") requestId: String?
    ): Response<UserRequestGenResponseV1?>

    @POST("v1/user/organizations/{orgId}/data-download/{requestId}/cancel")
    suspend fun dataDownloadCancelRequest(
        @Path("orgId") orgId: String?,
        @Path("requestId") requestId: String?
    ): Response<UserRequestGenResponseV1?>?
}