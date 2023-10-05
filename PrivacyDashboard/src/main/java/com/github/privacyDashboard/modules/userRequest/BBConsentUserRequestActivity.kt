package com.github.privacyDashboard.modules.userRequest

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.cocosw.bottomsheet.BottomSheet
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.databinding.BbconsentActivityUserRequestBinding
import com.github.privacyDashboard.models.userRequests.UserRequest
import com.github.privacyDashboard.models.userRequests.UserRequestGenResponse
import com.github.privacyDashboard.models.userRequests.UserRequestHistoryResponse
import com.github.privacyDashboard.models.userRequests.UserRequestStatus
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.modules.logging.BBConsentLoggingActivity
import com.github.privacyDashboard.modules.userRequestStatus.BBConsentUserRequestStatusActivity
import com.github.privacyDashboard.modules.webView.BBConsentWebViewActivity
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentMessageUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import com.paginate.Paginate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BBConsentUserRequestActivity : BBConsentBaseActivity() {

    private var adapter: BBConsentUserRequestHistoryAdapter? = null
    private var mOrgName: String? = ""
    private var mOrgId: String? = ""
    private lateinit var binding: BbconsentActivityUserRequestBinding

    private var isLoadingData = false
    private var hasLoadedAllItems = false

    private var startId = ""

    private val requestHistories: ArrayList<UserRequest> = ArrayList<UserRequest>()

    private var isDownloadRequestOngoing: Boolean? = false
    private var isDeleteRequestOngoing: Boolean? = false

    companion object {
        const val EXTRA_TAG_USER_REQUEST_ORGID =
            "com.github.privacyDashboard.modules.userRequest.BBConsentUserRequestActivity.orgId"
        const val EXTRA_TAG_USER_REQUEST_ORG_NAME =
            "com.github.privacyDashboard.modules.userRequest.BBConsentUserRequestActivity.orgName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.bbconsent_activity_user_request)
        getIntentData()
        setUpToolBar()
        initListeners()
        setUpList()
        getRequestHistory(true)
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            mOrgId =
                intent.getStringExtra(EXTRA_TAG_USER_REQUEST_ORGID)
            mOrgName =
                intent.getStringExtra(EXTRA_TAG_USER_REQUEST_ORG_NAME)
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
        supportActionBar?.title = resources.getString(R.string.bb_consent_user_request_user_request)
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

    private fun initListeners() {
        binding.tvNewRequest.setOnClickListener {

            val popupMenu = PopupMenu(this, binding.tvNewRequest)
            popupMenu.menuInflater.inflate(
                R.menu.menu_new_requests,
                popupMenu.getMenu()
            )

            // Handle item clicks in the popup menu
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.action_download_data -> {
                        downloadDataRequestStatus()
                        true
                    }
                    R.id.action_forgot_me -> {
                        deleteDataRequestStatus()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    /**
     * To delete the user data and forget me
     */
    private fun deleteDataRequestStatus() {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this, true)) {
            binding.llProgressBar.visibility = View.VISIBLE
            val callback: Callback<UserRequestStatus?> = object : Callback<UserRequestStatus?> {
                override fun onResponse(
                    call: Call<UserRequestStatus?>,
                    response: Response<UserRequestStatus?>
                ) {
                    binding.llProgressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        if (response.body()?.requestOngoing == true) {
                            gotToStatusPage(false)
                        } else {
                            confirmationAlert(true)
                        }
                    } else {
                        BBConsentMessageUtils.showSnackbar(
                            binding.root,
                            resources.getString(R.string.bb_consent_error_unexpected)
                        )
                    }
                }

                override fun onFailure(call: Call<UserRequestStatus?>, t: Throwable) {
                    binding.llProgressBar.visibility = View.GONE
                    BBConsentMessageUtils.showSnackbar(
                        binding.root,
                        resources.getString(R.string.bb_consent_error_unexpected)
                    )
                }
            }
            BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(this, BBConsentDataUtils.EXTRA_TAG_BASE_URL)
            )?.service?.getDataDeleteStatus(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_ORG_ID
                )
            )?.enqueue(callback)
        }
    }

    /**
     * Request to download the data
     */
    private fun downloadDataRequestStatus() {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this, true)) {
            binding.llProgressBar.visibility = View.VISIBLE
            val callback: Callback<UserRequestStatus?> = object : Callback<UserRequestStatus?> {
                override fun onResponse(
                    call: Call<UserRequestStatus?>,
                    response: Response<UserRequestStatus?>
                ) {
                    binding.llProgressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        if (response.body()?.requestOngoing == true) {
                            gotToStatusPage(true)
                        } else {
                            confirmationAlert(false)
                        }
                    } else {
                        BBConsentMessageUtils.showSnackbar(
                            binding.root,
                            resources.getString(R.string.bb_consent_error_unexpected)
                        )
                    }
                }

                override fun onFailure(call: Call<UserRequestStatus?>, t: Throwable) {
                    binding.llProgressBar.visibility = View.GONE
                    BBConsentMessageUtils.showSnackbar(
                        binding.root,
                        resources.getString(R.string.bb_consent_error_unexpected)
                    )
                }
            }
            BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(this, BBConsentDataUtils.EXTRA_TAG_BASE_URL)
            )?.service?.getDataDownloadStatus(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_ORG_ID
                )
            )?.enqueue(callback)
        }
    }

    private fun confirmationAlert(isDelete: Boolean) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogCustom))
            .setTitle(resources.getString(R.string.bb_consent_user_request_confirmation))
            .setMessage(
                resources.getString(
                    R.string.bb_consent_user_request_confirmation_message,
                    if (isDelete) resources.getString(R.string.bb_consent_user_request_data_delete) else resources.getString(
                        R.string.bb_consent_user_request_download_data
                    ),
                    mOrgName
                )
            ).setPositiveButton(R.string.bb_consent_user_request_confirm,
                DialogInterface.OnClickListener { dialog, which ->
                    if (isDelete) {
                        dataDeleteRequest()
                    } else {
                        dataDownloadRequest()
                    }
                }).setNegativeButton(R.string.bb_consent_general_cancel, null).show()
    }

    private fun dataDeleteRequest() {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this, true)) {
            binding.llProgressBar.visibility = View.VISIBLE
            val callback: Callback<Void> = object : Callback<Void> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    binding.llProgressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        refreshData()
                    } else {
                        BBConsentMessageUtils.showSnackbar(
                            binding.root,
                            resources.getString(R.string.bb_consent_error_unexpected)
                        )
                    }
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    binding.llProgressBar.visibility = View.GONE
                    BBConsentMessageUtils.showSnackbar(
                        binding.root,
                        resources.getString(R.string.bb_consent_error_unexpected)
                    )
                }
            }
            BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(this, BBConsentDataUtils.EXTRA_TAG_BASE_URL)
            )?.service?.dataDeleteRequest(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_ORG_ID
                )
            )?.enqueue(callback)
        }
    }

    private fun dataDownloadRequest() {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this, true)) {
            binding.llProgressBar.visibility = View.VISIBLE
            val callback: Callback<Void> = object : Callback<Void> {
                override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                    binding.llProgressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        refreshData()
                    } else {
                        BBConsentMessageUtils.showSnackbar(
                            binding.root,
                            resources.getString(R.string.bb_consent_error_unexpected)
                        )
                    }
                }

                override fun onFailure(call: Call<Void?>, t: Throwable) {
                    binding.llProgressBar.visibility = View.GONE
                    BBConsentMessageUtils.showSnackbar(
                        binding.root,
                        resources.getString(R.string.bb_consent_error_unexpected)
                    )
                }
            }
            BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(this, BBConsentDataUtils.EXTRA_TAG_BASE_URL)
            )?.service?.dataDownloadRequest(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_ORG_ID
                )
            )?.enqueue(callback)
        }
    }

    private fun setUpList() {
        adapter = BBConsentUserRequestHistoryAdapter(
            requestHistories,
            object : BBConsentUserRequestClickListener {

                override fun onRequestClick(request: UserRequest?) {
                    gotToStatusPage(request?.type != 1)
                }

                override fun onRequestCancel(request: UserRequest?) {
                    request?.let { cancelDataRequest(request.type == 2, it) }
                }

            })
        binding.rvRequestHistory.adapter = adapter
        Paginate.with(binding.rvRequestHistory, object : Paginate.Callbacks {
            override fun onLoadMore() {
                getRequestHistory(false)
            }

            override fun isLoading(): Boolean {
                return isLoadingData
            }

            override fun hasLoadedAllItems(): Boolean {
                return hasLoadedAllItems
            }
        }).setLoadingTriggerThreshold(1)
            .addLoadingListItem(true)
            .build()
    }

    private fun getRequestHistory(showProgress: Boolean) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this)) {
            isLoadingData = true
            if (showProgress) binding.llProgressBar.visibility = View.VISIBLE
            val callback: Callback<UserRequestHistoryResponse> =
                object : Callback<UserRequestHistoryResponse> {
                    override fun onResponse(
                        call: Call<UserRequestHistoryResponse>,
                        response: Response<UserRequestHistoryResponse>
                    ) {
                        isLoadingData = false
                        binding.llProgressBar.visibility = View.GONE
                        if (response.code() == 200) {
                            if (response.body()?.dataRequests != null) {
                                if (startId.equals("", ignoreCase = true)) {
                                    requestHistories.clear()
                                    isDownloadRequestOngoing =
                                        response.body()?.dataDownloadRequestOngoing
                                    isDeleteRequestOngoing =
                                        response.body()?.dataDeleteRequestOngoing
                                    requestHistories.addAll(
                                        setOngoingRequests(
                                            response.body()?.dataRequests ?: ArrayList()
                                        )
                                    )
                                } else {
                                    requestHistories.addAll(
                                        response.body()?.dataRequests ?: ArrayList()
                                    )
                                }
                                if (requestHistories.size > 0) {
                                    startId =
                                        requestHistories[requestHistories.size - 1].iD ?: ""
                                }
                                binding.rvRequestHistory.visibility =
                                    if (requestHistories.size > 0) View.VISIBLE else View.GONE
                            } else {
                                if (startId.equals("", ignoreCase = true)) {
                                    requestHistories.clear()
                                    binding.rvRequestHistory.visibility =
                                        if (requestHistories.size > 0) View.VISIBLE else View.GONE
                                }
                            }
                            if (response.body()?.dataRequests == null) {
                                hasLoadedAllItems = true
                            }
                            adapter?.notifyDataSetChanged()
                        }
                    }

                    override fun onFailure(call: Call<UserRequestHistoryResponse>, t: Throwable) {
                        isLoadingData = false
                        binding.llProgressBar.visibility = View.GONE
                    }
                }
            BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(this, BBConsentDataUtils.EXTRA_TAG_BASE_URL)
            )?.service?.getOrgRequestStatus(
                mOrgId, startId
            )?.enqueue(callback)
        }
    }

    private fun gotToStatusPage(isDownloadRequest: Boolean) {
        val intent = Intent(this, BBConsentUserRequestStatusActivity::class.java)
        intent.putExtra(
            BBConsentUserRequestStatusActivity.EXTRA_DATA_REQUEST_TYPE,
            isDownloadRequest
        )
        startActivity(intent)
    }

    private fun cancelDataRequest(isDownloadData: Boolean, request: UserRequest) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this)) {
            binding.llProgressBar.visibility = View.VISIBLE
            val callback: Callback<UserRequestGenResponse?> =
                object : Callback<UserRequestGenResponse?> {
                    override fun onResponse(
                        call: Call<UserRequestGenResponse?>,
                        response: Response<UserRequestGenResponse?>
                    ) {
                        binding.llProgressBar.visibility = View.GONE
                        if (response.code() == 200) {
                            Toast.makeText(
                                this@BBConsentUserRequestActivity,
                                resources.getString(R.string.bb_consent_user_request_request_cancelled),
                                Toast.LENGTH_SHORT
                            ).show()
                            refreshData()
                        } else {
                            Toast.makeText(
                                this@BBConsentUserRequestActivity,
                                resources.getString(R.string.bb_consent_error_unexpected),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<UserRequestGenResponse?>, t: Throwable) {
                        binding.llProgressBar.visibility = View.GONE
                        Toast.makeText(
                            this@BBConsentUserRequestActivity,
                            resources.getString(R.string.bb_consent_error_unexpected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            if (isDownloadData) BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(this, BBConsentDataUtils.EXTRA_TAG_BASE_URL)
            )?.service?.dataDownloadCancelRequest(
                mOrgId, request.iD
            )?.enqueue(callback) else BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(this, BBConsentDataUtils.EXTRA_TAG_BASE_URL)
            )?.service?.dataDeleteCancelRequest(
                mOrgId, request.iD
            )?.enqueue(callback)
        }
    }

    private fun setOngoingRequests(dataRequests: ArrayList<UserRequest>?): Collection<UserRequest> {
        if (dataRequests != null) {
            for (data in dataRequests) {
                if (data.type == 1 && isDeleteRequestOngoing!!) {
                    data.isOnGoing = (true)
                    isDeleteRequestOngoing = false
                }
                if (data.type == 2 && isDownloadRequestOngoing!!) {
                    data.isOnGoing = (true)
                    isDownloadRequestOngoing = false
                }
                if (!isDownloadRequestOngoing!! && !isDeleteRequestOngoing!!) {
                    return dataRequests
                }
            }
        }
        return dataRequests ?: ArrayList()
    }

    private fun refreshData() {
        requestHistories.clear()
        startId = ""
        getRequestHistory(true)
    }
}