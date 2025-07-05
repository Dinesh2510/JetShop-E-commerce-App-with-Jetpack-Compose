package com.app.ecomapp.data.models.refer


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Referral(
    @SerializedName("bonus_earned")
    val bonusEarned: Int,
    @SerializedName("email")
    val email: String,
    @SerializedName("joined_at")
    val joinedAt: String,
    @SerializedName("name")
    val name: String
) : Parcelable