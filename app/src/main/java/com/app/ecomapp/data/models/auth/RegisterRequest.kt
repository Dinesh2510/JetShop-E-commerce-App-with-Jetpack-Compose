package com.app.ecomapp.data.models.auth

// RegisterRequest.kt
data class RegisterRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone: String,
    val password: String,
    val referred_by: String?
)