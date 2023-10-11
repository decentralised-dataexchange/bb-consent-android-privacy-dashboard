package com.github.privacyDashboard.modules.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.devs.readmoreoption.ReadMoreOption
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.communication.repositories.GetOrganizationDetailApiRepository
import com.github.privacyDashboard.databinding.BbconsentActivityDashboardBinding
import com.github.privacyDashboard.events.RefreshHome
import com.github.privacyDashboard.models.Organization
import com.github.privacyDashboard.models.OrganizationDetailResponse
import com.github.privacyDashboard.models.PurposeConsent
import com.github.privacyDashboard.models.attributes.DataAttributesResponse
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.models.consent.UpdateConsentStatusResponse
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.modules.dataAttribute.BBConsentDataAttributeListingActivity
import com.github.privacyDashboard.modules.dataAttribute.BBConsentDataAttributeListingActivity.Companion.TAG_DATA_ATTRIBUTES
import com.github.privacyDashboard.modules.dataAttribute.BBConsentDataAttributeListingActivity.Companion.TAG_EXTRA_DESCRIPTION
import com.github.privacyDashboard.modules.dataAttribute.BBConsentDataAttributeListingActivity.Companion.TAG_EXTRA_NAME
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

    var organization: Organization? = null
    private var consentId: String? = null
    private val purposeConsents = ArrayList<PurposeConsent>()
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
        getOrganizationDetail(true)
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
                        organization?.policyURL ?: ""
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
                        organization?.iD
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
                        organization?.iD
                    )
                    userOrgRequestIntent.putExtra(
                        BBConsentUserRequestActivity.EXTRA_TAG_USER_REQUEST_ORG_NAME,
                        organization?.name
                    )
                    startActivity(userOrgRequestIntent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun getOrganizationDetail(showProgress: Boolean) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this)) {
            viewModel?.isLoading?.value = showProgress
            val organizationDetailRepository = GetOrganizationDetailApiRepository(this)

            val callback: Callback<OrganizationDetailResponse?> =
                object : Callback<OrganizationDetailResponse?> {

                    override fun onResponse(
                        call: Call<OrganizationDetailResponse?>,
                        response: Response<OrganizationDetailResponse?>
                    ) {
                        viewModel?.isLoading?.value = false
                        if (response.code() == 200) {
                            try {
                                purposeConsents.clear()
                                purposeConsents.addAll(
                                    response.body()?.purposeConsents ?: ArrayList()
                                )
                                adapter = UsagePurposeAdapter(
                                    purposeConsents,
                                    object : UsagePurposeClickListener {
                                        override fun onItemClick(consent: PurposeConsent?) {
                                            getConsentList(consent)
                                        }

                                        override fun onSetStatus(
                                            consent: PurposeConsent?,
                                            isChecked: Boolean?
                                        ) {
                                            setOverallStatus(consent, isChecked)
                                        }
                                    })
                                binding.rvDataAgreements.adapter = adapter
                                consentId = response.body()?.consentID
                                organization =
                                    response.body()?.organization
                                if (showProgress)
                                    initView(response.body()?.organization)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    override fun onFailure(call: Call<OrganizationDetailResponse?>, t: Throwable) {
                        viewModel?.isLoading?.value = false
                        Toast.makeText(
                            this@BBConsentDashboardActivity,
                            resources.getString(R.string.bb_consent_error_unexpected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            organizationDetailRepository.getOrganizationDetail(BBConsentDataUtils.getStringValue(
                this,
                BBConsentDataUtils.EXTRA_TAG_ORG_ID
            ),callback)
        }
    }

    private fun initView(mOrg: Organization?) {
        try {
            BBConsentImageUtils.setImage(
                binding.ivLogo,
                mOrg?.logoImageURL,
                R.drawable.bb_consent_default_logo
            )
            BBConsentImageUtils.setImage(
                binding.ivCoverUrl,
                mOrg?.coverImageURL,
                R.drawable.bb_consent_default_cover
            )
            binding.tvName.text = mOrg?.name
            binding.tvLocation.text = mOrg?.location
            if (mOrg?.description != null || !mOrg?.description.equals("")) {
                if ((mOrg?.description?.length ?: 0) > 120) {
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
                        mOrg?.description ?: ""
                    )
                } else {
                    binding.tvDescription.text = mOrg?.description ?: ""
                }
            } else {
                binding.tvDescription.visibility = View.GONE
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun getConsentList(consent: PurposeConsent?) {
        viewModel?.isLoading?.value = true
        if (BBConsentNetWorkUtil.isConnectedToInternet(this)) {
            val callback: Callback<DataAttributesResponse?> =
                object : Callback<DataAttributesResponse?> {
                    override fun onResponse(
                        call: Call<DataAttributesResponse?>,
                        response: Response<DataAttributesResponse?>
                    ) {
                        viewModel?.isLoading?.value = false
                        if (response.code() == 200) {
                            val intent = Intent(
                                this@BBConsentDashboardActivity,
                                BBConsentDataAttributeListingActivity::class.java
                            )
                            intent.putExtra(TAG_DATA_ATTRIBUTES, response.body())
                            intent.putExtra(TAG_EXTRA_NAME, consent?.purpose?.name)
                            intent.putExtra(
                                TAG_EXTRA_DESCRIPTION,
                                consent?.purpose?.description
                            )
                            startActivity(intent)
//                            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
                        }
                    }

                    override fun onFailure(call: Call<DataAttributesResponse?>, t: Throwable) {
                        viewModel?.isLoading?.value = false
                        Toast.makeText(
                            this@BBConsentDashboardActivity,
                            resources.getString(R.string.bb_consent_error_unexpected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            try {
                BBConsentAPIManager.getApi(
                    BBConsentDataUtils.getStringValue(
                        this,
                        BBConsentDataUtils.EXTRA_TAG_TOKEN
                    ) ?: "",
                    BBConsentDataUtils.getStringValue(
                        this,
                        BBConsentDataUtils.EXTRA_TAG_BASE_URL
                    )
                )?.service?.getConsentList(
                    BBConsentDataUtils.getStringValue(
                        this,
                        BBConsentDataUtils.EXTRA_TAG_ORG_ID
                    ),
                    BBConsentDataUtils.getStringValue(
                        this,
                        BBConsentDataUtils.EXTRA_TAG_USERID
                    ),
                    consentId,
                    consent?.purpose?.iD
                )?.enqueue(callback)
            } catch (e: java.lang.Exception) {
                viewModel?.isLoading?.value = false
                e.printStackTrace()
            }
        }
    }

    private fun setOverallStatus(consent: PurposeConsent?, isChecked: Boolean?) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this)) {
            viewModel?.isLoading?.value = true
            val body = ConsentStatusRequest()
            body.consented = if (isChecked == true) "Allow" else "DisAllow"
            val callback: Callback<UpdateConsentStatusResponse?> =
                object : Callback<UpdateConsentStatusResponse?> {
                    override fun onResponse(
                        call: Call<UpdateConsentStatusResponse?>,
                        response: Response<UpdateConsentStatusResponse?>
                    ) {
                        viewModel?.isLoading?.value = false
                        if (response.code() == 200) {
                            getOrganizationDetail(false)
                        }
                    }

                    override fun onFailure(
                        call: Call<UpdateConsentStatusResponse?>,
                        t: Throwable
                    ) {
                        viewModel?.isLoading?.value = false
                    }
                }
            BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                ) ?: "",
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service?.setOverallStatus(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_ORG_ID
                ),
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_USERID
                ),
                consentId,
                consent?.purpose?.iD,
                body
            )?.enqueue(callback)
        } else {
            adapter!!.notifyDataSetChanged()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshHome?) {
        getOrganizationDetail(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}