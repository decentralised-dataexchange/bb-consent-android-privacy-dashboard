package com.github.privacyDashboard.communication

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class BBConsentAPIManager private constructor() {
    val service: BBConsentAPIServices?
        get() = Companion.service

    private class HttpInterceptor(var token: String) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request: Request = chain.request()

            //Build new request
            val builder: Request.Builder = request.newBuilder()
            builder.header("Accept", "application/json")
            if (!token.equals("", ignoreCase = true)) {
                setAuthHeader(builder, token)
            }
            request = builder.build()
            var response: Response =
                chain.proceed(request)
            return response
        }

        private fun setAuthHeader(builder: Request.Builder, token: String?) {
            if (token != null) //Add Auth token to each request if authorized
                builder.header("Authorization", "ApiKey $token")
        }
    }

    companion object {
        private var okClient: OkHttpClient? = null
        private var service: BBConsentAPIServices? = null
        private var httpClient: OkHttpClient.Builder? = null
        private var apiManager: BBConsentAPIManager? = null
        fun getApi(token: String, baseUrl: String?): BBConsentAPIManager? {
            if (apiManager == null) {
                apiManager = BBConsentAPIManager()
                httpClient = OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                httpClient?.addInterceptor(httpLoggingInterceptor)
                httpClient?.interceptors()?.add(HttpInterceptor(token))
                okClient = httpClient?.build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                service = retrofit.create(BBConsentAPIServices::class.java)
            }
            return apiManager
        }

        fun resetApi() {
            apiManager = null
        }
    }
}
