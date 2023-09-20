package com.github.privacyDashboard.modules.logging

import android.graphics.Color
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentItemConsentHistoryBinding
import com.github.privacyDashboard.models.logging.ConsentHistory
import com.github.privacyDashboard.utils.BBConsentDateUtils
import com.github.privacyDashboard.utils.BBConsentDateUtils.getApiFormatDate

class BBConsentHistoryAdapter(
    consentHistories: ArrayList<ConsentHistory>
) :
    RecyclerView.Adapter<BBConsentHistoryAdapter.ViewHolder?>() {
    private val mList: ArrayList<ConsentHistory>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: BbconsentItemConsentHistoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.bbconsent_item_consent_history, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val consentHistory: ConsentHistory = mList[position]
        holder.bind(consentHistory, position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemRowBinding: BbconsentItemConsentHistoryBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: BbconsentItemConsentHistoryBinding
        fun bind(consentHistory: ConsentHistory, position: Int) {
            itemRowBinding.clItem.setBackgroundColor(
                if (position % 2 == 0) Color.parseColor("#ffffff") else Color.parseColor(
                    "#dddddd"
                )
            )

            itemRowBinding.tvLog.text = consentHistory.log
            itemRowBinding.tvTimeStamp.text =
                TimeAgo.using(getApiFormatDate(consentHistory.timeStamp).time)
        }

        init {
            this.itemRowBinding = itemRowBinding
        }
    }


    init {
        mList = consentHistories
    }
}
