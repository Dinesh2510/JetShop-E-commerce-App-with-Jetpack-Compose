package com.app.ecomapp.data.models


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class CommonResponse(
    @SerializedName("flag")
    val flag: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("payment_status")
    val paymentStatus: String,
) : Parcelable