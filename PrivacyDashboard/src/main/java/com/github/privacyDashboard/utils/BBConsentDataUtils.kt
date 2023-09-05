package com.github.privacyDashboard.utils

import android.content.Context
import android.preference.PreferenceManager

object BBConsentDataUtils {

    const val EXTRA_TAG_USERID = "com.github.privacyDashboard.utils.BBConsentDataUtils.userid"
    const val EXTRA_TAG_TOKEN = "com.github.privacyDashboard.utils.BBConsentDataUtils.token"
    const val EXTRA_TAG_ORG_ID = "com.github.privacyDashboard.utils.BBConsentDataUtils.orgId"
    const val EXTRA_TAG_BASE_URL = "com.github.privacyDashboard.utils.BBConsentDataUtils.baseUrl"

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
}
