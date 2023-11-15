package com.github.privacyDashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.communication.repositories.GetDataAgreementApiRepository
import com.github.privacyDashboard.communication.repositories.IndividualApiRepository
import com.github.privacyDashboard.communication.repositories.UpdateDataAgreementStatusApiRepository
import com.github.privacyDashboard.models.DataAgreementPolicyModel
import com.github.privacyDashboard.models.interfaces.dataAttributesList.DataAgreement
import com.github.privacyDashboard.models.v2.consent.ConsentStatusRequestV2
import com.github.privacyDashboard.models.v2.dataAgreement.DataAgreementV2
import com.github.privacyDashboard.models.v2.individual.IndividualRequest
import com.github.privacyDashboard.modules.dataAgreementPolicy.BBConsentDataAgreementPolicyActivity
import com.github.privacyDashboard.modules.home.BBConsentDashboardActivity
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ACCESS_TOKEN
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_BASE_URL
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ENABLE_ASK_ME
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ENABLE_ATTRIBUTE_LEVEL_CONSENT
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ENABLE_USER_REQUEST
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_TOKEN
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_USERID
import com.github.privacyDashboard.utils.BBConsentLocaleHelper
import com.google.gson.Gson


object PrivacyDashboard {

    private var destination: Int? = -1

    private var mUserId: String? = null
    private var mApiKey: String? = null
    private var mAccessToken: String? = null
    private var mBaseUrl: String? = ""
    private var mLocale: String? = ""
    private var mEnableUserRequest: Boolean? = false
    private var mEnableAskMe: Boolean? = false
    private var mEnableAttributeLevelConsent: Boolean? = false
    private var mDataAgreement: String? = null
    private var mIntent: Intent? = null

    fun showPrivacyDashboard(): PrivacyDashboard {
        destination = 0
        mIntent = Intent()
        return this
    }

    fun showDataAgreementPolicy(): PrivacyDashboard {
        destination = 1
        mIntent = Intent()
        return this
    }

    /**
     * Set data agreement for igrant sdk.
     *
     * @param userId
     */
    fun withDataAgreement(dataAgreement: String?): PrivacyDashboard {
        this.mDataAgreement = if (dataAgreement == "") null else
            dataAgreement
        return this
    }

    private fun buildListForDataAgreementPolicy(
        context: Context,
        dataAgreement: DataAgreementV2?
    ): String {
        var list: ArrayList<ArrayList<DataAgreementPolicyModel>> = ArrayList()
        var subList: ArrayList<DataAgreementPolicyModel> = ArrayList()
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.bb_consent_data_agreement_policy_purpose),
                dataAgreement?.purpose
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.bb_consent_data_agreement_policy_purpose_description),
                dataAgreement?.purposeDescription
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.bb_consent_data_agreement_policy_lawful_basis_of_processing),
                dataAgreement?.lawfulBasis
            )
        )
        list.add(subList)
        subList = ArrayList()
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.bb_consent_data_agreement_policy_policy_url),
                dataAgreement?.policy?.url
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.bb_consent_data_agreement_policy_jurisdiction),
                dataAgreement?.policy?.jurisdiction
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.bb_consent_data_agreement_policy_third_party_disclosure),
                dataAgreement?.policy?.thirdPartyDataSharing.toString()
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.bb_consent_data_agreement_policy_industry_scope),
                dataAgreement?.policy?.industrySector
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.bb_consent_data_agreement_policy_geographic_restriction),
                dataAgreement?.policy?.geographicRestriction
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.bb_consent_data_agreement_policy_retention_period),
                dataAgreement?.policy?.dataRetentionPeriodDays.toString()
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.bb_consent_data_agreement_policy_storage_location),
                dataAgreement?.policy?.storageLocation
            )
        )
        list.add(subList)

        return Gson().toJson(list)
    }

    /**
     * Set user id for igrant sdk.
     *
     * @param userId
     */
    fun withUserId(userId: String?): PrivacyDashboard {
        this.mUserId = if (userId == "") null else userId
        return this
    }

    /**
     * Set Api key for the iGrant Sdk.
     *
     * @param apiKey
     */
    fun withApiKey(apiKey: String?): PrivacyDashboard {
        this.mApiKey = if (apiKey == "") null else apiKey
        return this
    }

    /**
     * Set Access token for the iGrant Sdk.
     *
     * @param accessToken
     */
    fun withAccessToken(accessToken: String?): PrivacyDashboard {
        this.mAccessToken = if (accessToken == "") null else accessToken
        return this
    }

    /**
     * Set base url for the iGrant Sdk.
     *
     * @param baseUrl
     */
    fun withBaseUrl(baseUrl: String?): PrivacyDashboard {
        if (baseUrl?.last().toString() == "/")
            this.mBaseUrl = baseUrl
        else
            this.mBaseUrl = "$baseUrl/"
        return this
    }

    /**
     * Set Language code for the iGrant Sdk.
     *
     * @param languageCode
     */
    fun withLocale(languageCode: String): PrivacyDashboard {
        this.mLocale = languageCode
        return this
    }

    /**
     * To enable the user request for the iGrant Sdk.
     */
    fun enableUserRequest(): PrivacyDashboard {
        this.mEnableUserRequest = true
        return this
    }

    /**
     * To enable the ask me in consent detail screen for the iGrant Sdk.
     */
    fun enableAskMe(): PrivacyDashboard {
        this.mEnableAskMe = true
        return this
    }

    /**
     * To enable updating attribute level consent in iGrant Sdk.
     */
    fun enableAttributeLevelConsent(): PrivacyDashboard {
        this.mEnableAttributeLevelConsent = true
        return this
    }

    /**
     * Send the Intent from an Activity
     *
     * @param activity Activity to start activity
     */
    fun start(activity: Activity) {
        if (destination == 0) {
            if (mAccessToken != null || (mApiKey != null && mUserId != null))
                activity.startActivity(getIntent(activity))
        } else if (destination == 1) {
            if (mDataAgreement != null)
                activity.startActivity(getIntent(activity))
        }
    }

    /**
     * Send the Intent from an Activity with a custom request code
     *
     * @param context Context to start activity
     */
    fun start(context: Context) {
        if (destination == 0) {
            if (mAccessToken != null || (mApiKey != null && mUserId != null))
                context.startActivity(getIntent(context))
        } else if (destination == 1) {
            if (mDataAgreement != null)
                context.startActivity(getIntent(context))
        }
    }

    /**
     * Get Intent to start [BBConsentDashboardActivity]
     *
     * @return Intent for [BBConsentDashboardActivity]
     */
    private fun getIntent(context: Context): Intent? {
        if (destination == 0) {
            mIntent?.setClass(context, BBConsentDashboardActivity::class.java)
            BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_BASE_URL, this.mBaseUrl)
            BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_USERID, this.mUserId)
            BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_TOKEN, this.mApiKey)
            BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_ACCESS_TOKEN, this.mAccessToken)
            BBConsentLocaleHelper.setLocale(context, mLocale ?: "en")
            BBConsentDataUtils.saveBooleanValues(
                context,
                EXTRA_TAG_ENABLE_USER_REQUEST,
                this.mEnableUserRequest
            )
            BBConsentDataUtils.saveBooleanValues(
                context,
                EXTRA_TAG_ENABLE_ASK_ME,
                this.mEnableAskMe
            )
            BBConsentDataUtils.saveBooleanValues(
                context,
                EXTRA_TAG_ENABLE_ATTRIBUTE_LEVEL_CONSENT,
                this.mEnableAttributeLevelConsent
            )
            return mIntent
        } else {
            if (mDataAgreement != null)
                mIntent?.setClass(context, BBConsentDataAgreementPolicyActivity::class.java)
            BBConsentLocaleHelper.setLocale(context, mLocale ?: "en")
            mIntent?.putExtra(
                BBConsentDataAgreementPolicyActivity.TAG_EXTRA_ATTRIBUTE_LIST,
                buildListForDataAgreementPolicy(
                    context,
                    Gson().fromJson(mDataAgreement, DataAgreementV2::class.java)
                )
            )
            return mIntent
        }
    }

    fun setLocale(context: Context, languageCode: String) {
        BBConsentLocaleHelper.setLocale(context, languageCode)
    }

    suspend fun optInToDataAgreement(
        dataAgreementId: String,
        baseUrl: String,
        accessToken: String? = null,
        apiKey: String? = null,
        userId: String? = null,
    ): String? {
        val body = ConsentStatusRequestV2()
        body.optIn = true

        val apiService: BBConsentAPIServices = BBConsentAPIManager.getApi(
            apiKey = apiKey,
            accessToken = accessToken,
            baseUrl = (if (baseUrl.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/")
        )?.service!!

        val updateDataAgreementStatusApiRepository =
            UpdateDataAgreementStatusApiRepository(apiService)

        val result = updateDataAgreementStatusApiRepository.updateDataAgreementStatus(
            userId = userId,
            dataAgreementId = dataAgreementId,
            body = body
        )

        return if (result.isSuccess) {
            Gson().toJson(result.getOrNull()?.dataAgreementRecord)
        } else {
            null
        }

    }

    suspend fun getDataAgreement(
        dataAgreementId: String,
        baseUrl: String,
        accessToken: String? = null,
        apiKey: String? = null,
        userId: String? = null,
    ): String? {

        val apiService: BBConsentAPIServices = BBConsentAPIManager.getApi(
            apiKey = apiKey,
            accessToken = accessToken,
            baseUrl = (if (baseUrl.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/")
        )?.service!!

        val dataAgreementApiRepository =
            GetDataAgreementApiRepository(apiService)

        val result = dataAgreementApiRepository.getDataAgreement(
            userId = userId,
            dataAgreementId = dataAgreementId
        )

        return if (result.isSuccess) {
            Gson().toJson(result.getOrNull()?.dataAgreement)
        } else {
            null
        }

    }

    suspend fun createAnIndividual(
        accessToken: String? = null,
        apiKey: String? = null,
        baseUrl: String? = null,
        name: String? = null,
        email: String? = null,
        phone: String? = null
    ): String? {
        val apiService: BBConsentAPIServices = BBConsentAPIManager.getApi(
            accessToken,
            apiKey,
            if (baseUrl?.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/"
        )?.service!!

        val individualApiRepository = IndividualApiRepository(apiService)

        val result =
            individualApiRepository.createAnIndividual(
                name, email, phone
            )
        return Gson().toJson(result?.getOrNull())
    }

    suspend fun fetchTheIndividual(
        accessToken: String? = null,
        apiKey: String? = null,
        baseUrl: String? = null,
        individualId: String
    ): String? {
        val apiService: BBConsentAPIServices = BBConsentAPIManager.getApi(
            accessToken,
            apiKey,
            if (baseUrl?.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/"
        )?.service!!

        val individualApiRepository = IndividualApiRepository(apiService)

        val result =
            individualApiRepository.readTheIndividual(
                individualId
            )
        return Gson().toJson(result?.getOrNull())
    }

    suspend fun updateTheIndividual(
        accessToken: String? = null,
        apiKey: String? = null,
        baseUrl: String? = null,
        name: String,
        email: String,
        phone: String,
        individualId: String
    ): String? {
        val apiService: BBConsentAPIServices = BBConsentAPIManager.getApi(
            accessToken,
            apiKey,
            if (baseUrl?.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/"
        )?.service!!

        val individualApiRepository = IndividualApiRepository(apiService)

        val result =
            individualApiRepository.updateIndividual(
                individualId, name, email, phone
            )
        return Gson().toJson(result?.getOrNull())
    }

    suspend fun getAllIndividuals(
        accessToken: String? = null,
        apiKey: String? = null,
        baseUrl: String? = null,
        offset: Int?,
        limit: Int?
    ): String? {
        val apiService: BBConsentAPIServices = BBConsentAPIManager.getApi(
            accessToken,
            apiKey,
            if (baseUrl?.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/"
        )?.service!!

        val individualApiRepository = IndividualApiRepository(apiService)

        val result =
            individualApiRepository.getAllIndividuals(
                offset, limit
            )
        return Gson().toJson(result?.getOrNull())
    }
}