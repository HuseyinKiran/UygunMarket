package com.huseyinkiran.trendavmapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.huseyinkiran.trendavmapp.model.Product
import com.huseyinkiran.trendavmapp.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepository {

    private val _productList = MutableLiveData<List<Product>>()
    private val _selectedProduct = MutableLiveData<Product>()
    val products: LiveData<List<Product>> get() = _productList
    val selectedProduct: LiveData<Product> get() = _selectedProduct

    fun fetchProducts() {
        RetrofitInstance.api.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                response.body()?.let { products ->
                    _productList.postValue(products)
                } ?: Log.d("ProductRepository", "Product list is empty or null.")
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.d("ProductRepository", "API call failed: ${t.message}")
            }
        })
    }

    fun fetchProductDetails(productId: Int) {
        RetrofitInstance.api.getProductById(productId).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                response.body()?.let { product ->
                    _selectedProduct.postValue(product)
                } ?: Log.e("ProductRepository", "Error fetching product details.")
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Log.e("ProductRepository", "API call failed: ${t.message}")
            }
        })
    }
}

