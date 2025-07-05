package com.app.ecomapp.data.models.checkout


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class OrderDetailsResponse(
    @SerializedName("cart_items")
    val cartItems: List<CartItem>,
    @SerializedName("coupon_applied")
    val couponApplied: Int,
    @SerializedName("Delivery Charges")
    val deliveryCharges: String,
    @SerializedName("Discount")
    val discount: String,
    @SerializedName("Final Total")
    val finalTotal: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("shippingAddress")
    val shippingAddress: ShippingAddress?,
    @SerializedName("status")
    val status: String,
    @SerializedName("Subtotal")
    val subtotal: String,
    @SerializedName("Tax")
    val tax: String
) : Parcelable