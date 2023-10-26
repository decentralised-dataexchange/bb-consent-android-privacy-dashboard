package com.github.privacyDashboard.utils

import android.content.Context
import android.preference.PreferenceManager

object BBConsentDataUtils {

    const val EXTRA_TAG_USERID = "com.github.privacyDashboard.utils.BBConsentDataUtils.userid"
    const val EXTRA_TAG_TOKEN = "com.github.privacyDashboard.utils.BBConsentDataUtils.token"
    const val EXTRA_TAG_ORG_ID = "com.github.privacyDashboard.utils.BBConsentDataUtils.orgId"
    const val EXTRA_TAG_BASE_URL = "com.github.privacyDashboard.utils.BBConsentDataUtils.baseUrl"
    const val EXTRA_TAG_ENABLE_USER_REQUEST = "com.github.privacyDashboard.utils.BBConsentDataUtils.enableUserRequest"
    const val EXTRA_TAG_ENABLE_ASK_ME = "com.github.privacyDashboard.utils.BBConsentDataUtils.enableAskMe"
    const val EXTRA_TAG_ENABLE_ATTRIBUTE_LEVEL_CONSENT = "com.github.privacyDashboard.utils.BBConsentDataUtils.enableAttributeLevelConsent"

    //todo update the deprecated preference manager
    fun saveStringValues(context: Context?, tag: String?, value: String?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tag, value)
        editor.commit()
    }

    fun getStringValue(context: Context?, tag: String?): String? {
        return try {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.getString(tag, "")
        } catch (e: Exception) {
            ""
        }
    }

    fun saveBooleanValues(context: Context?, tag: String?, value: Boolean?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(tag, value?:false)
        editor.commit()
    }

    fun getBooleanValue(context: Context?, tag: String?): Boolean? {
        return try {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.getBoolean(tag, false)
        } catch (e: Exception) {
            false
        }
    }
}
