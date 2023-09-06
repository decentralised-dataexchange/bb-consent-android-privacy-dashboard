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
import com.github.privacyDashboard.models.attributes.DataAttribute
import com.github.privacyDashboard.models.attributes.DataAttributesResponse
import com.github.privacyDashboard.modules.BBConsentBaseActivity

class BBConsentDataAttributeListingActivity : BBConsentBaseActivity() {
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
    }

    private fun setUpDataAttributeList() {
        val adapter = BBConsentDataAttributesAdapter(
            dataAttributesResponse?.consents?.consents ?: ArrayList(),
            object : DataAttributeClickListener {
                override fun onAttributeClick(dataAttribute: DataAttribute) {
                    if (dataAttributesResponse?.consents?.purpose?.lawfulUsage == false
                    ) {

                    }
                }
            })
        binding.rvDataAttributes.layoutManager = LinearLayoutManager(this)
        binding.rvDataAttributes.adapter = adapter
    }

    private fun setUpDescription() {
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
            "sfkhbdf sdjasf afasd fadfdafas adsfadfadf dafadfad adfadfadfad dafdafadf dafadfafad dafadfadf adfadfdafd dsf df d fad fadfadf adf dafadfda adfadfda adfadfadf adsfafd dsf ad adfadfad"
        )
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
        supportActionBar?.title = mTitle
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