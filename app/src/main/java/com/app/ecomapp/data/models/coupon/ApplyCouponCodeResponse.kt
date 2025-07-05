package com.app.ecomapp.data.models.coupon


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

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
) : Parcelable