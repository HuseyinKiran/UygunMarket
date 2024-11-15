package com.huseyinkiran.trendavmapp.model

data class Product(
    val category: String? = null,
    val description: String? = null,
    val id: Int = 0,
    val image: String? = null,
    val price: Double? = null,
    val rating: Rating? = null,
    val title: String? = null,
    var quantity: Int = 1
)

