package com.github.privacyDashboard.modules.dataAttribute

import com.github.privacyDashboard.models.attributes.DataAttributeV1
import com.github.privacyDashboard.models.uiModels.dataAttributesList.DataAttribute

interface DataAttributeClickListener {
    fun onAttributeClick(dataAttribute: DataAttribute?)
}