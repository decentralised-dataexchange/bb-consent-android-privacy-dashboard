package com.github.privacyDashboard.modules.dataAttribute

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.devs.readmoreoption.ReadMoreOption
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentActivityDataAttributesBinding
import com.github.privacyDashboard.events.RefreshList
import com.github.privacyDashboard.models.base.attribute.DataAttribute
import com.github.privacyDashboard.models.base.attribute.DataAttributesResponse
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.modules.webView.BBConsentWebViewActivity
import com.github.privacyDashboard.modules.webView.BBConsentWebViewActivity.Companion.TAG_EXTRA_WEB_TITLE
import com.github.privacyDashboard.modules.webView.BBConsentWebViewActivity.Companion.TAG_EXTRA_WEB_URL
import com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity
import com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.Companion.EXTRA_TAG_CONSENT
import com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.Companion.EXTRA_TAG_CONSENTID
import com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.Companion.EXTRA_TAG_ORGID
import com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.Companion.EXTRA_TAG_PURPOSEID
import com.github.privacyDashboard.utils.BBConsentStringUtils.toCamelCase
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

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
        var dataAttributesResponse: DataAttributesResponse? = null
    }

    private var mTitle = ""
    private var mDescription = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.bbconsent_activity_data_attributes)
        EventBus.getDefault().register(this)
        getIntentData()
        setUpToolBar()
        setUpDescription()
        setUpDataAttributeList()
        initListener()
    }

    private fun setUpDataAttributeList() {
        adapter = BBConsentDataAttributesAdapter(
            dataAttributesResponse?.consents?.consents ?: ArrayList(),
            object : DataAttributeClickListener {
                override fun onAttributeClick(dataAttribute: DataAttribute?) {
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
                            Gson().toJson(dataAttribute)
                        )
                        startActivity(intent)
                        BBConsentDataAttributeDetailActivity.mDataAttribute=dataAttribute
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
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshList?) {
        for (item in dataAttributesResponse?.consents?.consents ?: ArrayList()) {
            if (item?.mId == event?.purposeId) {
                item?.status = event?.status
            }
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        dataAttributesResponse = null
    }
}