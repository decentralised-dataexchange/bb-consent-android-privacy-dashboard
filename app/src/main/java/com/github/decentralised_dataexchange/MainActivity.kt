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
            /* to create an individual
            GlobalScope.launch {
                PrivacyDashboard.createAnIndividual(
                    apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJTY29wZXMiOlsic2VydmljZSJdLCJPcmdhbmlzYXRpb25JZCI6IjY1MjY1Nzk2OTM4MGYzNWZhMWMzMDI0NSIsIk9yZ2FuaXNhdGlvbkFkbWluSWQiOiI2NTI2NTc5NjkzODBmMzVmYTFjMzAyNDMiLCJleHAiOjE3MDI0NDUyMjR9.iMbsEe-nN_EHzWg0KOeUGTUR1F3xYH8lQBP90Mpt_ZY",
                    baseUrl = "https://staging-consent-bb-api.igrant.io/v2/"
                )
            }
            to create an individual*/

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

            /* DAta agreement policy
            PrivacyDashboard.showDataAgreementPolicy().withDataAgreement(
                "{\"active\":true,\"compatibleWithVersionId\":\"\",\"controllerId\":\"652657969380f35fa1c30245\",\"controllerName\":\"\",\"controllerUrl\":\"\",\"dataAttributes\":[{\"category\":\"string\",\"description\":\"Backup\",\"id\":\"65534a939a6116c5c2b98d54\",\"name\":\"Backup\",\"sensitivity\":false}],\"dpiaDate\":\"2023-11-14T10:22\",\"dpiaSummaryUrl\":\"https://privacyant.se/dpia_results.html\",\"forgettable\":false,\"id\":\"65534a939a6116c5c2b98d51\",\"lawfulBasis\":\"consent\",\"lifecycle\":\"complete\",\"methodOfUse\":\"data_using_service\",\"policy\":{\"dataRetentionPeriodDays\":730,\"geographicRestriction\":\"Europe\",\"id\":\"65534a939a6116c5c2b98d52\",\"industrySector\":\"Retail\",\"jurisdiction\":\"Sweden\",\"name\":\"string\",\"storageLocation\":\"Sweden\",\"thirdPartyDataSharing\":false,\"url\":\"https://uidai.gov.in/en/privacy-policy.html\",\"version\":\"\"},\"purpose\":\"Backup and Restore (DO NOT DELETE)\",\"purposeDescription\":\"Backup and restore\",\"signature\":{\"id\":\"65534a939a6116c5c2b98d53\",\"objectReference\":\"\",\"objectType\":\"\",\"payload\":\"\",\"signature\":\"\",\"signedWithoutObjectReference\":false,\"timestamp\":\"\",\"verificationArtifact\":\"\",\"verificationJwsHeader\":\"\",\"verificationMethod\":\"\",\"verificationPayload\":\"\",\"verificationPayloadHash\":\"\",\"verificationSignedAs\":\"\",\"verificationSignedBy\":\"\"},\"version\":\"1.0.0\"}"
            )
                .start(this)

            DAta agreement policy */

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