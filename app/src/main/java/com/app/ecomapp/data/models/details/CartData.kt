package com.app.ecomapp.data.models.details


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CartData(
    @SerializedName("added_at")
    val addedAt: String,
    @SerializedName("cart_id")
    val cartId: String,
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("user_id")
    val userId: String
) : Parcelable