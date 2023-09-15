package com.github.privacyDashboard.modules.attributeDetail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.privacyDashboard.R
import com.github.privacyDashboard.communication.BBConsentAPIManager
import com.github.privacyDashboard.databinding.BbconsentActivityDataAttributeDetailBinding
import com.github.privacyDashboard.events.RefreshList
import com.github.privacyDashboard.models.attributes.DataAttribute
import com.github.privacyDashboard.models.attributes.Status
import com.github.privacyDashboard.models.consent.ConsentStatusRequest
import com.github.privacyDashboard.models.consent.ResultResponse
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.utils.BBConsentDataUtils
import com.github.privacyDashboard.utils.BBConsentNetWorkUtil
import org.greenrobot.eventbus.EventBus
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


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
    }

    private var mPurposeId: String? = ""
    private var mConsentId: String? = ""
    private var mOrgId: String? = ""
    private var mDataAttribute: DataAttribute? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.bbconsent_activity_data_attribute_detail)
        getIntentData()
        setUpToolBar()
        initValues()
        initListeners()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            mOrgId =
                intent.getStringExtra(EXTRA_TAG_ORGID)
            mConsentId =
                intent.getStringExtra(EXTRA_TAG_CONSENTID)
            mPurposeId =
                intent.getStringExtra(EXTRA_TAG_PURPOSEID)
            mDataAttribute = intent.getSerializableExtra(EXTRA_TAG_CONSENT) as DataAttribute
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
        supportActionBar?.title = mDataAttribute?.description
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
        setChecked()
        binding.tvDays.text = resources.getString(
            R.string.bb_consent_data_attribute_detail_days_with_count,
            mDataAttribute?.status?.remaining ?: 0
        )
        binding.sbDays.progress = mDataAttribute?.status?.remaining ?: 0
    }

    private fun setChecked() {
        try {
            if (mDataAttribute?.status?.consented.equals("Allow", ignoreCase = true)
            ) {
                binding.ivAllow.visibility = View.VISIBLE
                binding.ivDisallow.visibility = View.GONE
                binding.ctvStatusMessage.text =
                    resources.getString(R.string.bb_consent_data_attribute_detail_allow_consent_rule)
            } else if (mDataAttribute?.status?.consented.equals("Disallow", ignoreCase = true)
            ) {
                binding.ivDisallow.visibility = View.VISIBLE
                binding.ivAllow.visibility = View.GONE
                binding.ctvStatusMessage.text =
                    resources.getString(R.string.bb_consent_data_attribute_detail_disallow_consent_rule)
            } else {
                binding.ivDisallow.visibility = View.GONE
                binding.ivAllow.visibility = View.GONE
                binding.ctvStatusMessage.text =
                    resources.getString(R.string.bb_consent_data_attribute_detail_askme_consent_rule)
            }
        } catch (e: Exception) {
            binding.ivDisallow.visibility = View.GONE
            binding.ivAllow.visibility = View.GONE
        }
    }

    private fun initListeners() {
        binding.llAllow.setOnClickListener {
            val body = ConsentStatusRequest()
            body.consented = "Allow"
            updateConsentStatus(body)
        }

        binding.llDisallow.setOnClickListener {
            val body = ConsentStatusRequest()
            body.consented = "Disallow"
            updateConsentStatus(body)
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
                binding.ctvStatusMessage.text =
                    resources.getString(R.string.bb_consent_data_attribute_detail_askme_consent_rule)
                body.days = seekBar.progress
                body.consented = "Askme"
                updateConsentStatus(body)
            }
        })
    }

    private fun updateConsentStatus(body: ConsentStatusRequest) {
        if (BBConsentNetWorkUtil.isConnectedToInternet(this)) {
            binding.llProgressBar.visibility = View.VISIBLE
            val callback: Callback<ResultResponse?> = object : Callback<ResultResponse?> {
                override fun onResponse(
                    call: Call<ResultResponse?>,
                    response: Response<ResultResponse?>
                ) {
                    binding.llProgressBar.visibility = View.GONE
                    if (response.code() == 200) {
                        val status: Status? = mDataAttribute?.status
                        status?.consented = (body.consented)
                        status?.remaining = (body.days)
                        mDataAttribute?.status = status
                        try {
                            setChecked()
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                        EventBus.getDefault().post(RefreshList())
                    }
                }

                override fun onFailure(call: Call<ResultResponse?>, t: Throwable) {
                    binding.llProgressBar.visibility = View.GONE
                }
            }

            //todo user id
            BBConsentAPIManager.getApi(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_TOKEN
                )?:"",
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service?.setAttributeStatus(
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_ORG_ID
                ),
                BBConsentDataUtils.getStringValue(
                    this,
                    BBConsentDataUtils.EXTRA_TAG_USERID
                ),
                mConsentId,
                mPurposeId,
                mDataAttribute?.iD,
                body
            )?.enqueue(callback)
        }
    }

}