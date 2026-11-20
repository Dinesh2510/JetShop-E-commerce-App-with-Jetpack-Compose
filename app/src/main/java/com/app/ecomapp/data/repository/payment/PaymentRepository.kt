package com.app.ecomapp.data.repository.payment

import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.placeOrder.PurchasePrimeRequest
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    suspend fun initiatePayment(
        orderId: String,
        userId: String,
        paymentMethod: String,
        amount: Double,
        currency: String
    ): Flow<Resource<CommonResponse>>

    suspend fun verifyPayment(
        user_id: String,
        orderId: String,
        transactionId: String,
        status: String,
        gatewayResponse: String,
        planName: String,
        planPrice: String
    ): Flow<Resource<CommonResponse>>
    suspend fun purchasePrimeMembership(request: PurchasePrimeRequest): Flow<Resource<CommonResponse>>

}