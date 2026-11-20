package com.app.ecomapp.data.models.address

data class AddressRequest(
    val userId: String,
    val address: String,
    val city: String,
    val state: String,
    val zip_code: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val type: String,
    val google_address: String,
    val default_address: String
)
