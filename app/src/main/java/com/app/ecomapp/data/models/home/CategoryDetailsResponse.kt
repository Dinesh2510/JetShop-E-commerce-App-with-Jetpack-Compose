package com.app.ecomapp.data.models.home
data class CategoryDetailsResponse(
    val success: Boolean,
    val error: Boolean,
    val products: List<ProductData>?
)