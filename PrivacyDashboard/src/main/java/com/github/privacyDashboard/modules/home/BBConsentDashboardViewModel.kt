package com.github.privacyDashboard.modules.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.communication.repositories.GetConsentsByIdApiRepository
import com.github.privacyDashboard.communication.repositories.GetDataAgreementsApiRepository
import com.github.privacyDashboard.communication.repositories.GetOrganisationDetailApiRepository
import com.github.privacyDashboard.communication.repositories.UpdateDataAgreementStatusApiRepository
import com.github.privacyDashboard.models.Organization
import com.github.privacyDashboard.models.OrganizationDetailResponse
import com.github.privacyDashboard.models.PurposeConsent
import com.github.privacyDashboard.models.v2.consent.ConsentStatusRequestV2
import com.github.privacyDashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementLatestRecordResponseV2
import com.github.privacyDashboard.modules.base.BBConsentBaseViewModel
import com.github.privacyDashboard.modules.dataAttribute.BBConsentDataAttributeListingActivity
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

    var isListLoading = MutableLiveData<Boolean>()

    private fun updateUI(orgDetail: OrganizationDetailResponse?) {
        consentId = orgDetail?.consentID
        purposeConsents.value = orgDetail?.purposeConsents ?: ArrayList()
    }

    private fun updateOrganization(orgDetail: OrganizationDetailResponse?) {
        organization.value = orgDetail?.organization
        orgName.value = orgDetail?.organization?.name
        orgLocation.value = orgDetail?.organization?.location
        orgDescription.value = orgDetail?.organization?.description
    }

    init {
        isLoading.value = true
        isListLoading.value = false
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

            val organizationDetailRepository = GetOrganisationDetailApiRepository(apiService)

            GlobalScope.launch {
                val result = organizationDetailRepository.getOrganizationDetail(
                    BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_USERID
                    )
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        isListLoading.value = purposeConsents.value?.isEmpty()
                        updateOrganization(result.getOrNull())
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        isListLoading.value = purposeConsents.value?.isEmpty()
                    }
                }
            }
        }
    }

    fun getDataAgreements(showProgress: Boolean, context: Context) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context)) {
//            isLoading.value = showProgress

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

            val organizationDetailRepository = GetDataAgreementsApiRepository(apiService)

            GlobalScope.launch {
                val result = organizationDetailRepository.getOrganizationDetail(
                    BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_USERID
                    )
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isListLoading.value = false
                        updateUI(result.getOrNull())
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isListLoading.value = false
                    }
                }
            }
        }
    }

    fun setOverallStatus(consent: PurposeConsent?, isChecked: Boolean?, context: Context) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = true
            val body = ConsentStatusRequestV2()
            body.optIn = isChecked == true

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

            val updateDataAgreementStatusApiRepository =
                UpdateDataAgreementStatusApiRepository(apiService)

            GlobalScope.launch {
                val result = updateDataAgreementStatusApiRepository.updateDataAgreementStatus(
                    userId = BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_USERID
                    ),
                    dataAgreementId = consent?.purpose?.iD,
                    body = body
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
//                        getDataAgreements(false, context)
                        updatePurposeConsent(result.getOrNull())
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        } else {
//            adapter!!.notifyDataSetChanged()
        }
    }

    private fun updatePurposeConsent(record: DataAgreementLatestRecordResponseV2?) {
        val count =
            purposeConsents.value?.find { it.purpose?.iD == record?.dataAgreementRecord?.dataAgreementId }?.count?.total

        purposeConsents.value?.find { it.purpose?.iD == record?.dataAgreementRecord?.dataAgreementId }?.count?.consented =
            if (record?.dataAgreementRecord?.optIn == true) count else 0

        purposeConsents.value = purposeConsents.value
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
                    userId = BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_USERID
                    ),
                    dataAgreementId = consent?.purpose?.iD,
                    isAllAllowed = consent?.count?.consented == consent?.count?.total
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