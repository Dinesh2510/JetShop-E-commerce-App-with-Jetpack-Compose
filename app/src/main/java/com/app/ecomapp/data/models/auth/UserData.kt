package com.app.ecomapp.data.models.auth


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class UserData(
    @SerializedName("email")
    val email: String = "",

    @SerializedName("first_name")
    val firstName: String = "",

    @SerializedName("isPrimeActive")
    val isPrimeActive: Int = 0,

    @SerializedName("last_name")
    val lastName: String = "",

    @SerializedName("name")
    val name: String = "",

    @SerializedName("phone")
    val phone: String = "",

    @SerializedName("prime_membership")
    val primeMembership: PrimeMembership? = null, // Nullable and optional by default

    @SerializedName("user_id")
    val userId: String = "",

    @SerializedName("profile_image")
    val profileImage: String = "",

    @SerializedName("referral_code")
    val referralCode: String? = null,

    @SerializedName("referred_by")
    val referredBy: String? = null,

    @SerializedName("wallet_balance")
    val walletBalance: Double = 0.0

) : Parcelable
