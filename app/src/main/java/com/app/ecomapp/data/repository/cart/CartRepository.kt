package com.app.ecomapp.data.repository.cart
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.cart.CartResponse
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.checkout.OrderDetailsResponse
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun getCartList(user_id: String): Flow<Resource<CartResponse>>
    suspend fun deleteCart(cartId: String): Flow<Resource<CommonResponse>>
    suspend fun addToCart(user_id: String, product_id: String,quantity: String): Flow<Resource<CommonResponse>>
    suspend fun removeFromCart(user_id: String, product_id: String,quantity: String): Flow<Resource<CommonResponse>>
    suspend fun getOrderDetails(user_id: String, coupon_code: String): Flow<Resource<OrderDetailsResponse>>

}