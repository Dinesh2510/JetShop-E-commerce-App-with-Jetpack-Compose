package com.app.ecomapp.data.models.cart


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import com.app.ecomapp.data.models.home.ProductData

@Parcelize
data class CartResponse(
    @SerializedName("cart_items")
    val cartItems: List<ProductData>,
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("totalCartPrice")
    val totalCartPrice: String,
) : Parcelable