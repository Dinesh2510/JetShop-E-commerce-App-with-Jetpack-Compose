package com.app.ecomapp.presentation.screens.order

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.ecomapp.data.Resource
import com.app.ecomapp.data.models.CommonResponse
import com.app.ecomapp.data.models.coupon.ApplyCouponCodeResponse
import com.app.ecomapp.data.models.order.UserOrderResponse
import com.app.ecomapp.data.models.placeOrder.OrderRequest
import com.app.ecomapp.data.models.placeOrder.PlaceOrderDetailsResponse
import com.app.ecomapp.data.models.placeOrder.PlaceOrderResponse
import com.app.ecomapp.data.repository.cart.CartRepository
import com.app.ecomapp.data.repository.order.OrderRepository
import com.app.ecomapp.utils.UserDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: OrderRepository, private val dataStoreHelper: UserDataStore,
) : ViewModel() {
    private val _orderState: MutableStateFlow<Resource<PlaceOrderDetailsResponse?>?> =
        MutableStateFlow(null)
    val orderState: StateFlow<Resource<PlaceOrderDetailsResponse?>?> get() = _orderState

    private val _applyCoupon: MutableStateFlow<Resource<ApplyCouponCodeResponse?>?> =
        MutableStateFlow(null)
    val applyCoupon: StateFlow<Resource<ApplyCouponCodeResponse?>?> get() = _applyCoupon

    private val _removeCoupons: MutableStateFlow<Resource<ApplyCouponCodeResponse?>?> =
        MutableStateFlow(null)
    val removeCoupons: StateFlow<Resource<ApplyCouponCodeResponse?>?> get() = _removeCoupons

    private val _orderId = MutableStateFlow<String?>(null) // Holds the order_id
    val orderId: StateFlow<String?> get() = _orderId

    private val _cancelOrderState = MutableStateFlow<Resource<CommonResponse>?>(null)
    val cancelOrderState: StateFlow<Resource<CommonResponse>?> get() = _cancelOrderState

    private val _userOrdersState = MutableStateFlow<Resource<UserOrderResponse>?>(null)
    val userOrdersState: StateFlow<Resource<UserOrderResponse>?> get() = _userOrdersState


    var isLoading by mutableStateOf(false)
        private set

    init {
        viewModelScope.launch {
            _orderId.collectLatest { orderId ->
                orderId?.let {
                    getPlaceOrderDetails(it, "")
                }
            }
        }
    }

    fun setOrderId(orderId: String) {
        _orderId.value = orderId
    }

    fun placeOrder(
        orderRequest: OrderRequest,
        onResult: (Boolean, String, PlaceOrderResponse?) -> Unit
    ) {
        viewModelScope.launch {
            repository.placeOrder(orderRequest)
                .onStart { isLoading = true } // Start loader before API call
                .catch { e ->
                    isLoading = false
                    onResult(false, e.localizedMessage ?: "Something went wrong!", null)
                }
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            isLoading = false
                            val response = result.data
                            onResult(true, response.message, response)
                        }
                        is Resource.Error -> {
                            isLoading = false
                            onResult(false, result.message ?: "Something went wrong!", null)
                        }
                        Resource.Loading -> Unit // No need to manually set `isLoading` here
                    }
                }
        }
    }

    fun getPlaceOrderDetails(order_id: String, coupon_code: String) {
        viewModelScope.launch {
            repository.placeOrderDetails(order_id, coupon_code).collect { response ->
                _orderState.value = response
            }
        }
    }
    fun applyCoupon(order_id: String, coupon_code: String) {
        Log.d("OrderScreen", "applyCoupon: ViewOdel$coupon_code")
        viewModelScope.launch {
            repository.applyCoupon(order_id, coupon_code).collect { response ->
                Log.d("OrderScreen", "applyCoupon: Response")
                _applyCoupon.value = response
            }
        }
    }
    fun removeCoupon(order_id: String, coupon_code: String) {
        viewModelScope.launch {
            repository.removeCoupon(order_id, coupon_code).collect { response ->
                _removeCoupons.value = response
            }
        }
    }
    fun cancelOrder(userId: String, orderId: String) {
        viewModelScope.launch {
            repository.cancelOrder(userId, orderId).collect { response ->
                _cancelOrderState.value = response
                if (response is Resource.Success) {
                    // Call getUserOrders again to refresh the list
                    getUserOrders(userId)
                }
            }
        }
    }

    fun getUserOrders(userId: String) {
        viewModelScope.launch {
            repository.getUserOrders(userId).collect { response ->
                _userOrdersState.value = response
            }
        }
    }
}
