package com.app.ecomapp.data.models.order


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class UserOrderResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("orders")
    val userOrders: List<UserOrder>,
    @SerializedName("success")
    val success: Boolean
) : Parcelable