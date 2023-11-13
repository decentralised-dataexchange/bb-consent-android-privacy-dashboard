package com.github.privacyDashboard.modules.dataAgreementPolicy

import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.privacyDashboard.R
import com.github.privacyDashboard.databinding.BbconsentActivityDataAgreementPolicyBinding
import com.github.privacyDashboard.models.DataAgreementPolicyList
import com.github.privacyDashboard.models.DataAgreementPolicyModel
import com.github.privacyDashboard.modules.BBConsentBaseActivity
import com.github.privacyDashboard.utils.BBConsentDisplayUtils
import com.google.gson.Gson

class BBConsentDataAgreementPolicyActivity : BBConsentBaseActivity() {
    private lateinit var binding: BbconsentActivityDataAgreementPolicyBinding

    companion object {
        const val TAG_EXTRA_ATTRIBUTE_LIST =
            "com.github.privacyDashboard.modules.dataAgreementPolicy.dataAgreementPolicyAttributes"
    }

    var adapter: BBConsentDataAgreementPolicyAdapter? = null
    var list: ArrayList<ArrayList<DataAgreementPolicyModel>> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.bbconsent_activity_data_agreement_policy)
        getIntentData()
        setUpToolBar()
        setUpList()
    }

    private fun setUpList() {
        val width = BBConsentDisplayUtils.getScreenWidth()- BBConsentDisplayUtils.convertDpToPixel(
            60f,
            binding.rvDataAgreementPolicy.context
        )
        adapter = BBConsentDataAgreementPolicyAdapter(list,width)
        binding.rvDataAgreementPolicy.adapter = adapter
    }

    private fun getIntentData() {
        if (intent.extras != null)
            list = Gson().fromJson(
                intent?.getStringExtra(TAG_EXTRA_ATTRIBUTE_LIST),
                DataAgreementPolicyList::class.java
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
        supportActionBar?.title =
            resources.getString(R.string.bb_consent_data_agreement_policy_data_agreement)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}