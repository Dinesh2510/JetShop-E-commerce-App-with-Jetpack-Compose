package com.app.ecomapp.data.models.order


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PaymentDetails(
    @SerializedName("amount")
    val amount: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("payment_id")
    val paymentId: Int,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("transaction_id")
    val transactionId: String,
    @SerializedName("transaction_status")
    val transactionStatus: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("user_id")
    val userId: Int
) : Parcelable