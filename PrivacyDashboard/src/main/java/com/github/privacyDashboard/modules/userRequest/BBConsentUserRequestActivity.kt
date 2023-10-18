package com.github.privacyDashboard.modules.userRequest

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.databinding.BbconsentActivityUserRequestBinding
import com.github.privacyDashboard.models.userRequests.UserRequest
import com.github.privacyDashboard.models.userRequests.UserRequestGenResponseV1
import com.github.privacyDashboard.models.userRequests.UserRequestHistoryResponse
import com.github.privacyDashboard.models.userRequests.UserRequestStatus
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailViewModel
import com.github.privacyDashboard.modules.userRequestStatus.BBConsentUserRequestStatusActivity
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentMessageUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import com.paginate.Paginate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BBConsentUserRequestActivity : BBConsentBaseActivity() {

    private var adapter: BBConsentUserRequestHistoryAdapter? = null

    private lateinit var binding: BbconsentActivityUserRequestBinding

    private var viewModel: BBConsentUserRequestViewModel? = null

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
        viewModel = ViewModelProvider(this)[BBConsentUserRequestViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        getIntentData()
        setUpToolBar()
        initListeners()
        setUpList()
        viewModel?.getRequestHistory(true, this)
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            viewModel?.mOrgId =
                intent.getStringExtra(EXTRA_TAG_USER_REQUEST_ORGID)
            viewModel?.mOrgName =
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

        viewModel?.requestHistories?.observe(this, Observer { newData ->
            adapter?.updateList(newData)
        })

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
            viewModel?.isLoading?.value = true
            val callback: Callback<UserRequestStatus?> = object : Callback<UserRequestStatus?> {
                override fun onResponse(
                    call: Call<UserRequestStatus?>,
                    response: Response<UserRequestStatus?>
                ) {
                    viewModel?.isLoading?.value = false
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
                    viewModel?.isLoading?.value = false
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
            viewModel?.isLoading?.value = true
            val callback: Callback<UserRequestStatus?> = object : Callback<UserRequestStatus?> {
                override fun onResponse(
                    call: Call<UserRequestStatus?>,
                    response: Response<UserRequestStatus?>
                ) {
                    viewModel?.isLoading?.value = false
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
                    viewModel?.isLoading?.value = false
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
                    viewModel?.mOrgName
                )
            ).setPositiveButton(R.string.bb_consent_user_request_confirm,
                DialogInterface.OnClickListener { dialog, which ->
                    if (isDelete) {
                        viewModel?.dataDeleteRequest(this)
                    } else {
                        viewModel?.dataDownloadRequest(this)
                    }
                }).setNegativeButton(R.string.bb_consent_general_cancel, null).show()
    }

    private fun setUpList() {
        adapter = BBConsentUserRequestHistoryAdapter(
            viewModel?.requestHistories?.value ?: ArrayList(),
            object : BBConsentUserRequestClickListener {

                override fun onRequestClick(request: UserRequest?) {
                    gotToStatusPage(request?.type != 1)
                }

                override fun onRequestCancel(request: UserRequest?) {
                    request?.let {
                        viewModel?.cancelDataRequest(
                            request.type == 2,
                            it,
                            this@BBConsentUserRequestActivity
                        )
                    }
                }

            })
        binding.rvRequestHistory.adapter = adapter
        Paginate.with(binding.rvRequestHistory, object : Paginate.Callbacks {
            override fun onLoadMore() {
                viewModel?.getRequestHistory(false, this@BBConsentUserRequestActivity)
            }

            override fun isLoading(): Boolean {
                Log.d("milan", "isLoading: ${viewModel?.isLoadingData}")
                return viewModel?.isLoadingData == true
            }

            override fun hasLoadedAllItems(): Boolean {
                Log.d("milan", "hasLoadedAllItems: ${viewModel?.hasLoadedAllItems}")

                return viewModel?.hasLoadedAllItems == true
            }
        }).setLoadingTriggerThreshold(1)
            .addLoadingListItem(true)
            .build()
    }

    private fun gotToStatusPage(isDownloadRequest: Boolean) {
        val intent = Intent(this, BBConsentUserRequestStatusActivity::class.java)
        intent.putExtra(
            BBConsentUserRequestStatusActivity.EXTRA_DATA_REQUEST_TYPE,
            isDownloadRequest
        )
        startActivity(intent)
    }
}