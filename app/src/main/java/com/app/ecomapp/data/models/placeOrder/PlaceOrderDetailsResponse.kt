package com.app.ecomapp.data.models.placeOrder


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import android.os.Parcel

@Parcelize
data class PlaceOrderDetailsResponse(
    @SerializedName("applied_coupon")
    val appliedCoupon: String,
    @SerializedName("order_details")
    val orderDetails: OrderDetails,
    @SerializedName("order_items")
    val orderItems: List<OrderItem>,
    @SerializedName("status")
    val status: String
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}