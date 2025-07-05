package com.app.ecomapp.data.models.address


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class AddresseData(
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("zip_code")
    val zipCode: String,
    @SerializedName("default_address")
    val default_address: String,
    @SerializedName("google_address")
    val googleMapAddress: String,
) : Parcelable