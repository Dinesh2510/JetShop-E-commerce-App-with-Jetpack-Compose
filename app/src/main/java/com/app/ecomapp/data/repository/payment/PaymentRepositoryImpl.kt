package com.app.ecomapp.data.repository.payment

import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.placeOrder.PurchasePrimeRequest
import com.app.ecomapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(private val apiService: ApiService) : PaymentRepository {
    override suspend fun initiatePayment(
        orderId: String,
        userId: String,
        paymentMethod: String,
        amount: Double,  // Convert to actual amount format (not paise)
        currency: String
    ): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val formattedAmount = String.format("%.2f", amount)  // Convert to 2 decimal places
                val response = apiService.initiatePayment(orderId, userId, paymentMethod, formattedAmount, currency)
                if (response.status == "error") {
                    emit(Resource.Error(response.message ?: "Payment initiation failed"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    override suspend fun verifyPayment(
        user_id: String,
        orderId: String,
        transactionId: String,
        status: String,
        gatewayResponse: String,
        planName: String,
        planPrice: String
    ): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.verifyPayment(user_id ,orderId, transactionId, status, gatewayResponse,planName, planPrice)
                if (response.status == "error") {
                    emit(Resource.Error(response.message ?: "Payment verification failed"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }
    override suspend fun purchasePrimeMembership(request: PurchasePrimeRequest): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.purchasePrimeMembership(request)
                if (response.status == "error") {
                    emit(Resource.Error(response.message ?: "Membership purchase failed"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

}

