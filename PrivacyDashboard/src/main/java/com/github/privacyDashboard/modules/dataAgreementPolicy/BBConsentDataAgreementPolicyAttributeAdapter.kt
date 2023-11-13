package com.github.privacyDashboard.modules.dataAgreementPolicy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentItemDataAgreementPolicyAttributeBinding
import com.github.privacyDashboard.models.DataAgreementPolicyModel
import com.github.privacyDashboard.utils.BBConsentStringUtils

class BBConsentDataAgreementPolicyAttributeAdapter(
    dataAttributes: ArrayList<DataAgreementPolicyModel>?,
    val width: Float
) :
    RecyclerView.Adapter<BBConsentDataAgreementPolicyAttributeAdapter.ViewHolder?>() {
    private val mList: ArrayList<DataAgreementPolicyModel>?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: BbconsentItemDataAgreementPolicyAttributeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.bbconsent_item_data_agreement_policy_attribute, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataPolicy: DataAgreementPolicyModel? = mList?.get(position)
        holder.bind(dataPolicy, position == (mList?.size ?: 1) - 1, width)
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    class ViewHolder(itemRowBinding: BbconsentItemDataAgreementPolicyAttributeBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: BbconsentItemDataAgreementPolicyAttributeBinding
        fun bind(
            attribute: DataAgreementPolicyModel?,
            isLast: Boolean,
            width: Float,
        ) {
            BBConsentStringUtils.findTextWidth(
                itemRowBinding.tvAttributeValue,
                itemRowBinding.tvAttributeValue1,
                itemRowBinding.tvAttributeName,
                attribute?.name ?: "",
                attribute?.value ?: "",
                width.toInt()
            )
            itemRowBinding.vDivider.visibility = if (isLast) View.GONE else View.VISIBLE
        }

        init {
            this.itemRowBinding = itemRowBinding
        }
    }


    init {
        mList = dataAttributes
    }
}
