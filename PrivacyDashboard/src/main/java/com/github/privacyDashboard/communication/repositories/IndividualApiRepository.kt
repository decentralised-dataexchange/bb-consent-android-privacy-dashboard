package com.github.privacyDashboard.communication.repositories

import com.github.privacyDashboard.communication.BBConsentAPIServices
import com.github.privacyDashboard.models.interfaces.userRequests.UserRequestGenResponse
import com.github.privacyDashboard.models.v2.individual.Individual
import com.github.privacyDashboard.models.v2.individual.IndividualRequest

class IndividualApiRepository(private val apiService: BBConsentAPIServices) {

    suspend fun createAnIndividual(
        name: String?,
        email: String?,
        phone: String?
    ): Result<IndividualRequest?>? {
        return try {
            val individual = IndividualRequest(
                individual = Individual(
                    name = name,
                    email = email,
                    phone = phone
                )
            )
            val response = apiService.createAnIndividual(
                individual
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun readTheIndividual(
        individualId: String?
    ): Result<IndividualRequest?>? {
        return try {
            val response = apiService.readAnIndividual(
                individualId = individualId
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateIndividual(
        individualId: String?,
        name: String,
        email: String,
        phone: String
    ): Result<IndividualRequest?>? {
        return try {
            val individual = IndividualRequest(
                individual = Individual(
                    name = name,
                    email = email,
                    phone = phone
                )
            )
            val response = apiService.updateAnIndividual(
                individualId = individualId,
                body = individual
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllIndividuals(
        offset:Int?,
        limit:Int?
    ): Result<IndividualRequest?>? {
        return try {
            val response = apiService.getAllIndividual(
                offset = offset,
                limit = limit
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}