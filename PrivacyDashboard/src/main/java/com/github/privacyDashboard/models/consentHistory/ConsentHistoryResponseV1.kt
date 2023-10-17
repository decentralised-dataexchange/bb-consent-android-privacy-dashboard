package com.github.privacyDashboard.models.consentHistory

import com.github.privacyDashboard.models.attributes.DataAttributeV1
import com.github.privacyDashboard.models.uiModels.consentHistory.ConsentHistory
import com.github.privacyDashboard.models.uiModels.consentHistory.ConsentHistoryResponse
import com.github.privacyDashboard.models.uiModels.dataAttributesList.DataAttribute
import com.google.gson.annotations.SerializedName

class ConsentHistoryResponseV1(
    @SerializedName("ConsentHistory")
    var consentHistory: ArrayList<ConsentHistoryV1?>? = null
) : ConsentHistoryResponse {
    override val mConsentHistory: ArrayList<ConsentHistory?>?
        get() = convertToList(consentHistory)

    private fun convertToList(consents: ArrayList<ConsentHistoryV1?>?): ArrayList<ConsentHistory?>? {
        return consents?.filterNotNull()?.toCollection(ArrayList())
    }

}
