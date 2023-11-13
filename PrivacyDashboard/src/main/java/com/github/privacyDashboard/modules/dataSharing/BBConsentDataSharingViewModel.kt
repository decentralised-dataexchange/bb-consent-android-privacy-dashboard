package com.github.privacyDashboard.modules.dataSharing

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.communication.repositories.GetDataAgreementApiRepository
import com.github.privacyDashboard.communication.repositories.GetDataAgreementRecordApiRepository
import com.github.privacyDashboard.communication.repositories.GetOrganisationDetailApiRepository
import com.github.privacyDashboard.communication.repositories.UpdateDataAgreementStatusApiRepository
import com.github.privacyDashboard.models.Organization
import com.github.privacyDashboard.models.v2.consent.ConsentStatusRequestV2
import com.github.privacyDashboard.models.v2.dataAgreement.DataAgreementV2
import com.github.privacyDashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementRecordsV2
import com.github.privacyDashboard.modules.base.BBConsentBaseViewModel
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BBConsentDataSharingViewModel : BBConsentBaseViewModel() {

    val fetchingDataAgreementRecord = MutableLiveData<Boolean>(true)
    val organization = MutableLiveData<Organization?>()
    val dataAgreement = MutableLiveData<DataAgreementV2?>(null)

    val dataAgreementRecord = MutableLiveData<DataAgreementRecordsV2?>(null)

    var otherApplicationName: String? = null
    var otherApplicationLogo: String? = null

    init {
        isLoading.value = false
    }

    fun fetchDataAgreementRecord(showProgress: Boolean, context: Context) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = showProgress

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

            val organizationDetailRepository = GetDataAgreementRecordApiRepository(apiService)

            GlobalScope.launch {
                val result = organizationDetailRepository.getDataAgreementRecord(
                    BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_USERID,
                        null
                    ),
                    BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_DATA_AGREEMENT_ID,
                        null
                    )
                )

                if (result.isSuccess && result.getOrNull()?.dataAgreementRecord != null) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        dataAgreementRecord.value = result.getOrNull()?.dataAgreementRecord
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        getOrganizationDetail(true, context)
//                        fetchingDataAgreementRecord.value = false
                    }
                }
            }
        }
    }

    fun getOrganizationDetail(showProgress: Boolean, context: Context) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = showProgress

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

            val organizationDetailRepository = GetOrganisationDetailApiRepository(apiService)

            GlobalScope.launch {
                val result = organizationDetailRepository.getOrganizationDetail(
                    BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_USERID,
                        null
                    )
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        getDataAgreement(true, context, result.getOrNull()?.organization)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun getDataAgreement(showProgress: Boolean, context: Context, organizationCopy: Organization?) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = showProgress

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

            val organizationDetailRepository = GetDataAgreementApiRepository(apiService)

            GlobalScope.launch {
                val result = organizationDetailRepository.getDataAgreement(
                    BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_USERID,
                        null
                    ), BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_DATA_AGREEMENT_ID,
                        null
                    )
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        organization.value = organizationCopy
                        dataAgreement.value = result.getOrNull()?.dataAgreement
                        fetchingDataAgreementRecord.value = false
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun authorizeRequest(context: Context) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = true
            val body = ConsentStatusRequestV2()
            body.optIn = true

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

            val updateDataAgreementStatusApiRepository =
                UpdateDataAgreementStatusApiRepository(apiService)

            GlobalScope.launch {
                val result = updateDataAgreementStatusApiRepository.updateDataAgreementStatus(
                    userId = BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_USERID,
                        null
                    ),
                    dataAgreementId = BBConsentDataUtils.getStringValue(
                        context,
                        BBConsentDataUtils.EXTRA_TAG_DATA_AGREEMENT_ID,
                        null
                    ),
                    body = body
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        dataAgreementRecord.value = result.getOrNull()?.dataAgreementRecord
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