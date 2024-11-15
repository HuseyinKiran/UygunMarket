package com.huseyinkiran.trendavmapp.retrofit

import com.huseyinkiran.trendavmapp.model.Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {

    @GET("/products")
    fun getProducts() : Call<List<Product>>

    @GET("products/{id}")
    fun getProductById(@Path("id") productId: Int): Call<Product>

}

