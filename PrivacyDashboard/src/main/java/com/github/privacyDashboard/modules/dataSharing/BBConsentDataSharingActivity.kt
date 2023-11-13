package com.github.privacyDashboard.modules.dataSharing

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentActivityDataSharingBinding
import com.github.privacyDashboard.models.DataAgreementPolicyModel
import com.github.privacyDashboard.models.v2.dataAgreement.DataAgreementV2
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.modules.dataAgreementPolicy.BBConsentDataAgreementPolicyActivity
import com.github.privacyDashboard.modules.dataAttribute.BBConsentDataAttributeListingActivity
import com.github.privacyDashboard.modules.webView.BBConsentWebViewActivity
import com.github.privacyDashboard.utils.BBConsentImageUtils
import com.google.gson.Gson

class BBConsentDataSharingActivity : BBConsentBaseActivity() {

    private var adapter: BBConsentDataSharingAttributesAdapter? = null
    private lateinit var binding: BbconsentActivityDataSharingBinding
    private var viewModel: BBConsentDataSharingViewModel? = null

    companion object {
        const val TAG_EXTRA_OTHER_APPLICATION_NAME =
            "com.github.privacyDashboard.modules.dataSharing.otherApplicationName"
        const val TAG_EXTRA_OTHER_APPLICATION_LOGO =
            "com.github.privacyDashboard.modules.dataSharing.otherApplicationLogo"
        const val TAG_EXTRA_SECONDARY_BUTTON_TEXT =
            "com.github.privacyDashboard.modules.dataSharing.secondaryButtonText"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.bbconsent_activity_data_sharing)
        viewModel = ViewModelProvider(this)[BBConsentDataSharingViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        getIntentData()
        viewModel?.fetchDataAgreementRecord(true, this)
        initListeners()
        setUpToolBar()
    }

    private fun getIntentData() {
        viewModel?.otherApplicationName = intent.getStringExtra(TAG_EXTRA_OTHER_APPLICATION_NAME)?:"Application"
        viewModel?.otherApplicationLogo = intent.getStringExtra(TAG_EXTRA_OTHER_APPLICATION_LOGO)
        val secondaryButtonText = intent.getStringExtra(TAG_EXTRA_SECONDARY_BUTTON_TEXT)
        if (secondaryButtonText != null)
            binding.btnCancel.text = secondaryButtonText
        if (viewModel?.otherApplicationLogo != null) {
            BBConsentImageUtils.setImage(
                binding.iv3ppLogo,
                viewModel?.otherApplicationLogo,
                R.drawable.bb_consent_default_logo
            )
            binding.cv3ppLogo.visibility = View.VISIBLE
            binding.ivArrows.visibility = View.VISIBLE
        }

        setupTermsOfServicesView()
    }

    private fun setupTermsOfServicesView() {
        val termsOfServiceClick: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(
                    this@BBConsentDataSharingActivity,
                    BBConsentWebViewActivity::class.java
                )
                intent.putExtra(
                    BBConsentWebViewActivity.TAG_EXTRA_WEB_URL,
                    viewModel?.organization?.value?.policyURL
                )
                intent.putExtra(
                    BBConsentWebViewActivity.TAG_EXTRA_WEB_TITLE,
                    resources.getString(R.string.bb_consent_web_view_policy)
                )
                startActivity(intent)
            }
        }
        val dataAgreementDetails: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(
                    this@BBConsentDataSharingActivity,
                    BBConsentDataAgreementPolicyActivity::class.java
                )
                intent.putExtra(
                    BBConsentDataAgreementPolicyActivity.TAG_EXTRA_ATTRIBUTE_LIST,
                    buildListForDataAgreementPolicy(viewModel?.dataAgreement?.value)
                )
                startActivity(intent)
            }
        }
        setClickableString(
            resources.getString(R.string.bb_consent_data_sharing_sensitive_info_see_data_agreement_details_and_terms_of_services),
            binding.tvTermsOfServices,
            arrayOf("Data Agreement details", "Terms of Services"),
            arrayOf(dataAgreementDetails, termsOfServiceClick)
        )
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
        supportActionBar?.title = ""
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                sendResult()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initListeners() {
        viewModel?.dataAgreement?.observe(this, Observer { newData ->

            binding.tvMainDesc.text = Html.fromHtml(
                resources.getString(
                    R.string.bb_consent_data_sharing_main_desc,
                    if ((viewModel?.dataAgreement?.value?.methodOfUse
                            ?: "") == "data_using_service"
                    ) viewModel?.organization?.value?.name else viewModel?.otherApplicationName,
                    if ((viewModel?.dataAgreement?.value?.methodOfUse
                            ?: "") == "data_using_service"
                    ) viewModel?.otherApplicationName else viewModel?.organization?.value?.name,
                    viewModel?.dataAgreement?.value?.purpose ?: ""
                )
            )

            BBConsentImageUtils.setImage(
                binding.ivAppLogo,
                if ((viewModel?.dataAgreement?.value?.methodOfUse
                        ?: "") == "data_using_service"
                ) viewModel?.otherApplicationLogo else viewModel?.organization?.value?.logoImageURL ?: "",
                R.drawable.bb_consent_default_logo
            )

            BBConsentImageUtils.setImage(
                binding.iv3ppLogo,
                if ((viewModel?.dataAgreement?.value?.methodOfUse
                        ?: "") == "data_using_service"
                ) viewModel?.organization?.value?.logoImageURL ?: "" else viewModel?.otherApplicationLogo,
                R.drawable.bb_consent_default_logo,
            )

            binding.tvMakeSureTrust.text = resources.getString(
                R.string.bb_consent_data_sharing_make_sure_that_you_trust,
                if ((viewModel?.dataAgreement?.value?.methodOfUse
                        ?: "") == "data_using_service"
                ) viewModel?.organization?.value?.name else viewModel?.otherApplicationName
            )

            binding.tvDesc.text = resources.getString(
                R.string.bb_consent_data_sharing_by_authrizing_text,
                if ((viewModel?.dataAgreement?.value?.methodOfUse
                        ?: "") == "data_using_service"
                ) viewModel?.organization?.value?.name else viewModel?.otherApplicationName
            )

            adapter = BBConsentDataSharingAttributesAdapter(
                newData?.dataAttributes ?: ArrayList()
            )
            binding.rvDataAttributes.layoutManager = LinearLayoutManager(this)
            binding.rvDataAttributes.adapter = adapter
        })

        viewModel?.dataAgreementRecord?.observe(this, Observer { newData ->
            if (newData != null) {
                sendResult()
            }
        })

        binding.btnAuthorize.setOnClickListener {
            viewModel?.authorizeRequest(this)
        }

        binding.btnCancel.setOnClickListener {
            sendResult()
        }
    }

    private fun sendResult() {
        if (viewModel?.dataAgreementRecord?.value != null) {
            val resultIntent = Intent()
            resultIntent.putExtra(
                "data_agreement_record",
                Gson().toJson(viewModel?.dataAgreementRecord?.value)
            )
            setResult(RESULT_OK, resultIntent)
            finish()
        } else {
            val resultIntent = Intent()
            setResult(RESULT_CANCELED, resultIntent)
            finish()
        }
    }

    private fun setClickableString(
        wholeValue: String,
        textView: TextView,
        clickableValue: Array<String>,
        clickableSpans: Array<ClickableSpan?>
    ) {
        val spannableString = SpannableString(wholeValue)
        for (i in clickableValue.indices) {
            val clickableSpan: ClickableSpan? = clickableSpans[i]
            val link = clickableValue[i]
            val startIndexOfLink = wholeValue.indexOf(link)
            spannableString.setSpan(
                clickableSpan,
                startIndexOfLink,
                startIndexOfLink + link.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.setHighlightColor(
            Color.TRANSPARENT
        ) // prevent TextView change background when highlight
        textView.setMovementMethod(LinkMovementMethod.getInstance())
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun buildListForDataAgreementPolicy(dataAgreement: DataAgreementV2?): String {
        var list: ArrayList<ArrayList<DataAgreementPolicyModel>> = ArrayList()
        var subList: ArrayList<DataAgreementPolicyModel> = ArrayList()
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.bb_consent_data_agreement_policy_purpose),
                dataAgreement?.purpose
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.bb_consent_data_agreement_policy_purpose_description),
                dataAgreement?.purposeDescription
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.bb_consent_data_agreement_policy_lawful_basis_of_processing),
                dataAgreement?.lawfulBasis
            )
        )
        list.add(subList)
        subList = ArrayList()
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.bb_consent_data_agreement_policy_policy_url),
                dataAgreement?.policy?.url
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.bb_consent_data_agreement_policy_jurisdiction),
                dataAgreement?.policy?.jurisdiction
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.bb_consent_data_agreement_policy_third_party_disclosure),
                dataAgreement?.policy?.thirdPartyDataSharing
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.bb_consent_data_agreement_policy_industry_scope),
                dataAgreement?.policy?.industrySector
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.bb_consent_data_agreement_policy_geographic_restriction),
                dataAgreement?.policy?.geographicRestriction
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.bb_consent_data_agreement_policy_retention_period),
                dataAgreement?.policy?.dataRetentionPeriodDays.toString()
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.bb_consent_data_agreement_policy_storage_location),
                dataAgreement?.policy?.storageLocation
            )
        )
        list.add(subList)

        return Gson().toJson(list)
    }
}