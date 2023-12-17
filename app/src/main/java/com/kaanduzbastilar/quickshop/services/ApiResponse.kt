package com.kaanduzbastilar.quickshop.services

data class ApiResponse<T>(
    val data: T?,
    val status: Int,
    val message: String
)