package com.github.privacyDashboard.communication.repositories

import android.content.Context
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.OrganizationDetailResponse
import com.github.privacyDashboard.utils.BBConsentDataUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetOrganizationDetailApiRepository(var context: Context) {
    private val apiService: BBConsentAPIServices = BBConsentAPIManager.getApi(
        BBConsentDataUtils.getStringValue(
            context,
            BBConsentDataUtils.EXTRA_TAG_TOKEN
        ) ?: "",
        BBConsentDataUtils.getStringValue(
            context,
            BBConsentDataUtils.EXTRA_TAG_BASE_URL
        )
    )?.service!!

    fun getOrganizationDetail(orgId: String?, callback: Callback<OrganizationDetailResponse?>) {
        val call: Call<OrganizationDetailResponse?>? = apiService.getOrganizationDetail(orgId)
        call?.enqueue(object : Callback<OrganizationDetailResponse?> {
            override fun onResponse(
                call: Call<OrganizationDetailResponse?>,
                response: Response<OrganizationDetailResponse?>
            ) {
                callback.onResponse(call, response)
            }

            override fun onFailure(call: Call<OrganizationDetailResponse?>, t: Throwable) {
                callback.onFailure(call, t)
            }
        })
    }
}