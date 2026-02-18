package com.jewelpromo.app.data.model

data class RegisterRequest(
    val name: String,
    val mobile: String,
    val dob: String,
)

data class RegisterResponse(
    val userId: Int,
    val age: Int,
    val chances: List<Int>,
)

data class UpdateDiscountRequest(
    val userId: Int,
    val finalDiscount: Int,
)

data class GenericApiResponse(
    val message: String? = null,
    val error: String? = null,
)
