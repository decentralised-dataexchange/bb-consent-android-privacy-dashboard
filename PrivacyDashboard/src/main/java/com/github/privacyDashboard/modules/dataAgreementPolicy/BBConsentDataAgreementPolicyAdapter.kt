package com.github.privacyDashboard.modules.dataAgreementPolicy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentItemDataAgreementPolicyBinding
import com.github.privacyDashboard.models.DataAgreementPolicyModel

class BBConsentDataAgreementPolicyAdapter(
    dataAttributes: ArrayList<ArrayList<DataAgreementPolicyModel>>,
    val width: Float,
) :
    RecyclerView.Adapter<BBConsentDataAgreementPolicyAdapter.ViewHolder?>() {
    private val mList: ArrayList<ArrayList<DataAgreementPolicyModel>>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: BbconsentItemDataAgreementPolicyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.bbconsent_item_data_agreement_policy, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val policyList: ArrayList<DataAgreementPolicyModel>? = mList[position]
        holder.bind(policyList, width)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemRowBinding: BbconsentItemDataAgreementPolicyBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: BbconsentItemDataAgreementPolicyBinding
        fun bind(
            attribute: ArrayList<DataAgreementPolicyModel>?,
            width: Float,
        ) {
            val adapter = BBConsentDataAgreementPolicyAttributeAdapter(
                attribute, width
            )
            itemRowBinding.rvAttributes.layoutManager =
                LinearLayoutManager(itemRowBinding.rvAttributes.context)
            itemRowBinding.rvAttributes.adapter = adapter
        }

        init {
            this.itemRowBinding = itemRowBinding
        }
    }


    init {
        mList = dataAttributes
    }
}
