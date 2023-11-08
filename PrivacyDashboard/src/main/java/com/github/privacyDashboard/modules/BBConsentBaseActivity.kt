package com.github.privacyDashboard.modules

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.github.privacyDashboard.PrivacyDashboard
import com.github.privacyDashboard.utils.BBConsentLocaleHelper

open class BBConsentBaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        var newBase = newBase
        newBase = BBConsentLocaleHelper.onAttach(newBase)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkIfAnimationsSetOrNot(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onResume() {
        super.onResume()
        val lang: String = BBConsentLocaleHelper.getLanguage(this) ?: "en"
        BBConsentLocaleHelper.setLocale(this, lang)
    }

    private fun checkIfAnimationsSetOrNot(activity: Activity) {
        val entryAnimation = "bb_consent_entry_anim"
        val exitAnimation = "bb_consent_exit_anim"
        try {
            // Try to obtain the style resource ID
            val entryId =
                activity.resources.getIdentifier(entryAnimation, "anim", activity.packageName)
            val exitId =
                activity.resources.getIdentifier(exitAnimation, "anim", activity.packageName)

            if (entryId != 0 && exitId != 0)
                activity.overridePendingTransition(entryId, exitId)
        } catch (e: Resources.NotFoundException) {
            Log.d("milna", "checkIfAnimationsSetOrNot: ")
        }
    }

}