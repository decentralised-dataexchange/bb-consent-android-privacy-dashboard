package com.github.privacyDashboard.modules

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import com.github.privacyDashboard.utils.BBConsentLocaleHelper

open class BBConsentBaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        var newBase = newBase
        newBase = BBConsentLocaleHelper.onAttach(newBase)
        super.attachBaseContext(newBase)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onResume() {
        super.onResume()
        val lang: String = BBConsentLocaleHelper.getLanguage(this) ?: "en"
        BBConsentLocaleHelper.setLocale(this, lang)
    }

}