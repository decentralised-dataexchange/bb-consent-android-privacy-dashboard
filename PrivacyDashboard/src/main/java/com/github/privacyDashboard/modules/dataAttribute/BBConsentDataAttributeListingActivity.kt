package com.github.privacyDashboard.modules.dataAttribute

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.devs.readmoreoption.ReadMoreOption
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.databinding.BbconsentActivityDataAttributesBinding
import com.github.privacyDashboard.models.PurposeConsent
import com.github.privacyDashboard.models.attributes.DataAttribute
import com.github.privacyDashboard.models.attributes.DataAttributesResponse
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.models.consent.UpdateConsentStatusResponse
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.modules.webView.BBConsentWebViewActivity
import com.github.privacyDashboard.modules.webView.BBConsentWebViewActivity.Companion.TAG_EXTRA_WEB_TITLE
import com.github.privacyDashboard.modules.webView.BBConsentWebViewActivity.Companion.TAG_EXTRA_WEB_URL
import com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity
import com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.Companion.EXTRA_TAG_CONSENT
import com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.Companion.EXTRA_TAG_CONSENTID
import com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.Companion.EXTRA_TAG_ORGID
import com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.Companion.EXTRA_TAG_PURPOSEID
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import com.github.privacyDashboard.utils.BBConsentStringUtils.toCamelCase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BBConsentDataAttributeListingActivity : BBConsentBaseActivity() {
    private var adapter: BBConsentDataAttributesAdapter? = null
    private lateinit var binding: BbconsentActivityDataAttributesBinding

    companion object {
        const val TAG_EXTRA_NAME =
            "com.github.privacyDashboard.modules.dataAttribute.BBConsentDataAttributeListingActivity.name"
        const val TAG_EXTRA_DESCRIPTION =
            "com.github.privacyDashboard.modules.dataAttribute.BBConsentDataAttributeListingActivity.description"
        const val TAG_DATA_ATTRIBUTES =
            "com.github.privacyDashboard.modules.dataAttribute.BBConsentDataAttributeListingActivity.dataAttributes"
    }

    private var dataAttributesResponse: DataAttributesResponse? = null
    private var mTitle = ""
    private var mDescription = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.bbconsent_activity_data_attributes)
        getIntentData()
        setUpToolBar()
        setUpDescription()
        setUpDataAttributeList()
        initListener()
        updateDisallowAllButtonVisibility(true)
    }

    private fun setUpDataAttributeList() {
        adapter = BBConsentDataAttributesAdapter(
            dataAttributesResponse?.consents?.consents ?: ArrayList(),
            object : DataAttributeClickListener {
                override fun onAttributeClick(dataAttribute: DataAttribute) {
                    if (dataAttributesResponse?.consents?.purpose?.lawfulUsage == false
                    ) {
                        val intent: Intent = Intent(
                            this@BBConsentDataAttributeListingActivity,
                            BBConsentDataAttributeDetailActivity::class.java
                        )
                        intent.putExtra(
                            EXTRA_TAG_ORGID,
                            dataAttributesResponse?.orgID
                        )
                        intent.putExtra(
                            EXTRA_TAG_CONSENTID,
                            dataAttributesResponse?.consentID
                        )
                        intent.putExtra(
                            EXTRA_TAG_PURPOSEID,
                            dataAttributesResponse?.iD
                        )
                        intent.putExtra(
                            EXTRA_TAG_CONSENT,
                            dataAttribute
                        )
                        startActivity(intent)
                    }
                }
            })
        binding.rvDataAttributes.layoutManager = LinearLayoutManager(this)
        binding.rvDataAttributes.adapter = adapter
    }

    private fun setUpDescription() {
        if (mDescription.length > 120) {
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
                mDescription
            )
        } else {
            binding.tvDescription.text = mDescription
        }
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            mTitle =
                intent.getStringExtra(TAG_EXTRA_NAME) ?: ""
            mDescription =
                intent.getStringExtra(TAG_EXTRA_DESCRIPTION) ?: ""
            dataAttributesResponse =
                intent.getSerializableExtra(TAG_DATA_ATTRIBUTES) as DataAttributesResponse
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
        supportActionBar?.title = toCamelCase(mTitle)
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

    private fun initListener() {
        binding.btnPrivacyPolicy.setOnClickListener {
            val intent = Intent(
                this,
                BBConsentWebViewActivity::class.java
            )
            intent.putExtra(
                TAG_EXTRA_WEB_URL,
                dataAttributesResponse?.consents?.purpose?.policyURL
            )
            intent.putExtra(
                TAG_EXTRA_WEB_TITLE,
                resources.getString(R.string.bb_consent_web_view_policy)
            )
            startActivity(intent)
        }

        binding.tvDisallowAll.setOnClickListener {
            setOverallStatus(false)
        }
    }

    private fun setOverallStatus(isChecked: Boolean?) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this)) {
            binding.llProgressBar.visibility = View.VISIBLE
            val body = ConsentStatusRequest()
            body.consented = if (isChecked == true) "Allow" else "DisAllow"
            val callback: Callback<UpdateConsentStatusResponse?> =
                object : Callback<UpdateConsentStatusResponse?> {
                    override fun onResponse(
                        call: Call<UpdateConsentStatusResponse?>,
                        response: Response<UpdateConsentStatusResponse?>
                    ) {
                        binding.llProgressBar.visibility = View.GONE
                        if (response.code() == 200) {
                            for (item in dataAttributesResponse?.consents?.consents
                                ?: ArrayList()) {
                                item.status?.consented =
                                    if (isChecked == true) "Allow" else "DisAllow"
                                item.status?.days = 0
                                item.status?.remaining = 0
                            }
                        }
                        adapter?.notifyDataSetChanged()
                        updateDisallowAllButtonVisibility(false)
                    }

                    override fun onFailure(
                        call: Call<UpdateConsentStatusResponse?>,
                        t: Throwable
                    ) {
                        binding.llProgressBar.visibility = View.GONE
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
                dataAttributesResponse?.consentID,
                dataAttributesResponse?.consents?.purpose?.iD,
                body
            )?.enqueue(callback)
        }
    }

    private fun updateDisallowAllButtonVisibility(checkAndUpdate: Boolean?) {
        if (checkAndUpdate == true) {
            var visibility = false
            if (dataAttributesResponse?.consents?.purpose?.lawfulUsage != true) {
                for (item in dataAttributesResponse?.consents?.consents
                    ?: ArrayList()) {
                    if (item.status?.consented == "Allow") {
                        visibility = true
                    }
                }
            }
            binding.tvDisallowAll.visibility = if (visibility) View.VISIBLE else View.GONE
        } else {
            binding.tvDisallowAll.visibility = View.GONE
        }
    }
}