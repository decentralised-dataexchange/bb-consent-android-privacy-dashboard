package com.github.privacyDashboard.modules.home

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.communication.repositories.GetOrganizationDetailApiRepository
import com.github.privacyDashboard.models.Organization
import com.github.privacyDashboard.models.OrganizationDetailResponse
import com.github.privacyDashboard.models.PurposeConsent
import com.github.privacyDashboard.modules.base.BBConsentBaseViewModel
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BBConsentDashboardViewModel() : BBConsentBaseViewModel() {

    var orgName = MutableLiveData<String>("")
    var orgLocation = MutableLiveData<String>("")
    var orgDescription = MutableLiveData<String>("")

    val organization = MutableLiveData<Organization?>()
    var consentId: String? = null
    val purposeConsents = MutableLiveData<ArrayList<PurposeConsent>>()

    fun updateUI(orgDetail: OrganizationDetailResponse?) {
        organization.value = orgDetail?.organization
        orgName.value = orgDetail?.organization?.name
        orgLocation.value = orgDetail?.organization?.location
        orgDescription.value = orgDetail?.organization?.description
        consentId = orgDetail?.consentID
        purposeConsents.value = orgDetail?.purposeConsents ?: ArrayList()
    }

    init {
        isLoading.value = true
        organization.value = null
        purposeConsents.value = ArrayList()
    }

    fun getOrganizationDetail(showProgress: Boolean, context: Context) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = showProgress

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

            val organizationDetailRepository = GetOrganizationDetailApiRepository(apiService)

            GlobalScope.launch {
                val result = organizationDetailRepository.getOrganizationDetail(
                    BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_ORG_ID
                    )
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        updateUI(result.getOrNull())
                    }
                } else {
                    isLoading.value = false
                }
            }
        }
    }
}