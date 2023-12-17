package com.kaanduzbastilar.quickshop.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val data: LoginResponseData,
    val message: String,
    val status: Int,
)

data class LoginResponseData(
    val access_token: String,
    val refresh_token: String,
    val email: String
)