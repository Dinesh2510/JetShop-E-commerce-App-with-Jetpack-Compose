package com.app.ecomapp.data.models.coupon


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CouponData(
    @SerializedName("coupon_code")
    val couponCode: String,
    @SerializedName("coupon_id")
    val couponId: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("discount_type")
    val discountType: String,
    @SerializedName("discount_value")
    val discountValue: String,
    @SerializedName("expiry_date")
    val expiryDate: String,
    @SerializedName("is_active")
    val isActive: String,
    @SerializedName("is_expired")
    val isExpired: Int,
    @SerializedName("title")
    val title: String
) : Parcelable