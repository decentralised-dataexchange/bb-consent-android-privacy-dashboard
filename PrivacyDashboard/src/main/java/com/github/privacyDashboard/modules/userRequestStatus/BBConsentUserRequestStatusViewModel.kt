package com.github.privacyDashboard.modules.userRequestStatus

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.communication.repositories.CancelDataRequestApiRepository
import com.github.privacyDashboard.communication.repositories.UserRequestStatusApiRepository
import com.github.privacyDashboard.models.uiModels.userRequests.UserRequestStatus
import com.github.privacyDashboard.models.userRequests.UserRequestStatusV1
import com.github.privacyDashboard.modules.base.BBConsentBaseViewModel
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentMessageUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BBConsentUserRequestStatusViewModel() : BBConsentBaseViewModel() {

    var shouldFinish = MutableLiveData<Boolean>(false)

    init {
        isLoading.value = true
    }

    var status = MutableLiveData<UserRequestStatus?>(null)


    fun cancelDataRequest(isDownloadData: Boolean?, requestId: String?, context: Context) {
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
                val result = if (isDownloadData == true)
                    cancelDataRequestApiRepository.cancelDataRequest(
                        BBConsentDataUtils.getStringValue(
                            context,
                            BBConsentDataUtils.EXTRA_TAG_ORG_ID
                        ), requestId
                    )
                else
                    cancelDataRequestApiRepository.cancelDeleteDataRequest(
                        BBConsentDataUtils.getStringValue(
                            context,
                            BBConsentDataUtils.EXTRA_TAG_ORG_ID
                        ), requestId
                    )

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.bb_consent_user_request_request_cancelled),
                            Toast.LENGTH_SHORT
                        ).show()
                        shouldFinish.value = true
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

    fun getDataRequestStatus(mIsDownloadData: Boolean?, context: Context) {
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

            val userRequestStatusApiRepository = UserRequestStatusApiRepository(apiService)

            GlobalScope.launch {
                val result = if (mIsDownloadData == true)
                    userRequestStatusApiRepository.dataDownloadStatusRequest(
                        BBConsentDataUtils.getStringValue(
                            context,
                            BBConsentDataUtils.EXTRA_TAG_ORG_ID
                        )
                    )
                else
                    userRequestStatusApiRepository.deleteDataStatusRequest(
                        BBConsentDataUtils.getStringValue(
                            context,
                            BBConsentDataUtils.EXTRA_TAG_ORG_ID
                        )
                    )

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        status.value = result.getOrNull()
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