package com.app.ecomapp.data.models.coupon


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import android.os.Parcel

@Parcelize
data class ApplyCouponCodeResponse(
    @SerializedName("discount")
    val discount: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("new_total")
    val newTotal: Double,
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