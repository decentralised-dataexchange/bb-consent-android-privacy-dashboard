package com.github.privacyDashboard.modules.dataAttribute

import com.github.privacyDashboard.models.interfaces.dataAttributesList.DataAttribute

interface DataAttributeClickListener {
    fun onAttributeClick(dataAttribute: DataAttribute?)
}