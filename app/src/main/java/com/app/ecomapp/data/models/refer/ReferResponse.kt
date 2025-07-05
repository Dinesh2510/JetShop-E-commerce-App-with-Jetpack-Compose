package com.app.ecomapp.data.models.refer


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ReferResponse(
    @SerializedName("referrals")
    val referrals: List<Referral>,
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("total_referrals")
    val totalReferrals: Int
) : Parcelable