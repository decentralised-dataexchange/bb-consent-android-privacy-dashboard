package com.github.privacyDashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import com.github.privacyDashboard.modules.home.BBConsentDashboardActivity
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_BASE_URL
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ENABLE_ASK_ME
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ENABLE_ATTRIBUTE_LEVEL_CONSENT
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ENABLE_USER_REQUEST
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_ORG_ID
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_TOKEN
import com.github.privacyDashboard.utils.BBConsentDataUtils.EXTRA_TAG_USERID
import com.github.privacyDashboard.utils.BBConsentLocaleHelper

object PrivacyDashboard {

    private var mUserId: String? = ""
    private var mApiKey: String? = ""
    private var mOrgId: String? = ""
    private var mBaseUrl: String? = ""
    private var mLocale: String? = ""
    private var mEnableUserRequest: Boolean? = false
    private var mEnableAskMe: Boolean? = false
    private var mEnableAttributeLevelConsent: Boolean? = false

    private var mPrivacyDashboardIntent: Intent? = null

    fun showPrivacyDashboard(): PrivacyDashboard {
        mPrivacyDashboardIntent = Intent()
        return this
    }

    /**
     * Set user id for igrant sdk.
     *
     * @param userId
     */
    fun withUserId(userId: String?): PrivacyDashboard {
        this.mUserId = userId
        return this
    }

    /**
     * Set Api key for the iGrant Sdk.
     *
     * @param apiKey
     */
    fun withApiKey(apiKey: String?): PrivacyDashboard {
        this.mApiKey = apiKey
        return this
    }

    /**
     * Set organization id for the iGrant Sdk.
     *
     * @param orgId
     */
    fun withOrgId(orgId: String?): PrivacyDashboard {
        this.mOrgId = orgId
        return this
    }

    /**
     * Set base url for the iGrant Sdk.
     *
     * @param baseUrl
     */
    fun withBaseUrl(baseUrl: String?): PrivacyDashboard {
        this.mBaseUrl = baseUrl
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
        activity.startActivity(getIntent(activity))
    }

    /**
     * Send the Intent from an Activity with a custom request code
     *
     * @param context Context to start activity
     */
    fun start(context: Context) {
        context.startActivity(getIntent(context))
    }

    /**
     * Get Intent to start [BBConsentDashboardActivity]
     *
     * @return Intent for [BBConsentDashboardActivity]
     */
    private fun getIntent(context: Context): Intent? {
        mPrivacyDashboardIntent?.setClass(context, BBConsentDashboardActivity::class.java)
        BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_BASE_URL, this.mBaseUrl)
        BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_ORG_ID, this.mOrgId)
        BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_USERID, this.mUserId)
        BBConsentDataUtils.saveStringValues(context, EXTRA_TAG_TOKEN, this.mApiKey)
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
        return mPrivacyDashboardIntent
    }

    fun setLocale(context: Context, languageCode: String) {
        BBConsentLocaleHelper.setLocale(context, languageCode)
    }
}