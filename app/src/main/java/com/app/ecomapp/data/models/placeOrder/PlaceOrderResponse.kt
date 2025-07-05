package com.app.ecomapp.data.models.placeOrder


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PlaceOrderResponse(
    @SerializedName("Delivery Charges")
    val deliveryCharges: String,
    @SerializedName("Discount")
    val discount: String,
    @SerializedName("Final Total")
    val finalTotal: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("shipping_address")
    val shippingAddress: ShippingAddress,
    @SerializedName("status")
    val status: String,
    @SerializedName("Subtotal")
    val subtotal: String,
    @SerializedName("Tax")
    val tax: String
) : Parcelable