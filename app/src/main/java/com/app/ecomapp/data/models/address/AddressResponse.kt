package com.app.ecomapp.data.models.address


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AddressResponse(
    @SerializedName("addresses")
    val addresses: List<AddresseData>,
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
) : Parcelable