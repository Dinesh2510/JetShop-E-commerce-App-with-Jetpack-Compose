package com.app.ecomapp.data.models.coupon

import android.os.Parcelable
import android.os.Parcel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class CouponData(
    @SerializedName("coupon_id")
    val couponId: String? = null,

    @SerializedName("coupon_code")
    val couponCode: String = "",

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("description")
    val description: String = "",

    @SerializedName("discount_value")
    val discountValue: String? = null,

    @SerializedName("discount_type")
    val discountType: String? = null, // "fixed" or "percentage"

    @SerializedName("min_order_amount")
    val minOrderAmount: String? = null,

    @SerializedName("max_discount")
    val maxDiscount: String? = null,

    @SerializedName("usage_limit")
    val usageLimit: String? = null,

    @SerializedName("used_count")
    val usedCount: String? = null,

    @SerializedName("expiry_date")
    val expiryDate: String = "",

    @SerializedName("status")
    val status: String? = null, // "active" or "inactive"

    // Optional legacy fallback if used elsewhere
    @SerializedName("is_active")
    val isActive: String? = null,

    @SerializedName("is_expired")
    val isExpired: Int? = 0
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}