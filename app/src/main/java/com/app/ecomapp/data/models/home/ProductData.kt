package com.app.ecomapp.data.models.home


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ProductData(
    @SerializedName("product_brand_id")
    val productBrandId: Int,
    @SerializedName("product_category_id")
    val productCategoryId: Int,
    @SerializedName("product_color")
    val productColor: String,
    @SerializedName("product_created_at")
    val productCreatedAt: String,
    @SerializedName("product_description")
    val productDescription: String,
    @SerializedName("product_dimensions")
    val productDimensions: String,
    @SerializedName("product_discount_price")
    val productDiscountPrice: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("product_image_url")
    val productImageUrl: String,
    @SerializedName("product_is_active")
    val productIsActive: Int,
    @SerializedName("product_is_featured")
    val productIsFeatured: Int,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("product_price")
    val productPrice: String,
    @SerializedName("product_rating")
    val productRating: String,
    @SerializedName("product_size")
    val productSize: String,
    @SerializedName("product_stock_quantity")
    val productStockQuantity: Int,
    @SerializedName("product_thumbnail_url")
    val productThumbnailUrl: String,
    @SerializedName("product_total_reviews")
    val productTotalReviews: Int,
    @SerializedName("product_updated_at")
    val productUpdatedAt: String,
    @SerializedName("product_weight")
    val productWeight: String,
    @SerializedName("user_cart_quantity")
    val user_cart_quantity: String?,
    @SerializedName("cart_id")
    val cart_id: String,
    @SerializedName("quantity")
    val quantity: String,
) : Parcelable