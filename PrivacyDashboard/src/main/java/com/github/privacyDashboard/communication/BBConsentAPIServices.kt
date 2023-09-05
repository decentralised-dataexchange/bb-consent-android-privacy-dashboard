package com.github.privacyDashboard.communication

import com.github.privacyDashboard.models.OrganizationDetailResponse
import retrofit2.Call
import retrofit2.http.*

interface BBConsentAPIServices {
    @GET("v1/GetUserOrgsAndConsents")
    fun getOrganizationDetail(
        @Query("orgID") orgID: String?
    ): Call<OrganizationDetailResponse?>?

}