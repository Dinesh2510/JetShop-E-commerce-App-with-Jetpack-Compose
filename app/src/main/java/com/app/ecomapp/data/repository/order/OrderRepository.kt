package com.app.ecomapp.data.repository.order

import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.checkout.OrderDetailsResponse
import com.app.ecomapp.data.models.coupon.ApplyCouponCodeResponse
import com.app.ecomapp.data.models.order.UserOrderResponse
import com.app.ecomapp.data.models.placeOrder.OrderRequest
import com.app.ecomapp.data.models.placeOrder.PlaceOrderDetailsResponse
import com.app.ecomapp.data.models.placeOrder.PlaceOrderResponse
import kotlinx.coroutines.flow.Flow


interface OrderRepository {
    suspend fun placeOrder(orderRequest: OrderRequest):  Flow<Resource<PlaceOrderResponse>>
    suspend fun placeOrderDetails(order_id: String, coupon_code: String,):  Flow<Resource<PlaceOrderDetailsResponse>>
    suspend fun removeCoupon(order_id: String, coupon_code: String,):  Flow<Resource<ApplyCouponCodeResponse>>
    suspend fun applyCoupon(order_id: String, coupon_code: String,):  Flow<Resource<ApplyCouponCodeResponse>>
    suspend fun cancelOrder(userId: String, orderId: String): Flow<Resource<CommonResponse>>
    suspend fun getUserOrders(userId: String): Flow<Resource<UserOrderResponse>>
}
