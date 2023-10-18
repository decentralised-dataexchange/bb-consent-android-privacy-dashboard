package com.github.privacyDashboard.modules.userRequest

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.communication.repositories.CancelDataRequestApiRepository
import com.github.privacyDashboard.communication.repositories.GetConsentsByIdApiRepository
import com.github.privacyDashboard.communication.repositories.GetOrganizationDetailApiRepository
import com.github.privacyDashboard.communication.repositories.UserRequestApiRepository
import com.github.privacyDashboard.models.Organization
import com.github.privacyDashboard.models.OrganizationDetailResponse
import com.github.privacyDashboard.models.PurposeConsent
import com.github.privacyDashboard.models.attributes.DataAttributesResponse
import com.github.privacyDashboard.models.userRequests.UserRequest
import com.github.privacyDashboard.models.userRequests.UserRequestGenResponseV1
import com.github.privacyDashboard.models.userRequests.UserRequestHistoryResponse
import com.github.privacyDashboard.modules.base.BBConsentBaseViewModel
import com.github.privacyDashboard.modules.dataAttribute.BBConsentDataAttributeListingActivity
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentMessageUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BBConsentUserRequestViewModel() : BBConsentBaseViewModel() {

    var mOrgName: String? = ""
    var mOrgId: String? = ""

    var isLoadingData = false
    var hasLoadedAllItems = false

    private var startId = ""

    private var isDownloadRequestOngoing: Boolean? = false
    private var isDeleteRequestOngoing: Boolean? = false

    var requestHistories = MutableLiveData<ArrayList<UserRequest>>(ArrayList())
    var listVisibility = MutableLiveData<Boolean>(false)

    init {
        isLoading.value = true
    }

    fun cancelDataRequest(isDownloadData: Boolean, request: UserRequest, context: Context) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = true

            val apiService: BBConsentAPIServices = BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    context,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(
                    context,
                    BBConsentDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service!!

            val cancelDataRequestApiRepository = CancelDataRequestApiRepository(apiService)

            GlobalScope.launch {
                val result = if (isDownloadData)
                    cancelDataRequestApiRepository.cancelDataRequest(mOrgId, request.iD)
                else
                    cancelDataRequestApiRepository.cancelDeleteDataRequest(mOrgId, request.iD)

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.bb_consent_user_request_request_cancelled),
                            Toast.LENGTH_SHORT
                        ).show()
                        refreshData(context)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.bb_consent_error_unexpected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun getRequestHistory(showProgress: Boolean, context: Context) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context)) {
            isLoadingData = true
            if (showProgress) isLoading.value = true
            val callback: Callback<UserRequestHistoryResponse> =
                object : Callback<UserRequestHistoryResponse> {
                    override fun onResponse(
                        call: Call<UserRequestHistoryResponse>,
                        response: Response<UserRequestHistoryResponse>
                    ) {
                        isLoadingData = false
                        isLoading.value = false
                        if (response.code() == 200) {
                            if (response.body()?.dataRequests != null) {
                                if (startId.equals("", ignoreCase = true)) {
                                    requestHistories.value = ArrayList()
                                    isDownloadRequestOngoing =
                                        response.body()?.dataDownloadRequestOngoing
                                    isDeleteRequestOngoing =
                                        response.body()?.dataDeleteRequestOngoing
                                    val tempList = requestHistories.value
                                    tempList?.addAll(
                                        setOngoingRequests(
                                            response.body()?.dataRequests ?: ArrayList()
                                        )
                                    )
                                    requestHistories.value = tempList
                                } else {
                                    val tempList = requestHistories.value
                                    tempList?.addAll(response.body()?.dataRequests ?: ArrayList())
                                    requestHistories.value = tempList
                                }
                                if ((requestHistories.value?.size ?: 0) > 0) {
                                    startId =
                                        requestHistories.value?.get(
                                            (requestHistories.value?.size ?: 1) - 1
                                        )?.iD ?: ""
                                }
                                listVisibility.value = (requestHistories.value?.size ?: 0) > 0

                            } else {
                                if (startId.equals("", ignoreCase = true)) {
                                    requestHistories.value = ArrayList()
                                    listVisibility.value = (requestHistories.value?.size ?: 0) > 0

                                }
                            }
                            if (response.body()?.dataRequests == null) {
                                hasLoadedAllItems = true
                            }
                        }
                    }

                    override fun onFailure(call: Call<UserRequestHistoryResponse>, t: Throwable) {
                        isLoadingData = false
                        isLoading.value = false
                    }
                }
            BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    context,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(context, BBConsentDataUtils.EXTRA_TAG_BASE_URL)
            )?.service?.getOrgRequestStatus(
                mOrgId, startId
            )?.enqueue(callback)
        }
    }

    private fun setOngoingRequests(dataRequests: ArrayList<UserRequest>?): Collection<UserRequest> {
        if (dataRequests != null) {
            for (data in dataRequests) {
                if (data.type == 1 && isDeleteRequestOngoing!!) {
                    data.isOnGoing = (true)
                    isDeleteRequestOngoing = false
                }
                if (data.type == 2 && isDownloadRequestOngoing!!) {
                    data.isOnGoing = (true)
                    isDownloadRequestOngoing = false
                }
                if (!isDownloadRequestOngoing!! && !isDeleteRequestOngoing!!) {
                    return dataRequests
                }
            }
        }
        return dataRequests ?: ArrayList()
    }

    fun refreshData(context: Context) {
        requestHistories.value?.clear()
        startId = ""
        getRequestHistory(true, context)
    }

    fun dataDeleteRequest(context: Context) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context, true)) {
            isLoading.value = true
            val apiService: BBConsentAPIServices = BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    context,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(
                    context,
                    BBConsentDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service!!

            val userRequestApiRepository = UserRequestApiRepository(apiService)

            GlobalScope.launch {
                val result = userRequestApiRepository.deleteDataRequest(mOrgId)

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        refreshData(context)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.bb_consent_error_unexpected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun dataDownloadRequest(context: Context) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context, true)) {
            isLoading.value = true
            val apiService: BBConsentAPIServices = BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    context,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(
                    context,
                    BBConsentDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service!!

            val userRequestApiRepository = UserRequestApiRepository(apiService)

            GlobalScope.launch {
                val result = userRequestApiRepository.dataDownloadRequest(mOrgId)

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        refreshData(context)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.bb_consent_error_unexpected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}