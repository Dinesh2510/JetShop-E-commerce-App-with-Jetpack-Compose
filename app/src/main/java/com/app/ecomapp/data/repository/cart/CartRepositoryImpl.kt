package com.app.ecomapp.data.repository.cart// data/repository/AuthRepositoryImpl.kt
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.cart.CartResponse
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.checkout.OrderDetailsResponse
import com.app.ecomapp.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


class CartRepositoryImpl @Inject constructor(private val apiService: ApiService) : CartRepository {


    override suspend fun getCartList( user_id: String,
    ): Flow<Resource<CartResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.getCartList(user_id)
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

    override suspend fun deleteCart(cartId: String): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.deleteCart(cartId)
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

    override suspend fun addToCart(
        user_id: String,
        product_id: String,
        quantity: String
    ): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.addToCart(user_id, product_id, quantity)
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

    override suspend fun removeFromCart(
        user_id: String,
        product_id: String,
        quantity: String
    ): Flow<Resource<CommonResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response =
                    apiService.removeFromCart(user_id, product_id, quantity)
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

    override suspend fun getOrderDetails(
        user_id: String,
        coupon_code: String
    ): Flow<Resource<OrderDetailsResponse>> {
        return flow {
            try {
                emit(Resource.Loading)
                val response = apiService.getOrderDetails(user_id, coupon_code)
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

}
