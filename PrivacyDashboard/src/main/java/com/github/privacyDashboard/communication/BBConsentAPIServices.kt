package com.github.privacyDashboard.communication

import com.github.privacyDashboard.models.OrganizationDetailResponse
import com.github.privacyDashboard.models.attributes.DataAttributesResponse
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.models.consent.ResultResponse
import com.github.privacyDashboard.models.consent.UpdateConsentStatusResponse
import com.github.privacyDashboard.models.logging.ConsentHistoryResponse
import com.github.privacyDashboard.models.userRequests.UserRequestGenResponse
import com.github.privacyDashboard.models.userRequests.UserRequestHistoryResponse
import com.github.privacyDashboard.models.userRequests.UserRequestStatus
import retrofit2.Call
import retrofit2.http.*

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
    ): Call<ConsentHistoryResponse?>

    @GET("v1/organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}")
    fun getConsentList(
        @Path("orgID") orgID: String?,
        @Path("userId") userId: String?,
        @Path("consentId") consentId: String?,
        @Path("purposeId") purposeId: String?
    ): Call<DataAttributesResponse?>?

    @PATCH("v1/organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}/attributes/{attributeId}")
    fun setAttributeStatus(
        @Path("orgID") orgID: String?,
        @Path("userId") userId: String?,
        @Path("consentId") consentId: String?,
        @Path("purposeId") purposeId: String?,
        @Path("attributeId") attributeId: String?,
        @Body body: ConsentStatusRequest?
    ): Call<ResultResponse?>?

    @POST("v1/organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}/status")
    fun setOverallStatus(
        @Path("orgID") orgID: String?,
        @Path("userId") userId: String?,
        @Path("consentId") consentId: String?,
        @Path("purposeId") purposeId: String?,
        @Body body: ConsentStatusRequest?
    ): Call<UpdateConsentStatusResponse?>?

    @GET("v1/user/organizations/{orgId}/data-status")
    fun getOrgRequestStatus(
        @Path("orgId") orgId: String?,
        @Query("startid") startid: String?
    ): Call<UserRequestHistoryResponse>

    //data download and delete
    @GET("v1/user/organizations/{orgId}/data-download/status")
    fun getDataDownloadStatus(
        @Path("orgId") orgId: String?
    ): Call<UserRequestStatus?>

    @GET("v1/user/organizations/{orgId}/data-delete/status")
    fun getDataDeleteStatus(
        @Path("orgId") orgId: String?
    ): Call<UserRequestStatus?>

    @POST("v1/user/organizations/{orgId}/data-delete")
    fun dataDeleteRequest(
        @Path("orgId") orgId: String?
    ): Call<Void>

    @POST("v1/user/organizations/{orgId}/data-download")
    fun dataDownloadRequest(
        @Path("orgId") orgId: String?
    ): Call<Void>

    @POST("v1/user/organizations/{orgId}/data-delete/{requestId}/cancel")
    fun dataDeleteCancelRequest(
        @Path("orgId") orgId: String?,
        @Path("requestId") requestId: String?
    ): Call<UserRequestGenResponse?>

    @POST("v1/user/organizations/{orgId}/data-download/{requestId}/cancel")
    fun dataDownloadCancelRequest(
        @Path("orgId") orgId: String?,
        @Path("requestId") requestId: String?
    ): Call<UserRequestGenResponse?>
}