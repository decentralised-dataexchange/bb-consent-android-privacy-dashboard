package com.github.privacyDashboard.modules.home

import com.github.privacyDashboard.models.PurposeConsent

interface UsagePurposeClickListener {
    fun onItemClick(consent: PurposeConsent?)
    fun onSetStatus(consent: PurposeConsent?, isChecked: Boolean?)
}