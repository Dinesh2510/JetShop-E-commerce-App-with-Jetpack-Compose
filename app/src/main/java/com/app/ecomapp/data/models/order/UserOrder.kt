package com.app.ecomapp.data.models.order


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class UserOrder(
    @SerializedName("coupon_code")
    val couponCode: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("delivery_charges")
    val deliveryCharges: String,
    @SerializedName("discount_value")
    val discountValue: String,
    @SerializedName("items")
    val items: List<Item>,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("payment_details")
    val paymentDetails: PaymentDetails?,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("payment_status")
    val paymentStatus: String,
    @SerializedName("shipping_details")
    val shippingDetails: ShippingDetails,
    @SerializedName("status")
    val status: String,
    @SerializedName("subtotal")
    val subtotal: String,
    @SerializedName("tax_amount")
    val taxAmount: String,
    @SerializedName("total_amount")
    val totalAmount: String,
    @SerializedName("transaction_status")
    val transactionStatus: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("expected_delivery_date")
    val expected_delivery_date: String
) : Parcelable