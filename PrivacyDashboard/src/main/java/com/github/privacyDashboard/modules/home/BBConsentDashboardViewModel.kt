package com.github.privacyDashboard.modules.home

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.communication.repositories.GetConsentsByIdApiRepository
import com.github.privacyDashboard.communication.repositories.GetOrganizationDetailApiRepository
import com.github.privacyDashboard.models.Organization
import com.github.privacyDashboard.models.OrganizationDetailResponse
import com.github.privacyDashboard.models.PurposeConsent
import com.github.privacyDashboard.models.attributes.DataAttributesResponse
import com.github.privacyDashboard.modules.base.BBConsentBaseViewModel
import com.github.privacyDashboard.modules.dataAttribute.BBConsentDataAttributeListingActivity
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BBConsentDashboardViewModel() : BBConsentBaseViewModel() {

    var orgName = MutableLiveData<String>("")
    var orgLocation = MutableLiveData<String>("")
    var orgDescription = MutableLiveData<String>("")

    val organization = MutableLiveData<Organization?>()
    var consentId: String? = null
    val purposeConsents = MutableLiveData<ArrayList<PurposeConsent>>()

    private fun updateUI(orgDetail: OrganizationDetailResponse?) {
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
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun getConsentList(consent: PurposeConsent?, context: Context) {
        isLoading.value = true
        if (BBConsentNetWorkUtil.isConnectedToInternet(context)) {

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

            val consentListRepository = GetConsentsByIdApiRepository(apiService)

            GlobalScope.launch {
                val result = consentListRepository.getConsentsById(
                    BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_ORG_ID
                    ),
                    BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_USERID
                    ),
                    consentId,
                    consent?.purpose?.iD
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        val intent = Intent(
                            context,
                            BBConsentDataAttributeListingActivity::class.java
                        )
                        intent.putExtra(
                            BBConsentDataAttributeListingActivity.TAG_EXTRA_NAME,
                            consent?.purpose?.name
                        )
                        intent.putExtra(
                            BBConsentDataAttributeListingActivity.TAG_EXTRA_DESCRIPTION,
                            consent?.purpose?.description
                        )
                        context.startActivity(intent)
                        BBConsentDataAttributeListingActivity.dataAttributesResponse =
                            result.getOrNull()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

}