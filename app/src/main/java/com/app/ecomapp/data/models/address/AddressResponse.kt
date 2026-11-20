package com.app.ecomapp.data.models.address


import android.os.Parcel
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
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }

}