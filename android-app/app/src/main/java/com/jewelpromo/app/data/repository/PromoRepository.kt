package com.jewelpromo.app.data.repository

import com.jewelpromo.app.data.api.PromoApiService
import com.jewelpromo.app.data.model.RegisterRequest
import com.jewelpromo.app.data.model.RegisterResponse
import com.jewelpromo.app.data.model.UpdateDiscountRequest

class PromoRepository(private val apiService: PromoApiService) {

    suspend fun registerCustomer(name: String, mobile: String, dob: String): Result<RegisterResponse> {
        return try {
            val response = apiService.registerCustomer(
                RegisterRequest(name = name, mobile = mobile, dob = dob),
            )

            if (response.isSuccessful) {
                val payload = response.body()
                if (payload != null) {
                    Result.success(payload)
                } else {
                    Result.failure(IllegalStateException("Empty response from server"))
                }
            } else {
                Result.failure(IllegalStateException("Registration failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateDiscount(userId: Int, finalDiscount: Int): Result<Unit> {
        return try {
            val response = apiService.updateDiscount(
                UpdateDiscountRequest(userId = userId, finalDiscount = finalDiscount),
            )

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Update failed with code ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
