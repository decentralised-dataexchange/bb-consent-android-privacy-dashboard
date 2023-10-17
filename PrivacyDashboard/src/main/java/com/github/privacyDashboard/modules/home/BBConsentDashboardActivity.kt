package com.github.privacyDashboard.modules.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.devs.readmoreoption.ReadMoreOption
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.databinding.BbconsentActivityDashboardBinding
import com.github.privacyDashboard.events.RefreshHome
import com.github.privacyDashboard.models.PurposeConsent
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.models.consent.UpdateConsentStatusResponseV1
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.modules.logging.BBConsentLoggingActivity
import com.github.privacyDashboard.modules.logging.BBConsentLoggingActivity.Companion.TAG_EXTRA_ORG_ID
import com.github.privacyDashboard.modules.userRequest.BBConsentUserRequestActivity
import com.github.privacyDashboard.modules.webView.BBConsentWebViewActivity
import com.github.privacyDashboard.modules.webView.BBConsentWebViewActivity.Companion.TAG_EXTRA_WEB_TITLE
import com.github.privacyDashboard.modules.webView.BBConsentWebViewActivity.Companion.TAG_EXTRA_WEB_URL
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentImageUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BBConsentDashboardActivity : BBConsentBaseActivity() {

    private lateinit var binding: BbconsentActivityDashboardBinding

    private var adapter: UsagePurposeAdapter? = null

    private var viewModel: BBConsentDashboardViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.bbconsent_activity_dashboard)
        viewModel = ViewModelProvider(this)[BBConsentDashboardViewModel::class.java]
        binding.viewModel = viewModel;
        binding.lifecycleOwner = this;
        EventBus.getDefault().register(this)
        setUpToolBar()
        initView()
        viewModel?.getOrganizationDetail(true, this)
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolBarCommon)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_back_bg
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_more, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        } else if (item.itemId == R.id.menu_more) {
            val view = findViewById<View>(R.id.menu_more)
            showPopupMenu(view)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val isUserRequestAvailable = BBConsentDataUtils.getBooleanValue(
            this,
            BBConsentDataUtils.EXTRA_TAG_ENABLE_USER_REQUEST
        )
        popupMenu.menuInflater.inflate(
            if (isUserRequestAvailable == true) R.menu.menu_more_items else R.menu.menu_more_items_no_user_request,
            popupMenu.getMenu()
        )

        // Handle item clicks in the popup menu
        popupMenu.setOnMenuItemClickListener { item ->
            when (item?.itemId) {
                R.id.action_webpage -> {
                    val intent = Intent(
                        this,
                        BBConsentWebViewActivity::class.java
                    )
                    intent.putExtra(
                        TAG_EXTRA_WEB_URL,
                        viewModel?.organization?.value?.policyURL ?: ""
                    )
                    intent.putExtra(
                        TAG_EXTRA_WEB_TITLE,
                        resources.getString(R.string.bb_consent_web_view_privacy_policy)
                    )
                    startActivity(intent)
                    true
                }
                R.id.action_consent_history -> {
                    val consentHistory = Intent(
                        this,
                        BBConsentLoggingActivity::class.java
                    )
                    consentHistory.putExtra(
                        TAG_EXTRA_ORG_ID,
                        viewModel?.organization?.value?.iD
                    )
                    startActivity(consentHistory)
                    true
                }
                R.id.action_request -> {
                    val userOrgRequestIntent = Intent(
                        this,
                        BBConsentUserRequestActivity::class.java
                    )
                    userOrgRequestIntent.putExtra(
                        BBConsentUserRequestActivity.EXTRA_TAG_USER_REQUEST_ORGID,
                        viewModel?.organization?.value?.iD
                    )
                    userOrgRequestIntent.putExtra(
                        BBConsentUserRequestActivity.EXTRA_TAG_USER_REQUEST_ORG_NAME,
                        viewModel?.organization?.value?.name
                    )
                    startActivity(userOrgRequestIntent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun initView() {
        viewModel?.organization?.observe(this, Observer { newData ->
            try {
                BBConsentImageUtils.setImage(
                    binding.ivLogo,
                    viewModel?.organization?.value?.logoImageURL,
                    R.drawable.bb_consent_default_logo
                )
                BBConsentImageUtils.setImage(
                    binding.ivCoverUrl,
                    viewModel?.organization?.value?.coverImageURL,
                    R.drawable.bb_consent_default_cover
                )
                if (viewModel?.organization?.value?.description != null || !viewModel?.organization?.value?.description.equals(
                        ""
                    )
                ) {
                    if ((viewModel?.organization?.value?.description?.length ?: 0) > 120) {
                        val readMoreOption: ReadMoreOption = ReadMoreOption.Builder(this)
                            .textLength(3) // OR
                            .textLengthType(ReadMoreOption.TYPE_LINE) //.textLength(300, ReadMoreOption.TYPE_CHARACTER)
                            .moreLabel(resources.getString(R.string.bb_consent_dashboard_read_more))
                            .lessLabel(resources.getString(R.string.bb_consent_dashboard_read_less))
                            .moreLabelColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.bb_consent_read_more_color
                                )
                            )
                            .lessLabelColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.bb_consent_read_more_color
                                )
                            )
                            .labelUnderLine(false)
                            .expandAnimation(true)
                            .build()

                        readMoreOption.addReadMoreTo(
                            binding.tvDescription,
                            viewModel?.organization?.value?.description ?: ""
                        )
                    } else {
                        binding.tvDescription.text =
                            viewModel?.organization?.value?.description ?: ""
                    }
                } else {
                    binding.tvDescription.visibility = View.GONE
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        })

        viewModel?.purposeConsents?.observe(this, Observer { newData ->
            try {
                if (newData != null && newData.isNotEmpty()) {
                    adapter = UsagePurposeAdapter(
                        viewModel?.purposeConsents?.value ?: ArrayList(),
                        object : UsagePurposeClickListener {
                            override fun onItemClick(consent: PurposeConsent?) {
                                viewModel?.getConsentList(consent, this@BBConsentDashboardActivity)
                            }

                            override fun onSetStatus(
                                consent: PurposeConsent?,
                                isChecked: Boolean?
                            ) {
                                viewModel?.setOverallStatus(consent, isChecked,this@BBConsentDashboardActivity)
                            }
                        })
                    binding.rvDataAgreements.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshHome?) {
        viewModel?.getOrganizationDetail(false, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}