package com.app.ecomapp.data.models.coupon


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CouponsResponse(
    @SerializedName("data")
    val `data`: List<CouponData>,
    @SerializedName("success")
    val status: String
) : Parcelable