package com.app.ecomapp.utils

sealed class PaymentState {
    object Idle : PaymentState()
    object Loading : PaymentState()
    data class Success(val paymentId: String) : PaymentState()
    data class Failure(val error: String, val code :Int) : PaymentState()
}