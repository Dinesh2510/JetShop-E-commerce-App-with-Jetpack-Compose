package com.app.ecomapp.data.models.order

import android.os.Parcelable
import android.os.Parcel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserOrderResponse(
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("orders")
    val userOrders: List<UserOrder>? = emptyList(),
    @SerializedName("success")
    val success: Boolean = false
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}