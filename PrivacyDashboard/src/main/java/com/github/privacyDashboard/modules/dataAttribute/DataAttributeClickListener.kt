package com.github.privacyDashboard.modules.dataAttribute

import com.github.privacyDashboard.models.base.attribute.DataAttribute


interface DataAttributeClickListener {
    fun onAttributeClick(dataAttribute: DataAttribute?)
}