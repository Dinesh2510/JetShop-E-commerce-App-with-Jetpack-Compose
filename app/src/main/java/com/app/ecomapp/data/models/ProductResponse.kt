package com.app.ecomapp.data.models

import com.app.ecomapp.data.models.home.ProductData

data class ProductResponse(
    val status: String,
    val current_page: Int,
    val total_pages: Int,
    val total_products: String,
    val data: List<ProductData>?,
    val products: List<ProductData>?
)
