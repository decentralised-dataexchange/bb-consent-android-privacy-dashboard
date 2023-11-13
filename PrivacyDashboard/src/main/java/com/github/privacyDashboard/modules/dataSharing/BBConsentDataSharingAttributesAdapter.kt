package com.github.privacyDashboard.modules.dataSharing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentItemDataAttributeBinding
import com.github.privacyDashboard.databinding.BbconsentItemDataSharingAttributeBinding
import com.github.privacyDashboard.models.base.attribute.DataAttribute
import com.github.privacyDashboard.models.v2.dataAgreement.dataAttributes.DataAttributesV2
import com.github.privacyDashboard.utils.BBConsentStringUtils

class BBConsentDataSharingAttributesAdapter(
    dataAttributes: ArrayList<DataAttributesV2?>,
) :
    RecyclerView.Adapter<BBConsentDataSharingAttributesAdapter.ViewHolder?>() {
    private val mList: ArrayList<DataAttributesV2?>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: BbconsentItemDataSharingAttributeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.bbconsent_item_data_sharing_attribute, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val purposeConsent: DataAttributesV2? = mList[position]
        holder.bind(purposeConsent, position == mList.size - 1)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemRowBinding: BbconsentItemDataSharingAttributeBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: BbconsentItemDataSharingAttributeBinding
        fun bind(attribute: DataAttributesV2?, isLast: Boolean) {
            itemRowBinding.tvAttributeName.text = attribute?.name
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
