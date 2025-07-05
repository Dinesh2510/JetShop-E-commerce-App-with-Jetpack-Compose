package com.app.ecomapp.data.models.order


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Item(
    @SerializedName("item_status")
    val itemStatus: String,
    @SerializedName("order_details_id")
    val orderDetailsId: Int,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("price")
    val price: String,
    @SerializedName("product_description")
    val productDescription: String,
    @SerializedName("product_discount_price")
    val productDiscountPrice: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("product_image_url")
    val productImageUrl: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("product_price")
    val productPrice: String,
    @SerializedName("product_stock_quantity")
    val productStockQuantity: Int,
    @SerializedName("quantity")
    val quantity: String,
    @SerializedName("tax")
    val tax: String,
    @SerializedName("total_price")
    val totalPrice: String
) : Parcelable