package com.app.ecomapp.data.models.auth


import android.os.Parcel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PrimeMembership(
    @SerializedName("amount_paid")
    val amountPaid: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("currency")
    val currency: String,
    @SerializedName("expiry_date")
    val expiryDate: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("payment_gateway_response")
    val paymentGatewayResponse: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("plan_name")
    val planName: String,
    @SerializedName("plan_price")
    val planPrice: String,
    @SerializedName("start_date")
    val startDate: String,
    @SerializedName("transaction_id")
    val transactionId: String,
    @SerializedName("transaction_status")
    val transactionStatus: String,
    @SerializedName("user_id")
    val userId: Int
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}