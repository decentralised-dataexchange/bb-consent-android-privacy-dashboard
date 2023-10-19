package com.github.privacyDashboard.modules.userRequestStatus

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentActivityUserRequestStatusBinding
import com.github.privacyDashboard.models.interfaces.userRequests.UserRequestStatus
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.utils.BBConsentDateUtils

class BBConsentUserRequestStatusActivity : BBConsentBaseActivity() {

    private lateinit var binding: BbconsentActivityUserRequestStatusBinding

    private var mIsDownloadData: Boolean? = false

    private var viewModel: BBConsentUserRequestStatusViewModel? = null

    companion object {
        const val EXTRA_DATA_REQUEST_TYPE =
            "com.github.privacyDashboard.modules.userRequestStatus.BBConsentUserRequestStatusActivity.type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.bbconsent_activity_user_request_status)
        viewModel = ViewModelProvider(this)[BBConsentUserRequestStatusViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        getIntentData()
        setUpToolBar()
        viewModel?.getDataRequestStatus(mIsDownloadData, this)
        initListener()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            mIsDownloadData =
                intent.getBooleanExtra(EXTRA_DATA_REQUEST_TYPE, false)
        }
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolBarCommon)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_back_black_pad
            )
        )
        supportActionBar?.title =
            if (mIsDownloadData == true) resources.getString(R.string.bb_consent_user_request_download_data_status) else resources.getString(
                R.string.bb_consent_user_request_delete_data_status
            )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setUpStepView(status: UserRequestStatus?) {
        if (status != null && (status.mState ?: 0) > 0) {
            binding.btnCancel.visibility = View.VISIBLE
            val list0: MutableList<String> = ArrayList()
            list0.add(resources.getString(R.string.bb_consent_user_request_request_initiated))
            list0.add(resources.getString(R.string.bb_consent_user_request_request_acknowledged))
            list0.add(resources.getString(R.string.bb_consent_user_request_request_processed))
            val dates: MutableList<String> = ArrayList()
            dates.add(
                if (status.mRequestedDate != null && !status.mRequestedDate.equals("")
                ) BBConsentDateUtils.getApiFormatTime(
                    BBConsentDateUtils.YYYYMMDDHHMMSS,
                    BBConsentDateUtils.DDMMYYYYHHMMA,
                    status.mRequestedDate?.replace(" +0000 UTC", "")
                ) else ""
            )
            dates.add("")
            dates.add("")
            val comments: List<String> = ArrayList()
            dates.add("")
            dates.add("")
            dates.add("")

            binding.stepView.setStepsViewIndicatorComplectingPosition(getSelectedStatusPosition())
                .reverseDraw(false)
                .setStepViewTexts(list0)
                .setStepViewDates(dates)
                .setStepViewComments(comments)
                .setLinePaddingProportion(1f)
                .setTextSize(15)
                .setDateTextSize(12)
                .setStepsViewIndicatorCompletedLineColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bb_consent_accent_blue
                    )
                )
                .setStepsViewIndicatorUnCompletedLineColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bb_consent_accent_blue
                    )
                )
                .setStepViewComplectedTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bb_consent_accent_blue
                    )
                )
                .setStepViewUnComplectedTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bbConsentTextColorLight
                    )
                )
                .setStepsViewIndicatorCompleteIcon(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_stepview_completed
                    )
                )
                .setStepsViewIndicatorDefaultIcon(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_stepview_uncompleted
                    )
                )
                .setStepsViewIndicatorAttentionIcon(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_stepview_current
                    )
                )
        }
    }

    private fun getSelectedStatusPosition(): Int {
        return when (viewModel?.status?.value?.mState) {
            1 -> 0
            2 -> 1
            6, 7 -> 0
            else -> 0
        }
    }

    private fun initListener() {
        binding.btnCancel.setOnClickListener {
            viewModel?.cancelDataRequest(
                mIsDownloadData,
                viewModel?.status?.value?.mId,
                this
            )
        }

        viewModel?.shouldFinish?.observe(this, Observer { newData ->
            if (newData)
                finish()
        })

        viewModel?.status?.observe(this, Observer { newData ->
            setUpStepView(newData)
        })
    }
}