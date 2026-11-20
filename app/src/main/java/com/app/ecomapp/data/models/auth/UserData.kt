package com.app.ecomapp.data.models.auth

import android.os.Parcel
import android.os.Parcelable
import com.app.ecomapp.data.models.address.UserAddressData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserData(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("profile_image")
    val profileImage: String,
    @SerializedName("isPrimeActive")
    val isPrimeActive: Int = 0,
    @SerializedName("referral_code")
    val referralCode: String? = null,
    @SerializedName("referred_by")
    val referredBy: String? = null,
    @SerializedName("wallet_balance")
    val walletBalance: Double = 0.0,
    @SerializedName("prime_membership")
    val primeMembership: PrimeMembership? = null,
    @SerializedName("default_address")
    val defaultAddress: UserAddressData? = null
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}