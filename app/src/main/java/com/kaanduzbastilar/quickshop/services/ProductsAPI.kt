package com.kaanduzbastilar.quickshop.services

import com.kaanduzbastilar.quickshop.model.ProductCategory
import com.kaanduzbastilar.quickshop.model.ProductModel
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductsAPI {
    @GET("products")
    suspend fun getAllProducts(): ApiResponse<List<ProductModel>>
    @GET("categories")
    suspend fun getAllCategories() : ApiResponse<List<ProductCategory>>
    @GET("products/{productSlug}")
    suspend fun getProductBySlug(@Path("productSlug") productSlug: String): ApiResponse<ProductModel>

}