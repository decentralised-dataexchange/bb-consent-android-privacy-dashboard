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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

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

            /*data sharing UI sample

            val intent = DataSharingUI.showDataSharingUI()
                .withApiKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJTY29wZXMiOlsic2VydmljZSJdLCJPcmdhbmlzYXRpb25JZCI6IjY1MjY1Nzk2OTM4MGYzNWZhMWMzMDI0NSIsIk9yZ2FuaXNhdGlvbkFkbWluSWQiOiI2NTI2NTc5NjkzODBmMzVmYTFjMzAyNDMiLCJleHAiOjE3MDI0NDUyMjR9.iMbsEe-nN_EHzWg0KOeUGTUR1F3xYH8lQBP90Mpt_ZY")
                .withUserId("65366979db611cb1948aca50")
                .withDataAgreementId("655260b7e7bea8d94286a193")
                .withThirdPartyApplication("Data4Diabetes","")
                .withBaseUrl("https://staging-consent-bb-api.igrant.io/v2")
                .secondaryButtonText("Skip")
                .get(this)

            resultLauncher.launch(intent)

             data sharing UI sample*/


            PrivacyDashboard.showPrivacyDashboard()
                .withApiKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJTY29wZXMiOlsic2VydmljZSJdLCJPcmdhbmlzYXRpb25JZCI6IjY1MjY1Nzk2OTM4MGYzNWZhMWMzMDI0NSIsIk9yZ2FuaXNhdGlvbkFkbWluSWQiOiI2NTI2NTc5NjkzODBmMzVmYTFjMzAyNDMiLCJleHAiOjE3MDI0NDUyMjR9.iMbsEe-nN_EHzWg0KOeUGTUR1F3xYH8lQBP90Mpt_ZY")
                .withUserId("65366979db611cb1948aca50")
                .withBaseUrl("https://staging-consent-bb-api.igrant.io/v2")
                .start(this)

            /*Optin to data agreement sample
            GlobalScope.launch {
                PrivacyDashboard.optInToDataAgreement(
                    dataAgreementId = "65522d05b792e39cff5cab2c",
                    baseUrl = "https://staging-consent-bb-api.igrant.io/v2/",
                    apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJTY29wZXMiOlsic2VydmljZSJdLCJPcmdhbmlzYXRpb25JZCI6IjY1MjY1Nzk2OTM4MGYzNWZhMWMzMDI0NSIsIk9yZ2FuaXNhdGlvbkFkbWluSWQiOiI2NTI2NTc5NjkzODBmMzVmYTFjMzAyNDMiLCJleHAiOjE3MDA5ODE5NzV9.9t8IzamNi10kwhKh8xYJJfW1G4RlZz9Pekn1Q_QrrXc",
                    userId = "65366979db611cb1948aca50"
                )
            }
             Optin to data agreement sample*/
        }

    }
}