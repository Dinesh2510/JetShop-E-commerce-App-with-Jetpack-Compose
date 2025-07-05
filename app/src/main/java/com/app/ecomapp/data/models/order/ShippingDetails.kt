package com.app.ecomapp.data.models.order


import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class ShippingDetails(
    @SerializedName("address")
    val address: String,
    @SerializedName("city")
    val city: String,
    @SerializedName("country")
    val country: String,
    @SerializedName("google_address")
    val googleAddress: String,
    @SerializedName("latitude")
    val latitude: String,
    @SerializedName("longitude")
    val longitude: String,
    @SerializedName("order_id")
    val orderId: String,
    @SerializedName("order_shipping_id")
    val orderShippingId: Int,
    @SerializedName("phone")
    val phone: String,
    @SerializedName("postal_code")
    val postalCode: String,
    @SerializedName("shipping_status")
    val shippingStatus: String,
    @SerializedName("state")
    val state: String,
    @SerializedName("user_id")
    val userId: Int
) : Parcelable