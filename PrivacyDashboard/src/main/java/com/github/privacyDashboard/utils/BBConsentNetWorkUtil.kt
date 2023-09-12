package com.github.privacyDashboard.utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.github.privacyDashboard.R

object BBConsentNetWorkUtil {
    fun isConnectedToInternet(context: Context, showError: Boolean?): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        } else {
            Toast.makeText(
                context,
                context.resources.getString(R.string.bb_consent_error_internet),
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    fun isConnectedToInternet(context: Context): Boolean {
        return isConnectedToInternet(context, false)
    }
}