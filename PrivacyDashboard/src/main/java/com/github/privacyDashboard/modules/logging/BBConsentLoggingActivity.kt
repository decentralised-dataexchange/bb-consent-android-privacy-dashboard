package com.github.privacyDashboard.modules.logging

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.repositories.GetConsentHistoryApiRepository
import com.github.privacyDashboard.communication.repositories.GetOrganizationDetailApiRepository
import com.github.privacyDashboard.databinding.BbconsentActivityLoggingBinding
import com.github.privacyDashboard.models.logging.ConsentHistory
import com.github.privacyDashboard.models.logging.ConsentHistoryResponse
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import com.paginate.Paginate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BBConsentLoggingActivity : BBConsentBaseActivity() {

    private var adapter: BBConsentHistoryAdapter? = null
    private lateinit var binding: BbconsentActivityLoggingBinding

    companion object {
        const val TAG_EXTRA_ORG_ID =
            "com.github.privacyDashboard.modules.logging.BBConsentLoggingActivity.orgId"
    }

    private var mOrgId = ""
    private var startId = ""

    private var isCallLoading = false
    private var hasLoadedAllItems = false

    private var consentHistories: ArrayList<ConsentHistory>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.bbconsent_activity_logging)
        getIntentData()
        setUpToolBar()
        setupRecyclerView()
        fetchConsentHistory(true)
    }

    private fun fetchConsentHistory(showProgress: Boolean) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this, true)) {
            isCallLoading = true
            if (showProgress) binding.llProgressBar.visibility = View.VISIBLE
            val consentHistoryRepository = GetConsentHistoryApiRepository(this)

            val callback: Callback<ConsentHistoryResponse?> =
                object : Callback<ConsentHistoryResponse?> {
                    override fun onResponse(
                        call: Call<ConsentHistoryResponse?>,
                        response: Response<ConsentHistoryResponse?>
                    ) {
                        isCallLoading = false
                        binding.llProgressBar.visibility = View.GONE
                        if (response.code() == 200) {
                            if (response.body()?.consentHistory != null) {
                                if (startId == "") consentHistories!!.clear()

                                consentHistories!!.addAll(
                                    response.body()?.consentHistory ?: ArrayList()
                                )
                                binding.rvConsentHistory.visibility =
                                    if (consentHistories!!.size > 0) View.VISIBLE else View.GONE
                                if (consentHistories!!.size > 0) {
                                    startId =
                                        consentHistories!![consentHistories!!.size - 1].iD ?: ""
                                }

                            } else {
                                if (startId == "") {
                                    consentHistories!!.clear()
                                    binding.rvConsentHistory.visibility =
                                        if (consentHistories!!.size > 0) View.VISIBLE else View.GONE
                                }
                            }

                            if (response.body()?.consentHistory == null) {
                                hasLoadedAllItems = true
                            }
                            adapter!!.notifyDataSetChanged()
                        }
                    }

                    override fun onFailure(call: Call<ConsentHistoryResponse?>, t: Throwable) {
                        isCallLoading = false
                        binding.llProgressBar.visibility = View.GONE
                    }
                }
            consentHistoryRepository.getConsentHistory(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_USERID
                ), mOrgId, 8, startId, callback
            )
        }
    }

    private fun setupRecyclerView() {
        adapter = BBConsentHistoryAdapter(consentHistories ?: ArrayList())
        binding.rvConsentHistory.adapter = adapter
        Paginate.with(binding.rvConsentHistory, object : Paginate.Callbacks {
            override fun onLoadMore() {
                fetchConsentHistory(false)
            }

            override fun isLoading(): Boolean {
                return isCallLoading
            }

            override fun hasLoadedAllItems(): Boolean {
                return hasLoadedAllItems
            }
        }).setLoadingTriggerThreshold(1)
            .addLoadingListItem(true)
            .build()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            mOrgId =
                intent.getStringExtra(TAG_EXTRA_ORG_ID) ?: ""
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
        supportActionBar?.title = resources.getString(R.string.bb_consent_history_consent_history)
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
}