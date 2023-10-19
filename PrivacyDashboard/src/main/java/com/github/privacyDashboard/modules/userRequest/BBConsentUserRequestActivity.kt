package com.github.privacyDashboard.modules.userRequest

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentActivityUserRequestBinding
import com.github.privacyDashboard.models.interfaces.userRequests.UserRequest
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.paginate.Paginate

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
                        viewModel?.downloadDataRequestStatus(this)
                        true
                    }
                    R.id.action_forgot_me -> {
                        viewModel?.deleteDataRequestStatus(this)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun setUpList() {
        adapter = BBConsentUserRequestHistoryAdapter(
            viewModel?.requestHistories?.value ?: ArrayList(),
            object : BBConsentUserRequestClickListener {

                override fun onRequestClick(request: UserRequest?) {
                    viewModel?.gotToStatusPage(
                        request?.mType != 1,
                        this@BBConsentUserRequestActivity
                    )
                }

                override fun onRequestCancel(request: UserRequest?) {
                    request?.let {
                        viewModel?.cancelDataRequest(
                            request.mType == 2,
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
}