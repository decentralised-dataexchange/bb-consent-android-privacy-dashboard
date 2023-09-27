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
            PrivacyDashboard.showPrivacyDashboard().withApiKey("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyaWQiOiI2NGYwYTYxZThlNWYzODAwMDE0YTg3YTYiLCJvcmdpZCI6IiIsImVudiI6IiIsImV4cCI6MTcyNDU5Njk2MX0.M3I6hJWtOyqbZXQwEGCK43AvROaoR_zncItmULpbFYE")
                .withUserId("64f0a61e8e5f3800014a87a6")
                .withOrgId("64f09f778e5f3800014a879a")
                .withBaseUrl("https://demo-consent-bb-api.igrant.io/")
                .enableUserRequest(true).start(this)
        }

    }
}