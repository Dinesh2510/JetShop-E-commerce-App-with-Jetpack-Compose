package com.app.ecomapp.data.models.placeOrder

data class PurchasePrimeRequest(
    val user_id: Int,
    val plan_name: String,
    val plan_price: Int,
    val amount_paid: Int,
    val payment_method: String,
    val transaction_id: String,
    val transaction_status: String
)
