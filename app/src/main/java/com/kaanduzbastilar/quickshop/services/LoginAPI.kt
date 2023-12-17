package com.kaanduzbastilar.quickshop.services

import com.kaanduzbastilar.quickshop.model.LoginRequest
import com.kaanduzbastilar.quickshop.model.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginAPI {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
}