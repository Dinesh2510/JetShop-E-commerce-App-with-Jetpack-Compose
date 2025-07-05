package com.app.ecomapp.data.models.placeOrder


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class OrderItem(
    @SerializedName("price")
    val price: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("quantity")
    val quantity: String,
    @SerializedName("tax")
    val tax: String,
    @SerializedName("total_price")
    val totalPrice: String
) : Parcelable