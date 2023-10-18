package com.github.privacyDashboard.modules.userRequestStatus

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.databinding.BbconsentActivityUserRequestStatusBinding
import com.github.privacyDashboard.models.userRequests.UserRequestGenResponseV1
import com.github.privacyDashboard.models.userRequests.UserRequestStatus
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.modules.userRequest.BBConsentUserRequestViewModel
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentDateUtils
import com.github.privacyDashboard.utils.BBConsentMessageUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BBConsentUserRequestStatusActivity : BBConsentBaseActivity() {

    private lateinit var binding: BbconsentActivityUserRequestStatusBinding

    private var mIsDownloadData: Boolean? = false

    private var status: UserRequestStatus? = null

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
        getDataRequestStatus()
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

    private fun getDataRequestStatus() {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this, true)) {
            viewModel?.isLoading?.value = true
            val callback: Callback<UserRequestStatus?> = object : Callback<UserRequestStatus?> {
                override fun onResponse(
                    call: Call<UserRequestStatus?>,
                    response: Response<UserRequestStatus?>
                ) {
                    viewModel?.isLoading?.value = false
                    if (response.code() == 200) {
                        status = response.body()
                        setUpStepView()
                    } else {
                        BBConsentMessageUtils.showSnackbar(
                            window.decorView.findViewById(android.R.id.content),
                            resources.getString(R.string.bb_consent_error_unexpected)
                        )
                    }
                }

                override fun onFailure(call: Call<UserRequestStatus?>, t: Throwable) {
                    viewModel?.isLoading?.value = false
                    BBConsentMessageUtils.showSnackbar(
                        window.decorView.findViewById(android.R.id.content),
                        resources.getString(R.string.bb_consent_error_unexpected)
                    )
                }
            }
            if (mIsDownloadData == true) BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(this, BBConsentDataUtils.EXTRA_TAG_TOKEN) ?: "",
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service?.getDataDownloadStatus(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_ORG_ID
                )
            )?.enqueue(callback) else BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(this, BBConsentDataUtils.EXTRA_TAG_TOKEN) ?: "",
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service?.getDataDeleteStatus(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_ORG_ID
                )
            )?.enqueue(callback)
        }
    }

    private fun setUpStepView() {
        if (status != null && (status?.state ?: 0) > 0) {
            binding.btnCancel.visibility = View.VISIBLE
            val list0: MutableList<String> = ArrayList()
            list0.add(resources.getString(R.string.bb_consent_user_request_request_initiated))
            list0.add(resources.getString(R.string.bb_consent_user_request_request_acknowledged))
            list0.add(resources.getString(R.string.bb_consent_user_request_request_processed))
            val dates: MutableList<String> = ArrayList()
            dates.add(
                if (status?.requestedDate != null && !status?.requestedDate.equals("")
                ) BBConsentDateUtils.getApiFormatTime(
                    BBConsentDateUtils.YYYYMMDDHHMMSS,
                    BBConsentDateUtils.DDMMYYYYHHMMA,
                    status?.requestedDate?.replace(" +0000 UTC", "")
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
        return when (status?.state) {
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
                status?.iD,
                this
            )
        }

        viewModel?.shouldFinish?.observe(this, Observer { newData ->
            if (newData)
                finish()
        })
    }
}