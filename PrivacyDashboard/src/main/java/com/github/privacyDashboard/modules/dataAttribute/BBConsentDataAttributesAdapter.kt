package com.github.privacyDashboard.modules.dataAttribute

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentItemDataAttributeBinding
import com.github.privacyDashboard.models.base.attribute.DataAttribute
import com.github.privacyDashboard.utils.BBConsentStringUtils

class BBConsentDataAttributesAdapter(
    dataAttributes: ArrayList<DataAttribute?>,
    mListener: DataAttributeClickListener
) :
    RecyclerView.Adapter<BBConsentDataAttributesAdapter.ViewHolder?>() {
    private val mList: ArrayList<DataAttribute?>
    private val mListener: DataAttributeClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: BbconsentItemDataAttributeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.bbconsent_item_data_attribute, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val purposeConsent: DataAttribute? = mList[position]
        holder.bind(purposeConsent, mListener, position == mList.size - 1)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemRowBinding: BbconsentItemDataAttributeBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: BbconsentItemDataAttributeBinding
        fun bind(
            attribute: DataAttribute?,
            mListener: DataAttributeClickListener,
            isLast: Boolean
        ) {
            itemRowBinding.ctvItemName.text = attribute?.description
            if (attribute?.status?.consented != null && !attribute.status?.consented.equals("")
            ) {
                itemRowBinding.ctvStatus.text = BBConsentStringUtils.toCamelCase(
                    if (attribute.status?.consented.equals("allow", true))
                        itemRowBinding.ctvStatus.context.resources.getString(R.string.bb_consent_data_attribute_allow)
                    else if (attribute.status?.consented.equals("disallow", true))
                        itemRowBinding.ctvStatus.context.resources.getString(R.string.bb_consent_dashboard_disallow)
                    else
                        attribute.status?.consented
                )
            } else {
                itemRowBinding.ctvStatus.text =
                    itemRowBinding.ctvStatus.context.resources.getString(R.string.bb_consent_data_attribute_allow)
            }
            itemRowBinding.llItem.setOnClickListener(View.OnClickListener {
                mListener.onAttributeClick(
                    attribute
                )
            })
            itemRowBinding.vDivider.visibility = if (isLast) View.GONE else View.VISIBLE
        }

        init {
            this.itemRowBinding = itemRowBinding
        }
    }


    init {
        mList = dataAttributes
        this.mListener = mListener
    }
}
