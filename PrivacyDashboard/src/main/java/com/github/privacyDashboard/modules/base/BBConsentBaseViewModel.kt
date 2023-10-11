package com.github.privacyDashboard.modules.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


abstract class BBConsentBaseViewModel : ViewModel() {
    var isLoading = MutableLiveData<Boolean>()

    override fun onCleared() {
        super.onCleared()
    }

    fun setLoading(mIsLoading: Boolean) {
        isLoading.value = mIsLoading
    }

    init {

    }
}