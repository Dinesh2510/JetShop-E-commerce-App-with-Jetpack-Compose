package com.app.ecomapp.data.models.placeOrder


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ShippingAddress(
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("default_address")
    val defaultAddress: Int,
    @SerializedName("google_address")
    val googleAddress: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("zip_code")
    val zipCode: String
) : Parcelable