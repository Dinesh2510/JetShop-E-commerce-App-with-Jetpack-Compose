package com.app.ecomapp.data.models.auth


import android.os.Parcel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class RegisterResponse(
    @SerializedName("flag")
    val flag: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("user_id")
    val user_id: String
) : Parcelable {
    /*
    * Using same data class for Register, VerifyOtp, Login page  because response is almost same
    * */
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}