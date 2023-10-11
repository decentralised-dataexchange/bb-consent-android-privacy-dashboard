package com.github.privacyDashboard.communication.repositories

import android.content.Context
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.OrganizationDetailResponse
import com.github.privacyDashboard.models.logging.ConsentHistoryResponse
import com.github.privacyDashboard.utils.BBConsentDataUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GetConsentHistoryApiRepository(var context: Context) {
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

    fun getConsentHistory(
        userId: String?,
        orgId: String?,
        limit: Int?,
        startId: String?,
        callback: Callback<ConsentHistoryResponse?>
    ) {
        val call: Call<ConsentHistoryResponse?> = apiService.getConsentHistory(userId,limit?:10,orgId,startId)
        call.enqueue(object : Callback<ConsentHistoryResponse?> {
            override fun onResponse(
                call: Call<ConsentHistoryResponse?>,
                response: Response<ConsentHistoryResponse?>
            ) {
                callback.onResponse(call, response)
            }

            override fun onFailure(call: Call<ConsentHistoryResponse?>, t: Throwable) {
                callback.onFailure(call, t)
            }
        })
    }
}