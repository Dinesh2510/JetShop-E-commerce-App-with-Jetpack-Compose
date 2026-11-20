package com.app.ecomapp.data.models.address

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserAddressData(
    @SerializedName("id")
    val id: Int? = 0,
    @SerializedName("address")
    val address: String? = "",
    @SerializedName("default_address")
    val defaultAddress: Int? = 0,
    @SerializedName("google_address")
    val googleAddress: String? = "",
    @SerializedName("city")
    val city: String? = "",
    @SerializedName("state")
    val state: String? = "",
    @SerializedName("zip_code")
    val zipCode: String? = "",
    @SerializedName("country")
    val country: String? = "",
    @SerializedName("latitude")
    val latitude: String? = "",
    @SerializedName("longitude")
    val longitude: String? = "",
    @SerializedName("type")
    val type: String? = ""
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        TODO("Not yet implemented")
    }
}