package com.github.privacyDashboard.modules.attributeDetail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentActivityDataAttributeDetailBinding
import com.github.privacyDashboard.models.base.attribute.DataAttribute
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentStringUtils.toCamelCase


class BBConsentDataAttributeDetailActivity : BBConsentBaseActivity() {

    private lateinit var binding: BbconsentActivityDataAttributeDetailBinding

    companion object {
        const val EXTRA_TAG_ORGID =
            "com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.orgId"
        const val EXTRA_TAG_CONSENTID =
            "com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.consentId"
        const val EXTRA_TAG_PURPOSEID =
            "com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.purposeId"
        const val EXTRA_TAG_CONSENT =
            "com.github.privacyDashboard.modules.attributeDetail.BBConsentDataAttributeDetailActivity.consent"
        var mDataAttribute: DataAttribute? = null
    }

    private var viewModel: BBConsentDataAttributeDetailViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.bbconsent_activity_data_attribute_detail)
        viewModel = ViewModelProvider(this)[BBConsentDataAttributeDetailViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this;
        getIntentData()
        setUpToolBar()
        initValues()
        initListeners()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            viewModel?.mOrgId =
                intent.getStringExtra(EXTRA_TAG_ORGID)
            viewModel?.mConsentId =
                intent.getStringExtra(EXTRA_TAG_CONSENTID)
            viewModel?.mPurposeId =
                intent.getStringExtra(EXTRA_TAG_PURPOSEID)
        }
        viewModel?.mDataAttribute = mDataAttribute
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
        supportActionBar?.title = toCamelCase(viewModel?.mDataAttribute?.mDescription)
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

    private fun initValues() {
        viewModel?.setChecked(this)
        val isAskMeEnabled = BBConsentDataUtils.getBooleanValue(
            this,
            BBConsentDataUtils.EXTRA_TAG_ENABLE_ASK_ME
        )
        if (isAskMeEnabled == true) {
            binding.llAskme.visibility = View.VISIBLE
            binding.vAskMe.visibility = View.VISIBLE
            binding.tvDays.text = resources.getString(
                R.string.bb_consent_data_attribute_detail_days_with_count,
                viewModel?.mDataAttribute?.status?.remaining ?: 0
            )
            binding.sbDays.progress = viewModel?.mDataAttribute?.status?.remaining ?: 0
        } else {
            binding.llAskme.visibility = View.GONE
            binding.vAskMe.visibility = View.GONE
        }
    }


    private fun initListeners() {
        binding.llAllow.setOnClickListener {
            val body = ConsentStatusRequest()
            body.consented = "Allow"
            viewModel?.updateConsentStatus(body, this)
        }

        binding.llDisallow.setOnClickListener {
            val body = ConsentStatusRequest()
            body.consented = "Disallow"
            viewModel?.updateConsentStatus(body, this)
        }

        binding.sbDays.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                val body = ConsentStatusRequest()
                binding.tvDays.text = resources.getString(
                    R.string.bb_consent_data_attribute_detail_days_with_count,
                    seekBar.progress
                )
                viewModel?.statusMessage?.value =
                    resources.getString(R.string.bb_consent_data_attribute_detail_askme_consent_rule)
                body.days = seekBar.progress
                body.consented = "Askme"
                viewModel?.updateConsentStatus(body, this@BBConsentDataAttributeDetailActivity)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataAttribute = null
    }
}