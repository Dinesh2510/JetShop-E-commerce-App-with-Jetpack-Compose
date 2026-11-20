package com.app.ecomapp.data.models.refer


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import android.os.Parcel

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
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}