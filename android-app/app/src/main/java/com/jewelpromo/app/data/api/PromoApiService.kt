package com.jewelpromo.app.data.api

import com.jewelpromo.app.data.model.GenericApiResponse
import com.jewelpromo.app.data.model.RegisterRequest
import com.jewelpromo.app.data.model.RegisterResponse
import com.jewelpromo.app.data.model.UpdateDiscountRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface PromoApiService {
    @POST("api/register")
    suspend fun registerCustomer(
        @Body request: RegisterRequest,
    ): Response<RegisterResponse>

    @POST("api/update-discount")
    suspend fun updateDiscount(
        @Body request: UpdateDiscountRequest,
    ): Response<GenericApiResponse>
}
