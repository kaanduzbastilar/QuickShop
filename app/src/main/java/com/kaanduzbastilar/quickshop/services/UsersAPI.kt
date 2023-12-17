package com.kaanduzbastilar.quickshop.services

import com.kaanduzbastilar.quickshop.model.User
import retrofit2.http.GET

interface UsersAPI {
    @GET("users")
    suspend fun getUser(): ApiResponse<List<User>>
}