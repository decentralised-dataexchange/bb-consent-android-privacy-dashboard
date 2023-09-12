package com.github.privacyDashboard.utils

import java.util.*

object BBConsentStringUtils {
    fun toCamelCase(init: String?): String? {
        if (init == null) return null
        val ret = StringBuilder(init.length)
        for (word in init.split(" ").toTypedArray()) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).uppercase(Locale.getDefault()))
                ret.append(word.substring(1).lowercase(Locale.getDefault()))
            }
            if (ret.length != init.length) ret.append(" ")
        }
        return ret.toString()
    }
}