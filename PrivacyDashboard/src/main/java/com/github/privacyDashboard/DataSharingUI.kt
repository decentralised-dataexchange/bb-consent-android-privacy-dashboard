package com.github.privacyDashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.github.privacyDashboard.modules.dataSharing.BBConsentDataSharingActivity
import com.github.privacyDashboard.modules.home.BBConsentDashboardActivity
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ACCESS_TOKEN
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_BASE_URL
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_DATA_AGREEMENT_ID
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ENABLE_ASK_ME
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ENABLE_ATTRIBUTE_LEVEL_CONSENT
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ENABLE_USER_REQUEST
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_TOKEN
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_USERID
import com.github.privacyDashboard.utils.BBConsentLocaleHelper

object DataSharingUI {

    private var mUserId: String? = null
    private var mApiKey: String? = null
    private var mAccessToken: String? = null
    private var mDataAgreementId: String? = null
    private var mBaseUrl: String? = ""
    private var mLocale: String? = ""

    private var mDataSharingUIIntent: Intent? = null

    fun showDataSharingUI(): DataSharingUI {
        mDataSharingUIIntent = Intent()
        return this
    }

    /**
     * Set user id for igrant sdk.
     *
     * @param userId
     */
    fun withUserId(userId: String?): DataSharingUI {
        this.mUserId = if (userId == "") null else userId
        return this
    }

    /**
     * Set Api key for the iGrant Sdk.
     *
     * @param apiKey
     */
    fun withApiKey(apiKey: String?): DataSharingUI {
        this.mApiKey = if (apiKey == "") null else apiKey
        return this
    }

    /**
     * Set Access token for the iGrant Sdk.
     *
     * @param accessToken
     */
    fun withAccessToken(accessToken: String?): DataSharingUI {
        this.mAccessToken = if (accessToken == "") null else accessToken
        return this
    }

    /**
     * Set data agreement id id for the iGrant Sdk.
     *
     * @param dataAgreementId
     */
    fun withDataAgreementId(dataAgreementId: String?): DataSharingUI {
        this.mDataAgreementId = dataAgreementId
        return this
    }

    /**
     * Set base url for the iGrant Sdk.
     *
     * @param baseUrl
     */
    fun withBaseUrl(baseUrl: String?): DataSharingUI {
        if (baseUrl?.last().toString() == "/")
            this.mBaseUrl = baseUrl
        else
            this.mBaseUrl = "$baseUrl/"
        return this
    }

    /**
     * Set other application details for the iGrant Sdk.
     *
     * @param applicationName
     * @param logoUrl
     */
    fun withThirdPartyApplication(applicationName: String?, logoUrl: String?): DataSharingUI {
        mDataSharingUIIntent?.putExtra(
            BBConsentDataSharingActivity.TAG_EXTRA_OTHER_APPLICATION_NAME,
            applicationName
        )
        mDataSharingUIIntent?.putExtra(
            BBConsentDataSharingActivity.TAG_EXTRA_OTHER_APPLICATION_LOGO,
            logoUrl
        )
        return this
    }

    /**
     * Set secondaryButtonText for the iGrant Sdk.
     *
     * @param text
     */
    fun secondaryButtonText(text: String?): DataSharingUI {
        mDataSharingUIIntent?.putExtra(
            BBConsentDataSharingActivity.TAG_EXTRA_SECONDARY_BUTTON_TEXT,
            text
        )
        return this
    }

    /**
     * Set Language code for the iGrant Sdk.
     *
     * @param languageCode
     */
    fun withLocale(languageCode: String): DataSharingUI {
        this.mLocale = languageCode
        return this
    }

    /**
     * Send the Intent from an Activity
     *
     * @param activity Activity to start activity
     */
    fun get(activity: Activity): Intent? {
        if (mAccessToken != null || (mApiKey != null && mUserId != null))
            if (mDataAgreementId != null) {
                return getIntent(activity)
            }

        return null
    }

    /**
     * Send the Intent from an Activity with a custom request code
     *
     * @param context Context to start activity
     */
    fun get(context: Context): Intent? {
        if (mAccessToken != null || (mApiKey != null && mUserId != null))
            if (mDataAgreementId != null) {
                return getIntent(context)
            }

        return null
    }

    /**
     * Get Intent to start [BBConsentDashboardActivity]
     *
     * @return Intent for [BBConsentDashboardActivity]
     */
    private fun getIntent(context: Context): Intent? {
        mDataSharingUIIntent?.setClass(context, BBConsentDataSharingActivity::class.java)
        BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_BASE_URL, this.mBaseUrl)
        BBConsentDataUtils.saveStringValues(
            context,
            EXTRA_TAG_DATA_AGREEMENT_ID,
            this.mDataAgreementId
        )
        BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_USERID, this.mUserId)
        BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_TOKEN, this.mApiKey)
        BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_ACCESS_TOKEN, this.mAccessToken)
        BBConsentLocaleHelper.setLocale(context, mLocale ?: "en")
        return mDataSharingUIIntent
    }

    fun setLocale(context: Context, languageCode: String) {
        BBConsentLocaleHelper.setLocale(context, languageCode)
    }
}