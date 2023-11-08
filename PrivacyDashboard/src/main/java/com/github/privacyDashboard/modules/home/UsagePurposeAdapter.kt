package com.github.privacyDashboard.modules.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.privacyDashboard.R
import com.github.privacyDashboard.customView.SwitchButton
import com.github.privacyDashboard.databinding.BbconsentItemUsagePurposeBinding
import com.github.privacyDashboard.models.PurposeConsent


class UsagePurposeAdapter(
    purposeConsents: ArrayList<PurposeConsent>,
    usagePurposeClickListener: UsagePurposeClickListener
) :
    RecyclerView.Adapter<UsagePurposeAdapter.ViewHolder?>() {
    private val mList: ArrayList<PurposeConsent>
    private val mListener: UsagePurposeClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: BbconsentItemUsagePurposeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.bbconsent_item_usage_purpose, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val purposeConsent: PurposeConsent = mList[position]
        holder.bind(purposeConsent, mListener)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemRowBinding: BbconsentItemUsagePurposeBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: BbconsentItemUsagePurposeBinding
        fun bind(purposeConsent: PurposeConsent?, mListener: UsagePurposeClickListener) {
            itemRowBinding.ctvItemName.text = purposeConsent?.purpose?.name ?: ""
            itemRowBinding.ctvStatus.text =
                if (purposeConsent?.count?.consented == 0) itemRowBinding.ctvStatus.context
                    .resources
                    .getString(R.string.bb_consent_dashboard_disallow) else itemRowBinding.ctvStatus.context
                    .resources.getString(
                        R.string.bb_consent_dashboard_allow,
                        purposeConsent?.count?.consented,
                        purposeConsent?.count?.total
                    )
            itemRowBinding.llDisable.visibility =
                if (purposeConsent?.purpose?.lawfulUsage == true) View.VISIBLE else View.GONE

            itemRowBinding.switchButtonDisabled.visibility =
                if (purposeConsent?.purpose?.lawfulUsage == true) View.VISIBLE else View.GONE
            itemRowBinding.switchButton.setOnCheckedChangeListener(null)
            itemRowBinding.switchButton.isChecked = purposeConsent?.count?.consented != 0
            itemRowBinding.switchButtonDisabled.isChecked = purposeConsent?.count?.consented != 0
            itemRowBinding.switchButton.setOnCheckedChangeListener(object :
                SwitchButton.OnCheckedChangeListener {
                override fun onCheckedChanged(view: SwitchButton?, isChecked: Boolean) {
                    mListener.onSetStatus(
                        purposeConsent,
                        isChecked
                    )
                }
            })
            itemRowBinding.llItem.setOnClickListener { mListener.onItemClick(purposeConsent) }
        }

        init {
            this.itemRowBinding = itemRowBinding
        }
    }


    init {
        mList = purposeConsents
        mListener = usagePurposeClickListener
    }
}
