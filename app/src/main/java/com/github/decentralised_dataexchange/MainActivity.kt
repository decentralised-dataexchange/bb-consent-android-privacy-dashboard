package com.github.decentralised_dataexchange

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.decentralised_dataexchange.databinding.ActivityMainBinding
import com.github.privacyDashboard.DataSharingUI
import com.github.privacyDashboard.PrivacyDashboard

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    Log.d("milna", data.getStringExtra("data_agreement_record") ?: "")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.container.btPrivacyDashboard.setOnClickListener {
//            val intent = DataSharingUI.showDataSharingUI()
//                .withApiKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJTY29wZXMiOlsic2VydmljZSJdLCJPcmdhbmlzYXRpb25JZCI6IjY1MjY1Nzk2OTM4MGYzNWZhMWMzMDI0NSIsIk9yZ2FuaXNhdGlvbkFkbWluSWQiOiI2NTI2NTc5NjkzODBmMzVmYTFjMzAyNDMiLCJleHAiOjE3MDI0NDUyMjR9.iMbsEe-nN_EHzWg0KOeUGTUR1F3xYH8lQBP90Mpt_ZY")
//                .withUserId("65366979db611cb1948aca50")
//                .withDataAgreementId("6551c2b27adedd223d2e6213")
//                .withOtherApplication("Data4Diabetes","")
//                .withBaseUrl("https://staging-consent-bb-api.igrant.io/")
//                .secondaryButtonText("Skip")
//                .get(this)
//
//            resultLauncher.launch(intent)


            PrivacyDashboard.showPrivacyDashboard()
                .withApiKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJTY29wZXMiOlsic2VydmljZSJdLCJPcmdhbmlzYXRpb25JZCI6IjY1MjY1Nzk2OTM4MGYzNWZhMWMzMDI0NSIsIk9yZ2FuaXNhdGlvbkFkbWluSWQiOiI2NTI2NTc5NjkzODBmMzVmYTFjMzAyNDMiLCJleHAiOjE3MDI0NDUyMjR9.iMbsEe-nN_EHzWg0KOeUGTUR1F3xYH8lQBP90Mpt_ZY")
                .withUserId("65366979db611cb1948aca50")
                .withBaseUrl("https://staging-consent-bb-api.igrant.io/v2")
                .start(this)
        }

    }
}