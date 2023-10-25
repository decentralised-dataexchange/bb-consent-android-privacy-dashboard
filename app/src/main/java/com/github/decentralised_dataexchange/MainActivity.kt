package com.github.decentralised_dataexchange

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.decentralised_dataexchange.databinding.ActivityMainBinding
import com.github.privacyDashboard.PrivacyDashboard

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.container.btPrivacyDashboard.setOnClickListener {
            PrivacyDashboard.showPrivacyDashboard().withApiKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJTY29wZXMiOlsic2VydmljZSJdLCJPcmdhbmlzYXRpb25JZCI6IjY1MjY1Nzk2OTM4MGYzNWZhMWMzMDI0NSIsIk9yZ2FuaXNhdGlvbkFkbWluSWQiOiI2NTI2NTc5NjkzODBmMzVmYTFjMzAyNDMiLCJleHAiOjE3MDA2NTYxMzJ9.BIfpLGSQTr4IQW1OMuIelS5TnOhvPiz3umtnSKaVpeo")
                .withUserId("65366979db611cb1948aca50")
                .withOrgId("64f09f778e5f3800014a879a")
                .withBaseUrl("https://staging-consent-bb-api.igrant.io/")
                .start(this)
        }

    }
}