package com.app.ecomapp.data.models.address

data class AddressUpdateRequest(
    val addressId: String,
    val userId: String,
    val address: String,
    val city: String,
    val state: String,
    val zip_code: String,
    val country: String,
    val latitude: String,
    val longitude: String,
    val type: String,
    val google_address: String

)
