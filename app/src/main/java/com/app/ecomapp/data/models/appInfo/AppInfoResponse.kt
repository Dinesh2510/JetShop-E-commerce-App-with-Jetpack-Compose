package com.app.ecomapp.data.models.appInfo


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AppInfoResponse(
    @SerializedName("data")
    val appInfoData: AppInfoData,
    @SerializedName("status")
    val status: Boolean,
    @SerializedName("message")
    val message: String
) : Parcelable