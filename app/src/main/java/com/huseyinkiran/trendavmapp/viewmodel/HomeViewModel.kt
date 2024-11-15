package com.huseyinkiran.trendavmapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.huseyinkiran.trendavmapp.model.Product
import com.huseyinkiran.trendavmapp.repository.ProductRepository

class HomeViewModel : ViewModel() {

    private val repository = ProductRepository()
    val products: LiveData<List<Product>> = repository.products

    private val _selectedProduct = repository.selectedProduct
    val selectedProduct: LiveData<Product> get() = _selectedProduct

    init {
        fetchProducts()
    }

    fun fetchProducts() {
        repository.fetchProducts()
    }

    fun selectProductById(productId: Int) {
        repository.fetchProductDetails(productId)
    }
}

