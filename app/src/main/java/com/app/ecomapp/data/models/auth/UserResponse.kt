package com.app.ecomapp.data.models.auth


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class UserResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("user")
    val userData: UserData
) : Parcelable