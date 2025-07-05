package com.app.ecomapp.data.models.placeOrder


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class OrderDetails(
    @SerializedName("coupon_code")
    val couponCode: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("delivery_charges")
    val deliveryCharges: String,
    @SerializedName("discount_value")
    val discountValue: String,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("payment_status")
    val paymentStatus: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("subtotal")
    val subtotal: String,
    @SerializedName("tax_amount")
    val taxAmount: String,
    @SerializedName("total_amount")
    val totalAmount: String,
    @SerializedName("user_id")
    val userId: Int
) : Parcelable