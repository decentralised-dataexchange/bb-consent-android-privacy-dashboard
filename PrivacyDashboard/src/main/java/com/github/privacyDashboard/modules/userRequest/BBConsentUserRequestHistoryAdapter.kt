package com.github.privacyDashboard.modules.userRequest

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentItemUserRequestBinding
import com.github.privacyDashboard.models.userRequests.UserRequest
import com.github.privacyDashboard.utils.BBConsentDateUtils
import com.github.privacyDashboard.utils.BBConsentDateUtils.YYYYMMDDHHMMSS
import com.github.privacyDashboard.utils.BBConsentDateUtils.getApiFormatDate

class BBConsentUserRequestHistoryAdapter(
    userRequests: ArrayList<UserRequest>,
    mClickListener: BBConsentUserRequestClickListener
) :
    RecyclerView.Adapter<BBConsentUserRequestHistoryAdapter.ViewHolder?>() {
    private var mList: ArrayList<UserRequest>
    private val mListener: BBConsentUserRequestClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: BbconsentItemUserRequestBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.bbconsent_item_user_request, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val purposeConsent: UserRequest = mList[position]
        holder.bind(purposeConsent, mListener)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemRowBinding: BbconsentItemUserRequestBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: BbconsentItemUserRequestBinding
        fun bind(
            userRequest: UserRequest,
            mListener: BBConsentUserRequestClickListener,
        ) {
            itemRowBinding.tvRequestType.text = userRequest.typeStr
            itemRowBinding.tvRequestDate.setText(
                TimeAgo.using(
                    getApiFormatDate(
                        userRequest.requestedDate?.replace(" +0000 UTC", ""),
                        YYYYMMDDHHMMSS
                    ).time
                )
            )
            itemRowBinding.tvRequestStatus.text = userRequest.stateStr

            itemRowBinding.tvCancel.visibility =
                if (userRequest.isOnGoing == true) View.VISIBLE else View.GONE

            itemRowBinding.tvCancel.setOnClickListener {
                mListener.onRequestCancel(
                    userRequest
                )
            }

            itemRowBinding.llItem.setOnClickListener {
                if (userRequest.isOnGoing == true) mListener.onRequestClick(
                    userRequest
                )
            }
        }

        init {
            this.itemRowBinding = itemRowBinding
        }
    }

    fun updateList(userRequests: ArrayList<UserRequest>){
        mList = userRequests
        notifyDataSetChanged()
    }

    init {
        mList = userRequests
        mListener = mClickListener
    }
}