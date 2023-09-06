package com.github.privacyDashboard.modules.dataAttribute

import com.github.privacyDashboard.models.attributes.DataAttribute

interface DataAttributeClickListener {
    fun onAttributeClick(dataAttribute: DataAttribute)
}