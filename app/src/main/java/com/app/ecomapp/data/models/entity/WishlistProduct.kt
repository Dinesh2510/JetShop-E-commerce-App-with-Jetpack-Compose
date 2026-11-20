package com.app.ecomapp.data.models.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist")
data class WishlistProduct(
    @PrimaryKey val product_id: String,
    val product_name: String,
    val product_description: String?,
    val product_price: Double,
    val product_discount_price: Double?,
    val product_stock_quantity: Int,
    val product_category_id: Int,
    val product_brand_id: Int?,
    val product_weight: String?,
    val product_dimensions: String?,
    val product_color: String?,
    val product_size: String?,
    val product_rating: Float?,
    val product_total_reviews: Int?,
    val product_image_url: String?,
    val product_thumbnail_url: String?,
    val product_is_featured: Boolean,
    val product_is_active: Boolean,
    val product_created_at: String?,
    val product_updated_at: String?
)
