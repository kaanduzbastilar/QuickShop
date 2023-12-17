package com.kaanduzbastilar.quickshop.services

import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
/*
class RetrofitService {

    private val BASE_URL = "https://api.storerestapi.com/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    val apiService: ProductsAPI = retrofit.create(ProductsAPI::class.java)

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}
*/

object RetrofitService {

    private const val BASE_URL = "https://api.storerestapi.com/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()

    val apiService: ProductsAPI = retrofit.create(ProductsAPI::class.java)

    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}


