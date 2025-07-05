package com.app.ecomapp.data.repository.order

import android.util.Log
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.cart.CartResponse
import com.app.ecomapp.data.models.coupon.ApplyCouponCodeResponse
import com.app.ecomapp.data.models.order.UserOrderResponse
import com.app.ecomapp.data.models.placeOrder.OrderRequest
import com.app.ecomapp.data.models.placeOrder.PlaceOrderDetailsResponse
import com.app.ecomapp.data.models.placeOrder.PlaceOrderResponse
import com.app.ecomapp.data.remote.ApiService
import com.app.ecomapp.data.repository.cart.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(private val apiService: ApiService) : OrderRepository {

    override suspend fun placeOrder(orderRequest: OrderRequest): Flow<Resource<PlaceOrderResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.placeOrder(orderRequest)
                if (response.status == "error") {
                    emit(Resource.Error(response.message ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    override suspend fun placeOrderDetails(
        order_id: String,
        coupon_code: String,
    ): Flow<Resource<PlaceOrderDetailsResponse>> {
        return flow {
            Log.d("OrderScreen", "OrderRepositoryImpl:")

            try {
                emit(Resource.Loading)
                val response =
                    apiService.getPlaceOrderDetails(order_id, coupon_code)
                if (response.status == "error") {
                    emit(Resource.Error(response.status ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    override suspend fun applyCoupon(
        order_id: String,
        coupon_code: String,
    ): Flow<Resource<ApplyCouponCodeResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.applyCoupon(order_id, coupon_code)
                if (response.status == "error") {
                    emit(Resource.Error(response.message ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }

    override suspend fun removeCoupon(
        order_id: String,
        coupon_code: String,
    ): Flow<Resource<ApplyCouponCodeResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.removeCoupon(order_id, coupon_code)
                if (response.status == "error") {
                    emit(Resource.Error(response.message ?: "Error occurred"))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
    }
    override suspend fun cancelOrder(userId: String, orderId: String): Flow<Resource<CommonResponse>> =
        flow {
            emit(Resource.Loading)
            try {
                val response = apiService.cancelOrder(userId, orderId)
                if (response.status == "error") {
                    emit(Resource.Error(response.message))
                } else {
                    emit(Resource.Success(response))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }

    override suspend fun getUserOrders(userId: String): Flow<Resource<UserOrderResponse>> =
        flow {
            emit(Resource.Loading)
            try {
                val response = apiService.getUserOrders(userId,"")
                if (response.success) {
                    emit(Resource.Success(response))
                } else {
                    emit(Resource.Error(response.message))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.localizedMessage ?: "Network error"))
            }
        }
}

