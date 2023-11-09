package com.github.privacyDashboard.modules.attributeDetail

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.communication.repositories.UpdateDataAttributeStatusApiRepository
import com.github.privacyDashboard.events.RefreshHome
import com.github.privacyDashboard.events.RefreshList
import com.github.privacyDashboard.models.base.attribute.DataAttribute
import com.github.privacyDashboard.models.base.attribute.Status
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.modules.base.BBConsentBaseViewModel
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus

class BBConsentDataAttributeDetailViewModel() : BBConsentBaseViewModel() {

    var mPurposeId: String? = ""
    var mConsentId: String? = ""
    var mOrgId: String? = ""
    var mDataAttribute: DataAttribute? = null

    var ivAllow = MutableLiveData<Boolean>(true)
    var ivDisAllow = MutableLiveData<Boolean>(true)
    var statusMessage = MutableLiveData<String>("")

    init {
        isLoading.value = false
    }

    fun updateConsentStatus(body: ConsentStatusRequest, context: Context) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = true

            val apiService: BBConsentAPIServices = BBConsentAPIManager.getApi(
                apiKey = BBConsentDataUtils.getStringValue(
                    context,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN,
                    null
                ),
                accessToken = BBConsentDataUtils.getStringValue(
                    context,
                    BBConsentDataUtils.EXTRA_TAG_ACCESS_TOKEN,
                    null
                ),
                baseUrl = BBConsentDataUtils.getStringValue(
                    context,
                    BBConsentDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service!!

            val updateDataAttributeStatusApiRepository =
                UpdateDataAttributeStatusApiRepository(apiService)

            GlobalScope.launch {
                val result = updateDataAttributeStatusApiRepository.updateAttributeStatus(
                    BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_ORG_ID
                    ),
                    BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_USERID,
                        null
                    ),
                    mConsentId,
                    mPurposeId,
                    mDataAttribute?.iD,
                    body
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        val status: Status? = mDataAttribute?.status
                        status?.consented = (body.consented)
                        status?.remaining = (body.days)
                        mDataAttribute?.status = status
                        try {
                            setChecked(context)
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        EventBus.getDefault().post(RefreshHome())
                        EventBus.getDefault()
                            .post(RefreshList(mDataAttribute?.iD, status))
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun setChecked(context: Context) {
        try {
            if (mDataAttribute?.status?.consented.equals("Allow", ignoreCase = true)
            ) {
                ivAllow.value = true
                ivDisAllow.value = false
                statusMessage.value =
                    context.resources.getString(R.string.bb_consent_data_attribute_detail_allow_consent_rule)
            } else if (mDataAttribute?.status?.consented.equals(
                    "Disallow",
                    ignoreCase = true
                )
            ) {
                ivDisAllow.value = true
                ivAllow.value = false
                statusMessage.value =
                    context.resources.getString(R.string.bb_consent_data_attribute_detail_disallow_consent_rule)
            } else {
                ivDisAllow.value = false
                ivAllow.value = false
                statusMessage.value =
                    context.resources.getString(R.string.bb_consent_data_attribute_detail_askme_consent_rule)
            }
        } catch (e: Exception) {
            ivDisAllow.value = false
            ivAllow.value = false
        }
    }

}